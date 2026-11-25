package org.brutality.utils.misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.Brutality;
import org.brutality.lag.apih.ApihHandler;
import org.brutality.lag.apih.InboundType;
import org.brutality.lag.timeout.TimeoutHandler2;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.AntiBot;
import org.brutality.module.impl.combat.AutoClicker;
import org.brutality.module.impl.combat.Displace;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.OnMouseHandler;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.movement.Stasis;
import org.brutality.module.impl.player.AutoTool;
import org.brutality.module.impl.player.Autoblockin;
import org.brutality.utils.modules.CountMovementHelper;
import org.brutality.utils.modules.NoneType;
import org.brutality.utils.rotation.OnJumpHelper;

public final class ColorMathUtil {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static void process() {
        if (Displace.instance != null) {
            Displace.instance.process2();
        }

        if (Autoblockin.instance != null) {
            Autoblockin.instance.process2();
        }

        if (Stasis.instance != null) {
            Stasis.instance.process2();
        }

        if (OnMouseHandler.instance != null) {
            OnMouseHandler.instance.process();
        }
    }

    public static Vec3 computeVec3(float f, float f2) {
        float f3 = -f * (float) (Math.PI / 180.0) - (float) Math.PI;
        float f4 = -f2 * (float) (Math.PI / 180.0);
        return new Vec3(MathHelper.sin(f3) * -MathHelper.cos(f4), MathHelper.sin(f4), MathHelper.cos(f3) * -MathHelper.cos(f4));
    }

    private ColorMathUtil() {
    }

    public static class Entry extends GetBlockHelper {
    }

    public static final class Entry10 {
        private static final long timestamp = -6824592798696603649L;

        public static int computeInt(long l, long l2) {
            return Color.HSBtoRGB((float)((System.currentTimeMillis() + l2) % (Math.max(1L, l) * 1000L)) / (float)(Math.max(1L, l) * 1000L), 1.0F, 1.0F);
        }

        public static int computeInt(Color color, Color color2, double d) {
            double d2 = Math.max(0.0, Math.min(1.0, d));
            return new Color(
                    (int)(color.getRed() + (color2.getRed() - color.getRed()) * d2),
                    (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * d2),
                    (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * d2)
                )
                .getRGB();
        }

        public static int computeInt(int n) {
            return Math.max(0, Math.min(255, n));
        }

        public static void resetColor() {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        public static int computeInt(int n, int n2) {
            return n & (int)timestamp | n2 << 24;
        }

        public static int computeInt(long l) {
            return computeInt(l, 0L);
        }
    }

    public static final class Entry11 {
        private static Vec3 computeVec3(double d, double d2, double d3, Axis axis, double d4, Axis axis2, double d5) {
            if (axis == Axis.X) {
                d += d4;
            } else if (axis == Axis.Y) {
                d2 += d4;
            } else {
                d3 += d4;
            }

            if (axis2 == Axis.X) {
                d += d5;
            } else if (axis2 == Axis.Y) {
                d2 += d5;
            } else {
                d3 += d5;
            }

            return computeVec3(d, d2, d3);
        }

        public static float[] computeFloatArray(float f, float f2, float f3, float f4) {
            float f5 = ColorMathUtil.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f6 = f5 * f5 * f5 * 1.2F;
            return new float[]{
                f3 + Math.round((f - f3) / f6) * f6, MathHelper.clamp_float(f4 + Math.round((f2 - f4) / f6) * f6, -90.0F, 90.0F)
            };
        }

        private static Vec3 computeVec3(double d, double d2, double d3) {
            double d4 = 0.04;
            double d5 = 0.01;
            if (d % 1.0 != 0.0) {
                double d6;
                double d7 = Math.floor(d6 = d + ColorMathUtil.Entry5.computeDouble(-d4, d4));
                double d8 = Math.ceil(d6);
                d = Math.max(d7 + d5, Math.min(d8 - d5, d6));
            }

            if (d2 % 1.0 != 0.0) {
                double d9;
                double d10 = Math.floor(d9 = d2 + ColorMathUtil.Entry5.computeDouble(-d4, d4));
                double d11 = Math.ceil(d9);
                d2 = Math.max(d10 + d5, Math.min(d11 - d5, d9));
            }

            if (d3 % 1.0 != 0.0) {
                double d12;
                double d13 = Math.floor(d12 = d3 + ColorMathUtil.Entry5.computeDouble(-d4, d4));
                double d14 = Math.ceil(d12);
                d3 = Math.max(d13 + d5, Math.min(d14 - d5, d12));
            }

            return new Vec3(d, d2, d3);
        }

