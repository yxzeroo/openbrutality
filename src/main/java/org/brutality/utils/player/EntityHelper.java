package org.brutality.utils.player;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.EntityList;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.brutality.utils.mcp.McpApi;

public final class EntityHelper {
    private static boolean flag;

    public static boolean isFlag() {
        return flag;
    }

    private static String computeString(ItemStack stack) {
        String str = String.valueOf(StatCollector.translateToLocal(stack.getUnlocalizedName() + ".name")).trim();
        String stringFromID = EntityList.getStringFromID(stack.getMetadata());
        if (stringFromID != null) {
            str = str + " " + StatCollector.translateToLocal("entity." + stringFromID + ".name");
        }

        return str;
    }

    public static void process() {
        flag = false;
        if (McpApi.mc.currentScreen instanceof GuiChest) {
            Container openContainer = McpApi.mc.thePlayer.openContainer;
            int n = 0;
            int n2 = 0;
            int n3 = 0;

            for (Slot inventorySlot : openContainer.inventorySlots) {
                if (inventorySlot.getHasStack() && n3++ <= 26) {
                    ItemStack stack = inventorySlot.getStack();
                    if (stack != null) {
                        String displayName = stack.getDisplayName();
                        String str = computeString(stack);
                        String str2 = displayName.toLowerCase().replace(" ", "");
                        String str3 = str.toLowerCase().replace(" ", "");
                        if (str2.contains(str3)) {
                            n = (int)(n - 0.1);
                        } else {
                            n++;
                        }

                        n2++;
                    }
                }
            }

            flag = (float)n / n2 > 0.5F;
        }
    }
}
