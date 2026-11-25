package org.brutality.mellow.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.brutality.mellow.Mellow;
import org.brutality.mellow.api.ApiHandler;
import org.brutality.mellow.api.ERRORHandler;
import org.brutality.mellow.api.PingHandler;
import org.brutality.mellow.api.StripColorHandler;
import org.brutality.mellow.api.SuccessHandler;
import org.brutality.mellow.config.IsPregameStatsHandler;
import org.brutality.mellow.data.GetPlayerNameHandler;
import org.brutality.mellow.util.UtilHelper3;

public class ClearCacheHandler {
    private final StripColorHandler stripColorHandler;
    private final ERRORHandler errorHandler;
    private final SuccessHandler successHandler;
    private final PingHandler pingHandler;
    private final ApiHandler apiHandler;
    private final UtilHelper3 utilHelper3;
    private final IsPregameStatsHandler isPregameStatsHandler;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Map<String, GetPlayerNameHandler> lookupMap = new ConcurrentHashMap<>();
    private final Map<String, Long> lookupMap2 = new ConcurrentHashMap<>();
    private static final long timestamp = 300000L;

    public void handleString3(String str) {
        this.lookupMap.remove(str);
        this.lookupMap2.remove(str);
    }

    private boolean matchesString(String str) {
        Long val = this.lookupMap2.get(str);
        return val == null ? false : System.currentTimeMillis() - val < 300000L;
    }

    public void handleString(String str, boolean flag) {
        this.executorService.submit(() -> {
            GetPlayerNameHandler getPlayerNameHandler = this.successHandler.computeGetPlayerNameHandler(str);
            if (getPlayerNameHandler != null) {
                this.lookupMap.put(str, getPlayerNameHandler);
                this.lookupMap2.put(str, System.currentTimeMillis());
                if (Mellow.lookupMap != null) {
                    Mellow.lookupMap.put(str, getPlayerNameHandler);
                }

                if (!flag && this.isPregameStatsHandler.isPrintStats()) {
                    this.handleString(str, getPlayerNameHandler);
                }
            }
        });
    }

    public void handleString(String str) {
        this.handleString3(str);
    }

    public void handleString2(String str) {
        this.handleString(str, false);
    }

    public ClearCacheHandler(
        StripColorHandler stripColorHandler, ERRORHandler errorHandler, SuccessHandler successHandler, PingHandler pingHandler, ApiHandler apiHandler, UtilHelper3 utilHelper3, IsPregameStatsHandler isPregameStatsHandler
    ) {
        this.stripColorHandler = stripColorHandler;
        this.errorHandler = errorHandler;
        this.successHandler = successHandler;
        this.pingHandler = pingHandler;
        this.apiHandler = apiHandler;
        this.utilHelper3 = utilHelper3;
        this.isPregameStatsHandler = isPregameStatsHandler;
    }

    public GetPlayerNameHandler computeGetPlayerNameHandler(String str) {
        return this.matchesString(str) ? this.lookupMap.get(str) : null;
    }

    private void handleString(String str, GetPlayerNameHandler getPlayerNameHandler) {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.ingameGUI == null) {
                return;
            }

            String text = getPlayerNameHandler.getText();
            String text2 = getPlayerNameHandler.getText2();
            String text3 = getPlayerNameHandler.getText3();
            String text4 = getPlayerNameHandler.getText4();
            String text6 = getPlayerNameHandler.getText6();
            String str2 = "§7[§b" + text + "§7] §f" + str + " §7FKDR: §a" + text2 + " §7WS: §e" + text3 + " §7WLR: §b" + text4 + " §7Wins: §2" + text6;
            ChatComponentText chatComponentText = new ChatComponentText(str2);
            mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
        } catch (Exception ignored) {
        }
    }

    public boolean matchesString2(String str) {
        return this.lookupMap.containsKey(str) && this.matchesString(str);
    }

    public void clearCache() {
        this.lookupMap.clear();
        this.lookupMap2.clear();
        if (Mellow.lookupMap != null) {
            Mellow.lookupMap.clear();
        }
    }
}
