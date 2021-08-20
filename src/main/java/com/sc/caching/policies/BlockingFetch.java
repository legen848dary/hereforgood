package com.sc.caching.policies;

import com.sc.caching.NoArgFunction;

public class BlockingFetch<V> implements DataFetchPolicy<V> {

    @Override
    public V execute(NoArgFunction<V> function) {
        synchronized (this) {
            return function.apply();
        }
    }
}
