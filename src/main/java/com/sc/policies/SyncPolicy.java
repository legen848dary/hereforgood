package com.sc.policies;

import com.sc.caching.NoArgFunction;

public interface SyncPolicy<V> {
    V execute(NoArgFunction<V> function);
    void execute(Runnable runnable);
}
