package com.sc.scheduler;

import com.sc.policies.NonBlockingSync;
import com.sc.policies.SyncPolicy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class CharteredSchedulerEngine implements DeadlineEngine {
    private final NavigableMap<Long, List<Long>> deadlineToRequestIds;
    private final Map<Long, Long> requestIdToDeadline;
    private final AtomicLong requestIds = new AtomicLong(0);
    private final int EXECUTION_OK = 0;
    private final int EXECUTION_ERROR = -1;
    private final SyncPolicy<Integer> syncPolicy = new NonBlockingSync<>(100, TimeUnit.MILLISECONDS);

    public CharteredSchedulerEngine() {
        this.deadlineToRequestIds = new ConcurrentSkipListMap<>();
        this.requestIdToDeadline = new ConcurrentHashMap<>();
    }

    private long getNextUniqueId() {
        return requestIds.incrementAndGet();
    }

    @Override
    public long schedule(long deadlineMs) {
        long requestId = getNextUniqueId();
        syncPolicy.execute (()-> {
            this.requestIdToDeadline.put(requestId, deadlineMs);
            List<Long> requestIds = this.deadlineToRequestIds.computeIfAbsent(deadlineMs, x -> new ArrayList<>());
            requestIds.add(requestId);
        });
        return requestId;
    }

    @Override
    public boolean cancel(long requestId) {
        return syncPolicy.execute(() -> {
            Long deadLine = requestIdToDeadline.remove(requestId);
            if (deadLine != null) {
                List<Long> requestIds = deadlineToRequestIds.get(deadLine);
                if (requestIds != null) {
                    requestIds.remove(requestId);
                    if (requestIds.isEmpty()) {
                        deadlineToRequestIds.remove(deadLine);
                    }
                }
                return EXECUTION_OK;
            }
            return EXECUTION_ERROR;
        }) == EXECUTION_OK;
    }

    @Override
    public int poll(long nowMs, Consumer<Long> handler, int maxPoll) {
        NavigableSet<Long> keySet = deadlineToRequestIds.navigableKeySet();
        int count = 0;
        List<Long> expiredList = new ArrayList<>();
        for (Long deadline: keySet) {
            if (deadline > nowMs || count > maxPoll - 1) {
                count = Math.min(count, maxPoll);
                break;
            }
            List<Long> requestIds = deadlineToRequestIds.get(deadline);
            for (Long requestId : requestIds) {
                if (count > maxPoll - 1) {
                    count = maxPoll;
                    break;
                }
                expiredList.add(requestId);
                count++;
            }
        }
        if (!expiredList.isEmpty()) {
            triggerExpiredSchedules(expiredList, handler);
        }
        return count;
    }

    private void triggerExpiredSchedules(List<Long> requestIds, Consumer<Long> handler) {
        for (Long requestId : requestIds) {
            if (cancel(requestId)) handler.accept(requestId);
        }
    }

    @Override
    public int size() {
        int size = syncPolicy.execute(() -> {
            int size1 = this.requestIdToDeadline.size();
            long verifySize = deadlineToRequestIds.values().stream().mapToLong(Collection::size).sum();
            if (size1 == verifySize) return size1;
            System.out.println("mismatch, deadlineToRequestIds.values.size " + verifySize + ", requestIdToDeadline.size " + size1);
            return EXECUTION_ERROR;
        });
        if (size > EXECUTION_ERROR) return size;
        throw new IllegalStateException("mismatch");
    }
}
