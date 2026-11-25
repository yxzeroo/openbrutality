package org.brutality.utils.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class PlayerHelper10 {
    public static double computeDouble(EntityPlayer player) {
        double d = 0.0;

        for (int i = 0; i < 4; i++) {
            ItemStack currentArmor = player.getCurrentArmor(i);
            if (currentArmor != null && currentArmor.getItem() instanceof ItemArmor) {
                d += SwapHelper.computeDouble(currentArmor);
            }
        }

        return d;
    }
}
