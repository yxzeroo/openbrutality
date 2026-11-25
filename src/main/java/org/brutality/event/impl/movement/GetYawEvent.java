package org.brutality.event.impl.movement;

import org.brutality.event.Event;

public class GetYawEvent extends Event {
    private float yaw2;
    private float factor;
    private float factor2;
    private float factor3;

    public float getYaw() {
        return this.yaw2;
    }

    public void setFactor2(float f) {
        this.factor2 = f;
    }

    public void setFactor3(float f) {
        this.factor3 = f;
    }

    public GetYawEvent(float yaw2, float factor, float f, float f2, float f3) {
        this.yaw2 = yaw2;
        this.factor = factor;
        this.factor2 = f;
        this.factor3 = f2;
    }

    public float getFactor() {
        return this.factor;
    }

    public float getFactor2() {
        return this.factor2;
    }

    public void setYaw(float yaw2) {
        this.yaw2 = yaw2;
    }

    public float getFactor3() {
        return this.factor3;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }
}