        public static ColorMathUtil.Entry11.Entry computeEntry(BlockPos pos, EnumFacing facing) {
            Vec3 positionEyes = ColorMathUtil.mc.thePlayer.getPositionEyes(1.0F);

            for (Vec3 vec : computeList(pos, facing)) {
                double d = vec.xCoord - positionEyes.xCoord;
                double d2 = vec.yCoord - positionEyes.yCoord;
                double d3 = vec.zCoord - positionEyes.zCoord;
                float f = (float)(Math.toDegrees(Math.atan2(d3, d)) - 90.0);
                float f2 = (float)(-Math.toDegrees(Math.atan2(d2, Math.sqrt(d * d + d3 * d3))));
                if (ColorMathUtil.Entry9.computeMovingObjectPosition(pos, facing, ColorMathUtil.mc.playerController.getBlockReachDistance(), f, f2) != null) {
                    return new ColorMathUtil.Entry11.Entry(f, f2, vec);
                }
            }

            return null;
        }

        public static List<Vec3> computeList(BlockPos pos, EnumFacing facing) {
            ArrayList list = new ArrayList(33);
            double d = pos.getX() + 0.5 + 0.5 * facing.getFrontOffsetX();
            double d2 = pos.getY() + 0.5 + 0.5 * facing.getFrontOffsetY();
            double d3 = pos.getZ() + 0.5 + 0.5 * facing.getFrontOffsetZ();
            Axis axis;
            Axis axis2;
            if (Math.abs(facing.getFrontOffsetY()) == 1) {
                axis = Axis.X;
                axis2 = Axis.Z;
            } else if (Math.abs(facing.getFrontOffsetZ()) == 1) {
                axis = Axis.X;
                axis2 = Axis.Y;
            } else {
                axis = Axis.Z;
                axis2 = Axis.Y;
            }

            double d4 = 0.245;
            list.add(computeVec3(d, d2, d3));

            for (int i = 1; i <= 2; i++) {
                list.add(computeVec3(d, d2, d3, axis, i * d4, axis2, 0.0));
                list.add(computeVec3(d, d2, d3, axis, -i * d4, axis2, 0.0));
                list.add(computeVec3(d, d2, d3, axis, 0.0, axis2, i * d4));
                list.add(computeVec3(d, d2, d3, axis, 0.0, axis2, -i * d4));

                for (int j = -i; j <= i; j++) {
                    for (int k = ~i + 1; k <= i; k++) {
                        int n = Math.abs(j);
                        int n2;
                        if (Math.max(n, n2 = Math.abs(k)) == i && (j != 0 || n2 != i) && (k != 0 || n != i)) {
                            list.add(computeVec3(d, d2, d3, axis, j * d4, axis2, k * d4));
                        }
                    }
                }
            }

            return list;
        }

        public static final class Entry {
            public final float factor;
            public final float factor2;
            public final Vec3 vec3;

            public Entry(float factor, float f, Vec3 vec3) {
                this.factor = factor;
                this.factor2 = f;
                this.vec3 = vec3;
            }
        }
    }

    public static final class Entry12 {
        public static int computeInt(Entity entity, int n) {
            double d = entity.posX - ColorMathUtil.mc.thePlayer.posX;
            double d2 = entity.posZ - ColorMathUtil.mc.thePlayer.posZ;
            double dist = Math.sqrt(d * d + d2 * d2);
            if (dist < 0.001) {
                return -1;
            }
            double d3 = d / dist;
            double d4 = d2 / dist;
            double d5 = -d4;
            double d6 = d3;
            int[] ints = new int[3];

            for (int n2 : new int[]{1, 2, 3, 4, 6, 8}) {
                int n3 = n2 < 6 ? 2 : 1;

                for (int i = -1; i <= 1; i++) {
                    double[][] doublesArray = new double[][]{
                        {entity.posX + d3 * n2 + d5 * i, entity.posZ + d4 * n2 + d6 * i},
                        {entity.posX - d5 * n2 + d3 * i, entity.posZ - d6 * n2 + d4 * i},
                        {entity.posX + d5 * n2 + d3 * i, entity.posZ + d6 * n2 + d4 * i}
                    };

                    for (int j = 0; j < 3; j++) {
                        if (ColorMathUtil.mc
                                .theWorld
                                .rayTraceBlocks(
                                    new Vec3(doublesArray[j][0], entity.posY, doublesArray[j][1]),
                                    new Vec3(doublesArray[j][0], entity.posY - 6.0, doublesArray[j][1]),
                                    true
                                )
                            == null) {
                            ints[j] += n3;
                        }
                    }
                }
            }

            if (ints[1] == 0 && ints[2] == 0 || ints[0] >= ints[1] && ints[0] >= ints[2]) {
                return -1;
            }
            if (ints[1] == ints[2] && n != -1) {
                return n;
            }
            return ints[1] > ints[2] ? 0 : 1;
        }
    }

