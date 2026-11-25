package org.brutality.module.impl.visual;

import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;

public class AspectRatio extends Module {
    public static AspectRatio instance;
    private final NumberSetting ratioSetting = new NumberSetting("Ratio", this, 1.0, 0.5, 2.0, 0.01);

    public static float computeFloat(float f) {
        return instance != null && instance.checkState44() ? f * (float)instance.ratioSetting.computeDouble() : f;
    }

    public AspectRatio() {
        super("Aspect Ratio", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.ratioSetting});
        instance = this;
    }

    @Override
    public void onEvent(Event event) {
    }
}
