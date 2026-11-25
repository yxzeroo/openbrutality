package org.brutality.automation.grinding.lifecycle;

import java.util.Random;

public final class LifecycleHandler2 {
    private final Random random;
    private boolean started;
    private boolean flag;
    private int count;

    public LifecycleHandler2(Random random) {
        this.random = random;
    }

    public boolean checkState() {
        if (this.count == 0) {
            this.flag = this.started ? !this.flag : this.random.nextBoolean();
            this.started = true;
            this.count = 1 + this.random.nextInt(4);
        }

        this.count--;
        return this.flag;
    }

    public void reset() {
        this.started = false;
        this.count = 0;
    }
}
