package com.sc.caching;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class StandardCacheTest {

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @Test
    public void test_null_supplier_not_allowed() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new StandardCache<>(null));
        assertEquals(Constants.ERROR_NON_NULL_MAP_FUNCTION, exception.getMessage());
    }

}