package org.brutality.settings.impl;

import java.util.ArrayList;
import java.util.List;
import org.brutality.module.Module;
import org.brutality.service.itemsearch.IsVisibleApi;
import org.brutality.settings.Setting;

public class KeybindListSetting extends Setting {
    private List<Setting> entries = new ArrayList<>();

    public KeybindListSetting(String name, Module parent, Setting... settings) {
        super(name, parent);

        for (Setting setting : settings) {
            this.entries.add(setting);
        }
    }

    public void handleSetting(Setting setting) {
        this.entries.add(setting);
    }

    public List<Setting> getEntries() {
        return this.entries;
    }

    public Setting computeSetting(String name) {
        for (Setting setting : this.entries) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }

        return null;
    }

    public KeybindListSetting computeKeybindListSetting(IsVisibleApi visibility) {
        this.setConditionalVisibility(visibility);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Setting setting : this.entries) {
            sb.append(setting.getName()).append(": ").append(setting.toString()).append(", ");
        }

        return sb.toString();
    }
}
