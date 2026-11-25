package org.brutality.util;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;

public class ComputeScheduledFutureHelper {
    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
    private static final long timestamp = 25L;

    public static ScheduledFuture<? extends Future<?>> computeScheduledFuture(Runnable task, long l, TimeUnit timeUnit) {
        return scheduledThreadPoolExecutor.schedule(() -> threadPoolExecutor.submit(task), l, timeUnit);
    }

    public static int computeInt() {
        return threadPoolExecutor.getActiveCount();
    }

    public static void processRunnable(Runnable task, long l, TimeUnit timeUnit) {
        scheduledThreadPoolExecutor.schedule(() -> mc.addScheduledTask(task), l, timeUnit);
    }

    public static int getPoolSize() {
        return threadPoolExecutor.getPoolSize();
    }

    public static int w() {
        return threadPoolExecutor.getLargestPoolSize();
    }

    public static long computeLong() {
        return threadPoolExecutor.getCompletedTaskCount();
    }

    public static void handleRunnable2(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public static ScheduledFuture<?> computeScheduledFuture(Runnable task, long l, long l2, TimeUnit timeUnit) {
        return scheduledThreadPoolExecutor.scheduleAtFixedRate(task, l, l2, timeUnit);
    }

    static {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor2 = new ScheduledThreadPoolExecutor(3);
        scheduledThreadPoolExecutor = scheduledThreadPoolExecutor2;
        scheduledThreadPoolExecutor2.setRemoveOnCancelPolicy(true);
    }

    public static void handleRunnable(Runnable task) {
        processRunnable(task, timestamp, TimeUnit.MILLISECONDS);
    }
}
