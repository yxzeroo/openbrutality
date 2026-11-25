package org.brutality.utils.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.brutality.module.Module;

public class GetTopSlowestHelper {
    private static final Map<String, Entry2> lookupMap = new ConcurrentHashMap<String, Entry2>();
    private static final Map<String, Long> lookupMap2 = new ConcurrentHashMap<String, Long>();
    private static boolean enabled = true;
    public static long timestamp = System.currentTimeMillis();

    public static String computeString(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\u00a7l\u00a7nPerformance Profile\u00a7r\n\n");
        List<Entry2> list = GetTopSlowestHelper.getTopSlowest(n);
        stringBuilder.append("\u00a7eTop ").append(n).append(" Slowest:\u00a7r\n");
        for (int i = 0; i < list.size(); ++i) {
            Entry2 entry2 = list.get(i);
            String string = i == 0 ? "\u00a7c" : (i < 3 ? "\u00a76" : "\u00a7e");
            stringBuilder.append(String.format("%s%d. %s: %.2fms (%.3fms avg)\u00a7r\n", string, i + 1, entry2.text, entry2.getTotalTime(), entry2.getAmount()));
        }
        return stringBuilder.toString();
    }

    public static Entry2 computeEntry2(String string) {
        return lookupMap.get(string);
    }

    public static void handleModule2(Module module) {
        if (!enabled || module == null) {
            return;
        }
        GetTopSlowestHelper.handleString(module.getName());
    }

