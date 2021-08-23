package com.sc.policies;

import com.sc.caching.NoArgFunction;

public class BlockingSync<V> implements SyncPolicy<V> {

    @Override
    public V execute(NoArgFunction<V> function) {
        synchronized (this) {
            return function.apply();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (this) {
            runnable.run();
        }
    }
}
