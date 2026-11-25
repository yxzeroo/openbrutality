package org.brutality.mellow.util;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.StringUtils;

public class UnknownHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static String computeString() {
        if (mc.theWorld != null && mc.theWorld.getScoreboard() != null) {
            for (ScoreObjective scoreObjective : mc.theWorld.getScoreboard().getScoreObjectives()) {
                if (scoreObjective != null) {
                    String str = StringUtils.stripControlCodes(scoreObjective.getDisplayName()).toLowerCase();
                    if (str.contains("bed")) {
                        return "bedwars";
                    }

                    if (str.contains("duels") || str.contains("arena")) {
                        return "duels";
                    }

                    if (str.contains("skywars")) {
                        return "skywars";
                    }

                    if (str.contains("bridge")) {
                        return "the_bridge";
                    }
                }
            }

            return "unknown";
        }
        return "unknown";
    }

    public static boolean checkState() {
        if (mc.theWorld != null && mc.getNetHandler() != null) {
            try {
                String serverIp = mc.getCurrentServerData().serverIP;
                if (serverIp == null || !serverIp.toLowerCase().contains("hypixel")) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

            if (mc.theWorld.getScoreboard() == null) {
                return false;
            }
            for (ScoreObjective scoreObjective : mc.theWorld.getScoreboard().getScoreObjectives()) {
                if (scoreObjective != null) {
                    String displayName = scoreObjective.getDisplayName();
                    String str = StringUtils.stripControlCodes(displayName).toLowerCase();
                    if (str.contains("bed") || str.contains("wins") || str.contains("kills") || str.contains("final")) {
                        return true;
                    }
                }
            }

            return false;
        }
        return false;
    }
}
