package org.brutality.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class UtilHelper3 {
    public static void handleInt2(int n) {
        KeyBinding.setKeyBindState(n, n < 0 ? Mouse.isButtonDown(n + 100) : Keyboard.isKeyDown(n));
    }

    public static void handleInt3(int n) {
        if (Minecraft.getMinecraft().currentScreen != null) {
            KeyBinding.setKeyBindState(n, false);
        } else {
            KeyBinding.setKeyBindState(n, n < 0 ? Mouse.isButtonDown(n + 100) : Keyboard.isKeyDown(n));
        }
    }

    public static boolean matchesInt(int n) {
        return n < 0 ? Mouse.isButtonDown(n + 100) : Keyboard.isKeyDown(n);
    }

    public static void handleInt(int n, boolean flag) {
        KeyBinding.setKeyBindState(n, flag);
    }

    public static void handleInt(int n) {
        KeyBinding.onTick(n);
    }
}
