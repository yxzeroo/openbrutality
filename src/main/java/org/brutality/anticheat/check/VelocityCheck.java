package org.brutality.anticheat.check;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import org.brutality.anticheat.data.DataHandler;

public class VelocityCheck extends Check {
    private final Map<Integer, VelocityCheck.Entry> lookupMap = new ConcurrentHashMap<>();
    private final Map<Integer, Long> lookupMap2 = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> lookupMap3 = new ConcurrentHashMap<>();
    private static final int count = 5;
    private static final int count2 = 20;
    private static final double amount = 0.35;
    private static final double amount2 = 0.62;
    private static final double amount3 = 0.2;
    private static final double amount4 = 1.5;

    @Override
    public void handlePacket(Packet<?> packet) {
    }

    @Override
    public void handleEntityPlayer(EntityPlayer player) {
        if (player != null) {
            int entityId = player.getEntityId();
            this.lookupMap.remove(entityId);
            this.lookupMap2.remove(entityId);
            this.lookupMap3.remove(entityId);
        } else {
            this.lookupMap.clear();
            this.lookupMap2.clear();
            this.lookupMap3.clear();
        }

        super.handleEntityPlayer(player);
    }

    @Override
    public void onUpdate(EntityPlayer player) {
        DataHandler dataHandler = this.computeDataHandler(player);
        DataHandler dataHandler2 = this.computeDataHandler2(player);
        List list = this.computeList(player);
        if (dataHandler != null && dataHandler2 != null && list.size() >= 2) {
            int hurtTime = dataHandler2.hurtTime;
            int hurtTime2 = dataHandler.hurtTime;
            long tick = dataHandler.tick;
            int entityId = player.getEntityId();
            if (hurtTime <= 0 && hurtTime2 > 0) {
                long orDefault = this.lookupMap2.getOrDefault(entityId, 0L);
                long l = tick - orDefault;
                int n = l <= 20L ? this.lookupMap3.getOrDefault(entityId, 0) + 1 : 1;
                this.lookupMap3.put(entityId, n);
                this.lookupMap2.put(entityId, tick);
                VelocityCheck.Entry entry = new VelocityCheck.Entry();
                entry.timestamp = dataHandler.tick;
                entry.amount = dataHandler.posX;
                entry.amount2 = dataHandler.posY;
                entry.amount3 = dataHandler.posZ;
                entry.amount4 = dataHandler.computeDouble();
                entry.flag = !dataHandler.onGround;
                entry.count = n;
                this.lookupMap.put(entityId, entry);
            }

            this.lookupMap.entrySet().removeIf(entry2 -> dataHandler.tick - entry2.getValue().timestamp > 10L);
            VelocityCheck.Entry entry3 = this.lookupMap.get(entityId);
            if (entry3 != null && dataHandler.tick - entry3.timestamp >= 5L) {
                if (!this.matchesEntityPlayer(player, list) && !this.matchesEntityPlayer(player, 1)) {
                    double d = dataHandler.posX - entry3.amount;
                    double d2 = dataHandler.posY - entry3.amount2;
                    double d3 = dataHandler.posZ - entry3.amount3;
                    double dist = Math.sqrt(d * d + d3 * d3);
                    double d4 = Math.abs(d2);
                    boolean flag = entry3.amount4 > 0.1;
                    double d5 = entry3.count > 1 ? 0.16000000000000003 : (flag ? 0.2 : 0.62);
                    double d6 = !entry3.flag && entry3.count <= 1 ? 0.35 : 0.24499999999999997;
                    if (dist < d5 * 0.5) {
                        double d7 = dist / d5;
                        double d8 = (1.0 - d7) * 1.5 * this.computeDouble(d7) * this.computeDouble(entry3.count);
                        this.handleEntityPlayer(
                            player,
                            d8,
                            String.format(
                                "reduced horizontal KB | moved=%.3f | expected≥%.3f | ratio=%.0f%% | combo=%d", dist, d5, d7 * 100.0, entry3.count
                            )
                        );
                    }

                    if (d4 < d6 * 0.5 && entry3.count <= 1 && !entry3.flag) {
                        double d9 = d4 / d6;
                        double d10 = (1.0 - d9) * 1.5 * this.computeDouble(d9);
                        this.handleEntityPlayer(
                            player, d10, String.format("reduced vertical KB | moved=%.3f | expected≥%.3f | ratio=%.0f%%", d4, d6, d9 * 100.0)
                        );
                    }
                }

                this.lookupMap.remove(entityId);
            }
        }
    }

    public VelocityCheck() {
        super("Velocity", "Detects manipulated knockback");
    }

    private double computeDouble(double d) {
        if (d < 0.1) {
            return 2.0;
        }
        if (d < 0.3) {
            return 1.5;
        }
        return d < 0.5 ? 1.0 : 0.5;
    }

    private double computeDouble(int n) {
        if (n >= 3) {
            return 0.3;
        }
        return n == 2 ? 0.5 : 1.0;
    }

    private boolean matchesEntityPlayer(EntityPlayer player, List<DataHandler> dataHandlers) {
        if (player.isBurning()) {
            return true;
        }
        if (dataHandlers.size() >= 3) {
            for (int i = Math.max(0, dataHandlers.size() - 3); i < dataHandlers.size(); i++) {
                if (((DataHandler)dataHandlers.get(i)).computeDouble() > 0.4) {
                    return true;
                }
            }
        }

        return false;
    }

    public static class Entry {
        public long timestamp;
        public double amount;
        public double amount2;
        public double amount3;
        public double amount4;
        public boolean flag;
        public int count;
    }
}
