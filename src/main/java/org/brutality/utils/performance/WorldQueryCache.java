package org.brutality.utils.performance;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.core.CoreHandler2;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.pit.hypixel.hud.Cake;
import org.brutality.module.impl.pit.hypixel.minorevent.CarePackage;
import org.brutality.module.impl.pit.hypixel.minorevent.DragonEgg;
import org.brutality.module.impl.player.BedAura;
import org.brutality.module.impl.player.ChestAura;
import org.brutality.utils.player.PlayerApi;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.brutality.utils.rotation.OnJumpHelper;

public final class WorldQueryCache {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static Object object;
    private static Object object2;
    private static volatile Module module;
    private static volatile Module module2;
    private static Module module3;
    private static int count;
    private static volatile long timestamp;
    private static volatile long timestamp2 = -1L;
    private static volatile long timestamp3 = -1L;
    private static volatile long timestamp4 = -1L;
    private static volatile long timestamp5 = -1L;
    private static volatile long timestamp6 = -1L;
    private static volatile long timestamp7 = -1L;
    private static volatile long timestamp8 = -1L;
    private static volatile float factor;
    private static volatile float factor2;
    private static volatile double amount;
    private static volatile double amount2;
    private static volatile double amount3;
    private static boolean initialized;
    private static boolean flag;
    private static final long timestamp9 = -5685475591726301184L;

    public static boolean checkState2() {
        return !checkState()
            || !flag
            || module == null && module2 == null && timestamp > timestamp2 && timestamp3 != timestamp && timestamp5 != timestamp && timestamp8 != timestamp;
    }

    public static boolean matchesModule(Module module32) {
        process2();
        module3 = module32;
        count = mc.thePlayer == null ? 0 : mc.thePlayer.ticksExisted;
        if (Scaffold.instance != null && Scaffold.instance.checkState44()) {
            return false;
        }
        if (module == module32) {
            return true;
        }
        if (module != null) {
            return false;
        }
        KillAura instance = KillAura.instance;
        if (instance != null && instance.y() && !instance.checkState55()) {
            return false;
        }
        if (instance == null || !instance.checkState53() && !instance.checkState50()) {
            module = module32;
            timestamp2 = timestamp;
            flag = true;
            return true;
        }
        return false;
    }

    public static Vec3 computeVec3() {
        return checkState() ? new Vec3(amount, amount2 + mc.thePlayer.getEyeHeight(), amount3) : mc.thePlayer.getPositionEyes(1.0F);
    }

    public static MovingObjectPosition computeMovingObjectPosition(BlockPos pos) {
        if (mc.thePlayer != null && mc.theWorld != null && mc.playerController != null) {
            float[] floats = computeFloatArray();
            Vec3 vec = computeVec3();
            Vec3 vec2 = OnJumpHelper.computeVec3(floats[1], floats[0]);
            double blockReachDistance = mc.playerController.getBlockReachDistance();
            MovingObjectPosition hit = mc.theWorld
                .rayTraceBlocks(vec, vec.addVector(vec2.xCoord * blockReachDistance, vec2.yCoord * blockReachDistance, vec2.zCoord * blockReachDistance), false, false, false);
            return hit != null && hit.typeOfHit == MovingObjectType.BLOCK && pos.equals(hit.getBlockPos()) ? hit : null;
        }
        return null;
    }

    public static boolean matchesModule(Module module, BlockPos pos, boolean flag) {
        if (matchesModule(module, pos) && timestamp6 != timestamp) {
            MovingObjectPosition hit = computeMovingObjectPosition(pos);
            if (hit == null) {
                return false;
            }
            timestamp7 = timestamp;
            if (flag) {
                mc.thePlayer.swingItem();
            }

            return mc.playerController.clickBlock(pos, hit.sideHit);
        }
        return false;
    }

    private static boolean checkState() {
        return initialized && object == mc.theWorld && object2 == mc.thePlayer;
    }

    public static boolean matchesModule2(Module module2) {
        return checkState() && module == module2 && module2.checkState44();
    }

