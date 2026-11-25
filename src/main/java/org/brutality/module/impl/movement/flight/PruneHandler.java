package org.brutality.module.impl.movement.flight;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PruneHandler {
    private final Map<Integer, PruneHandler.Entry> lookupMap = new LinkedHashMap<>();
    public static final int count = 256;

    public boolean matchesInt(int n, long l) {
        PruneHandler.Entry entry = this.computeEntry(n, l);
        if (entry.timestamp4 >= 0L && l - entry.timestamp4 < 3000L) {
            return false;
        }
        if (l - entry.timestamp2 > 3000L || entry.count == 0) {
            entry.timestamp = l;
            entry.count = 0;
        }

        entry.timestamp2 = l;
        entry.count++;
        if (entry.count < 5 || l - entry.timestamp < 500L || entry.timestamp3 != 0L && l - entry.timestamp3 < 10000L) {
            return false;
        }
        entry.timestamp3 = l;
        return true;
    }

    public void handleInt(int n) {
        this.lookupMap.remove(n);
    }

    public void handleInt(int n, long l) {
        PruneHandler.Entry entry = this.computeEntry(n, l);
        entry.timestamp4 = l;
        entry.timestamp2 = l;
        entry.count = 0;
    }

    public void prune(long l) {
        this.lookupMap.values().removeIf(entry -> l - entry.timestamp2 > 15000L);
    }

    private PruneHandler.Entry computeEntry(int n, long l) {
        PruneHandler.Entry entry = this.lookupMap.get(n);
        if (entry != null) {
            return entry;
        }
        if (this.lookupMap.size() >= 256) {
            this.lookupMap.remove(this.lookupMap.keySet().iterator().next());
        }

        entry = new PruneHandler.Entry(l);
        this.lookupMap.put(n, entry);
        return entry;
    }

    public int size() {
        return this.lookupMap.size();
    }

    public void clear() {
        this.lookupMap.clear();
    }

    public static final class Entry {
        public long timestamp;
        public long timestamp2;
        public long timestamp3;
        public int count;
        public long timestamp4;
        private static final long timestamp5 = -1L;

        public Entry(long l) {
            this.timestamp4 = timestamp5;
            this.timestamp = this.timestamp2 = l;
        }
    }
}
