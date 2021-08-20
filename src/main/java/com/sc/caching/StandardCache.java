package com.sc.caching;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.sc.caching.Constants.*;


/**
 *
 */
public class StandardCache<K, V> implements Cache<K, V> {

    private final Map<K, V> internalCollection;
    private final Function<K, V> mappingFunction;
    private final Set<K> nullKeys;

    public StandardCache(Function<K, V> mappingFunction) {
        this(mappingFunction, HashMap::new);
    }

    public StandardCache(Function<K, V> mappingFunction, Supplier<Map<K, V>> internalCollectionSupplier) {
        Objects.requireNonNull(mappingFunction, ERROR_NON_NULL_MAP_FUNCTION);
        Objects.requireNonNull(internalCollectionSupplier, ERROR_NON_NULL_INTERNAL_COLLECTION_SUPPLIER);
        this.internalCollection = internalCollectionSupplier.get();
        Objects.requireNonNull(this.internalCollection, ERROR_NON_NULL_INTERNAL_COLLECTION);
        this.mappingFunction = mappingFunction;
        this.nullKeys = new HashSet<>();
    }

    @Override
    public V get(K key) {
        Objects.requireNonNull(key, ERROR_NON_NULL_KEY);
        V cached = internalCollection.get(key);
        if (cached != null || nullKeys.contains(key)) return cached;
        synchronized (this) {
            cached = internalCollection.get(key);
            if (cached == null && !nullKeys.contains(key)) {
                cached = mappingFunction.apply(key);
                if (cached != null) {
                    internalCollection.put(key, cached);
                } else {
                    nullKeys.add(key);
                }
            }
        }
        return cached;
    }

    @Override
    public void clear() {
        this.internalCollection.clear();
        this.nullKeys.clear();
    }

    @Override
    public void clearNullKeys() {
        this.nullKeys.clear();
    }
}
