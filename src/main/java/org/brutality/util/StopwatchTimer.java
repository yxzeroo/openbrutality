package org.brutality.util;

public class StopwatchTimer {
    private long timestamp;

    public long getTimestamp() {
        return this.timestamp;
    }

    public void clear() {
        this.timestamp = 0L;
    }

    public long computeLong() {
        return System.currentTimeMillis() - this.timestamp;
    }

    public boolean matchesLong(long l) {
        return this.computeLong() >= l;
    }

    public void reset() {
        this.timestamp = System.currentTimeMillis();
    }

    public StopwatchTimer() {
        this.reset();
    }

    public static int computeInt(int n) {
        return n <= 0 ? 0 : 25 + (n - 1) * 50;
    }

    public StopwatchTimer(long timestamp) {
        this.timestamp = timestamp;
    }
}
