package org.brutality.gui.font.api;

public interface DrawStringWithShadowApi {
    default void handleString(String str, float f, float f2, int n) {
        this.computeInt(str, f - 0.5F, f2, -16777216);
        this.computeInt(str, f + 0.5F, f2, -16777216);
        this.computeInt(str, f, f2 - 0.5F, -16777216);
        this.computeInt(str, f, f2 + 0.5F, -16777216);
        this.computeInt(str, f, f2, n);
    }

    default float computeFloat2(String str) {
        return 11.0F;
    }

    int drawStringWithShadow(String text, float x, float y, int color);

    float computeFloat(String text);

    int computeInt(String text, float x, float y, int color);
}
