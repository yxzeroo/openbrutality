package org.brutality.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.brutality.module.Module;

public class SettingsManager {
    private List<Setting> entries = new ArrayList<>();
    private Map<Module, List<Setting>> lookupMap = new HashMap<>();
    private final Map<Module, List<Setting>> lookupMap2 = new HashMap<>();
    private final Map<Module, List<Setting>> lookupMap3 = new HashMap<>();

    public void addSetting(Setting setting) {
        if (setting != null) {
            this.entries.add(setting);
            Module parent = setting.getParent();
            if (parent != null) {
                List list = this.lookupMap.computeIfAbsent(parent, module -> new ArrayList<>());
                this.lookupMap3.computeIfAbsent(parent, module2 -> Collections.unmodifiableList(list));
                list.add(setting);
                this.lookupMap2.remove(parent);
            }
        }
    }

    public Setting computeSetting(Module module, String str) {
        if (module != null && str != null) {
            for (Setting setting : this.computeList(module)) {
                if (setting.getName().equalsIgnoreCase(str)) {
                    return setting;
                }
            }

            return null;
        }
        return null;
    }

    public List<Setting> computeList(Module module) {
        return module == null ? Collections.emptyList() : this.lookupMap3.getOrDefault(module, Collections.emptyList());
    }

    public List<Setting> computeList2(Module module) {
        return module == null
            ? Collections.emptyList()
            : this.lookupMap2.computeIfAbsent(module, module2 -> OrganizeHandler.organize(module2, this.computeList(module2)));
    }

    public void handleSetting(Setting setting) {
        if (setting != null) {
            this.entries.remove(setting);
            Module parent = setting.getParent();
            if (parent != null) {
                List list = this.lookupMap.get(parent);
                if (list != null) {
                    list.remove(setting);
                }

                this.lookupMap2.remove(parent);
            }
        }
    }

    public boolean hasSetting(String str) {
        return str == null ? false : this.entries.stream().anyMatch(setting -> setting.getName().equalsIgnoreCase(str));
    }

    public void handleSettingArray(Setting... settings) {
        for (Setting setting : settings) {
            this.addSetting(setting);
        }
    }

    public Setting computeSetting(String str) {
        if (str == null) {
            return null;
        }
        for (Setting setting : this.entries) {
            if (setting.getName().equalsIgnoreCase(str)) {
                return setting;
            }
        }

        return null;
    }
}
