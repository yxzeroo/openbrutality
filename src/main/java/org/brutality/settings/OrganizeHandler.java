package org.brutality.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.brutality.module.Module;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.module.impl.pit.hypixel.automation.AutoHunt;
import org.brutality.service.itemsearch.SectionHandler;
import org.brutality.settings.impl.KeybindListSetting;

public final class OrganizeHandler {
    private OrganizeHandler() {
    }

    private static boolean matchesKeybindListSetting(KeybindListSetting keybindListSetting) {
        for (Setting setting : keybindListSetting.getEntries()) {
            if (setting.isVisible()) {
                return true;
            }
        }

        return false;
    }

    private static int computeInt(List<Setting> settings) {
        int n = 0;

        for (Setting setting : settings) {
            n += setting instanceof KeybindListSetting ? computeInt(((KeybindListSetting)setting).getEntries()) : 1;
        }

        return n;
    }

    public static List<Setting> organize(Module module, List<Setting> settings) {
        if (computeInt(settings) < 10) {
            return settings;
        }
        Class clazz = module.getClass();
        LinkedHashMap map = new LinkedHashMap();
        ArrayList list = new ArrayList();

        for (Setting setting : settings) {
            if (setting instanceof KeybindListSetting keybindListSetting2) {
                if (clazz != AutoGrinder.class && clazz != AutoHunt.class || !keybindListSetting2.getName().equals("Combat") && !keybindListSetting2.getName().equals("Movement")) {
                    list.add(setting);
                } else {
                    KeybindListSetting keybindListSetting = new KeybindListSetting(keybindListSetting2.getName(), module);
                    keybindListSetting.computeKeybindListSetting(() -> keybindListSetting2.isVisible() && matchesKeybindListSetting(keybindListSetting));
                    list.add(keybindListSetting);

                    for (Setting setting2 : keybindListSetting2.getEntries()) {
                        if ("Rotations".equals(SectionHandler.computeString(clazz, setting2.getName()))) {
                            handleModule(module, "Rotations", setting2, keybindListSetting2, map, list);
                        } else {
                            keybindListSetting.handleSetting(setting2);
                        }
                    }
                }
            } else {
                String str = SectionHandler.computeString(clazz, setting.getName());
                if (str == null) {
                    list.add(setting);
                } else {
                    handleModule(module, str, setting, null, map, list);
                }
            }
        }

        return Collections.unmodifiableList(list);
    }

    private static void handleModule(Module module, String str, Setting setting, KeybindListSetting keybindListSetting, Map<String, KeybindListSetting> map, List<Setting> settings) {
        var keybindListSetting2 = (KeybindListSetting)map.get(str);
        if (keybindListSetting2 == null) {
            keybindListSetting2 = new OrganizeHandler.Entry(str, module);
            map.put(str, keybindListSetting2);
            settings.add(keybindListSetting2);
        }

        ((OrganizeHandler.Entry)keybindListSetting2).handleSetting(setting, keybindListSetting);
    }

    public static final class Entry extends KeybindListSetting {
        private final Map<Setting, KeybindListSetting> lookupMap = new IdentityHashMap<>();

        public Entry(String str, Module module) {
            super(str, module);
            this.computeKeybindListSetting(() -> {
                for (Setting setting : this.getEntries()) {
                    KeybindListSetting keybindListSetting = this.lookupMap.get(setting);
                    if (setting.isVisible() && (keybindListSetting == null || keybindListSetting.isVisible())) {
                        return true;
                    }
                }

                return false;
            });
        }

        public void handleSetting(Setting setting, KeybindListSetting keybindListSetting) {
            this.handleSetting(setting);
            this.lookupMap.put(setting, keybindListSetting);
        }
    }
}
