package org.brutality.module.impl.visual;

import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;

public class ItemPhysics extends Module {
    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    public ItemPhysics() {
        super("Item Physics", 0, Category.VISUAL);
    }
}
