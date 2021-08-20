package com.sc.caching.policies;

import com.sc.caching.NoArgFunction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NonBlockingFetch<V> implements DataFetchPolicy<V> {
    private final Lock lock = new ReentrantLock();

    private final long time;
    private final TimeUnit timeUnit;

    public NonBlockingFetch(long time, TimeUnit timeUnit) {
        this.time = time;
        this.timeUnit = timeUnit;
    }

    @Override
    public V execute(NoArgFunction<V> function) {
        boolean isAcquired = false;
        try {
            isAcquired = lock.tryLock(time, timeUnit);
        } catch (InterruptedException ignored) {
        }
        if (isAcquired) {
            try {
                return function.apply();
            } finally {
                lock.unlock();
            }
        }
        return null;
    }
}
