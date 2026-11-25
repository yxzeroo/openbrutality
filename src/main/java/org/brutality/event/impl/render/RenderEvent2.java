package org.brutality.event.impl.render;

import net.minecraft.tileentity.TileEntityChest;
import org.brutality.event.Event;

public class RenderEvent2 extends Event {
    private final TileEntityChest tileEntityChest;
    private final Runnable runnable;

    public TileEntityChest getTileEntityChest() {
        return this.tileEntityChest;
    }

    public RenderEvent2(TileEntityChest tileEntityChest, Runnable runnable) {
        this.tileEntityChest = tileEntityChest;
        this.runnable = runnable;
    }

    public void process2() {
        this.runnable.run();
    }
}
