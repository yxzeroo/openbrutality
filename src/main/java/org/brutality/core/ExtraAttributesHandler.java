package org.brutality.core;

import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.utils.player.PlayerHelper12;

public class ExtraAttributesHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern pattern = Pattern.compile("\\btier\\s*:?\\s*(?:iii|3)\\b", 2);

    private static int computeInt(ItemStack stack, EntityPlayer player, boolean flag, boolean flag2) {
        if (stack == null) {
            return -1;
        }
        Item item = stack.getItem();
        boolean flag3 = player != null && GetMysticEnchantLoreHelper.matchesEntityPlayer3(player);
        boolean flag4 = player != null && GetMysticEnchantLoreHelper.matchesEntityPlayer10(player);
        boolean flag5 = flag2 || flag3 || flag;
        if (!flag2 && matchesItemStack2(stack)) {
            return flag5 && flag4 ? 99 : 100;
        }
        if (flag5) {
            if (flag4) {
                if (item == Items.diamond_shovel) {
                    return 100;
                }

                if (item == Items.diamond_sword) {
                    return 98;
                }
            } else {
                if (item == Items.diamond_sword) {
                    return 99;
                }

                if (item == Items.diamond_shovel) {
                    return 98;
                }
            }
        }

        if (item == Items.diamond_shovel && flag4) {
            return 9;
        }
        if (item == Items.diamond_axe) {
            return 8;
        }
        if (item == Items.diamond_sword) {
            return 7;
        }
        if (item == Items.diamond_shovel) {
            return 6;
        }
        if (item == Items.golden_sword) {
            return 5;
        }
        if (item == Items.iron_sword) {
            return 4;
        }
        if (item instanceof ItemSword) {
            return 3;
        }
        return item instanceof ItemAxe ? 2 : 0;
    }

    private static boolean matchesString(String str) {
        return str != null && pattern.matcher(EnumChatFormatting.getTextWithoutFormattingCodes(str)).find();
    }

    public static int computeInt(EntityPlayer player, boolean flag, boolean flag2) {
        int n = 0;
        int n2 = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            int n3 = computeInt(stack, player, flag, flag2);
            if (n3 > n2) {
                n2 = n3;
                n = i;
            }
        }

        return n;
    }

    public static void process2() {
        if (checkState()) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (!matchesItemStack(stack)) {
                    mc.thePlayer.inventory.currentItem = i;
                    return;
                }
            }
        }
    }

    private static boolean matchesItemStack(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        return item instanceof ItemFood
            || item instanceof ItemSword
            || item instanceof ItemBow
            || item instanceof ItemBlock
            || item == Item.getItemFromBlock(Blocks.cactus)
            || item == Items.emerald
            || item == Items.shears
            || item == Items.spawn_egg;
    }

    private static boolean matchesItemStack2(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSword)) {
            return false;
        }
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null) {
            NBTTagCompound compoundTag = tagCompound.getCompoundTag("ExtraAttributes");
            NBTTagCompound nbt = compoundTag.hasKey("UpgradeTier") ? compoundTag : tagCompound;
            if (nbt.hasKey("UpgradeTier", 99)) {
                return nbt.getInteger("UpgradeTier") == 3;
            }

            if (nbt.hasKey("UpgradeTier", 8)) {
                String str = nbt.getString("UpgradeTier").trim();
                if (!str.isEmpty()) {
                    return "3".equals(str) || "III".equalsIgnoreCase(str);
                }
            }
        }

        if (matchesString(stack.getDisplayName())) {
            return true;
        }
        if (tagCompound != null) {
            NBTTagList tagList = tagCompound.getCompoundTag("display").getTagList("Lore", 8);

            for (int i = 0; i < tagList.tagCount(); i++) {
                if (matchesString(tagList.getStringTagAt(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void handleEntityPlayer(EntityPlayer player, boolean flag, boolean flag2) {
        if (checkState()) {
            if (!PlayerHelper12.checkState3()) {
                mc.thePlayer.inventory.currentItem = computeInt(player, flag, flag2);
            }
        }
    }

    public static void process() {
        if (checkState()) {
            for (int i = 0; i < 9; i++) {
                if (mc.thePlayer.inventory.getStackInSlot(i) == null) {
                    mc.thePlayer.inventory.currentItem = i;
                    return;
                }
            }
        }
    }

    private static boolean checkState() {
        return mc.thePlayer != null && !GetMysticEnchantLoreHelper.checkState5() && !CoreHandler2.checkState() && PlayerHelper12.checkState();
    }
}
