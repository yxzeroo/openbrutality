package org.brutality.module.impl.visual;

import net.minecraft.client.gui.Gui;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;

public final class Brightness extends Module {
    private final NumberSetting opacitySetting = new NumberSetting("Opacity", this, 100.0, 0.0, 100.0, 1.0);
    private float factor;

    @Override
    public void onEnable() {
        this.factor = mc.gameSettings.gammaSetting;
        super.onEnable();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                mc.gameSettings.gammaSetting = 100.0F;
            } else if (event instanceof EventRender2D) {
                int n = (int)Math.round((100.0 - this.opacitySetting.computeDouble()) * 2.55);
                if (n > 0) {
                    Gui.drawRect(
                        0,
                        0,
                        ((EventRender2D)event).getScaledResolution().getScaledWidth(),
                        ((EventRender2D)event).getScaledResolution().getScaledHeight(),
                        n << 24
                    );
                }
            }
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = this.factor;
        super.onDisable();
    }

    public Brightness() {
        super("Brightness", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.opacitySetting});
    }
}
