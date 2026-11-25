package org.brutality.lag.handler;

import net.minecraft.network.Packet;

public abstract class HandlerHandler {
    public abstract void handlePacket(Packet<?> packet);
}
