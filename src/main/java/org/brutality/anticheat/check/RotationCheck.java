package org.brutality.anticheat.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.anticheat.data.DataHandler;

public class RotationCheck extends Check {
    private final Map<EntityPlayer, List<Float>> lookupMap = new ConcurrentHashMap<>();
    private final Map<EntityPlayer, List<Float>> lookupMap2 = new ConcurrentHashMap<>();
    private static final long timestamp = 4180427463177797648L;

    private boolean matchesList(List<Float> list, List<Float> list2) {
        if (list.size() >= 5 && list2.size() >= 5) {
            var list3 = list.subList(0, Math.min(5, list.size()));
            var list4 = list2.subList(0, Math.min(5, list2.size()));
            int n = 0;
            int n2 = 0;

            for (float f : list3) {
                if (f > 25.0F) {
                    n++;
                }
            }

            for (float f2 : list4) {
                if (f2 > 35.0F) {
                    n2++;
                }
            }

            return n >= 3 && n2 >= 3;
        }
        return false;
    }

    public RotationCheck() {
        super("Rotation", "Detects illegal rotations and unnatural patterns");
        this.count = (int)timestamp;
    }

    @Override
    public void handleEntityPlayer(EntityPlayer player) {
        if (player != null) {
            this.lookupMap.remove(player);
            this.lookupMap2.remove(player);
        } else {
            this.lookupMap.clear();
            this.lookupMap2.clear();
        }

        super.handleEntityPlayer(player);
    }

    @Override
    public void onUpdate(EntityPlayer player) {
        if (player != mc.thePlayer) {
            if (!this.matchesEntityPlayer2(player)) {
                if (!this.matchesEntityPlayer(player)) {
                    DataHandler dataHandler = this.computeDataHandler(player);
                    if (dataHandler != null) {
                        float pitch = dataHandler.pitch;
                        float f = dataHandler.factor2;
                        float yaw = dataHandler.yaw;
                        float factor = dataHandler.factor;
                        float f2 = Math.abs(pitch - f);
                        float f3 = Math.abs(yaw - factor);
                        var list = this.lookupMap.computeIfAbsent(player, player2 -> new ArrayList<>());
                        var list2 = this.lookupMap2.computeIfAbsent(player, player3 -> new ArrayList<>());
                        list.add(0, f2);
                        list2.add(0, f3);
                        if (list.size() > 20) {
                            list.remove(list.size() - 1);
                        }

                        if (list2.size() > 20) {
                            list2.remove(list2.size() - 1);
                        }

                        if (Math.abs(pitch) > 90.0F) {
                            double d = (Math.abs(pitch) - 90.0) / 90.0 * 100.0;
                            double d2 = Math.abs(pitch) > 120.0F ? 8.0 : (Math.abs(pitch) > 100.0F ? 6.0 : 4.5);
                            this.handleEntityPlayer(player, d2, String.format("illegal-pitch | pitch=%.1f° | limit=±90° | excess=%.1f%%", pitch, d));
                        } else if ((f3 > 40.0F || f2 > 30.0F) && factor != 0.0F && f != 0.0F) {
                            boolean flag = this.matchesList(list, list2);
                            double d3 = f3 > 70.0F && f2 > 50.0F ? 5.0 : (flag ? 4.0 : 3.0);
                            String str = flag ? "aimbot-pattern" : "snap-rotation";
                            this.handleEntityPlayer(player, d3, String.format("%s | yaw=%.1f° | pitch=%.1f°", str, f3, f2));
                        } else if (this.computeDouble(player) > 0.0) {
                            this.handleEntityPlayer(player, 0.8);
                        }
                    }
                }
            }
        }
    }
}
