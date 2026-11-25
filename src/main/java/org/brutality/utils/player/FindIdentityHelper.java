package org.brutality.utils.player;

import java.util.Locale;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.brutality.core.CoreHandler2;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.utils.combat.CombatHelper;

public final class FindIdentityHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final long timestamp = 8500L;

    public static EntityPlayer computeEntityPlayer() {
        if (KillAura.instance != null
            && KillAura.instance.checkState44()
            && KillAura.entityLivingBase2 instanceof EntityPlayer
            && matchesEntityPlayer((EntityPlayer)KillAura.entityLivingBase2)) {
            return (EntityPlayer)KillAura.entityLivingBase2;
        }
        EntityPlayer player = CombatHelper.computeEntityPlayer(timestamp);
        return matchesEntityPlayer(player) ? player : null;
    }

    public static boolean matchesItemStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == 2;
    }

    public static ItemStack computeItemStack(int n) {
        if (mc.thePlayer == null) {
            return null;
        }
        if (n >= 36 && n <= 44) {
            return mc.thePlayer.inventory.mainInventory[n - 36];
        }
        return n >= 9 && n <= 35 ? mc.thePlayer.inventory.mainInventory[n] : null;
    }

    public static String computeString(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        NBTTagCompound nbt = stack.hasTagCompound() ? (NBTTagCompound)stack.getTagCompound().copy() : new NBTTagCompound();
        NBTTagCompound compoundTag = nbt.getCompoundTag("ExtraAttributes");

        for (String str : new String[]{"Nonce", "nonce"}) {
            if (compoundTag.hasKey(str)) {
                return Item.getIdFromItem(stack.getItem()) + ":nonce:" + compoundTag.getTag(str);
            }
        }

        compoundTag.removeTag("Lives");
        if (nbt.hasKey("ExtraAttributes")) {
            nbt.setTag("ExtraAttributes", compoundTag);
        }

        if (compoundTag.hasKey("CustomEnchants")) {
            nbt.getCompoundTag("display").removeTag("Lore");
        }

        nbt.removeTag("RepairCost");
        return Item.getIdFromItem(stack.getItem()) + ":" + (stack.isItemStackDamageable() ? 0 : stack.getMetadata()) + ":" + nbt;
    }

    public static void handleInt(int n, int n2, boolean flag, boolean flag2, long l, Runnable task) {
        if (flag) {
            StartHotbarMovesHelper.handleInt2(n, n2, flag2, l, true, true, task);
        } else {
            StartHotbarMovesHelper.handleInt(n, n2, flag2, l, true, true, task);
        }
    }

    public static boolean matchesFlag(boolean flag) {
        return mc.thePlayer != null
            && mc.theWorld != null
            && !mc.thePlayer.isDead
            && mc.thePlayer.getHealth() > 0.0F
            && (mc.currentScreen == null || flag && mc.currentScreen instanceof GuiInventory)
            && mc.thePlayer.openContainer == mc.thePlayer.inventoryContainer
            && mc.thePlayer.inventory.getItemStack() == null
            && !StartHotbarMovesHelper.flag
            && !CoreHandler2.checkState();
    }

    public static boolean checkState() {
        return matchesFlag(false);
    }

    public static int computeInt(Predicate<ItemStack> predicate, boolean flag) {
        if (mc.thePlayer == null) {
            return -1;
        }
        for (int i = 0; i < (flag ? 36 : 9); i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && predicate.test(stack)) {
                return i < 9 ? 36 + i : i;
            }
        }

        return -1;
    }

    private static boolean matchesEntityPlayer(EntityPlayer player) {
        return player != null && player != mc.thePlayer && !player.isDead && player.getHealth() > 0.0F && (mc.theWorld == null || player.worldObj == mc.theWorld);
    }

    public static int findIdentity(String str, boolean flag) {
        return str == null ? -1 : computeInt(stack -> str.equals(computeString(stack)), flag);
    }

    public static boolean matchesItemStack2(ItemStack stack) {
        return matchesItemStack(stack) && (matchesItemStack(stack, "escape-pod") || matchesItemStack(stack, "phoenix"));
    }

    public static boolean matchesItemStack(ItemStack stack, String str) {
        if (stack == null) {
            return false;
        }
        String str2 = str.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "").replace(" ", "");
        if (stack.hasTagCompound()) {
            NBTTagList tagList = stack.getTagCompound().getCompoundTag("ExtraAttributes").getTagList("CustomEnchants", 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
                String str3 = tagList.getCompoundTagAt(i).getString("Key").toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
                if (str3.equals(str2)) {
                    return true;
                }
            }

            String str4 = stack.getTagCompound().toString().toLowerCase(Locale.ROOT).replace("-", "").replace("_", "").replace(" ", "");
            if (str4.contains(str2)) {
                return true;
            }
        }

        return stack.getDisplayName().toLowerCase(Locale.ROOT).replace(" ", "").contains(str2);
    }

    private FindIdentityHelper() {
    }
}
