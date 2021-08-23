package com.sc.scheduler;

import com.sc.policies.BlockingSync;
import com.sc.policies.NonBlockingSync;
import com.sc.policies.SyncPolicy;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class CharteredSchedulerEngine implements DeadlineEngine {
    private final NavigableMap<Long, List<Long>> deadlineToRequestIds;
    private final Map<Long, Long> requestIdToDeadline;
    private final AtomicLong requestIds = new AtomicLong(0);
    private final SyncPolicy<Void> syncPolicy = new NonBlockingSync<>(100, TimeUnit.MILLISECONDS);

    public CharteredSchedulerEngine() {
        this.deadlineToRequestIds = new ConcurrentSkipListMap<>();
        this.requestIdToDeadline = new HashMap<>();
    }

    private long getNextUniqueId() {
        return requestIds.incrementAndGet();
    }

    @Override
    public long schedule(long deadlineMs) {
        long requestId = getNextUniqueId();
        this.requestIdToDeadline.put(requestId, deadlineMs);
        syncPolicy.execute (()-> {
            List<Long> requestIds = this.deadlineToRequestIds.computeIfAbsent(deadlineMs, x -> new ArrayList<>());
            requestIds.add(requestId);
        });
        return requestId;
    }

    @Override
    public boolean cancel(long requestId) {
        Long deadLine = requestIdToDeadline.remove(requestId);
        if (deadLine != null) {
            syncPolicy.execute(() -> {
                List<Long> requestIds = deadlineToRequestIds.get(deadLine);
                if (requestIds != null) {
                    requestIds.remove(requestId);
                    if (requestIds.isEmpty()) {
                        deadlineToRequestIds.remove(deadLine);
                    }
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public int poll(long nowMs, Consumer<Long> handler, int maxPoll) {
        NavigableMap<Long, List<Long>> eligible = deadlineToRequestIds.headMap(nowMs, true);
        int count = 0;
        List<Long> toRemove = new ArrayList<>();
        for (Long deadline : eligible.keySet()) {
            if (deadline > nowMs || count > maxPoll - 1) {
                triggerExpiries(toRemove, handler);
                return Math.min(count, maxPoll);
            }
            List<Long> requestIds = eligible.get(deadline);
            for (Long requestId : requestIds) {
                if (count > maxPoll - 1) {
                    triggerExpiries(toRemove, handler);
                    return maxPoll;
                }
                toRemove.add(requestId);
                count++;
            }
        }
        triggerExpiries(toRemove, handler);
        return count;
    }

    private void triggerExpiries(List<Long> requestIds, Consumer<Long> handler) {
        for (Long requestId : requestIds) {
            cancel(requestId);
            handler.accept(requestId);
        }
    }

    @Override
    public int size() {
        if (this.requestIdToDeadline.size() == deadlineToRequestIds.values().stream().mapToLong(Collection::size).sum())
            return this.requestIdToDeadline.size();
        else
            throw new IllegalStateException("mismatch, deadlineToRequestIds.values.size " + deadlineToRequestIds.values().size() + ", requestIdToDeadline.size " + requestIdToDeadline.size());
    }
}
