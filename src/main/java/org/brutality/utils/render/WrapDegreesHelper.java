package org.brutality.utils.render;

public final class WrapDegreesHelper {
    private static final double amount = 0.06;
    private float yaw2;
    private float pitch2;
    private long timestamp;
    private boolean initialized;

    public void reset() {
        this.initialized = false;
    }

    private static float wrapDegrees(float f) {
        float f2 = f % 360.0F;
        if (f2 >= 180.0F) {
            f2 -= 360.0F;
        }

        if (f2 < -180.0F) {
            f2 += 360.0F;
        }

        return f2;
    }

    public void handleFloat(float yaw2, float pitch2, long timestamp) {
        double d = (timestamp - this.timestamp) / 1.0E9;
        if (this.initialized && !(d > 0.25) && !(d < 0.0)) {
            float f = (float)(1.0 - Math.exp(-d / 0.06));
            this.yaw2 = this.yaw2 + wrapDegrees(yaw2 - this.yaw2) * f;
            this.pitch2 = this.pitch2 + (pitch2 - this.pitch2) * f;
        } else {
            this.yaw2 = yaw2;
            this.pitch2 = pitch2;
            this.initialized = true;
        }

        this.timestamp = timestamp;
    }

    public float getYaw() {
        return this.yaw2;
    }

    public float getPitch() {
        return this.pitch2;
    }
}
