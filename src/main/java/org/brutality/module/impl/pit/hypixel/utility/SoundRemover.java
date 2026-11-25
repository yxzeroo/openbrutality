package org.brutality.module.impl.pit.hypixel.utility;

import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;

public class SoundRemover extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final BooleanSetting perunSetting = new BooleanSetting("Perun", this, true);

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    public boolean matchesString(String str) {
        if (str == null) {
            return false;
        }
        String str2 = str.toLowerCase();
        return this.perunSetting.isEnabled() && (str2.contains("thunder") || str2.contains("lightning"));
    }

    public boolean checkState() {
        return this.perunSetting.isEnabled();
    }

    public SoundRemover() {
        super("Sound Remover", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.perunSetting});
    }
}
