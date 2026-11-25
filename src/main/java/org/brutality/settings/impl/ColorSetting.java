package org.brutality.settings.impl;

import java.awt.Color;
import org.brutality.module.Module;
import org.brutality.service.itemsearch.IsVisibleApi;
import org.brutality.settings.Setting;

public class ColorSetting extends Setting {
    private float factor = 0.0F;
    private final float factor2;
    private float saturation = 1.0F;
    private final float factor3;
    private float factor4 = 1.0F;
    private final float factor5;
    private float alpha2 = 1.0F;
    private final float factor6;
    private boolean open = false;

    public ColorSetting(String name, Module parent, float defaultHue) {
        this(name, parent, defaultHue, 1.0F, 1.0F, 1.0F);
    }

    public ColorSetting(String name, Module parent, float defaultHue, float defaultSaturation, float defaultBrightness) {
        this(name, parent, defaultHue, defaultSaturation, defaultBrightness, 1.0F);
    }

    public ColorSetting(String name, Module parent, float defaultHue, float defaultSaturation, float defaultBrightness, float defaultAlpha) {
        super(name, parent);
        this.factor2 = Math.max(0.0F, Math.min(1.0F, defaultHue));
        this.factor3 = Math.max(0.0F, Math.min(1.0F, defaultSaturation));
        this.factor5 = Math.max(0.0F, Math.min(1.0F, defaultBrightness));
        this.factor6 = Math.max(0.0F, Math.min(1.0F, defaultAlpha));
        this.factor = this.factor2;
        this.saturation = this.factor3;
        this.factor4 = this.factor5;
        this.alpha2 = this.factor6;
    }

    public float getFactor() {
        return this.factor;
    }

    public void handleFloat3(float hue) {
        this.factor = Math.max(0.0F, Math.min(1.0F, hue));
    }

    public float getFactor2() {
        return this.factor2;
    }

    public float getSaturation() {
        return this.saturation;
    }

    public void handleFloat4(float saturation) {
        this.saturation = Math.max(0.0F, Math.min(1.0F, saturation));
    }

    public float getFactor3() {
        return this.factor3;
    }

    public float getFactor4() {
        return this.factor4;
    }

    public void handleFloat2(float brightness) {
        this.factor4 = Math.max(0.0F, Math.min(1.0F, brightness));
    }

    public float getFactor5() {
        return this.factor5;
    }

    public float getAlpha() {
        return this.alpha2;
    }

    public void handleFloat(float alpha) {
        this.alpha2 = Math.max(0.0F, Math.min(1.0F, alpha));
    }

    public float getFactor6() {
        return this.factor6;
    }

    public Color computeColor() {
        Color hsbColor = Color.getHSBColor(this.factor, this.saturation, this.factor4);
        return new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int)(this.alpha2 * 255.0F));
    }

    public int computeInt() {
        return this.computeColor().getRGB();
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void r() {
        this.open = !this.open;
    }

    public void reset() {
        this.factor = this.factor2;
        this.saturation = this.factor3;
        this.factor4 = this.factor5;
        this.alpha2 = this.factor6;
    }

    public ColorSetting computeColorSetting(IsVisibleApi visibility) {
        this.setConditionalVisibility(visibility);
        return this;
    }

    public static float computeFloat(int x, int barWidth) {
        return Math.max(0.0F, Math.min(1.0F, (float)x / barWidth));
    }

    public static Color computeColor(float position) {
        position = Math.max(0.0F, Math.min(1.0F, position));
        return Color.getHSBColor(position, 1.0F, 1.0F);
    }
}
