package org.brutality.clickgui.classic;

import org.brutality.settings.impl.NumberSetting;

public final class ClassicComponent {
    private final NumberSetting numberSetting;

    public ClassicComponent(NumberSetting numberSetting) {
        this.numberSetting = numberSetting;
    }

    public double computeDouble4() {
        return this.numberSetting.getMaxValue();
    }

    public double computeDouble() {
        return this.numberSetting.getMinValue();
    }

    public void handleDouble(double d) {
        this.numberSetting.handleDouble2(d);
    }

    public double computeDouble3() {
        return this.numberSetting.computeDouble();
    }

    public String computeString() {
        return this.numberSetting.toString();
    }

    public double computeDouble2() {
        return this.numberSetting.getIncrement();
    }
}