    public static final class Entry2 {
        public static boolean checkState() {
            return ColorMathUtil.mc.thePlayer != null && ColorMathUtil.mc.theWorld != null;
        }
    }

    public static final class Entry3 {
        public static boolean matchesItemStack(ItemStack stack) {
            return GetBlockHelper.matchesItemStack(stack) && GetBlockHelper.matchesBlock(GetBlockHelper.computeBlock(stack));
        }

        public static boolean matchesItemStack2(ItemStack stack) {
            if (stack == null) {
                return false;
            }
            Item item = stack.getItem();
            return item instanceof ItemSnowball
                || item instanceof ItemEgg
                || item instanceof ItemEnderPearl
                || item instanceof ItemExpBottle
                || item == Items.fire_charge
                || item == Items.ender_eye
                || item == Items.nether_star
                || item instanceof ItemPotion && ItemPotion.isSplash(stack.getMetadata());
        }
    }

    public static final class Entry4 {
        public static void handleKeyBinding(KeyBinding keyBinding) {
            KeyBinding.onTick(keyBinding.getKeyCode());
        }
    }

    public static final class Entry5 {
        public static double computeDouble(double d, double d2) {
            return ThreadLocalRandom.current().nextDouble(d, d2);
        }

        public static float computeFloat(float f, float f2) {
            return (float)ThreadLocalRandom.current().nextDouble(f, f2);
        }
    }

    public static final class Entry6 {
        public static boolean checkState2() {
            return ColorMathUtil.Entry2.checkState() && ColorMathUtil.mc.currentScreen == null;
        }

        public static boolean checkState() {
            return false;
        }
    }

    public static final class Entry7 {
        private final Module module;
        private TimeoutHandler2 timeoutHandler2;

        public void process() {
            if (this.timeoutHandler2 != null) {
                this.timeoutHandler2.process();
                this.timeoutHandler2 = null;
            }
        }

        public Entry7(Module module) {
            this.module = module;
        }

        public void open() {
            if (this.timeoutHandler2 == null && Brutality.getLagHandler() != null && !ColorMathUtil.mc.isSingleplayer()) {
                this.timeoutHandler2 = new TimeoutHandler2(this.module);
                Brutality.getLagHandler().handleApihHandler(new ApihHandler(InboundType.ONLY_OUTBOUND, this.timeoutHandler2));
            }
        }

        public void close() {
            if (this.timeoutHandler2 != null) {
                this.timeoutHandler2.process();
                this.timeoutHandler2 = null;
                if (Brutality.getLagHandler() != null) {
                    Brutality.getLagHandler().process();
                }
            }
        }
    }

    public static final class Entry8 {
        private static int count = Integer.MIN_VALUE;
        private static int count2 = Integer.MIN_VALUE;
        private static int count3 = Integer.MIN_VALUE;
        private static int count4 = Integer.MIN_VALUE;

        public static void handlePacket(Packet<?> packet) {
            if (ColorMathUtil.mc.thePlayer != null) {
                int ticksExisted = ColorMathUtil.mc.thePlayer.ticksExisted;
                if (packet instanceof C02PacketUseEntity) {
                    count = ticksExisted;
                } else if (packet instanceof C08PacketPlayerBlockPlacement) {
                    count3 = ticksExisted;
                } else if (packet instanceof C07PacketPlayerDigging) {
                    count2 = ticksExisted;
                    Action status = ((C07PacketPlayerDigging)packet).getStatus();
                    if (status == Action.START_DESTROY_BLOCK || status == Action.STOP_DESTROY_BLOCK || status == Action.ABORT_DESTROY_BLOCK) {
                        count4 = ticksExisted;
                    }
                }
            }
        }

