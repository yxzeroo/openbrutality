package org.brutality.mellow.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.brutality.mellow.cache.ClearCacheHandler;
import org.brutality.mellow.config.IsPregameStatsHandler;

public class UtilHelper {
    private final ClearCacheHandler clearCacheHandler;
    private final IsPregameStatsHandler isPregameStatsHandler;
    private final Map<String, String> lookupMap = new ConcurrentHashMap<>();
    private List<String> entries = new ArrayList<>();

    public boolean matchesString(String str) {
        return this.lookupMap.containsKey(str.toLowerCase());
    }

    public void handleList(List<String> list) {
        this.entries = new ArrayList<>(list);
    }

    public String computeString(String str) {
        return this.lookupMap.get(str.toLowerCase());
    }

    public void clear() {
        this.lookupMap.clear();
        this.entries.clear();
    }

    public UtilHelper(ClearCacheHandler clearCacheHandler, IsPregameStatsHandler isPregameStatsHandler) {
        this.clearCacheHandler = clearCacheHandler;
        this.isPregameStatsHandler = isPregameStatsHandler;
    }

    public void handleString2(String str) {
        this.clearCacheHandler.handleString2(str);
    }

    public void handleString(String str) {
        this.lookupMap.remove(str.toLowerCase());
    }

    public List<String> getEntries() {
        return this.entries;
    }

    public void handleString(String str, String str2) {
        this.lookupMap.put(str.toLowerCase(), str2);
    }
}
