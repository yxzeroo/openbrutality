package org.brutality.utils.hypixel.pit;

import java.util.HashMap;
import java.util.Map;

public final class AttackHelper {
    private static final long timestamp = 1000L;
    private final Map<String, AttackHelper.Entry> lookupMap = new HashMap<>();
    private String text;
    private long lastHit;
    private long timestamp2;
    private long timestamp3;
    private long timestamp4 = -1L;
    private long timestamp5;
    private long timestamp6;
    private long timestamp7;
    private String text2;

    private boolean matchesString(String text, AttackHelper.Entry entry, long lastHit) {
        this.lookupMap.remove(text);
        if (entry.timestamp >= this.timestamp3) {
            this.text = text;
            this.lastHit = lastHit;
            this.timestamp3 = entry.timestamp;
        }

        if (this.timestamp4 >= 0L && entry.timestamp <= this.timestamp5 && entry.timestamp >= this.timestamp6) {
            this.text2 = text;
            this.timestamp7 = lastHit;
            this.timestamp6 = entry.timestamp;
        }

        return true;
    }

    public void attack(String str, int n, long l) {
        this.lookupMap.entrySet().removeIf(entry -> l - entry.getValue().time > 1000L);
        AttackHelper.Entry entry2 = this.lookupMap.get(str);
        if (entry2 == null) {
            this.lookupMap.put(str, new AttackHelper.Entry(l, n));
        } else {
            entry2.time = l;
        }

        this.lookupMap.get(str).timestamp = ++this.timestamp2;
    }

    public void clear() {
        this.lookupMap.clear();
        this.text = this.text2 = null;
        this.lastHit = this.timestamp7 = 0L;
        this.timestamp4 = -1L;
        this.timestamp2 = this.timestamp3 = this.timestamp5 = this.timestamp6 = 0L;
    }

    public boolean matchesString(String str, long l) {
        AttackHelper.Entry entry = this.lookupMap.get(str);
        if (entry == null) {
            return false;
        }
        if (l - entry.time > 1000L) {
            this.lookupMap.remove(str);
            return false;
        }
        return this.matchesString(str, entry, l);
    }

    public String computeString(int n, int n2, long l) {
        if (PitHelper2.matchesInt(n, n2)) {
            this.timestamp4 = l;
            this.timestamp5 = this.timestamp2;
            this.text2 = this.text;
            this.timestamp7 = this.lastHit;
            this.timestamp6 = this.timestamp3;
        }

        if (this.timestamp4 >= 0L && l - this.timestamp4 <= 750L && this.text2 != null && l - this.timestamp7 <= 1000L) {
            for (AttackHelper.Entry entry : this.lookupMap.values()) {
                if (entry.timestamp > this.timestamp6 && entry.timestamp <= this.timestamp5 && l - entry.time <= 1000L) {
                    return null;
                }
            }

            String str = this.text2;
            this.timestamp4 = -1L;
            this.text2 = null;
            if (this.timestamp3 <= this.timestamp5) {
                this.text = null;
            }

            return str;
        }
        return null;
    }

    public boolean matchesString(String str, int n, long l) {
        AttackHelper.Entry entry = this.lookupMap.get(str);
        if (entry == null) {
            return false;
        }
        if (l - entry.time > 1000L) {
            this.lookupMap.remove(str);
            return false;
        }
        boolean flag = n >= 9 && n > entry.count;
        entry.count = n;
        return !flag ? false : this.matchesString(str, entry, l);
    }

    public static final class Entry {
        public long time;
        public int count;
        public long timestamp;

        public Entry(long time, int count) {
            this.time = time;
            this.count = count;
        }
    }
}
