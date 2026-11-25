package org.brutality.module.impl.visual.notifications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public final class NotificationsHandler {
    private final Map<String, NotificationsHandler.EeEntry> lookupMap = new LinkedHashMap<>();
    private long timestamp = -1L;

    public void handleCollection(Collection<NotificationsHandler.EeEntry> eeEntries) {
        this.clear();
        this.lookupMap.putAll(computeMap(eeEntries));
    }

    private static Map<String, NotificationsHandler.EeEntry> computeMap(Collection<NotificationsHandler.EeEntry> eeEntries) {
        LinkedHashMap map = new LinkedHashMap();

        for (NotificationsHandler.EeEntry eeEntry : eeEntries) {
            map.put(eeEntry.ee().toLowerCase(Locale.ROOT), eeEntry);
        }

        return map;
    }

    public List<NotificationsHandler.L1l1Entry> computeList(Collection<NotificationsHandler.EeEntry> eeEntries, long l) {
        if (this.timestamp >= 0L && l < this.timestamp) {
            return List.of();
        }
        this.timestamp = -1L;
        var map = computeMap(eeEntries);
        ArrayList list = new ArrayList();

        for (Entry entry : this.lookupMap.entrySet()) {
            if (!map.containsKey(entry.getKey()) && ((NotificationsHandler.EeEntry)entry.getValue()).tracked()) {
                list.add(new NotificationsHandler.L1l1Entry((NotificationsHandler.EeEntry)entry.getValue(), false));
            }
        }

        for (Entry entry2 : map.entrySet()) {
            NotificationsHandler.EeEntry eeEntry = this.lookupMap.get(entry2.getKey());
            NotificationsHandler.EeEntry val = (NotificationsHandler.EeEntry)entry2.getValue();
            if (val.tracked() && (eeEntry == null || val.hj() && !eeEntry.hj() || val.hk() && !eeEntry.hk())) {
                list.add(new NotificationsHandler.L1l1Entry(val, true));
            }
        }

        this.lookupMap.clear();
        this.lookupMap.putAll(map);
        return List.copyOf(list);
    }

    public void handleLong(long l) {
        if (this.timestamp < 0L) {
            this.timestamp = l + 1000L;
        }
    }

    public void clear() {
        this.lookupMap.clear();
        this.timestamp = -1L;
    }

    public record EeEntry(String ee, boolean hj, boolean hk) {
        public boolean tracked() {
            return this.hj || this.hk;
        }
    }

    public record L1l1Entry(NotificationsHandler.EeEntry l1l1, boolean hi) {
    }
}
