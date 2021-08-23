package com.sc.scheduler;

import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;
import org.junit.Test;

public class CharteredSchedulerEngineSameDeadlineCancelStressTest extends MultithreadedTestCase {

    private CharteredSchedulerEngine engine;

    long r1, r2;

    @Override
    public void initialize() {
        engine = new CharteredSchedulerEngine();
    }

    public void thread1() {
        r1 = engine.schedule(100);
        engine.cancel(r1);
    }

    public void thread2() {
        r2 = engine.schedule(100);
    }

    @Override
    public void finish() {
        assertEquals(1, engine.size());
    }

    @Test
    public void testCounter() throws Throwable {
        TestFramework.runManyTimes(new CharteredSchedulerEngineSameDeadlineCancelStressTest(), 1);
    }
}