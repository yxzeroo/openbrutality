package org.brutality.module.impl.pit.hypixel.swap;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import org.brutality.core.CoreHandler2;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class InventoryShortcuts extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private static InventoryShortcuts instance;

    @Override
    public void onEvent(Event event) {
    }

    public InventoryShortcuts() {
        super("InventoryShortcuts", 0, Category.PIT);
        instance = this;
    }

    public static int computeInt(int n) {
        return n >= 9 && n <= 35 ? (n - 9) % 9 : -1;
    }

    public static boolean matchesSlot(Slot slot, int n, boolean flag) {
        Minecraft mc = Minecraft.getMinecraft();
        if (instance != null
            && instance.checkState44()
            && n == 0
            && flag
            && slot != null
            && mc.thePlayer != null
            && mc.playerController != null
            && !StartHotbarMovesHelper.flag
            && !CoreHandler2.checkState()
            && mc.thePlayer.inventory.getItemStack() == null
            && slot.inventory == mc.thePlayer.inventory) {
            int n2 = computeInt(slot.getSlotIndex());
            if (n2 < 0) {
                return false;
            }
            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slot.slotNumber, n2, 2, mc.thePlayer);
            return true;
        }
        return false;
    }
}
