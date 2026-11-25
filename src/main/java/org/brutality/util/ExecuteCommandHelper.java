package org.brutality.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import org.brutality.core.DisplayNameHandler;

public final class ExecuteCommandHelper {
    public static final String text = "✬";
    public static final char charVal = 10140;
    private static final String text2 = "§8[§4B§8] §r";
    private static final HashMap<String, String> lookupMap = DisplayNameHandler.lookupMap11;

    public static void handleString6(String str) {
    }

    public static void handleString(String str) {
        var str2 = String.valueOf(EnumChatFormatting.YELLOW);
        EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
        EnumChatFormatting BOLD = EnumChatFormatting.BOLD;
        handleString("§8[§4B§8] §r", str2 + BOLD + "WARNING! " + YELLOW + str);
    }

    public static void handleString4(String str) {
        String str2 = String.valueOf(EnumChatFormatting.AQUA);
        EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
        handleString("§8[§4B§8] §r", str2 + "TIP: " + WHITE + str);
    }

    public static void executeCommand(String str) {
        Minecraft mc = Minecraft.getMinecraft();
        String str2 = str.trim();
        mc.addScheduledTask(() -> {
            if (!str2.startsWith("/")) {
                handleString2(str2);
            } else {
                mc.ingameGUI.getChatGUI().addToSentMessages(str2);
                mc.thePlayer.sendChatMessage(str2);
            }
        });
    }

