package org.brutality.settings;

import org.brutality.module.Module;
import org.brutality.service.itemsearch.IsVisibleApi;

public class Setting {
    private String name2;
    private Module parent;
    private boolean visible2;
    private IsVisibleApi conditionalVisibility;

    public Setting(String name, Module parent) {
        this.name2 = name;
        this.parent = parent;
        this.visible2 = true;
        this.conditionalVisibility = null;
    }

    public String getName() {
        return this.name2;
    }

    public void setName(String name) {
        this.name2 = name;
    }

    public Module getParent() {
        return this.parent;
    }

    public void setParent(Module parent) {
        this.parent = parent;
    }

    public boolean isVisible() {
        return this.conditionalVisibility != null ? this.conditionalVisibility.isVisible() : this.visible2;
    }

    public void setVisible(boolean visible) {
        this.visible2 = visible;
    }

    public void setConditionalVisibility(IsVisibleApi conditionalVisibility) {
        this.conditionalVisibility = conditionalVisibility;
    }

    public void process() {
        this.conditionalVisibility = null;
    }
}
