package org.brutality.settings.impl;

import java.util.List;
import java.util.function.Supplier;
import org.brutality.extensions.APPDATAHandler;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.InterfaceType;
import org.brutality.service.itemsearch.IsVisibleApi;
import org.brutality.settings.Setting;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.ModuleSpecificHelper;

public class ModeSetting extends Setting {
    private final List<String> modes;
    private final String defaultMode;
    private String text;
    private Supplier<List<String>> dynamicModeSupplier;
    private boolean flag = false;
    private Runnable callback;

    public ModeSetting(String name, Module parent, String defaultMode, List<String> modes) {
        super(name, parent);
        this.modes = modes;
        this.defaultMode = defaultMode;
        this.text = modes.contains(defaultMode) ? defaultMode : (String)modes.get(0);
        this.flag = false;
    }

    public ModeSetting(String name, Module parent, String defaultMode, Supplier<List<String>> dynamicModeSupplier) {
        super(name, parent);
        this.dynamicModeSupplier = dynamicModeSupplier;
        this.modes = (List<String>)dynamicModeSupplier.get();
        this.defaultMode = defaultMode;
        this.text = this.modes.contains(defaultMode) ? defaultMode : this.modes.get(0);
        this.flag = true;
    }

    /** @deprecated */
    public static ModeSetting computeModeSetting(String name, Module parent) {
        return "Theme".equalsIgnoreCase(name)
            ? ModuleSpecificHelper.computeModeSetting(parent)
            : new ModeSetting(name, parent, BrutalityHelper.getInstance().getName(), BrutalityHelper::computeList);
    }

    public String getText() {
        return this.text;
    }

    public void handleString(String mode) {
        var list = this.computeList();
        if (this.matchesString(mode)) {
            String text = this.text;
            this.text = list.stream().filter(str -> str.equalsIgnoreCase(mode)).findFirst().orElse(this.text);
            if (!this.text.equals(text)) {
                APPDATAHandler.process4();
            }

            this.process4();
            if (this.callback != null) {
                this.callback.run();
            }
        }
    }

    public boolean matchesString(String mode) {
        return this.computeList().stream().anyMatch(str -> str.equalsIgnoreCase(mode));
    }

    public List<String> computeList2() {
        return this.computeList();
    }

    private List<String> computeList() {
        return this.flag && this.dynamicModeSupplier != null ? this.dynamicModeSupplier.get() : this.modes;
    }

    public boolean matchesString2(String mode) {
        return this.text.equalsIgnoreCase(mode);
    }

    public void process2() {
        var list = this.computeList();
        int n = list.indexOf(this.text);
        int n2 = (n + 1) % list.size();
        this.text = (String)list.get(n2);
        this.process4();
        if (this.callback != null) {
            this.callback.run();
        }
    }

    public void process3() {
        var list = this.computeList();
        int n = list.indexOf(this.text);
        int n2 = (n - 1 + list.size()) % list.size();
        this.text = (String)list.get(n2);
        this.process4();
        if (this.callback != null) {
            this.callback.run();
        }
    }

    public String getDefaultMode() {
        return this.defaultMode;
    }

    private void process4() {
        if (this.getParent() != null && this.getParent() instanceof InterfaceType) {
            ((InterfaceType)this.getParent()).process3();
        }
    }

    public int computeInt() {
        return this.computeList().indexOf(this.text);
    }

    public void handleInt(int index) {
        var list = this.computeList();
        if (index >= 0 && index < list.size()) {
            this.text = (String)list.get(index);
            this.process4();
            if (this.callback != null) {
                this.callback.run();
            }
        }
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void process5() {
        if (this.flag && this.dynamicModeSupplier != null) {
            var list = this.dynamicModeSupplier.get();
            if (!list.contains(this.text)) {
                this.text = list.contains(this.defaultMode) ? this.defaultMode : (String)list.get(0);
                this.process4();
                if (this.callback != null) {
                    this.callback.run();
                }
            }
        }
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public ModeSetting computeModeSetting(IsVisibleApi visibility) {
        this.setConditionalVisibility(visibility);
        return this;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
