package org.brutality.utils.render;

public class RenderHelper5 {
    public static boolean matchesFloat(float f, float f2, float f3, float f4, int n, int n2) {
        return n >= f && n <= f + f3 && n2 >= f2 && n2 <= f2 + f4;
    }
}
