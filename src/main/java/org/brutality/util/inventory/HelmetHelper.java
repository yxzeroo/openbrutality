package org.brutality.util.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.util.ExecuteCommandHelper;

public class HelmetHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void handleInt2(int n, int n2) {
        assert n >= 0 && n <= 8;

        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(n);
        if (stack != null) {
            String unlocalizedName = stack.getUnlocalizedName();
            HelmetHelper.HelmetMode helmetMode;
            if (unlocalizedName.contains("helmet")) {
                helmetMode = HelmetHelper.HelmetMode.HELMET;
            } else if (unlocalizedName.contains("chestplate")) {
                helmetMode = HelmetHelper.HelmetMode.CHESTPLATE;
            } else if (unlocalizedName.contains("leggings")) {
                helmetMode = HelmetHelper.HelmetMode.LEGGINGS;
            } else if (unlocalizedName.contains("boots")) {
                helmetMode = HelmetHelper.HelmetMode.BOOTS;
            } else {
                helmetMode = null;
            }

            OnClientTickHelper.handleIsCompleteApi(new IsCompleteHelper(stack, helmetMode.getContainerSlot(), n2));
        }
    }

    public static int computeInt(String str) {
        if (str == null) {
            return -1;
        }
        for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
            if (str.equals(computeString(mc.thePlayer.inventory.mainInventory[i]))) {
                return i < 9 ? i + 36 : i;
            }
        }

        for (int j = 0; j < mc.thePlayer.inventory.armorInventory.length; j++) {
            if (str.equals(computeString(mc.thePlayer.inventory.armorInventory[j]))) {
                return 5 + (3 - j);
            }
        }

        return -1;
    }

    public static List<Integer> computeList(String str) {
        if (str == null) {
            return Collections.emptyList();
        }
        ArrayList list = new ArrayList();

        for (int i = 0; i < mc.thePlayer.inventory.armorInventory.length; i++) {
            if (str.equals(computeString(mc.thePlayer.inventory.armorInventory[i]))) {
                list.add(5 + (3 - i));
            }
        }

        for (int j = 0; j < mc.thePlayer.inventory.mainInventory.length; j++) {
            if (str.equals(computeString(mc.thePlayer.inventory.mainInventory[j]))) {
                list.add(j < 9 ? j + 36 : j);
            }
        }

        return list;
    }

    public static boolean matchesInt2(int n) {
        return n >= 36 && n <= 44;
    }

    public static int computeInt() {
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) == null) {
                return computeInt(i);
            }
        }

        for (int j = 0; j < 9; j++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(j);
            boolean flag = stack != null && stack.getItem() instanceof ItemSword;
            if (stack != null && !GetMysticEnchantLoreHelper.matchesItemStack11(stack) && !flag) {
                return computeInt(j);
            }
        }

        return computeInt(8);
    }

    public static boolean matchesInt(int n) {
        return n >= 5 && n <= 8;
    }

    public static String computeString(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        String unlocalizedName = stack.getUnlocalizedName();
        MysticItemInfo mysticItemInfo = new MysticItemInfo(stack);
        return (unlocalizedName + mysticItemInfo.count3 + mysticItemInfo.count2 + mysticItemInfo.lookupMap).replace(" ", "");
    }

    public static void handleInt(int n, int n2) {
        assert n >= 0 && n <= 35;

        assert n2 >= 0 && n2 <= 8;

        if (mc.currentScreen instanceof GuiInventory) {
            if (GetMysticEnchantLoreHelper.checkState6()) {
                ExecuteCommandHelper.handleString("Canceled swap: Cannot swap while holding an item in your inventory.");
            } else {
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, n2, 2, mc.thePlayer);
            }
        }
    }

    public static int computeInt2(int n) {
        if (matchesInt2(n)) {
            return n - 36;
        }
        return n >= 9 && n <= 35 ? n : -1;
    }

    public static int computeInt(int n) {
        if (n >= 0 && n <= 8) {
            return n + 36;
        }
        return n >= 9 && n <= 35 ? n : -1;
    }

    public static void handleInt(int n) {
        if (mc.thePlayer != null && mc.theWorld != null && mc.currentScreen != null) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, 0, 1, mc.thePlayer);
        }
    }

    public static void handleInt2(int n) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            Container openContainer = mc.thePlayer.openContainer;
            if (openContainer != null && n >= 0 && n < openContainer.inventorySlots.size()) {
                mc.playerController.windowClick(openContainer.windowId, n, 0, 0, mc.thePlayer);
                mc.playerController.updateController();
            }
        }
    }

    public static enum HelmetMode {
        HELMET(5, 3),
        CHESTPLATE(6, 2),
        LEGGINGS(7, 1),
        BOOTS(8, 0);

        private final int armorInventoryIndex;
        private final int containerSlot;

        public static String computeString(int n) {
            for (HelmetHelper.HelmetMode helmetMode : values()) {
                if (helmetMode.getContainerSlot() == n) {
                    return helmetMode.name();
                }
            }

            return String.valueOf(HelmetHelper.computeInt2(n) + 1);
        }

        private static HelmetHelper.HelmetMode[] computeHelmetModeArray() {
            return new HelmetHelper.HelmetMode[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS};
        }

        private HelmetMode(int containerSlot, int armorInventoryIndex) {
            this.containerSlot = containerSlot;
            this.armorInventoryIndex = armorInventoryIndex;
        }

        public int getArmorInventoryIndex() {
            return this.armorInventoryIndex;
        }

        public int getContainerSlot() {
            return this.containerSlot;
        }
    }
}
