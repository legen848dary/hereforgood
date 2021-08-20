package com.sc.caching;

import com.sc.caching.policies.StoragePolicy;
import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;
import org.junit.Test;

import java.util.function.Function;

import static org.mockito.Mockito.*;

public class StandardCacheGCStressTest extends MultithreadedTestCase {
    private Cache<String, String> cache;
    private Function<String, String> mapping;

    @Override
    public void initialize() {
        mapping = mock(Function.class);
        when(mapping.apply(eq("DJ"))).thenReturn("Sark");
        cache = new StandardCache<>(mapping, StoragePolicy.GC_OPTMIZED);
    }

    public void thread1() throws InterruptedException {
        cache.get("DJ");
    }

    public void thread2() throws InterruptedException {
        cache.get("DJ");
    }

    public void thread3() throws InterruptedException {
        cache.get("DJ");
    }

    @Override
    public void finish() {
        verify(mapping, times(1)).apply(eq("DJ"));
    }

    @Test
    public void testCounter() throws Throwable {
        TestFramework.runManyTimes(new StandardCacheGCStressTest(), 100);
    }
}
