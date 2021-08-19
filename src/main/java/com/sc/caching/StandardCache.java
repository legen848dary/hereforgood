package com.sc.caching;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.sc.caching.Constants.ERROR_NON_NULL_KEY;
import static com.sc.caching.Constants.ERROR_NON_NULL_MAP_FUNCTION;


/**
 *
 */
public class StandardCache<K, V> implements Cache<K, V> {

    private final Map<K, V> internalCollection;
    private final Function<K, V> mappingFunction;

    public StandardCache(Function<K, V> mappingFunction) {
        ensureNonNull(mappingFunction);
        this.internalCollection = new HashMap<>();
        this.mappingFunction = mappingFunction;
    }

    private void ensureNonNull(Function<K, V> mappingFunction) {
        Objects.requireNonNull(mappingFunction, ERROR_NON_NULL_MAP_FUNCTION);
    }

    @Override
    public V get(K key) {
        Objects.requireNonNull(key, ERROR_NON_NULL_KEY);
        V cached = internalCollection.get(key);
        if (cached != null) return cached;
        synchronized (this) {
            cached = internalCollection.get(key);
            if (cached == null) {
                cached = mappingFunction.apply(key);
                internalCollection.put(key, cached);
            }
        }
        return cached;
    }
}
