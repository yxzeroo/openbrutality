package org.brutality.utils.rotation;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.brutality.utils.mcp.McpApi;
import org.brutality.utils.player.PlayerHelper5;

public class IsMovingHelper {
    public static boolean isMoving() {
        return McpApi.mc.thePlayer.movementInput.moveForward != 0.0F || McpApi.mc.thePlayer.movementInput.moveStrafe != 0.0F;
    }

    public static float[] computeFloatArray(EntityLivingBase living) {
        Minecraft mc = Minecraft.getMinecraft();
        float yaw = mc.thePlayer.rotationYaw;
        float pitch = mc.thePlayer.rotationPitch;
        return computeFloatArray(living, yaw, pitch);
    }

    public static float[] computeFloatArray(double d, double d2, double d3) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        double d4 = d - player.posX;
        double d5 = d2 - (player.posY + player.getEyeHeight());
        double d6 = d3 - player.posZ;
        double d7 = MathHelper.sqrt_double(d4 * d4 + d6 * d6);
        float f = (float)(Math.atan2(d6, d4) * 180.0 / Math.PI) - 90.0F;
        float f2 = (float)(-(Math.atan2(d5, d7) * 180.0 / Math.PI));
        f = MathHelper.wrapAngleTo180_float(f);
        f2 = MathHelper.clamp_float(f2, -90.0F, 90.0F);
        return new float[]{f, f2};
    }

    public static float computeFloat(float f, float f2) {
        return MathHelper.wrapAngleTo180_float(f - f2);
    }

    public static float[] computeFloatArray(Entity entity, float f, float f2) {
        Minecraft mc = Minecraft.getMinecraft();
        ArrayList<double[]> list = new ArrayList<>();
        list.add(new double[]{entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ});
        list.add(new double[]{entity.posX, entity.posY + entity.height / 2.0F, entity.posZ});
        if ((int)mc.thePlayer.posY != (int)entity.posY) {
            list.add(new double[]{entity.posX, entity.posY + 0.3, entity.posZ});
        }

        float[] floats = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (double[] doubles : list) {
            float[] floats2 = computeFloatArray(
                doubles[0] + (isMoving() ? PlayerHelper5.computeDouble5(0.03, -0.03) : 0.0),
                doubles[1] + (isMoving() ? PlayerHelper5.computeDouble5(0.07, -0.07) : 0.0),
                doubles[2] + (isMoving() ? PlayerHelper5.computeDouble5(0.03, -0.03) : 0.0)
            );
            double d = Math.abs(computeFloat(floats2[0], f));
            double d2 = Math.abs(computeFloat(floats2[1], f2));
            double d3 = d + d2;
            if (d3 < MAX_VALUE) {
                MAX_VALUE = d3;
                floats = floats2;
            }
        }

        return floats;
    }

    public static float[] computeFloatArray(Entity entity) {
        Minecraft mc = Minecraft.getMinecraft();
        float yaw = mc.thePlayer.rotationYaw;
        float pitch = mc.thePlayer.rotationPitch;
        return computeFloatArray(entity, yaw, pitch);
    }
}
