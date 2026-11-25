package org.brutality.util.contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.util.inventory.GetHoveredSlotHelper;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;

public class IsVileContractChoiceHelper {
    public static Contract computeContract(List<Contract> contracts) {
        return contracts.stream().min(Comparator.comparingInt(Contract::getPriority)).orElse(null);
    }

    public static boolean matchesGuiScreen(GuiScreen screen) {
        return contractStatusContains(screen, "reached daily limit!");
    }

    public static boolean matchesSlot(Slot slot) {
        return slot != null && slot.getHasStack() && slot.getStack().getDisplayName().toLowerCase().contains("choice");
    }

    private static List<Contract> computeList(GuiContainer gui) {
        return gui.inventorySlots.inventorySlots.stream().filter(IsVileContractChoiceHelper::matchesSlot).map(Contract::new).collect(Collectors.toList());
    }

    public static boolean matchesGuiScreen3(GuiScreen screen) {
        return contractStatusContains(screen, "too low level!");
    }

    private static boolean contractStatusContains(GuiScreen screen, String str) {
        var list = computeList(screen);
        return list != null && list.stream().anyMatch(str2 -> str2.toLowerCase().contains(str));
    }

    public static List<Integer> computeList2(GuiScreen screen) {
        if (screen == null) {
            return Collections.emptyList();
        }
        if (!(screen instanceof GuiContainer gui)) {
            return Collections.emptyList();
        }
        if (!GetHoveredSlotHelper.matchesGuiScreen7(gui)) {
            return Collections.emptyList();
        }
        List list = new ArrayList();

        for (Slot inventorySlot : gui.inventorySlots.inventorySlots) {
            if (inventorySlot != null && inventorySlot.getHasStack()) {
                ItemStack stack = inventorySlot.getStack();
                if (stack != null && stack.getItem() == Items.paper) {
                    list.add(inventorySlot.slotNumber);
                }
            }
        }

        return list;
    }

    public static List<Contract> computeList3(GuiScreen screen) {
        return (List<Contract>)(!(screen instanceof GuiContainer) ? new ArrayList<>() : computeList((GuiContainer)screen));
    }

    public static Contract computeContract(GuiScreen screen) {
        if (screen == null) {
            return null;
        }
        if (!(screen instanceof GuiContainer gui)) {
            return null;
        }
        if (!GetHoveredSlotHelper.matchesGuiScreen11(screen)) {
            return null;
        }
        var contracts = computeList(gui);
        return contracts.isEmpty() ? null : computeContract(contracts);
    }

    public static List<Contract> computeList4(GuiScreen screen) {
        if (screen == null) {
            return null;
        }
        if (!(screen instanceof GuiContainer gui)) {
            return null;
        }
        if (!GetHoveredSlotHelper.matchesGuiScreen11(screen)) {
            return null;
        }
        var contracts = computeList(gui);
        if (contracts.isEmpty()) {
            return null;
        }
        for (int i = 0; i < contracts.size(); i++) {
            ExecuteCommandHelper.handleString2(i + 1 + ". " + ((Contract)contracts.get(i)).computeString());
        }

        return contracts.stream().filter(Contract::isFlag).sorted(Comparator.comparingInt(Contract::getPriority)).collect(Collectors.toList());
    }

    public static boolean isVileContractChoice(Slot slot) {
        if (!matchesSlot(slot)) {
            return false;
        }
        ItemStack stack = slot.getStack();
        return stack != null && GetMysticEnchantLoreHelper.computeList(stack).stream().anyMatch(str -> str.toLowerCase().contains("chunk of vile"));
    }

    public static String computeString(Slot slot) {
        if (!matchesSlot(slot)) {
            return null;
        }
        ItemStack stack = slot.getStack();
        if (stack == null) {
            return null;
        }
        var list = GetMysticEnchantLoreHelper.computeList(stack);
        list.replaceAll(ExecuteCommandHelper::computeString6);

        for (int i = 0; i < list.size(); i++) {
            if (((String)list.get(i)).contains("Task:")) {
                int n = i + 1;
                if (n >= list.size()) {
                    return null;
                }

                StringBuilder sb = new StringBuilder();

                for (int j = n; j < list.size(); j++) {
                    String str = (String)list.get(j);
                    if (str.isEmpty()) {
                        break;
                    }

                    if (sb.length() > 0) {
                        sb.append(' ');
                    }

                    sb.append(str);
                }

                return sb.toString().trim();
            }
        }

        return null;
    }

    public static boolean matchesGuiScreen2(GuiScreen screen) {
        return contractStatusContains(screen, "not enough gold!");
    }

    private static List<String> computeList(GuiScreen screen) {
        if (screen == null) {
            return null;
        }
        if (!(screen instanceof GuiContainer)) {
            return null;
        }
        if (!GetHoveredSlotHelper.matchesGuiScreen7((GuiContainer)screen)) {
            return null;
        }
        Slot slot = GetHoveredSlotHelper.computeSlot("big time contract");
        if (slot != null && slot.getHasStack()) {
            var list = GetMysticEnchantLoreHelper.computeList(slot.getStack());
            list.replaceAll(ExecuteCommandHelper::computeString6);
            return list;
        }
        return null;
    }
}
