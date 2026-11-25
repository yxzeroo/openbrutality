package org.brutality.clickgui.newclick;

import net.minecraft.client.gui.Gui;

public final class NewclickComponent3 {
    public static final int count = 10;
    private static final int count2 = -12961222;
    private static final int count3 = -7697782;
    private static final int count4 = -7697782;
    private static final int count5 = -1754827;

    private static void handleInt(int n, int n2) {
        int n3 = n + 2;
        int n4 = n2 + 6;
        int n5 = n + 4;
        int n6 = n2 + 8;
        int n7 = n + 8;
        int n8 = n2 + 2;
        handleInt(n3, n4, n5, n6);
        handleInt(n5, n6, n7, n8);
    }

    public static boolean matchesInt(int n, int n2, int n3, int n4) {
        return n >= n3 && n <= n3 + 10 && n2 >= n4 && n2 <= n4 + 10;
    }

    private static void handleInt(int n, int n2, int n3, int n4) {
        int n5 = Math.abs(n3 - n);
        int n6 = Math.abs(n4 - n2);
        int n7 = n < n3 ? 1 : -1;
        int n8 = n2 < n4 ? 1 : -1;
        int n9 = n5 - n6;

        while (true) {
            Gui.drawRect(n, n2, n + 1, n2 + 1, -1754827);
            if (n == n3 && n2 == n4) {
                return;
            }

            int n10 = 2 * n9;
            if (n10 > -n6) {
                n9 -= n6;
                n += n7;
            }

            if (n10 < n5) {
                n9 += n5;
                n2 += n8;
            }
        }
    }

    public static void handleInt(int n, int n2, boolean flag) {
        Gui.drawRect(n - 1, n2 - 1, n + 10 + 1, n2 + 10 + 1, -12961222);
        Gui.drawRect(n, n2, n + 10, n2 + 10, flag ? -7697782 : -7697782);
        if (flag) {
            handleInt(n, n2);
        }
    }

    private NewclickComponent3() {
    }

    public static int computeInt(int n, int n2) {
        return n + n2 - 10 - 6;
    }
}
