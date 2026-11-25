package org.brutality.management;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NativesExtractor {
    private static final String text = "natives/";
    private static boolean loaded = false;

    private static boolean matchesFile(File file, String str, String str2) {
        try {
            String path = NativesExtractor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (path.startsWith("file:")) {
                path = path.substring(5);
            }

            String str3 = URLDecoder.decode(path, "UTF-8");
            if (str3 != null && str3.endsWith(".jar")) {
                File file2 = new File(str3);
                if (!file2.exists()) {
                    return false;
                }
                int n = 0;

                try (ZipFile zipFile = new ZipFile(file2)) {
                    Enumeration enumeration = zipFile.entries();

                    while (enumeration.hasMoreElements()) {
                        ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
                        String name = zipEntry.getName();
                        if (name.startsWith("natives/") && !zipEntry.isDirectory()) {
                            String name2 = new File(name).getName();
                            boolean flag = false;
                            if (str.equals("windows")) {
                                flag = name2.toLowerCase().endsWith(".dll");
                            } else if (str.equals("macos")) {
                                flag = name2.toLowerCase().endsWith(".dylib") || name2.toLowerCase().endsWith(".jnilib");
                            } else if (str.equals("linux")) {
                                flag = name2.toLowerCase().endsWith(".so");
                            }

                            if (flag) {
                                File file3 = new File(file, name2);

                                try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
                                    Files.copy(inputStream, file3.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    n++;
                                }
                            }
                        }
                    }
                }

                return n > 0;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized void extractNatives() {
        if (!loaded) {
            try {
                String str = System.getProperty("os.name").toLowerCase();
                String str2 = System.getProperty("os.arch").toLowerCase();
                boolean flag = str2.contains("aarch64") || str2.contains("arm64");
                String str3;
                String str4;
                if (str.contains("win")) {
                    str3 = "windows";
                    str4 = ".dll";
                } else if (str.contains("mac")) {
                    str3 = "macos";
                    str4 = ".dylib";
                    if (flag) {
                    }
                } else if (!str.contains("linux") && !str.contains("nix") && !str.contains("nux")) {
                    str3 = "linux";
                    str4 = ".so";
                } else {
                    str3 = "linux";
                    str4 = ".so";
                }

                String str5 = str3.equals("windows")
                    ? (str2.contains("64") ? "lwjgl64.dll" : "lwjgl.dll")
                    : (str3.equals("macos") ? "liblwjgl.dylib" : (str2.contains("64") ? "liblwjgl64.so" : "liblwjgl.so"));
                File file = computeFile(System.getProperty("org.lwjgl.librarypath"), str5);
                if (file == null) {
                    file = computeFile(System.getProperty("java.library.path"), str5);
                }

                if (file != null) {
                    System.setProperty("org.lwjgl.librarypath", file.getAbsolutePath());
                    loaded = true;
                    return;
                }

                File file2 = extractNatives(str3, str4);
                if (file2 != null && file2.exists()) {
                    System.setProperty("org.lwjgl.librarypath", file2.getAbsolutePath());
                    System.setProperty("net.java.games.input.librarypath", file2.getAbsolutePath());
                } else {
                    File file3 = computeFile(str3);
                    if (file3 != null && file3.exists()) {
                        System.setProperty("org.lwjgl.librarypath", file3.getAbsolutePath());
                        System.setProperty("net.java.games.input.librarypath", file3.getAbsolutePath());
                    }
                }

                loaded = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static File extractNatives(String str, String str2) throws IOException {
        File file = Files.createTempDirectory("brutality_natives_" + str + "_").toFile();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> handleFile(file)));
        boolean flag = matchesFile(file, str, str2);
        if (!flag) {
            handleFile(file);
            return null;
        }
        return file;
    }

    public static File computeFile(String str, String str2) {
        if (str != null && !str.isEmpty()) {
            for (String str3 : str.split(Pattern.quote(File.pathSeparator))) {
                if (!str3.isEmpty()) {
                    File file = new File(str3);
                    if (new File(file, str2).isFile()) {
                        return file;
                    }
                }
            }

            return null;
        }
        return null;
    }

    private static void handleFile(File file) {
        if (file != null && file.exists()) {
            try {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File file2 : files) {
                        if (file2.isDirectory()) {
                            handleFile(file2);
                        } else {
                            file2.delete();
                        }
                    }
                }

                file.delete();
            } catch (Exception ignored) {
            }
        }
    }

    private static File computeFile() {
        try {
            String path = NativesExtractor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (path.startsWith("file:")) {
                path = path.substring(5);
            }

            path = URLDecoder.decode(path, "UTF-8");

            for (File i = new File(path); i != null; i = i.getParentFile()) {
                if (new File(i, "settings.gradle").exists() || new File(i, "build.gradle").exists()) {
                    return i;
                }
            }
        } catch (Exception ignored) {
        }

        return new File(System.getProperty("user.dir"));
    }

    private static File computeFile(String str) {
        try {
            File file = computeFile();
            String str2 = str.contains("mac") ? "macos" : (str.contains("win") ? "windows" : "linux");
            File file2 = new File(file, "test_natives/" + str2);
            if (file2.exists()) {
                return file2;
            }
        } catch (Exception ignored) {
        }

        return null;
    }
}
