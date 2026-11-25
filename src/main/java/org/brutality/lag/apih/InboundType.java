package org.brutality.lag.apih;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;

public enum InboundType {
    INBOUND(packet -> {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.getNetHandler() != null) {
            try {
                ((Packet<NetHandlerPlayClient>)packet).processPacket(mc.getNetHandler());
            } catch (ThreadQuickExitException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }),
    OUTBOUND(packet -> {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.getNetHandler() != null) {
            mc.getNetHandler().addToSendQueue(packet);
        }
    });

    public static final Set<InboundType> ONLY_INBOUND = EnumSet.of(INBOUND);
    public static final Set<InboundType> ONLY_OUTBOUND = EnumSet.of(OUTBOUND);
    public static final Set<InboundType> BIDIRECTIONAL = EnumSet.allOf(InboundType.class);
    private final Consumer<Packet<?>> channel;

    private InboundType(Consumer<Packet<?>> channel) {
        this.channel = channel;
    }

    private static InboundType[] computeInboundTypeArray() {
        return new InboundType[]{INBOUND, OUTBOUND};
    }

    public void handlePacket(Packet<?> packet) {
        this.channel.accept(packet);
    }
}
