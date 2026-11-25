package org.brutality.script.packet;

import net.minecraft.network.Packet;
import org.brutality.script.packet.clientbound.GetHealthSPacket;
import org.brutality.script.packet.clientbound.GetItemStackSPacket;
import org.brutality.script.packet.clientbound.GetPitchSPacket;
import org.brutality.script.packet.clientbound.GetPitchSPacket2;
import org.brutality.script.packet.clientbound.GetReasonSPacket;
import org.brutality.script.packet.clientbound.GetStrengthSPacket;
import org.brutality.script.packet.clientbound.GetTypeSPacket;
import org.brutality.script.packet.clientbound.GetTypeSPacket2;
import org.brutality.script.packet.clientbound.GetVelocityZSPacket;
import org.brutality.script.packet.clientbound.GetYawSPacket;
import org.brutality.script.packet.clientbound.SPacket;
import org.brutality.script.packet.serverbound.CPacket;
import org.brutality.script.packet.serverbound.GetDataCPacket;
import org.brutality.script.packet.serverbound.GetEntityIdCPacket;
import org.brutality.script.packet.serverbound.GetEntityIdCPacket2;
import org.brutality.script.packet.serverbound.GetFacingCPacket;
import org.brutality.script.packet.serverbound.GetModeCPacket;
import org.brutality.script.packet.serverbound.GetPositionCPacket;
import org.brutality.script.packet.serverbound.GetSlotIdCPacket;
import org.brutality.script.packet.serverbound.GetUidCPacket;
import org.brutality.script.packet.serverbound.GetWalkSpeedCPacket;
import org.brutality.script.packet.serverbound.GetWindowIdCPacket;
import org.brutality.script.packet.serverbound.IsOnGroundCPacket;
import org.brutality.script.packet.serverbound.ServerboundCPacket;
import org.brutality.script.packet.serverbound.ServerboundCPacket2;
import org.brutality.script.packet.serverbound.ServerboundCPacket3;
import org.brutality.script.packet.serverbound.SetMessageCPacket;

public class C01PacketChatMessageHandler {
    public static CPacket computeCPacket(Packet<?> packet) {
        if (packet == null) {
            return null;
        }
        String simpleName = packet.getClass().getSimpleName();
        switch (simpleName) {
            case "C01PacketChatMessage":
                return new SetMessageCPacket(packet);
            case "C02PacketUseEntity":
                return new GetEntityIdCPacket(packet);
            case "C03PacketPlayer":
            case "C04PacketPlayerPosition":
            case "C05PacketPlayerLook":
            case "C06PacketPlayerPosLook":
                return new IsOnGroundCPacket(packet);
            case "C07PacketPlayerDigging":
                return new GetFacingCPacket(packet);
            case "C08PacketPlayerBlockPlacement":
                return new GetPositionCPacket(packet);
            case "C09PacketHeldItemChange":
                return new ServerboundCPacket(packet);
            case "C0APacketAnimation":
                return new ServerboundCPacket2(packet);
            case "C0BPacketEntityAction":
                return new GetEntityIdCPacket2(packet);
            case "C0DPacketCloseWindow":
                return new GetWindowIdCPacket(packet);
            case "C0EPacketClickWindow":
                return new GetModeCPacket(packet);
            case "C0FPacketConfirmTransaction":
                return new GetUidCPacket(packet);
            case "C10PacketCreativeInventoryAction":
                return new GetSlotIdCPacket(packet);
            case "C13PacketPlayerAbilities":
                return new GetWalkSpeedCPacket(packet);
            case "C16PacketClientStatus":
                return new ServerboundCPacket3(packet);
            case "C17PacketCustomPayload":
                return new GetDataCPacket(packet);
            default:
                return new CPacket(packet);
        }
    }

    public static Packet<?> computePacket(Object obj) {
        if (obj instanceof CPacket) {
            return ((CPacket)obj).getPacket();
        }
        if (obj instanceof SPacket) {
            return ((SPacket)obj).getPacket();
        }
        return obj instanceof Packet ? (Packet)obj : null;
    }

    public static SPacket computeSPacket(Packet<?> packet) {
        if (packet == null) {
            return null;
        }
        String simpleName = packet.getClass().getSimpleName();
        switch (simpleName) {
            case "S02PacketChat":
                return new GetTypeSPacket(packet);
            case "S04PacketEntityEquipment":
                return new GetItemStackSPacket(packet);
            case "S06PacketUpdateHealth":
                return new GetHealthSPacket(packet);
            case "S08PacketPlayerPosLook":
                return new GetPitchSPacket(packet);
            case "S12PacketEntityVelocity":
                return new GetVelocityZSPacket(packet);
            case "S19PacketEntityHeadLook":
                return new GetYawSPacket(packet);
            case "S27PacketExplosion":
                return new GetStrengthSPacket(packet);
            case "S29PacketSoundEffect":
                return new GetPitchSPacket2(packet);
            case "S40PacketDisconnect":
                return new GetReasonSPacket(packet);
            case "S45PacketTitle":
                return new GetTypeSPacket2(packet);
            default:
                return new SPacket(packet);
        }
    }

    public static Object computeObject(Packet<?> packet) {
        if (packet == null) {
            return null;
        }
        String simpleName = packet.getClass().getSimpleName();
        if (C00PacketHandshakeHandler.matchesString(simpleName)) {
            return computeCPacket(packet);
        }
        return C00PacketHandshakeHandler.matchesString2(simpleName) ? computeSPacket(packet) : packet;
    }
}
