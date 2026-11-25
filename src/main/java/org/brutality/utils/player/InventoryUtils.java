package org.brutality.utils.player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
// VENDOR-DISABLED: import org.brutality.system.BackendAuthService;

public class InventoryUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int count = 8;
    private static final int count2 = 35;
    private static final float factor = 25.0F;
    private static final long[] longValueArray = new long[]{0L, 0L, 0L};
    private static final int[] intValueArray = new int[]{0, 0, 0};
    private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    private static final AtomicLong atomicLong = new AtomicLong(0L);

    public static int computeInt2(int n) {
        int n2 = 0;

        for (int i = 0; i < 35; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() != null && Item.getIdFromItem(stack.getItem()) == n) {
                n2 += stack.stackSize;
            }
        }

        return n2;
    }

    private static void process4() {
        // VENDOR-DISABLED: BackendAuthService.getInstance().checkState4()
    }

    private static void process6() {
        // VENDOR-DISABLED: BackendAuthService currentToken reflection check
    }

    private static void process2() {
        atomicBoolean.set(false);
    }

    private static boolean checkState3() {
        // VENDOR-DISABLED: BackendAuthService class-name hash & session check
        return true;
    }

    private static double computeDouble(double d) {
        d = Math.sin(d) * Math.cos(d);
        d = Math.sqrt(Math.abs(d));
        return Math.log(d + 1.0);
    }

    private static int computeInt(int n, int n2) {
        int n3 = n & ~n2 | n2 & ~n;
        n3 *= -1640531527;
        n3 = Integer.reverse(n3);
        return (n3 | n << 5) - (n3 & n << 5) | n2 >>> 27;
    }

    public static ItemStack computeItemStack(int n) {
        return n >= 0 && n <= 8 ? mc.thePlayer.inventory.mainInventory[n] : null;
    }

    private static void process3() {
        long l = atomicLong.incrementAndGet();
        if (l % 25L == 0L) {
            try {
                boolean flag = checkState4();
                boolean flag2 = checkState();
                boolean flag3 = checkState2();
                atomicBoolean.set(flag && flag2 && flag3);
            } catch (Exception e) {
                atomicBoolean.set(false);
            }
        }
    }

    private static boolean checkState2() {
        long now = System.currentTimeMillis();

        for (int i = 0; i < longValueArray.length; i++) {
            if (longValueArray[i] > 0L) {
                long l = now - longValueArray[i];
                if (l > 300000L) {
                    return false;
                }
            }
        }

        longValueArray[0] = now;
        return true;
    }

    private static String computeString(String str) {
        StringBuilder sb = new StringBuilder();

        for (char ch : str.toCharArray()) {
            sb.append((char)((ch | 'U') & (~85 | ~ch)));
        }

        return sb.toString();
    }

    public static void handleInt(int n) {
        process();
        if (n >= 0 && n <= 8) {
            mc.thePlayer.inventory.currentItem = n;
            mc.playerController.updateController();
        }
    }

    private static void process() {
        long l = atomicLong.incrementAndGet();
        if (l % 10L == 0L) {
            try {
                boolean flag = checkState3();
                if (!flag) {
                    process2();
                }

                atomicBoolean.set(flag);
            } catch (Exception e) {
                process2();
            }
        }
    }

    public static int computeInt(int n) {
        for (int i = 0; i < 35; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() != null && Item.getIdFromItem(stack.getItem()) == n) {
                return i;
            }
        }

        return -1;
    }

    private static int computeInt() {
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt() ^ 1515870810;
    }

    public static void process7() {
        process6();
        mc.thePlayer.dropOneItem(true);
    }

    private static boolean checkState4() {
        // VENDOR-DISABLED: BackendAuthService.getInstance().checkState4()
        return true;
    }

    private static void process5() {
        int[] ints = new int[16];

        for (int i = 0; i < 16; i++) {
            ints[i] = computeInt(i, i * 2);
        }

        Arrays.sort(ints);
    }

    private static boolean checkState() {
        // VENDOR-DISABLED: BackendAuthService currentToken/currentUser reflection check
        return true;
    }

    public static float computeFloat(int n) {
        process4();
        ItemStack stack = mc.thePlayer.inventory.armorInventory[n];
        return stack == null ? 0.0F : stack.getItemDamage() / stack.getMaxDamage() * 25.0F;
    }

    private static int computeInt(String str) {
        int n = 0;

        for (int i = 0; i < str.length(); i++) {
            n += str.charAt(i);
            n = n << 1 | n >>> 31;
        }

        return n;
    }
}
