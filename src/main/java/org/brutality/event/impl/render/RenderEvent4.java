package org.brutality.event.impl.render;

import net.minecraft.world.World;
import org.brutality.event.Event;

public class RenderEvent4 extends Event {
    public World lastWorld;
    public World world;

    public RenderEvent4(World lastWorld, World world) {
        this.lastWorld = lastWorld;
        this.world = world;
    }
}
