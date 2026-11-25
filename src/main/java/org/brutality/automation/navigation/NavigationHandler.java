package org.brutality.automation.navigation;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;

public class NavigationHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int computeInt() {
        if (mc.thePlayer == null) {
            return 1;
        }
        int n = 0;
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            n = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
        }

        switch (n + 1) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            default:
                return 1;
        }
    }
}
