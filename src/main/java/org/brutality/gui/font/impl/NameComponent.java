package org.brutality.gui.font.impl;

import java.awt.Font;
import java.util.Map;
import java.util.TreeMap;
import org.brutality.gui.font.api.BrutalityFont;
import org.brutality.gui.font.api.NameApi;

public final class NameComponent implements NameApi {
    private final Map<Float, BrutalityFont> lookupMap = new TreeMap<>();
    private final String text;
    private final Font font;

    private NameComponent(String text, Font font) {
        this.text = text;
        this.font = font;
    }

    @Override
    public String name() {
        return this.text;
    }

    public static NameApi computeNameApi(String str, Font font) {
        return new NameComponent(str, font);
    }

    @Override
    public BrutalityFont computeApiApi(float f) {
        BrutalityFont apiApi = this.lookupMap.get(f);
        if (apiApi == null) {
            apiApi = FilterComponent.computeApiApi(this.font.deriveFont(0, f));
            this.lookupMap.put(f, apiApi);
        }

        return apiApi;
    }
}
