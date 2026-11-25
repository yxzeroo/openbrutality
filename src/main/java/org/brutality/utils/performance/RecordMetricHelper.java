package org.brutality.utils.performance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;

public class RecordMetricHelper {
    private static final int count = 100;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final long[] longValueArray = new long[100];
    private static int frameCount;
    private static int frameIndex;
    private static long timestamp;
    private static final Map<String, RecordMetricHelper.Entry> lookupMap = new ConcurrentHashMap<>();
    private static long timestamp2 = System.nanoTime();
    private static long timestamp3 = 0L;
    private static double amount = 0.0;
    private static double amount2 = 0.0;
    private static double amount3 = Double.MAX_VALUE;
    private static double amount4 = 0.0;
    private static long timestamp4 = 0L;
    private static double amount5 = 0.0;
    private static double amount6 = 0.0;
    private static double amount7 = 0.0;

    public static void recordMetric(String str, double d) {
        RecordMetricHelper.Entry entry = lookupMap.computeIfAbsent(str, str2 -> new RecordMetricHelper.Entry());
        entry.handleDouble(d);
    }

    public static String computeString() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════════╗\n");
        sb.append("║              REAL-TIME PERFORMANCE MONITOR                     ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════╝\n\n");
        sb.append("Frame Performance:\n");
        sb.append(String.format("  Average FPS:    %.1f\n", amount));
        sb.append(String.format("  Frame Time:     %.2fms (avg) / %.2fms (min) / %.2fms (max)\n", amount2, amount3, amount4));
        sb.append(String.format("  Sample Size:    %d frames\n", frameCount));
        sb.append("\nMemory Usage:\n");
        sb.append(String.format("  Used:           %.1f MB\n", amount5));
        sb.append(String.format("  Max:            %.1f MB\n", amount6));
        sb.append(String.format("  Usage:          %.1f%%\n", amount7));
        if (!lookupMap.isEmpty()) {
            sb.append("\nCustom Metrics:\n");

            for (Map.Entry entry : lookupMap.entrySet()) {
                RecordMetricHelper.Entry val = (RecordMetricHelper.Entry)entry.getValue();
                sb.append(String.format("  %-20s: %.2f (avg) / %.2f (min) / %.2f (max)\n", entry.getKey(), val.amount2, val.amount3, val.amount4));
            }
        }

        return sb.toString();
    }

    public static double getAmount() {
        return amount;
    }

    public static void process4() {
        timestamp3 = System.nanoTime();
    }

    public static double getAmount5() {
        return amount5;
    }

    public static List<String> computeList() {
        ArrayList list = new ArrayList();
        list.add(String.format("§eFPS: §f%.0f §7(%.1fms)", amount, amount2));
        list.add(String.format("§eMemory: §f%.0fMB / %.0fMB §7(%.0f%%)", amount5, amount6, amount7));
        if (GetTopSlowestHelper.isEnabled()) {
            List topSlowest = GetTopSlowestHelper.getTopSlowest(3);
            if (!topSlowest.isEmpty()) {
                list.add("§eTop Modules:");

                for (int i = 0; i < Math.min(3, topSlowest.size()); i++) {
                    GetTopSlowestHelper.Entry2 entry2 = (GetTopSlowestHelper.Entry2)topSlowest.get(i);
                    String str = i == 0 ? "§c" : "§6";
                    list.add(String.format("  %s%s: §f%.2fms", str, entry2.text, entry2.getAmount()));
                }
            }
        }

        return list;
    }

    private static void process3() {
        Runtime runtime = Runtime.getRuntime();
        long l = runtime.maxMemory();
        long l2 = runtime.totalMemory();
        long l3 = runtime.freeMemory();
        long l4 = l2 - l3;
        amount6 = l / 1024.0 / 1024.0;
        amount5 = l4 / 1024.0 / 1024.0;
        amount7 = l4 * 100.0 / l;
    }

    public static void process2() {
        if (timestamp3 != 0L) {
            long now = System.nanoTime();
            long l = now - timestamp3;
            timestamp = timestamp - longValueArray[frameIndex];
            longValueArray[frameIndex] = l;
            timestamp += l;
            frameIndex = (frameIndex + 1) % 100;
            if (frameCount < 100) {
                frameCount++;
            }

            process();
            if (System.currentTimeMillis() - timestamp4 > 1000L) {
                process3();
                timestamp4 = System.currentTimeMillis();
            }

            timestamp2 = now;
            timestamp3 = 0L;
        }
    }

    public static void reset() {
        Arrays.fill(longValueArray, 0L);
        frameCount = 0;
        frameIndex = 0;
        timestamp = 0L;
        lookupMap.clear();
        amount = 0.0;
        amount2 = 0.0;
        amount3 = Double.MAX_VALUE;
        amount4 = 0.0;
    }

    public static RecordMetricHelper.Entry computeEntry(String str) {
        return lookupMap.get(str);
    }

    private static void process() {
        if (frameCount != 0) {
            long MAX_VALUE = Long.MAX_VALUE;
            long l = 0L;

            for (int i = 0; i < frameCount; i++) {
                long l2 = longValueArray[i];
                MAX_VALUE = Math.min(MAX_VALUE, l2);
                l = Math.max(l, l2);
            }

            amount2 = (double)timestamp / frameCount / 1000000.0;
            amount3 = MAX_VALUE / 1000000.0;
            amount4 = l / 1000000.0;
            amount = 1000.0 / amount2;
        }
    }

    public static double getAmount6() {
        return amount6;
    }

    public static double getAmount3() {
        return amount3;
    }

    public static double getAmount2() {
        return amount2;
    }

    public static double getAmount7() {
        return amount7;
    }

    public static double getAmount4() {
        return amount4;
    }

    public static class Entry {
        public double amount = 0.0;
        public double amount2 = 0.0;
        public double amount3 = Double.MAX_VALUE;
        public double amount4 = 0.0;
        public long timestamp = 0L;

        public void handleDouble(double amount) {
            this.amount = amount;
            this.amount3 = Math.min(this.amount3, amount);
            this.amount4 = Math.max(this.amount4, amount);
            this.timestamp++;
            this.amount2 = (this.amount2 * (this.timestamp - 1L) + amount) / this.timestamp;
        }
    }
}
