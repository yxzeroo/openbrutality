package org.brutality.utils.network;

import java.util.ArrayDeque;
import java.util.function.BiConsumer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;

public final class SizeHelper {
    private static final int count = 2048;
    private final ArrayDeque<SizeHelper.Entry> arrayDeque = new ArrayDeque<>();
    private final BiConsumer<Packet<?>, Boolean> biConsumer;
    private long timestamp;

    public synchronized boolean matchesPacket(Packet<?> packet, boolean flag, long l) {
        if (matchesPacket(packet, flag) && this.timestamp != 0L) {
            if (l < this.timestamp && this.arrayDeque.size() < 2048) {
                this.arrayDeque.addLast(new SizeHelper.Entry(packet, flag, l));
                return true;
            }
            this.flush();
            return false;
        }
        return false;
    }

    public synchronized void setTimestamp(long l) {
        this.timestamp = l + 100L;
    }

    public synchronized int size() {
        return this.arrayDeque.size();
    }

    public SizeHelper(BiConsumer<Packet<?>, Boolean> biConsumer) {
        this.biConsumer = biConsumer;
    }

    public synchronized void handleLong(long l) {
        if (this.timestamp != 0L && l >= this.timestamp || !this.arrayDeque.isEmpty() && l - this.arrayDeque.peekFirst().timestamp >= 30000L) {
            this.flush();
        }
    }

    public static boolean matchesPacket(Packet<?> packet, boolean flag) {
        return flag
            ? packet instanceof C03PacketPlayer
                || packet instanceof C0APacketAnimation
                || packet instanceof C00PacketKeepAlive
                || packet instanceof C0FPacketConfirmTransaction
            : packet instanceof S1CPacketEntityMetadata;
    }

    public synchronized void flush() {
        this.timestamp = 0L;

        while (!this.arrayDeque.isEmpty()) {
            SizeHelper.Entry entry = this.arrayDeque.removeFirst();
            this.biConsumer.accept(entry.packet, entry.flag);
        }
    }

    public synchronized void process() {
        this.timestamp = 0L;

        while (!this.arrayDeque.isEmpty()) {
            SizeHelper.Entry entry = this.arrayDeque.removeFirst();
            if (entry.flag && (entry.packet instanceof C00PacketKeepAlive || entry.packet instanceof C0FPacketConfirmTransaction)) {
                this.biConsumer.accept(entry.packet, true);
            }
        }
    }

    public synchronized void process2() {
        this.timestamp = 0L;
        this.arrayDeque.clear();
    }

    public static final class Entry {
        public final Packet<?> packet;
        public final boolean flag;
        public final long timestamp;

        public Entry(Packet<?> packet, boolean flag, long timestamp) {
            this.packet = packet;
            this.flag = flag;
            this.timestamp = timestamp;
        }
    }
}
