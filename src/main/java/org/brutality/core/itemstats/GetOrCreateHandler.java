package org.brutality.core.itemstats;

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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import org.brutality.utils.modules.AttemptDenickWithRetriesHelper;

public final class GetOrCreateHandler {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File file = new File("Brutality/itemstats.json");
    private static final Map<String, SerializeHandler> lookupMap = new ConcurrentHashMap<>();
    private static volatile boolean loaded;

    public static String computeString(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            Integer integer = AttemptDenickWithRetriesHelper.computeInteger(stack.getTagCompound());
            return integer != null && integer > 0 ? integer + "-" + computeInt(stack) : null;
        }
        return null;
    }

    private GetOrCreateHandler() {
    }

    public static synchronized void save() {
        load();

        try {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            JsonObject json = new JsonObject();

            for (Entry entry : lookupMap.entrySet()) {
                json.addProperty((String)entry.getKey(), ((SerializeHandler)entry.getValue()).serialize());
            }

            try (FileWriter fileWriter = new FileWriter(file)) {
                gson.toJson((JsonElement)json, fileWriter);
            }
        } catch (Exception ignored) {
        }
    }

    public static SerializeHandler getOrCreate(ItemStack stack) {
        String str = computeString(stack);
        if (str != null && (stack.getItem() == Items.golden_sword || stack.getItem() == Items.bow)) {
            load();
            return lookupMap.computeIfAbsent(str, str2 -> new SerializeHandler());
        }
        return null;
    }

    private static int computeInt(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("display", 10)) {
            StringBuilder sb = new StringBuilder();
            if (stack.getTagCompound().getCompoundTag("display").hasKey("Lore", 9)) {
                NBTTagList tagList = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);

                for (int i = 1; i < tagList.tagCount(); i++) {
                    sb.append(tagList.getStringTagAt(i)).append('|');
                }
            }

            return sb.toString().hashCode();
        }
        return 0;
    }

    private static synchronized void load() {
        if (!loaded) {
            loaded = true;
            if (file.isFile()) {
                try (FileReader fileReader = new FileReader(file)) {
                    JsonObject asJsonObject = new JsonParser().parse(fileReader).getAsJsonObject();

                    for (Entry entry : asJsonObject.entrySet()) {
                        try {
                            lookupMap.put((String)entry.getKey(), SerializeHandler.computeSerializeHandler(((JsonElement)entry.getValue()).getAsString()));
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignored2) {
                }
            }
        }
    }

    public static SerializeHandler computeSerializeHandler(ItemStack stack) {
        String str = computeString(stack);
        if (str == null) {
            return null;
        }
        load();
        return lookupMap.get(str);
    }
}
