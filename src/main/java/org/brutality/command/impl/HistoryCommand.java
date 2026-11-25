package org.brutality.command.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.command.Command;
import org.brutality.command.CommandHandler;

public class HistoryCommand extends Command {
    private final Gson gson = new Gson();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm");

    @Override
    public List<String> computeList(String[] parts) {
        return (List<String>)(parts.length > 1 ? new ArrayList<>() : CommandHandler.computeList(parts, false));
    }

    public HistoryCommand() {
        super("history", "Check username history and account info", "history <username>", "hist");
    }

    private String computeString(String str) {
        return str.length() == 32
            ? str.substring(0, 8) + "-" + str.substring(8, 12) + "-" + str.substring(12, 16) + "-" + str.substring(16, 20) + "-" + str.substring(20, 32)
            : str;
    }

    private void handleString(String str, HistoryCommand.Entry entry) throws Exception {
        URL url = new URL("https://mcuuid.net/api/user/" + str);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("HTTP " + responseCode);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String str2;
        while ((str2 = reader.readLine()) != null) {
            sb.append(str2);
        }

        reader.close();
        JsonObject json = this.gson.fromJson(sb.toString(), JsonObject.class);
        if (json.has("uuid")) {
            String asString = json.get("uuid").getAsString();
            if (entry.text2 == null) {
                entry.text2 = asString;
                entry.flag = true;
            }
        }

        if (json.has("username")) {
            entry.text3 = json.get("username").getAsString();
        }

        if (json.has("username_history")) {
            for (JsonElement element : json.getAsJsonArray("username_history")) {
                JsonObject asJsonObject = element.getAsJsonObject();
                String asString2 = asJsonObject.get("username").getAsString();
                long l = asJsonObject.has("changed_to_at") ? asJsonObject.get("changed_to_at").getAsLong() : 0L;
                if (l > 0L) {
                    entry.entries.add(asString2 + " (until " + this.simpleDateFormat.format(new Date(l)) + ")");
                } else {
                    entry.entries.add(asString2 + " (original)");
                }
            }
        }
    }

    private void handleString2(String str, HistoryCommand.Entry entry) throws Exception {
        entry.entries2.add("NameMC: Check https://namemc.com/profile/" + str + " for more details");
    }

    private void sendMessage(String str) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(str));
        }
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length != 1) {
            this.sendMessage(EnumChatFormatting.RED + "Usage: ." + this.getName() + " <username>");
        } else {
            String str = args[0];
            this.sendMessage(EnumChatFormatting.YELLOW + "Looking up " + str + "...");
            CompletableFuture.<HistoryCommand.Entry>supplyAsync(() -> this.computeEntry(str)).thenAccept(entry -> {
                if (!entry.flag) {
                    this.sendMessage(EnumChatFormatting.RED + "Player '" + entry.text + "' not found!");
                    if (!entry.entries3.isEmpty()) {
                        this.sendMessage(EnumChatFormatting.GRAY + "Errors encountered:");

                        for (String str2 : entry.entries3) {
                            this.sendMessage(EnumChatFormatting.GRAY + "- " + str2);
                        }
                    }
                } else {
                    this.sendMessage(EnumChatFormatting.YELLOW + "=== " + entry.text + " Account Info ===");
                    if (entry.text3 != null && !entry.text3.equalsIgnoreCase(entry.text)) {
                        this.sendMessage(EnumChatFormatting.GREEN + "Current Name: " + entry.text3);
                    }

                    if (entry.text2 != null) {
                        String str3 = this.computeString(entry.text2);
                        this.sendMessage(EnumChatFormatting.AQUA + "UUID: " + str3);
                    }

                    if (!entry.entries.isEmpty()) {
                        this.sendMessage(EnumChatFormatting.YELLOW + "Username History:");

                        for (String str4 : entry.entries) {
                            this.sendMessage(EnumChatFormatting.WHITE + "- " + str4);
                        }
                    } else {
                        this.sendMessage(EnumChatFormatting.GRAY + "Username History: " + EnumChatFormatting.RED + "Unavailable (Mojang removed this API)");
                        this.sendMessage(EnumChatFormatting.GRAY + "Note: Username history is no longer publicly accessible");
                    }

                    if (!entry.entries2.isEmpty()) {
                        this.sendMessage(EnumChatFormatting.YELLOW + "Additional Resources:");

                        for (String str5 : entry.entries2) {
                            this.sendMessage(EnumChatFormatting.GRAY + "- " + str5);
                        }
                    }

                    this.sendMessage(EnumChatFormatting.YELLOW + "Manual Check Options:");
                    this.sendMessage(EnumChatFormatting.GRAY + "- NameMC: https://namemc.com/profile/" + entry.text);
                    this.sendMessage(EnumChatFormatting.GRAY + "- MCChecker: https://mcchecker.net/");
                    this.sendMessage(EnumChatFormatting.GRAY + "- PitPanda: Use .check " + entry.text + " for scammer info");
                    if (!entry.entries3.isEmpty()) {
                        this.sendMessage(EnumChatFormatting.DARK_GRAY + "Note: Some lookups failed, results may be incomplete");
                    }

                    this.sendMessage(EnumChatFormatting.YELLOW + "===============================");
                }
            }).exceptionally(t -> {
                this.sendMessage(EnumChatFormatting.RED + "Error looking up player: " + t.getMessage());
                return null;
            });
        }
    }

    private HistoryCommand.Entry computeEntry(String str) {
        HistoryCommand.Entry entry = new HistoryCommand.Entry(str);

        try {
            String str2 = this.computeString2(str);
            if (str2 != null) {
                entry.text2 = str2;
                entry.flag = true;
            }
        } catch (Exception e) {
            entry.entries3.add("Mojang API: " + e.getMessage());
        }

        try {
            this.handleString2(str, entry);
        } catch (Exception e2) {
            entry.entries3.add("NameMC lookup failed: " + e2.getMessage());
        }

        try {
            this.handleString(str, entry);
        } catch (Exception e3) {
            entry.entries3.add("MCUUID lookup failed: " + e3.getMessage());
        }

        return entry;
    }

    private String computeString2(String str) throws Exception {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + str);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        int responseCode = conn.getResponseCode();
        if (responseCode == 404) {
            return null;
        }
        if (responseCode != 200) {
            throw new Exception("HTTP " + responseCode);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String str2;
        while ((str2 = reader.readLine()) != null) {
            sb.append(str2);
        }

        reader.close();
        JsonObject json = this.gson.fromJson(sb.toString(), JsonObject.class);
        return json.get("id").getAsString();
    }

    public static class Entry {
        public final String text;
        public boolean flag = false;
        public String text2 = null;
        public String text3 = null;
        public List<String> entries = new ArrayList<>();
        public List<String> entries2 = new ArrayList<>();
        public List<String> entries3 = new ArrayList<>();

        public Entry(String text) {
            this.text = text;
        }
    }
}
