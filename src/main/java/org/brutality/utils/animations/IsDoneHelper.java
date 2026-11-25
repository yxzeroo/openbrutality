package org.brutality.utils.animations;

import org.brutality.utils.timer.TimerUtil;

public abstract class IsDoneHelper {
    public TimerUtil timerUtil = new TimerUtil();
    protected int duration;
    protected double amount;
    protected ForwardsType forwardsType;

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void process() {
        this.computeIsDoneHelper(this.forwardsType.computeForwardsType());
    }

    public IsDoneHelper(int n, double d) {
        this(n, d, ForwardsType.FORWARDS);
    }

    public boolean matchesForwardsType(ForwardsType forwardsType) {
        return this.isDone() && this.forwardsType.equals(forwardsType);
    }

    public double getAmount() {
        return this.amount;
    }

    public IsDoneHelper computeIsDoneHelper(ForwardsType forwardsType) {
        if (this.forwardsType != forwardsType) {
            this.forwardsType = forwardsType;
            this.timerUtil.setTimestamp2(System.currentTimeMillis() - (this.duration - Math.min((long)this.duration, this.timerUtil.computeLong5())));
        }

        return this;
    }

    public void reset() {
        this.timerUtil.reset();
    }

    public boolean isDone() {
        return this.timerUtil.matchesLong10(this.duration);
    }

    public double computeDouble() {
        if (this.forwardsType.checkState()) {
            return this.isDone() ? this.amount : this.computeDouble((double)this.timerUtil.computeLong5() / this.duration) * this.amount;
        }
        if (this.isDone()) {
            return 0.0;
        }
        if (this.checkState()) {
            double d = Math.min((long)this.duration, Math.max(0L, this.duration - this.timerUtil.computeLong5()));
            return this.computeDouble(d / this.duration) * this.amount;
        }
        return (1.0 - this.computeDouble((double)this.timerUtil.computeLong5() / this.duration)) * this.amount;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double computeDouble2() {
        return 1.0 - (double)this.timerUtil.computeLong5() / this.duration * this.amount;
    }

    public ForwardsType getForwardsType() {
        return this.forwardsType;
    }

    protected boolean checkState() {
        return false;
    }

    public IsDoneHelper(int duration, double amount, ForwardsType forwardsType) {
        this.duration = duration;
        this.amount = amount;
        this.forwardsType = forwardsType;
    }

    public void process2() {
        this.timerUtil.setTimestamp2(System.currentTimeMillis() - this.duration);
    }

    protected abstract double computeDouble(double x);
}
