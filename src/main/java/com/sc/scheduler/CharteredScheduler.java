package com.sc.scheduler;

import java.util.function.Consumer;

public class CharteredScheduler implements DeadlineEngine {
    @Override
    public long schedule(long deadlineMs) {
        return 0;
    }

    @Override
    public boolean cancel(long requestId) {
        return false;
    }

    @Override
    public int poll(long nowMs, Consumer<Long> handler, int maxPoll) {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }
}
