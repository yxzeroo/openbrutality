package org.brutality.automation.hunting;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public final class HuntingHandler {
    private HuntingHandler() {
    }

    public static double computeDouble(EntityLivingBase living, EntityLivingBase living2) {
        if (living != null && living2 != null) {
            double d = living.posX - living2.posX;
            double d2 = living.posZ - living2.posZ;
            float f = (float)Math.toDegrees(Math.atan2(d2, d)) - 90.0F;
            return Math.abs(MathHelper.wrapAngleTo180_float(living2.rotationYaw - f));
        }
        return 180.0;
    }

    public static boolean matchesEntityLivingBase(EntityLivingBase living, EntityLivingBase living2, double d) {
        return living != null
            && living2 != null
            && living.isEntityAlive()
            && living2.isEntityAlive()
            && (living.getHealth() + living.getAbsorptionAmount()) * 0.5 <= d
            && living2.getLook(1.0F).dotProduct(living.getPositionEyes(1.0F).subtract(living2.getPositionEyes(1.0F)).normalize()) > Math.cos(Math.toRadians(45.0));
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        if (player != null && player.inventory != null) {
            ItemStack stack = player.inventory.armorInventory[2];
            ItemStack stack2 = player.inventory.armorInventory[0];
            return stack != null && stack.getItem() == Items.diamond_chestplate && stack2 != null && stack2.getItem() == Items.diamond_boots;
        }
        return false;
    }

    public static boolean matchesEntityLivingBase(EntityLivingBase living, EntityLivingBase living2) {
        return living != null && living2 != null && computeDouble(living, living2) >= 90.0;
    }
}
