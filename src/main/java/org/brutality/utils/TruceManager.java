package org.brutality.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;

public final class TruceManager {
    private static final File file = new File(Minecraft.getMinecraft().mcDataDir, "Brutality/Players/truce.txt");
    private static final Set<String> itemSet = new HashSet<>();

    private static void save() {
        try {
            file.getParentFile().mkdirs();

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String str : itemSet) {
                    writer.println(str);
                }
            }
        } catch (IOException ignored) {
        }
    }

    static {
        load();
    }

    public static boolean matchesString2(String str) {
        return matchesString(str, !matchesString(str));
    }

    public static boolean matchesString3(String str) {
        return matchesString(str, true);
    }

    private TruceManager() {
    }

    public static boolean matchesString(String str) {
        return str != null && itemSet.contains(str.trim().toLowerCase());
    }

    public static Set<String> computeSet() {
        return Collections.unmodifiableSet(itemSet);
    }

    private static void load() {
        if (file.exists()) {
            String str;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while ((str = reader.readLine()) != null) {
                    if (!str.trim().isEmpty()) {
                        itemSet.add(str.trim().toLowerCase());
                    }
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static boolean matchesString4(String str) {
        return matchesString(str, false);
    }

    private static boolean matchesString(String str, boolean flag) {
        if (str != null && !str.trim().isEmpty()) {
            String str2 = str.trim().toLowerCase();
            boolean flag2 = flag ? itemSet.add(str2) : itemSet.remove(str2);
            if (flag2) {
                save();
            }

            return flag2;
        }
        return false;
    }
}
