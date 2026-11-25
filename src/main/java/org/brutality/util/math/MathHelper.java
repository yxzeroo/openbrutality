package org.brutality.util.math;

import java.util.Random;
import net.minecraft.client.Minecraft;

public class MathHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float[] computeFloatArray(float[] floats) {
        double d = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        double d2 = d * d * d * 8.0;
        float[] floats2 = new float[]{(float)Math.round(floats[0] / d2), (float)Math.round(floats[1] / d2)};
        floats2[0] = (float)(floats2[0] * d2);
        floats2[1] = (float)(floats2[1] * d2);
        return floats2;
    }

    public static void handleFloatArray(float[] floats) {
        int n = new Random().nextInt(5);

        for (int i = 0; i < n; i++) {
            int n2 = new Random().nextInt(1000);
            floats[0] = (float)(floats[0] + computeDouble(n2));
            floats[1] = (float)(floats[1] + computeDouble(n2));
        }
    }

    public static float[] computeFloatArray(double d, double d2, double d3) {
        double d4 = d - mc.thePlayer.posX;
        double d5 = d2 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double d6 = d3 - mc.thePlayer.posZ;
        double d7 = net.minecraft.util.MathHelper.sqrt_double(d4 * d4 + d6 * d6);
        float f = (float)Math.toDegrees(Math.atan2(d6, d4)) - 90.0F;
        float f2 = (float)(-Math.toDegrees(Math.atan2(d5, d7)));
        return new float[]{f, f2};
    }

    public static float computeFloat(float f, float f2) {
        return Math.abs(net.minecraft.util.MathHelper.wrapAngleTo180_float(f2 - f));
    }

    public static double computeDouble(double d) {
        return Math.sin(Math.toRadians(System.currentTimeMillis() / d * 100.0 % 360.0));
    }

    public static boolean matchesInt(int n) {
        return n > 0 && n <= 100;
    }
}
