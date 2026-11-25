package org.brutality.module.impl.visual;

import java.awt.Color;
import lombok.Generated;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.ModuleSpecificHelper;

public class Glint extends Module {
    private final ModeSetting glintSetting = ModuleSpecificHelper.computeModeSetting(this);
    public final ColorSetting colorSetting = new ColorSetting("Color", this, 0.85F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.glintSetting));
    private BrutalityHelper brutalityHelper;
    private static Glint instance;
    private static final long timestamp = 3000L;

    public boolean checkState2() {
        return ModuleSpecificHelper.matchesModeSetting(this.glintSetting);
    }

    private void process() {
        this.brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.glintSetting);
    }

    public Color computeColor(long l) {
        float f = (float)(l % timestamp) / 3000.0F;
        return this.computeColor(f * 360.0F);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    public BrutalityHelper getBrutalityHelper() {
        return this.brutalityHelper;
    }

    public Color computeColor() {
        return this.computeColor(System.currentTimeMillis());
    }

    public Color computeColor(EnchantGlint.PrimaryMode primaryMode, int n) {
        this.process();
        if (this.brutalityHelper == null) {
            return Color.WHITE;
        }
        switch (primaryMode) {
            case PRIMARY:
                if (n != 0) {
                    return GetColorHelper.computeColor(15, n, this.brutalityHelper.getColor(), this.brutalityHelper.getColor2(), false);
                }

                return this.brutalityHelper.getColor();
            case SECONDARY:
                if (n != 0) {
                    return GetColorHelper.computeColor(15, n, this.brutalityHelper.getColor2(), this.brutalityHelper.getColor3(), false);
                }

                return this.brutalityHelper.getColor2();
            case ACCENT:
                if (n != 0) {
                    return GetColorHelper.computeColor(15, n, this.brutalityHelper.getColor3(), this.brutalityHelper.getColor(), false);
                }

                return this.brutalityHelper.getColor3();
            case GRADIENT:
                return this.brutalityHelper.computeColor(n / 360.0F);
            default:
                return this.brutalityHelper.getColor();
        }
    }

    @Generated
    public static Glint getInstance() {
        return instance;
    }

    public Glint() {
        super("Glint", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.glintSetting, this.colorSetting});
        instance = this;
        this.process();
    }

    public Color computeColor(float f) {
        this.process();
        if (!ModuleSpecificHelper.matchesModeSetting(this.glintSetting) || this.brutalityHelper == null) {
            return this.colorSetting.computeColor();
        }
        return f != 0.0F
            ? GetColorHelper.computeColor(15, (int)(f * 360.0F), this.brutalityHelper.getColor(), this.brutalityHelper.getColor2(), false)
            : this.brutalityHelper.getColor();
    }

    public Color computeColor2() {
        return this.computeColor(0.0F);
    }

    public boolean checkState() {
        return !this.checkState2();
    }

    public Color computeColor(EnchantGlint.PrimaryMode primaryMode) {
        return this.computeColor(primaryMode, 0);
    }
}
