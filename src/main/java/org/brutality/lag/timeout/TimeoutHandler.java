package org.brutality.lag.timeout;

public abstract class TimeoutHandler {
    private volatile boolean flag = false;

    public final boolean checkState2() {
        return this.flag || this.checkState();
    }

    protected abstract boolean checkState();

    public final void process() {
        this.flag = true;
    }
}
