package org.brutality.util.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.util.ExecuteCommandHelper;
import org.lwjgl.input.Keyboard;

public class MOUSEHelper {
    private static final int count = 1;
    private static final int count2 = 16;
    private static final Map<String, String> lookupMap = new MOUSEHelper.Entry();

    public static String computeString2(int n) {
        if (n >= -1) {
            return Keyboard.getKeyName(n);
        }
        if (n >= -100 && n <= -85) {
            return "MOUSE" + (n + 101);
        }
        throw new IllegalArgumentException("Invalid mouse button code, has to be between -100 and -85: " + n);
    }

    public static boolean matchesKeyBinding2(KeyBinding keyBinding) {
        return matchesKeyBinding(keyBinding);
    }

    public static List<String> computeList4() {
        return computeList();
    }

    public static boolean matchesKeyBinding(KeyBinding keyBinding) {
        try {
            keyBinding.pressTime = 0;
            keyBinding.pressed = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int computeInt(String str) {
        String str2 = str.toUpperCase();
        if (str2.startsWith("MOUSE")) {
            try {
                int n = Integer.parseInt(str.substring(5));
                if (n >= 1 && n <= 16) {
                    return n - 101;
                }

                EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
                EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
                EnumChatFormatting YELLOW2 = EnumChatFormatting.YELLOW;
                ExecuteCommandHelper.handleString5("Mouse button names must between " + YELLOW2 + "MOUSE1" + WHITE + " and " + YELLOW + "MOUSE16");
                throw new IllegalArgumentException("Invalid mouse button code, has to be between 1 and 16: " + n);
            } catch (NumberFormatException e) {
                ExecuteCommandHelper.handleString5("Invalid key name: " + str);
            }
        }

        String orDefault = lookupMap.getOrDefault(str2, str2);
        return Keyboard.getKeyIndex(orDefault);
    }

    public static String computeString(int n) {
        return computeString2(n);
    }

    public static List<String> computeList3() {
        return computeList2();
    }

    public static int computeInt2(String str) {
        return computeInt(str);
    }

    public static List<String> computeList2() {
        ArrayList list = new ArrayList();

        for (int i = 1; i <= 16; i++) {
            list.add("MOUSE" + i);
        }

        return list;
    }

    public static List<String> computeList() {
        List list = computeList2();

        for (int i = 0; i < Keyboard.getKeyCount(); i++) {
            String keyName = Keyboard.getKeyName(i);
            if (keyName != null) {
                list.add(keyName);
            }
        }

        return list;
    }

    public static final class Entry extends HashMap<String, String> {
        public Entry() {
            this.put("LCTRL", "LCONTROL");
            this.put("RCTRL", "RCONTROL");
            this.put("SHIFT", "LSHIFT");
            this.put(";", "SEMICOLON");
            this.put("'", "APOSTROPHE");
            this.put(",", "COMMA");
            this.put(".", "PERIOD");
            this.put("/", "SLASH");
            this.put("\\", "BACKSLASH");
            this.put("[", "LBRACKET");
            this.put("]", "RBRACKET");
            this.put("`", "GRAVE");
            this.put("-", "MINUS");
            this.put("=", "EQUALS");
        }
    }
}
