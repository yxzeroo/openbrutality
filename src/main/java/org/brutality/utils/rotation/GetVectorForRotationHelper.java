package org.brutality.utils.rotation;

import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public final class GetVectorForRotationHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static float[] floatValueArray = OnJumpHelper.floatValueArray;

    public static MovingObjectPosition computeMovingObjectPosition2(double d, float f, float[] floats, EntityLivingBase living) {
        process();
        if (living != null) {
            MovingObjectPosition hit = computeMovingObjectPosition(d, f, floats, living);
            if (hit != null) {
                return hit;
            }
        }

        if (mc.thePlayer != null && mc.theWorld != null) {
            if (floats == null) {
                floats = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
            }

            MovingObjectPosition hit2 = computeMovingObjectPosition(d, floats[0], floats[1]);
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(f);
            double d2 = hit2 != null ? hit2.hitVec.distanceTo(positionEyes) : d;
            Vec3 vectorForRotation = getVectorForRotation(floats[1], floats[0]);
            Vec3 vec = positionEyes.addVector(vectorForRotation.xCoord * d, vectorForRotation.yCoord * d, vectorForRotation.zCoord * d);
            Entity entity = null;
            Vec3 vec2 = null;

            for (Entity entity2 : mc.theWorld
                .getEntitiesInAABBexcluding(
                    mc.thePlayer,
                    mc.thePlayer.getEntityBoundingBox().addCoord(vectorForRotation.xCoord * d, vectorForRotation.yCoord * d, vectorForRotation.zCoord * d).expand(1.0, 1.0, 1.0),
                    Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith)
                )) {
                float collisionBorderSize = entity2.getCollisionBorderSize();
                AxisAlignedBB box = entity2.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                MovingObjectPosition hit3 = box.calculateIntercept(positionEyes, vec);
                if (box.isVecInside(positionEyes)) {
                    entity = entity2;
                    vec2 = hit3 == null ? positionEyes : hit3.hitVec;
                    d2 = 0.0;
                } else if (hit3 != null) {
                    double d3 = positionEyes.distanceTo(hit3.hitVec);
                    if (d3 < d2) {
                        entity = entity2;
                        vec2 = hit3.hitVec;
                        d2 = d3;
                    }
                }
            }

            return entity != null ? new MovingObjectPosition(entity, vec2) : hit2;
        }
        return null;
    }

    private GetVectorForRotationHelper() {
    }

    public static MovingObjectPosition computeMovingObjectPosition(double d, float f, float f2) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
            Vec3 vectorForRotation = getVectorForRotation(f2, f);
            Vec3 vec = positionEyes.addVector(vectorForRotation.xCoord * d, vectorForRotation.yCoord * d, vectorForRotation.zCoord * d);
            return mc.theWorld.rayTraceBlocks(positionEyes, vec, false, false, true);
        }
        return null;
    }

    public static void process() {
        floatValueArray = OnJumpHelper.floatValueArray;
    }

    public static float[] computeFloatArray(Vec3 vec) {
        return computeFloatArray(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static MovingObjectPosition computeMovingObjectPosition(double d, float f, float[] floats, EntityLivingBase living) {
        if (mc.thePlayer != null && mc.theWorld != null && living != null) {
            if (floats == null) {
                floats = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
            }

            MovingObjectPosition hit = computeMovingObjectPosition(d, floats[0], floats[1]);
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(f);
            double d2 = hit != null ? hit.hitVec.distanceTo(positionEyes) : d;
            Vec3 vectorForRotation = getVectorForRotation(floats[1], floats[0]);
            Vec3 vec = positionEyes.addVector(vectorForRotation.xCoord * d, vectorForRotation.yCoord * d, vectorForRotation.zCoord * d);
            float collisionBorderSize = living.getCollisionBorderSize();
            AxisAlignedBB box = living.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
            MovingObjectPosition hit2 = box.calculateIntercept(positionEyes, vec);
            if (box.isVecInside(positionEyes)) {
                return new MovingObjectPosition(living, positionEyes);
            }
            return hit2 != null && positionEyes.distanceTo(hit2.hitVec) < d2 ? new MovingObjectPosition(living, hit2.hitVec) : hit;
        }
        return null;
    }

    public static float[] computeFloatArray(Entity entity, float f, float f2) {
        if (entity != null && mc.thePlayer != null) {
            double d = entity.posX - mc.thePlayer.posX;
            double d2 = entity.posZ - mc.thePlayer.posZ;
            double d3;
            if (entity instanceof EntityLivingBase living) {
                d3 = living.posY + living.getEyeHeight() * 0.9 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
            } else {
                d3 = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
            }

            float f3 = (float)(Math.atan2(d2, d) * (float) (180.0 / Math.PI)) - 90.0F;
            float f4 = (float)(-(Math.atan2(d3, MathHelper.sqrt_double(d * d + d2 * d2)) * (float) (180.0 / Math.PI)));
            float f5 = f + MathHelper.wrapAngleTo180_float(f3 - f);
            float f6 = computeFloat(f2 + MathHelper.wrapAngleTo180_float(f4 - f2) + 3.0F);
            return new float[]{f5, f6};
        }
        return null;
    }

    public static float[] computeFloatArray(double d, double d2, double d3) {
        if (mc.thePlayer == null) {
            return null;
        }
        double d4 = d - mc.thePlayer.posX;
        double d5 = d2 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double d6 = d3 - mc.thePlayer.posZ;
        float f = (float)(Math.atan2(d6, d4) * (180.0 / Math.PI)) - 90.0F;
        float f2 = mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(f - mc.thePlayer.rotationYaw);
        double d7 = MathHelper.sqrt_double(d4 * d4 + d6 * d6);
        float f3 = (float)(-(Math.atan2(d5, d7) * (180.0 / Math.PI)));
        float f4 = computeFloat(mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(f3 - mc.thePlayer.rotationPitch));
        return new float[]{f2, f4};
    }

    public static float computeFloat(float f) {
        return MathHelper.clamp_float(f, -90.0F, 90.0F);
    }

    public static Vec3 getVectorForRotation(float f, float f2) {
        float cos = MathHelper.cos(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float sin = MathHelper.sin(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f3 = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
        float sin2 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
        return new Vec3(sin * f3, sin2, cos * f3);
    }

    public static float[] computeFloatArray(Entity entity) {
        return computeFloatArray(entity, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
    }

    public static double computeDouble(Entity entity) {
        if (entity != null && mc.thePlayer != null) {
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
            float collisionBorderSize = entity.getCollisionBorderSize();
            AxisAlignedBB box = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
            Vec3 vec = computeVec3(box, positionEyes);
            double d = positionEyes.xCoord - vec.xCoord;
            double d2 = positionEyes.yCoord - vec.yCoord;
            double d3 = positionEyes.zCoord - vec.zCoord;
            return d * d + d2 * d2 + d3 * d3;
        }
        return Double.MAX_VALUE;
    }

    public static Vec3 computeVec3(AxisAlignedBB box, Vec3 vec) {
        double d = Math.max(box.minX, Math.min(box.maxX, vec.xCoord));
        double d2 = Math.max(box.minY, Math.min(box.maxY, vec.yCoord));
        double d3 = Math.max(box.minZ, Math.min(box.maxZ, vec.zCoord));
        return new Vec3(d, d2, d3);
    }
}
