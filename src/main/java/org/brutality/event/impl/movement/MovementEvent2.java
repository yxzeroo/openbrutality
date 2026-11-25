package org.brutality.event.impl.movement;

import org.brutality.event.Event;

public class MovementEvent2 extends Event {
    private float factor;
    private float factor2;

    public void handleFloat(float f) {
        this.setFactor2(f);
        this.setFactor(f);
    }

    public void setFactor2(float f) {
        this.factor2 = f;
    }

    public float getFactor2() {
        return this.factor2;
    }

    public MovementEvent2(float factor, float f) {
        this.factor = factor;
        this.factor2 = f;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public float getFactor() {
        return this.factor;
    }
}
