package org.brutality.module.impl.visual;

import lombok.Generated;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;

public class PlayerHUD extends Module {
    public static PlayerHUD instance;
    public final BooleanSetting removeHungerSetting = new BooleanSetting("Remove Hunger", this, false);
    public final BooleanSetting removeArmorSetting = new BooleanSetting("Remove Armor", this, false);

    @Generated
    public static PlayerHUD getInstance() {
        return instance;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    public PlayerHUD() {
        super("PlayerHUD", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.removeHungerSetting, this.removeArmorSetting});
        instance = this;
    }
}
