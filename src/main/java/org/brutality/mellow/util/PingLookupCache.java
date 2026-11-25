package org.brutality.mellow.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.brutality.mellow.api.PingHandler;

public class PingLookupCache {
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Map<String, String> lookupMap = new ConcurrentHashMap<>();

    public String computeString(String str) {
        return this.lookupMap.get(str);
    }

    public void clear() {
        this.lookupMap.clear();
    }

    public void handleString(String str) {
        this.executorService.submit(() -> {
            try {
                PingHandler pingHandler = new PingHandler(null);
                String str2 = pingHandler.computeString(str);
                if (str2 != null) {
                    this.lookupMap.put(str, str2);
                }
            } catch (Exception ignored) {
            }
        });
    }
}
