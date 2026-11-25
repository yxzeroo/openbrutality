package org.brutality.script.packet.clientbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S06PacketUpdateHealth;

public class GetHealthSPacket extends SPacket {
    public float getHealth() {
        return this.packet instanceof S06PacketUpdateHealth ? ((S06PacketUpdateHealth)this.packet).getHealth() : 0.0F;
    }

    public GetHealthSPacket(Packet<?> packet) {
        super(packet);
    }

    public int getFoodLevel() {
        return this.packet instanceof S06PacketUpdateHealth ? ((S06PacketUpdateHealth)this.packet).getFoodLevel() : 0;
    }

    public float getSaturationLevel() {
        return this.packet instanceof S06PacketUpdateHealth ? ((S06PacketUpdateHealth)this.packet).getSaturationLevel() : 0.0F;
    }
}
