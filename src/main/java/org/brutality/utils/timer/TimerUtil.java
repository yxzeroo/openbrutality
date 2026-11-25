package org.brutality.utils.timer;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

public class TimerUtil {
    private long timestamp;
    private long timestamp2 = -1L;
    private boolean flag;
    private static float timerSpeed = 1.0F;

    public long computeLong() {
        return this.computeLong6() - this.timestamp;
    }

    public double computeDouble() {
        return this.computeLong6() - this.getTimestamp();
    }

    public boolean matchesLong9(long l) {
        return this.computeLong4() >= l;
    }

    public void setTimestamp2(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean matchesDouble(double d) {
        return Math.max(0.0, (double)(this.computeLong3() - this.timestamp)) >= d;
    }

    public TimerUtil(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean matchesLong10(long l) {
        return this.computeLong4() >= l;
    }

    public boolean matchesLong7(long l) {
        if (this.matchesLong4(l)) {
            this.reset();
            return true;
        }
        return false;
    }

    public void handleLong3(long l) {
        this.timestamp = this.computeLong6() - l;
    }

    public boolean matchesLong8(long l) {
        return this.computeLong3() - l >= this.timestamp;
    }

    public void handleFloat(float timerSpeed2) {
        timerSpeed = timerSpeed2;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean matchesDouble2(double d) {
        return this.computeLong4() >= (long)d;
    }

    public void start() {
        this.reset();
        this.flag = false;
    }

    public boolean matchesLong5(long l) {
        return this.computeLong() >= l;
    }

    public boolean matchesFloat2(float f) {
        return (float)(this.computeLong6() - this.timestamp) >= f;
    }

    public void process() {
        this.timestamp = this.computeLong6();
    }

    public long computeLong7() {
        return this.computeLong3() - this.timestamp;
    }

    public float computeFloat() {
        return timerSpeed;
    }

    public boolean matchesLong6(long l) {
        return (float)(this.computeLong6() - this.timestamp) >= (float)l / timerSpeed;
    }

    public long computeLong(long l) {
        return l - this.computeLong4();
    }

    public boolean matchesLong2(long l) {
        if (this.computeLong() >= l) {
            this.reset();
            return true;
        }
        return false;
    }

    public boolean checkState() {
        return this.matchesLong(this.timestamp + this.computeLong4(), System::currentTimeMillis);
    }

    public boolean matchesFloat3(float f) {
        return (float)(this.computeLong3() - this.timestamp2) >= f;
    }

    public boolean matchesFloat(float f) {
        return (float)(this.computeLong3() - this.timestamp) > f;
    }

    public long computeLong3() {
        return System.currentTimeMillis();
    }

    public boolean matchesLong4(long l) {
        return this.computeLong4() >= l;
    }

    private boolean matchesBooleanSupplier(BooleanSupplier booleanSupplier) {
        if (booleanSupplier.getAsBoolean() && !this.flag) {
            this.flag = true;
            return true;
        }
        return false;
    }

    public void handleLong2(long l) {
        this.timestamp = this.computeLong3() - l;
    }

    public TimerUtil(int n) {
        this.timestamp = this.computeLong3() + n;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public static short computeInt2(float f) {
        return (short)(1000.0F / f);
    }

    public boolean matchesLong2(long l, boolean flag) {
        if ((float)(this.computeLong6() - this.timestamp) >= (float)l / timerSpeed) {
            if (flag) {
                this.reset();
            }

            return true;
        }
        return false;
    }

    public boolean matchesInt(int n) {
        return this.computeLong4() >= n;
    }

    private boolean matchesLong(long l, LongSupplier longSupplier) {
        return longSupplier.getAsLong() >= l;
    }

    public boolean matchesLong(long l, boolean flag) {
        if (this.computeLong4() >= l) {
            if (flag) {
                this.reset();
            }

            return true;
        }
        return false;
    }

    public long computeLong2() {
        return this.computeLong3() - this.timestamp;
    }

    public void setTimestamp3(long timestamp) {
        this.timestamp = timestamp;
    }

    public short computeInt(float f) {
        return (short)(1000.0F / f);
    }

    public void handleLong(long l) {
        this.timestamp = this.computeLong3() + l;
    }

    public long computeLong5() {
        return this.computeLong3() - this.timestamp;
    }

    public boolean checkState2() {
        return this.matchesBooleanSupplier(() -> this.computeLong3() >= this.timestamp + this.computeLong4());
    }

    public boolean matchesLong3(long l) {
        return this.computeLong3() - this.timestamp >= l;
    }

    public long computeLong6() {
        return System.nanoTime() / 1000000L;
    }

    public void reset() {
        this.timestamp = this.computeLong3();
        this.timestamp2 = this.computeLong3();
        this.flag = false;
    }

    @Override
    public String toString() {
        long timestamp = this.timestamp;
        long l = this.computeLong4();
        float f = timerSpeed;
        long l2 = l;
        long l3 = timestamp;
        return "BrutalityTimer{lastMS=" + l3 + ", timePassed=" + l2 + ", timerSpeed=" + f + "}";
    }

    public boolean matchesLong(long l) {
        return this.computeLong() >= l;
    }

    public TimerUtil() {
        this.reset();
    }

    public long computeLong4() {
        return this.computeLong3() - this.timestamp;
    }
}
