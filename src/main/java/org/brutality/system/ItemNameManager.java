package org.brutality.system;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.brutality.core.itemstats.GetOrCreateHandler;

public final class ItemNameManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File file = new File("Brutality");
    private static final File file2 = new File(file, "item_names.json");
    private static final Map<String, String> lookupMap = new ConcurrentHashMap<>();
    private static volatile boolean loaded = false;

    private ItemNameManager() {
    }

    private static synchronized void process() {
        if (!loaded) {
            loaded = true;

            try {
                if (file2.exists()) {
                    try (FileReader fileReader = new FileReader(file2)) {
                        JsonElement element = new JsonParser().parse(fileReader);
                        if (element != null && element.isJsonObject()) {
                            JsonObject asJsonObject = element.getAsJsonObject();

                            for (Entry entry : asJsonObject.entrySet()) {
                                try {
                                    if (entry.getValue() != null && ((JsonElement)entry.getValue()).isJsonPrimitive()) {
                                        lookupMap.put((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }

                            return;
                        }
                    }
                }
            } catch (Exception ignored2) {
            }
        }
    }

    private static synchronized void save() {
        try {
            if (!file.exists()) {
                file.mkdirs();
            }

            JsonObject json = new JsonObject();

            for (Entry entry : lookupMap.entrySet()) {
                json.addProperty((String)entry.getKey(), (String)entry.getValue());
            }

            try (FileWriter fileWriter = new FileWriter(file2)) {
                gson.toJson((JsonElement)json, fileWriter);
            }
        } catch (Exception ignored) {
        }
    }

    public static void handleItemStack(ItemStack stack, String coloredName) {
        process();
        String str = computeString2(stack);
        if (str != null) {
            if (coloredName != null && !coloredName.isEmpty()) {
                lookupMap.put(str, coloredName);
            } else {
                lookupMap.remove(str);
            }

            save();
        }
    }

    public static String computeString(ItemStack stack) {
        process();
        String str = computeString2(stack);
        return str == null ? null : lookupMap.get(str);
    }

    public static boolean checkState() {
        process();
        return !lookupMap.isEmpty();
    }

    public static void handleItemStack2(ItemStack stack) {
        process();
        String str = computeString2(stack);
        if (str != null && lookupMap.remove(str) != null) {
            save();
        }
    }

    public static void handleEntityPlayer(EntityPlayer player) {
        process();
        if (!lookupMap.isEmpty() && player != null && player.inventory != null) {
            ItemStack[] mainInventory = player.inventory.mainInventory;
            if (mainInventory != null) {
                for (ItemStack stack : mainInventory) {
                    handleItemStack(stack);
                }
            }

            ItemStack[] armorInventory = player.inventory.armorInventory;
            if (armorInventory != null) {
                for (ItemStack stack2 : armorInventory) {
                    handleItemStack(stack2);
                }
            }
        }
    }

    private static void handleItemStack(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            String str = computeString2(stack);
            if (str != null) {
                String str2 = lookupMap.get(str);
                if (str2 != null && !str2.isEmpty()) {
                    if (!str2.equals(stack.getDisplayName())) {
                        stack.setStackDisplayName(str2);
                    }
                }
            }
        }
    }

    private static String computeString2(ItemStack stack) {
        return GetOrCreateHandler.computeString(stack);
    }
}
