package org.brutality.utils.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ObserveHelper {
    public static final long timestamp = 2500L;
    public static final long timestamp2 = 1500L;
    private final Map<UUID, String> lookupMap = new HashMap<>();
    private final LinkedHashMap<UUID, ObserveHelper.Entry> linkedHashMap = new LinkedHashMap<>();
    private final Set<UUID> itemSet = new HashSet<>();
    private long timestamp3 = -1L;
    private long timestamp4 = -1L;

    public void observe(Map<UUID, String> map, long l) {
        int n = 0;

        for (UUID uuid : this.lookupMap.keySet()) {
            if (!map.containsKey(uuid)) {
                n++;
            }
        }

        if (!map.isEmpty() && (n < 8 || n * 5 < this.lookupMap.size() * 4)) {
            long l2 = (this.active() ? this.timestamp3 : l) - 2500L;
            this.linkedHashMap.entrySet().removeIf(entry -> entry.getValue().timestamp < l2 || map.containsKey(entry.getKey()));
            this.itemSet.removeIf(map::containsKey);

            for (Map.Entry entry2 : this.lookupMap.entrySet()) {
                if (!map.containsKey(entry2.getKey()) && !this.itemSet.contains(entry2.getKey())) {
                    this.linkedHashMap.put((UUID)entry2.getKey(), new ObserveHelper.Entry((String)entry2.getValue(), l));
                }
            }

            while (this.linkedHashMap.size() > 128) {
                this.linkedHashMap.remove(this.linkedHashMap.keySet().iterator().next());
            }

            this.lookupMap.clear();
            this.lookupMap.putAll(map);
        } else {
            this.handleMap(map);
        }
    }

    public void handleLong(long l) {
        if (!this.active()) {
            this.timestamp3 = l;
        }

        this.timestamp4 = Math.min(l + 1500L, this.timestamp3 + 5000L);
    }

    public boolean active() {
        return this.timestamp4 >= 0L;
    }

    public void handleMap(Map<UUID, String> map) {
        this.lookupMap.clear();
        this.lookupMap.putAll(map);
        this.linkedHashMap.clear();
        this.itemSet.clear();
        this.timestamp3 = this.timestamp4 = -1L;
    }

    public List<String> computeList() {
        ArrayList list = new ArrayList();
        if (!this.active()) {
            return list;
        }
        for (Map.Entry entry : this.linkedHashMap.entrySet()) {
            ObserveHelper.Entry val = (ObserveHelper.Entry)entry.getValue();
            if (val.timestamp >= this.timestamp3 - 2500L
                && val.timestamp <= this.timestamp4
                && !this.lookupMap.containsKey(entry.getKey())
                && this.itemSet.add((UUID)entry.getKey())) {
                list.add(val.text);
            }
        }

        this.linkedHashMap.clear();
        this.timestamp3 = this.timestamp4 = -1L;
        return list;
    }

    public boolean matchesLong(long l) {
        return this.active() && l >= this.timestamp4;
    }

    public static final class Entry {
        public final String text;
        public final long timestamp;

        public Entry(String text, long timestamp) {
            this.text = text;
            this.timestamp = timestamp;
        }
    }
}
