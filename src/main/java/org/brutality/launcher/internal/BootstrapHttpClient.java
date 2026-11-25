package org.brutality.launcher.internal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Locale;

final class BootstrapHttpClient {
    private BootstrapHttpClient() {
    }

    static String computeString(String[] parts) throws IOException {
        return ManifestJsonParser.computeString(b(parts), "key");
    }

    private static void handleLinkedHashSet(LinkedHashSet<Path> linkedHashSet, Path path) {
        linkedHashSet.add(path.resolve("license.dat"));
        linkedHashSet.add(path.resolve("Brutality").resolve("license.dat"));
        linkedHashSet.add(path.resolve("mods").resolve("license.dat"));
    }

    static String b(String[] parts) throws IOException {
        Path path = null;

        try {
            path = Path.of(BootstrapHttpClient.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (Exception ignored) {
        }

        LinkedHashSet<Path> linkedHashSet = new LinkedHashSet<>();
        if (path != null) {
            linkedHashSet.add(path.resolve("license.dat"));
        }

        for (int i = 0; i < parts.length; i++) {
            if ("--gameDir".equals(parts[i]) && i + 1 < parts.length) {
                handleLinkedHashSet(linkedHashSet, Path.of(parts[++i]));
            } else if (parts[i].startsWith("--gameDir=")) {
                handleLinkedHashSet(linkedHashSet, Path.of(parts[i].substring(10)));
            }
        }

        handleLinkedHashSet(linkedHashSet, Path.of("."));
        linkedHashSet.add(Path.of("..", "license.dat"));
        String str = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String property = System.getProperty("user.home", ".");
        Path path2 = str.contains("win") && !str.contains("darwin")
            ? Path.of(System.getenv().getOrDefault("APPDATA", property), ".minecraft")
            : (!str.contains("mac") && !str.contains("darwin") ? Path.of(property, ".minecraft") : Path.of(property, "Library", "Application Support", "minecraft"));
        handleLinkedHashSet(linkedHashSet, path2);

        for (Path path3 : linkedHashSet) {
            if (Files.isRegularFile(path3)) {
                String str2 = Files.readString(path3, StandardCharsets.UTF_8);
                String str3 = ManifestJsonParser.computeString(str2, "key");
                if (str3 != null && !str3.isBlank()) {
                    return str2;
                }
            }
        }

        throw new PayloadBootstrapException(
            "No readable license.dat was found. Download the client again and keep license.dat beside its JAR or in your game directory."
        );
    }
}
