package org.brutality.settings.impl;

import org.brutality.extensions.APPDATAHandler;
import org.brutality.module.Module;
import org.brutality.service.itemsearch.IsVisibleApi;
import org.brutality.settings.Setting;

public class BooleanSetting extends Setting {
    private boolean enabled2;
    private final boolean enabled3;

    public BooleanSetting(String name, Module parent, boolean enabled) {
        super(name, parent);
        this.enabled2 = enabled;
        this.enabled3 = enabled;
    }

    public boolean isEnabled() {
        return this.enabled2;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled2 != enabled) {
            this.enabled2 = enabled;
            APPDATAHandler.process4();
        }
    }

    public void r() {
        this.enabled2 = !this.enabled2;
        APPDATAHandler.process4();
    }

    public boolean isEnabled3() {
        return this.enabled3;
    }

    public BooleanSetting computeBooleanSetting(IsVisibleApi visibility) {
        this.setConditionalVisibility(visibility);
        return this;
    }

    @Override
    public String toString() {
        return this.enabled2 ? "Enabled" : "Disabled";
    }
}
