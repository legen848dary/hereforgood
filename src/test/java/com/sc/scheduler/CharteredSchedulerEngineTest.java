package com.sc.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CharteredSchedulerEngineTest {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_schedule() {
        DeadlineEngine engine = new CharteredSchedulerEngine();
        assertTrue(engine.schedule(System.currentTimeMillis()) > 0);
        assertEquals(1, engine.size());
    }

    @Test
    public void test_cancel() {
        DeadlineEngine engine = new CharteredSchedulerEngine();
        long requestId = engine.schedule(System.currentTimeMillis());
        assertEquals(1, engine.size());
        assertTrue(engine.cancel(requestId));
        assertEquals(0, engine.size());
    }

    @Test
    public void test_poll() {
        {
            Consumer<Long> handler = mock(Consumer.class);
            DeadlineEngine engine = new CharteredSchedulerEngine();
            engine.schedule(100);
            engine.schedule(200);
            assertEquals(2, engine.size());
            int polled = engine.poll(50, handler, 100);
            assertEquals(0, polled);
            verify(handler, never()).accept(anyLong());
        }
        {
            Consumer<Long> handler = mock(Consumer.class);
            DeadlineEngine engine = new CharteredSchedulerEngine();
            engine.schedule(100);
            engine.schedule(200);
            assertEquals(2, engine.size());
            int polled = engine.poll(150, handler, 100);
            assertEquals(1, polled);
            verify(handler, times(1)).accept(anyLong());
        }
        {
            Consumer<Long> handler = mock(Consumer.class);
            DeadlineEngine engine = new CharteredSchedulerEngine();
            engine.schedule(100);
            engine.schedule(200);
            assertEquals(2, engine.size());
            int polled = engine.poll(500, handler, 100);
            assertEquals(2, polled);
            verify(handler, times(2)).accept(anyLong());
        }
    }
}