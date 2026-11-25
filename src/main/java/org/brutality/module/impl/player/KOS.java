package org.brutality.module.impl.player;

import lombok.Generated;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;

public class KOS extends Module {
    private static KOS instance;

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Generated
    public static KOS getInstance() {
        return instance;
    }

    public KOS() {
        super("KOS", 0, Category.PLAYER);
        instance = this;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
