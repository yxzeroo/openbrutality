package org.brutality.command.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.command.Command;
import org.brutality.command.CommandHandler;

public class CheckCommand extends Command {
    private final Gson gson = new Gson();

    private void sendMessage(String str) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(str));
        }
    }

    @Override
    public List<String> computeList(String[] parts) {
        return (List<String>)(parts.length > 1 ? new ArrayList<>() : CommandHandler.computeList(parts, false));
    }

    public CheckCommand() {
        super("check", "Check if a player is on the scammer list", "check <username>", "c");
    }

    private CheckCommand.Entry computeEntry(String str) {
        try {
            URL url = new URL("https://pitpanda.rocks/api/players/" + str);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            int responseCode = conn.getResponseCode();
            if (responseCode == 404) {
                return new CheckCommand.Entry(false, "Player not found", null, str);
            }
            if (responseCode != 200) {
                return new CheckCommand.Entry(false, "API request failed with code: " + responseCode, null, str);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String str2;
            while ((str2 = reader.readLine()) != null) {
                sb.append(str2);
            }

            reader.close();
            JsonObject json = this.gson.fromJson(sb.toString(), JsonObject.class);
            return new CheckCommand.Entry(true, null, json, str);
        } catch (Exception e) {
            return new CheckCommand.Entry(false, "Connection error: " + e.getMessage(), null, str);
        }
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length != 1) {
            this.sendMessage(EnumChatFormatting.RED + "Usage: ." + this.getName() + " <username>");
        } else {
            String str = args[0];
            this.sendMessage(EnumChatFormatting.YELLOW + "Checking " + str + "...");
            CompletableFuture.<CheckCommand.Entry>supplyAsync(() -> this.computeEntry(str)).thenAccept(entry -> {
                if (!entry.flag) {
                    this.sendMessage(EnumChatFormatting.RED + entry.text);
                } else {
                    JsonObject jsonObject = entry.jsonObject;
                    String str2 = entry.text2;
                    if (jsonObject.has("name") && !jsonObject.get("name").isJsonNull()) {
                        str2 = jsonObject.get("name").getAsString();
                    }

                    if (jsonObject.has("displays") && !jsonObject.get("displays").isJsonNull()) {
                        JsonArray asJsonArray = jsonObject.getAsJsonArray("displays");
                        if (asJsonArray.size() == 0) {
                            this.sendMessage(EnumChatFormatting.GREEN + "No scammer tag detected in pitpanda");
                        } else {
                            boolean flag = false;
                            String str3 = "";

                            for (JsonElement element : asJsonArray) {
                                if (element.isJsonObject()) {
                                    JsonObject asJsonObject = element.getAsJsonObject();
                                    if (asJsonObject.has("type") && !asJsonObject.get("type").isJsonNull()) {
                                        String str4 = asJsonObject.get("type").getAsString().trim().toLowerCase();
                                        if ("scammer".equals(str4)) {
                                            flag = true;
                                            if (asJsonObject.has("notes") && !asJsonObject.get("notes").isJsonNull()) {
                                                str3 = asJsonObject.get("notes").getAsString();
                                            }
                                            break;
                                        }
                                    }
                                }
                            }

                            if (flag) {
                                this.sendMessage(EnumChatFormatting.RED + str2 + " is a SCAMMER! Reason: " + str3);
                            } else {
                                this.sendMessage(EnumChatFormatting.GREEN + "No scammer tag detected in pitpanda");
                            }
                        }
                    } else {
                        this.sendMessage(EnumChatFormatting.GREEN + "No scammer tag detected in pitpanda");
                    }
                }
            }).exceptionally(t -> {
                this.sendMessage(EnumChatFormatting.RED + "Error checking player: " + t.getMessage());
                return null;
            });
        }
    }

    public static class Entry {
        public final boolean flag;
        public final String text;
        public final JsonObject jsonObject;
        public final String text2;

        public Entry(boolean flag, String text, JsonObject jsonObject, String str) {
            this.flag = flag;
            this.text = text;
            this.jsonObject = jsonObject;
            this.text2 = str;
        }
    }
}
