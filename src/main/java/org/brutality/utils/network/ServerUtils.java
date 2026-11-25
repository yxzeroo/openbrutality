package org.brutality.utils.network;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.brutality.utils.mcp.McpApi;

public class ServerUtils implements McpApi {
    public static ServerData serverData;
    private static boolean flag;

    public static void process() throws IOException {
        flag = isHostsRedirectingHypixel();
    }

    public static boolean isOnSameTeam(EntityLivingBase living) {
        if (mc.thePlayer != null) {
            String text = mc.thePlayer.getDisplayName().getUnformattedText();
            if (text.contains("§")) {
                int n = text.indexOf("§");
                String str = text.substring(n, n + 2);
                return living != null && living.getDisplayName().getFormattedText().contains(str);
            }
        }

        return false;
    }

    public static boolean checkState5() {
        if (!mc.isSingleplayer() && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
            String str = mc.getCurrentServerData().serverIP.toLowerCase();
            int n = str.indexOf(58);
            if (n > 0) {
                str = str.substring(0, n);
            }

            return str.endsWith("pitclassic.net");
        }
        return false;
    }

    public static boolean checkState4() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.getCurrentServerData() != null) {
            String str = mc.getCurrentServerData().serverIP.toLowerCase();
            return str.contains("hypixel") || str.contains("liquidproxy") || checkState5();
        }
        return false;
    }

    public static boolean checkState3() {
        if (mc.theWorld == null) {
            return true;
        }
        List entities = mc.theWorld.getLoadedEntityList();

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = (Entity)entities.get(i);
            if (entity != null && entity.getDisplayName().equals("§e§lCLICK TO PLAY")) {
                return true;
            }
        }

        return false;
    }

    public static boolean matchesString(String str) {
        if (mc.getCurrentServerData() == null) {
            return false;
        }
        str = str.toLowerCase();
        String str2 = mc.isSingleplayer() ? "" : mc.getCurrentServerData().serverIP.toLowerCase();
        return str2.endsWith("." + str) || str2.equals(str);
    }

    public static boolean isHostsRedirectingHypixel() throws IOException {
        Path path = Paths.get(System.getenv("SystemDrive") + "\\Windows\\System32\\drivers\\etc\\hosts");
        return !Files.notExists(path) && Files.lines(path).anyMatch(str -> str.toLowerCase().contains("hypixel"));
    }

    public static boolean in() {
        return checkState() && !flag;
    }

    public static boolean checkState2() {
        return !mc.isSingleplayer() && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null
            ? mc.getCurrentServerData().serverIP.toLowerCase().contains("hypit")
            : false;
    }

    public static boolean checkState() {
        if (!mc.isSingleplayer() && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
            String str = mc.getCurrentServerData().serverIP.toLowerCase();
            if (str.contains("liquidproxy")) {
                return true;
            }
            if (!str.contains("hypixel")) {
                return false;
            }
            if (mc.thePlayer == null) {
                return true;
            }
            String clientBrand = mc.thePlayer.getClientBrand();
            return clientBrand != null && clientBrand.startsWith("Hypixel BungeeCord");
        }
        return false;
    }

    public static boolean ip() {
        return !mc.isSingleplayer() && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null
            ? mc.getCurrentServerData().serverIP.toLowerCase().contains("orionpit")
            : false;
    }
}
