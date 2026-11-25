package org.brutality.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.brutality.core.CoreHandler2;
import org.brutality.module.Module;
import org.brutality.module.impl.pit.hypixel.automation.AutoAura;
import org.brutality.module.impl.pit.hypixel.automation.AutoEgg;
import org.brutality.module.impl.pit.hypixel.automation.AutoFeast;
import org.brutality.module.impl.pit.hypixel.automation.AutoPotato;
import org.brutality.module.impl.pit.hypixel.automation.AutoSpire;
import org.brutality.module.impl.pit.hypixel.swap.AutoLifesteal;
import org.brutality.module.impl.player.AutoHeal;

public final class PlayerHelper12 {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final long timestamp = 1500L;
    private static Object object;
    private static Object object2;
    private static Object object3;
    private static int slot = -1;
    private static ItemStack item;
    private static long expiresAt;
    private static boolean flag;

    private static void process() {
        object3 = null;
        slot = -1;
        item = null;
        flag = false;
        expiresAt = 0L;
    }

    public static void reset() {
        process();
        object2 = null;
        object = null;
    }

    public static void handleObject(Object obj) {
        if (object3 == obj) {
            process();
        }
    }

    private static void process2() {
        if (object != mc.thePlayer || object2 != mc.theWorld) {
            reset();
            object = mc.thePlayer;
            object2 = mc.theWorld;
        }
    }

    public static boolean matchesObject(Object obj, int n, long l) {
        process2();
        if (!checkState() || n < 0 || n > 8) {
            return false;
        }
        if (AutoLifesteal.computeInt() >= 0) {
            AutoLifesteal.checkState();
            return false;
        }
        return matchesObject(obj, n, l, false);
    }

    public static boolean checkState() {
        return mc.thePlayer != null
            && mc.theWorld != null
            && !mc.thePlayer.isDead
            && mc.thePlayer.getHealth() > 0.0F
            && !CoreHandler2.checkState()
            && !StartHotbarMovesHelper.flag
            && mc.currentScreen == null
            && !checkState2();
    }

    public static boolean matchesObject(Object obj, int n) {
        process2();
        return checkState() && n >= 0 && n <= 8 && matchesObject(obj, n, 1500L, true);
    }

    private static boolean checkState2() {
        AutoHeal instance = AutoHeal.getInstance();
        if (instance != null && instance.checkState44() && instance.isCount()) {
            return true;
        }
        AutoFeast instance2 = AutoFeast.getInstance();
        if (instance2 != null && instance2.checkState44() && instance2.isCount()) {
            return true;
        }
        AutoAura instance3 = AutoAura.getInstance();
        if (instance3 != null && instance3.checkState44() && instance3.isCount()) {
            return true;
        }
        if (AutoSpire.instance != null && AutoSpire.instance.checkState44() && AutoSpire.instance.isCount()) {
            return true;
        }
        return AutoPotato.instance != null && AutoPotato.instance.checkState44() && AutoPotato.instance.isCount()
            ? true
            : AutoEgg.instance != null && AutoEgg.instance.checkState44() && AutoEgg.instance.isCount();
    }

    private static boolean matchesObject(Object obj, int slot2, long l, boolean flag2) {
        if (mc.thePlayer.inventory.currentItem != slot2 && mc.thePlayer.isUsingItem()) {
            StartHotbarMovesHelper.process();
        }

        object3 = obj == null ? PlayerHelper12.class : obj;
        slot = slot2;
        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);
        item = stack == null ? null : stack.copy();
        flag = flag2;
        expiresAt = System.currentTimeMillis() + Math.max(1L, Math.min(10000L, l));
        if (mc.thePlayer.inventory.currentItem != slot) {
            mc.thePlayer.inventory.currentItem = slot;
            if (mc.playerController != null) {
                mc.playerController.syncCurrentPlayItem();
            } else if (mc.thePlayer.sendQueue != null) {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
            }
        }

        return true;
    }

    public static boolean checkState3() {
        process2();
        if (AutoLifesteal.computeInt() >= 0) {
            AutoLifesteal.checkState();
            return true;
        }
        if (flag) {
            process();
        }

        if (object3 == null) {
            return false;
        }
        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);
        boolean flag2 = stack != null && item != null ? stack.getItem() == item.getItem() && ItemStack.areItemStackTagsEqual(stack, item) : stack == item;
        if (System.currentTimeMillis() <= expiresAt
            && flag2
            && mc.thePlayer.inventory.currentItem == slot
            && (!(object3 instanceof Module) || ((Module)object3).checkState44())) {
            return true;
        }
        process();
        return false;
    }

    private PlayerHelper12() {
    }
}
