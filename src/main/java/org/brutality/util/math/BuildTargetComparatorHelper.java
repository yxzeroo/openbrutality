package org.brutality.util.math;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.Brutality;
import org.brutality.automation.grinding.targeting.FilterTargetsHandler;
import org.brutality.automation.grinding.targeting.SelectNearestPlayerHandler;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.core.DisplayNameHandler;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.util.BEASTHelper;
import org.brutality.util.GetDisplayedTitleHelper;
import org.brutality.util.PizzaType;
import org.brutality.util.UtilHelper6;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.WHelper;

public class BuildTargetComparatorHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Map<String, String> lookupMap = DisplayNameHandler.lookupMap8;

    public static EntityPlayer selectTarget(
        AutoGrinder autoGrinder, AutoGrinder.YawMode yawMode, boolean flag, double d, double d2, double d3, double d4, double d5, boolean flag2, int n
    ) {
        var players = FilterTargetsHandler.filterTargets(
            autoGrinder,
            yawMode,
            flag,
            flag2,
            autoGrinder.diamondIfLookingSetting.isEnabled(),
            Brutality.moduleManager.computeModule("AntiBot") != null && Brutality.moduleManager.computeModule("AntiBot").checkState44()
        );
        double d6 = Double.parseDouble(lookupMap.getOrDefault("maxMidOverride", "-1"));
        var players2 = players.stream()
            .filter(autoGrinder::matchesEntityPlayer2)
            .filter(player -> d6 <= 0.0 || player.getDistance(d3, d4, d5) <= d6 || autoGrinder.checkState17())
            .collect(Collectors.toList());
        var players3 = players2.stream()
            .filter(player2 -> matchesEntityPlayer(mc.thePlayer, (Entity)player2))
            .sorted(Comparator.comparingDouble(player3 -> mc.thePlayer.getDistanceToEntity(player3)))
            .collect(Collectors.toList());
        var comparator = buildTargetComparator(yawMode, n);
        if (autoGrinder.checkState17()) {
            BlockPos blockPos = autoGrinder.getBlockPos();
            if (blockPos != null) {
                EntityPlayer player4 = players3.stream().sorted(Comparator.<EntityPlayer>comparingInt(player5 -> {
                    double dist = mc.thePlayer.getDistanceToEntity(player5);
                    if (dist <= 3.0) {
                        return 0;
                    }
                    if (dist <= 4.0) {
                        return 1;
                    }
                    if (dist <= 5.5) {
                        return 2;
                    }
                    if (dist <= 8.0) {
                        return 3;
                    }
                    return dist <= 12.0 ? 4 : 5;
                }).thenComparingInt(player6 -> computeInt(player6, blockPos)).thenComparing(comparator)).findFirst().orElse(null);
                if (player4 != null) {
                    return player4;
                }
            }
        }

        if (autoGrinder.checkState24()) {
            PizzaType pizzaType = ConnectToServerHandler.getPizzaType();
            switch (pizzaType) {
                case TDM:
                default:
                    break;
                case BEAST:
                    boolean flag3 = BEASTHelper.matchesEntityPlayer(mc.thePlayer);
                    EntityPlayer player7 = players3.stream()
                        .filter(player8 -> BEASTHelper.matchesEntityPlayer(player8) != flag3)
                        .sorted(Comparator.<EntityPlayer>comparingInt(player9 -> {
                            double dist2 = mc.thePlayer.getDistanceToEntity(player9);
                            if (dist2 <= 3.0) {
                                return 0;
                            }
                            if (dist2 <= 4.0) {
                                return 1;
                            }
                            if (dist2 <= 5.5) {
                                return 2;
                            }
                            if (dist2 <= 8.0) {
                                return 3;
                            }
                            return dist2 <= 12.0 ? 4 : 5;
                        }).thenComparing(comparator))
                        .findFirst()
                        .orElse(null);
                    if (player7 != null) {
                        return player7;
                    }
                    break;
                case BLOCKHEAD:
                    return SelectNearestPlayerHandler.selectNearestPlayer(players);
                case SPIRE:
                    return selectSpireTarget(flag);
            }
        }

        boolean flag4 = autoGrinder.utilHelper7.computeInt(30) >= 5 && autoGrinder.utilHelper7.computeInt(18) >= 3 && autoGrinder.utilHelper7.computeInt(8) >= 1;
        boolean flag5 = BEASTHelper.computeInt() == 5 || flag4;
        if (yawMode == AutoGrinder.YawMode.HUNT_ENEMIES && flag5) {
            EntityPlayer player10 = players3.stream()
                .filter(player11 -> WHelper.matchesString(player11.getName()))
                .filter(player12 -> mc.thePlayer.getDistanceToEntity(player12) <= 6.5)
                .sorted(comparator)
                .findFirst()
                .orElse(null);
            if (player10 != null) {
                return player10;
            }
        }

        var players4 = players3.stream().filter(player13 -> mc.thePlayer.getDistanceToEntity(player13) <= d2).sorted(comparator).collect(Collectors.toList());
        if (players4.isEmpty()) {
            double d7 = computeDouble();
            players4 = players3.stream().filter(player14 -> mc.thePlayer.getDistanceToEntity(player14) <= d7).sorted(comparator).collect(Collectors.toList());
        }

        if (players4.isEmpty() && !GetDisplayedTitleHelper.checkState2()) {
            double d8 = computeDouble() + 2.5;
            players4 = players3.stream()
                .filter(player15 -> mc.thePlayer.getDistanceToEntity(player15) <= d8)
                .filter(BuildTargetComparatorHelper::matchesEntityPlayer)
                .sorted(comparator)
                .collect(Collectors.toList());
        }

        if (players4.isEmpty()) {
            players4 = players3.stream()
                .filter(player16 -> player16.getDistance(d3, d4, d5) <= d)
                .sorted(Comparator.comparingDouble(player17 -> mc.thePlayer.getDistanceToEntity(player17)))
                .collect(Collectors.toList());
        }

        if (players4.isEmpty()) {
            players4 = players2.stream()
                .sorted(
                    Comparator.<EntityPlayer, Boolean>comparing(player18 -> !GetDisplayedTitleHelper.matchesEntityPlayer(player18))
                        .thenComparingDouble(player19 -> player19.getDistanceToEntity(mc.thePlayer))
                )
                .collect(Collectors.toList());
        }

        if (!players4.isEmpty()) {
            return (EntityPlayer)players4.get(0);
        }
        return players3.isEmpty() ? null : (EntityPlayer)players3.get(0);
    }

    private static double computeDouble() {
        return Double.parseDouble(lookupMap.getOrDefault("nearbyRange", "0"));
    }

    public static boolean matchesEntityPlayer(EntityPlayer player, int n) {
        return computeDouble(player) <= n / 2.0;
    }

    private static float computeFloat(float f, float f2) {
        return f >= 0.0F ? f + Math.abs(f2) : f - Math.abs(f2);
    }

    public static EntityPlayer findNearestVisiblePlayer() {
        return mc.theWorld
            .playerEntities
            .stream()
            .filter(player -> player != mc.thePlayer)
            .filter(player2 -> !player2.isDead && !player2.isInvisible())
            .filter(player3 -> matchesEntityPlayer(mc.thePlayer, player3))
            .min(Comparator.comparingDouble(player4 -> mc.thePlayer.getDistanceToEntity(player4)))
            .orElse(null);
    }

    private static int computeInt(EntityPlayer player, BlockPos pos) {
        double dist = Math.sqrt(player.getDistanceSq(pos));
        if (dist <= 4.5) {
            return 0;
        }
        if (dist <= 7.5) {
            return 1;
        }
        if (dist <= 12.0) {
            return 2;
        }
        return dist <= 25.0 ? 3 : 4;
    }

    public static void handleDouble(double d, double d2, double d3, int n, int n2) {
        handleFloatArray(MathHelper.computeFloatArray(d, d2, d3), n, n2);
    }

    public static boolean matchesEntityPlayer(EntityPlayer player, Entity entity, double d, double d2) {
        double minX = entity.getEntityBoundingBox().minX;
        double maxX = entity.getEntityBoundingBox().maxX;
        double minY = entity.getEntityBoundingBox().minY;
        double maxY = entity.getEntityBoundingBox().maxY;
        double minZ = entity.getEntityBoundingBox().minZ;
        double maxZ = entity.getEntityBoundingBox().maxZ;
        double d3 = (maxX - minX) * d;
        double d4 = (maxY - minY) * d;
        double d5 = (maxZ - minZ) * d;
        double d6 = minX + d3;
        double d7 = maxX - d3;
        double d8 = minY + d4;
        double d9 = maxY - d4;
        double d10 = minZ + d5;
        double d11 = maxZ - d5;
        Vec3[] vecs = new Vec3[]{
            new Vec3(d6, d8, d10),
            new Vec3(d7, d8, d10),
            new Vec3(d6, d9, d10),
            new Vec3(d7, d9, d10),
            new Vec3(d6, d8, d11),
            new Vec3(d7, d8, d11),
            new Vec3(d6, d9, d11),
            new Vec3(d7, d9, d11),
            new Vec3((minX + maxX) / 2.0, (minY + maxY) / 2.0, (minZ + maxZ) / 2.0)
        };
        int n = 0;

        for (Vec3 vec : vecs) {
            if (matchesEntityPlayer(player, vec)) {
                n++;
            }
        }

        return n >= d2 * vecs.length;
    }

    public static boolean matchesEntityPlayer(EntityPlayer player, BlockPos pos) {
        double d = pos.getX() + 0.5;
        double y = pos.getY();
        double d2 = pos.getZ() + 0.5;
        Vec3[] vecs = new Vec3[]{
            new Vec3(d - 0.25, y, d2 - 0.25),
            new Vec3(d + 0.25, y, d2 - 0.25),
            new Vec3(d - 0.25, y, d2 + 0.25),
            new Vec3(d + 0.25, y, d2 + 0.25)
        };
        int len = vecs.length;

        for (int i = 0; i < 4; i++) {
            if (!matchesEntityPlayer(player, vecs[i])) {
                return false;
            }
        }

        return true;
    }

    public static Comparator<EntityPlayer> buildTargetComparator(AutoGrinder.YawMode yawMode, int n) {
        Comparator<EntityPlayer> comparator = Comparator.comparing((EntityPlayer player) -> matchesEntityPlayer(player, n), Comparator.reverseOrder());
        Comparator<EntityPlayer> comparator2 = Comparator.comparing((EntityPlayer player2) -> player2.hurtResistantTime > 0);
        Comparator<EntityPlayer> comparator3 = Comparator.comparingDouble(BuildTargetComparatorHelper::computeDouble);
        Comparator<EntityPlayer> comparator4 = Comparator.comparingDouble(BEASTHelper::computeFloat);
        Comparator<EntityPlayer> comparator5 = comparator4.reversed();
        Comparator<EntityPlayer> comparator6 = Comparator.comparingInt((EntityPlayer player3) -> player3.hurtResistantTime);
        Comparator<EntityPlayer> comparator7 = Comparator.comparingDouble((EntityPlayer player4) -> mc.thePlayer.getDistanceToEntity(player4));

        return comparator.thenComparing(switch (yawMode) {
            case YAW -> comparator3.thenComparing(comparator4);
            case HEALTH -> comparator4.thenComparing(comparator3);
            case HEALTH_ONLY_HITTABLE -> comparator2.thenComparing(comparator4).thenComparing(comparator3);
            case HIGHEST_HEALTH -> comparator5.thenComparing(comparator3);
            case HURT_TIME, HUNT_ENEMIES -> comparator6.thenComparing(comparator4);
            default -> comparator3.thenComparing(comparator4);
        }).thenComparing(comparator7);
    }

    public static void handleBlockPos(BlockPos pos, int n, int n2) {
        handleDouble(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, n, n2);
    }

    public static void handleEntity(Entity entity, int n, int n2) {
        double d;
        if (entity instanceof EntityLivingBase living) {
            d = living.posY + living.getEyeHeight() * 0.9;
        } else {
            d = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0;
        }

        handleDouble(entity.posX, d, entity.posZ, n, n2);
    }

    public static boolean matchesEntityPlayer(EntityPlayer player, Vec3 vec) {
        MovingObjectPosition hit = player.worldObj.rayTraceBlocks(new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ), vec, false, true, false);
        return hit == null || hit.typeOfHit == MovingObjectType.MISS;
    }

    public static boolean matchesEntityPlayer(EntityPlayer player, Entity entity) {
        return matchesEntityPlayer(player, entity, 0.15, 0.75);
    }

    public static boolean matchesEntityPlayer(EntityPlayer player, EntityPlayer player2) {
        return matchesEntityPlayer(player, new Vec3(player2.posX, player2.posY + player2.getEyeHeight(), player2.posZ));
    }

    private static EntityPlayer selectSpireTarget(boolean flag) {
        return mc.theWorld
            .playerEntities
            .stream()
            .filter(player -> player != mc.thePlayer)
            .filter(player2 -> !player2.isDead && !player2.isInvisible())
            .filter(player3 -> player3.posY - mc.thePlayer.posY <= 2.2)
            .filter(player4 -> matchesEntityPlayer(mc.thePlayer, (Entity)player4))
            .filter(player5 -> !flag || !DefaultHelper.matchesString(player5.getName()))
            .sorted(Comparator.<EntityPlayer>comparingInt(player6 -> {
                double dist = mc.thePlayer.getDistanceToEntity(player6);
                if (dist <= 5.0) {
                    return 0;
                }
                return (dist > 7.5 ? 1 : (dist == 7.5 ? 0 : -1)) <= 0 ? 1 : 2;
            }).thenComparing(player7 -> player7.hurtResistantTime > 0).thenComparingDouble(BEASTHelper::computeFloat))
            .findFirst()
            .orElse(null);
    }

    public static void handleFloatArray(float[] floats, int n, int n2) {
        float yaw = mc.thePlayer.rotationYaw;
        float pitch = mc.thePlayer.rotationPitch;
        float f = net.minecraft.util.MathHelper.wrapAngleTo180_float(floats[0] - yaw);
        float f2 = net.minecraft.util.MathHelper.wrapAngleTo180_float(floats[1] - pitch);
        float[] floats2 = new float[]{computeFloat(f * (n / 100.0F), 0.05F), computeFloat(f2 * (n2 / 100.0F), 0.05F)};
        float[] floats3 = new float[]{
            net.minecraft.util.MathHelper.clamp_float(floats2[0], -30.0F, 30.0F), net.minecraft.util.MathHelper.clamp_float(floats2[1], -30.0F, 30.0F)
        };
        float[] floats4 = MathHelper.computeFloatArray(floats3);

        for (int i = 0; i < 2; i++) {
            if (floats4[i] == 0.0F) {
                floats4[i] = floats3[i];
            }
        }

        mc.thePlayer.rotationYaw = mc.thePlayer.rotationYaw + net.minecraft.util.MathHelper.clamp_float(floats4[0], Math.min(0.0F, f), Math.max(0.0F, f));
        float f3 = net.minecraft.util.MathHelper.clamp_float(floats4[1], Math.min(0.0F, f2), Math.max(0.0F, f2));
        mc.thePlayer.rotationPitch = net.minecraft.util.MathHelper.clamp_float(pitch + f3, -90.0F, 90.0F);
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        return UtilHelper6.matchesEntityPlayer(player, 30.0F, 50.0F);
    }

    private static double computeDouble(EntityPlayer player) {
        return Math.abs(
            net.minecraft.util.MathHelper.wrapAngleTo180_float(
                MathHelper.computeFloatArray(player.posX, player.posY, player.posZ)[Integer.parseInt(lookupMap.get("z"))] - mc.thePlayer.rotationYaw
            )
        );
    }
}
