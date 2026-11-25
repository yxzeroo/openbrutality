package org.brutality.event.impl.movement;

import org.brutality.event.Event;

public class MovementEvent extends Event {
    private boolean flag;
    private boolean flag2;
    private float factor;
    private boolean flag3;

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public MovementEvent(boolean flag, boolean flag2, boolean flag3) {
        this.flag = flag;
        this.flag2 = flag2;
        this.flag3 = flag3;
        this.factor = 0.6F;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag2() {
        return this.flag2;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public boolean isFlag3() {
        return this.flag3;
    }

    public float getFactor() {
        return this.factor;
    }

    public void setFlag2(boolean flag) {
        this.flag2 = flag;
    }

    public MovementEvent(boolean flag, boolean flag2) {
        this(flag, flag2, false);
    }

    public void setFlag3(boolean flag) {
        this.flag3 = flag;
    }
}
