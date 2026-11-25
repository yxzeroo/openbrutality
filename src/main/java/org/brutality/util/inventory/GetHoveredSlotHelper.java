package org.brutality.util.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;

public class GetHoveredSlotHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean checkState2() {
        return matchesGuiScreen2(mc.currentScreen, "§8Report");
    }

    public static boolean matchesGuiScreen2(GuiScreen screen) {
        return matchesGuiScreen(screen, "Bid Confirmation");
    }

    public static boolean matchesInt(int n) {
        if (n >= 10 && n <= 16) {
            return true;
        }
        if (n >= 19 && n <= 25) {
            return true;
        }
        return n >= 28 && n <= 34 ? true : n >= 37 && n <= 43 || n == 50;
    }

    public static int computeInt(String str) {
        Slot slot = computeSlot(str);
        return slot == null ? -1 : slot.slotNumber;
    }

    public static Slot computeSlot(String str) {
        GuiContainer screen = (GuiContainer)Minecraft.getMinecraft().currentScreen;
        if (screen == null) {
            return null;
        }
        if (!(screen instanceof GuiContainer)) {
            return null;
        }
        for (Slot inventorySlot : screen.inventorySlots.inventorySlots) {
            if (inventorySlot != null && inventorySlot.getHasStack() && inventorySlot.getStack().getDisplayName().toLowerCase().contains(str.toLowerCase())) {
                return inventorySlot;
            }
        }

        return null;
    }

    public static boolean matchesGuiScreen11(GuiScreen screen) {
        return matchesGuiScreen2(screen, "Pick a contract!");
    }

    public static boolean matchesGuiScreen2(GuiScreen screen, String str) {
        if (!(screen instanceof GuiChest)) {
            return false;
        }
        IInventory iInventory = computeIInventory((GuiChest)screen);
        return iInventory == null ? false : iInventory.getDisplayName().getUnformattedText().equals(str);
    }

    public static Slot computeSlot2(String str) {
        GuiContainer screen = (GuiContainer)Minecraft.getMinecraft().currentScreen;
        if (screen == null) {
            return null;
        }
        for (Object inventorySlot : screen.inventorySlots.inventorySlots) {
            Slot slot = (Slot)inventorySlot;
            if (slot != null && slot.getHasStack() && slot.getStack().getDisplayName().toLowerCase().contains(str.toLowerCase())) {
                return slot;
            }
        }

        return null;
    }

    public static boolean matchesGuiScreen3(GuiScreen screen) {
        return matchesGuiScreen2(screen, "Prestige & Renown");
    }

    public static int computeInt() {
        Slot slot = computeSlot2("confirm");
        if (slot != null) {
            return slot.slotNumber;
        }
        GuiContainer screen = (GuiContainer)Minecraft.getMinecraft().currentScreen;
        if (screen == null) {
            return -1;
        }
        Item itemFromBlock = Item.getItemFromBlock(Blocks.stained_hardened_clay);

        for (Object inventorySlot : screen.inventorySlots.inventorySlots) {
            slot = (Slot)inventorySlot;
            if (slot != null && slot.getHasStack() && slot.getStack().getItem() == itemFromBlock && matchesInt2(slot.getStack().getMetadata())) {
                return slot.slotNumber;
            }
        }

        return -1;
    }

    public static boolean matchesGuiScreen8(GuiScreen screen) {
        return matchesGuiScreen2(screen, "Permanent upgrades");
    }

    public static boolean matchesGuiScreen5(GuiScreen screen) {
        return matchesGuiScreen(screen, "Auction Event");
    }

    public static Slot computeSlot(int n) {
        GuiContainer screen = (GuiContainer)Minecraft.getMinecraft().currentScreen;
        if (screen == null) {
            return null;
        }
        if (!(screen instanceof GuiContainer)) {
            return null;
        }
        for (Slot inventorySlot : screen.inventorySlots.inventorySlots) {
            if (inventorySlot != null && inventorySlot.slotNumber == n) {
                return inventorySlot;
            }
        }

        return null;
    }

    public static boolean matchesGuiScreen7(GuiScreen screen) {
        return matchesGuiScreen2(screen, "Quests & Contracts");
    }

    public static boolean matchesGuiScreen6(GuiScreen screen) {
        return matchesGuiScreen2(screen, "Are you sure?")
            || matchesGuiScreen2(screen, "Confirm")
            || matchesGuiScreen2(screen, "Confirm Purchase")
            || matchesGuiScreen2(screen, "Confirm?")
            || matchesGuiScreen(screen, "Are you sure");
    }

    public static Slot computeSlot(Item item) {
        GuiContainer screen = (GuiContainer)Minecraft.getMinecraft().currentScreen;
        if (screen == null) {
            return null;
        }
        if (!(screen instanceof GuiContainer)) {
            return null;
        }
        for (Slot inventorySlot : screen.inventorySlots.inventorySlots) {
            if (inventorySlot != null && inventorySlot.getHasStack() && inventorySlot.getStack().getItem().equals(item)) {
                return inventorySlot;
            }
        }

        return null;
    }

    public static boolean checkState() {
        return matchesGuiScreen2(mc.currentScreen, "Report Cheating/Hacking");
    }

    public static boolean matchesGuiScreen9(GuiScreen screen) {
        return matchesGuiScreen2(screen, "Start event?");
    }

    private static boolean matchesGuiScreen(GuiScreen screen, String str) {
        if (!(screen instanceof GuiChest)) {
            return false;
        }
        IInventory iInventory = computeIInventory((GuiChest)screen);
        return iInventory == null ? false : iInventory.getDisplayName().getUnformattedText().startsWith(str);
    }

    private static IInventory computeIInventory(GuiChest gui) {
        return gui != null && gui.inventorySlots instanceof ContainerChest ? ((ContainerChest)gui.inventorySlots).getLowerChestInventory() : null;
    }

    public static boolean matchesInt2(int n) {
        return n == EnumDyeColor.LIME.getMetadata() || n == EnumDyeColor.GREEN.getMetadata();
    }

    public static boolean matchesGuiScreen12(GuiScreen screen) {
        return matchesGuiScreen(screen, "Prestige?");
    }

    public static Slot getHoveredSlot() {
        GuiContainer screen = (GuiContainer)Minecraft.getMinecraft().currentScreen;
        if (screen == null) {
            return null;
        }
        return !(screen instanceof GuiContainer) ? null : screen.getHoveredSlot();
    }

    public static boolean matchesGuiScreen(GuiScreen screen) {
        return matchesGuiScreen2(screen, "Killstreaks");
    }

    public static boolean matchesGuiScreen4(GuiScreen screen) {
        return matchesGuiScreen2(screen, "Choose a killstreak");
    }

    public static boolean matchesGuiScreen10(GuiScreen screen) {
        return matchesGuiScreen2(screen, "Choose a perk");
    }
}
