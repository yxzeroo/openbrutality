package org.brutality.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.BasicEvent3;

public class LockForMillisHelper {
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static final LockForMillisHelper instance = new LockForMillisHelper();
    private static volatile boolean flag = false;
    private static ScheduledFuture<?> scheduledFuture;

    public static void onEvent(Event event) {
        if (event instanceof BasicEvent3) {
            instance.onMouseInput((BasicEvent3)event);
        }
    }

    public static synchronized void process() {
        lockForMillis(1000);
    }

    public static synchronized void lockForMillis(int n) {
        flag = true;
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(false);
        }

        scheduledFuture = scheduledExecutorService.schedule(() -> flag = false, (long)n, TimeUnit.MILLISECONDS);
    }

    public void onMouseInput(BasicEvent3 basicEvent3) {
        if (flag) {
            int n = basicEvent3.count6;
            if (Minecraft.getMinecraft().currentScreen != null) {
                if (n != 0 && n != 1) {
                    return;
                }

                basicEvent3.cancel();
            }
        }
    }

    public static synchronized void process2() {
        flag = false;
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(false);
        }
    }
}
