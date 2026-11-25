package org.brutality.utils.player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
// VENDOR-DISABLED: import org.brutality.system.BackendAuthService;

public class MovementUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final double amount = 0.2873;
    private static final double amount2 = 0.37349;
    private static final double amount3 = 0.08619;
    private static final float factor = 0.42F;
    private static final long[] longValueArray = new long[8];
    private static final int[] intValueArray = new int[8];
    private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    public static double getDistance(double d, double d2, double d3) {
        if (mc.thePlayer == null) {
            return 0.0;
        }
        double d4 = mc.thePlayer.posX - d;
        double d5 = mc.thePlayer.posY - d2;
        double d6 = mc.thePlayer.posZ - d3;
        return Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
    }

    private static void process2() {
        long now = System.currentTimeMillis();

        for (int i = 0; i < 8; i++) {
            intValueArray[i] = (int)((now ^ i * 2654435769L) & -1L);
        }
    }

    public static float computeFloat() {
        if (mc.thePlayer == null) {
            return 0.0F;
        }
        float yaw = mc.thePlayer.rotationYaw;
        float moveForward = mc.thePlayer.movementInput.moveForward;
        float moveStrafe = mc.thePlayer.movementInput.moveStrafe;
        float f = 1.0F;
        if (f == 0.0F && moveStrafe == 0.0F) {
            return yaw;
        }
        float f2 = yaw;
        if (f < 0.0F) {
            f2 = yaw + 180.0F;
        }

        float f3 = MathHelper.wrapAngleTo180_float(f2 - yaw) * 0.5F;
        return yaw - MathHelper.wrapAngleTo180_float(f3);
    }

    private static void process() {
        atomicBoolean.set(false);
    }

    private static void process9() {
        int n = atomicInteger.incrementAndGet();
        if ((127 | ~n) - ~n == 0) {
            try {
                boolean flag = checkState();
                atomicBoolean.set(flag);
                if (!flag) {
                    process8();
                }
            } catch (Exception e) {
                atomicBoolean.set(false);
            }
        }
    }

    public static void handleDouble(double d) {
        if (mc.thePlayer != null) {
            float moveForward = mc.thePlayer.movementInput.moveForward;
            float moveStrafe = mc.thePlayer.movementInput.moveStrafe;
            float yaw = mc.thePlayer.rotationYaw;
            if (moveForward != 0.0F || moveStrafe != 0.0F) {
                float f = yaw;
                if (moveForward < 0.0F) {
                    f = yaw + 180.0F;
                }

                float f2 = MathHelper.wrapAngleTo180_float(f - yaw) * 0.5F;
                float f3 = MathHelper.wrapAngleTo180_float(f + 90.0F - yaw) * 0.5F;
                mc.thePlayer.motionX += (moveForward * f2 + moveStrafe * f3) * d;
                mc.thePlayer.motionZ += (moveForward * f3 - moveStrafe * f2) * d;
            }
        }
    }

    private static boolean checkState7() {
        // VENDOR-DISABLED: BackendAuthService currentToken/currentUser reflection check
        return true;
    }

    private static void process8() {
        // VENDOR-DISABLED: BackendAuthService.getInstance().checkState4()
        atomicBoolean.set(true);
    }

    private static boolean checkState6() {
        // VENDOR-DISABLED: BackendAuthService.getInstance().checkState4()
        return true;
    }

    private static boolean checkState5() {
        try {
            boolean flag = checkState6();
            boolean flag2 = checkState2();
            boolean flag3 = checkState4();
            boolean flag4 = flag && flag2 && flag3;
            if (flag4) {
                process2();
            }

            return flag4;
        } catch (Exception e) {
            return false;
        }
    }

    private static void process7() {
        int n = atomicInteger.incrementAndGet();
        if ((n & 31) == 0) {
            try {
                boolean flag = checkState7();
                atomicBoolean.set(flag);
                if (!flag) {
                    process5();
                }
            } catch (Exception e) {
                process5();
            }
        }
    }

    private static boolean checkState4() {
        try {
            int n = intValueArray[0];
            int n2 = (int)(System.currentTimeMillis() % 2147483647L);
            return (65535 | ~(n ^ n2)) - ~(n ^ n2) == 23130;
        } catch (Exception e) {
            return false;
        }
    }

    private static int computeInt(int n) {
        n = n << 13 | n >>> 19;
        n *= -1640531527;
        return Integer.reverse(n);
    }

    private static double computeDouble(double d) {
        d = Math.sin(d) * Math.cos(d);
        d = Math.sqrt(Math.abs(d));
        return Math.log(d + 1.0);
    }

    public static double getSpeed() {
        if (mc.thePlayer == null) {
            return 0.0;
        }
        double walkSpeed = mc.thePlayer.capabilities.getWalkSpeed();
        if (mc.thePlayer.isSprinting()) {
            return walkSpeed * 1.3;
        }
        return mc.thePlayer.isSneaking() ? walkSpeed * 0.3 : walkSpeed;
    }

    private static String computeString(String str) {
        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char)(chars[i] & ~90 | 90 & ~chars[i]);
        }

        return new String(chars);
    }

    private static void process6() {
        int n = atomicInteger.incrementAndGet();
        if ((63 | ~n) - ~n == 0) {
            try {
                boolean flag = checkState5();
                atomicBoolean.set(flag);
                if (!flag) {
                    process();
                }
            } catch (Exception e) {
                process();
            }
        }
    }

    private static boolean checkState3() {
        try {
            return atomicBoolean.get();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkState2() {
        // VENDOR-DISABLED: BackendAuthService currentToken reflection check
        return true;
    }

    static {
        for (int i = 0; i < 8; i++) {
            longValueArray[i] = System.currentTimeMillis() ^ i * 2654435769L;
            intValueArray[i] = i * -1640531527;
        }
    }

    private static void process5() {
        atomicBoolean.set(false);
        atomicInteger.set(0);
        Arrays.fill(longValueArray, 0L);
        Arrays.fill(intValueArray, 0);
    }

    private static void process4() {
        long now = System.currentTimeMillis();

        for (int i = 0; i < 8; i++) {
            longValueArray[i] = now ^ i * 2654435769L;
        }
    }

    private static void process3() {
        int n = atomicInteger.incrementAndGet();
        if ((n & 0xFF) == 0) {
            try {
                boolean flag = checkState3();
                atomicBoolean.set(flag);
                if (!flag) {
                    process4();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static boolean checkState() {
        // VENDOR-DISABLED: BackendAuthService periodic session check
        return true;
    }

    public static boolean isMoving() {
        return mc.thePlayer == null
            ? false
            : mc.thePlayer.movementInput.moveForward != 0.0F || mc.thePlayer.movementInput.moveStrafe != 0.0F || mc.thePlayer.movementInput.jump;
    }
}
