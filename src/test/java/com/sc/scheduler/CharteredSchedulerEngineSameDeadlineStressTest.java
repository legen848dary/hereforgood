package com.sc.scheduler;

import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CharteredSchedulerEngineSameDeadlineStressTest extends MultithreadedTestCase {

    private CharteredSchedulerEngine engine;

    long r1, r2;

    @Override
    public void initialize() {
        engine = new CharteredSchedulerEngine();
    }

    public void thread1() {
        r1 = engine.schedule(100);
    }

    public void thread2() {
        r2 = engine.schedule(100);
    }

    @Override
    public void finish() {
        assertTrue(r1 < 3);
        assertTrue(r2 < 3);
        assertTrue(r1 != r2);
        assertEquals(2, engine.size());
    }

    @Test
    public void testCounter() throws Throwable {
        TestFramework.runManyTimes(new CharteredSchedulerEngineSameDeadlineStressTest(), 100);
    }
}