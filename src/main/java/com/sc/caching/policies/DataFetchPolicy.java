package com.sc.caching.policies;

import com.sc.caching.NoArgFunction;

public interface DataFetchPolicy<V> {
    V execute(NoArgFunction<V> function);
}
