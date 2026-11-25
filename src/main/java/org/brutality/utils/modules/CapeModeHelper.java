package org.brutality.utils.modules;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.util.ResourceLocation;
import org.brutality.module.impl.client.Cape;

public final class CapeModeHelper {
    public static final CapeModeHelper instance = new CapeModeHelper();
    private volatile CapeModeHelper.Entry entry = new CapeModeHelper.Entry(Collections.emptyMap(), Collections.emptyMap());

    public ResourceLocation computeResourceLocation(UUID uuid, String str) {
        CapeModeHelper.Entry entry = this.entry;
        ResourceLocation location = uuid == null ? null : entry.lookupMap.get(uuid);
        return location != null ? location : (str == null ? null : entry.lookupMap2.get(str.toLowerCase(Locale.ROOT)));
    }

    private static String computeString(JsonObject json, String str) {
        JsonElement element = json.get(str);
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString() ? element.getAsString() : null;
    }

    public void handleJsonArray(JsonArray jsonArray) {
        HashMap map = new HashMap();
        HashMap map2 = new HashMap();

        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) {
                JsonObject asJsonObject = element.getAsJsonObject();
                ResourceLocation location = Cape.computeResourceLocation(computeString(asJsonObject, "cape_mode"));
                String str = computeString(asJsonObject, "ingame_username");
                if (location != null && str != null && str.matches("[A-Za-z0-9_]{1,16}")) {
                    String str2 = computeString(asJsonObject, "minecraft_uuid");
                    if (str2 == null || str2.isEmpty()) {
                        map2.put(str.toLowerCase(Locale.ROOT), location);
                    } else if (str2.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")) {
                        try {
                            map.put(UUID.fromString(str2), location);
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            }
        }

        this.entry = new CapeModeHelper.Entry(Collections.unmodifiableMap(map), Collections.unmodifiableMap(map2));
    }

    public void clear() {
        this.entry = new CapeModeHelper.Entry(Collections.emptyMap(), Collections.emptyMap());
    }

    public static final class Entry {
        public final Map<UUID, ResourceLocation> lookupMap;
        public final Map<String, ResourceLocation> lookupMap2;

        public Entry(Map<UUID, ResourceLocation> lookupMap, Map<String, ResourceLocation> lookupMap2) {
            this.lookupMap = lookupMap;
            this.lookupMap2 = lookupMap2;
        }
    }
}
