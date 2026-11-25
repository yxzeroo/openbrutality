package org.brutality.utils.rotation;

import java.security.SecureRandom;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.movement.GetYawEvent;
import org.brutality.event.impl.movement.GetYawEvent2;
import org.brutality.event.impl.movement.MovementEvent3;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.MovementFix;
import org.brutality.utils.mcp.McpApi;
import org.brutality.utils.player.PlayerHelper4;

public class OnJumpHelper implements McpApi {
    public static float[] floatValueArray = new float[]{0.0F, 0.0F};
    public static float[] floatValueArray2 = null;
    public static boolean isEnabled = false;
    private static float factor = 3.0F;
    private static int count;
    private static float factor2;
    private static float factor3;
    private static float factor4;
    private static float factor5;
    private static boolean flag = false;
    public static boolean flag2 = false;
    public static Float floatValue = null;
    public static Float floatValue2 = null;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final PlayerHelper4 playerHelper4 = new PlayerHelper4();
    private static final float factor6 = 0.001F;
    private static EntityLivingBase entityLivingBase = null;
    private static float[] floatValueArray3 = new float[]{0.0F, 0.0F};
    private static final long timestamp = -3513183025066147838L;

    public static Vec3 computeVec3(EntityLivingBase living) {
        AxisAlignedBB entityBoundingBox = living.getEntityBoundingBox();
        return computeVec3(mc.thePlayer.getPositionEyes(1.0F), entityBoundingBox);
    }

    public static float computeFloat(double d, double d2) {
        return (float)(Math.atan2(d - mc.thePlayer.posX, d2 - mc.thePlayer.posZ) * (float) (180.0 / Math.PI) * -1.0);
    }

    public static boolean matchesEntityLivingBase(EntityLivingBase living, float f) {
        if (living == null) {
            return false;
        }
        float yaw = mc.thePlayer.rotationYaw;
        double d = living.posX - mc.thePlayer.posX;
        double d2 = living.posZ - mc.thePlayer.posZ;
        float f2 = (float)(Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0F;
        f2 = MathHelper.wrapAngleTo180_float(f2);
        float f3 = Math.abs(MathHelper.wrapAngleTo180_float(f2 - yaw));
        return f3 <= f / 2.0F;
    }

    public static MovingObjectPosition computeMovingObjectPosition(Entity entity) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        float collisionBorderSize = entity.getCollisionBorderSize();
        AxisAlignedBB box = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
        Vec3 vec = computeVec3(positionEyes, box);
        return mc.theWorld.rayTraceBlocks(positionEyes, vec);
    }

    public static void handleEntity(Entity entity, boolean flag, float f, float f2, boolean flag2, boolean flag3, double d) {
        handleEntity(entity, flag, f, f2, flag2, flag3, d, 0.0);
    }

    public static void handleFloat(float f, float f2, float f3, float f4) {
        computeFloatArray2(f, f2, f3, f4, true);
    }

