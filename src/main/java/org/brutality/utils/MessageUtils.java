package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class MessageUtils {
    public static final String text = EnumChatFormatting.BLACK.toString();
    public static final String text2 = EnumChatFormatting.DARK_BLUE.toString();
    public static final String text3 = EnumChatFormatting.DARK_GREEN.toString();
    public static final String text4 = EnumChatFormatting.DARK_AQUA.toString();
    public static final String text5 = EnumChatFormatting.DARK_RED.toString();
    public static final String text6 = EnumChatFormatting.DARK_PURPLE.toString();
    public static final String text7 = EnumChatFormatting.GOLD.toString();
    public static final String text8 = EnumChatFormatting.GRAY.toString();
    public static final String text9 = EnumChatFormatting.DARK_GRAY.toString();
    public static final String text10 = EnumChatFormatting.BLUE.toString();
    public static final String text11 = EnumChatFormatting.GREEN.toString();
    public static final String text12 = EnumChatFormatting.AQUA.toString();
    public static final String text13 = EnumChatFormatting.RED.toString();
    public static final String text14 = EnumChatFormatting.LIGHT_PURPLE.toString();
    public static final String text15 = EnumChatFormatting.YELLOW.toString();
    public static final String text16 = EnumChatFormatting.WHITE.toString();
    public static final String text17 = EnumChatFormatting.OBFUSCATED.toString();
    public static final String text18 = EnumChatFormatting.BOLD.toString();
    public static final String text19 = EnumChatFormatting.STRIKETHROUGH.toString();
    public static final String text20 = EnumChatFormatting.UNDERLINE.toString();
    public static final String text21 = EnumChatFormatting.ITALIC.toString();
    public static final String text22 = EnumChatFormatting.RESET.toString();
    public static final String text23;

    public static void x(String message) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) {
            EntityPlayerSP player = mc.thePlayer;
            if (player != null) {
                Runnable task = () -> {
                    if (mc.thePlayer == player) {
                        player.addChatMessage(new ChatComponentText(text23 + message));
                    }
                };
                if (mc.isCallingFromMinecraftThread()) {
                    task.run();
                } else {
                    mc.addScheduledTask(task);
                }
            }
        }
    }

    public static String computeString(String message, String color) {
        return color + message + text22;
    }

    public static String computeString3(String message) {
        return text18 + message + text22;
    }

    public static String computeString5(String message) {
        return text21 + message + text22;
    }

    public static String computeString4(String message) {
        return text20 + message + text22;
    }

    public static String computeString(String message) {
        return text19 + message + text22;
    }

    public static String computeString6(String message) {
        return text17 + message + text22;
    }

    public static String computeString(String message, String... formats) {
        StringBuilder sb = new StringBuilder();

        for (String str : formats) {
            sb.append(str);
        }

        sb.append(message).append(text22);
        return sb.toString();
    }

    public static void y(String message) {
        if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().getNetHandler() != null) {
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C01PacketChatMessage(message));
        }
    }

    public static String computeString2(String message) {
        return text23 + message;
    }

    static {
        String str = text8;
        String str2 = text9;
        String str3 = text5;
        text23 = text9 + "[" + str3 + "B" + str2 + "] " + str + "- ";
    }
}
