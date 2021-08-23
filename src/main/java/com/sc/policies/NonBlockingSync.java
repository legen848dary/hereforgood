package com.sc.policies;

import com.sc.caching.NoArgFunction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NonBlockingSync<V> implements SyncPolicy<V> {
    private final Lock lock = new ReentrantLock();

    private final long time;
    private final TimeUnit timeUnit;

    public NonBlockingSync(long time, TimeUnit timeUnit) {
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

    @Override
    public void execute(Runnable runnable) {
        boolean isAcquired = false;
        try {
            isAcquired = lock.tryLock(time, timeUnit);
        } catch (InterruptedException ignored) {
        }
        if (isAcquired) {
            try {
                runnable.run();
            } finally {
                lock.unlock();
            }
        }
    }
}
