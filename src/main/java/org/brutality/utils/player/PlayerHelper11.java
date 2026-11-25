package org.brutality.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.brutality.module.Module;

public class PlayerHelper11 {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static PlayerHelper11.Entry computeEntry2(String str) {
        return computeEntry(str);
    }

    public static boolean matchesEntry(PlayerHelper11.Entry entry, Module module) {
        if (entry == null || mc.thePlayer == null) {
            return false;
        }
        if (entry.count < 0 || entry.count > 8) {
            return false;
        }
        return !entry.isValid() ? false : PlayerHelper12.matchesObject(module, entry.count, 1500L);
    }

    public static PlayerHelper11.Entry computeEntry(String str) {
        if (mc.thePlayer != null && str != null) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && stack.getDisplayName() != null && stack.getDisplayName().contains(str)) {
                    return new PlayerHelper11.Entry(i, str);
                }
            }

            return null;
        }
        return null;
    }

    public static boolean matchesInt2(int n) {
        return mc.thePlayer != null && n >= 0 && n <= 8 ? mc.thePlayer.inventory.getStackInSlot(n) != null : false;
    }

    public static boolean matchesInt(int n) {
        if (mc.thePlayer != null && n >= 0 && n <= 8) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(n);
            return stack == null ? false : PlayerHelper12.matchesObject(null, n, 1500L);
        }
        return false;
    }

    public static boolean matchesString(String str) {
        return computeEntry(str) != null;
    }

    public static boolean matchesEntryArray(PlayerHelper11.Entry... entries) {
        if (mc.thePlayer == null) {
            return false;
        }
        for (PlayerHelper11.Entry entry : entries) {
            if (entry == null || !entry.isValid()) {
                return false;
            }
        }

        return true;
    }

    public static boolean matchesEntry(PlayerHelper11.Entry entry) {
        return matchesEntry(entry, null);
    }

    public static class Entry {
        public final int count;
        public final String text;

        public boolean isValid() {
            if (PlayerHelper11.mc.thePlayer != null && this.count >= 0 && this.count <= 8) {
                ItemStack stack = PlayerHelper11.mc.thePlayer.inventory.getStackInSlot(this.count);
                return stack == null ? false : stack.getDisplayName() != null && stack.getDisplayName().contains(this.text);
            }
            return false;
        }

        @Override
        public String toString() {
            return "SlotInfo{slot=" + this.count + ", item='" + this.text + "'}";
        }

        public Entry(int count, String text) {
            this.count = count;
            this.text = text;
        }
    }
}
