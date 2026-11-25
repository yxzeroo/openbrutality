package org.brutality.event.impl.player;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;

public class PlayerEvent extends Event {
    private final Block block;
    private final BlockPos blockPos;
    private AxisAlignedBB boundingBox;

    public PlayerEvent(Block block, BlockPos blockPos, AxisAlignedBB boundingBox) {
        this.block = block;
        this.blockPos = blockPos;
        this.boundingBox = boundingBox;
    }

    public final void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }

    public final AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public final BlockPos getBlockPos() {
        return this.blockPos;
    }

    public final Block getBlock() {
        return this.block;
    }
}