    private static void process2() {
        if (!checkState()) {
            reset();
            object = mc.theWorld;
            object2 = mc.thePlayer;
            if (mc.thePlayer != null) {
                float[] lastReportedRotations = mc.thePlayer.getLastReportedRotations();
                factor = lastReportedRotations[0];
                factor2 = lastReportedRotations[1];
                amount = mc.thePlayer.posX;
                amount2 = mc.thePlayer.posY;
                amount3 = mc.thePlayer.posZ;
            }

            initialized = true;
        }
    }

    public static float[] computeFloatArray() {
        return checkState() ? new float[]{factor, factor2} : mc.thePlayer.getLastReportedRotations();
    }

    public static boolean matchesModule(Module module, BlockPos pos) {
        if (matchesModule2(module)
            && module2 == module
            && timestamp > timestamp2
            && timestamp4 != timestamp
            && timestamp5 != timestamp
            && timestamp8 != timestamp
            && mc.currentScreen == null
            && !StartHotbarMovesHelper.flag
            && !CoreHandler2.checkState()
            && mc.thePlayer != null
            && !mc.thePlayer.isDead
            && !mc.thePlayer.isUsingItem()) {
            KillAura instance = KillAura.instance;
            return (instance == null || !instance.checkState53() && !instance.checkState50()) && computeMovingObjectPosition(pos) != null;
        }
        return false;
    }

    public static boolean isActive() {
        return checkState() ? module != null : computeModule() != null;
    }

    private WorldQueryCache() {
    }

