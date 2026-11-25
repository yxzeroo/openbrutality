package org.brutality.lag.handler;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.lag.apih.ApihHandler;
import org.brutality.lag.apih.InboundType;
import org.brutality.lag.queue.QueueHandler;
import org.brutality.utils.network.NetworkHelper;

public final class LagHandler extends HandlerHandler {
    private final QueueHandler queueHandler = new QueueHandler(this);
    private final Set<Packet<?>> itemSet = Collections.newSetFromMap(Collections.synchronizedMap(new IdentityHashMap<>()));
    private volatile Vec3 vec3;

    public void process3() {
        this.queueHandler.clear();
        this.itemSet.clear();
        this.process4();
    }

    private void process4() {
        this.vec3 = null;
    }

    public void process() {
        this.queueHandler.matchesPacket(null, null);
    }

    public Vec3 getVec3() {
        return this.vec3;
    }

    @Override
    public void handlePacket(Packet<?> packet) {
        this.itemSet.add(packet);
    }

    private void handlePacket2(Packet<?> packet) {
        if (packet instanceof C03PacketPlayer packet2) {
            if (packet2.isMoving()) {
                this.vec3 = new Vec3(packet2.getPositionX(), packet2.getPositionY(), packet2.getPositionZ());
            }
        }
    }

    private boolean matchesPacket(Packet<?> packet) {
        return this.itemSet.remove(packet);
    }

    public void onReceivePacket(EventPacketReceive event) {
        if (Minecraft.getMinecraft().getNetHandler() == null) {
            this.queueHandler.clear();
            this.process4();
        } else {
            Packet packet = event.getPacket();
            boolean flag = this.matchesPacket(packet);
            if (!event.cancel2()) {
                if (!flag) {
                    if (!NetworkHelper.matchesPacket2(packet)) {
                        if (this.queueHandler.matchesPacket(packet, InboundType.INBOUND)) {
                            event.cancel();
                        }
                    }
                }
            }
        }
    }

    public void onSendPacket(EventPacketSend event) {
        if (Minecraft.getMinecraft().getNetHandler() == null) {
            this.queueHandler.clear();
            this.process4();
        } else {
            Packet packet = event.getPacket();
            boolean flag = this.matchesPacket(packet);
            if (!event.isCancelled()) {
                if (flag) {
                    this.handlePacket2(packet);
                } else if (this.queueHandler.matchesPacket(packet, InboundType.OUTBOUND)) {
                    event.setCancelled(true);
                } else {
                    this.handlePacket2(packet);
                }
            }
        }
    }

    public void process2() {
        if (Minecraft.getMinecraft().getNetHandler() == null) {
            this.queueHandler.clear();
            this.process4();
        } else {
            this.queueHandler.matchesPacket(null, null);
        }
    }

    public void handleInboundType(InboundType inboundType, long l) {
        this.queueHandler.handleInboundType(inboundType, l);
    }

    public void handleApihHandler(ApihHandler apihHandler) {
        this.queueHandler.handleApihHandler(apihHandler);
    }
}
