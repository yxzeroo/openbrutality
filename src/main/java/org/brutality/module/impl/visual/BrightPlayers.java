package org.brutality.module.impl.visual;

import lombok.Generated;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;

public class BrightPlayers extends Module {
    public static BrightPlayers instance;

    @Generated
    public static BrightPlayers getInstance() {
        return instance;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    public BrightPlayers() {
        super("BrightPlayers", 0, Category.VISUAL);
        instance = this;
    }
}
