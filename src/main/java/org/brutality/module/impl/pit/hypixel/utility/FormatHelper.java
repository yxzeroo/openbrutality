package org.brutality.module.impl.pit.hypixel.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

public final class FormatHelper {
    public static String format(String str, Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            if ("Names".equals(str)) {
                return computeString(new TreeSet<>(map.keySet()));
            }
            if (!"Kits".equals(str)) {
                return map.size() + " people";
            }
            TreeMap<String, Integer> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            for (String str2 : map.values()) {
                treeMap.merge(str2, 1, Integer::sum);
            }

            if (treeMap.size() == 1) {
                Entry<String, Integer> entry = treeMap.entrySet().iterator().next();
                return entry.getValue() == 1 ? (String)entry.getKey() + "!" : entry.getValue() + " " + computeString((String)entry.getKey());
            }
            ArrayList list = new ArrayList();
            boolean flag = treeMap.values().stream().anyMatch(integer -> integer > 1);

            for (Entry entry2 : treeMap.entrySet()) {
                String key = (String)entry2.getKey();
                int val = (Integer)entry2.getValue();
                list.add(val == 1 ? key : val + " " + computeString(key));
            }

            return flag ? String.join(" ", list) : computeString(list);
        }
        return "";
    }

    private static String computeString(Collection<String> collection) {
        if (collection.size() == 1) {
            return (String)collection.iterator().next();
        }
        java.util.List list = new ArrayList(collection);
        return String.join(", ", list.subList(0, list.size() - 1)) + " AND " + (String)list.get(list.size() - 1);
    }

    private FormatHelper() {
    }

    private static String computeString(String str) {
        if ("SET".equalsIgnoreCase(str)) {
            return "SETS";
        }
        return "SHOP".equalsIgnoreCase(str) ? "SHOPS" : str;
    }
}
