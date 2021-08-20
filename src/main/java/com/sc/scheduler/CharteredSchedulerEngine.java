package com.sc.scheduler;

import com.sc.caching.policies.StoragePolicy;
import org.agrona.collections.Long2LongHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CharteredSchedulerEngine implements DeadlineEngine {
    private final static long THE_MISSING_VALUE = 0;
    private final Map<Long, Long> registry;
    private final AtomicLong requestIds = new AtomicLong(0);

    public CharteredSchedulerEngine(StoragePolicy storagePolicy) {
        this(storagePolicy == StoragePolicy.GC_OPTMIZED ? () -> new Long2LongHashMap(THE_MISSING_VALUE) : HashMap::new);
    }

    /**
     * To keep it simple, we are not exposing this flexibility at this moment
     *
     * @param supplier
     */
    private CharteredSchedulerEngine(Supplier<Map<Long, Long>> supplier) {
        this.registry = supplier.get();
    }

    @Override
    public long schedule(long deadlineMs) {
        long requestId = requestIds.incrementAndGet();
        this.registry.put(requestId, deadlineMs);
        return requestId;
    }

    @Override
    public boolean cancel(long requestId) {
        return this.registry.remove(requestId) > THE_MISSING_VALUE;
    }

    @Override
    public int poll(long nowMs, Consumer<Long> handler, int maxPoll) {
        // the interesting part
        return 0;
    }

    @Override
    public int size() {
        return this.registry.size();
    }
}
