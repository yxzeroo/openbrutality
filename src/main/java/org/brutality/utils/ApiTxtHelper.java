package org.brutality.utils;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.brutality.utils.hypixel.GetPotentialItemOwnersFromNonceHelper;

public class ApiTxtHelper {
    private static final File file = new File("Brutality/api");
    private static final File file2 = new File(file, "api.txt");
    private static String text = null;

    public static String computeString() {
        if (text != null) {
            return text;
        }
        if (!file2.exists()) {
            return null;
        }
        try {
            String str = new String(Files.readAllBytes(Paths.get(file2.getPath()))).trim();
            if (!str.isEmpty()) {
                text = str;
                return text;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean checkState() {
        try {
            text = null;
            return file2.exists() ? file2.delete() : true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static JsonObject computeJsonObject(String str) {
        String str2 = computeString(str);
        return str2 == null ? null : GetPotentialItemOwnersFromNonceHelper.computeJsonObject(str2);
    }

    public static String computeString(String str) {
        String str2 = computeString();
        if (str2 != null && !str2.isEmpty()) {
            String str3 = "https://api.hypixel.net/";
            if (str.startsWith("/")) {
                str = str.substring(1);
            }

            String str4 = str.contains("?") ? "&" : "?";
            return str3 + str + str4 + "key=" + str2;
        }
        return null;
    }

    static {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean matchesString(String str) {
        if (str != null && !str.trim().isEmpty()) {
            try {
                try (PrintWriter writer = new PrintWriter(new FileWriter(file2))) {
                    writer.println(str.trim());
                }

                text = str.trim();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean checkState3() {
        String str = computeString();
        if (str != null && !str.isEmpty()) {
            try {
                String str2 = "https://api.hypixel.net/key?key=" + str;
                JsonObject json = GetPotentialItemOwnersFromNonceHelper.computeJsonObject(str2);
                if (json != null && json.has("success")) {
                    return json.get("success").getAsBoolean();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
        return false;
    }

    public static boolean checkState2() {
        String str = computeString();
        return str != null && !str.isEmpty();
    }
}
