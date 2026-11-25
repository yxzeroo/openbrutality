package org.brutality.event.impl.attack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;

public final class AttackEvent extends Event {
    public final EntityPlayer entityPlayer;
    public final EntityPlayer entityPlayer2;
    private final ItemStack itemStack;

    public EntityPlayer getEntityPlayer() {
        return this.entityPlayer;
    }

    public AttackEvent(EntityPlayer entityPlayer, EntityPlayer entityPlayer2, ItemStack stack) {
        this.entityPlayer = entityPlayer;
        this.entityPlayer2 = entityPlayer2;
        this.itemStack = stack == null ? null : stack.copy();
    }

    public ItemStack computeItemStack() {
        return this.itemStack == null ? null : this.itemStack.copy();
    }

    public EntityPlayer getEntityPlayer2() {
        return this.entityPlayer2;
    }
}
