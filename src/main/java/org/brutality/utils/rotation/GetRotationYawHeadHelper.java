package org.brutality.utils.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class GetRotationYawHeadHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static int state = -1;
    private static float prevRenderYawOffset;
    private static float renderYawOffset;
    private static float prevRotationYawHead;
    private static float rotationYawHead;
    private static float prevRotationPitch;
    private static float rotationPitch;
    private static float factor;
    private static int priority;
    private static EntityPlayerSP entityPlayerSP;
    private static float factor2;
    private static float factor3;
    private static float factor4;
    private static float factor5;
    private static float factor6;
    private static float factor7;

    public static float getRotationPitch() {
        return rotationPitch;
    }

    public static void handleFlag(boolean flag, float f, float f2, float factor2, int priority2) {
        state = flag ? 0 : state + 1;
        prevRenderYawOffset = renderYawOffset;
        renderYawOffset = flag ? computeFloat(f, renderYawOffset) : mc.thePlayer.renderYawOffset;
        prevRotationYawHead = rotationYawHead;
        rotationYawHead = flag ? f : mc.thePlayer.rotationYawHead;
        prevRotationPitch = rotationPitch;
        rotationPitch = flag ? f2 : mc.thePlayer.rotationPitch;
        factor = factor2;
        priority = priority2;
    }

    public static void handleEntityPlayerSP(EntityPlayerSP entityPlayerSP2, float f, float f2) {
        if (entityPlayerSP2 == mc.thePlayer) {
            if (entityPlayerSP != entityPlayerSP2) {
                entityPlayerSP = entityPlayerSP2;
                factor2 = entityPlayerSP2.prevRotationYawHead;
                factor4 = entityPlayerSP2.prevRotationPitchHead;
                factor6 = entityPlayerSP2.prevRenderYawOffset;
            }

            factor3 = factor2;
            factor5 = factor4;
            factor7 = factor6;
            factor2 = factor3 + MathHelper.wrapAngleTo180_float(f - factor3);
            factor4 = f2;
            factor6 = computeFloat(factor2, factor7);
        }
    }

    public static float getPrevRotationYawHead() {
        return prevRotationYawHead;
    }

    public static float getPrevRotationPitch() {
        return prevRotationPitch;
    }

    public static float getRenderYawOffset() {
        return renderYawOffset;
    }

    public static boolean matchesInt(int n) {
        return state < 0 ? false : state <= n;
    }

    public static float getPrevRenderYawOffset() {
        return prevRenderYawOffset;
    }

    public static void reset() {
        entityPlayerSP = null;
        state = -1;
        if (mc.thePlayer != null) {
            float yaw = mc.thePlayer.rotationYaw;
            float pitch = mc.thePlayer.rotationPitch;
            prevRenderYawOffset = renderYawOffset = mc.thePlayer.renderYawOffset;
            prevRotationYawHead = rotationYawHead = mc.thePlayer.rotationYawHead;
            rotationPitch = pitch;
            prevRotationPitch = pitch;
            factor = yaw;
        } else {
            renderYawOffset = 0.0F;
            prevRenderYawOffset = 0.0F;
            rotationYawHead = 0.0F;
            prevRotationYawHead = 0.0F;
            rotationPitch = 0.0F;
            prevRotationPitch = 0.0F;
            factor = 0.0F;
        }

        priority = 0;
    }

    public static float getFactor() {
        return factor;
    }

    private static float computeFloat(float f, float f2) {
        float f3 = f2;
        double d = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double d2 = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        if ((float)(d * d + d2 * d2) > 0.0025000002F) {
            f3 = (float)MathHelper.atan2(d2, d) * 180.0F / (float) Math.PI - 90.0F;
        }

        if (mc.thePlayer.swingProgress > 0.0F) {
            f3 = f;
        }

        float f4 = MathHelper.wrapAngleTo180_float(f3 - f2);
        float f5;
        float f6 = MathHelper.wrapAngleTo180_float(f - (f5 = f2 + f4 * 0.3F));
        if (f6 < -75.0F) {
            f6 = -75.0F;
        }

        if (f6 >= 75.0F) {
            f6 = 75.0F;
        }

        float f7 = f - f6;
        if (f6 * f6 > 2500.0F) {
            f7 += f6 * 0.2F;
        }

        return f7;
    }

    public static float getRotationYawHead() {
        return rotationYawHead;
    }

    public static float getPriority() {
        return priority;
    }

    public static boolean checkState() {
        return matchesInt(0);
    }

    public static float[] computeFloatArray(Entity entity, float f) {
        if (entity == entityPlayerSP && entity == mc.thePlayer) {
            float f2 = MathHelper.clamp_float(f, 0.0F, 1.0F);
            return new float[]{
                factor7 + MathHelper.wrapAngleTo180_float(factor6 - factor7) * f2,
                factor3 + MathHelper.wrapAngleTo180_float(factor2 - factor3) * f2,
                factor5 + (factor4 - factor5) * f2
            };
        }
        return null;
    }
}
