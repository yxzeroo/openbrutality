package org.brutality.gui.font.api;

import java.util.List;

public interface GlyphFonts {
    NameApi computeNameApi(String name);

    default BrutalityFont computeApiApi(String str, float f) {
        return this.computeNameApi(str).computeApiApi(f);
    }

    List<String> computeList();
}
