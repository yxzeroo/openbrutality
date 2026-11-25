package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

public class HudDragHelper {
    private static Object object = null;
    private static boolean flag = false;
    private static int dragOffsetX = 0;
    private static int dragOffsetY = 0;

    public static void handleObject(Object obj, NumberSetting numberSetting, NumberSetting numberSetting2, int n, int n2) {
        if (!EditableModulesHelper.isActive()) {
            handleObject(obj);
        } else {
            int[] ints = computeIntArray();
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            handleObject(obj, numberSetting, numberSetting2, n, n2, ints[0], ints[1], Mouse.isButtonDown(0), sr.getScaledWidth(), sr.getScaledHeight());
        }
    }

    public static int[] computeIntArray(Object obj) {
        if (!EditableModulesHelper.isActive()) {
            return null;
        }
        if (flag && object == obj) {
            int[] ints = computeIntArray();
            int n = ints[0];
            int n2 = ints[1];
            int n3 = n - dragOffsetX;
            int n4 = n2 - dragOffsetY;
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            n3 = Math.max(0, Math.min(n3, sr.getScaledWidth()));
            n4 = Math.max(0, Math.min(n4, sr.getScaledHeight()));
            return new int[]{n3, n4};
        }
        return null;
    }

    public static boolean matchesInt(int n, int n2, double d, double d2, int n3, int n4) {
        return n >= d && n <= d + n3 && n2 >= d2 && n2 <= d2 + n4;
    }

    public static boolean matchesObject(Object obj) {
        return flag && object == obj;
    }

    public static void handleObject(Object obj) {
        if (object == obj) {
            flag = false;
            object = null;
            dragOffsetX = 0;
            dragOffsetY = 0;
        }
    }

    public static int[] computeIntArray() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int x = Mouse.getX();
        int y = Mouse.getY();
        int scaledWidth = sr.getScaledWidth();
        int scaledHeight = sr.getScaledHeight();
        x = x * scaledWidth / mc.displayWidth;
        y = scaledHeight - y * scaledHeight / mc.displayHeight;
        return new int[]{x, y};
    }

    public static void process() {
        flag = false;
        object = null;
        dragOffsetX = 0;
        dragOffsetY = 0;
    }

    public static Object getObject() {
        return object;
    }

    public static boolean matchesObject(Object object2, int n, int n2, int n3, int n4) {
        if (!EditableModulesHelper.isActive()) {
            return false;
        }
        if (flag && object != object2) {
            return false;
        }
        object = object2;
        flag = true;
        dragOffsetX = n - n3;
        dragOffsetY = n2 - n4;
        return true;
    }

    public static boolean isFlag() {
        return flag;
    }

    public static void handleObject(
        Object obj, NumberSetting numberSetting, NumberSetting numberSetting2, int n, int n2, int n3, int n4, boolean flag, int n5, int n6
    ) {
        if (EditableModulesHelper.isActive() && flag) {
            if (!matchesObject(obj) && matchesInt(n3, n4, numberSetting.computeDouble(), numberSetting2.computeDouble(), n, n2)) {
                matchesObject(obj, n3, n4, (int)numberSetting.computeDouble(), (int)numberSetting2.computeDouble());
            }

            if (matchesObject(obj)) {
                numberSetting.handleDouble2(Math.max(0, Math.min(n3 - dragOffsetX, Math.max(0, n5 - n))));
                numberSetting2.handleDouble2(Math.max(0, Math.min(n4 - dragOffsetY, Math.max(0, n6 - n2))));
            }
        } else {
            handleObject(obj);
        }
    }
}
