package org.brutality.gui.chat;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.brutality.utils.WHelper;
import org.brutality.utils.render.OutlineThicknessHelper;

public final class ParseIntComponent {
    public static final String text = "/brutality:chataction ";
    public static final String text2 = " ";
    private static final String text3 = "(?:\\[[^\\]]*\\]\\s*)*";
    private static final String text4 = "([A-Za-z0-9_]{1,16})";
    private static final Pattern pattern = Pattern.compile("^STREAK! of ([0-9,]+) kills by (?:\\[[^\\]]*\\]\\s*)*([A-Za-z0-9_]{1,16})");
    private static final Pattern pattern2 = Pattern.compile("^MEGASTREAK!\\s*(?:\\[[^\\]]*\\]\\s*)*([A-Za-z0-9_]{1,16}) (activated .+?)\\s*$");
    private static final Pattern pattern3 = Pattern.compile("^BOUNTY! bump ([0-9,.]+g?) on (?:\\[[^\\]]*\\]\\s*)*([A-Za-z0-9_]{1,16})");
    private static final Pattern pattern4 = Pattern.compile(
        "^(?:(?:Guild|Party|Officer) > |From |To )?(?:\\[[^\\]]*\\]\\s*)*([A-Za-z0-9_]{1,16})(?:\\s\\[[^\\]]*\\])?:\\s"
    );
    private static final Pattern pattern5 = Pattern.compile("\\[([^\\]]*)\\]\\s*([A-Za-z0-9_]{1,16})");
    private static int count;
    private static final Map<Integer, ParseIntComponent.Entry> lookupMap = new LinkedHashMap<Integer, ParseIntComponent.Entry>(64, 0.75F, false) {
        @Override
        protected boolean removeEldestEntry(java.util.Map.Entry<Integer, ParseIntComponent.Entry> entry) {
            return this.size() > 400;
        }
    };
    public static final int count2 = 12;

    private static synchronized int computeInt(ParseIntComponent.Entry entry) {
        int n = ++count;
        lookupMap.put(n, entry);
        return n;
    }

