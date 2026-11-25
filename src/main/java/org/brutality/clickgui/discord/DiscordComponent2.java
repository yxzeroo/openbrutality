package org.brutality.clickgui.discord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.brutality.Brutality;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ColorThemeSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.MultiBoolSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;

public final class DiscordComponent2 {
    private final DiscordGUI discordGUI;
    private final Map<Module, List<IsVisibleComponent>> lookupMap = new LinkedHashMap<>();

    public List<IsVisibleComponent> computeList2(Module module) {
        if (module == null) {
            return Collections.emptyList();
        }
        Object obj = this.lookupMap.get(module);
        if (obj == null) {
            obj = new ArrayList();

            for (Setting setting : Brutality.settingsManager.computeList2(module)) {
                this.handleSetting(setting, (List<IsVisibleComponent>)obj, null);
            }

            this.lookupMap.put(module, (List<IsVisibleComponent>)obj);
        }

        return (List<IsVisibleComponent>)obj;
    }

    public DiscordComponent2(DiscordGUI discordGUI) {
        this.discordGUI = discordGUI;
    }

    private static GroupType computeGroupType(Setting setting) {
        if (setting instanceof BooleanSetting) {
            return GroupType.CHECKBOX;
        }
        if (setting instanceof ModeSetting) {
            return GroupType.COMBOBOX;
        }
        if (setting instanceof MultiBoolSetting) {
            return GroupType.SELECTBOX;
        }
        if (setting instanceof NumberSetting) {
            return GroupType.SLIDER;
        }
        if (setting instanceof StringSetting) {
            return GroupType.TEXTBOX;
        }
        if (setting instanceof ColorThemeSetting) {
            return GroupType.COLOR_PICKER;
        }
        return setting instanceof ColorSetting ? GroupType.COLOR_PICKER : null;
    }

    public List<IsVisibleComponent> computeList() {
        ArrayList list = new ArrayList();

        for (List list2 : this.lookupMap.values()) {
            list.addAll(list2);
        }

        return list;
    }

    public List<IsVisibleComponent> computeList(Module module) {
        ArrayList list = new ArrayList();

        for (IsVisibleComponent isVisibleComponent : this.computeList2(module)) {
            if (isVisibleComponent.isVisible()) {
                list.add(isVisibleComponent);
            }
        }

        return list;
    }

    public void clear() {
        this.lookupMap.clear();
    }

    private void handleSetting(Setting setting, List<IsVisibleComponent> isVisibleComponents, IsVisibleComponent isVisibleComponent) {
        if (!(setting instanceof KeybindListSetting)) {
            GroupType groupType = computeGroupType(setting);
            if (groupType != null) {
                IsVisibleComponent isVisibleComponent2 = new IsVisibleComponent(this.discordGUI, setting, groupType);
                isVisibleComponent2.setIsVisibleComponent(isVisibleComponent);
                isVisibleComponents.add(isVisibleComponent2);
            }
        } else {
            IsVisibleComponent isVisibleComponent3 = new IsVisibleComponent(this.discordGUI, setting, GroupType.GROUP);
            isVisibleComponent3.setIsVisibleComponent(isVisibleComponent);
            isVisibleComponents.add(isVisibleComponent3);

            for (Setting setting2 : ((KeybindListSetting)setting).getEntries()) {
                this.handleSetting(setting2, isVisibleComponents, isVisibleComponent3);
            }
        }
    }
}
