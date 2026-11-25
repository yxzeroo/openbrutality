package org.brutality.event.impl.render;

import org.brutality.event.Event;

public class RenderEvent extends Event {
    private float factor = 1.0F;
    private float factor2 = 40.0F;
    private float factor3 = 8000.0F;
    private float factor4 = 200.0F;
    private float factor5 = 14.0F;

    public void setFactor3(float f) {
        this.factor3 = f;
    }

    public void setFactor4(float f) {
        this.factor4 = f;
    }

    public float getFactor4() {
        return this.factor4;
    }

    public float getFactor2() {
        return this.factor2;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public float getFactor() {
        return this.factor;
    }

    public void setFactor2(float f) {
        this.factor2 = f;
    }

    public float getFactor3() {
        return this.factor3;
    }

    public void setFactor5(float f) {
        this.factor5 = f;
    }

    public float getFactor5() {
        return this.factor5;
    }
}
