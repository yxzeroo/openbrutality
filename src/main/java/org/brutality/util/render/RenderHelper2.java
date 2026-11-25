package org.brutality.util.render;

public class RenderHelper2 {
    public float red;
    public float green;
    public float blue;
    public float alpha;

    public RenderHelper2(String str, int n) {
        this(
            Integer.parseInt(str.substring(0, 2), 16) / 255.0F,
            Integer.parseInt(str.substring(2, 4), 16) / 255.0F,
            Integer.parseInt(str.substring(4, 6), 16) / 255.0F,
            n / 100.0F
        );
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public RenderHelper2() {
        this(1.0F, 0.5F, 0.8F, 0.25F);
    }

    public RenderHelper2(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
}
