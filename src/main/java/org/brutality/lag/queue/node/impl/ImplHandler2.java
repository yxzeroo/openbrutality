package org.brutality.lag.queue.node.impl;

import net.minecraft.network.Packet;
import org.brutality.lag.apih.InboundType;
import org.brutality.lag.handler.HandlerHandler;
import org.brutality.lag.queue.node.api.ApiHandler;

public final class ImplHandler2 extends ApiHandler {
    private final Packet<?> packet;
    private final InboundType inboundType;
    private final long timestamp;

    public void handleHandlerHandler(HandlerHandler handlerHandler) {
        if (this.inboundType == InboundType.OUTBOUND) {
            handlerHandler.handlePacket(this.packet);
        }

        this.inboundType.handlePacket(this.packet);
    }

    public ImplHandler2(Packet<?> packet, InboundType inboundType) {
        this.packet = packet;
        this.inboundType = inboundType;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
