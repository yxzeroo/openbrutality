package org.brutality.system;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class JarIntegrityVerifier {
    public static final String text = "META-INF/BRUTALITY.DIGEST";
    private static final byte[] intValueArray = new byte[]{66, 114, 117, 116, 97, 108, 105, 116, 121, 45, 73, 110, 116, 49, 103, 114};
    private static final byte[] intValueArray2 = new byte[]{83, 51, 97, 108, 45, 50, 48, 50, 54, 45, 72, 77, 65, 67, 33, 0};
    private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    private static final AtomicBoolean atomicBoolean2 = new AtomicBoolean(false);
    private static final AtomicBoolean atomicBoolean3 = new AtomicBoolean(true);
    private static final AtomicReference<String> atomicReference = new AtomicReference<>("dev");

    private JarIntegrityVerifier() {
    }

    public static String computeString() {
        ensureChecked();
        return atomicReference.get();
    }

    public static boolean checkState() {
        ensureChecked();
        return !atomicBoolean2.get() || !atomicBoolean.get() || atomicBoolean3.get();
    }

    private static void ensureChecked() {
        if (!atomicBoolean.get() && atomicBoolean2.compareAndSet(false, true)) {
            CompletableFuture.runAsync(() -> {
                try {
                    JarIntegrityVerifier.Entry entry = computeEntry();
                    if (entry == null) {
                        atomicBoolean3.set(true);
                        atomicReference.set("dev");
                    } else {
                        atomicBoolean3.set(entry.flag);
                        atomicReference.set(entry.text);
                        if (!entry.flag) {
                        }
                    }
                } catch (Exception e) {
                    atomicBoolean3.set(true);
                    atomicReference.set("err");
                } finally {
                    atomicBoolean.set(true);
                }
            }, task -> {
                Thread thread = new Thread(task, "Brutality-JarIntegrity");
                thread.setDaemon(true);
                thread.setPriority(1);
                thread.start();
            });
        }
    }

    public static byte[] computeIntArray() throws Exception {
        MessageDigest instance = MessageDigest.getInstance("SHA-256");
        instance.update(intValueArray);
        instance.update(intValueArray2);
        return instance.digest();
    }

    public static String computeString(byte[] bytes, byte[] bytes2) throws Exception {
        Mac instance = Mac.getInstance("HmacSHA256");
        instance.init(new SecretKeySpec(bytes, "HmacSHA256"));
        byte[] bytes3 = instance.doFinal(bytes2);
        StringBuilder sb = new StringBuilder(bytes3.length * 2);

        for (byte b : bytes3) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    private static JarIntegrityVerifier.Entry computeEntry() throws Exception {
        URL location = JarIntegrityVerifier.class.getProtectionDomain().getCodeSource().getLocation();
        if (location == null) {
            return null;
        }
        String path = location.toURI().getPath();
        if (path == null) {
            return null;
        }
        if (path.matches("^/[A-Za-z]:/.*")) {
            path = path.substring(1);
        }

        File file = new File(path);
        if (file.isFile() && file.getName().endsWith(".jar")) {
            ArrayList list = new ArrayList();
            String str = null;

            JarIntegrityVerifier.Entry entry;
            try (JarFile jarFile = new JarFile(file)) {
                Enumeration enumeration = jarFile.entries();

                while (enumeration.hasMoreElements()) {
                    JarEntry jarEntry = (JarEntry)enumeration.nextElement();
                    if (!jarEntry.isDirectory()) {
                        String name = jarEntry.getName();
                        if ("META-INF/BRUTALITY.DIGEST".equals(name)) {
                            try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                                str = new String(readAll(inputStream), StandardCharsets.UTF_8).trim();
                            }
                        } else {
                            list.add(name);
                        }
                    }
                }

                Collections.sort(list);
                byte[] bytes = computeIntArray(jarFile, list);
                if (str == null || str.isEmpty()) {
                    String str2 = computeString(bytes).substring(0, 16);
                    return new JarIntegrityVerifier.Entry(true, str2);
                }

                String str3 = computeString(computeIntArray(), bytes);
                String str4 = str3.substring(0, 16);
                boolean equal = MessageDigest.isEqual(str3.getBytes(StandardCharsets.US_ASCII), str.getBytes(StandardCharsets.US_ASCII));
                entry = new JarIntegrityVerifier.Entry(equal, str4);
            }

            return entry;
        }
        return null;
    }

    private static byte[] readAll(InputStream in) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[8192];

        int n;
        while ((n = in.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, n);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] computeIntArray(JarFile jarFile, List<String> list) throws Exception {
        MessageDigest instance = MessageDigest.getInstance("SHA-256");
        byte[] bytes = new byte[8192];

        for (String str : list) {
            instance.update(str.getBytes(StandardCharsets.UTF_8));
            instance.update((byte)0);
            JarEntry jarEntry = jarFile.getJarEntry(str);
            if (jarEntry != null) {
                int n;
                try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                    while ((n = inputStream.read(bytes)) > 0) {
                        instance.update(bytes, 0, n);
                    }
                }
            }
        }

        return instance.digest();
    }

    public static byte[] computeIntArray(TreeMap<String, byte[]> treeMap) throws Exception {
        MessageDigest instance = MessageDigest.getInstance("SHA-256");

        for (Map.Entry entry : treeMap.entrySet()) {
            instance.update(((String)entry.getKey()).getBytes(StandardCharsets.UTF_8));
            instance.update((byte)0);
            instance.update((byte[])entry.getValue());
        }

        return instance.digest();
    }

    private static String computeString(byte[] bytes) throws Exception {
        MessageDigest instance = MessageDigest.getInstance("SHA-256");
        byte[] bytes2 = instance.digest(bytes);
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes2) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static final class Entry {
        public final boolean flag;
        public final String text;

        public Entry(boolean flag, String text) {
            this.flag = flag;
            this.text = text;
        }
    }
}