    private static int parseInt(String str) {
        try {
            return Integer.parseInt(str.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static boolean matchesString(String str) {
        if (str != null && str.startsWith("/brutality:chataction ")) {
            ParseIntComponent.Entry entry;
            try {
                synchronized (ParseIntComponent.class) {
                    entry = lookupMap.get(Integer.parseInt(str.substring("/brutality:chataction ".length()).trim()));
                }
            } catch (NumberFormatException e) {
                return true;
            }

            if (entry != null) {
                Minecraft.getMinecraft().displayGuiScreen(new SendAnnouncementScreen(entry));
            }

            return true;
        }
        return false;
    }

    public static ParseIntComponent.Entry computeEntry(String str) {
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            matcher = pattern2.matcher(str);
            if (matcher.find()) {
                String str2 = matcher.group(1);
                String str3 = matcher.group(2).replaceAll("[.!]+$", "");
                return new ParseIntComponent.Entry(
                    ParseIntComponent.StreakMode.MEGASTREAK, str2, computeString(str, str2), str, str2 + " " + str3 + "!", false
                );
            }
            matcher = pattern3.matcher(str);
            if (matcher.find()) {
                String str4 = matcher.group(2);
                return new ParseIntComponent.Entry(
                    ParseIntComponent.StreakMode.BOUNTY,
                    str4,
                    computeString(str, str4),
                    str,
                    str4 + " just got a " + matcher.group(1) + " bounty bump!",
                    false
                );
            }
            return null;
        }
        String str5 = matcher.group(2);
        int n = parseInt(matcher.group(1));
        boolean flag = n >= 100 && WHelper.matchesString(str5) && matchesString(str5, "UBER");
        String str6 = flag ? str5 + " is on a " + n + " uber right now!" : str5 + " is on a " + n + " killstreak right now!";
        return new ParseIntComponent.Entry(ParseIntComponent.StreakMode.STREAK, str5, computeString(str, str5), str, str6, flag);
    }

    public static boolean matchesIChatComponent(IChatComponent component) {
        if (component == null) {
            return false;
        }
        IChatComponent component2 = null;

        for (IChatComponent component3 : component) {
            String unformattedTextForChat = component3.getUnformattedTextForChat();
            if (unformattedTextForChat != null && !unformattedTextForChat.isEmpty()) {
                component2 = component3;
            }
        }

        if (component2 != null && " ".equals(component2.getUnformattedTextForChat())) {
            ClickEvent chatClickEvent = component2.getChatStyle().getChatClickEvent();
            return chatClickEvent != null && chatClickEvent.getValue() != null && chatClickEvent.getValue().startsWith("/brutality:chataction ");
        }
        return false;
    }

    private static IChatComponent computeIChatComponent(ParseIntComponent.Entry entry) {
        String str = EnumChatFormatting.GRAY.toString();
        String str2 = EnumChatFormatting.WHITE.toString();
        String str3 = EnumChatFormatting.YELLOW.toString();
        String str4;
        switch (entry.streakMode) {
            case STREAK:
                String str5 = entry.flag ? "'s uber" : "'s streak";
                String text = entry.text;
                str4 = str3 + "Click to announce " + str2 + text + str3 + str5 + "\n" + str + "Guild, party or message - plus player actions";
                break;
            case MEGASTREAK:
                String text2 = entry.text;
                str4 = str3 + "Click to announce " + str2 + text2 + str3 + "'s megastreak\n" + str + "Guild, party or message - plus player actions";
                break;
            case BOUNTY:
                String text3 = entry.text;
                str4 = str3 + "Click to announce " + str2 + text3 + str3 + "'s bounty bump\n" + str + "Guild, party or message - plus player actions";
                break;
            default:
                String text4 = entry.text;
                str4 = str3 + "Click to open actions for " + str2 + text4 + "\n" + str + "Message, party, report, KOS, PitPanda profile";
        }

        return new ChatComponentText(str4);
    }

    private static String computeString(String str, String str2) {
        Matcher matcher = pattern5.matcher(str);

        while (matcher.find()) {
            if (matcher.group(2).equals(str2)) {
                return matcher.group(1);
            }
        }

        return "";
    }

    public static IChatComponent computeIChatComponent(IChatComponent component) {
        if (component == null) {
            return component;
        }
        try {
            String str = EnumChatFormatting.getTextWithoutFormattingCodes(component.getUnformattedText()).trim();
            ParseIntComponent.Entry entry = computeEntry(str);
            if (entry == null) {
                Matcher matcher = pattern4.matcher(str);
                if (!matcher.find()) {
                    return component;
                }

                entry = new ParseIntComponent.Entry(ParseIntComponent.StreakMode.PLAYER, matcher.group(1), computeString(str, matcher.group(1)), str, "", false);
            }

            ClickEvent clickEvent = new ClickEvent(Action.RUN_COMMAND, "/brutality:chataction " + computeInt(entry));
            HoverEvent hoverEvent = new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, computeIChatComponent(entry));
            ChatComponentText chatComponentText = new ChatComponentText(" ");
            chatComponentText.getChatStyle().setChatClickEvent(clickEvent).setChatHoverEvent(hoverEvent);
            IChatComponent component2 = component.createCopy();

            for (IChatComponent component3 : component2) {
                ClickEvent chatClickEvent = component3.getChatStyle().getChatClickEvent();
                if (chatClickEvent == null || chatClickEvent.getAction() != Action.OPEN_URL) {
                    component3.getChatStyle().setChatClickEvent(clickEvent);
                    component3.getChatStyle().setChatHoverEvent(hoverEvent);
                }
            }

            ChatComponentText chatComponentText2 = new ChatComponentText("");
            chatComponentText2.appendSibling(component2);
            chatComponentText2.appendSibling(chatComponentText);
            return chatComponentText2;
        } catch (RuntimeException e) {
            return component;
        }
    }

    public static ParseIntComponent.Entry computeEntry2(String str) {
        return new ParseIntComponent.Entry(ParseIntComponent.StreakMode.VIEW, str, "", "", "", false);
    }

    public static boolean matchesString(String str, String str2) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() != null && mc.ingameGUI != null) {
            NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(str);
            if (playerInfo == null) {
                return false;
            }
            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(mc.ingameGUI.getTabList().getPlayerName(playerInfo));
            return textWithoutFormattingCodes != null && textWithoutFormattingCodes.toUpperCase(Locale.ROOT).contains(str2);
        }
        return false;
    }

    private ParseIntComponent() {
    }

    public static void handleInt(int n, int n2, int n3) {
        if (n3 >= 8) {
            float f = n3 / 255.0F;
            OutlineThicknessHelper.handleFloat(n, n2 - 0.5F, 9.0F, 9.0F, 4.5F, new Color(255, 255, 255, (int)(52.0F * f)));
            OutlineThicknessHelper.handleFloat(n, n2 - 0.5F, 9.0F, 9.0F, 4.5F, 0.5F, new Color(0, 0, 0, 0), new Color(255, 255, 255, (int)(120.0F * f)));
            int n4 = (int)(245.0F * f) << 24 | 16777215;

            for (int i = 0; i < 3; i++) {
                Gui.drawRect(n + 2 + i * 2, n2 + 4, n + 3 + i * 2, n2 + 5, n4);
            }
        }
    }

    public static final class Entry {
        public final ParseIntComponent.StreakMode streakMode;
        public final String text;
        public final String text2;
        public final String text3;
        public final String text4;
        public final boolean flag;
        public final String text5;

        public Entry(ParseIntComponent.StreakMode streakMode, String text, String str, String str2, String str3, boolean flag) {
            this.streakMode = streakMode;
            this.text = text;
            this.text2 = str;
            this.text3 = str2;
            this.text4 = str3;
            this.flag = flag;
            int n = str2.indexOf(": ");
            this.text5 = streakMode == ParseIntComponent.StreakMode.PLAYER && n >= 0 ? str2.substring(n + 2) : str2;
        }
    }

    public static enum StreakMode {
        STREAK,
        MEGASTREAK,
        BOUNTY,
        PLAYER,
        VIEW;

        private static ParseIntComponent.StreakMode[] computeStreakModeArray() {
            return new ParseIntComponent.StreakMode[]{STREAK, MEGASTREAK, BOUNTY, PLAYER, VIEW};
        }
    }
}
