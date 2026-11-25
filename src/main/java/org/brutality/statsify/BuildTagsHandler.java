package org.brutality.statsify;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;

public class BuildTagsHandler {
    private static BuildTagsHandler instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final MinFkdrHandler minFkdrHandler;
    private final Map<String, List<String>> lookupMap = new ConcurrentHashMap<>();
    private final Map<String, BuildTagsHandler.Entry> lookupMap2 = new ConcurrentHashMap<>();
    private List<String> entries = new ArrayList<>();
    private static final long timestamp = 60L;

    public String computeString2(String str) throws Exception {
        String str2 = this.computeString7(str);
        if (str2 == null) {
            return str + " §cis possibly nicked.";
        }
        String str3 = this.computeString3(str2);
        if (str3 != null && !str3.isEmpty()) {
            JsonObject asJsonObject = new JsonParser().parse(str3).getAsJsonObject();
            JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("profile");
            if (asJsonObject2.has("tagged_name")) {
                asJsonObject2.get("tagged_name").getAsString();
            }

            JsonObject asJsonObject3 = asJsonObject.getAsJsonObject("achievements");
            String str4 = asJsonObject3.has("bedwars_level") ? asJsonObject3.get("bedwars_level").getAsString() : "0";
            String str5 = this.computeString4(str4);
            JsonObject asJsonObject4 = asJsonObject.getAsJsonObject("stats").getAsJsonObject("Bedwars");
            String str6 = asJsonObject4.has("final_kills_bedwars") ? asJsonObject4.get("final_kills_bedwars").getAsString() : "0";
            String str7 = asJsonObject4.has("final_deaths_bedwars") ? asJsonObject4.get("final_deaths_bedwars").getAsString() : "0";
            if (asJsonObject4.has("wins_bedwars")) {
                asJsonObject4.get("wins_bedwars").getAsInt();
            } else {
            }

            if (asJsonObject4.has("losses_bedwars")) {
                asJsonObject4.get("losses_bedwars").getAsInt();
            } else {
            }

            String str8 = asJsonObject4.has("winstreak") ? asJsonObject4.get("winstreak").getAsString() : "0";
            int n = Integer.parseInt(str6.replace(",", ""));
            int n2 = Integer.parseInt(str7.replace(",", ""));
            double d = n2 == 0 ? n : (double)n / n2;
            if (d < this.minFkdrHandler.count && this.minFkdrHandler.count != -1) {
                return "";
            }
            String str9 = "§7";
            if (d >= 1.0 && d < 3.0) {
                str9 = "§f";
            }

            if (d >= 3.0 && d < 8.0) {
                str9 = "§a";
            }

            if (d >= 8.0 && d < 16.0) {
                str9 = "§6";
            }

            if (d >= 16.0 && d < 25.0) {
                str9 = "§d";
            }

            if (d > 25.0) {
                str9 = "§4";
            }

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String str10 = decimalFormat.format(d);
            String str11 = "";
            int n3 = Integer.parseInt(str8.replace(",", "").trim());
            if (n3 > 0) {
                str11 = this.computeString(str8);
            }

            String str12 = str9 + str10;
            if (this.minFkdrHandler.flag2) {
                this.lookupMap.put(str, Arrays.asList(str5, str12));
            }

            String str13 = str + " §r" + str5 + "§r§7 |§r FKDR: " + str9 + str10;
            if (!str11.isEmpty()) {
                str13 = str13 + " §r§7|§r WS: " + str11;
            }

            if (this.minFkdrHandler.flag) {
                String str14 = this.buildTags(str, str2, Integer.parseInt(str4), d, n3, n, n2);
                if (!str14.isEmpty()) {
                    str13 = str13 + "§r [ " + str14.trim() + " ]";
                }
            }

            return str13;
        }
        return str + " §cis possibly nicked.";
    }

    public MinFkdrHandler getMinFkdrHandler() {
        return this.minFkdrHandler;
    }

