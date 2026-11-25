package org.brutality.module.impl.client;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import org.brutality.alt.altmanager.AccountManager;
import org.brutality.alt.altmanager.auth.AccountSession;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.EventChat;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.network.IrcClient;

public class IRC extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ModeSetting modeSetting = new ModeSetting("Trigger Mode", this, "#", Arrays.asList("#", "@", "!", "~"));
    private long lastPingTime = 0L;
    private static final long timestamp = 20000L;

    private String computeString3() {
        try {
            AccountSession accountSession = AccountManager.getAccountSession();
            if (accountSession != null && accountSession.getUsername() != null && !accountSession.getUsername().trim().isEmpty()) {
                return accountSession.getUsername();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    @Override
    public void handleFlag3(boolean flag) {
        super.handleFlag3(true);
    }

    private String computeString4() {
        try {
            if (this.mc.getSession() != null && this.mc.getSession().getUsername() != null) {
                return this.mc.getSession().getUsername();
            }

            if (this.mc.thePlayer != null) {
                return this.mc.thePlayer.getName();
            }
        } catch (Exception ignored) {
        }

        return "Unknown";
    }

    private void handleJsonObject(JsonObject json) {
        String str = this.computeString(json, "username", "Unknown");
        String str2 = this.computeString(json, "message", "");
        String str3 = this.computeString(json, "ingame_username", null);
        String str4 = this.computeString(json, "alt_username", null);
        int n = json.has("buyer_count") && !json.get("buyer_count").isJsonNull() ? json.get("buyer_count").getAsInt() : -1;
        boolean flag = json.has("fromDiscord") && json.get("fromDiscord").getAsBoolean();
        ChatComponentText chatComponentText = this.computeChatComponentText();
        if (flag) {
            String str5 = str.startsWith("[Discord] ") ? str.substring(10) : str;
            String str6 = MessageUtils.text8;
            String str7 = MessageUtils.text12;
            String str8 = MessageUtils.text9;
            String str9 = MessageUtils.text12;
            chatComponentText.appendSibling(new ChatComponentText(MessageUtils.text9 + "[" + str9 + "DC" + str8 + "] " + str7 + str5 + str6 + ": "));
            this.handleChatComponentText(chatComponentText, str2);
        } else {
            this.handleChatComponentText(chatComponentText, str, str2, str3, str4, n);
        }

        if (!str.equalsIgnoreCase(IrcClient.computeString()) && this.matchesString2(str2)) {
            this.mc.thePlayer.playSound("note.pling", 1.0F, 1.0F);
        }

        this.mc.thePlayer.addChatMessage(chatComponentText);
    }

    private void handleChatComponentText(ChatComponentText chatComponentText, String str, String str2, String str3, String str4, int n) {
        String str5 = str3 != null && !str3.trim().isEmpty() && !"Unknown".equalsIgnoreCase(str3) ? str3 : str;
        if (n > 0) {
            chatComponentText.appendSibling(new ChatComponentText(MessageUtils.text7 + n + MessageUtils.text16 + " "));
        }

        ChatComponentText chatComponentText2 = new ChatComponentText(MessageUtils.text15 + str5);
        chatComponentText2.setChatStyle(this.computeChatStyle(str5, str4));
        chatComponentText.appendSibling(chatComponentText2);
        chatComponentText.appendSibling(new ChatComponentText(MessageUtils.text8 + ": "));
        this.handleChatComponentText(chatComponentText, str2);
    }

    private Pattern computePattern() {
        return computePattern(IrcClient.computeString(), this.computeString4());
    }

    @Override
    public boolean checkState3() {
        return true;
    }

    public void handleString(String str) {
        String str2 = IrcClient.computeString();
        if (str2 != null) {
            IrcClient.handleString2(str2, str);
        }
    }

    public String computeString2() {
        return IrcClient.computeString();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (RuntimeAuthGuard.checkState2()) {
                if (event instanceof EventUpdate) {
                    this.process();
                } else if (event instanceof EventChat) {
                    this.onEventChat((EventChat)event);
                }
            }
        }
    }

    private ChatStyle computeChatStyle(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageUtils.text8).append("Minecraft: ").append(MessageUtils.text16).append(str);
        if (str2 != null && !str2.trim().isEmpty()) {
            sb.append("\n").append(MessageUtils.text8).append("Alt: ").append(MessageUtils.text16).append(str2);
        }

        ChatStyle chatStyle = new ChatStyle();
        chatStyle.setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentText(sb.toString())));
        return chatStyle;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        IrcClient.process4();
        this.handleFlag3(true);
    }

    @Override
    public void process28() {
        if (this.checkState44()) {
            this.handleString2(MessageUtils.text13 + "IRC is forced on.");
        } else {
            super.process28();
        }
    }

    private String computeString(JsonObject json, String str, String str2) {
        return json.has(str) ? json.get(str).getAsString() : str2;
    }

    private void handleJsonObject3(JsonObject json) {
        ChatComponentText chatComponentText = this.computeChatComponentText();
        String str = MessageUtils.text13;
        String str2 = MessageUtils.text8;
        String str3 = this.computeString(json, "error", "Unknown");
        String str4 = str2;
        chatComponentText.appendSibling(new ChatComponentText(str + "[Error] " + str4 + str3));
        this.mc.thePlayer.addChatMessage(chatComponentText);
    }

    public boolean checkState() {
        return IrcClient.isConnected();
    }

    private void handleJsonObject2(JsonObject json) {
        ChatComponentText chatComponentText = this.computeChatComponentText();
        chatComponentText.appendSibling(new ChatComponentText(MessageUtils.text15 + this.computeString(json, "message", "")));
        this.mc.thePlayer.addChatMessage(chatComponentText);
    }

    private void onEventChat(EventChat event) {
        String message = event.getMessage();
        String text = this.modeSetting.getText();
        if (message.startsWith(text) && !message.startsWith("/") && !message.startsWith(".")) {
            String str = message.substring(text.length()).trim();
            if (!str.isEmpty()) {
                String str2 = IrcClient.computeString();
                if (str2 == null) {
                    this.handleString2(MessageUtils.text13 + "Not authenticated with IRC.");
                    event.cancel();
                } else {
                    IrcClient.handleString2(str2, str);
                    JsonObject json = new JsonObject();
                    json.addProperty("type", "message");
                    json.addProperty("username", str2);
                    json.addProperty("message", str);
                    json.addProperty("ingame_username", this.computeString4());
                    json.addProperty("rank", IrcClient.computeString(str2));
                    Integer integer = IrcClient.computeInteger();
                    if (integer != null) {
                        json.addProperty("buyer_count", integer);
                    }

                    String str3 = this.computeString3();
                    if (str3 != null) {
                        json.addProperty("alt_username", str3);
                    }

                    this.handleJsonObject4(json);
                    event.cancel();
                }
            }
        }
    }

    private static boolean matchesString(String str) {
        return str != null && !str.trim().isEmpty() && !"Unknown".equalsIgnoreCase(str);
    }

    public String computeString() {
        return this.modeSetting.getText();
    }

    @Override
    public void onEnable() {
        if (!RuntimeAuthGuard.checkState2()) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            IrcClient.connect("Player" + System.currentTimeMillis() % 1000L);
        }
    }

    public IRC() {
        super("IRC", 0, Category.CLIENT);
        this.setRequiresInGame(false);
        this.handleSettingArray(new Setting[]{this.modeSetting});
        this.process29();
    }

    private void handleString2(String str) {
        String str2 = MessageUtils.text9;
        String str3 = MessageUtils.text10;
        this.mc.thePlayer.addChatMessage(new ChatComponentText(MessageUtils.text9 + "[" + str3 + "Brutality-IRC" + str2 + "] " + str));
    }

    private boolean matchesString2(String str) {
        Pattern pattern = this.computePattern();
        return pattern != null && pattern.matcher(str).find();
    }

    public static Pattern computePattern(String str, String str2) {
        String str3 = matchesString(str) ? Pattern.quote(str) : null;
        String str4 = matchesString(str2) && !str2.equalsIgnoreCase(str) ? Pattern.quote(str2) : null;
        return str3 == null && str4 == null
            ? null
            : Pattern.compile("(?<![A-Za-z0-9_])(?:" + (str3 == null ? str4 : (str4 == null ? str3 : str3 + "|" + str4)) + ")(?![A-Za-z0-9_])", 2);
    }

    private void process() {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            if (!IrcClient.isConnected()) {
                IrcClient.process();
            } else {
                long now = System.currentTimeMillis();
                if (IrcClient.checkState()) {
                    this.lastPingTime = now;
                }

                if (now - this.lastPingTime >= 20000L) {
                    this.lastPingTime = now;
                    IrcClient.sendPing();
                }
            }

            JsonObject json;
            while ((json = IrcClient.computeJsonObject()) != null) {
                this.handleJsonObject4(json);
            }
        }
    }

    private void handleJsonObject4(JsonObject json) {
        try {
            String str = json.has("type") ? json.get("type").getAsString() : "raw";
            switch (str) {
                case "message":
                    this.handleJsonObject(json);
                    break;
                case "system":
                    this.handleJsonObject2(json);
                    break;
                case "error":
                    this.handleJsonObject3(json);
            }
        } catch (Exception ignored) {
        }
    }

    private void handleChatComponentText(ChatComponentText chatComponentText, String str) {
        Pattern pattern = this.computePattern();
        if (pattern == null) {
            chatComponentText.appendSibling(new ChatComponentText(MessageUtils.text22 + str));
        } else {
            Matcher matcher = pattern.matcher(str);

            int n;
            for (n = 0; matcher.find(); n = matcher.end()) {
                chatComponentText.appendSibling(new ChatComponentText(MessageUtils.text22 + str.substring(n, matcher.start())));
                String str2 = MessageUtils.text15;
                String str3 = matcher.group();
                String str4 = MessageUtils.text22;
                String str5 = str3;
                chatComponentText.appendSibling(new ChatComponentText(str2 + "§l" + str5 + str4));
            }

            chatComponentText.appendSibling(new ChatComponentText(MessageUtils.text22 + str.substring(n)));
        }
    }

    private ChatComponentText computeChatComponentText() {
        return new ChatComponentText(MessageUtils.text9 + "[" + MessageUtils.text10 + "Brutality-IRC" + MessageUtils.text9 + "] ");
    }
}
