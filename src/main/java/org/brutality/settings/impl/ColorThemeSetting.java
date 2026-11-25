package org.brutality.settings.impl;

import java.awt.Color;
import org.brutality.module.Module;
import org.brutality.service.itemsearch.IsVisibleApi;
import org.brutality.settings.Setting;

public class ColorThemeSetting extends Setting {
    private int defaultColor;
    private int defaultColor2;
    private final int defaultColor3;
    private boolean enabled = false;
    private String text = "Solid";
    private double amount = 1.0;
    private double amount2 = 1.7;
    private double amount3 = 5.0;
    private static final long timestamp = 3000L;

    public ColorThemeSetting(String name, Module parent, int defaultColor) {
        super(name, parent);
        this.defaultColor3 = defaultColor;
        this.defaultColor = defaultColor;
        this.defaultColor2 = defaultColor;
    }

    public int getDefaultColor() {
        return this.defaultColor;
    }

    public int getDefaultColor3() {
        return this.defaultColor3;
    }

    public void setDefaultColor(int colorValue) {
        this.defaultColor = colorValue;
    }

    public int getDefaultColor2() {
        return this.defaultColor2;
    }

    public void setDefaultColor2(int secondaryColorValue) {
        this.defaultColor2 = secondaryColorValue;
    }

    public Color computeColor() {
        return new Color(this.defaultColor);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void handleFlag(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            this.text = "Chroma";
        }
    }

    public String getText() {
        return this.text;
    }

    public void handleString(String colorMode) {
        if (colorMode != null) {
            String str = colorMode.trim();
            if (str.equalsIgnoreCase("solid") || str.equalsIgnoreCase("blend") || str.equalsIgnoreCase("gradient") || str.equalsIgnoreCase("chroma")) {
                this.text = Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
                this.enabled = this.text.equalsIgnoreCase("chroma");
            }
        }
    }

    public double getAmount() {
        return this.amount;
    }

    public void handleDouble3(double blendFactor) {
        this.amount = Math.max(0.0, Math.min(1.0, blendFactor));
    }

    public double getAmount2() {
        return this.amount2;
    }

    public void handleDouble2(double fadeSpeed) {
        this.amount2 = Math.max(0.1, Math.min(10.0, fadeSpeed));
    }

    public double getAmount3() {
        return this.amount3;
    }

    public void handleDouble(double fadeSpread) {
        this.amount3 = Math.max(0.1, Math.min(10.0, fadeSpread));
    }

    public void update() {
        if (this.enabled) {
            long now = System.currentTimeMillis();
            float f = (float)(now % timestamp) / 3000.0F;
            this.defaultColor = Color.HSBtoRGB(f, 1.0F, 1.0F);
        }
    }

    public ColorThemeSetting computeColorThemeSetting(IsVisibleApi visibility) {
        this.setConditionalVisibility(visibility);
        return this;
    }
}
