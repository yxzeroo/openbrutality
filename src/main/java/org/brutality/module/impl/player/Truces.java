package org.brutality.module.impl.player;

import lombok.Generated;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;

public final class Truces extends Module {
    private static Truces instance;

    public Truces() {
        super("Truces", 0, Category.PLAYER);
        instance = this;
        this.handleFlag3(true);
    }

    @Generated
    public static Truces getInstance() {
        return instance;
    }

    @Override
    public void onEvent(Event event) {
    }
}
