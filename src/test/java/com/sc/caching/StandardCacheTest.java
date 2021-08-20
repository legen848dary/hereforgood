package com.sc.caching;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StandardCacheTest {

    @Test
    public void test_null_mapping_supplier_not_allowed() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new StandardCache<>(null));
        assertEquals(Constants.ERROR_NON_NULL_MAP_FUNCTION, exception.getMessage());
    }

    @Test
    public void test_only_called_once_for_a_key() {
        Function<String, String> mapping = mock(Function.class);
        when(mapping.apply("DJ")).thenReturn("Debjyoti");
        Cache<String, String> cache = new StandardCache<>(mapping);
        assertEquals("Debjyoti", cache.get("DJ"));
        verify(mapping, times(1)).apply(eq("DJ"));
        assertEquals("Debjyoti", cache.get("DJ"));
        verify(mapping, times(1)).apply(eq("DJ"));
    }

    @Test
    public void test_only_called_once_even_for_null_result() {
        Function<String, String> mapping = mock(Function.class);
        when(mapping.apply("DJ")).thenReturn(null);
        Cache<String, String> cache = new StandardCache<>(mapping);
        assertNull(cache.get("DJ"));
        verify(mapping, times(1)).apply(eq("DJ"));
        assertNull(cache.get("DJ"));
        verify(mapping, times(1)).apply(eq("DJ"));
    }

}