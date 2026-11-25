package org.brutality.utils.performance;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.utils.render.RenderHelper;

public final class InvalidateHelper {
    private static ScaledResolution scaledResolution;
    private static ScaledResolution scaledResolution2;
    private static int count = -1;
    private static int count2 = -1;
    private static int count3 = -1;
    private static boolean flag;
    private static int count4 = -1;
    private static int count5 = -1;
    private static int count6 = -1;
    private static boolean flag2;

    private InvalidateHelper() {
    }

    public static ScaledResolution computeScaledResolution(Minecraft mc) {
        int width = mc.displayWidth;
        int height = mc.displayHeight;
        int n = RenderHelper.computeInt(mc.gameSettings.guiScale);
        boolean unicode = mc.isUnicode();
        if (RenderHelper.isActive()) {
            if (scaledResolution2 == null || width != count || height != count2 || n != count3 || unicode != flag) {
                scaledResolution2 = new ScaledResolution(mc);
                count = width;
                count2 = height;
                count3 = n;
                flag = unicode;
            }

            return scaledResolution2;
        }
        if (scaledResolution == null || width != count4 || height != count5 || n != count6 || unicode != flag2) {
            scaledResolution = new ScaledResolution(mc);
            count4 = width;
            count5 = height;
            count6 = n;
            flag2 = unicode;
        }

        return scaledResolution;
    }

    public static void invalidate() {
        scaledResolution = null;
        scaledResolution2 = null;
        count4 = -1;
        count5 = -1;
        count6 = -1;
    }

    public static ScaledResolution computeScaledResolution() {
        return computeScaledResolution(Minecraft.getMinecraft());
    }
}
