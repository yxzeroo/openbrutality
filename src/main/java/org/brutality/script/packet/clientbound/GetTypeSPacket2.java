package org.brutality.script.packet.clientbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S45PacketTitle.Type;
import net.minecraft.util.IChatComponent;

public class GetTypeSPacket2 extends SPacket {
    public String getType() {
        if (this.packet instanceof S45PacketTitle) {
            Type type = ((S45PacketTitle)this.packet).getType();
            return type != null ? type.name() : "";
        }
        return "";
    }

    public int computeInt() {
        return this.packet instanceof S45PacketTitle ? ((S45PacketTitle)this.packet).getFadeInTime() + ((S45PacketTitle)this.packet).getFadeOutTime() : 0;
    }

    public GetTypeSPacket2(Packet<?> packet) {
        super(packet);
    }

    public String getFormattedText() {
        if (this.packet instanceof S45PacketTitle) {
            IChatComponent message = ((S45PacketTitle)this.packet).getMessage();
            return message != null ? message.getFormattedText() : "";
        }
        return "";
    }

    public int getFadeInTime() {
        return this.packet instanceof S45PacketTitle ? ((S45PacketTitle)this.packet).getFadeInTime() : 0;
    }

    public int getFadeOutTime() {
        return this.packet instanceof S45PacketTitle ? ((S45PacketTitle)this.packet).getFadeOutTime() : 0;
    }

    public String getText() {
        if (this.packet instanceof S45PacketTitle) {
            IChatComponent message = ((S45PacketTitle)this.packet).getMessage();
            return message != null ? message.getUnformattedText() : "";
        }
        return "";
    }
}