    public static IChatComponent computeIChatComponent(String str, String str2, String str3) {
        ChatComponentText chatComponentText = new ChatComponentText(str);
        ChatStyle chatStyle = new ChatStyle();
        chatStyle.setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, str3));
        chatStyle.setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new ChatComponentText(str2)));
        chatComponentText.setChatStyle(chatStyle);
        return chatComponentText;
    }

    public static void handleString2(String str, String str2) {
        var str3 = String.valueOf(EnumChatFormatting.RED);
        EnumChatFormatting RED = EnumChatFormatting.RED;
        EnumChatFormatting BOLD = EnumChatFormatting.BOLD;
        handleString(str, str3 + BOLD + "ERROR! " + RED + str2);
    }

    public static void handleString2(String str) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(str));
        }
    }

    public static double computeDouble4(String str) {
        double d = computeDouble6(str);
        if (d > 0.0) {
            return d;
        }
        double d2 = computeDouble2(str);
        if (d2 > 0.0) {
            return d2;
        }
        double d3 = computeDouble3(str);
        if (d3 > 0.0) {
            return d3;
        }
        int n = computeInt4(str);
        return n > 0 ? n : 0.0;
    }

    private static int computeInt4(String str) {
        if (!computeString6(str).startsWith(String.valueOf('➜'))) {
            return 0;
        }
        String str2 = str.split(" ")[1];

        try {
            return str2.endsWith("0g") ? Integer.parseInt(str2.replace("+", "").replace(",", "").replace("g", "")) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int computeInt(String str) {
        return matchesString2(str) ? 1 : 0;
    }

    public static float computeFloat(String str) throws NumberFormatException {
        return Float.parseFloat(str.toLowerCase().replace("xp", "").replace(",", "").replace("+", "").trim());
    }

    public static float computeFloat2(String str) throws NumberFormatException {
        return Float.parseFloat(str.toLowerCase().replace("g", "").replace(",", "").replace("+", "").trim());
    }

    public static int computeInt5(String str) {
        return computeString6(str).startsWith("MYSTIC ITEM!") ? 1 : 0;
    }

    public static String computeString5(String str) {
        return str.replaceAll(" §6§l\\d+g", "").replaceAll(" §c§l\\d+g", "").replaceAll(" §b§l\\d+g", "");
    }

    public static String computeString4(String str) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == 167 && i + 1 < str.length()) {
                sb.append(ch).append(str.charAt(++i));
            } else if (ch == ' ') {
                sb.append(ch);
            } else if (sb.length() >= 2 && ch != 194) {
                break;
            }
        }

        return sb.toString();
    }

    public static String computeString8(String str) {
        String str2 = computeString6(str);
        if (!matchesString2(str2)) {
            return null;
        }
        String[] parts = str2.split(" ");
        if (str2.startsWith("MULTI KILL!")) {
            return parts[5];
        }
        return str2.startsWith("KILL!") ? parts[3] : parts[4];
    }

    public static boolean matchesString2(String str) {
        String str2 = computeString6(str);
        return str2.startsWith("KILL!")
            || str2.startsWith("DOUBLE KILL!")
            || str2.startsWith("TRIPLE KILL!")
            || str2.startsWith("QUADRA KILL!")
            || str2.startsWith("PENTA KILL")
            || str2.startsWith("MULTI KILL!");
    }

    private static double computeDouble2(String str) {
        String str2 = str.replace(",", "").replace("g", "");
        if (str2.startsWith("SELF-CHECKOUT!")) {
            String[] parts = str2.split(" ");
            return Double.parseDouble(parts[parts.length - 1]);
        }
        return 0.0;
    }

    public static double computeDouble5(String str) {
        double d = computeDouble(str);
        if (d > 0.0) {
            return d;
        }
        int n = computeInt3(str);
        return n > 0 ? n : 0.0;
    }

    public static String computeString(String str) {
        return str.replaceAll("(§r)?§.\\[§.(?:§l)?\\d{1,3}§.]", "").replaceFirst(" ", "");
    }

    public static String computeString2(String str) {
        return str.replace(" §e✬", "");
    }

    private ExecuteCommandHelper() {
    }

    public static String computeString3(String str) {
        return !str.startsWith("§") ? EnumChatFormatting.BOLD + str : str.substring(0, 2) + EnumChatFormatting.BOLD + str.substring(2);
    }

    private static double computeDouble3(String str) {
        if (!str.startsWith("HIGHLANDER!")) {
            return 0.0;
        }
        try {
            return Double.parseDouble(str.substring(str.indexOf(43) + 1).replace(",", "").replace("g", ""));
        } catch (NumberFormatException e) {
            return -9.9999999E7;
        }
    }

    public static void handleString(String str, String str2) {
        String str3 = str + str2;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(str3));
        }
    }

    public static int computeInt2(String str) {
        return computeString6(str).startsWith("ASSIST!") ? 1 : 0;
    }

    public static String computeString6(String str) {
        return str.replaceAll("§.", "");
    }

    private static int computeInt3(String str) {
        if (!computeString6(str).startsWith(String.valueOf('➜'))) {
            return 0;
        }
        return str.contains("XP") ? Integer.parseInt(str.split("")[1].replace("+", "").replace(",", "")) : 0;
    }

    public static double computeDouble6(String str) {
        if (computeInt(str) == 0 && computeInt2(str) == 0) {
            return 0.0;
        }
        Matcher matcher = Pattern.compile("\\+([\\d.,]+)g").matcher(str);
        if (matcher.find()) {
            String str2 = matcher.group(1);
            if (str2.indexOf(44) >= 0 && str2.indexOf(46) >= 0) {
                str2 = str2.lastIndexOf(44) > str2.lastIndexOf(46) ? str2.replace(".", "").replace(',', '.') : str2.replace(",", "");
            } else {
                str2 = str2.replace(',', '.');
            }

            try {
                return Double.parseDouble(str2);
            } catch (NumberFormatException ignored) {
            }
        }

        handleString("§8[§4B§8] §r", "\"Failed to parse gold message: \"" + str);
        return -99999.0;
    }

    public static void handleString5(String str) {
        var str2 = String.valueOf(EnumChatFormatting.RED);
        EnumChatFormatting RED = EnumChatFormatting.RED;
        EnumChatFormatting BOLD = EnumChatFormatting.BOLD;
        handleString("§8[§4B§8] §r", str2 + BOLD + "ERROR! " + RED + str);
    }

    public static String computeString7(String str) {
        return str.replaceAll(lookupMap.getOrDefault("t52343f12312451g12cp211", "a"), lookupMap.getOrDefault("t52343f12312451g12cp2122221", "5"));
    }

    public static double computeDouble(String str) {
        if (computeInt(str) == 0 && computeInt2(str) == 0) {
            return 0.0;
        }
        Matcher matcher = Pattern.compile("\\+(\\d+(?:[.,]\\d+)?)\\s*XP").matcher(str);
        return matcher.find() ? Double.parseDouble(matcher.group(1).replace(",", ".")) : 0.0;
    }

    public static boolean matchesString(String str) {
        if (str == null) {
            return false;
        }
        String str2 = StringUtils.stripControlCodes(str).trim();
        return matchesString2(str2) && str2.contains("XP");
    }

    public static void handleString3(String str) {
        handleString("§8[§4B§8] §r", EnumChatFormatting.GRAY + lookupMap.get("t52343f12312451g12cp1") + str + lookupMap.get("t52343f12312451g12cp2"));
    }

    public static void handleString7(String str) {
        handleString("§8[§4B§8] §r", str);
    }

    public static void handleString(String str, EnumChatFormatting enumChatFormatting) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(str).setChatStyle(new ChatStyle().setColor(enumChatFormatting)));
        }
    }
}
