package com.sc.caching;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StandardCacheTest {

    @Test
    public void test_null_mapping_supplier_not_allowed() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new StandardCache<>(null));
        assertEquals(Constants.ERROR_NON_NULL_MAP_FUNCTION, exception.getMessage());
    }

    @Test
    public void test_null_collection_supplier_not_allowed() {
        Function mapping = mock(Function.class);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new StandardCache<>(mapping, null));
        assertEquals(Constants.ERROR_NON_NULL_INTERNAL_COLLECTION_SUPPLIER, exception.getMessage());
    }

    @Test
    public void test_null_collection_supply_not_allowed() {
        Function mapping = mock(Function.class);
        Supplier<Map<String, String>> collectionSupplier = mock(Supplier.class);
        when(collectionSupplier.get()).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new StandardCache<String, String>(mapping, collectionSupplier));
        assertEquals(Constants.ERROR_NON_NULL_INTERNAL_COLLECTION, exception.getMessage());
    }

}