package org.brutality.utils.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.module.Module;

public class PrintCopyPasteReportHelper {
    private static boolean enabled = false;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final StringBuilder stringBuilder = new StringBuilder();
    private static long timestamp = 0L;
    private static final long timestamp2 = 5000L;
    private static final Map<String, AtomicLong> lookupMap = new ConcurrentHashMap<>();
    private static final Map<String, AtomicLong> lookupMap2 = new ConcurrentHashMap<>();
    private static final Map<String, AtomicLong> lookupMap3 = new ConcurrentHashMap<>();
    private static final Map<String, AtomicLong> lookupMap4 = new ConcurrentHashMap<>();
    private static int count = 0;
    private static int count2 = 0;
    private static int particleCount = 0;
    private static long timestamp3 = 0L;
    private static long timestamp4 = 0L;
    private static long timestamp5 = 0L;
    private static long timestamp6 = 0L;
    private static int frameCount = 0;
    private static final int count3 = 60;
    private static final Queue<Long> queue = new LinkedList<>();
    private static final Map<String, Integer> lookupMap5 = new ConcurrentHashMap<>();
    private static final Map<String, Long> lookupMap6 = new HashMap<>();

    public static void handleString2(String str, Runnable task) {
        if (!enabled) {
            task.run();
        } else {
            handleString("event." + str);

            try {
                task.run();
            } finally {
                end("event." + str);
            }
        }
    }

    public static double computeDouble3() {
        return frameCount == 0 ? 0.0 : timestamp5 / frameCount / 1000000.0;
    }

    public static void handleModule(Module module, int n, Runnable task) {
        if (!enabled) {
            task.run();
        } else {
            String name = module.getName();
            handleString("entity." + name);

            try {
                trackEntityProcessing(name, n, 0L);
                task.run();
            } finally {
                end("entity." + name);
            }
        }
    }

    public static void printCopyPasteReport() {
        if (enabled) {
            StringBuilder sb = new StringBuilder();
            sb.append("```\n");
            sb.append("BRUTALITY PERFORMANCE DEBUG REPORT\n");
            sb.append("==================================\n\n");
            sb.append(String.format("FPS: %.1f (avg: %.2f ms/frame)\n", computeDouble(), computeDouble3()));
            sb.append(String.format("Players: %d visible, %d total\n", count2, count));
            sb.append(String.format("Particles: %d\n\n", particleCount));
            sb.append("TOP BOTTLENECKS:\n");
            List<Map.Entry<String, AtomicLong>> list = new ArrayList<>(lookupMap.entrySet());
            list.sort((entry, entry2) -> Long.compare(((AtomicLong)entry2.getValue()).get(), ((AtomicLong)entry.getValue()).get()));

            for (int i = 0; i < Math.min(10, list.size()); i++) {
                Map.Entry entry3 = (Map.Entry)list.get(i);
                double d = ((AtomicLong)entry3.getValue()).get() / 1000000.0;
                sb.append(String.format("  %d. %s: %.2f ms\n", i + 1, entry3.getKey(), d));
            }

            sb.append("\n```\n");
        }
    }

    public static double computeDouble2() {
        return queue.isEmpty() ? 0.0 : 1.0E9 / queue.peek().longValue();
    }

    public static int getCount2() {
        return count2;
    }

    public static void process() {
        if (enabled) {
            long now = System.currentTimeMillis();
            if (now - timestamp3 >= 500L) {
                count = 0;
                count2 = 0;
                particleCount = 0;
                if (mc.theWorld != null) {
                    for (Entity entity : mc.theWorld.loadedEntityList) {
                        if (entity instanceof EntityPlayer) {
                            count++;
                            double dist = mc.thePlayer.getDistanceToEntity(entity);
                            if (dist < 64.0) {
                                count2++;
                            }
                        }
                    }

                    if (mc.effectRenderer != null) {
                        particleCount = mc.effectRenderer.getParticleCount();
                    }
                }

                timestamp3 = now;
            }
        }
    }

