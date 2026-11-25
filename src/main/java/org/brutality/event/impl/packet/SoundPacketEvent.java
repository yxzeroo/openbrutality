package org.brutality.event.impl.packet;

import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;

public class SoundPacketEvent extends Event {
    private S29PacketSoundEffect s29PacketSoundEffect;
    private boolean cancelled;
    private String soundName2;
    private float volume2;
    private float pitch2;
    private double x2;
    private double y2;
    private double z2;

    public boolean checkState2() {
        return this.soundName2 != null && (this.soundName2.contains("mob") || this.soundName2.contains("entity"));
    }

    public boolean isVolume22() {
        return this.volume2 < 0.3F;
    }

    public double getX() {
        return this.x2;
    }

    public BlockPos getBlockPos() {
        return new BlockPos(this.x2, this.y2, this.z2);
    }

    public boolean checkState() {
        return this.checkState3() || this.checkState5() || this.checkState6();
    }

    public boolean checkState6() {
        return this.soundName2 != null && (this.soundName2.contains("ambient") || this.soundName2.contains("weather"));
    }

    public void handleS29PacketSoundEffect(S29PacketSoundEffect s29PacketSoundEffect) {
        this.s29PacketSoundEffect = s29PacketSoundEffect;
        if (s29PacketSoundEffect != null) {
            this.soundName2 = s29PacketSoundEffect.getSoundName();
            this.volume2 = s29PacketSoundEffect.getVolume();
            this.pitch2 = s29PacketSoundEffect.getPitch();
            this.x2 = s29PacketSoundEffect.getX();
            this.y2 = s29PacketSoundEffect.getY();
            this.z2 = s29PacketSoundEffect.getZ();
        }
    }

    public S29PacketSoundEffect getS29PacketSoundEffect() {
        return this.s29PacketSoundEffect;
    }

    public boolean checkState5() {
        return this.soundName2 != null && this.soundName2.contains("place");
    }

    public SoundPacketEvent(S29PacketSoundEffect s29PacketSoundEffect) {
        this.s29PacketSoundEffect = s29PacketSoundEffect;
        this.cancelled = false;
        this.soundName2 = s29PacketSoundEffect.getSoundName();
        this.volume2 = s29PacketSoundEffect.getVolume();
        this.pitch2 = s29PacketSoundEffect.getPitch();
        this.x2 = s29PacketSoundEffect.getX();
        this.y2 = s29PacketSoundEffect.getY();
        this.z2 = s29PacketSoundEffect.getZ();
    }

    public boolean isPitch2() {
        return this.pitch2 > 1.5F;
    }

    public String getSoundName() {
        return this.soundName2;
    }

    public boolean isVolume2() {
        return this.volume2 > 1.0F;
    }

    public float getVolume() {
        return this.volume2;
    }

    public boolean checkState10() {
        return this.checkState9() || this.checkState7() || this.checkState8();
    }

    public float getPitch() {
        return this.pitch2;
    }

    public double getZ() {
        return this.z2;
    }

    public boolean isPitch22() {
        return this.pitch2 < 0.7F;
    }

    public boolean checkState7() {
        return this.soundName2 != null && (this.soundName2.contains("hurt") || this.soundName2.contains("damage") || this.soundName2.contains("hit"));
    }

    public boolean checkState8() {
        return this.soundName2 != null && this.soundName2.contains("item");
    }

    public boolean checkState9() {
        return this.soundName2 != null && (this.soundName2.contains("step") || this.soundName2.contains("walk"));
    }

    public boolean checkState3() {
        return this.soundName2 != null && (this.soundName2.contains("break") || this.soundName2.contains("destroy"));
    }

    public double getY() {
        return this.y2;
    }

    @Override
    public String toString() {
        return String.format(
            "SoundPacketEvent{sound='%s', volume=%.2f, pitch=%.2f, pos=(%.1f, %.1f, %.1f), cancelled=%s}",
            this.soundName2,
            this.volume2,
            this.pitch2,
            this.x2,
            this.y2,
            this.z2,
            this.cancelled
        );
    }

    public double computeDouble(double d, double d2, double d3) {
        double d4 = this.x2 - d;
        double d5 = this.y2 - d2;
        double d6 = this.z2 - d3;
        return Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
    }
}
