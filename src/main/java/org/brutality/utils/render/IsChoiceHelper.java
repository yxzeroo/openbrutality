package org.brutality.utils.render;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.brutality.module.Module;
import org.brutality.settings.impl.ModeSetting;

public final class IsChoiceHelper {
    public static final List<String> entries = Collections.unmodifiableList(Arrays.asList("Quan Pro", "Brutality", "Arial", "Minecraft"));
    public static final String text = "Brutality";

    private IsChoiceHelper() {
    }

    public static boolean isChoice(String str) {
        return str != null && entries.stream().anyMatch(str2 -> str2.equalsIgnoreCase(str));
    }

    public static String computeString(String str) {
        String str2 = normalize(str);
        switch (str2) {
            case "Minecraft":
                return "minecraft";
            case "Quan Pro":
                return "quan-pro-regular.otf";
            case "Arial":
                return "arial.ttf";
            default:
                return "brutality.ttf";
        }
    }

    public static boolean matchesString(String str) {
        return "Minecraft".equals(normalize(str));
    }

    public static ModeSetting computeModeSetting(String str, Module module) {
        return computeModeSetting(str, module, "Brutality");
    }

    public static String normalize(String str) {
        String str2 = str == null ? "" : str.toLowerCase(Locale.ROOT).replace(".ttf", "").replace(".otf", "").replaceAll("[ _-]", "");
        switch (str2) {
            case "minecraft":
            case "vanilla":
                return "Minecraft";
            case "quanpro":
            case "quanproregular":
                return "Quan Pro";
            case "sf":
            case "sfpro":
            case "sfregular":
            case "sfthin":
            case "sfbold":
            case "sanfrancisco":
                return "Brutality";
            case "arial":
                return "Arial";
            case "tahoma":
                return "Brutality";
            default:
                return "Brutality";
        }
    }

    public static ModeSetting computeModeSetting(String str, Module module, String str2) {
        return new IsChoiceHelper.Entry(str, module, normalize(str2));
    }

    public static final class Entry extends ModeSetting {
        public Entry(String str, Module module, String str2) {
            super(str, module, str2, IsChoiceHelper.entries);
        }

        @Override
        public boolean matchesString(String str) {
            return str != null && !str.trim().isEmpty();
        }

        @Override
        public void handleString(String str) {
            if (this.matchesString(str)) {
                super.handleString(IsChoiceHelper.normalize(str));
            }
        }
    }
}
