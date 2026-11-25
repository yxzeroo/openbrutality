package org.brutality.util.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.brutality.util.ExecuteCommandHelper;

public final class NoSwapsNeededHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static List<IsCompleteHelper> computeList(String str, int n, int n2, int n3, boolean flag, String str2, boolean flag2) {
        int n4 = HelmetHelper.computeInt(str);
        if (n4 == -1) {
            return Collections.emptyList();
        }
        if (n4 == n) {
            ExecuteCommandHelper.handleString6("No Swaps Needed");
            return Collections.emptyList();
        }
        boolean flag3 = HelmetHelper.matchesInt2(n);
        boolean flag4 = HelmetHelper.matchesInt2(n4);
        if (flag4 || flag3) {
            ExecuteCommandHelper.handleString6("1 Swap");
            IsCompleteHelper isCompleteHelper = new IsCompleteHelper(str, n, n2);
            isCompleteHelper.setDescription(str2);
            isCompleteHelper.handleFlag(flag2);
            return Collections.singletonList(isCompleteHelper);
        }
        if (!flag) {
            return Collections.emptyList();
        }
        ArrayList list = new ArrayList();
        IsCompleteHelper isCompleteHelper2 = new IsCompleteHelper(str, n3, n2);
        IsCompleteHelper isCompleteHelper3 = new IsCompleteHelper(str, n, n2);
        isCompleteHelper2.setDescription("pre-" + str2);
        isCompleteHelper3.setDescription(str2);
        isCompleteHelper2.handleFlag(flag2);
        isCompleteHelper3.handleFlag(flag2);
        ExecuteCommandHelper.handleString6("Before swap: " + str + " to temp slot " + n3);
        ExecuteCommandHelper.handleString6("Main swap: " + str + " to target slot " + n);
        list.add(isCompleteHelper2);
        list.add(isCompleteHelper3);
        ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(n).getStack();
        if (stack != null) {
            IsCompleteHelper isCompleteHelper4 = new IsCompleteHelper(stack, n4, n2);
            isCompleteHelper4.setDescription("post-" + str2);
            isCompleteHelper4.handleFlag(flag2);
            list.add(isCompleteHelper4);
            ExecuteCommandHelper.handleString6("After swap: " + stack + " to source slot " + n4);
            ExecuteCommandHelper.handleString6("3 Swaps");
        } else {
            ExecuteCommandHelper.handleString6("2 Swaps");
        }

        return list;
    }

    public static List<IsCompleteHelper> computeList(ItemStack stack, int n, int n2, int n3, boolean flag, String str) {
        return computeList(stack, n, n2, n3, flag, str, false);
    }

    public static List<IsCompleteHelper> computeList(ItemStack stack, int n, int n2, int n3, boolean flag, String str, boolean flag2) {
        return computeList(HelmetHelper.computeString(stack), n, n2, n3, flag, str, flag2);
    }

    private NoSwapsNeededHelper() {
    }

    public static List<IsCompleteHelper> computeList(String str, int n, int n2, int n3, boolean flag, String str2) {
        return computeList(str, n, n2, n3, flag, str2, false);
    }

    public static List<IsCompleteHelper> computeList(int n, int n2, int n3, int n4, boolean flag, String str) {
        return computeList(mc.thePlayer.inventory.getStackInSlot(n), n2, n3, n4, flag, str, false);
    }

    public static List<IsCompleteHelper> computeList(int n, int n2, int n3, int n4, boolean flag, String str, boolean flag2) {
        return computeList(mc.thePlayer.inventory.getStackInSlot(n), n2, n3, n4, flag, str, flag2);
    }
}
