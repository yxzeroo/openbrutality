package org.brutality.module.impl.visual;

import java.awt.Color;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.render.GetStateEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.wings.WingBoneHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.GlStateGuard;

public class DragonWings extends Module {
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.25, 1.25, 0.25);
    private final BooleanSetting defaultColorSetting = new BooleanSetting("Default Color", this, true);
    private final BooleanSetting syncThemeSetting = new BooleanSetting("Sync Theme", this, false);
    private final ColorSetting customColorSetting = new ColorSetting("Custom Color", this, 0.0F)
        .computeColorSetting(() -> !this.defaultColorSetting.isEnabled() && !this.syncThemeSetting.isEnabled());
    private final WingBoneHandler wingBoneHandler = new WingBoneHandler();
    private BrutalityHelper brutalityHelper;
    private static final long timestamp = 15L;

    public DragonWings() {
        super("Dragon Wings", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.scaleSetting, this.defaultColorSetting, this.syncThemeSetting, this.customColorSetting});
        this.process();
    }

    private void process() {
        this.brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
    }

    private Color computeColor() {
        if (this.defaultColorSetting.isEnabled()) {
            return Color.WHITE;
        }
        this.process();
        return this.syncThemeSetting.isEnabled() && this.brutalityHelper != null
            ? GetColorHelper.computeColor(
                15, (int)(System.currentTimeMillis() / timestamp) % 360, this.brutalityHelper.getColor(), this.brutalityHelper.getColor2(), false
            )
            : this.customColorSetting.computeColor();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetStateEvent getStateEvent && getStateEvent.getState() == EventState.POST && getStateEvent.getEntity() == mc.thePlayer && !mc.thePlayer.isInvisible()) {
                try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                    this.wingBoneHandler.handleEntityPlayer(mc.thePlayer, getStateEvent.getPartialTicks(), this.scaleSetting.computeDouble(), this.computeColor());
                }
            }
        }
    }

    public BrutalityHelper getBrutalityHelper() {
        return this.brutalityHelper;
    }
}
