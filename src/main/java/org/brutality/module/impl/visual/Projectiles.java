package org.brutality.module.impl.visual;

import java.awt.Color;
import org.brutality.event.Event;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.ResetColorHelper;

public class Projectiles extends Module {
    private final ModeSetting projectilesSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final ColorSetting customColorSetting = new ColorSetting("Custom Color", this, 0.0F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.projectilesSetting));
    private BrutalityHelper brutalityHelper;
    private static final long timestamp = 12L;

    private Color computeColor() {
        this.process();
        return ModuleSpecificHelper.matchesModeSetting(this.projectilesSetting) && this.brutalityHelper != null
            ? GetColorHelper.computeColor(
                15, (int)(System.currentTimeMillis() / timestamp) % 360, this.brutalityHelper.getColor(), this.brutalityHelper.getColor2(), false
            )
            : this.customColorSetting.computeColor();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetPartialTicksEvent) {
                ResetColorHelper.handleInt2(this.computeColor().getRGB());
            }
        }
    }

    public Projectiles() {
        super("Projectiles", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.projectilesSetting, this.customColorSetting});
        this.process();
    }

    public BrutalityHelper getBrutalityHelper() {
        return this.brutalityHelper;
    }

    private void process() {
        this.brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.projectilesSetting);
    }
}
