package org.brutality.system;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;

public final class RuntimeAuthGuard {
    private static final boolean flag = Boolean.getBoolean("brut.dev");
    private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);
    private static final AtomicLong atomicLong = new AtomicLong(0L);
    private static final AtomicReference<byte[]> atomicReference = new AtomicReference<>(null);
    private static volatile byte[] intValueArray = null;
    private static volatile byte[] intValueArray2 = null;
    private static volatile byte[] intValueArray3 = null;
    private static volatile byte[] intValueArray4 = null;
    private static final int count = 10;
    private static final int count2 = 5;
    private static final long gl = 600000L;

    public static void handleString(String username, String sessionToken, String hwid) {
        if (username != null && sessionToken != null && hwid != null) {
            try {
                byte[] bytes = computeIntArray(username, sessionToken, hwid);
                if (bytes == null) {
                    process2();
                    return;
                }

                intValueArray4 = computeIntArray(Math.max(Math.max(username.length(), sessionToken.length()), hwid.length()));
                intValueArray2 = computeIntArray(username.getBytes(StandardCharsets.UTF_8), intValueArray4);
                intValueArray = computeIntArray(sessionToken.getBytes(StandardCharsets.UTF_8), intValueArray4);
                intValueArray3 = computeIntArray(hwid.getBytes(StandardCharsets.UTF_8), intValueArray4);
                atomicReference.set(bytes);
                atomicLong.set(System.currentTimeMillis());
                atomicBoolean.set(true);
                atomicInteger.set(0);
            } catch (Exception e) {
                process2();
            }
        } else {
            process2();
        }
    }

    public static boolean checkState2() {
        if (flag) {
            return true;
        }
        if (!atomicBoolean.get()) {
            return false;
        }
        if (atomicInteger.get() >= 10) {
            return false;
        }
        if (atomicReference.get() == null) {
            return false;
        }
        try {
            return JarIntegrityVerifier.checkState();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkState5() {
        return flag || atomicBoolean.get();
    }

    public static boolean checkState4() {
        if (flag) {
            return true;
        }
        if (!atomicBoolean.get()) {
            return false;
        }
        try {
            byte[] bytes = computeIntArray(intValueArray2, intValueArray4);
            byte[] bytes2 = computeIntArray(intValueArray, intValueArray4);
            byte[] bytes3 = computeIntArray(intValueArray3, intValueArray4);
            String str = new String(bytes, StandardCharsets.UTF_8);
            String str2 = new String(bytes2, StandardCharsets.UTF_8);
            String str3 = new String(bytes3, StandardCharsets.UTF_8);
            byte[] bytes4 = computeIntArray(str, str2, str3);
            byte[] bytes5 = atomicReference.get();
            if (bytes4 != null && bytes5 != null) {
                boolean equal = MessageDigest.isEqual(bytes4, bytes5);
                if (equal) {
                    atomicLong.set(System.currentTimeMillis());
                }

                return equal;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkState3() {
        if (flag) {
            return true;
        }
        if (!atomicBoolean.get()) {
            return false;
        }
        long l = System.currentTimeMillis() - atomicLong.get();
        if (l >= 600000L) {
            return false;
        }
        try {
            if (!JarIntegrityVerifier.checkState()) {
                return false;
            }
        } catch (Exception ignored) {
        }

        return true;
    }

    public static void process2() {
        if (!flag) {
            int n = atomicInteger.incrementAndGet();
            if (n >= 10) {
                LogManager.getLogger("BrutalityStartup")
                    .error("[Brutality] Startup/runtime authentication checks failed repeatedly; closing client. Restart and sign in again.");
                process();
                System.exit(0);
            }
        }
    }

    public static boolean checkState() {
        return !flag && atomicInteger.get() >= 5;
    }

    public static int computeInt() {
        return flag ? 0 : atomicInteger.get();
    }

    private static byte[] computeIntArray(String str, String str2, String str3) throws Exception {
        MessageDigest instance = MessageDigest.getInstance("SHA-256");
        byte[] bytes = instance.digest(str3.getBytes(StandardCharsets.UTF_8));
        Mac instance2 = Mac.getInstance("HmacSHA256");
        instance2.init(new SecretKeySpec(bytes, "HmacSHA256"));
        String str4 = str + ":" + str2;
        return instance2.doFinal(str4.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] computeIntArray(byte[] bytes, byte[] bytes2) {
        if (bytes != null && bytes2 != null) {
            byte[] bytes3 = new byte[bytes.length];

            for (int i = 0; i < bytes.length; i++) {
                bytes3[i] = (byte)(bytes[i] ^ bytes2[i % bytes2.length]);
            }

            return bytes3;
        }
        return new byte[0];
    }

    private static byte[] computeIntArray(int n) {
        byte[] bytes = new byte[n];
        long l = System.nanoTime() ^ System.currentTimeMillis();

        for (int i = 0; i < n; i++) {
            l = l * 6364136223846793005L + 1442695040888963407L;
            bytes[i] = (byte)(l >>> 40);
        }

        return bytes;
    }

    private static void process() {
        try {
            byte[] bytes = atomicReference.get();
            if (bytes != null) {
                Arrays.fill(bytes, (byte)0);
            }

            if (intValueArray != null) {
                Arrays.fill(intValueArray, (byte)0);
            }

            if (intValueArray2 != null) {
                Arrays.fill(intValueArray2, (byte)0);
            }

            if (intValueArray3 != null) {
                Arrays.fill(intValueArray3, (byte)0);
            }

            if (intValueArray4 != null) {
                Arrays.fill(intValueArray4, (byte)0);
            }

            atomicReference.set(null);
            atomicBoolean.set(false);
        } catch (Exception ignored) {
        }
    }
}
