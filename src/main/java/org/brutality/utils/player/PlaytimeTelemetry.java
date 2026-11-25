package org.brutality.utils.player;

import net.minecraft.client.Minecraft;
import org.brutality.system.BackendAuthService;

public final class PlaytimeTelemetry {
    private static final long timestamp = 30000L;
    private static final long timestamp2 = 60000L;
    private static String text;
    private static String text2;
    private static long timestamp3 = 0L;
    private static long timestamp4 = 0L;
    private static long timestamp5 = 0L;
    private static final String text3 = "brutality-playtime-sync";

    public static void init() {
        timestamp4 = System.currentTimeMillis();
    }

    private PlaytimeTelemetry() {
    }

    public static void tick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            timestamp3 = 0L;
        } else if (mc.isGamePaused()) {
            timestamp3 = 0L;
        } else {
            long now = System.currentTimeMillis();
            String name = mc.thePlayer.getName();
            String str = mc.thePlayer.getUniqueID().toString();
            if (text != null && !text.equalsIgnoreCase(name)) {
                syncPending();
                timestamp3 = 0L;
            }

            text = name;
            text2 = str;
            if (timestamp3 == 0L) {
                timestamp3 = now;
            } else {
                long l = now - timestamp3;
                timestamp3 = now;
                if (l > 0L && l <= 60000L) {
                    timestamp5 += l;
                }

                if (now - timestamp4 >= 30000L) {
                    syncPending();
                    timestamp4 = now;
                }
            }
        }
    }

    private static void syncPending() {
        if (timestamp5 > 0L && text != null) {
            long l = timestamp5;
            String str = text;
            String str2 = text2;
            timestamp5 = 0L;
            new Thread(() -> {
                try {
                    BackendAuthService instance = BackendAuthService.getInstance();
                    instance.handleString(str, str2, l);
                } catch (Exception e) {
                    timestamp5 += l;
                }
            }, text3).start();
        }
    }

    public static void shutdown() {
        syncPending();
        text = null;
        text2 = null;
        timestamp3 = 0L;
        timestamp5 = 0L;
    }
}
