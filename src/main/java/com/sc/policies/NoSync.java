package com.sc.policies;

import com.sc.caching.NoArgFunction;

/**
 * This policy is not thread safe, hence should only be used if a single threaded caching model is used
 * @param <V>
 */
public class NoSync<V> implements SyncPolicy<V> {
    @Override
    public V execute(NoArgFunction<V> function) {
        return function.apply();
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
