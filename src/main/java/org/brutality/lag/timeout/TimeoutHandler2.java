package org.brutality.lag.timeout;

import org.brutality.module.Module;

public final class TimeoutHandler2 extends TimeoutHandler {
    private final Module module;
    private final TimeoutHandler timeoutHandler;
    private boolean flag = false;

    public TimeoutHandler2(Module module) {
        this(module, null);
    }

    @Override
    protected boolean checkState() {
        if (!this.module.checkState44()) {
            this.flag = true;
        }

        return this.flag ? true : this.timeoutHandler != null && this.timeoutHandler.checkState2();
    }

    public TimeoutHandler2(Module module, TimeoutHandler timeoutHandler) {
        this.module = module;
        this.timeoutHandler = timeoutHandler;
        if (!module.checkState44()) {
            this.flag = true;
        }
    }
}
