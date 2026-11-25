package org.brutality.system.prestige;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public final class PrestigeServerResolver {
    private PrestigeServerResolver() {
    }

    public static String g() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) {
            return "unknown";
        }
        if (mc.isSingleplayer()) {
            return "singleplayer";
        }
        ServerData serverData = mc.getCurrentServerData();
        return serverData != null && serverData.serverIP != null && !serverData.serverIP.trim().isEmpty() ? serverData.serverIP.trim() : "unknown";
    }

    public static String computeString(String serverIp) {
        if (serverIp != null && !serverIp.isEmpty()) {
            String str = serverIp.toLowerCase();
            if ("singleplayer".equals(str)) {
                return "Singleplayer";
            }
            if (str.contains("hypixel")) {
                return "Hypixel";
            }
            if (str.contains("pitclassic.net") || str.contains("classicpit") || str.contains("classic-pit") || str.contains("classic.pit")) {
                return "Classic Pit";
            }
            if (str.contains("mc.orionpit.com") || str.contains("orionpit")) {
                return "Orion Pit";
            }
            if (str.contains("hypit")) {
                return "Hypit";
            }
            if (str.contains("harrys")) {
                return "Harrys";
            }
            return str.contains("jungle") ? "Jungle" : serverIp.trim();
        }
        return "Unknown";
    }

    public static String computeString2(String serverIp) {
        String str = computeString(serverIp);
        return "Unknown".equals(str) ? "Prestiged on server Unknown" : "Prestiged on " + str;
    }

    public static String computeString3(String serverIp) {
        return serverIp != null && !serverIp.isEmpty() && !"unknown".equalsIgnoreCase(serverIp) ? serverIp : "Unknown";
    }
}
