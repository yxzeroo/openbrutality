package org.brutality.module.impl.visual.notifications;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class NotificationsHandler2 {
    private final Map<String, NotificationsHandler2.Entry> lookupMap = new HashMap<>();

    public boolean matchesString(String str, Object obj, boolean flag, boolean flag2, boolean flag3, double d) {
        String str2 = str.toLowerCase(Locale.ROOT);
        if (flag3 && obj != null) {
            NotificationsHandler2.Entry entry = this.lookupMap.get(str2);
            if (entry == null || entry.object != obj) {
                entry = new NotificationsHandler2.Entry(obj);
                this.lookupMap.put(str2, entry);
            }

            if (flag) {
                if (flag2) {
                    entry.amount = entry.flag ? Math.min(entry.amount, d) : d;
                    entry.flag = true;
                }

                return false;
            }
            if (entry.flag && d <= entry.amount + 0.25) {
                entry.flag = false;
                return true;
            }
            return false;
        }
        this.lookupMap.remove(str2);
        return false;
    }

    public void handleString(String str) {
        this.lookupMap.remove(str.toLowerCase(Locale.ROOT));
    }

    public void clear() {
        this.lookupMap.clear();
    }

    public static final class Entry {
        public final Object object;
        public boolean flag;
        public double amount;

        public Entry(Object object) {
            this.object = object;
        }
    }
}