    public static double computeDouble(float f, double d, double d2) {
        if (d == 0.0 && d2 == 0.0) {
            return Math.toRadians(f);
        }
        double rad = Math.toRadians(f);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);
        double d3 = d * cos - d2 * sin;
        double d4 = d * sin + d2 * cos;
        return Math.atan2(d4, d3);
    }

    public static float[] computeFloatArray(float f, float f2, float f3, boolean flag) {
        return computeFloatArray2(f, f2, f3 * 10.0F, 50.0F, flag);
    }

    public static final Vec3 computeVec3(float f, float f2) {
        float cos = MathHelper.cos(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float sin = MathHelper.sin(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f3 = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
        float sin2 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
        return new Vec3(sin * f3, sin2, cos * f3);
    }

    public static void handleFloat(float f, float f2, int count2) {
        float[] floats = computeFloatArray2(f, f2, 30.0F, 0.0F, false);
        float f3 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f4 = f3 * f3 * f3 * 8.0F;
        float f5 = MathHelper.wrapAngleTo180_float(floats[0] - floatValueArray[0]);
        float f6 = MathHelper.wrapAngleTo180_float(floats[1] - floatValueArray[1]);
        f5 -= f5 % (f4 * 0.15F);
        f6 -= f6 % (f4 * 0.15F);
        float f7 = floatValueArray[0] + f5;
        float f8 = floatValueArray[1] + f6;
        floatValueArray2 = new float[]{f7, MathHelper.clamp_float(f8, -90.0F, 90.0F)};
        count = count2;
        isEnabled = true;
        flag = true;
    }

    public static MovingObjectPosition computeMovingObjectPosition(AxisAlignedBB box, float f, float f2, double d) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        Vec3 vec = computeVec3(f2, f);
        Vec3 vec2 = positionEyes.addVector(vec.xCoord * d, vec.yCoord * d, vec.zCoord * d);
        return box.calculateIntercept(positionEyes, vec2);
    }

    public static void handleEntity(Entity entity, boolean flag, float f) {
        handleEntity(entity, flag, f * 10.0F, 50.0F);
    }

    public static void process() {
        if (KillAura.getInstance() == null
            || !KillAura.getInstance().checkState44() && !KillAura.checkState48()
            || KillAura.getInstance().floatValueArray5 == null) {
            floatValue2 = null;
            floatValue = null;
            flag2 = false;
            flag = false;
        }
    }

    public static float[] computeFloatArray(float[] floats, float[] floats2) {
        float mouseSensitivity = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
        float f = mouseSensitivity * 0.6F + 0.2F;
        float f2 = f * f * f * 8.0F * 0.15F;
        float f3 = MathHelper.wrapAngleTo180_float(floats[0] - floats2[0]);
        float f4 = computeFloat6(floats2[0], floats2[0] + Math.round(f3 / f2) * f2);
        float f5 = computeFloat6(floats2[1], floats2[1] + Math.round((floats[1] - floats2[1]) / f2) * f2);
        return new float[]{f4, MathHelper.clamp_float(f5, -90.0F, 90.0F)};
    }

    public static float[] computeFloatArray(double d, double d2, double d3) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        double d4 = positionEyes.xCoord - d;
        double d5 = positionEyes.yCoord - d2;
        double d6 = positionEyes.zCoord - d3;
        double d7 = Math.hypot(d4, d6);
        double deg = Math.toDegrees(Math.atan2(d6, d4));
        double deg2 = Math.toDegrees(Math.atan2(d5, d7));
        float f = MathHelper.wrapAngleTo180_float((float)deg + 90.0F);
        float f2 = MathHelper.clamp_float((float)deg2, -90.0F, 90.0F);
        return new float[]{f, f2};
    }

    public void onJump(GetYawEvent2 getYawEvent2) {
        if (this.checkState2() && floatValue != null && !floatValue.isNaN()) {
            getYawEvent2.setYaw(floatValue);
        }
    }

    public static void onTick() {
        KillAura.process27();
        if (floatValueArray2 != null) {
            process();
            count--;
            if (count <= 0) {
                count = 0;
                floatValueArray2 = null;
                isEnabled = false;
                flag = false;
            }
        }
    }

    public static void disable() {
        isEnabled = false;
        floatValueArray2 = null;
        count = 0;
        flag = false;
        flag2 = false;
        floatValue = null;
        floatValue2 = null;
        if (mc.thePlayer != null) {
            floatValueArray = mc.thePlayer.getLastReportedRotations();
        }

        GetVectorForRotationHelper.floatValueArray = floatValueArray;
        process2();
        GetRotationYawHeadHelper.reset();
    }

    public void process3() {
    }

    public static float computeFloat(Entity entity) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        float collisionBorderSize = entity.getCollisionBorderSize();
        AxisAlignedBB box = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
        if (box.isVecInside(positionEyes)) {
            return 0.0F;
        }
        double d = entity.posX - positionEyes.xCoord;
        double d2 = entity.posZ - positionEyes.zCoord;
        return Math.abs(MathHelper.wrapAngleTo180_float((float)(Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0F - mc.thePlayer.rotationYaw)) * 2.0F;
    }

    public static float computeFloat(float f, float f2) {
        return MathHelper.wrapAngleTo180_float(f - f2);
    }

    public static float computeFloat(EntityLivingBase living) {
        float collisionBorderSize = living.getCollisionBorderSize();
        AxisAlignedBB box = living.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
        return computeFloat(box);
    }

    public static void handleFloatArray(float[] floats, int count2) {
        floatValueArray2 = (float[])floats.clone();
        count = count2;
        isEnabled = true;
        flag = true;
    }

    @Deprecated
    public static float[] computeFloatArray(AxisAlignedBB box, float f, float f2, float f3, float f4) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        double d = (box.minX + box.maxX) / 2.0;
        double d2 = (box.minZ + box.maxZ) / 2.0;
        double d3 = box.maxY - box.minY;
        double d4 = box.minY + d3 * 0.05;
        double d5 = box.minY + d3 * 0.75;
        double d6;
        if (positionEyes.yCoord >= d4 && positionEyes.yCoord <= d5) {
            d6 = positionEyes.yCoord;
        } else if (positionEyes.yCoord < d4) {
            d6 = d4;
        } else {
            d6 = d5;
        }

        double d7 = d - positionEyes.xCoord;
        double d8 = d6 - positionEyes.yCoord;
        double d9 = d2 - positionEyes.zCoord;
        double dist = Math.sqrt(d7 * d7 + d9 * d9);
        float f5 = (float)(Math.atan2(d9, d7) * 180.0 / Math.PI) - 90.0F;
        float f6 = (float)(-(Math.atan2(d8, dist) * 180.0 / Math.PI));
        float f7 = computeFloat(f5 - f);
        float f8 = computeFloat(f6 - f2);
        if (Math.abs(f7) <= 1.0F) {
            f7 = 0.0F;
        }

        if (Math.abs(f8) <= 1.0F) {
            f8 = 0.0F;
        }

        float f9 = computeFloat3(f7, f3);
        float f10 = computeFloat3(f8, f3);
        if (f9 != 0.0F) {
            f9 = computeFloat5(f9, f4);
        }

        if (f10 != 0.0F) {
            f10 = computeFloat5(f10, f4);
        }

        float f11 = computeFloat2(f + f9);
        float f12 = computeFloat2(MathHelper.clamp_float(f2 + f10, -90.0F, 90.0F));
        return new float[]{f11, f12};
    }

    public static void handleDouble(double d, double d2, double d3, double d4) {
        double d5 = d - mc.thePlayer.posX;
        double d6 = d2 - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
        double d7 = d3 - mc.thePlayer.posZ;
        double d8 = Math.hypot(d5, d7);
        float[] floats = new float[]{
            MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(d7, d5)) - 90.0F),
            (float)Math.min(90.0, -Math.toDegrees(Math.atan2(d6, d8)))
        };
        floats = computeFloatArray2(floats[0], floats[1], (float)d4 * 10.0F, 40.0F, false);
        handleFloatArray2(floats, 2);
    }

    public static void process2() {
        if (mc.thePlayer != null) {
            float[] lastReportedRotations = mc.thePlayer.getLastReportedRotations();
            factor2 = factor4 = lastReportedRotations[0];
            factor3 = factor5 = lastReportedRotations[1];
        }
    }

    private static float computeFloat6(float f, float f2) {
        return Math.abs((double)f2 - f) < 0.001F ? f : f2;
    }

    public static void handleFloatArray2(float[] floats, int n) {
        handleFloat(floats[0], floats[1], n);
    }

    public void setPitch(float floatValue22) {
        floatValue2 = floatValue22;
        flag = true;
    }

    private static float computeFloat3(float f, float f2) {
        float f3 = Math.max(0.0F, Math.min(180.0F, f2));
        if (f > f3) {
            return f3;
        }
        return f < -f3 ? -f3 : f;
    }

    private static float computeFloat(float f) {
        float f2 = f % 360.0F;
        if (f2 >= 180.0F) {
            f2 -= 360.0F;
        }

        if (f2 < -180.0F) {
            f2 += 360.0F;
        }

        return f2;
    }

    private static float computeFloat2(float f, float f2) {
        return f + secureRandom.nextFloat() * (f2 - f);
    }

    public static Vec3 getLook(float f) {
        return checkState() ? computeVec3(floatValueArray2[1], floatValueArray2[0]) : null;
    }

    public static float[] computeFloatArray(float f, float f2, float f3, float f4, boolean flag) {
        float f5 = computeFloat(f - factor2);
        float f6 = computeFloat(f2 - factor3);
        float f7 = Math.abs(f5) + Math.abs(f6);
        float f8 = f3;
        float f9 = f4;
        if (f7 > 90.0F) {
            f8 = f3 * 2.0F;
            f9 = f4 * 0.7F;
        } else if (f7 < 5.0F) {
            f8 = f3 * 0.6F;
            f9 = f4 * 1.2F;
        }

        return computeFloatArray2(f, f2, f8, f9, flag);
    }

    public static void handleEntity(Entity entity, boolean flag, float f, float f2, boolean flag2) {
        handleEntity(entity, flag, f, f2, flag2, false, factor);
    }

    public void process6() {
        if (this.checkState2()) {
            if (floatValue != null && !floatValue.isNaN()) {
                float moveForward = mc.thePlayer.movementInput.moveForward;
                float moveStrafe = mc.thePlayer.movementInput.moveStrafe;
                if (moveForward != 0.0F || moveStrafe != 0.0F) {
                    float f = Math.max(Math.abs(moveForward), Math.abs(moveStrafe));
                    double d = MathHelper.wrapAngleTo180_double(Math.toDegrees(computeDouble(mc.thePlayer.rotationYaw, moveForward, moveStrafe)));
                    float f2 = 0.0F;
                    float f3 = 0.0F;
                    float MAX_VALUE = Float.MAX_VALUE;

                    for (float i = -1.0F; i <= 1.0F; i++) {
                        for (float j = -1.0F; j <= 1.0F; j++) {
                            if (i != 0.0F || j != 0.0F) {
                                float f4 = i * f;
                                float f5 = j * f;
                                double d2 = MathHelper.wrapAngleTo180_double(Math.toDegrees(computeDouble(floatValue, f4, f5)));
                                double d3 = Math.abs(MathHelper.wrapAngleTo180_double(d - d2));
                                if (d3 < MAX_VALUE) {
                                    MAX_VALUE = (float)d3;
                                    f2 = f4;
                                    f3 = f5;
                                }
                            }
                        }
                    }

                    mc.thePlayer.movementInput.moveForward = f2;
                    mc.thePlayer.movementInput.moveStrafe = f3;
                }
            }
        }
    }

    public static void handleEntity(Entity entity, boolean flag, float f, float f2) {
        handleEntity(entity, flag, f, f2, false);
    }

    public static void process5() {
        entityLivingBase = null;
        if (mc.thePlayer != null) {
            floatValueArray3 = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        } else {
            floatValueArray3 = new float[]{0.0F, 0.0F};
        }
    }

    public static boolean matchesEntity(Entity entity, double d, float[] floats) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        float f = floats[0];
        float f2 = floats[1];
        float f3 = -f * (float) (Math.PI / 180.0) - (float) Math.PI;
        float f4 = -f2 * (float) (Math.PI / 180.0);
        float cos = MathHelper.cos(f3);
        float sin = MathHelper.sin(f3);
        float f5 = -MathHelper.cos(f4);
        float sin2 = MathHelper.sin(f4);
        Vec3 vec = new Vec3(sin * f5, sin2, cos * f5);
        double d2 = vec.xCoord * d;
        double d3 = vec.yCoord * d;
        double d4 = vec.zCoord * d;
        Vec3 vec2 = positionEyes.addVector(d2, d3, d4);
        Entity renderViewEntity = mc.getRenderViewEntity();
        AxisAlignedBB box = renderViewEntity.getEntityBoundingBox().addCoord(d2, d3, d4).expand(1.0, 1.0, 1.0);

        for (Entity entity2 : mc.theWorld.getEntitiesWithinAABBExcludingEntity(renderViewEntity, box)) {
            if (entity2 == entity && entity2.canBeCollidedWith()) {
                float collisionBorderSize = entity2.getCollisionBorderSize();
                AxisAlignedBB box2 = entity2.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                MovingObjectPosition hit = box2.calculateIntercept(positionEyes, vec2);
                return hit != null;
            }
        }

        return false;
    }

    public static float computeFloat2(float f) {
        return (float)(f - f % 0.0096F);
    }

    public void setYaw(float floatValue2) {
        floatValue = floatValue2;
        flag = true;
    }

    public static float[] computeFloatArray(float[] floats, float[] floats2, float f) {
        float f2 = Math.max(0.0F, Math.min(180.0F, f));
        float[] floats3 = computeFloatArray(floats, floats2);
        float mouseSensitivity = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
        float f3 = mouseSensitivity * 0.6F + 0.2F;
        float f4 = f3 * f3 * f3 * 8.0F * 0.15F;

        for (int i = 0; i < 2; i++) {
            float f5 = floats3[i] - floats2[i];
            if (!(Math.abs(f5) <= f2)) {
                int n = (int)Math.floor(f2 / f4);
                float f6 = floats2[i] + Math.copySign(n * f4, f5);

                while (n > 0 && Math.abs(f6 - floats2[i]) > f2) {
                    f6 = floats2[i] + Math.copySign(--n * f4, f5);
                }

                floats3[i] = computeFloat6(floats2[i], f6);
            }
        }

        return floats3;
    }

    static {
        initialize();
    }

    public static float computeFloat(float f, float f2, float f3) {
        float f4 = 1.0F - Math.min(100.0F, Math.max(0.0F, f2)) / 200.0F;
        float f5 = Math.abs(f);
        return f5 <= f3 ? f : Math.copySign(Math.max(f3, f5 * f4), f);
    }

    public static float[] computeFloatArray2(float f, float f2, float f3, float f4, boolean flag) {
        float f5 = computeFloat(f - factor2);
        float f6 = computeFloat(f2 - factor3);
        f5 = computeFloat3(f5, f3);
        f6 = computeFloat3(f6, f3);
        f5 = computeFloat5(f5, f4 / 100.0F);
        f6 = computeFloat5(f6, f4 / 100.0F);
        float f7 = factor2 + f5;
        float f8 = factor3 + f6;
        f8 = MathHelper.clamp_float(f8, -90.0F, 90.0F);
        float[] floats = computeFloatArray(new float[]{f7, f8}, new float[]{factor4, factor5});
        factor2 = floats[0];
        factor3 = floats[1];
        factor4 = floats[0];
        factor5 = floats[1];
        if (flag && mc.thePlayer != null) {
            mc.thePlayer.rotationYaw = floats[0];
            mc.thePlayer.rotationPitch = floats[1];
        }

        return floats;
    }

    public static float[] computeFloatArray(float f, float f2) {
        float[] lastReportedRotations = mc.thePlayer.getLastReportedRotations();
        float f3 = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - lastReportedRotations[0]);
        float f4 = mc.thePlayer.rotationPitch - lastReportedRotations[1];
        if (Math.abs(f3) <= 3.0F && Math.abs(f4) <= 3.0F) {
            return null;
        }
        float f5 = Math.max(1.0F, f);
        float[] floats = computeFloatArray(
            new float[]{
                lastReportedRotations[0] + computeFloat(computeFloat3(f3, f5), f2, 7.0F),
                MathHelper.clamp_float(lastReportedRotations[1] + computeFloat(computeFloat3(f4, f5), f2, 3.0F), -90.0F, 90.0F)
            },
            lastReportedRotations,
            f5
        );
        handleFloatArray(floats, 2);
        floatValue = floats[0];
        floatValue2 = floats[1];
        return floats;
    }

    public static void handleFloat(float f, float f2, float f3) {
        handleFloat(f, f2, f3 * 10.0F, 50.0F);
    }

    private static float computeFloat5(float f, float f2) {
        f2 = Math.max(0.0F, Math.min(1.0F, f2));
        float f3 = computeFloat2(-0.1F, 0.1F);
        f2 = Math.max(0.0F, Math.min(1.0F, f2 + f3));
        float f4 = 0.5F + 0.5F * (1.0F - f2);
        return f * f4;
    }

    @Generated
    public static float getReach() {
        return factor;
    }

    public static boolean checkState() {
        return floatValueArray2 != null && isEnabled;
    }

    public void onGetYawEvent(GetYawEvent getYawEvent) {
        if (this.checkState2() && floatValue != null && !floatValue.isNaN()) {
            getYawEvent.setYaw(floatValue);
        }
    }

    public static float[] computeFloatArray(float f, float f2, float f3) {
        return computeFloatArray(f, f2, f3, true);
    }

    public static void handleEntity(Entity entity, boolean flag2, float f, float f2, boolean flag3, boolean flag4, double d, double d2) {
        if (entity instanceof EntityLivingBase living) {
            float collisionBorderSize = living.getCollisionBorderSize();
            AxisAlignedBB box = living.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
            double d3 = box.minY + 0.05 * (box.maxY - box.minY);
            double d4 = box.minY + 0.75 * (box.maxY - box.minY);
            double d5 = (box.minX + box.maxX) / 2.0 - positionEyes.xCoord;
            double d6 = (d3 + d4) * 0.5 - positionEyes.yCoord;
            double d7 = (box.minZ + box.maxZ) / 2.0 - positionEyes.zCoord;
            double dist = Math.sqrt(d5 * d5 + d7 * d7);
            float[] floats;
            if (entityLivingBase == living) {
                floats = (float[])floatValueArray3.clone();
            } else {
                floats = (float[])floatValueArray.clone();
                entityLivingBase = living;
            }

            float f3 = (float)(Math.atan2(d7, d5) * 180.0 / Math.PI) - 90.0F;
            float f4 = (float)(-Math.atan2(d6, dist) * 180.0 / Math.PI);
            if (flag2 && !flag3) {
                playerHelper4.handleOpensimplex2Mode2(PlayerHelper4.Opensimplex2Mode2.Perlin);
                f3 += playerHelper4.computeFloat(-5.0F + secureRandom.nextFloat() * 10.0F, -5.0F + secureRandom.nextFloat() * 10.0F) * 10.0F;
                f4 += playerHelper4.computeFloat(-5.0F + secureRandom.nextFloat() * 10.0F, -5.0F + secureRandom.nextFloat() * 10.0F) * 10.0F;
            }

            float f5 = MathHelper.wrapAngleTo180_float(f3 - floats[0]);
            float f6 = MathHelper.wrapAngleTo180_float(f4 - floats[1]);
            if (Math.abs(f5) <= 1.0F) {
                f5 = 0.0F;
            }

            if (Math.abs(f6) <= 1.0F) {
                f6 = 0.0F;
            }

            float f7 = Math.max(0.0F, f - computeFloat2(0.0F, 5.0F));
            float f8 = computeFloat3(f5, f7);
            float f9 = computeFloat3(f6, f7);
            float f10 = f2 / 100.0F;
            if (f8 != 0.0F) {
                f8 = computeFloat5(f8, f10);
            }

            if (f9 != 0.0F) {
                f9 = computeFloat5(f9, f10);
            }

            float f11 = floats[0] + f8;
            float f12 = MathHelper.clamp_float(floats[1] + f9, -90.0F, 90.0F);
            float[] floats2 = flag3
                ? computeFloatArray(new float[]{f3, f4}, floatValueArray)
                : computeFloatArray(new float[]{f11, f12}, floatValueArray);
            floatValueArray2 = floats2;
            floatValueArray3 = (float[])floats2.clone();
            count = (int)timestamp;
            isEnabled = true;
            flag = true;
            floatValue = floats2[0];
            floatValue2 = floats2[1];
        }
    }

    public void onEvent(Event event) {
        if (event instanceof EventRender3D && flag) {
        }

        if (event instanceof EventPreMotion) {
            if (!flag) {
                return;
            }

            if (floatValue != null && !floatValue.isNaN()) {
                ((EventPreMotion)event).setYaw(floatValue);
            }

            if (floatValue2 != null && !floatValue2.isNaN()) {
                ((EventPreMotion)event).setPitch(floatValue2);
            }
        } else if (event instanceof EventUpdate) {
            this.process3();
        } else if (event instanceof MovementEvent3) {
            this.process6();
        } else if (event instanceof GetYawEvent) {
            this.onGetYawEvent((GetYawEvent)event);
        } else if (event instanceof GetYawEvent2) {
            this.onJump((GetYawEvent2)event);
        }
    }

    public static void setEntityLivingBase(EntityLivingBase entityLivingBase2) {
        entityLivingBase = entityLivingBase2;
    }

    public static Vec3 computeVec3(Vec3 vec, AxisAlignedBB box) {
        double d = Math.max(box.minX, Math.min(vec.xCoord, box.maxX));
        double d2 = Math.max(box.minY, Math.min(vec.yCoord, box.maxY));
        double d3 = Math.max(box.minZ, Math.min(vec.zCoord, box.maxZ));
        return new Vec3(d, d2, d3);
    }

    public static float computeFloat4(float f, float f2) {
        return f2 + MathHelper.wrapAngleTo180_float(f - f2);
    }

    private static void initialize() {
        if (Minecraft.getMinecraft() != null && mc.thePlayer != null && mc.theWorld != null) {
            factor2 = mc.thePlayer.rotationYaw;
            factor3 = mc.thePlayer.rotationPitch;
            factor4 = mc.thePlayer.rotationYaw;
            factor5 = mc.thePlayer.rotationPitch;
        } else {
            factor2 = 0.0F;
            factor3 = 0.0F;
            factor4 = 0.0F;
            factor5 = 0.0F;
        }
    }

    public static boolean matchesEntityLivingBase(EntityLivingBase living) {
        return entityLivingBase != living;
    }

    public void handleFloat(float floatValue3, float floatValue22) {
        floatValue = floatValue3;
        floatValue2 = floatValue22;
        flag = true;
    }

    public static float[] computeFloatArray(Vec3 vec) {
        double d = vec.xCoord - mc.thePlayer.posX;
        double d2 = vec.yCoord - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
        double d3 = vec.zCoord - mc.thePlayer.posZ;
        double d4 = Math.hypot(d, d3);
        return new float[]{
            MathHelper.wrapAngleTo180_float((float)(Math.toDegrees(Math.atan2(d3, d)) - 90.0)),
            (float)Math.min(90.0, -Math.toDegrees(Math.atan2(d2, d4)))
        };
    }

    public static MovingObjectPosition rayTrace(double d, float f) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(f);
        Vec3 look = getLook(f);
        if (look == null) {
            return null;
        }
        Vec3 vec = positionEyes.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
        return mc.theWorld.rayTraceBlocks(positionEyes, vec, false, false, true);
    }

    private boolean checkState2() {
        return (flag2 || MovementFix.isEnabled()) && flag;
    }

    public static float computeFloat(AxisAlignedBB box) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        if (box.isVecInside(positionEyes)) {
            return 0.0F;
        }
        Vec3 vec = computeVec3(positionEyes, box);
        double d = vec.xCoord - positionEyes.xCoord;
        double d2 = vec.yCoord - positionEyes.yCoord;
        double d3 = vec.zCoord - positionEyes.zCoord;
        return MathHelper.sqrt_float((float)(d * d + d2 * d2 + d3 * d3));
    }

    @Generated
    public static void setReach(float factor2) {
        factor = factor2;
    }

    public static void process4() {
        factor = 3.0F;
    }
}
