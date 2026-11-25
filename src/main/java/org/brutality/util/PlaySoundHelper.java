package org.brutality.util;

import net.minecraft.client.Minecraft;

public final class PlaySoundHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void process11() {
        playSound("note.harp", 3.0F, 0.5F);
    }

    public static void process15() {
        playSound("random.click", 1.2F, 1.5F);
    }

    public static void process17() {
        playSound("note.harp", 3.0F, 1.5F);
    }

    public static void playSound(String str, float f, float f2) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            mc.addScheduledTask(() -> {
                if (mc.thePlayer != null) {
                    try {
                        mc.thePlayer.playSound(str, f, f2);
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    }

    public static void process21() {
        playSound("random.orb", 2.0F, 0.4F);
    }

    public static void process20() {
        playSound("random.anvil_land", 0.7F, 1.0F);
    }

    public static void handleFloat2(float f) {
        float f2 = f / 10.0F;
        playSound("note.pling", f2 + 1.0F, 2.5F - f2);
    }

    public static void handleFloat(float f) {
        playSound("mob.sheep.shear", 2.0F, f);
    }

    public static void process23() {
        playSound("random.orb", 1.0F, 2.5F);
    }

    public static void process2() {
        playSound("note.pling", 2.0F, 0.2F);
    }

    public static void process8() {
        playSound("mob.bat.death", 0.3F, 0.2F);
    }

    public static void process5() {
        playSound("mob.blaze.hit", 2.0F, 0.2F);
    }

    public static void process9() {
        playSound("random.click", 1.0F, 1.2F);
    }

    public static void process14() {
        playSound("random.anvil_use", 1.0F, 1.0F);
    }

    public static void process12() {
        playSound("mob.villager.no", 2.0F, 0.7F);
    }

    public static void process16() {
        playSound("mob.blaze.hit", 2.0F, 2.0F);
    }

    public static void process18() {
        for (int i = 0; i < 5; i++) {
            playSound("random.orb", 1.0F, 1.5F + i * 0.25F);
        }
    }

    public static void process22() {
        playSound("note.harp", 2.5F, 1.5F);
    }

    public static void process19() {
        playSound("note.harp", 2.5F, 0.2F);
    }

    public static void process() {
        playSound("random.levelup", 0.7F, 1.2F);
        playSound("random.levelup", 0.8F, 1.4F);
    }

    public static void process7() {
        playSound("random.click", 0.5F, 2.0F);
    }

    public static void process24() {
        playSound("note.pling", 2.0F, 0.5F);
    }

    public static void process3() {
        playSound("mob.bat.death", 0.3F, 2.0F);
    }

    public static void process6() {
        playSound("random.anvil_land", 2.0F, 2.0F);
    }

    public static void process4() {
        playSound("random.anvil_break", 2.0F, 1.0F);
    }

    public static void process10() {
        playSound("random.orb", 1.0F, 1.2F);
    }

    private PlaySoundHelper() {
    }

    public static void process13() {
        playSound("note.pling", 2.0F, 2.0F);
    }
}
