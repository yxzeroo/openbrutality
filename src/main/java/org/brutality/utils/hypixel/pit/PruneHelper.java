package org.brutality.utils.hypixel.pit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class PruneHelper {
    private final Map<UUID, PruneHelper.Entry> lookupMap = new HashMap<>();
    private long timestamp = Long.MIN_VALUE;

    public boolean hit(UUID uuid, long timestamp, long l) {
        if (this.timestamp != Long.MIN_VALUE && timestamp - this.timestamp < 250L) {
            return false;
        }
        this.timestamp = timestamp;
        PruneHelper.Entry entry = this.lookupMap.computeIfAbsent(uuid, uuid2 -> new PruneHelper.Entry());
        if (timestamp - entry.timestamp > l) {
            entry.count = 0;
        }

        entry.timestamp = timestamp;
        return ++entry.count >= 2;
    }

    public void prune(Set<UUID> uuids, long l, long l2) {
        this.lookupMap.entrySet().removeIf(entry -> !uuids.contains(entry.getKey()) || l - entry.getValue().timestamp > l2);
    }

    public void clear() {
        this.lookupMap.clear();
        this.timestamp = Long.MIN_VALUE;
    }

    public static final class Entry {
        public int count;
        public long timestamp;
    }
}
