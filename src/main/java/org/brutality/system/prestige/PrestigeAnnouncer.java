package org.brutality.system.prestige;

import net.minecraft.client.Minecraft;
import org.brutality.system.BackendAuthService;
import org.brutality.utils.MessageUtils;

public final class PrestigeAnnouncer {
    private static long timestamp;
    private static int count = -1;
    private static final long timestamp2 = 15000L;

    private PrestigeAnnouncer() {
    }

    public static void onChatLine(String message) {
        PrestigeChatParser.Entry entry = PrestigeChatParser.computeEntry(message);
        if (entry != null) {
            String str = computeString();
            if (str != null && matchesString(str, entry.text)) {
                long now = System.currentTimeMillis();
                if (entry.count != count || now - timestamp >= timestamp2) {
                    count = entry.count;
                    timestamp = now;
                    String str2 = PrestigeServerResolver.g();
                    Thread thread = new Thread(() -> handleEntry(entry, str, str2), "Prestige-Announce");
                    thread.setDaemon(true);
                    thread.start();
                }
            }
        }
    }

    private static void handleEntry(PrestigeChatParser.Entry entry, String str, String str2) {
        BackendAuthService instance = BackendAuthService.getInstance();
        String str3 = instance.checkState4() ? instance.getUsername() : "Unknown";
        int n = PrestigeStatsStore.computeInt(str3, str);
        int n2 = n >= 0 ? n : Math.max(0, entry.count - 1);
        if (n2 >= entry.count) {
            n2 = Math.max(0, entry.count - 1);
        }

        int count = entry.count;
        PrestigeStatsStore.handleString(str3, str, entry.count, count);
        boolean flag = PrestigeDeliveryClient.matchesInt(entry.count, Math.max(1, entry.count - n2), str, str2, str3, n2, entry.text2, null, count);
        notifyPrestigeDelivery(entry.count, flag);
    }

    private static void notifyPrestigeDelivery(int n, boolean flag) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) {
            String str;
            if (flag) {
                str = MessageUtils.text7;
                String str2 = MessageUtils.text18;
                String str3 = MessageUtils.text22;
                String str4 = MessageUtils.text8;
                String str5 = MessageUtils.text15;
                String str6 = PrestigeLevelFormat.format(n);
                String str7 = MessageUtils.text8;
                String str8 = str6;
                String str9 = str5;
                String str10 = str4;
                String str11 = str3;
                String str12 = str2;
                str = str + str12 + "PRESTIGE! " + str11 + str10 + "Announcement sent for prestige " + str9 + str8 + str7 + ".";
            } else {
                str = MessageUtils.text15
                    + "[Prestige] "
                    + MessageUtils.text8
                    + "Stats saved, but webhook delivery failed. Check the prestige webhook settings.";
            }

            String str13 = str;
            mc.addScheduledTask(() -> {
                if (mc.thePlayer != null) {
                    MessageUtils.x(str13);
                }
            });
        }
    }

    private static String computeString() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null) {
            String name = mc.thePlayer.getName();
            return name != null && !name.isEmpty() ? name.trim() : null;
        }
        return null;
    }

    private static boolean matchesString(String str, String str2) {
        return str2 != null && !str2.isEmpty() ? str.equalsIgnoreCase(str2.trim()) : false;
    }
}