    public static boolean matchesModule(Module module, BlockPos pos, boolean flag, boolean flag2) {
        if (matchesModule(module, pos) && timestamp7 != timestamp) {
            MovingObjectPosition hit = computeMovingObjectPosition(pos);
            if (hit == null) {
                return false;
            }
            timestamp6 = timestamp;
            if (flag) {
                mc.thePlayer.swingItem();
            }

            if (flag2) {
                return mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, hit.sideHit, hit.hitVec);
            }
            mc.thePlayer
                .sendQueue
                .addToSendQueue(
                    new C08PacketPlayerBlockPlacement(
                        pos,
                        hit.sideHit.getIndex(),
                        mc.thePlayer.getHeldItem(),
                        (float)(hit.hitVec.xCoord - pos.getX()),
                        (float)(hit.hitVec.yCoord - pos.getY()),
                        (float)(hit.hitVec.zCoord - pos.getZ())
                    )
                );
            return true;
        }
        return false;
    }

    private static Module computeModule() {
        if (BedAura.instance != null && BedAura.instance.checkState44() && BedAura.instance.bedPos != null) {
            return BedAura.instance;
        }
        if (Cake.instance != null && Cake.instance.checkState44() && Cake.instance.checkState4()) {
            return Cake.instance;
        }
        if (DragonEgg.instance != null && DragonEgg.instance.checkState44() && DragonEgg.instance.checkState()) {
            return DragonEgg.instance;
        }
        if (CarePackage.instance != null && CarePackage.instance.checkState44() && CarePackage.instance.checkState()) {
            return CarePackage.instance;
        }
        if (ChestAura.instance != null && ChestAura.instance.checkState44() && ChestAura.instance.checkState()) {
            return ChestAura.instance;
        }
        int n = mc.thePlayer == null ? -1 : mc.thePlayer.ticksExisted - count;
        return checkState() && module3 != null && module3.checkState44() && n >= 0 && n <= 1 ? module3 : null;
    }

    public static void reset() {
        module3 = null;
        module2 = null;
        module = null;
        count = (int)timestamp9;
        timestamp = 0L;
        timestamp8 = -1L;
        timestamp7 = -1L;
        timestamp6 = -1L;
        timestamp5 = -1L;
        timestamp4 = -1L;
        timestamp3 = -1L;
        timestamp2 = -1L;
        flag = false;
        initialized = false;
        object2 = null;
        object = null;
    }

    public static boolean matchesPacket(Packet<?> packet) {
        if (packet instanceof C02PacketUseEntity && ((C02PacketUseEntity)packet).getAction() == C02PacketUseEntity.Action.ATTACK) {
            if (!checkState()) {
                return computeModule() != null;
            }
            if (!flag) {
                return false;
            }
            if (!checkState2()) {
                return true;
            }
            KillAura instance = KillAura.instance;
            return instance != null && !instance.matchesEntity2(((C02PacketUseEntity)packet).getEntityFromWorld(mc.theWorld));
        }
        if (checkState() && flag) {
            BlockPos pos = null;
            if (packet instanceof C08PacketPlayerBlockPlacement packet2) {
                pos = packet2.getPosition();
                if (pos.equals(new BlockPos(-1, -1, -1))) {
                    return module != null && packet2.getStack() != null && packet2.getStack().getItem() instanceof ItemSword;
                }

                if (timestamp7 == timestamp) {
                    return true;
                }
            }

            if (packet instanceof C07PacketPlayerDigging packet3
                && (packet3.getStatus() == Action.START_DESTROY_BLOCK || packet3.getStatus() == Action.STOP_DESTROY_BLOCK)) {
                pos = packet3.getPosition();
                if (timestamp6 == timestamp) {
                    return true;
                }
            }

            return pos != null && (module == null || !matchesModule(module, pos));
        }
        return false;
    }

    public static void handlePacket(Packet<?> packet) {
        if (checkState()) {
            if (packet instanceof C03PacketPlayer packet2) {
                if (packet2.getRotating()) {
                    factor = packet2.getYaw();
                    factor2 = packet2.getPitch();
                }

                if (packet2.isMoving()) {
                    amount = packet2.getPositionX();
                    amount2 = packet2.getPositionY();
                    amount3 = packet2.getPositionZ();
                }

                timestamp++;
                module2 = module;
            } else {
                if (packet instanceof C0EPacketClickWindow
                    || packet instanceof C0DPacketCloseWindow
                    || packet instanceof C16PacketClientStatus && ((C16PacketClientStatus)packet).getStatus() == EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                    timestamp8 = timestamp;
                }

                if (packet instanceof C02PacketUseEntity && ((C02PacketUseEntity)packet).getAction() == C02PacketUseEntity.Action.ATTACK) {
                    timestamp4 = timestamp;
                }

                if (packet instanceof C08PacketPlayerBlockPlacement && !((C08PacketPlayerBlockPlacement)packet).getPosition().equals(new BlockPos(-1, -1, -1))) {
                    timestamp3 = timestamp6 = timestamp;
                }

                if (packet instanceof C07PacketPlayerDigging) {
                    Action status = ((C07PacketPlayerDigging)packet).getStatus();
                    if (status == Action.RELEASE_USE_ITEM || status == Action.ABORT_DESTROY_BLOCK) {
                        timestamp5 = timestamp;
                    } else if (status == Action.START_DESTROY_BLOCK || status == Action.STOP_DESTROY_BLOCK) {
                        timestamp3 = timestamp7 = timestamp;
                    }
                }
            }
        }
    }

    public static void process() {
        process2();
        Module module3 = computeModule();
        Module module4 = module;
        module = null;
        if (Scaffold.instance != null && Scaffold.instance.checkState44()) {
            if (module4 instanceof PlayerApi) {
                ((PlayerApi)module4).process4();
            }

            flag = false;
        } else {
            KillAura instance = KillAura.instance;
            boolean flag2 = module3 != null && instance != null && instance.checkState12();
            boolean flag3 = flag2 && (module4 == module3 ? instance.checkState8() : instance.checkState55());
            Module module5 = module3 == null || flag2 && !flag3 ? null : module3;
            if (mc.thePlayer != null && mc.thePlayer.isUsingItem() && (instance == null || !instance.checkState53())) {
                module5 = null;
            }

            if (module4 != module5) {
                timestamp2 = timestamp;
                if (module4 instanceof PlayerApi) {
                    ((PlayerApi)module4).process4();
                }
            }

            module = module5;
            flag = module3 != null || module4 != null || module2 != null;
            if (module5 != null) {
                KillAura.process33();
            }
        }
    }
}
