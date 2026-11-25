package org.brutality.utils.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PollHelper {
    public static final long timestamp = 350L;
    private final Map<Integer, PollHelper.Entry> lookupMap = new LinkedHashMap<>();
    private final Map<UUID, Long> lookupMap2 = new HashMap<>();

    public void handleInt(int n, UUID uuid, boolean flag, long l) {
        if (uuid != null && !this.lookupMap.containsKey(n)) {
            if (this.lookupMap.size() >= 256) {
                this.lookupMap.remove(this.lookupMap.keySet().iterator().next());
            }

            this.lookupMap.put(n, new PollHelper.Entry(uuid, flag, l));
        }
    }

    public void clear() {
        this.lookupMap.clear();
        this.lookupMap2.clear();
    }

    public List<PollHelper.Entry2> poll(long l) {
        this.lookupMap
            .entrySet()
            .removeIf(entry -> l - entry.getValue().timestamp > 30000L || entry.getValue().timestamp3 >= 0L && l - entry.getValue().timestamp3 > 700L);
        this.lookupMap2.entrySet().removeIf(entry2 -> l - entry2.getValue() > 350L);
        ArrayList list = new ArrayList();
        ArrayList list2 = new ArrayList();

        for (Map.Entry entry3 : this.lookupMap2.entrySet()) {
            PollHelper.Entry entry4 = null;
            int n = 0;

            for (PollHelper.Entry entry5 : this.lookupMap.values()) {
                if (!entry5.consumed && ((UUID)entry3.getKey()).equals(entry5.uuid2) && Math.abs((Long)entry3.getValue() - entry5.timestamp2) <= 350L) {
                    entry4 = entry5;
                    n++;
                }
            }

            if (n == 1
                && entry4.flag
                && !entry4.uuid.equals(entry3.getKey())
                && entry4.timestamp3 >= 0L
                && Math.abs(entry4.timestamp3 - (Long)entry3.getValue()) <= 350L) {
                entry4.consumed = true;
                list.add(new PollHelper.Entry2(entry4.uuid, (UUID)entry3.getKey()));
                list2.add((UUID)entry3.getKey());
            }
        }

        list2.forEach(this.lookupMap2::remove);
        return list;
    }

    public void handleInt(int n, long l) {
        PollHelper.Entry entry = this.lookupMap.get(n);
        if (entry != null && entry.timestamp3 < 0L) {
            entry.timestamp3 = l;
        }
    }

    public void handleInt(int n, UUID uuid, long l) {
        PollHelper.Entry entry = this.lookupMap.get(n);
        if (entry != null && !entry.consumed && entry.timestamp3 < 0L) {
            entry.uuid2 = uuid;
            entry.timestamp2 = l;
        }
    }

    public void handleUUID(UUID uuid, long l) {
        this.lookupMap2.put(uuid, l);
    }

    public static final class Entry {
        public final UUID uuid;
        public final boolean flag;
        public final long timestamp;
        public UUID uuid2;
        public long timestamp2;
        public long timestamp3;
        public boolean consumed;
        private static final long timestamp4 = -1L;

        public Entry(UUID uuid, boolean flag, long timestamp) {
            this.timestamp3 = timestamp4;
            this.uuid = uuid;
            this.flag = flag;
            this.timestamp = timestamp;
        }
    }

    public static final class Entry2 {
        public final UUID uuid;
        public final UUID uuid2;

        public Entry2(UUID uuid, UUID uuid2) {
            this.uuid = uuid;
            this.uuid2 = uuid2;
        }
    }
}
