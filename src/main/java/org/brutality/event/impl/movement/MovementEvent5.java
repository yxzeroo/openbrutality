package org.brutality.event.impl.movement;

import org.brutality.event.Event;

public class MovementEvent5 extends Event {
    private float factor;
    private float factor2;
    private boolean jump;
    private boolean sneak;
    private double amount;

    public void setFactor2(float f) {
        this.factor2 = f;
    }

    public boolean isJump() {
        return this.jump;
    }

    public float getFactor2() {
        return this.factor2;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public float getFactor() {
        return this.factor;
    }

    public boolean isSneak() {
        return this.sneak;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return this.amount;
    }

    public MovementEvent5(float factor, float f, boolean jump, boolean sneak, double amount) {
        this.factor = factor;
        this.factor2 = f;
        this.jump = jump;
        this.sneak = sneak;
        this.amount = amount;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }
}
