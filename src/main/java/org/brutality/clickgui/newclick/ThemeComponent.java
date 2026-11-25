package org.brutality.clickgui.newclick;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.brutality.Brutality;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;

public final class ThemeComponent {
    public static List<Setting> computeList(Module module) {
        return module != null && Brutality.settingsManager != null
            ? Brutality.settingsManager.computeList(module).stream().filter(ThemeComponent::matchesSetting).collect(Collectors.toList())
            : Collections.emptyList();
    }

    public static boolean matchesSetting(Setting setting) {
        if (setting != null && setting.isVisible()) {
            if (setting instanceof KeybindListSetting) {
                for (Setting setting2 : ((KeybindListSetting)setting).getEntries()) {
                    if (matchesSetting(setting2)) {
                        return true;
                    }
                }

                return false;
            }
            return !(setting instanceof ModeSetting modeSetting && matchesModeSetting(modeSetting));
        }
        return false;
    }

    public static boolean matchesSetting(Setting setting, Module module) {
        return matchesSetting(setting);
    }

    private static boolean matchesModeSetting(ModeSetting modeSetting) {
        if (!"Theme".equals(modeSetting.getName())) {
            return false;
        }
        List list = modeSetting.computeList2();
        return list != null && list.contains("Theme");
    }

    private ThemeComponent() {
    }
}