        public static boolean checkState2() {
            return ColorMathUtil.mc.thePlayer != null
                && count != ColorMathUtil.mc.thePlayer.ticksExisted
                && count3 != ColorMathUtil.mc.thePlayer.ticksExisted
                && count4 != ColorMathUtil.mc.thePlayer.ticksExisted
                && !ColorMathUtil.mc.thePlayer.isUsingItem()
                && !ColorMathUtil.mc.playerController.isHittingBlock;
        }

        public static boolean checkState() {
            return ColorMathUtil.mc.thePlayer != null
                && count != ColorMathUtil.mc.thePlayer.ticksExisted
                && count2 != ColorMathUtil.mc.thePlayer.ticksExisted
                && !ColorMathUtil.mc.thePlayer.isUsingItem()
                && !ColorMathUtil.mc.playerController.isHittingBlock;
        }
    }

    public static final class Entry9 {
        public static MovingObjectPosition computeMovingObjectPosition(double d) {
            return ColorMathUtil.mc.thePlayer.rayTrace(d, 1.0F);
        }

        public static int computeInt() {
            return AutoTool.computeInt();
        }

        public static boolean isAdventure() {
            return ColorMathUtil.mc.playerController.getCurrentGameType().isAdventure();
        }

        public static MovingObjectPosition computeMovingObjectPosition(BlockPos pos, EnumFacing facing, double d, float f, float f2) {
            Vec3 positionEyes = ColorMathUtil.mc.thePlayer.getPositionEyes(1.0F);
            Vec3 vec = ColorMathUtil.computeVec3(f, f2);
            MovingObjectPosition hit = ColorMathUtil.mc
                .theWorld
                .rayTraceBlocks(positionEyes, positionEyes.addVector(vec.xCoord * d, vec.yCoord * d, vec.zCoord * d), false, false, true);
            return hit != null && hit.typeOfHit == MovingObjectType.BLOCK && pos.equals(hit.getBlockPos()) && hit.sideHit == facing ? hit : null;
        }

        public static boolean checkState2() {
            return checkState6()
                || Autoblockin.active
                || Displace.instance != null && Displace.instance.checkState6()
                || KillAura.instance != null && KillAura.instance.checkState44() && KillAura.entityLivingBase2 != null
                || OnJumpHelper.checkState();
        }

        public static boolean checkState3() {
            ItemStack heldItem = ColorMathUtil.mc.thePlayer.getHeldItem();
            return heldItem != null && (heldItem.getItem() instanceof ItemTool || heldItem.getItem() instanceof ItemShears || heldItem.getItem() instanceof ItemHoe);
        }

        public static boolean id() {
            return CountMovementHelper.getInstance().getNoneType() != NoneType.NONE;
        }

        public static boolean checkState() {
            ItemStack heldItem = ColorMathUtil.mc.thePlayer.getHeldItem();
            return heldItem != null && (heldItem.getItem() instanceof ItemSword || heldItem.getItem() instanceof ItemAxe);
        }

        public static boolean checkState4() {
            return AutoClicker.instance != null && AutoClicker.instance.checkState44();
        }

        public static boolean isCreative() {
            return ColorMathUtil.mc.thePlayer.capabilities.isCreativeMode;
        }

        public static boolean checkState5() {
            return ColorMathUtil.mc.thePlayer.isInWater() || ColorMathUtil.mc.thePlayer.isInLava();
        }

        public static void handleInt(int n, boolean flag) {
            if (n >= 0 && n <= 8 && ColorMathUtil.mc.thePlayer != null) {
                ColorMathUtil.mc.thePlayer.inventory.currentItem = n;
                ColorMathUtil.mc.playerController.syncCurrentPlayItem();
            }
        }

        public static boolean matchesEntityLivingBase(EntityLivingBase living) {
            return ColorMathUtil.mc.thePlayer.isOnSameTeam(living);
        }

        public static boolean checkState6() {
            return Scaffold.getInstance() != null && Scaffold.getInstance().checkState44();
        }

        public static void handleFlag(boolean flag, boolean flag2) {
            if (flag) {
                ColorMathUtil.mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            } else {
                ColorMathUtil.mc.thePlayer.swingItem();
            }
        }

        public static boolean matchesInt(int n) {
            return KillAura.instance != null && KillAura.instance.checkState44() && KillAura.entityLivingBase2 != null
                || ColorMathUtil.mc.objectMouseOver != null && ColorMathUtil.mc.objectMouseOver.entityHit instanceof EntityLivingBase;
        }

        public static boolean matchesEntity(Entity entity) {
            return entity != null && AntiBot.matchesEntity(entity);
        }
    }
}
