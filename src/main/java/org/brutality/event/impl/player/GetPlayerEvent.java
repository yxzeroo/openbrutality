package org.brutality.event.impl.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;

public class GetPlayerEvent extends Event {
    private final EntityPlayer player;
    private final ItemStack itemStack;
    private int count;

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public int getCount() {
        return this.count;
    }

    public GetPlayerEvent(EntityPlayer player, ItemStack itemStack, int count) {
        this.player = player;
        this.itemStack = itemStack;
        this.count = count;
    }
}