    public static double computeDouble() {
        if (queue.isEmpty()) {
            return 0.0;
        }
        long l = 0L;

        for (long l2 : queue) {
            l += l2;
        }

        l /= queue.size();
        return 1.0E9 / l;
    }

    public static void process2() {
        long now = System.currentTimeMillis();
        if (now - timestamp >= 5000L) {
            printConsoleCompact();
            timestamp = now;
        }
    }

    public static String generateReport() {
        if (!enabled) {
            return "§cProfiler not enabled§r";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("§c§l═══ PERFORMANCE DEBUG REPORT §r§c§l══§r\n");
        double d = computeDouble();
        double d2 = computeDouble2();
        double d3 = computeDouble3();
        double d4 = computeDouble4();
        sb.append(String.format("§eFPS:§r §f%.1f§7 (curr: §f%.1f§7)§r\n", d, d2));
        sb.append(String.format("§eFrame Time:§r §f%.2fms§7 (worst: §f%.2fms§7)§r\n\n", d3, d4));
        sb.append(String.format("§ePlayers:§r §f%d§7 (§f%d visible§7)§r\n", count, count2));
        sb.append(String.format("§eParticles:§r §f%d§r\n\n", particleCount));
        List<Map.Entry<String, AtomicLong>> list = new ArrayList<>(lookupMap.entrySet());
        list.sort((entry, entry2) -> Long.compare(((AtomicLong)entry2.getValue()).get(), ((AtomicLong)entry.getValue()).get()));
        sb.append("§c§lPerformance Bottlenecks:§r\n");
        sb.append("§7─────────────────────────────────§r\n");
        long l = timestamp5 * Math.max(1L, frameCount / 60L);
        if (l == 0L) {
            l = 1L;
        }

        for (int i = 0; i < Math.min(12, list.size()); i++) {
            Map.Entry entry3 = (Map.Entry)list.get(i);
            String key = (String)entry3.getKey();
            long l2 = ((AtomicLong)entry3.getValue()).get();
            long l3 = lookupMap2.getOrDefault(key, new AtomicLong(0L)).get();
            long l4 = lookupMap3.getOrDefault(key, new AtomicLong(0L)).get();
            double d5 = l2 / 1000000.0;
            double d6 = l4 / 1000000.0;
            double d7 = l3 > 0L ? d5 / l3 : 0.0;
            String str = computeString(d5);
            String str2 = d6 > 5.0 ? " §c§l(!)§r" : "";
            String str3 = key.replace("module.", "§7").replace("render.", "§b").replace("entity.", "§a").replace("event.", "§e");
            sb.append(
                String.format(
                    "§7%d. %s%s:§r §f%.2fms§7 §8x§7%d§7 §8|§7 Avg: §f%.2fms§7 | Peak: §f%s%.2fms§r%s\n",
                    i + 1,
                    str,
                    str3,
                    d5,
                    l3 / Math.max(1, frameCount / 100),
                    d7,
                    d6 > 5.0 ? "§c" : "§7",
                    d6,
                    str2
                )
            );
        }

        if (!lookupMap5.isEmpty()) {
            sb.append("\n§c§lEntity Processing:§r\n");
            sb.append("§7─────────────────────────────────§r\n");

            for (Map.Entry entry4 : lookupMap5.entrySet()) {
                String key2 = (String)entry4.getKey();
                int val = (Integer)entry4.getValue();
                AtomicLong atomicLong = lookupMap4.get(key2);
                double d8 = atomicLong != null ? atomicLong.get() / 1000000.0 : 0.0;
                double d9 = val > 0 ? d8 / val : 0.0;
                sb.append(String.format("§7%s:§r §f%d entities§7, §f%.2fms total§7 (§f%.3fms/entity§7)§r\n", key2, val, d8, d9));
            }
        }

        return sb.toString();
    }

    public static int getCount() {
        return count;
    }

    public static void process3() {
        if (enabled) {
            timestamp4 = System.nanoTime();
        }
    }

    public static void process4() {
        if (enabled && timestamp4 != 0L) {
            long l = System.nanoTime() - timestamp4;
            timestamp5 += l;
            timestamp6 = Math.max(timestamp6, l);
            frameCount++;
            queue.offer(l);
            if (queue.size() > 60) {
                queue.poll();
            }

            timestamp4 = 0L;
        }
    }

    public static double computeDouble4() {
        return timestamp6 == 0L ? 0.0 : timestamp6 / 1000000.0;
    }

    public static void handleString(String str) {
        if (enabled) {
            long now = System.nanoTime();
            PrintCopyPasteReportHelper.Entry.handleString(str, now);
        }
    }

    public static void disable() {
        enabled = false;
    }

    public static long computeLong(String str) {
        return lookupMap6.getOrDefault("module." + str, 0L) / 1000000L;
    }

    public static void end(String str) {
        if (enabled) {
            long now = System.nanoTime();
            Long val = PrintCopyPasteReportHelper.Entry.computeLongType(str);
            if (val != null) {
                long l = now - val;
                lookupMap.computeIfAbsent(str, str2 -> new AtomicLong(0L)).addAndGet(l);
                lookupMap2.computeIfAbsent(str, str3 -> new AtomicLong(0L)).incrementAndGet();
                lookupMap3.computeIfAbsent(str, str4 -> new AtomicLong(0L)).updateAndGet(l2 -> Math.max(l2, l));
                if (str.startsWith("module.")) {
                    lookupMap6.put(str, l);
                }
            }
        }
    }

    public static void printConsoleCompact() {
        if (enabled) {
            double d = computeDouble();
            double d2 = computeDouble3();
            int n = getCount2();
            int particleCount = getParticleCount();
            List<Map.Entry<String, AtomicLong>> list = new ArrayList<>(lookupMap.entrySet());
            list.sort((entry, entry2) -> Long.compare(((AtomicLong)entry2.getValue()).get(), ((AtomicLong)entry.getValue()).get()));
            String str = "None";
            double d3 = 0.0;
            if (!list.isEmpty()) {
                Map.Entry entry3 = (Map.Entry)list.get(0);
                str = (String)entry3.getKey();
                d3 = ((AtomicLong)entry3.getValue()).get() / 1000000.0;
            }
        }
    }

    public static List<String> getTopBottlenecks(int n) {
        List<Map.Entry<String, AtomicLong>> list = new ArrayList<>(lookupMap.entrySet());
        list.sort((entry, entry2) -> Long.compare(((AtomicLong)entry2.getValue()).get(), ((AtomicLong)entry.getValue()).get()));
        List list2 = new ArrayList();

        for (int i = 0; i < Math.min(n, list.size()); i++) {
            Map.Entry entry3 = (Map.Entry)list.get(i);
            double d = ((AtomicLong)entry3.getValue()).get() / 1000000.0;
            long l = lookupMap2.getOrDefault(entry3.getKey(), new AtomicLong(0L)).get();
            String str = ((String)entry3.getKey()).replace("module.", "").replace("event.", "evt:").replace("render.", "rnd:").replace("entity.", "ent:");
            list2.add(String.format("%s %.2fms x%d", str, d, l));
        }

        return list2;
    }

    private static String computeString(double d) {
        if (d >= 10.0) {
            return "§4";
        }
        if (d >= 5.0) {
            return "§c";
        }
        if (d >= 2.0) {
            return "§6";
        }
        return d >= 1.0 ? "§e" : "§a";
    }

    public static int getParticleCount() {
        return particleCount;
    }

    private static String computeString(double d, double d2) {
        if (d2 > 10.0) {
            return "!!!";
        }
        if (d2 > 5.0) {
            return "!!";
        }
        if (d > 5.0) {
            return "!";
        }
        return d > 2.0 ? "~" : "";
    }

    public static void printConsoleReport() {
        if (enabled) {
            double d = computeDouble();
            double d2 = computeDouble2();
            double d3 = computeDouble3();
            double d4 = computeDouble4();
            if (count2 > 10) {
            }

            if (particleCount > 100) {
            }

            List<Map.Entry<String, AtomicLong>> list = new ArrayList<>(lookupMap.entrySet());
            list.sort((entry, entry2) -> Long.compare(((AtomicLong)entry2.getValue()).get(), ((AtomicLong)entry.getValue()).get()));
            long l = timestamp5 * Math.max(1L, frameCount / 60L);
            if (l == 0L) {
                l = 1L;
            }

            for (int i = 0; i < Math.min(15, list.size()); i++) {
                Map.Entry entry3 = (Map.Entry)list.get(i);
                String key = (String)entry3.getKey();
                long l2 = ((AtomicLong)entry3.getValue()).get();
                long l3 = lookupMap2.getOrDefault(key, new AtomicLong(0L)).get();
                long l4 = lookupMap3.getOrDefault(key, new AtomicLong(0L)).get();
                double d5 = l2 / 1000000.0;
                double d6 = l4 / 1000000.0;
                if (l3 > 0L) {
                    double d7 = d5 / l3;
                } else {
                }

                String str = computeString(d5, d6);
            }

            if (!lookupMap5.isEmpty()) {
                for (Map.Entry entry4 : lookupMap5.entrySet()) {
                    String key2 = (String)entry4.getKey();
                    int val = (Integer)entry4.getValue();
                    AtomicLong atomicLong = lookupMap4.get(key2);
                    double d9 = atomicLong != null ? atomicLong.get() / 1000000.0 : 0.0;
                    if (val > 0) {
                        double d10 = d9 / val;
                    } else {
                    }
                }
            }
        }
    }

    public static String generateCompactReport() {
        if (!enabled) {
            return "§cProfiler off§r";
        }
        double d = computeDouble();
        double d2 = computeDouble2();
        double d3 = computeDouble3();
        StringBuilder sb = new StringBuilder();
        String str = d >= 60.0 ? "§a" : (d >= 30.0 ? "§e" : "§c");
        sb.append(String.format("§6Perf§7: %s%.0f FPS §7(§f%.1fms§7)§r", str, d, d3));
        if (count2 > 5) {
            sb.append(String.format(" §7P:§f%d", count2));
        }

        if (particleCount > 50) {
            sb.append(String.format(" §7Pt:§f%d", particleCount));
        }

        List<Map.Entry<String, AtomicLong>> list = new ArrayList<>(lookupMap.entrySet());
        list.sort((entry, entry2) -> Long.compare(((AtomicLong)entry2.getValue()).get(), ((AtomicLong)entry.getValue()).get()));
        if (!list.isEmpty()) {
            Map.Entry entry3 = (Map.Entry)list.get(0);
            String key = (String)entry3.getKey();
            double d4 = ((AtomicLong)entry3.getValue()).get() / 1000000.0;
            if (d4 > 0.5) {
                String str2 = computeString(d4);
                String str3 = key.replace("module.", "").replace("render.", "").replace("entity.", "").replace("event.", "");
                sb.append(String.format(" §7%s:§r%s%.1fms", str3.substring(0, Math.min(8, str3.length())), str2, d4));
            }
        }

        return sb.toString();
    }

    public static void handleString(String str, Runnable task) {
        if (!enabled) {
            task.run();
        } else {
            handleString("render." + str);

            try {
                task.run();
            } finally {
                end("render." + str);
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void trackEntityProcessing(String str, int n, long l) {
        if (enabled) {
            lookupMap4.computeIfAbsent(str, str2 -> new AtomicLong(0L)).addAndGet(l);
            lookupMap5.put(str, n);
        }
    }

    public static void reset() {
        lookupMap.clear();
        lookupMap2.clear();
        lookupMap3.clear();
        lookupMap4.clear();
        queue.clear();
        frameCount = 0;
        timestamp5 = 0L;
        timestamp6 = 0L;
        lookupMap5.clear();
        lookupMap6.clear();
    }

    public static void enable() {
        enabled = true;
        reset();
    }

    public static class Entry {
        private static final ThreadLocal<Map<String, Long>> threadLocal = ThreadLocal.withInitial(HashMap::new);

        private Entry() {
        }

        public static Long computeLongType(String str) {
            return threadLocal.get().remove(str);
        }

        public static void handleString(String str, long l) {
            threadLocal.get().put(str, l);
        }
    }
}
