package org.brutality.utils.input;

import java.util.Locale;
import org.lwjgl.input.Keyboard;

public final class KeyMouseHelper {
    public static Integer computeInteger(String str) {
        if (str == null) {
            return null;
        }
        String str2 = str.trim();

        try {
            return Integer.valueOf(str2);
        } catch (NumberFormatException e) {
            if (str2.startsWith("key.mouse.")) {
                String str3 = str2.substring("key.mouse.".length());
                if (str3.equals("left")) {
                    return -100;
                }
                if (str3.equals("right")) {
                    return -99;
                }
                if (str3.equals("middle")) {
                    return -98;
                }
                try {
                    int n = Integer.parseInt(str3);
                    return n >= 1 && n <= 16 ? n - 101 : null;
                } catch (NumberFormatException e2) {
                    return null;
                }
            } else if (!str2.startsWith("key.keyboard.")) {
                return null;
            } else {
                String str4 = str2.substring("key.keyboard.".length());
                if (str4.equals("unknown")) {
                    return 0;
                }
                int keyIndex = Keyboard.getKeyIndex(switch (str4) {
                    case "left.control" -> "LCONTROL";
                    case "right.control" -> "RCONTROL";
                    case "left.shift" -> "LSHIFT";
                    case "right.shift" -> "RSHIFT";
                    case "left.alt" -> "LMENU";
                    case "right.alt" -> "RMENU";
                    case "left.win", "left.super" -> "LMETA";
                    case "right.win", "right.super" -> "RMETA";
                    case "enter" -> "RETURN";
                    case "backspace" -> "BACK";
                    case "page.up" -> "PRIOR";
                    case "page.down" -> "NEXT";
                    case "caps.lock" -> "CAPITAL";
                    case "num.lock" -> "NUMLOCK";
                    case "scroll.lock" -> "SCROLL";
                    case "print.screen" -> "SYSRQ";
                    case "menu" -> "APPS";
                    case "equal" -> "EQUALS";
                    case "left.bracket" -> "LBRACKET";
                    case "right.bracket" -> "RBRACKET";
                    case "apostrophe" -> "APOSTROPHE";
                    case "grave.accent" -> "GRAVE";
                    case "keypad.enter" -> "NUMPADENTER";
                    case "keypad.equal" -> "NUMPADEQUALS";
                    case "keypad.decimal" -> "DECIMAL";
                    case "keypad.add" -> "ADD";
                    case "keypad.subtract" -> "SUBTRACT";
                    case "keypad.multiply" -> "MULTIPLY";
                    case "keypad.divide" -> "DIVIDE";
                    default -> str4.matches("keypad\\.[0-9]") ? "NUMPAD" + str4.charAt(str4.length() - 1) : str4.toUpperCase(Locale.ROOT);
                });
                return keyIndex == 0 ? null : keyIndex;
            }
        }
    }

    private KeyMouseHelper() {
    }
}
