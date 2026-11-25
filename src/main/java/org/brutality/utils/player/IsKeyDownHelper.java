package org.brutality.utils.player;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class IsKeyDownHelper {
    public static boolean isKeyDown(KeyBinding keyBinding) {
        return keyBinding == null ? false : matchesInt(keyBinding.getKeyCode());
    }

    public static boolean matchesInt(int n) {
        if (n == 0) {
            return false;
        }
        try {
            if (n >= 0) {
                return Keyboard.isKeyDown(n);
            }
            int n2 = n + 100;
            return n2 >= 0 && n2 < 16 && Mouse.isButtonDown(n2);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static void handleInt(int n) {
        setKeyBindState(n, matchesInt(n));
    }

    public static String computeString(int n) {
        if (n < 0) {
            switch (n) {
                case -7:
                    return "MOUSE7";
                case -6:
                    return "MOUSE6";
                case -5:
                    return "MOUSE5";
                case -4:
                    return "MOUSE4";
                case -3:
                    return "MMB";
                case -2:
                    return "RMB";
                case -1:
                    return "LMB";
                default:
                    return n < -10 ? "MOUSE" + (-n - 10) : "MOUSE" + Math.abs(n);
            }
        } else {
            return Keyboard.getKeyName(n);
        }
    }

    public static void setKeyBindState(int n, boolean flag) {
        KeyBinding.setKeyBindState(n, flag);
    }

    public static void handleInt2(int n) {
        KeyBinding.onTick(n);
    }
}
