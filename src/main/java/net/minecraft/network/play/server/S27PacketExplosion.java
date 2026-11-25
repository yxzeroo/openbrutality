package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class S27PacketExplosion implements Packet<INetHandlerPlayClient> {
    public double posX;
    public double posY;
    public double posZ;
    public float strength;
    public List<BlockPos> affectedBlockPositions;
    public float field_149152_f;
    public float field_149153_g;
    public float field_149159_h;

    public S27PacketExplosion() {
    }

    public S27PacketExplosion(double p_i45193_1_, double y, double z, float strengthIn, List<BlockPos> affectedBlocksIn, Vec3 p_i45193_9_) {
        this.posX = p_i45193_1_;
        this.posY = y;
        this.posZ = z;
        this.strength = strengthIn;
        this.affectedBlockPositions = Lists.newArrayList(affectedBlocksIn);
        if (p_i45193_9_ != null) {
            this.field_149152_f = (float)p_i45193_9_.xCoord;
            this.field_149153_g = (float)p_i45193_9_.yCoord;
            this.field_149159_h = (float)p_i45193_9_.zCoord;
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.posX = buf.readFloat();
        this.posY = buf.readFloat();
        this.posZ = buf.readFloat();
        this.strength = buf.readFloat();
        int n = buf.readInt();
        this.affectedBlockPositions = Lists.newArrayListWithCapacity(n);
        int x = (int)this.posX;
        int y = (int)this.posY;
        int z = (int)this.posZ;

        for (int i = 0; i < n; i++) {
            int n2 = buf.readByte() + x;
            int n3 = buf.readByte() + y;
            int n4 = buf.readByte() + z;
            this.affectedBlockPositions.add(new BlockPos(n2, n3, n4));
        }

        this.field_149152_f = buf.readFloat();
        this.field_149153_g = buf.readFloat();
        this.field_149159_h = buf.readFloat();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeFloat((float)this.posX);
        buf.writeFloat((float)this.posY);
        buf.writeFloat((float)this.posZ);
        buf.writeFloat(this.strength);
        buf.writeInt(this.affectedBlockPositions.size());
        int x = (int)this.posX;
        int y = (int)this.posY;
        int z = (int)this.posZ;

        for (BlockPos affectedBlockPosition : this.affectedBlockPositions) {
            int n = affectedBlockPosition.getX() - x;
            int n2 = affectedBlockPosition.getY() - y;
            int n3 = affectedBlockPosition.getZ() - z;
            buf.writeByte(n);
            buf.writeByte(n2);
            buf.writeByte(n3);
        }

        buf.writeFloat(this.field_149152_f);
        buf.writeFloat(this.field_149153_g);
        buf.writeFloat(this.field_149159_h);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleExplosion(this);
    }

    public float func_149149_c() {
        return this.field_149152_f;
    }

    public float func_149144_d() {
        return this.field_149153_g;
    }

    public float func_149147_e() {
        return this.field_149159_h;
    }

    public double getX() {
        return this.posX;
    }

    public double getY() {
        return this.posY;
    }

    public double getZ() {
        return this.posZ;
    }

    public float getStrength() {
        return this.strength;
    }

    public List<BlockPos> getAffectedBlockPositions() {
        return this.affectedBlockPositions;
    }
}
