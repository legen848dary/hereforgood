package com.sc.scheduler;

import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Mockito.mock;

public class CharteredSchedulerEnginePollStressTest extends MultithreadedTestCase {

    private CharteredSchedulerEngine engine;

    @Override
    public void initialize() {
        engine = new CharteredSchedulerEngine();
    }

    public void thread1() {
        Consumer<Long> handler = mock(Consumer.class);
        engine.schedule(100);
        engine.schedule(200);
        engine.schedule(300);
        engine.poll(220, handler, 2);
    }

    public void thread2() {
        engine.schedule(100);
        engine.schedule(150);
    }

    @Override
    public void finish() {
        assertEquals(3, engine.size());
    }

    @Test
    public void testCounter() throws Throwable {
        TestFramework.runManyTimes(new CharteredSchedulerEnginePollStressTest(), 1000);
    }
}