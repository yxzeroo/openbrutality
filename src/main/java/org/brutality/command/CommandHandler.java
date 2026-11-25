package org.brutality.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

public final class CommandHandler {
    private static void handleSet(Set<String> set, String str, String str2, String str3, boolean flag) {
        if (str != null && str.matches("[A-Za-z0-9_]{1,16}")) {
            if (flag || !str.equalsIgnoreCase(str3)) {
                if (str.toLowerCase(Locale.ROOT).startsWith(str2)) {
                    set.add(str);
                }
            }
        }
    }

    public static List<String> computeList(String str, boolean flag) {
        TreeSet treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) {
            return new ArrayList<>();
        }
        String str2 = mc.thePlayer == null ? null : mc.thePlayer.getName();
        if (mc.getNetHandler() != null) {
            for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                if (playerInfo != null && playerInfo.getGameProfile() != null) {
                    handleSet(treeSet, playerInfo.getGameProfile().getName(), str, str2, flag);
                }
            }
        }

        if (mc.theWorld != null) {
            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity != null) {
                    handleSet(treeSet, playerEntity.getName(), str, str2, flag);
                }
            }
        }

        return new ArrayList<>(treeSet);
    }

    public static List<String> computeList(String[] parts, boolean flag) {
        String str = parts != null && parts.length != 0 ? parts[parts.length - 1].toLowerCase(Locale.ROOT) : "";
        return computeList(str, flag);
    }

    private CommandHandler() {
    }
}
