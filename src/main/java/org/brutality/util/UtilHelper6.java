package org.brutality.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class UtilHelper6 {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float computeFloat() {
        return 0.45979762F;
    }

    public static float computeFloat(float f) {
        return Math.round(f / computeFloat()) * computeFloat();
    }

    public static void handleFloat(float f, float f2, float f3, float f4) {
        EntityPlayerSP player = mc.thePlayer;
        player.rotationYaw = computeFloat(computeFloat(player.rotationYaw, f, f3));
        player.rotationPitch = computeFloat(computeFloat(player.rotationPitch, f2, f4));
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        MovingObjectPosition hit = mc.theWorld
            .rayTraceBlocks(
                new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
                new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ),
                false,
                true,
                false
            );
        return hit != null ? hit.typeOfHit != MovingObjectType.BLOCK : true;
    }

    public static float computeFloat(float f, float f2, float f3, float f4) {
        float f5 = MathHelper.wrapAngleTo180_float(f3 - f);
        float f6 = f4 - f2;
        return (float)Math.sqrt(f5 * f5 + f6 * f6);
    }

    public static float computeFloat(float f, float f2, float f3) {
        float f4 = MathHelper.wrapAngleTo180_float(f2 - f);
        return f + MathHelper.clamp_float(f4, -f3, f3);
    }

    public static boolean matchesEntityPlayer(EntityPlayer player, float f, float f2) {
        if (mc.thePlayer != null && player != null) {
            double d = mc.thePlayer.posX - player.posX;
            double d2 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - (player.posY + player.getEyeHeight());
            double d3 = mc.thePlayer.posZ - player.posZ;
            double d4 = MathHelper.sqrt_double(d * d + d3 * d3);
            if (d4 < 0.2) {
                return false;
            }
            float f3 = (float)Math.toDegrees(Math.atan2(d3, d)) - 90.0F;
            float f4 = (float)(-Math.toDegrees(Math.atan2(d2, d4)));
            float f5 = MathHelper.wrapAngleTo180_float(player.rotationYaw - f3);
            float f6 = MathHelper.wrapAngleTo180_float(player.rotationPitch - f4);
            return Math.abs(f5) < f && Math.abs(f6) < f2;
        }
        return false;
    }
}
