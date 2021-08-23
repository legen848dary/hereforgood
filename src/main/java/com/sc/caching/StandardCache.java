package com.sc.caching;

import com.sc.policies.SyncPolicy;
import com.sc.policies.NonBlockingSync;
import com.sc.policies.StoragePolicy;
import org.agrona.collections.Object2ObjectHashMap;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private final SyncPolicy<V> syncPolicy;

    public StandardCache(Function<K, V> mappingFunction) {
        this(mappingFunction, StoragePolicy.DEFAULT);
    }

    public StandardCache(Function<K, V> mappingFunction, StoragePolicy policy) {
        this(mappingFunction, policy == StoragePolicy.GC_OPTMIZED ? Object2ObjectHashMap::new: HashMap::new);
    }

    /**
     * To keep it simple, we are not exposing this flexibility at this moment
     * @param mappingFunction
     * @param customInternalMapProvider
     */
    private StandardCache(Function<K, V> mappingFunction, Supplier<Map<K, V>> customInternalMapProvider) {
        Objects.requireNonNull(mappingFunction, ERROR_NON_NULL_MAP_FUNCTION);
        this.internalCollection = customInternalMapProvider.get();
        this.mappingFunction = mappingFunction;
        this.nullKeys = new HashSet<>();
        syncPolicy = new NonBlockingSync<>(100, TimeUnit.MILLISECONDS);
    }

    @Override
    public V get(K key) {
        Objects.requireNonNull(key, ERROR_NON_NULL_KEY);
        V cached = internalCollection.get(key);
        if (cached != null || nullKeys.contains(key)) return cached;
        return syncPolicy.execute(() -> {
            V result = internalCollection.get(key);
            if (result == null && !nullKeys.contains(key)) {
                result = mappingFunction.apply(key);
                if (result != null) {
                    internalCollection.put(key, result);
                } else {
                    nullKeys.add(key);
                }
            }
            return result;
        });
    }

    @Override
    public void clearNullKeys() {
        this.nullKeys.clear();
    }
}
