package org.brutality.utils.render;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.brutality.module.Module;
import org.brutality.module.impl.client.ClickGUI;
import org.brutality.settings.impl.ModeSetting;

public final class ModuleSpecificHelper {
    public static final String text = "Module Specific";
    public static final String text2 = "Theme";
    private static final List<String> entries = Arrays.asList("Module Specific", "Theme");

    public static int computeInt(BrutalityHelper brutalityHelper, Function<BrutalityHelper, Color> fn, int n) {
        if (brutalityHelper == null) {
            return computeInt(Color.WHITE, n);
        }
        Color color = (Color)fn.apply(brutalityHelper);
        return computeInt(color != null ? color : Color.WHITE, n);
    }

    public static ModeSetting computeModeSetting(Module module) {
        return new ModeSetting("Theme", module, "Module Specific", entries);
    }

    public static BrutalityHelper computeBrutalityHelper(ModeSetting modeSetting) {
        if (modeSetting == null) {
            return null;
        }
        return modeSetting.matchesString2("Module Specific") ? null : computeBrutalityHelper();
    }

    public static boolean matchesModeSetting(ModeSetting modeSetting) {
        return modeSetting != null && modeSetting.matchesString2("Theme");
    }

    public static BrutalityHelper computeBrutalityHelper() {
        ClickGUI instance = ClickGUI.instance;
        if (instance != null) {
            BrutalityHelper brutalityHelper = instance.computeBrutalityHelper();
            if (brutalityHelper != null) {
                return brutalityHelper;
            }
        }

        return BrutalityHelper.getInstance();
    }

    public static int computeInt(Color color, int n) {
        int n2 = Math.max(0, Math.min(255, n));
        return n2 << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
    }

    private ModuleSpecificHelper() {
    }
}
