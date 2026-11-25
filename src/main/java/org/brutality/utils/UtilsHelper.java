package org.brutality.utils;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;

public final class UtilsHelper {
    private static final ArrayDeque<Long> arrayDeque = new ArrayDeque<>();
    private static final ArrayDeque<Long> arrayDeque2 = new ArrayDeque<>();
    private static final Map<Packet<?>, Boolean> lookupMap = new WeakHashMap<>();
    public static final ThreadLocal<int[]> threadLocal = ThreadLocal.withInitial(() -> new int[2]);
    public static long timestamp;
    public static long timestamp2;
    private static final long timestamp3 = 1000000000L;

    public static synchronized int computeInt() {
        handleArrayDeque(arrayDeque, System.nanoTime());
        return arrayDeque.size();
    }

    public static UtilsHelper.Entry computeEntry(int n) {
        if (n >= 0 && n <= 1) {
            int[] ints = threadLocal.get();
            if (ints[n]++ == 0) {
                handleInt(n);
            }

            return new UtilsHelper.Entry(n);
        }
        return new UtilsHelper.Entry(-1);
    }

    private static void handleArrayDeque(ArrayDeque<Long> arrayDeque, long l) {
        while (!arrayDeque.isEmpty() && l - arrayDeque.peekFirst() >= timestamp3) {
            arrayDeque.removeFirst();
        }
    }

    public static synchronized int computeInt2() {
        handleArrayDeque(arrayDeque2, System.nanoTime());
        return arrayDeque2.size();
    }

    public static synchronized void handlePacket(Packet<?> packet) {
        int n;
        if (packet instanceof C02PacketUseEntity) {
            n = ((C02PacketUseEntity)packet).getAction() == C02PacketUseEntity.Action.ATTACK ? 0 : 1;
        } else if (packet instanceof C08PacketPlayerBlockPlacement) {
            n = 1;
        } else if (packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)packet).getStatus() == Action.START_DESTROY_BLOCK) {
            n = 0;
        } else {
            if (!(packet instanceof C0EPacketClickWindow packet2)) {
                return;
            }

            if (packet2.getMode() == 2 || packet2.getMode() == 3 || packet2.getMode() == 4 || packet2.getMode() == 5) {
                return;
            }

            n = packet2.getUsedButton();
            if (n < 0 || n > 1) {
                return;
            }
        }

        if (lookupMap.put(packet, Boolean.TRUE) == null && threadLocal.get()[n] == 0) {
            handleInt(n);
        }
    }

    private static synchronized void handleInt(int n) {
        long now = System.nanoTime();
        ArrayDeque arrayDeque3 = n == 0 ? arrayDeque : arrayDeque2;
        handleArrayDeque(arrayDeque3, now);
        arrayDeque3.addLast(now);
        if (n == 0) {
            timestamp = System.currentTimeMillis();
        } else {
            timestamp2 = System.currentTimeMillis();
        }
    }

    public static void process() {
        handleInt(1);
    }

    public static void process2() {
        handleInt(0);
    }

    public static final class Entry implements AutoCloseable {
        private int button;

        public Entry(int button) {
            this.button = button;
        }

        @Override
        public void close() {
            if (this.button >= 0) {
                UtilsHelper.threadLocal.get()[this.button]--;
                this.button = -1;
            }
        }
    }
}
