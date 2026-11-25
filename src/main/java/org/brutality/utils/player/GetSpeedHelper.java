package org.brutality.utils.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.brutality.event.impl.movement.GetYawEvent;
import org.brutality.event.impl.movement.IsSafewalkEvent;
import org.brutality.utils.rotation.OnJumpHelper;

public class GetSpeedHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static boolean flag = false;
    public static boolean flag2 = false;
    public static float factor = 0.0F;
    public static boolean flag3 = false;

    public static double computeDouble4() {
        float movementYaw = mc.thePlayer.movementYaw;
        if (mc.thePlayer.moveForward < 0.0F) {
            movementYaw += 180.0F;
        }

        float f = 1.0F;
        if (mc.thePlayer.moveForward < 0.0F) {
            f = -0.5F;
        } else if (mc.thePlayer.moveForward > 0.0F) {
            f = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0.0F) {
            movementYaw -= 90.0F * f;
        }

        if (mc.thePlayer.moveStrafing < 0.0F) {
            movementYaw += 90.0F * f;
        }

        return Math.toRadians(movementYaw);
    }

    public static float computeFloat2() {
        return mc.thePlayer.getMaxFallHeight()
            + (mc.thePlayer.isPotionActive(Potion.jump) ? mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1 : 0);
    }

    public static void handleFloat2(float f) {
        handleFloat(f, computeFloat());
    }

    public double computeDouble3() {
        float movementYaw = mc.thePlayer.movementYaw;
        if (mc.thePlayer.moveForward < 0.0F && mc.thePlayer.moveStrafing == 0.0F) {
            movementYaw += 180.0F;
        }

        if (mc.thePlayer.moveStrafing > 0.0F) {
            movementYaw -= 90.0F;
        }

        if (mc.thePlayer.moveStrafing < 0.0F) {
            movementYaw += 90.0F;
        }

        return Math.toRadians(movementYaw);
    }

    public static void process4() {
        if (mc.thePlayer.fallDistance > 2.0F) {
            mc.thePlayer.motionY = 0.0;
            mc.thePlayer.fallDistance = 0.0F;
        }
    }

    public static void handleFloat5(float factor2) {
        flag = true;
        factor = factor2;
    }

    public static void process5() {
        if (isMoving()) {
            double d = computeDouble2();
            double d2 = d * 1.2;
            setSpeed((float)d2);
        }
    }

    public static boolean checkState() {
        if (mc.thePlayer == null) {
            return false;
        }
        for (int i = 0; i < 5; i++) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - i, mc.thePlayer.posZ);
            Block block = mc.theWorld.getBlockState(pos).getBlock();
            if (!(block instanceof BlockAir)) {
                return true;
            }
        }

        return false;
    }

    public static void process6() {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? getSpeed() < 0.367 : getSpeed() < 0.255) {
            handleFloat2((float)(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.367 : 0.255));
        }
    }

    public static double computeDouble2() {
        double d = mc.thePlayer.capabilities.getWalkSpeed() * 2.873;
        if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
            d /= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1);
        }

        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            d *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }

        return d;
    }

    public static void handleFloat4(float f) {
        if (isInLiquid() && !mc.thePlayer.isSneaking()) {
            mc.thePlayer.motionX *= f;
            mc.thePlayer.motionZ *= f;
        }
    }

    public static void process() {
        boolean flag = flag3;
        handleFloat2(getSpeed());
        flag3 = flag;
    }

    public static void handleFlag2(boolean flag3) {
        flag = true;
        flag2 = flag3;
    }

    public static float getSpeed() {
        return (float)Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void handleFloat(float f, GetYawEvent getYawEvent) {
        getYawEvent.setYaw(f);
    }

    public static void handleFlag3(boolean flag) {
        mc.thePlayer.setSprinting(flag);
    }

    public static boolean checkState2() {
        return mc.thePlayer != null
            && (
                GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
                    || GameSettings.isKeyDown(mc.gameSettings.keyBindRight)
                    || GameSettings.isKeyDown(mc.gameSettings.keyBindBack)
                    || GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)
            );
    }

    public static boolean isMoving() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0.0F || mc.thePlayer.movementInput.moveStrafe != 0.0F);
    }

    public static void handleIsSafewalkEvent(IsSafewalkEvent isSafewalkEvent, double d) {
        handleIsSafewalkEvent(isSafewalkEvent, d, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe, mc.thePlayer.movementInput.moveForward);
    }

    public void handleDouble3(double d) {
        handleDouble(d, mc.thePlayer);
    }

    public static void handleGetYawEvent(GetYawEvent getYawEvent, float f) {
        EntityPlayerSP player = mc.thePlayer;
        float yaw = player.rotationYaw;
        float factor = getYawEvent.getFactor();
        float factor2 = getYawEvent.getFactor2();
        float factor3 = getYawEvent.getFactor3();
        if (factor != 0.0F || factor2 != 0.0F) {
            float f2 = MathHelper.wrapAngleTo180_float(yaw - f - 23.5F - 135.0F) + 180.0F;
            int n = (int)(f2 / 45.0F);
            float f3 = 0.0F;
            float f4 = 0.0F;
            switch (n) {
                case 0:
                    f3 = factor2;
                    f4 = factor;
                    break;
                case 1:
                    f3 = factor2 + factor;
                    f4 = factor - factor2;
                    break;
                case 2:
                    f3 = factor;
                    f4 = -factor2;
                    break;
                case 3:
                    f3 = factor - factor2;
                    f4 = -factor2 - factor;
                    break;
                case 4:
                    f3 = -factor2;
                    f4 = -factor;
                    break;
                case 5:
                    f3 = -factor - factor2;
                    f4 = factor2 - factor;
                    break;
                case 6:
                    f3 = -factor;
                    f4 = factor2;
                    break;
                case 7:
                    f3 = -factor + factor2;
                    f4 = factor2 + factor;
            }

            if (f3 > 1.0F || f3 < 0.9F && f3 > 0.3F || f3 < -1.0F || f3 > -0.9F && f3 < -0.3F) {
                f3 *= 0.5F;
            }

            if (f4 > 1.0F || f4 < 0.9F && f4 > 0.3F || f4 < -1.0F || f4 > -0.9F && f4 < -0.3F) {
                f4 *= 0.5F;
            }

            getYawEvent.setFactor2(f3);
            getYawEvent.setFactor(f4);
            getYawEvent.setYaw(f);
        }
    }

    public static boolean isInLiquid() {
        int n = MathHelper.floor_double(mc.thePlayer.posX);
        int n2 = MathHelper.floor_double(mc.thePlayer.posY);
        int n3 = MathHelper.floor_double(mc.thePlayer.posZ);
        return mc.theWorld.getBlockState(new BlockPos(n, n2, n3)).getBlock().getMaterial().isLiquid();
    }

    public static void process2() {
        handleDouble(computeDouble5(), mc.thePlayer);
    }

    public static void handleDouble4(double d) {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ);
    }

    public static float computeFloat() {
        return OnJumpHelper.checkState() && OnJumpHelper.flag2
            ? computeFloat(mc.thePlayer.rotationYaw)
            : computeFloat(mc.thePlayer.rotationYaw, mc.thePlayer.moveForward, mc.thePlayer.moveStrafing);
    }

    public static void handleDouble2(double d) {
        mc.thePlayer
            .setPosition(
                mc.thePlayer.posX + -MathHelper.sinVanilla(computeFloat(mc.thePlayer.rotationYaw)) * d,
                mc.thePlayer.posY,
                mc.thePlayer.posZ + MathHelper.cosVanilla(computeFloat(mc.thePlayer.rotationYaw)) * d
            );
    }

    public static float computeFloat(float f, float f2, float f3) {
        if (f2 == 0.0F && f3 == 0.0F) {
            return f;
        }
        boolean flag = f2 < 0.0F;
        double d = 90.0 * (f2 > 0.0F ? 0.5 : (flag ? -0.5 : 1.0));
        if (flag) {
            f += 180.0F;
        }

        if (f3 > 0.0F) {
            f = (float)(f - d);
        } else if (f3 < 0.0F) {
            f = (float)(f + d);
        }

        return f;
    }

    public static void handleFloat(float f) {
        if (mc.thePlayer.onGround) {
            mc.thePlayer.motionY = f;
        }
    }

    public static float computeFloat(float f) {
        int n = 0;
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindForward)) {
            n++;
        }

        if (GameSettings.isKeyDown(mc.gameSettings.keyBindBack)) {
            n--;
        }

        int n2 = 0;
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) {
            n2++;
        }

        if (GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) {
            n2--;
        }

        boolean flag = n < 0;
        double d = 90.0 * (n > 0 ? 0.5 : (flag ? -0.5 : 1.0));
        if (flag) {
            f += 180.0F;
        }

        if (n2 > 0) {
            f = (float)(f + d);
        } else if (n2 < 0) {
            f = (float)(f - d);
        }

        return f;
    }

    public static void setSpeed(double d) {
        handleDouble(d, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe, mc.thePlayer.movementInput.moveForward);
    }

    public static void handleFloat(float f, float f2) {
        flag3 = true;
        mc.thePlayer.motionX = Math.cos(Math.toRadians(f2 + 90.0F)) * f;
        mc.thePlayer.motionZ = Math.cos(Math.toRadians(f2)) * f;
    }

    public static void process3() {
        handleFlag(true);
    }

    public static void handleFloat3(float f) {
        float f2 = computeFloat();
        mc.thePlayer.motionX = mc.thePlayer.motionX + Math.cos(Math.toRadians(f2 + 90.0F)) * f;
        mc.thePlayer.motionZ = mc.thePlayer.motionZ + Math.cos(Math.toRadians(f2)) * f;
    }

    public static double computeDouble() {
        return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void handleDouble(double d, float f, double d2, double d3) {
        if (d3 != 0.0) {
            if (d2 > 0.0) {
                f += d3 > 0.0 ? -45 : 45;
            } else if (d2 < 0.0) {
                f += d3 > 0.0 ? 45 : -45;
            }

            d2 = 0.0;
            if (d3 > 0.0) {
                d3 = 1.0;
            } else if (d3 < 0.0) {
                d3 = -1.0;
            }
        }

        if (d2 > 0.0) {
            d2 = 1.0;
        } else if (d2 < 0.0) {
            d2 = -1.0;
        }

        double cos = Math.cos(Math.toRadians(f + 90.0F));
        double sin = Math.sin(Math.toRadians(f + 90.0F));
        mc.thePlayer.motionX = d3 * d * cos + d2 * d * sin;
        mc.thePlayer.motionZ = d3 * d * sin - d2 * d * cos;
    }

    public void stop() {
        mc.thePlayer.motionX = 0.0;
        mc.thePlayer.motionZ = 0.0;
    }

    public static void handleIsSafewalkEvent(IsSafewalkEvent isSafewalkEvent, double d, float f, double d2, double d3) {
        if (d3 != 0.0) {
            if (d2 > 0.0) {
                f += d3 > 0.0 ? -45 : 45;
            } else if (d2 < 0.0) {
                f += d3 > 0.0 ? 45 : -45;
            }

            d2 = 0.0;
            if (d3 > 0.0) {
                d3 = 1.0;
            } else if (d3 < 0.0) {
                d3 = -1.0;
            }
        }

        if (d2 > 0.0) {
            d2 = 1.0;
        } else if (d2 < 0.0) {
            d2 = -1.0;
        }

        double cos = Math.cos(Math.toRadians(f + 90.0F));
        double sin = Math.sin(Math.toRadians(f + 90.0F));
        isSafewalkEvent.setX(d3 * d * cos + d2 * d * sin);
        isSafewalkEvent.setZ(d3 * d * sin - d2 * d * cos);
    }

    public static boolean matchesDouble(double d) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -d, 0.0)).isEmpty();
    }

    public void handleDouble(double d, float f) {
        if (isMoving()) {
            f = (float)Math.toRadians(f);
            mc.thePlayer.motionX = -MathHelper.sinVanilla(f) * d;
            mc.thePlayer.motionZ = MathHelper.cosVanilla(f) * d;
        }
    }

    public double computeDouble(float f, double d, double d2) {
        if (d < 0.0) {
            f += 180.0F;
        }

        float f2 = 1.0F;
        if (d < 0.0) {
            f2 = -0.5F;
        } else if (d > 0.0) {
            f2 = 0.5F;
        }

        if (d2 > 0.0) {
            f -= 90.0F * f2;
        }

        if (d2 < 0.0) {
            f += 90.0F * f2;
        }

        return Math.toRadians(f);
    }

    public static double computeDouble5() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public void handleEntity(Entity entity) {
        handleDouble(computeDouble5(), entity);
    }

    public static void handleFlag(boolean flag) {
        if (!flag || Minecraft.getCurrentScreen() == null) {
            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
            mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);
            mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint);
            mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
            mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
            mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
            mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
        }
    }

    public double computeDouble(float f, float f2) {
        float movementYaw = mc.thePlayer.movementYaw;
        if (f < 0.0F) {
            movementYaw += 180.0F;
        }

        float f3 = 1.0F;
        if (f < 0.0F) {
            f3 = -0.5F;
        } else if (f > 0.0F) {
            f3 = 0.5F;
        }

        if (f2 > 0.0F) {
            movementYaw -= 70.0F * f3;
        }

        if (f2 < 0.0F) {
            movementYaw += 70.0F * f3;
        }

        return Math.toRadians(movementYaw);
    }

    public static void handleDouble(double d, Entity entity) {
        if (isMoving()) {
            double d2 = computeDouble4();
            entity.motionX = -MathHelper.sinVanilla((float)d2) * d;
            entity.motionZ = MathHelper.cosVanilla((float)d2) * d;
        }
    }

    public static void handleDouble(double d) {
        float yaw = mc.thePlayer.rotationYaw;
        double moveForward = mc.thePlayer.moveForward;
        double moveStrafing = mc.thePlayer.moveStrafing;
        if (moveForward == 0.0 && moveStrafing == 0.0) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        } else {
            if (moveForward != 0.0) {
                if (moveStrafing > 0.0) {
                    yaw += moveForward > 0.0 ? -45 : 45;
                } else if (moveStrafing < 0.0) {
                    yaw += moveForward > 0.0 ? 45 : -45;
                }

                moveStrafing = 0.0;
                if (moveForward > 0.0) {
                    moveForward = 1.0;
                } else if (moveForward < 0.0) {
                    moveForward = -1.0;
                }
            }

            mc.thePlayer.motionX = moveForward * d * Math.cos(Math.toRadians(yaw + 90.0F)) + moveStrafing * d * Math.sin(Math.toRadians(yaw + 90.0F));
            mc.thePlayer.motionZ = moveForward * d * Math.sin(Math.toRadians(yaw + 90.0F)) - moveStrafing * d * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }
}
