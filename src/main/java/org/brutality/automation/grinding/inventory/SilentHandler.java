package org.brutality.automation.grinding.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.util.inventory.MysticItemInfo;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class SilentHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final StopwatchTimer utilHelper8 = new StopwatchTimer(0L);
    private final StopwatchTimer utilHelper82 = new StopwatchTimer(0L);
    private boolean flag;

    public static boolean checkState2() {
        ItemStack stack = GetMysticEnchantLoreHelper.computeItemStack();
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        return item == Items.iron_leggings || item == Items.chainmail_leggings;
    }

    public static boolean checkState() {
        ItemStack stack = GetMysticEnchantLoreHelper.computeItemStack();
        return stack != null && stack.getItem() == Items.leather_leggings;
    }

    public static int computeInt() {
        if (mc.thePlayer == null) {
            return -1;
        }
        int n = -1;
        int n2 = -1;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == Items.leather_leggings) {
                int n3 = (new MysticItemInfo(stack)).count3;
                if (n3 > n2) {
                    n2 = n3;
                    n = i;
                }
            }
        }

        return n;
    }

    public void handleString(String str, long l, boolean flag) {
        if (mc.thePlayer != null && !StartHotbarMovesHelper.flag && !this.flag && !checkState() && this.utilHelper8.matchesLong(500L)) {
            int n = computeInt();
            if (n != -1) {
                int n2 = n < 9 ? n + 36 : n;
                this.utilHelper8.reset();
                this.utilHelper82.reset();
                this.flag = true;
                KeybindStateUtil.process();
                Runnable task = () -> {
                    this.flag = false;
                    this.utilHelper8.reset();
                };
                if ("Silent".equalsIgnoreCase(str)) {
                    StartHotbarMovesHelper.handleInt2(2, n2, flag, l, false, false, task);
                } else {
                    StartHotbarMovesHelper.handleInt(2, n2, flag, l, false, false, task);
                }
            }
        }
    }

    public boolean checkState3() {
        return this.flag || !this.utilHelper82.matchesLong(5000L);
    }

    public void reset() {
        this.flag = false;
        this.utilHelper82.clear();
    }
}