    private String computeString3(String str) {
        try {
            String str2 = "https://nadeshiko.io/player/" + str + "/network";
            URL url = new URL(str2);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Accept", "application/json");
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();

                String str3;
                while ((str3 = reader.readLine()) != null) {
                    sb.append(str3);
                }

                reader.close();
                String str4 = sb.toString();
                Pattern pattern = Pattern.compile("playerData = JSON.parse\\(decodeURIComponent\\(\"(.*?)\"\\)\\)");
                Matcher matcher = pattern.matcher(str4);
                if (matcher.find()) {
                    String str5 = matcher.group(1);
                    return URLDecoder.decode(str5, "UTF-8");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public Map<String, List<String>> getLookupMap() {
        return this.lookupMap;
    }

    private String buildTags(String str, String str2, int n, double d, int n2, int n3, int n4) {
        String str3 = "";
        String[] parts = new String[]{"msmc", "kikin", "g0ld", "Fxrina_", "MAL_", "fer_", "ly_", "tzi_", "Verse_"};
        boolean flag = Arrays.stream(parts).anyMatch(str4 -> str.toLowerCase().contains(str4.toLowerCase()));
        if (flag || Pattern.compile("\\d.*\\d.*\\d.*\\d").matcher(str).find()) {
            str3 = str3 + "§eN §r";
        }

        if (n <= 6 && n2 >= 1) {
            str3 = str3 + "§aW §r";
        }

        if (n <= 6 && d >= 4.0) {
            str3 = str3 + "§4F §r";
        }

        if (n3 == 0 && n4 == 0) {
            str3 = str3 + "§c0F §r";
        }

        return str3;
    }

    public static String computeString6(String str) {
        try {
            String str2 = "https://api.minecraftservices.com/minecraft/profile/lookup/name/" + str;
            HttpURLConnection conn = (HttpURLConnection)new URL(str2).openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();

                String str3;
                while ((str3 = reader.readLine()) != null) {
                    sb.append(str3);
                }

                reader.close();
                String[] parts = sb.toString().split("\"");
                if (parts.length >= 5) {
                    return parts[3];
                }
            }

            if (responseCode == 404) {
                return "NICKED";
            }
        } catch (Exception ignored) {
        }

        return "NICKED";
    }

    public void clearCache() {
        this.lookupMap.clear();
        this.lookupMap2.clear();
    }

    public String computeString7(String str) {
        for (NetworkPlayerInfo playerInfo : this.mc.getNetHandler().getPlayerInfoMap()) {
            if (playerInfo.getGameProfile().getName().equalsIgnoreCase(str)) {
                return String.valueOf(playerInfo.getGameProfile().getId());
            }
        }

        return null;
    }

    private String computeString4(String str) {
        int n = Integer.parseInt(str);
        if (n < 100) {
            return "§7" + str + "✫";
        }
        if (n >= 100 && n < 200) {
            return "§f" + str + "✫";
        }
        if (n >= 200 && n < 300) {
            return "§6" + str + "✫";
        }
        if (n >= 300 && n < 400) {
            return "§b" + str + "✫";
        }
        if (n >= 400 && n < 500) {
            return "§2" + str + "✫";
        }
        if (n >= 500 && n < 600) {
            return "§3" + str + "✫";
        }
        if (n >= 600 && n < 700) {
            return "§4" + str + "✫";
        }
        if (n >= 700 && n < 800) {
            return "§d" + str + "✫";
        }
        if (n >= 800 && n < 900) {
            return "§9" + str + "✫";
        }
        if (n >= 900 && n < 1000) {
            return "§5" + str + "✫";
        }
        if (n >= 1000) {
            String[] parts = str.split("");
            String str2 = parts[0];
            String str3 = parts[1];
            String str4 = parts[2];
            String str5 = parts[3];
            String str6 = str4;
            String str7 = str3;
            String str8 = str2;
            return "§6" + str8 + "§e" + str7 + "§a" + str6 + "§b" + str5 + "§d✪";
        }
        return str;
    }

    private BuildTagsHandler() {
        this.minFkdrHandler = new MinFkdrHandler();
    }

    public void process2() {
        if (!this.entries.isEmpty()) {
            if (!this.minFkdrHandler.text3.equals("bws")) {
                this.mc.thePlayer.addChatMessage(new ChatComponentText("§r[§bF§r] §cPlancke is discontinued, use BWS."));
            } else {
                int n = Math.min(this.entries.size(), 20);
                ExecutorService executor = Executors.newFixedThreadPool(n);

                for (String str : this.entries) {
                    executor.submit(
                        () -> {
                            try {
                                String str2 = this.computeString2(str);
                                if (!str2.isEmpty()) {
                                    this.mc.addScheduledTask(() -> this.mc.thePlayer.addChatMessage(new ChatComponentText("§r[§bF§r] " + str2)));
                                }
                            } catch (Exception e) {
                                this.mc
                                    .addScheduledTask(
                                        () -> this.mc.thePlayer.addChatMessage(new ChatComponentText("§r[§bF§r] Failed to fetch stats for: " + str))
                                    );
                            }
                        }
                    );
                }

                executor.shutdown();
                new Thread(
                        () -> {
                            try {
                                if (executor.awaitTermination(timestamp, TimeUnit.SECONDS)) {
                                    this.mc.addScheduledTask(() -> this.mc.thePlayer.addChatMessage(new ChatComponentText("§r[§bF§r]§a Checks completed.")));
                                } else {
                                    this.mc
                                        .addScheduledTask(
                                            () -> this.mc.thePlayer.addChatMessage(new ChatComponentText("§r[§bF§r]§c Timeout waiting for completion."))
                                        );
                                }
                            } catch (InterruptedException e2) {
                                this.mc
                                    .addScheduledTask(
                                        () -> this.mc.thePlayer.addChatMessage(new ChatComponentText("§r[§bF§r] §cError while waiting: " + e2.getMessage()))
                                    );
                            }
                        }
                    )
                    .start();
            }
        }
    }

    private String computeString(String str) {
        int n = Integer.parseInt(str);
        String str2 = "§r";
        if (n >= 5 && n < 10) {
            str2 = "§b";
        }

        if (n >= 10 && n < 20) {
            str2 = "§6";
        }

        if (n >= 20) {
            str2 = "§4";
        }

        return str2 + str;
    }

    private String computeString5(String str) throws Exception {
        String str2 = "https://urchin.ws/player/" + str + "?key=" + this.minFkdrHandler.text4 + "&sources=MANUAL";
        URL url = new URL(str2);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("URCHIN: " + responseCode);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String str3;
        while ((str3 = reader.readLine()) != null) {
            sb.append(str3);
        }

        reader.close();
        String str4 = sb.toString();
        if (!str4.isEmpty()) {
            String str5 = "\"type\":\"(.*?)\".*?\"reason\":\"(.*?)\"";
            Pattern pattern = Pattern.compile(str5);
            Matcher matcher = pattern.matcher(str4);
            if (matcher.find()) {
                String str6 = matcher.group(1);
                String str7 = matcher.group(2);
                return "§r" + str6 + ". §rReason: §6" + str7;
            }
        }

        return "";
    }

    public void process() {
        if (this.minFkdrHandler.flag3 && !this.minFkdrHandler.text4.isEmpty()) {
            ExecutorService executor = Executors.newFixedThreadPool(5);

            for (String str : this.entries) {
                executor.submit(
                    () -> {
                        try {
                            String str2 = computeString6(str);
                            String str3 = this.computeString5(str)
                                .replace("sniper", "§4§lSniper")
                                .replace("blatant_cheater", "§4§lBlatant Cheater")
                                .replace("closet_cheater", "§e§lCloset Cheater")
                                .replace("confirmed_cheater", "§4§lConfirmed Cheater");
                            if (!str3.isEmpty()) {
                                this.mc
                                    .addScheduledTask(
                                        () -> this.mc
                                            .thePlayer
                                            .addChatMessage(new ChatComponentText("§r[§bF§r] §c⚠ §r" + str + " §ris §ctagged§r for: " + str3))
                                    );
                            }
                        } catch (Exception ignored) {
                        }
                    }
                );
            }

            executor.shutdown();
        }
    }

    public static BuildTagsHandler getInstance() {
        if (instance == null) {
            instance = new BuildTagsHandler();
        }

        return instance;
    }

    public void handleString(String str) {
        String[] parts = str.split(",\\s*");
        this.entries = new ArrayList<>(Arrays.asList(parts));
    }

    public static class Entry {
        public final String text;
        public final String text2;

        public Entry(String text, String str) {
            this.text = text;
            this.text2 = str;
        }
    }
}
