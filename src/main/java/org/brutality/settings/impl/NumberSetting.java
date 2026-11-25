package org.brutality.settings.impl;

import org.brutality.extensions.APPDATAHandler;
import org.brutality.module.Module;
import org.brutality.service.itemsearch.IsVisibleApi;
import org.brutality.settings.Setting;
import org.brutality.utils.XPosHelper;
import org.brutality.utils.performance.InvalidateHelper;

public class NumberSetting extends Setting {
    private double value2;
    private final double value3;
    private final double minValue;
    private final double maxValue;
    private final double increment;

    public NumberSetting(String name, Module parent, double value, double minValue, double maxValue, double increment) {
        super(name, parent);
        this.value2 = value;
        this.value3 = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.increment = increment;
    }

    public double getValue2() {
        return this.value2;
    }

    public double computeDouble() {
        if (this.gl()) {
            return XPosHelper.computeInt(this.value2, InvalidateHelper.computeScaledResolution());
        }
        return this.checkState() ? XPosHelper.computeInt2(this.value2, InvalidateHelper.computeScaledResolution()) : this.value2;
    }

    public void handleDouble2(double value) {
        double d = value;
        if (this.gl()) {
            d = XPosHelper.computeDouble((int)Math.round(value), InvalidateHelper.computeScaledResolution());
        } else if (this.checkState()) {
            d = XPosHelper.computeDouble2((int)Math.round(value), InvalidateHelper.computeScaledResolution());
        }

        this.handleDouble3(d);
    }

    private void handleDouble3(double d) {
        double d2 = this.value2;
        double d3 = this.gl() ? 1920.0 : (this.checkState() ? 1080.0 : this.maxValue);
        if (d < this.minValue) {
            this.value2 = this.minValue;
        } else if (d > d3) {
            this.value2 = d3;
        } else {
            this.value2 = Math.round(d / this.increment) * this.increment;
            this.value2 = Math.max(this.minValue, Math.min(d3, this.value2));
        }

        if (this.value2 != d2) {
            APPDATAHandler.process4();
        }
    }

    public void handleDouble4(double value) {
        this.handleDouble3(value);
    }

    public void handleDouble(double value) {
        if (this.value2 != value) {
            this.value2 = value;
            APPDATAHandler.process4();
        }
    }

    private boolean gl() {
        return XPosHelper.matchesString2(this.getName(), this.maxValue, this.increment);
    }

    private boolean checkState() {
        return XPosHelper.matchesString(this.getName(), this.maxValue, this.increment);
    }

    public NumberSetting computeNumberSetting(IsVisibleApi visibility) {
        this.setConditionalVisibility(visibility);
        return this;
    }

    @Override
    public String toString() {
        int n = this.r();
        return n == 0 ? String.format("%.0f", this.value2) : String.format("%." + n + "f", this.value2);
    }

    public double getMinValue() {
        return this.minValue;
    }

    public double getDefaultValue() {
        return this.value3;
    }

    public double getMaxValue() {
        return this.maxValue;
    }

    public double getIncrement() {
        return this.increment;
    }

    private int r() {
        if (this.increment >= 1.0) {
            return 0;
        }
        String str = String.valueOf(this.increment);
        return str.contains(".") ? str.length() - str.indexOf(".") - 1 : 0;
    }
}
