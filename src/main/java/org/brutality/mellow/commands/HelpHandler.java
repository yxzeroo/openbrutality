package org.brutality.mellow.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.brutality.mellow.Mellow;
import org.brutality.utils.MessageUtils;

public class HelpHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static Mellow mellow;

    private static void handleStringArray(String[] parts) {
        if (parts.length < 2) {
            String str = mellow != null ? mellow.getAuroraKeySetting().getValue() : "";
            if (str.isEmpty()) {
                MessageUtils.x(MessageUtils.text13 + "Usage: /mellow aurorakey <key>");
                MessageUtils.x(MessageUtils.text8 + "Current: Not set");
            } else {
                MessageUtils.x(MessageUtils.text8 + "Current: " + str.substring(0, Math.min(8, str.length())) + "...");
            }
        } else {
            String str2 = parts[1];
            if (mellow != null) {
                mellow.getAuroraKeySetting().setValue(str2);
                mellow.process();
                MessageUtils.x(MessageUtils.text11 + "Aurora API key set!");
            }
        }
    }

    public static boolean matchesStringArray(String[] parts) {
        if (parts.length == 0) {
            process();
            return true;
        }
        String str = parts[0].toLowerCase();
        switch (str) {
            case "help":
            case "?":
                process();
                break;
            case "bw":
                if (parts.length < 2) {
                    MessageUtils.x(MessageUtils.text13 + "Usage: /mellow bw <player>");
                    return true;
                }

                String str2 = parts[1];
                if (mellow != null && Mellow.clearCacheHandler != null) {
                    Mellow.clearCacheHandler.handleString2(str2);
                    MessageUtils.x(MessageUtils.text11 + "Fetching stats for " + str2 + "...");
                }
                break;
            case "fetchall":
            case "refresh":
                process2();
                break;
            case "who":
                if (mc.thePlayer != null) {
                    mc.thePlayer.sendChatMessage("/who");
                    MessageUtils.x(MessageUtils.text11 + "Sent /who command");
                }
                break;
            case "blacklist":
            case "bl":
                handleStringArray3(parts);
                break;
            case "clear":
            case "clearcache":
                if (Mellow.clearCacheHandler != null) {
                    Mellow.clearCacheHandler.clearCache();
                    MessageUtils.x(MessageUtils.text11 + "Cache cleared!");
                }
                break;
            case "reload":
                if (mellow != null) {
                    mellow.process();
                    MessageUtils.x(MessageUtils.text11 + "Config saved!");
                }
                break;
            case "urchinkey":
            case "ukey":
                handleStringArray5(parts);
                break;
            case "seraphkey":
            case "skey":
                handleStringArray4(parts);
                break;
            case "polsukey":
            case "pkey":
                handleStringArray2(parts);
                break;
            case "aurorakey":
            case "akey":
                handleStringArray(parts);
                break;
            default:
                MessageUtils.x(MessageUtils.text13 + "Unknown command. Use /mellow help");
        }

        return true;
    }

    private static void handleStringArray2(String[] parts) {
        if (parts.length < 2) {
            String str = mellow != null ? mellow.getPolsuKeySetting().getValue() : "";
            if (str.isEmpty()) {
                MessageUtils.x(MessageUtils.text13 + "Usage: /mellow polsukey <key>");
                MessageUtils.x(MessageUtils.text8 + "Current: Not set");
            } else {
                MessageUtils.x(MessageUtils.text8 + "Current: " + str.substring(0, Math.min(8, str.length())) + "...");
            }
        } else {
            String str2 = parts[1];
            if (mellow != null) {
                mellow.getPolsuKeySetting().setValue(str2);
                mellow.process();
                MessageUtils.x(MessageUtils.text11 + "Polsu API key set!");
            }
        }
    }

    private static void handleStringArray4(String[] parts) {
        if (parts.length < 2) {
            String str = mellow != null ? mellow.getSeraphKeySetting().getValue() : "";
            if (str.isEmpty()) {
                MessageUtils.x(MessageUtils.text13 + "Usage: /mellow seraphkey <key>");
                MessageUtils.x(MessageUtils.text8 + "Current: Not set");
            } else {
                MessageUtils.x(MessageUtils.text8 + "Current: " + str.substring(0, Math.min(8, str.length())) + "...");
            }
        } else {
            String str2 = parts[1];
            if (mellow != null) {
                mellow.getSeraphKeySetting().setValue(str2);
                mellow.process();
                MessageUtils.x(MessageUtils.text11 + "Seraph API key set!");
            }
        }
    }

    public static void setMellow(Mellow mellow2) {
        mellow = mellow2;
    }

    private static void handleStringArray5(String[] parts) {
        if (parts.length < 2) {
            String str = mellow != null ? mellow.getUrchinKeySetting().getValue() : "";
            if (str.isEmpty()) {
                MessageUtils.x(MessageUtils.text13 + "Usage: /mellow urchinkey <key>");
                MessageUtils.x(MessageUtils.text8 + "Current: Not set");
            } else {
                MessageUtils.x(MessageUtils.text8 + "Current: " + str.substring(0, Math.min(8, str.length())) + "...");
            }
        } else {
            String str2 = parts[1];
            if (mellow != null) {
                mellow.getUrchinKeySetting().setValue(str2);
                mellow.process();
                MessageUtils.x(MessageUtils.text11 + "Urchin API key set!");
            }
        }
    }

    private static void process2() {
        if (mc.getNetHandler() == null) {
            MessageUtils.x(MessageUtils.text13 + "Not connected to a server");
        } else {
            int n = 0;

            for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                String name = playerInfo.getGameProfile().getName();
                if (name != null && !name.isEmpty() && Mellow.clearCacheHandler != null) {
                    Mellow.clearCacheHandler.handleString2(name);
                    n++;
                }
            }

            MessageUtils.x(MessageUtils.text11 + "Fetching stats for " + n + " players...");
        }
    }

    private static void process() {
        MessageUtils.x(MessageUtils.text15 + "=== Mellow Commands ===");
        MessageUtils.x(MessageUtils.text16 + "/mellow bw <player> - Check player stats");
        MessageUtils.x(MessageUtils.text16 + "/mellow fetchall - Fetch all player stats");
        MessageUtils.x(MessageUtils.text16 + "/mellow who - Send /who command");
        MessageUtils.x(MessageUtils.text16 + "/mellow blacklist add <player> - Add to blacklist");
        MessageUtils.x(MessageUtils.text16 + "/mellow blacklist remove <player> - Remove from blacklist");
        MessageUtils.x(MessageUtils.text16 + "/mellow blacklist list - View blacklist");
        MessageUtils.x(MessageUtils.text16 + "/mellow clear - Clear cache");
        MessageUtils.x(MessageUtils.text16 + "/mellow reload - Save config");
        MessageUtils.x(MessageUtils.text16 + "/mellow urchinkey <key> - Set Urchin API key");
        MessageUtils.x(MessageUtils.text16 + "/mellow seraphkey <key> - Set Seraph API key");
        MessageUtils.x(MessageUtils.text16 + "/mellow polsukey <key> - Set Polsu API key");
        MessageUtils.x(MessageUtils.text16 + "/mellow aurorakey <key> - Set Aurora API key");
    }

    private static void handleStringArray3(String[] parts) {
        if (parts.length < 3) {
            MessageUtils.x(MessageUtils.text13 + "Usage: /mellow blacklist <add/remove/list> <player>");
        } else {
            String str = parts[1].toLowerCase();
            if (Mellow.removeHelper == null) {
                MessageUtils.x(MessageUtils.text13 + "Blacklist not initialized");
            } else {
                switch (str) {
                    case "add":
                        String str2 = parts[2];
                        Mellow.removeHelper.add(str2);
                        MessageUtils.x(MessageUtils.text11 + "Added " + str2 + " to blacklist");
                        break;
                    case "remove":
                    case "del":
                        String str3 = parts[2];
                        Mellow.removeHelper.remove(str3);
                        MessageUtils.x(MessageUtils.text11 + "Removed " + str3 + " from blacklist");
                        break;
                    case "list":
                        MessageUtils.x(MessageUtils.text15 + "Blacklist (" + Mellow.removeHelper.size() + "):");

                        for (String str4 : Mellow.removeHelper.computeSet()) {
                            MessageUtils.x(MessageUtils.text16 + "- " + str4);
                        }
                        break;
                    default:
                        MessageUtils.x(MessageUtils.text13 + "Usage: /mellow blacklist <add/remove/list> <player>");
                }
            }
        }
    }
}
