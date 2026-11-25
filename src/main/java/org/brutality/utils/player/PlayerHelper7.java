package org.brutality.utils.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class PlayerHelper7 {
    private static final Map<String, String> lookupMap = new HashMap<>();
    private static final File file = new File(Minecraft.getMinecraft().mcDataDir, "brutality/mutes.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static boolean matchesString(String str) {
        if (str == null) {
            return false;
        }
        String str2 = NormalizeHelper.computeString(str);
        return str2 != null && matchesString2(str2);
    }

    public static boolean matchesString2(String str) {
        return lookupMap.containsKey(str.toLowerCase());
    }

    public static void handleString(String str) {
        lookupMap.remove(str.toLowerCase());
        process();
    }

    public static void process2() {
        if (!file.exists()) {
            process();
        } else {
            try (FileReader fileReader = new FileReader(file)) {
                Type type = (new TypeToken<HashMap<String, String>>() {}).getType();
                Map map = gson.fromJson(fileReader, type);
                if (map != null) {
                    lookupMap.clear();
                    lookupMap.putAll(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, String> getLookupMap() {
        return lookupMap;
    }

    public static void handleString2(String str) {
        lookupMap.put(str.toLowerCase(), str);
        process();
    }

    public static void process() {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (FileWriter fileWriter = new FileWriter(file)) {
                gson.toJson(lookupMap, fileWriter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
