package org.brutality.utils.animation;

public class IsFinishedHelper {
    private LinearType linearType;
    private long timestamp;
    private long startTime;
    private double amount;
    private double amount2;
    private double value;
    private boolean finished2;

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setLinearType(LinearType linearType) {
        this.linearType = linearType;
    }

    public double getValue() {
        return this.value;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.amount = this.value;
        this.finished2 = false;
    }

    public void handleDouble2(double amount2) {
        long now = System.currentTimeMillis();
        if (this.amount2 != amount2) {
            this.amount2 = amount2;
            this.amount = this.value;
            this.startTime = now;
            this.finished2 = false;
        } else {
            this.finished2 = now - this.timestamp > this.startTime;
            if (this.finished2) {
                this.value = amount2;
                return;
            }
        }

        double d = this.computeDouble();
        double d2 = this.linearType.computeDouble(d);
        if (this.value > amount2) {
            this.value = this.amount - (this.amount - amount2) * d2;
        } else {
            this.value = this.amount + (amount2 - this.amount) * d2;
        }
    }

    public IsFinishedHelper(LinearType linearType, long timestamp) {
        this.linearType = linearType;
        this.timestamp = timestamp;
        this.startTime = System.currentTimeMillis();
        this.value = 0.0;
        this.finished2 = false;
    }

    public double computeDouble() {
        long l = System.currentTimeMillis() - this.startTime;
        return Math.max(0.0, Math.min(1.0, (double)l / this.timestamp));
    }

    public void handleDouble(double value) {
        this.value = value;
        this.amount = value;
    }

    public boolean isFinished() {
        return this.finished2;
    }
}
