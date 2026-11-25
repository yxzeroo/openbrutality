package org.brutality.module.impl.misc;

import java.awt.Color;
import java.util.Arrays;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.EventChat;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.StringSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class ChatColorizer extends Module {
    private final ModeSetting randomSetting = new ModeSetting(
        "Color Mode",
        this,
        "Custom",
        Arrays.asList(
            "Custom",
            "Black",
            "Dark Blue",
            "Dark Green",
            "Dark Aqua",
            "Dark Red",
            "Dark Purple",
            "Gold",
            "Gray",
            "Dark Gray",
            "Blue",
            "Green",
            "Aqua",
            "Red",
            "Light Purple",
            "Yellow",
            "White",
            "Random"
        )
    );
    private final ColorSetting customColorSetting = new ColorSetting("Custom Color", this, 240.0F);
    private final BooleanSetting prefixOnlySetting = new BooleanSetting("Prefix Only", this, false);
    private final StringSetting stringSetting = new StringSetting("Prefix", this, "[");
    private final StringSetting stringSetting2 = new StringSetting("Suffix", this, "]");
    private final BooleanSetting randomPerMessageSetting = new BooleanSetting("Random Per Message", this, false);
    private final BooleanSetting rainbowModeSetting = new BooleanSetting("Rainbow Mode", this, false);
    private static final String[] ms = new String[]{"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f"};
    private static final String[] stringArray = new String[]{
        "Black",
        "Dark Blue",
        "Dark Green",
        "Dark Aqua",
        "Dark Red",
        "Dark Purple",
        "Gold",
        "Gray",
        "Dark Gray",
        "Blue",
        "Green",
        "Aqua",
        "Red",
        "Light Purple",
        "Yellow",
        "White"
    };
    private static final long timestamp = 10000L;

    private void onEventChat(EventChat event) {
        String message = event.getMessage();
        if (!message.startsWith("/")) {
            String str = this.computeString(message);
            event.setMessage(str);
        }
    }

    private String computeString2() {
        String text = this.randomSetting.getText();
        switch (text) {
            case "Custom":
                return this.computeString3();
            case "Random":
                if (this.randomPerMessageSetting.isEnabled()) {
                    return ms[(int)(Math.random() * ms.length)];
                }

                return ms[(int)(System.currentTimeMillis() / timestamp % ms.length)];
            default:
                for (int i = 0; i < stringArray.length; i++) {
                    if (stringArray[i].equals(text)) {
                        return ms[i];
                    }
                }

                return "&f";
        }
    }

    public String computeString() {
        return this.computeString2();
    }

    private double computeDouble(Color color, Color color2) {
        int n = color.getRed() - color2.getRed();
        int n2 = color.getGreen() - color2.getGreen();
        int n3 = color.getBlue() - color2.getBlue();
        return Math.sqrt(n * n + n2 * n2 + n3 * n3);
    }

    private String computeString3() {
        Color color = this.customColorSetting.computeColor();
        return this.computeString(color);
    }

    private String computeString(Color color) {
        Color[] colors = new Color[]{
            new Color(0, 0, 0),
            new Color(0, 0, 170),
            new Color(0, 170, 0),
            new Color(0, 170, 170),
            new Color(170, 0, 0),
            new Color(170, 0, 170),
            new Color(255, 170, 0),
            new Color(170, 170, 170),
            new Color(85, 85, 85),
            new Color(85, 85, 255),
            new Color(85, 255, 85),
            new Color(85, 255, 255),
            new Color(255, 85, 85),
            new Color(255, 85, 255),
            new Color(255, 255, 85),
            new Color(255, 255, 255)
        };
        double MAX_VALUE = Double.MAX_VALUE;
        int n = 0;

        for (int i = 0; i < colors.length; i++) {
            double d = this.computeDouble(color, colors[i]);
            if (d < MAX_VALUE) {
                MAX_VALUE = d;
                n = i;
            }
        }

        return ms[n];
    }

    public Color computeColor() {
        String str = this.computeString();
        switch (str) {
            case "&0":
                return new Color(0, 0, 0);
            case "&1":
                return new Color(0, 0, 170);
            case "&2":
                return new Color(0, 170, 0);
            case "&3":
                return new Color(0, 170, 170);
            case "&4":
                return new Color(170, 0, 0);
            case "&5":
                return new Color(170, 0, 170);
            case "&6":
                return new Color(255, 170, 0);
            case "&7":
                return new Color(170, 170, 170);
            case "&8":
                return new Color(85, 85, 85);
            case "&9":
                return new Color(85, 85, 255);
            case "&a":
                return new Color(85, 255, 85);
            case "&b":
                return new Color(85, 255, 255);
            case "&c":
                return new Color(255, 85, 85);
            case "&d":
                return new Color(255, 85, 255);
            case "&e":
                return new Color(255, 255, 85);
            case "&f":
                return new Color(255, 255, 255);
            default:
                return Color.WHITE;
        }
    }

    public ChatColorizer() {
        super("Chat Colorizer", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(
            new Setting[]{
                this.randomSetting,
                this.customColorSetting,
                this.prefixOnlySetting,
                this.stringSetting,
                this.stringSetting2,
                this.randomPerMessageSetting,
                this.rainbowModeSetting
            }
        );
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventChat) {
                    this.onEventChat((EventChat)event);
                }
            }
        }
    }

    private String computeString(String str) {
        if (this.rainbowModeSetting.isEnabled()) {
            return this.computeString2(str);
        }
        String str2 = this.computeString2();
        return this.prefixOnlySetting.isEnabled() ? str2 + this.stringSetting.getValue() + str + this.stringSetting2.getValue() : str2 + str;
    }

    private String computeString2(String str) {
        if (str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        int n = 0;
        int[] ints = new int[]{9, 10, 11, 12, 13, 14};

        for (char ch : str.toCharArray()) {
            if (ch != ' ') {
                sb.append(ms[ints[n % ints.length]]);
                n++;
            }

            sb.append(ch);
        }

        return sb.toString();
    }
}
