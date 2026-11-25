package org.brutality.utils.hypixel.pit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class EquipmentHelper {
    private static final long timestamp = 1500L;
    private final Map<UUID, EquipmentHelper.Entry> lookupMap = new HashMap<>();

    public void handleUUID(UUID uuid, int n, float f, double d, long l) {
        EquipmentHelper.Entry entry = this.equipment(uuid, n, l);
        if (entry.timestamp >= 0L && l - entry.timestamp <= 1500L) {
            if (f > 0.0F && f <= 4.0F) {
                entry.timestamp3 = l;
            }

            if (entry.timestamp2 < 0L || l - entry.timestamp2 > 1500L) {
                entry.timestamp2 = l;
                entry.amount = d;
            }
        }
    }

    public EquipmentHelper.Entry2 computeEntry2(UUID uuid, long l) {
        EquipmentHelper.Entry entry = this.lookupMap.get(uuid);
        return entry != null && entry.entry2 != null && entry.entry2.timestamp > l ? entry.entry2 : null;
    }

    public void handleUUID(UUID uuid) {
        EquipmentHelper.Entry entry = this.lookupMap.get(uuid);
        if (entry != null) {
            entry.entry2 = null;
            entry.timestamp2 = entry.timestamp3 = -1L;
        }
    }

    public void remove(UUID uuid) {
        this.lookupMap.remove(uuid);
    }

    public void clear() {
        this.lookupMap.clear();
    }

    public void handleSet(Set<UUID> uuids) {
        this.lookupMap.keySet().retainAll(uuids);
    }

    public void handleUUID(UUID uuid, int n, int n2, long l) {
        EquipmentHelper.Entry entry = this.lookupMap.get(uuid);
        if (entry != null && n >= 1 && n <= 3 && n2 > 0 && n2 <= 600) {
            int n3 = entry.entry2 != null && entry.entry2.timestamp > l
                ? entry.entry2.count
                : (entry.timestamp >= 0L && l - entry.timestamp <= 1500L ? entry.count : 0);
            if (n3 == n) {
                long l2 = l + n2 * 50L;
                if (entry.entry2 != null && entry.entry2.timestamp > l) {
                    l2 = Math.min(l2, entry.entry2.timestamp);
                }

                entry.entry2 = new EquipmentHelper.Entry2(n3, l2, false);
                entry.timestamp2 = -1L;
            }
        }
    }

    private EquipmentHelper.Entry equipment(UUID uuid, int n, long l) {
        EquipmentHelper.Entry entry = this.lookupMap.computeIfAbsent(uuid, uuid2 -> new EquipmentHelper.Entry());
        if (n >= 1 && n <= 3) {
            entry.count = n;
            entry.timestamp = l;
        }

        return entry;
    }

    public void handleUUID(UUID uuid, int n, float f, double d, long l, boolean flag) {
        EquipmentHelper.Entry entry = this.equipment(uuid, n, l);
        if (f > 0.0F && f <= 4.0F) {
            entry.timestamp3 = l;
        }

        if (entry.entry2 != null && entry.entry2.timestamp <= l) {
            entry.entry2 = null;
        }

        if (flag
            && entry.entry2 == null
            && entry.timestamp2 >= 0L
            && entry.timestamp3 >= 0L
            && l - entry.timestamp2 <= 1500L
            && entry.timestamp3 >= entry.timestamp2 - 150L
            && l - entry.timestamp3 <= 750L
            && l - entry.timestamp <= 1500L
            && entry.count >= 1
            && !(d - entry.amount < 1.5)) {
            entry.entry2 = new EquipmentHelper.Entry2(entry.count, entry.timestamp2 + 30000L, true);
            entry.timestamp2 = -1L;
        }
    }

    public static final class Entry {
        public int count;
        public long timestamp = -1L;
        public long timestamp2 = -1L;
        public long timestamp3 = -1L;
        public double amount;
        public EquipmentHelper.Entry2 entry2;
    }

    public static final class Entry2 {
        public final int count;
        public final long timestamp;
        public final boolean flag;

        public Entry2(int count, long timestamp, boolean flag) {
            this.count = count;
            this.timestamp = timestamp;
            this.flag = flag;
        }

        public int getCount() {
            return this.count + 1;
        }
    }
}
