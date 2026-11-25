package org.brutality.util;

import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class KeybindStateUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void process() {
        if (mc.gameSettings != null) {
            for (KeyBinding keyBinding : new KeyBinding[]{
                mc.gameSettings.keyBindForward,
                mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint
            }) {
                if (keyBinding != null) {
                    keyBinding.pressed = false;
                }
            }

            mc.gameSettings.keyBindSneak.pressed = mc.thePlayer != null
                && mc.thePlayer.isRiding()
                && Keyboard.isCreated()
                && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);
        }
    }

    public static void process2() {
        if (mc.gameSettings != null && mc.currentScreen == null && Display.isCreated() && Display.isActive()) {
            handlePredicate(GameSettings::isKeyDown);
        }
    }

    private static void handlePredicate(Predicate<KeyBinding> predicate) {
        GameSettings settings = mc.gameSettings;
        if (settings != null) {
            for (KeyBinding keyBinding : new KeyBinding[]{
                settings.keyBindForward, settings.keyBindBack, settings.keyBindLeft, settings.keyBindRight, settings.keyBindJump, settings.keyBindSprint, settings.keyBindSneak
            }) {
                if (keyBinding != null) {
                    KeyBinding.setKeyBindState(keyBinding.getKeyCode(), predicate.test(keyBinding));
                }
            }
        }
    }
}
