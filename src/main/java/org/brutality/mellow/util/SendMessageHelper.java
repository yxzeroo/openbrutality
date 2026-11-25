package org.brutality.mellow.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.brutality.mellow.api.BedwarsFinalsHandler;
import org.brutality.mellow.config.IsPregameStatsHandler;

public class SendMessageHelper {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final IsPregameStatsHandler isPregameStatsHandler;
    private final BedwarsFinalsHandler bedwarsFinalsHandler;
    private final UtilHelper utilHelper;
    private boolean flag = false;
    private final Map<String, SendMessageHelper.Entry> lookupMap = new HashMap<>();
    private static final Pattern pattern = Pattern.compile("^(\\w+) was ([\\w-]+)'s final #([\\d,]+)\\. FINAL KILL!$");
    private static final Pattern pattern2 = Pattern.compile("^(?:BED DESTRUCTION > )?(\\w+) (?:Bed|bed) was bed #([\\d,]+) destroyed by ([\\w-]+)!$");

    private void sendMessage(String str) {
        if (this.mc.thePlayer != null) {
            this.mc.thePlayer.addChatMessage(new ChatComponentText(str));
        }
    }

    private void handleString(String str, String str2, String str3) {
        try {
            int n = Integer.parseInt(str3);
            if (str.equals("finals")) {
                this.bedwarsFinalsHandler.handleString(str2, n, entry -> {
                    if (entry != null && entry.flag && entry.entries != null && !entry.entries.isEmpty()) {
                        BedwarsFinalsHandler.Entry2 entry2 = entry.entries.get(0);
                        this.sendMessage("§aFound match: §f" + entry2.name + " §7(distance: " + entry2.count + ")");
                    } else {
                        this.sendMessage("§cNo matches found for " + str2);
                    }
                });
            } else {
                this.bedwarsFinalsHandler.handleString(str2, (double)n, entry3 -> {
                    if (entry3 != null && entry3.flag && entry3.entries != null && !entry3.entries.isEmpty()) {
                        BedwarsFinalsHandler.Entry2 entry22 = entry3.entries.get(0);
                        this.sendMessage("§aFound match: §f" + entry22.name + " §7(distance: " + entry22.count + ")");
                    } else {
                        this.sendMessage("§cNo matches found for " + str2);
                    }
                });
            }
        } catch (Exception e) {
            this.sendMessage("§cError processing " + str + ": " + e.getMessage());
        }
    }

    public void handleString(String str) {
        if (this.isPregameStatsHandler.isNumberDenicker()) {
            String str2 = str.replaceAll("§.", "").trim();
            if (this.matchesString(str2)) {
                if (!this.flag) {
                    this.flag = true;
                }
            } else if (this.flag) {
                Matcher matcher = pattern.matcher(str2);
                if (matcher.find()) {
                    String str3 = matcher.group(2);
                    String str4 = matcher.group(3).replace(",", "");

                    try {
                        int n = Integer.parseInt(str4);
                        if (n >= 10 && this.matchesString2(str3) && this.utilHelper.matchesString(str3)) {
                            this.sendMessage("§aAttempting to denick " + str3 + " with " + str4 + " finals");
                            this.handleString("finals", str3, str4);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                } else {
                    Matcher matcher2 = pattern2.matcher(str2);
                    if (matcher2.find()) {
                        String str5 = matcher2.group(3);
                        String str6 = matcher2.group(2).replace(",", "");
                        if (this.matchesString2(str5) && this.utilHelper.matchesString(str5)) {
                            this.sendMessage("§aAttempting to denick " + str5 + " with " + str6 + " beds");
                            this.handleString("beds", str5, str6);
                        }
                    }
                }
            }
        }
    }

    public SendMessageHelper(IsPregameStatsHandler isPregameStatsHandler, UtilHelper utilHelper, BedwarsFinalsHandler bedwarsFinalsHandler) {
        this.isPregameStatsHandler = isPregameStatsHandler;
        this.utilHelper = utilHelper;
        this.bedwarsFinalsHandler = bedwarsFinalsHandler;
    }

    private boolean matchesString(String str) {
        return str.contains("Protect your bed and destroy the enemy beds.") || str.contains("The game starts in") || str.contains("Starting in");
    }

    private boolean matchesString2(String str) {
        return this.mc.theWorld != null;
    }

    public void process() {
        this.flag = false;
        this.lookupMap.clear();
    }

    public static class Entry {
        public boolean g = false;
        public boolean flag = false;

        private Entry() {
        }
    }
}
