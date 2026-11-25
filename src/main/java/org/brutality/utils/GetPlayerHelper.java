package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class GetPlayerHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void handleString(String str) {
        mc.thePlayer.addChatMessage(new ChatComponentText(str));
    }

    public static boolean matchesEntityPlayer(EntityPlayer player, Item item) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == item) {
                return true;
            }
        }

        return false;
    }

    public static EntityPlayer getPlayer() {
        return mc.thePlayer;
    }

    public static int computeInt(EntityPlayer player, Item item) {
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == item) {
                return i;
            }
        }

        return -1;
    }

    public static World getWorld() {
        return mc.theWorld;
    }

    public static class Entry {
        public static final String text = EnumChatFormatting.BLACK.toString();
        public static final String text2 = EnumChatFormatting.DARK_RED.toString();
        public static final String text3 = EnumChatFormatting.RED.toString();
        public static final String text4 = EnumChatFormatting.GREEN.toString();
        public static final String text5 = EnumChatFormatting.GOLD.toString();
        public static final String text6 = EnumChatFormatting.GRAY.toString();
        public static final String text7 = EnumChatFormatting.DARK_GRAY.toString();
        public static final String text8 = EnumChatFormatting.BLUE.toString();
        public static final String text9 = EnumChatFormatting.DARK_BLUE.toString();
        public static final String text10 = EnumChatFormatting.LIGHT_PURPLE.toString();
        public static final String text11 = EnumChatFormatting.DARK_PURPLE.toString();
        public static final String text12 = EnumChatFormatting.AQUA.toString();
        public static final String text13 = EnumChatFormatting.YELLOW.toString();
        public static final String text14 = EnumChatFormatting.WHITE.toString();
    }
}
