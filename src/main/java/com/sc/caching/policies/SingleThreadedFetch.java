package com.sc.caching.policies;

import com.sc.caching.NoArgFunction;

/**
 * This policy is not thread safe, hence should only be used if a single threaded caching model is used
 * @param <V>
 */
public class SingleThreadedFetch<V> implements DataFetchPolicy<V> {
    @Override
    public V execute(NoArgFunction<V> function) {
        return function.apply();
    }
}
