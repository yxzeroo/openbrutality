package org.brutality.build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class OptiFineJarMerger {
    public static void main(String[] parts) throws IOException {
        if (parts.length < 3) {
            System.exit(1);
        }

        File file = new File(parts[0]);
        File file2 = new File(parts[1]);
        File file3 = new File(parts[2]);
        if (!file.exists()) {
            System.exit(1);
        }

        if (!file2.exists()) {
            System.exit(1);
        }

        try (
            JarFile jarFile = new JarFile(file);
            JarFile jarFile2 = new JarFile(file2);
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file3));
        ) {
            HashSet set = new HashSet();
            Enumeration enumeration = jarFile.entries();

            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry)enumeration.nextElement();
                String name = jarEntry.getName();
                if (set.add(name)) {
                    jarOutputStream.putNextEntry(new JarEntry(name));

                    try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                        handleInputStream(inputStream, jarOutputStream);
                    }

                    jarOutputStream.closeEntry();
                }
            }

            Enumeration enumeration2 = jarFile2.entries();
            int n = 0;

            while (enumeration2.hasMoreElements()) {
                JarEntry jarEntry2 = (JarEntry)enumeration2.nextElement();
                String name2 = jarEntry2.getName();
                if ((name2.startsWith("optifine/") || name2.startsWith("net/optifine/") || name2.startsWith("META-INF/")) && set.add(name2)) {
                    jarOutputStream.putNextEntry(new JarEntry(jarEntry2.getName()));

                    try (InputStream inputStream2 = jarFile2.getInputStream(jarEntry2)) {
                        handleInputStream(inputStream2, jarOutputStream);
                    }

                    jarOutputStream.closeEntry();
                    n++;
                }
            }
        }
    }

    private static void handleInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] bytes = new byte[8192];

        int n;
        while ((n = in.read(bytes)) > 0) {
            out.write(bytes, 0, n);
        }
    }
}