    public static void handleModule2(Module module, String string) {
        if (!enabled || module == null) {
            return;
        }
        GetTopSlowestHelper.handleString(module.getName() + "." + string);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static List<Entry2> getHighestAverage(int n) {
        ArrayList<Entry2> arrayList = new ArrayList<Entry2>(lookupMap.values());
        arrayList.sort((entry2, entry22) -> Double.compare(entry22.amount, entry2.amount));
        return arrayList.subList(0, Math.min(n, arrayList.size()));
    }

    public static void handleString2(String string) {
        if (!enabled) {
            return;
        }
        Long l = lookupMap2.remove(string);
        if (l == null) {
            return;
        }
        long l2 = System.nanoTime() - l;
        Entry2 entry2 = lookupMap.computeIfAbsent(string, Entry2::new);
        entry2.handleLong(l2);
    }

    public static void handleModule(Module module) {
        if (!enabled || module == null) {
            return;
        }
        GetTopSlowestHelper.handleString2(module.getName());
    }

    public static List<Entry2> getHighestFrequency(int n) {
        ArrayList<Entry2> arrayList = new ArrayList<Entry2>(lookupMap.values());
        arrayList.sort((entry2, entry22) -> Long.compare(entry22.timestamp, entry2.timestamp));
        return arrayList.subList(0, Math.min(n, arrayList.size()));
    }

    public static void reset() {
        lookupMap.clear();
        lookupMap2.clear();
        timestamp = System.currentTimeMillis();
    }

    private static String computeString(String string, int n) {
        if (string.length() <= n) {
            return string;
        }
        return string.substring(0, n - 3) + "...";
    }

    public static void handleModule(Module module, String string, Runnable runnable) {
        if (!enabled || module == null) {
            runnable.run();
            return;
        }
        GetTopSlowestHelper.handleModule2(module, string);
        try {
            runnable.run();
        }
        finally {
            GetTopSlowestHelper.handleModule(module, string);
        }
    }

    public static void handleModule(Module module, String string) {
        if (!enabled || module == null) {
            return;
        }
        String string2 = module.getName() + "." + string;
        Long l = lookupMap2.remove(string2);
        if (l == null) {
            return;
        }
        long l2 = System.nanoTime() - l;
        Entry2 entry2 = lookupMap.computeIfAbsent(module.getName(), Entry2::new);
        Entry entry = entry2.lookupMap.computeIfAbsent(string, Entry::new);
        entry.handleLong(l2);
    }

    public static Map<String, Entry2> computeMap() {
        return new HashMap<String, Entry2>(lookupMap);
    }

    public static String generateReport() {
        Object object;
        StringBuilder stringBuilder = new StringBuilder();
        long l = System.currentTimeMillis() - timestamp;
        double d = (double)l / 1000.0;
        stringBuilder.append("\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557\n");
        stringBuilder.append("\u2551              MODULE PERFORMANCE PROFILER REPORT                \u2551\n");
        stringBuilder.append("\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d\n\n");
        stringBuilder.append(String.format("Profiling Duration: %.2f seconds\n", d));
        stringBuilder.append(String.format("Total Modules Tracked: %d\n\n", lookupMap.size()));
        stringBuilder.append("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n");
        stringBuilder.append("TOP 10 SLOWEST MODULES (Total Time)\n");
        stringBuilder.append("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n\n");
        List<Entry2> list2 = GetTopSlowestHelper.getTopSlowest(10);
        stringBuilder.append(String.format("%-20s %10s %10s %10s %10s %10s\n", "Module", "Total(ms)", "Calls", "Avg(ms)", "Min(ms)", "Max(ms)"));
        stringBuilder.append("\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\n");
        for (Entry2 list3 : list2) {
            stringBuilder.append(String.format("%-20s %10.3f %10d %10.3f %10.3f %10.3f\n", GetTopSlowestHelper.computeString(list3.text, 20), list3.getTotalTime(), list3.timestamp, list3.getAmount(), list3.getTimestamp2(), list3.getMaxTime()));
        }
        stringBuilder.append("\n\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n");
        stringBuilder.append("TOP 10 MOST FREQUENTLY CALLED\n");
        stringBuilder.append("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n\n");
        List<Entry2> list4 = GetTopSlowestHelper.getHighestFrequency(10);
        stringBuilder.append(String.format("%-20s %10s %10s %15s\n", "Module", "Calls", "Total(ms)", "Calls/Second"));
        stringBuilder.append("\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\n");
        Iterator iterator = list4.iterator();
        while (iterator.hasNext()) {
            object = (Entry2)iterator.next();
            stringBuilder.append(String.format("%-20s %10d %10.3f %15.2f\n", GetTopSlowestHelper.computeString(((Entry2)object).text, 20), ((Entry2)object).timestamp, ((Entry2)object).getTotalTime(), ((Entry2)object).computeDouble()));
        }
        stringBuilder.append("\n\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n");
        stringBuilder.append("TOP 10 HIGHEST AVERAGE TIME\n");
        stringBuilder.append("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n\n");
        List<Entry2> list = GetTopSlowestHelper.getHighestAverage(10);
        stringBuilder.append(String.format("%-20s %10s %10s %10s\n", "Module", "Avg(ms)", "Calls", "Total(ms)"));
        stringBuilder.append("\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\n");
        for (Entry2 entry22 : list) {
            stringBuilder.append(String.format("%-20s %10.3f %10d %10.3f\n", GetTopSlowestHelper.computeString(entry22.text, 20), entry22.getAmount(), entry22.timestamp, entry22.getTotalTime()));
        }
        if (!list2.isEmpty()) {
            object = list2.get(0);
            if (!((Entry2)object).lookupMap.isEmpty()) {
                stringBuilder.append("\n\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n");
                stringBuilder.append(String.format("METHOD BREAKDOWN: %s\n", ((Entry2)object).text));
                stringBuilder.append("\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n\n");
                ArrayList<Entry> arrayList = new ArrayList<Entry>(((Entry2)object).lookupMap.values());
                arrayList.sort((entry, entry2) -> Long.compare(entry2.totalTime, entry.totalTime));
                stringBuilder.append(String.format("%-25s %10s %10s %10s\n", "Method", "Total(ms)", "Calls", "Avg(ms)"));
                stringBuilder.append("\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\n");
                for (Entry entry3 : arrayList) {
                    stringBuilder.append(String.format("%-25s %10.3f %10d %10.3f\n", GetTopSlowestHelper.computeString(entry3.text, 25), entry3.getTotalTime(), entry3.timestamp, entry3.getAmount()));
                }
            }
        }
        return stringBuilder.toString();
    }

    public static List<Entry2> getTopSlowest(int n) {
        ArrayList<Entry2> arrayList = new ArrayList<Entry2>(lookupMap.values());
        arrayList.sort((entry2, entry22) -> Long.compare(entry22.totalTime, entry2.totalTime));
        return arrayList.subList(0, Math.min(n, arrayList.size()));
    }

    public static void setEnabled(boolean bl) {
        enabled = bl;
        if (!bl) {
            lookupMap2.clear();
        }
    }

    public static void handleString(String string) {
        if (!enabled) {
            return;
        }
        lookupMap2.put(string, System.nanoTime());
    }

    public static class Entry2 {
        public String text;
        public long totalTime;
        public long timestamp;
        public long timestamp2;
        public long maxTime;
        public double amount;
        public Map<String, Entry> lookupMap;
        private static final long timestamp3 = Long.MAX_VALUE;

        public double getTimestamp2() {
            return (double)this.timestamp2 / 1000000.0;
        }

        public double getTotalTime() {
            return (double)this.totalTime / 1000000.0;
        }

        public double getMaxTime() {
            return (double)this.maxTime / 1000000.0;
        }

        public double getAmount() {
            return this.amount / 1000000.0;
        }

        public Entry2(String string) {
            this.text = string;
            this.totalTime = 0L;
            this.timestamp = 0L;
            this.timestamp2 = timestamp3;
            this.maxTime = 0L;
            this.amount = 0.0;
            this.lookupMap = new ConcurrentHashMap<String, Entry>();
        }

        public void handleLong(long l) {
            this.totalTime += l;
            ++this.timestamp;
            this.timestamp2 = Math.min(this.timestamp2, l);
            this.maxTime = Math.max(this.maxTime, l);
            this.amount = (double)this.totalTime / (double)this.timestamp;
        }

        public double computeDouble() {
            long l = System.currentTimeMillis() - timestamp;
            return (double)this.timestamp * 1000.0 / (double)Math.max(1L, l);
        }
    }

    public static class Entry {
        public String text;
        public long totalTime;
        public long timestamp;
        public double amount;

        public double getAmount() {
            return this.amount / 1000000.0;
        }

        public double getTotalTime() {
            return (double)this.totalTime / 1000000.0;
        }

        public void handleLong(long l) {
            this.totalTime += l;
            ++this.timestamp;
            this.amount = (double)this.totalTime / (double)this.timestamp;
        }

        public Entry(String string) {
            this.text = string;
            this.totalTime = 0L;
            this.timestamp = 0L;
            this.amount = 0.0;
        }
    }
}
