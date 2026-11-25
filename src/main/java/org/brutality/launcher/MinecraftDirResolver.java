package org.brutality.launcher;

import java.nio.file.Path;
import java.util.Locale;

final class MinecraftDirResolver {
    private MinecraftDirResolver() {
    }

    static Path computePath(String str, String str2, String str3) {
        String str4 = str.toLowerCase(Locale.ROOT);
        if (str4.contains("mac") || str4.contains("darwin")) {
            return Path.of(str2, "Library", "Application Support", "minecraft");
        }
        return !str4.contains("win") ? Path.of(str2, ".minecraft") : Path.of(str3 != null && !str3.isBlank() ? str3 : str2, ".minecraft");
    }
}
