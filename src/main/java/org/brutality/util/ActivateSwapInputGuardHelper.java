package org.brutality.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre;

public class ActivateSwapInputGuardHelper {
    private static ScheduledFuture<?> scheduledFuture;
    private static ScheduledFuture<?> scheduledFuture2;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static boolean flag = false;
    private static boolean id = false;

    public static synchronized void activateSwapInputGuard(int n) {
        id = true;
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(false);
        }

        scheduledFuture = scheduledExecutorService.schedule(() -> id = false, (long)n, TimeUnit.MILLISECONDS);
    }

    public static synchronized void process() {
        id = false;
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(false);
        }
    }

    public static synchronized void process2() {
        lockKeyboardInput(1000);
    }

    public static synchronized void process3() {
        flag = false;
        if (scheduledFuture2 != null && !scheduledFuture2.isDone()) {
            scheduledFuture2.cancel(false);
        }
    }

    public void handlePre(Pre pre) {
        if (mc.thePlayer != null) {
            if (flag && mc.currentScreen instanceof GuiInventory) {
                pre.setCanceled(true);
            }
        }
    }

    public static boolean isId() {
        return id;
    }

    public static synchronized void lockKeyboardInput(int n) {
        flag = true;
        if (scheduledFuture2 != null && !scheduledFuture2.isDone()) {
            scheduledFuture2.cancel(false);
        }

        scheduledFuture2 = scheduledExecutorService.schedule(() -> flag = false, (long)n, TimeUnit.MILLISECONDS);
    }
}
