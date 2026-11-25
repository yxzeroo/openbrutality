package org.brutality.mellow.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.brutality.mellow.Mellow;
import org.brutality.mellow.api.ApiHandler;
import org.brutality.mellow.api.PingHandler;

public class UtilHelper3 {
    private final PingHandler pingHandler;
    private final ApiHandler apiHandler;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Map<String, String> lookupMap = new ConcurrentHashMap<>();
    private final Map<String, String> lookupMap2 = new ConcurrentHashMap<>();

    public void handleString(String str) {
        this.executorService.submit(() -> {
            try {
                String str2 = "";
                Mellow instance = Mellow.getInstance();
                if (instance != null && instance.getUrchinKeySetting() != null) {
                    str2 = instance.getUrchinKeySetting().getValue();
                }

                String str3 = this.pingHandler.computeString(str, str2);
                if (str3 != null) {
                    this.lookupMap.put(str, str3);
                }
            } catch (Exception ignored) {
            }
        });
    }

    public String computeString2(String str) {
        return this.lookupMap.get(str);
    }

    public void handleString2(String str) {
        this.executorService.submit(() -> {
            try {
                String str2 = "";
                Mellow instance = Mellow.getInstance();
                if (instance != null && instance.getSeraphKeySetting() != null) {
                    str2 = instance.getSeraphKeySetting().getValue();
                }

                String str3 = this.apiHandler.computeString(str, str2);
                if (str3 != null) {
                    this.lookupMap2.put(str, str3);
                }
            } catch (Exception ignored) {
            }
        });
    }

    public String computeString(String str) {
        return this.lookupMap2.get(str);
    }

    public UtilHelper3(PingHandler pingHandler, ApiHandler apiHandler) {
        this.pingHandler = pingHandler;
        this.apiHandler = apiHandler;
    }

    public void clear() {
        this.lookupMap.clear();
        this.lookupMap2.clear();
    }
}
