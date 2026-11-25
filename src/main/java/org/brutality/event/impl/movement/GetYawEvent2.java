package org.brutality.event.impl.movement;

import org.brutality.event.Event;

public class GetYawEvent2 extends Event {
    private float factor;
    private boolean cancelled2;
    private boolean sprint;
    private float yaw2;
    private double motionY;

    public GetYawEvent2(float factor, float yaw2, boolean flag) {
        this.factor = factor;
        this.cancelled2 = false;
        this.sprint = this.sprint;
        this.yaw2 = yaw2;
        this.motionY = factor;
    }

    public float getYaw() {
        return this.yaw2;
    }

    public void setCancelled(boolean cancelled2) {
        this.cancelled2 = cancelled2;
    }

    public float getFactor() {
        return this.factor;
    }

    public void setSprint(boolean sprint) {
        this.sprint = sprint;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public double getMotionY() {
        return this.motionY;
    }

    public void setYaw(float yaw2) {
        this.yaw2 = yaw2;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public boolean isSprint() {
        return this.sprint;
    }

    public boolean isCancelled() {
        return this.cancelled2;
    }
}
