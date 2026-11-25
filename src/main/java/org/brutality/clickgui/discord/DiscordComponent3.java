package org.brutality.clickgui.discord;

public final class DiscordComponent3 {
    private float factor;
    private float factor2;
    private final float factor3;

    public void handleFloat(float factor) {
        this.factor = factor;
        this.factor2 = factor;
    }

    public float getFactor2() {
        return this.factor2;
    }

    public static float computeFloat(float f) {
        f = Math.max(0.0F, Math.min(1.0F, f));
        float f2 = 1.0F - f;
        return 1.0F - f2 * f2 * f2;
    }

    public void setFactor2(float f) {
        this.factor2 = f;
    }

    public static float computeFloat2(float f) {
        f = Math.max(0.0F, Math.min(1.0F, f));
        return f * f * (3.0F - 2.0F * f);
    }

    public float computeFloat() {
        this.factor = this.factor + (this.factor2 - this.factor) * this.factor3;
        if (Math.abs(this.factor2 - this.factor) < 0.008F) {
            this.factor = this.factor2;
        }

        return this.factor;
    }

    public DiscordComponent3(float factor, float f) {
        this.factor = factor;
        this.factor2 = factor;
        this.factor3 = f;
    }
}
