package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.Brutality;
import org.brutality.mellow.Mellow;
import org.brutality.mellow.data.GetPlayerNameHandler;

public class BrutalityUserHelper {
    private static final String text = "・";

    private static String computeString(NetworkPlayerInfo playerInfo) {
        return playerInfo.getPlayerTeam() != null ? playerInfo.getPlayerTeam().getColorPrefix() : "";
    }

    public static String computeString2(NetworkPlayerInfo playerInfo) {
        if (playerInfo == null) {
            return null;
        }
        String name = playerInfo.getGameProfile().getName();
        Minecraft mc = Minecraft.getMinecraft();
        String str = playerInfo.getDisplayName() != null
            ? playerInfo.getDisplayName().getFormattedText()
            : ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), name);
        if (mc.thePlayer != null && name.equals(mc.thePlayer.getName())) {
            return str;
        }
        // VENDOR-DISABLED: Brutality.proximityTracker.matchesString(name)
        if (Mellow.getInstance() != null && Mellow.getInstance().getTabStatsSetting() != null && Mellow.getInstance().getTabStatsSetting().isEnabled()) {
            GetPlayerNameHandler getPlayerNameHandler = Mellow.lookupMap.get(name);
            if (getPlayerNameHandler != null) {
                return computeString(playerInfo, name, getPlayerNameHandler);
            }
        }

        return str;
    }

    private static String computeString(NetworkPlayerInfo playerInfo, String str, GetPlayerNameHandler getPlayerNameHandler) {
        String str2 = computeString(playerInfo);
        String str3 = str2.length() >= 2 ? str2.substring(0, 2) : "";
        StringBuilder sb = new StringBuilder();
        String text = getPlayerNameHandler.getText();
        if (text != null && !text.isEmpty()) {
            sb.append("§7[").append(text).append("§7] ");
        }

        String text2 = getPlayerNameHandler.getText2();
        if (text2 != null && !text2.isEmpty()) {
            sb.append(text2).append(" ");
        }

        String text3 = getPlayerNameHandler.getText3();
        if (text3 != null
            && !text3.isEmpty()
            && Mellow.getInstance() != null
            && Mellow.getInstance().getShowWinstreakSetting() != null
            && Mellow.getInstance().getShowWinstreakSetting().isEnabled()) {
            sb.append(text3).append(" ");
        }

        sb.append(str3).append(str);
        return sb.toString();
    }
}
