package org.brutality.utils.player;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.utils.GetCurrentScreenHelper;

public class SwapHelper extends GetCurrentScreenHelper {
    public static void swap(int n, int n2) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, n2, 2, mc.thePlayer);
    }

    public static void handleInt(int n, int n2, boolean flag) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, n2, flag ? 1 : 0, mc.thePlayer);
    }

    public static double computeDouble(ItemStack stack) {
        double d = 0.0;
        ItemArmor item = (ItemArmor)stack.getItem();
        double d2 = d + item.damageReduceAmount;
        if (stack.isItemEnchanted()) {
            d2 += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.25;
        }

        return d2;
    }

    public static int computeInt2(Block block) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() == block) {
                return i;
            }
        }

        return -1;
    }

    public static boolean checkState7() {
        return computeItem() instanceof ItemSword;
    }

    public static Item computeItem() {
        return mc.thePlayer != null && mc.thePlayer.getCurrentEquippedItem() != null ? mc.thePlayer.getCurrentEquippedItem().getItem() : null;
    }

    public static boolean matchesItem(Item item) {
        return item == null || Item.getIdFromItem(item) == 0;
    }

    public static void handleInt(int n) {
        mc.playerController.windowClick(0, n, 1, 4, mc.thePlayer);
    }

    public static float computeFloat(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            ItemSword item = (ItemSword)stack.getItem();
            float f = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
            float f2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1.5F;
            return item.getDamageVsEntity() + f + f2;
        }
        return 0.0F;
    }

    public static int computeInt(Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == item) {
                return i;
            }
        }

        return -1;
    }
}
