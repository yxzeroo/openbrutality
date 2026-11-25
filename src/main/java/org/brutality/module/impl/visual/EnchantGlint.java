package org.brutality.module.impl.visual;

import java.awt.Color;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.ModuleSpecificHelper;

public class EnchantGlint extends Module {
    private final BooleanSetting syncThemeSetting = new BooleanSetting("Sync Theme", this, false);
    public final ColorSetting colorSetting = new ColorSetting("Color", this, 0.85F).computeColorSetting(() -> !this.syncThemeSetting.isEnabled());
    private BrutalityHelper brutalityHelper;
    private static final long timestamp = 3000L;

    public Color computeColor(float f) {
        this.process();
        if (!this.syncThemeSetting.isEnabled() || this.brutalityHelper == null) {
            return this.colorSetting.computeColor();
        }
        return f != 0.0F
            ? GetColorHelper.computeColor(15, (int)(f * 360.0F), this.brutalityHelper.getColor(), this.brutalityHelper.getColor2(), false)
            : this.brutalityHelper.getColor();
    }

    public boolean checkState() {
        return this.syncThemeSetting.isEnabled();
    }

    private void process() {
        this.brutalityHelper = this.syncThemeSetting.isEnabled() ? ModuleSpecificHelper.computeBrutalityHelper() : null;
    }

    public Color computeColor() {
        return this.computeColor(0.0F);
    }

    public EnchantGlint() {
        super("Enchant Glint", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.syncThemeSetting, this.colorSetting});
        this.process();
    }

    public boolean checkState2() {
        return !this.checkState();
    }

    public Color computeColor2() {
        return this.computeColor(System.currentTimeMillis());
    }

    public Color computeColor(long l) {
        float f = (float)(l % timestamp) / 3000.0F;
        return this.computeColor(f * 360.0F);
    }

    public BrutalityHelper getBrutalityHelper() {
        return this.brutalityHelper;
    }

    public Color computeColor(EnchantGlint.PrimaryMode primaryMode) {
        return this.computeColor(primaryMode, 0);
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

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    public static enum PrimaryMode {
        PRIMARY,
        SECONDARY,
        ACCENT,
        GRADIENT;

        private static EnchantGlint.PrimaryMode[] computePrimaryModeArray() {
            return new EnchantGlint.PrimaryMode[]{PRIMARY, SECONDARY, ACCENT, GRADIENT};
        }
    }
}
