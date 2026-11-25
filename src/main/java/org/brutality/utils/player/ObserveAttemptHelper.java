package org.brutality.utils.player;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ObserveAttemptHelper {
    public static final ObserveAttemptHelper instance = new ObserveAttemptHelper();
    private final Map<UUID, ObserveAttemptHelper.Entry2> lookupMap = new HashMap<>();
    private final Map<UUID, ObserveAttemptHelper.Entry> lookupMap2 = new HashMap<>();
    private WeakReference<Object> weakReference = new WeakReference<>(null);

    public long computeLong2(Object obj, UUID uuid, long l) {
        this.handleObject3(obj);
        ObserveAttemptHelper.Entry2 entry2 = this.lookupMap.get(uuid);
        return entry2 != null && entry2.timestamp3 > l ? entry2.timestamp3 : 0L;
    }

    public long computeLong(Object obj, UUID uuid, long l, long l2, long l3) {
        return this.shot(obj, uuid, l, l2, l3).timestamp3;
    }

    private ObserveAttemptHelper.Entry2 shot(Object obj, UUID uuid, long l, long l2, long l3) {
        this.handleObject3(obj);
        this.lookupMap.entrySet().removeIf(entry -> entry.getValue().timestamp3 <= l2);
        ObserveAttemptHelper.Entry2 entry2 = this.lookupMap.get(uuid);
        if (entry2 == null) {
            entry2 = new ObserveAttemptHelper.Entry2(l, l2, l2 + l3);
            this.lookupMap.put(uuid, entry2);
        }

        return entry2;
    }

    private void handleObject3(Object obj) {
        if (this.weakReference.get() != obj) {
            this.lookupMap2.clear();
            this.weakReference = new WeakReference<>(obj);
        }
    }

    public void handleUUID2(UUID uuid) {
        this.lookupMap2.remove(uuid);
    }

    public boolean matchesObject(Object obj, UUID uuid, long l, long l2, double d, double d2, double d3, double d4, double d5, double d6) {
        this.handleObject3(obj);
        ObserveAttemptHelper.Entry entry = this.lookupMap2.get(uuid);
        if (entry == null) {
            return false;
        }
        if (l2 >= entry.timestamp && l2 - entry.timestamp <= 5000L) {
            double d7 = d4 - d;
            double d8 = d5 - d2;
            double d9 = d6 - d3;
            double d10 = d4 - entry.amount;
            double d11 = d5 - entry.amount2;
            double d12 = d6 - entry.amount3;
            if (!(d7 * d7 + d8 * d8 + d9 * d9 < 4.0) && !(d10 * d10 + d11 * d11 + d12 * d12 > 20.25)) {
                this.lookupMap2.remove(uuid);
                ObserveAttemptHelper.Entry2 entry2 = new ObserveAttemptHelper.Entry2(l, l2, l2 + entry.timestamp2);
                entry2.flag = true;
                this.lookupMap.put(uuid, entry2);
                return true;
            }
            return false;
        }
        this.lookupMap2.remove(uuid);
        return false;
    }

    public boolean matchesObject(Object obj, UUID uuid, long l) {
        if (this.matchesObject2(obj, uuid, l) && this.computeLong2(obj, uuid, l) <= l) {
            ObserveAttemptHelper.Entry entry = this.lookupMap2.get(uuid);
            if (!entry.flag && l - entry.timestamp <= 250L) {
                entry.flag = true;
                return true;
            }
            return false;
        }
        return false;
    }

    public long computeLong3(Object obj, UUID uuid, long l) {
        this.handleObject3(obj);
        ObserveAttemptHelper.Entry2 entry2 = this.lookupMap.get(uuid);
        return entry2 != null && entry2.timestamp3 > l && entry2.count > 0 ? entry2.timestamp5 : 0L;
    }

    public void handleObject(Object obj) {
        this.handleObject3(obj);
        this.lookupMap.clear();
        this.lookupMap2.clear();
    }

    public void handleUUID(UUID uuid) {
        this.lookupMap.remove(uuid);
        this.lookupMap2.remove(uuid);
    }

    public void handleObject2(Object obj) {
        this.handleObject3(obj);
        this.lookupMap2.clear();
    }

    public boolean matchesObject(Object obj, UUID uuid, long l, long l2) {
        if (this.weakReference.get() != obj) {
            return false;
        }
        ObserveAttemptHelper.Entry2 entry2 = this.lookupMap.get(uuid);
        if (entry2 != null && entry2.timestamp4 > l && l2 > 0L) {
            entry2.timestamp4 = Math.max(l, entry2.timestamp4 - l2);
            return true;
        }
        return false;
    }

    public boolean matchesObject2(Object obj, UUID uuid, long l, long l2) {
        if (this.weakReference.get() != obj) {
            return false;
        }
        ObserveAttemptHelper.Entry2 entry2 = this.lookupMap.get(uuid);
        if (entry2 != null && entry2.timestamp3 > l && entry2.timestamp5 > l && l2 > 0L) {
            entry2.timestamp5 = Math.max(l, entry2.timestamp5 - l2);
            entry2.count++;
            return true;
        }
        return false;
    }

    public boolean matchesObject2(Object obj, UUID uuid, long l) {
        if (this.weakReference.get() != obj) {
            return false;
        }
        ObserveAttemptHelper.Entry entry = this.lookupMap2.get(uuid);
        return entry != null && l >= entry.timestamp && l - entry.timestamp <= 5000L;
    }

    public long computeLong(Object obj, UUID uuid, long l) {
        this.handleObject3(obj);
        ObserveAttemptHelper.Entry2 entry2 = this.lookupMap.get(uuid);
        return entry2 != null && entry2.timestamp4 > l ? entry2.timestamp4 : 0L;
    }

    public void observeAttempt(Object obj, UUID uuid, long l, long l2, long l3, double d, double d2, double d3) {
        this.handleObject3(obj);
        this.lookupMap2.entrySet().removeIf(entry -> l2 - entry.getValue().timestamp > 5000L);
        ObserveAttemptHelper.Entry2 entry2 = this.lookupMap.get(uuid);
        if (entry2 == null || !entry2.flag || l2 - entry2.timestamp2 > 250L) {
            ObserveAttemptHelper.Entry entry3 = this.lookupMap2.get(uuid);
            if (entry3 == null || l2 - entry3.timestamp > 250L) {
                this.lookupMap2.put(uuid, new ObserveAttemptHelper.Entry(l2, l3, d, d2, d3));
            }
        }
    }

    public static final class Entry {
        public final long timestamp;
        public final long timestamp2;
        public final double amount;
        public final double amount2;
        public final double amount3;
        public boolean flag;

        public Entry(long timestamp, long l, double amount, double amount2, double amount3) {
            this.timestamp = timestamp;
            this.timestamp2 = l;
            this.amount = amount;
            this.amount2 = amount2;
            this.amount3 = amount3;
        }
    }

    public static final class Entry2 {
        public final long timestamp;
        public final long timestamp2;
        public final long timestamp3;
        public long timestamp4;
        public long timestamp5;
        public int count;
        public boolean flag;

        public Entry2(long timestamp, long l, long l2) {
            this.timestamp = timestamp;
            this.timestamp2 = l;
            this.timestamp3 = l2;
            this.timestamp4 = l2;
            this.timestamp5 = l2;
        }
    }
}
