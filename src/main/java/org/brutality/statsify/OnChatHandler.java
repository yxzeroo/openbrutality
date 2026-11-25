package org.brutality.statsify;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;

public class OnChatHandler {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final BuildTagsHandler buildTagsHandler = BuildTagsHandler.getInstance();

    private String computeString(String str, String str2, String str3, List<String> list) {
        String str4 = this.buildTagsHandler.getMinFkdrHandler().text5;
        if (str4.equals("bracket_star_name_dot_fkdr")) {
            String str5 = (String)list.get(0);
            String str6 = (String)list.get(1);
            String str7 = str5;
            return str + "§7[" + str7 + "§7] " + str2 + str3 + "・" + str6;
        }
        if (str4.equals("star_dot_name_dot_fkdr")) {
            return str + (String)list.get(0) + "・" + str2 + str3 + "・" + (String)list.get(1);
        }
        if (str4.equals("name_dot_fkdr")) {
            return str + str2 + str3 + "・" + (String)list.get(1);
        }
        String str8 = (String)list.get(0);
        String str9 = (String)list.get(1);
        String str10 = str8;
        return str + "§7[" + str10 + "§7] " + str2 + str3 + "・" + str9;
    }

    private String[] computeStringArray(String str) {
        if (this.mc.theWorld != null && this.mc.theWorld.getScoreboard() != null) {
            ScorePlayerTeam playersTeam = this.mc.theWorld.getScoreboard().getPlayersTeam(str);
            if (playersTeam == null) {
                return new String[]{"", str, ""};
            }
            int len = playersTeam.getColorPrefix().length();
            if (len == 10) {
                return new String[]{playersTeam.getColorPrefix(), str, playersTeam.getColorSuffix()};
            }
            return len == 8 ? new String[]{playersTeam.getColorPrefix(), str, ""} : new String[]{"", str, ""};
        }
        return new String[]{"", str, ""};
    }

    public void onChat(ChatEvent chatEvent) {
        if (chatEvent != null && chatEvent.message != null) {
            try {
                String text = chatEvent.message.getUnformattedText();
                if (this.buildTagsHandler.getMinFkdrHandler().flag4
                    && text.contains("Protect your bed and destroy the enemy beds.")
                    && !text.contains(":")
                    && !text.contains("SHOUT")) {
                    this.mc.thePlayer.sendChatMessage("/who");
                }

                if (text.startsWith("ONLINE:")) {
                    String str = text.substring("ONLINE:".length()).trim();
                    this.buildTagsHandler.handleString(str);
                    this.buildTagsHandler.process2();
                    if (this.buildTagsHandler.getMinFkdrHandler().flag3) {
                        this.buildTagsHandler.process();
                    }
                }

                if (text.startsWith(" ") && text.contains("Opponent:")) {
                    String str2 = computeString(text);
                    new Thread(() -> {
                        try {
                            String str3 = this.buildTagsHandler.computeString2(str2);
                            this.mc.thePlayer.addChatMessage(new ChatComponentText("§r[§bF§r] " + str3));
                        } catch (Exception e) {
                            this.mc.thePlayer.addChatMessage(new ChatComponentText("§r[§bF§r] §c" + str2 + " is possibly nicked.§r"));
                        }
                    }).start();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void process() {
        if (this.buildTagsHandler.getMinFkdrHandler().flag2) {
            if (this.mc.thePlayer != null && this.mc.thePlayer.sendQueue != null) {
                try {
                    var playerInfoMap = this.mc.thePlayer.sendQueue.getPlayerInfoMap();
                    if (playerInfoMap.isEmpty()) {
                        return;
                    }

                    var lookupMap = this.buildTagsHandler.getLookupMap();

                    for (NetworkPlayerInfo playerInfo : playerInfoMap) {
                        if (playerInfo != null && playerInfo.getGameProfile() != null) {
                            String name = playerInfo.getGameProfile().getName();
                            var list = (List)lookupMap.get(name);
                            if (list != null && list.size() >= 2) {
                                String[] parts = this.computeStringArray(name);
                                String str = parts[0];
                                String str2 = parts[1];
                                String str3 = parts[2];
                                if (!str2.endsWith("・" + (String)list.get(1))) {
                                    String str4 = str.length() >= 2 ? str.substring(0, 2) : "";
                                    String str5 = this.computeString(str, str4, str2, list);
                                    if (str5 != null) {
                                        playerInfo.setDisplayName(new ChatComponentText(str5));
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void onEvent(Event event) {
        if (event instanceof ChatEvent) {
            this.onChat((ChatEvent)event);
        }
    }

    private static String computeString(String str) {
        str = str.trim();
        String[] parts = str.split("\\s+");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }
}
