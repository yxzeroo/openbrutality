package org.brutality.gui.font.api;

public interface BrutalityFont {
    boolean checkState();

    int getHeight();

    int computeInt(CharSequence text);

    String getName();

    default float computeFloat(CharSequence charSequence, float f, float f2, int n) {
        return this.computeFloat(charSequence, f, f2, n, false);
    }

    boolean isFlag2();

    float computeFloat(CharSequence text, double x, double y, int color, boolean dropShadow);

    default float computeFloat(CharSequence charSequence, float f, float f2, int n, boolean flag) {
        return this.computeFloat(charSequence, (double)(f - this.computeInt(charSequence) / 2.0F), (double)f2, n, flag);
    }

    String computeString(CharSequence text, int width, boolean reverse);

    float computeFloat(char ch);

    default String computeString(CharSequence charSequence, int n) {
        return this.computeString(charSequence, n, false);
    }

    default float computeFloat2(CharSequence charSequence, float f, float f2, int n) {
        return this.computeFloat(charSequence, (double)f, (double)f2, n, false);
    }
}
