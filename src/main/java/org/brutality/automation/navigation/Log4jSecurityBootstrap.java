package org.brutality.automation.navigation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Log4jSecurityBootstrap {
    public static boolean matchesString(String str) {
        if (str != null && str.matches("[0-9]+\\.[0-9]+\\.[0-9]+")) {
            String[] parts = str.split("\\.");
            int[] ints = new int[]{2, 25, 5};

            try {
                for (int i = 0; i < 3; i++) {
                    int n = Integer.parseInt(parts[i]);
                    if (n != ints[i]) {
                        return n > ints[i];
                    }
                }

                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private Log4jSecurityBootstrap() {
    }

    public static List<String> computeList(String str, String[] parts, List<Path> paths) {
        List list = new ArrayList();
        boolean flag = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows");
        list.add(Path.of(System.getProperty("java.home"), "bin", flag ? "java.exe" : "java").toString());

        for (String inputArgument : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (!inputArgument.startsWith("-Dbrutality.logging.repaired=")) {
                list.add(inputArgument);
            }
        }

        list.add("-Dbrutality.logging.repaired=true");
        list.add("-cp");
        String str2 = paths.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator));
        list.add(str2 + File.pathSeparator + System.getProperty("java.class.path"));
        list.add(str);
        Collections.addAll(list, parts);
        return list;
    }

    public static void process() throws Exception {
        ClassLoader classLoader = Log4jSecurityBootstrap.class.getClassLoader();

        for (String str : new String[]{"org.apache.logging.log4j.LogManager", "org.apache.logging.log4j.core.LoggerContext"}) {
            String implementationVersion = Class.forName(str, false, classLoader).getPackage().getImplementationVersion();
            if (!matchesString(implementationVersion)) {
                throw new SecurityException(
                    "Your Minecraft launch profile uses obsolete or unidentified Log4j. Reinstall the latest Brutality ZIP/profile or update the Brutality launcher."
                );
            }
        }
    }

    private static void handlePath(Path path) {
        try (var stream = Files.list(path)) {
            for (Path path2 : stream.toList()) {
                Files.deleteIfExists(path2);
            }

            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    public static void handleString(String str, String[] parts) throws Exception {
        try {
            process();
        } catch (Exception e) {
            if (Boolean.getBoolean("brutality.logging.repaired")) {
                throw e;
            }
            Path path = Files.createTempDirectory("brutality-logging-");

            try {
                List list = new ArrayList();

                for (String str2 : new String[]{"log4j-api", "log4j-core"}) {
                    Path path2 = path.resolve(str2 + ".jar");

                    try (InputStream resourceAsStream = Log4jSecurityBootstrap.class.getResourceAsStream("/logging-runtime/" + str2 + ".jar")) {
                        if (resourceAsStream == null) {
                            throw new SecurityException("Bundled logging runtime missing");
                        }

                        Files.copy(resourceAsStream, path2);
                    }

                    list.add(path2);
                }

                Process process2 = new ProcessBuilder(computeList(str, parts, list)).inheritIO().start();
                int n = process2.waitFor();
                handlePath(path);
                System.exit(n);
            } finally {
                handlePath(path);
            }
        }
    }
}
