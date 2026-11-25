package org.brutality.settings.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.brutality.extensions.APPDATAHandler;
import org.brutality.module.Module;
import org.brutality.service.itemsearch.IsVisibleApi;
import org.brutality.settings.Setting;

public class MultiBoolSetting extends Setting {
    private final List<String> entries;
    private final Set<String> itemSet;
    private final Set<String> itemSet2 = new LinkedHashSet<>();

    public MultiBoolSetting(String name, Module parent, Collection<String> defaults, Collection<String> options) {
        super(name, parent);
        this.entries = Collections.unmodifiableList(new ArrayList<>(options));
        this.itemSet = this.computeSet(defaults);
        this.itemSet2.addAll(this.itemSet);
    }

    public List<String> getEntries() {
        return this.entries;
    }

    public Set<String> computeSet() {
        return Collections.unmodifiableSet(this.itemSet2);
    }

    public boolean matchesString(String option) {
        String str = this.computeString(option);
        return str != null && this.itemSet2.contains(str);
    }

    public void handleString(String option) {
        String str = this.computeString(option);
        if (str != null) {
            if (!this.itemSet2.remove(str)) {
                this.itemSet2.add(str);
            }

            APPDATAHandler.process4();
        }
    }

    public void handleCollection(Collection<String> values) {
        Set set = this.computeSet(values);
        if (!this.itemSet2.equals(set)) {
            this.itemSet2.clear();
            this.itemSet2.addAll(set);
            APPDATAHandler.process4();
        }
    }

    public void reset() {
        this.handleCollection(this.itemSet);
    }

    public String computeString() {
        if (this.itemSet2.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();

        for (String str : this.entries) {
            if (this.itemSet2.contains(str)) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(str);
            }
        }

        return sb.toString();
    }

    public MultiBoolSetting computeMultiBoolSetting(IsVisibleApi visibility) {
        this.setConditionalVisibility(visibility);
        return this;
    }

    private Set<String> computeSet(Collection<String> collection) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        if (collection == null) {
            return linkedHashSet;
        }
        for (String str : collection) {
            String str2 = this.computeString(str);
            if (str2 != null) {
                linkedHashSet.add(str2);
            }
        }

        return linkedHashSet;
    }

    private String computeString(String str) {
        if (str == null) {
            return null;
        }
        for (String str2 : this.entries) {
            if (str2.equalsIgnoreCase(str)) {
                return str2;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.computeString();
    }
}
