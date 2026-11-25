package org.brutality.utils.modules;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.lag.apih.InboundType;
import org.brutality.module.impl.movement.NoSlowDown;
import org.brutality.utils.network.NetworkHelper;

public class CountMovementHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static CountMovementHelper instance;
    private NoneType noneType = NoneType.NONE;
    private boolean flag = false;
    private final Deque<Packet<?>> deque = new ConcurrentLinkedDeque<>();
    private final Deque<Packet<?>> deque2 = new ConcurrentLinkedDeque<>();
    private CountMovementHelper.OutboundMode outboundMode = CountMovementHelper.OutboundMode.OUTBOUND;

    public NoneType getNoneType() {
        return this.noneType;
    }

    public int computeInt() {
        return this.deque.size();
    }

    public void process() {
        this.flag = false;
        this.noneType = NoneType.NONE;
        this.deque.clear();
        this.deque2.clear();
    }

    public long countMovement() {
        return this.deque.stream().filter(packet -> packet instanceof C03PacketPlayer).count();
    }

    public void handleOutboundMode(CountMovementHelper.OutboundMode outboundMode) {
        this.outboundMode = outboundMode == null ? CountMovementHelper.OutboundMode.OUTBOUND : outboundMode;
    }

    public boolean matchesPacket2(Packet<?> packet) {
        if (this.noneType == NoneType.NO_SLOW && NoSlowDown.checkState6() && !(packet instanceof C03PacketPlayer)) {
            return false;
        }
        if (this.noneType == NoneType.NONE
            || this.outboundMode == CountMovementHelper.OutboundMode.INBOUND
            || packet instanceof C00PacketKeepAlive
            || packet instanceof C01PacketChatMessage) {
            return false;
        }
        if (this.deque.isEmpty() && packet instanceof C0FPacketConfirmTransaction) {
            return false;
        }
        this.deque.offer(packet);
        return true;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void onEvent(Event event) {
        if (event instanceof EventPacketSend event2) {
            Packet packet = event2.getPacket();
            if (packet instanceof C00Handshake
                || packet instanceof C00PacketLoginStart
                || packet instanceof C00PacketServerQuery
                || packet instanceof C01PacketPing
                || packet instanceof C01PacketEncryptionResponse) {
                this.matchesFlag(false, this.noneType);
            }
        }

        if (event instanceof EventUpdate && mc.thePlayer != null && mc.thePlayer.isDead) {
            this.matchesFlag(false, this.noneType);
        }
    }

    public boolean matchesFlag(boolean flag, NoneType noneType) {
        if (noneType == NoneType.NONE) {
            return false;
        }
        if (flag) {
            this.noneType = noneType;
            this.flag = true;
        } else {
            if (this.noneType != noneType) {
                return false;
            }

            this.flag = false;
            if (Minecraft.getMinecraft().getNetHandler() != null && this.deque.isEmpty() && this.deque2.isEmpty()) {
                this.noneType = NoneType.NONE;
                return true;
            }

            for (Packet packet : this.deque) {
                NetworkHelper.handlePacket(packet);
            }

            this.deque.clear();

            for (Packet packet2 : this.deque2) {
                InboundType.INBOUND.handlePacket(packet2);
            }

            this.deque2.clear();
            this.noneType = NoneType.NONE;
        }

        return true;
    }

    public static CountMovementHelper getInstance() {
        if (instance == null) {
            instance = new CountMovementHelper();
        }

        return instance;
    }

    public boolean matchesPacket(Packet<?> packet) {
        if (NetworkHelper.matchesPacket2(packet)) {
            return false;
        }
        if (this.noneType != NoneType.NONE && this.outboundMode != CountMovementHelper.OutboundMode.OUTBOUND && !(packet instanceof C00PacketKeepAlive)) {
            this.deque2.offer(packet);
            return true;
        }
        return false;
    }

    public static enum OutboundMode {
        OUTBOUND,
        INBOUND,
        BOTH;

        private static CountMovementHelper.OutboundMode[] computeOutboundModeArray() {
            return new CountMovementHelper.OutboundMode[]{OUTBOUND, INBOUND, BOTH};
        }
    }
}
