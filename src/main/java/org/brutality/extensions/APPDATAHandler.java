package org.brutality.extensions;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.module.impl.visual.FPSBooster;
import org.brutality.notification.old.ModuleEnabledHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ColorThemeSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.MultiBoolSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;

public class APPDATAHandler {
    private static File file = new File("Brutality/Configs");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final HashSet<String> itemSet = Sets.newHashSet();
    private String text = null;
    private boolean flag = false;
    private static final Map<Integer, String> lookupMap = new HashMap<>();
    private static final File file2 = new File("Brutality/configbinds.json");
    private static final long timestamp = 2500L;

    public void process6() {
        lookupMap.clear();
        this.process7();
    }

    private static File computeFile2() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.mcDataDir != null) {
                return new File(mc.mcDataDir, "Brutality/Configs");
            }
        } catch (Throwable ignored) {
        }

        String str = System.getenv("APPDATA");
        return str != null && !str.isEmpty() ? new File(str, ".minecraft/Brutality/Configs") : new File("Brutality/Configs");
    }

    public boolean matchesString3(String str) {
        File file = new File(this.computeFile(), str + ".json");
        return file.exists() && file.isFile();
    }

    public void init() {
        file = computeFile2();
        if (!file.exists()) {
            file.mkdirs();
        }

        this.process5();
    }

    public void process5() {
        lookupMap.clear();
        if (file2.exists()) {
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader(file2))) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    if (json != null) {
                        for (Entry entry : json.entrySet()) {
                            try {
                                int n = Integer.parseInt((String)entry.getKey());
                                String asString = ((JsonElement)entry.getValue()).getAsString();
                                lookupMap.put(n, asString);
                            } catch (Exception ignored) {
                            }
                        }

                        return;
                    }
                }
            } catch (Exception ignored2) {
            }
        }
    }

    private void handleJsonObject2(JsonObject json, Setting setting) {
        if (setting instanceof BooleanSetting) {
            json.addProperty(setting.getName(), ((BooleanSetting)setting).isEnabled());
        } else if (setting instanceof NumberSetting) {
            json.addProperty(setting.getName(), ((NumberSetting)setting).getValue2());
        } else if (setting instanceof ModeSetting) {
            json.addProperty(setting.getName(), ((ModeSetting)setting).getText());
        } else if (setting instanceof MultiBoolSetting) {
            JsonArray jsonArray = new JsonArray();

            for (String str : ((MultiBoolSetting)setting).computeSet()) {
                jsonArray.add(new JsonPrimitive(str));
            }

            json.add(setting.getName(), jsonArray);
        } else if (setting instanceof ColorThemeSetting) {
            JsonObject json2 = new JsonObject();
            ColorThemeSetting colorThemeSetting = (ColorThemeSetting)setting;
            json2.addProperty("color", colorThemeSetting.getDefaultColor());
            json2.addProperty("secondaryColor", colorThemeSetting.getDefaultColor2());
            json2.addProperty("chroma", colorThemeSetting.isEnabled());
            json2.addProperty("mode", colorThemeSetting.getText());
            json2.addProperty("blendFactor", colorThemeSetting.getAmount());
            json2.addProperty("fadeSpeed", colorThemeSetting.getAmount2());
            json2.addProperty("fadeSpread", colorThemeSetting.getAmount3());
            json.add(setting.getName(), json2);
        } else if (setting instanceof ColorSetting) {
            JsonObject json3 = new JsonObject();
            ColorSetting colorSetting = (ColorSetting)setting;
            json3.addProperty("hue", colorSetting.getFactor());
            json3.addProperty("saturation", colorSetting.getSaturation());
            json3.addProperty("brightness", colorSetting.getFactor4());
            json3.addProperty("alpha", colorSetting.getAlpha());
            json.add(setting.getName(), json3);
        } else if (setting instanceof StringSetting) {
            json.addProperty(setting.getName(), ((StringSetting)setting).getValue());
        } else if (setting instanceof KeybindListSetting) {
            JsonObject json4 = new JsonObject();

            for (Setting setting2 : ((KeybindListSetting)setting).getEntries()) {
                try {
                    this.handleJsonObject(json4, setting2);
                } catch (Exception ignored) {
                }
            }

            json.add(setting.getName(), json4);
        }
    }

    public boolean matchesString(String str, int n) {
        if (!this.matchesString3(str)) {
            return false;
        }
        lookupMap.remove(n);
        lookupMap.put(n, str);
        this.process7();
        return true;
    }

    public boolean matchesInt(int n) {
        String str = lookupMap.get(n);
        return str != null && this.matchesString3(str) ? this.matchesString(str) : false;
    }

    public void process2() {
        this.flag = true;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public static void process4() {
        if (Brutality.appdataHandler != null) {
            Brutality.appdataHandler.process2();
        }
    }

    public void process3() {
        for (Module module : ModuleManager.getEntries2()) {
            if (module.checkState44() && !this.matchesModule(module)) {
                module.process39();
            }

            for (Setting setting : Brutality.settingsManager.computeList(module)) {
                try {
                    this.handleSetting(setting);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private boolean matchesModule(Module module) {
        return module != null && module.checkState3();
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean matchesInt2(int n) {
        String str = lookupMap.remove(n);
        if (str != null) {
            this.process7();
            return true;
        }
        return false;
    }

    private void handleSetting(Setting setting) {
        if (setting instanceof BooleanSetting) {
            ((BooleanSetting)setting).setEnabled(((BooleanSetting)setting).isEnabled3());
        } else if (setting instanceof NumberSetting) {
            ((NumberSetting)setting).handleDouble(((NumberSetting)setting).getDefaultValue());
        } else if (setting instanceof ModeSetting) {
            ((ModeSetting)setting).handleString(((ModeSetting)setting).getDefaultMode());
        } else if (setting instanceof MultiBoolSetting) {
            ((MultiBoolSetting)setting).reset();
        } else if (setting instanceof ColorThemeSetting colorThemeSetting) {
            colorThemeSetting.setDefaultColor(colorThemeSetting.getDefaultColor3());
            colorThemeSetting.setDefaultColor2(colorThemeSetting.getDefaultColor3());
            colorThemeSetting.handleFlag(false);
            colorThemeSetting.handleString("Solid");
            colorThemeSetting.handleDouble3(1.0);
            colorThemeSetting.handleDouble2(1.7);
            colorThemeSetting.handleDouble(5.0);
        } else if (setting instanceof ColorSetting colorSetting) {
            colorSetting.handleFloat3(colorSetting.getFactor2());
            colorSetting.handleFloat4(colorSetting.getFactor3());
            colorSetting.handleFloat2(colorSetting.getFactor5());
            colorSetting.handleFloat(colorSetting.getFactor6());
        } else if (setting instanceof StringSetting) {
            ((StringSetting)setting).setValue(((StringSetting)setting).getText());
        } else if (setting instanceof KeybindListSetting) {
            for (Setting setting2 : ((KeybindListSetting)setting).getEntries()) {
                try {
                    this.handleSetting(setting2);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static void handleJsonObject(JsonObject json, String str, JsonObject json2, String str2) {
        JsonElement element = json.get(str);
        if (!json2.has(str2) && element != null && element.isJsonPrimitive()) {
            json2.add(str2, element);
        }
    }

    private void handleSetting(Setting setting, JsonElement element, Module module) {
        if (setting instanceof BooleanSetting) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
                ((BooleanSetting)setting).setEnabled(element.getAsBoolean());
            }
        } else if (setting instanceof NumberSetting) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                ((NumberSetting)setting).handleDouble4(element.getAsDouble());
            }
        } else if (setting instanceof ModeSetting) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String asString = element.getAsString();
                ModeSetting modeSetting = (ModeSetting)setting;
                if (modeSetting.matchesString(asString)) {
                    modeSetting.handleString(asString);
                }
            }
        } else if (setting instanceof MultiBoolSetting multiBoolSetting) {
            if (element.isJsonArray()) {
                ArrayList list = new ArrayList();

                for (JsonElement element2 : element.getAsJsonArray()) {
                    if (element2.isJsonPrimitive() && element2.getAsJsonPrimitive().isString()) {
                        list.add(element2.getAsString());
                    }
                }

                multiBoolSetting.handleCollection(list);
            } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                multiBoolSetting.handleCollection(Arrays.asList(element.getAsString().split(",\\s*")));
            }
        } else if (setting instanceof ColorThemeSetting colorThemeSetting) {
            if (element.isJsonObject()) {
                JsonObject asJsonObject = element.getAsJsonObject();
                if (asJsonObject.has("color")) {
                    colorThemeSetting.setDefaultColor(asJsonObject.get("color").getAsInt());
                }

                if (asJsonObject.has("secondaryColor")) {
                    colorThemeSetting.setDefaultColor2(asJsonObject.get("secondaryColor").getAsInt());
                }

                if (asJsonObject.has("mode")) {
                    colorThemeSetting.handleString(asJsonObject.get("mode").getAsString());
                }

                if (asJsonObject.has("blendFactor")) {
                    colorThemeSetting.handleDouble3(asJsonObject.get("blendFactor").getAsDouble());
                }

                if (asJsonObject.has("fadeSpeed")) {
                    colorThemeSetting.handleDouble2(asJsonObject.get("fadeSpeed").getAsDouble());
                }

                if (asJsonObject.has("fadeSpread")) {
                    colorThemeSetting.handleDouble(asJsonObject.get("fadeSpread").getAsDouble());
                }

                if (asJsonObject.has("chroma")) {
                    colorThemeSetting.handleFlag(asJsonObject.get("chroma").getAsBoolean());
                }
            } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                colorThemeSetting.setDefaultColor(element.getAsInt());
                colorThemeSetting.handleFlag(false);
                colorThemeSetting.handleString("Solid");
            }
        } else if (setting instanceof ColorSetting colorSetting) {
            if (element.isJsonObject()) {
                JsonObject asJsonObject2 = element.getAsJsonObject();
                if (asJsonObject2.has("hue")) {
                    float asFloat = asJsonObject2.get("hue").getAsFloat();
                    colorSetting.handleFloat3(Math.max(0.0F, Math.min(1.0F, asFloat)));
                }

                if (asJsonObject2.has("saturation")) {
                    float asFloat2 = asJsonObject2.get("saturation").getAsFloat();
                    colorSetting.handleFloat4(Math.max(0.0F, Math.min(1.0F, asFloat2)));
                }

                if (asJsonObject2.has("brightness")) {
                    float asFloat3 = asJsonObject2.get("brightness").getAsFloat();
                    colorSetting.handleFloat2(Math.max(0.0F, Math.min(1.0F, asFloat3)));
                }

                if (asJsonObject2.has("alpha")) {
                    float asFloat4 = asJsonObject2.get("alpha").getAsFloat();
                    colorSetting.handleFloat(Math.max(0.0F, Math.min(1.0F, asFloat4)));
                }
            } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                float asFloat5 = element.getAsFloat();
                colorSetting.handleFloat3(Math.max(0.0F, Math.min(1.0F, asFloat5)));
            }
        } else if (setting instanceof StringSetting) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                ((StringSetting)setting).setValue(element.getAsString());
            }
        } else if (setting instanceof KeybindListSetting && element.isJsonObject()) {
            JsonObject asJsonObject3 = element.getAsJsonObject();

            for (Setting setting2 : ((KeybindListSetting)setting).getEntries()) {
                if (asJsonObject3.has(setting2.getName())) {
                    try {
                        this.handleSetting(setting2, asJsonObject3.get(setting2.getName()), module);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    private void process() {
        if (Brutality.moduleManager != null) {
            for (Module module : ModuleManager.getEntries2()) {
                if (this.matchesModule(module) && !this.itemSet.contains(module.getClass().getName())) {
                    try {
                        if (!module.checkState44() || !ModuleManager.entries3.contains(module)) {
                            module.process30();
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    public File computeFile() {
        if (file == null || !file.exists()) {
            file = computeFile2();
        }

        return file;
    }

    private static void handleJsonObject3(JsonObject json) {
        JsonElement element = json.remove("Right Click View");
        if (element != null && element.isJsonObject()) {
            JsonElement element2 = element.getAsJsonObject().get("enabled");
            if (element2 != null && element2.isJsonPrimitive() && element2.getAsJsonPrimitive().isBoolean()) {
                if (!json.has("PitUtils")) {
                    json.add("PitUtils", new JsonObject());
                }

                if (json.get("PitUtils").isJsonObject()) {
                    JsonObject asJsonObject = json.getAsJsonObject("PitUtils");
                    if (!asJsonObject.has("settings")) {
                        asJsonObject.add("settings", new JsonObject());
                    }

                    if (asJsonObject.get("settings").isJsonObject()) {
                        JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("settings");
                        if (!asJsonObject2.has("Right Click View in Spawn")) {
                            asJsonObject2.add("Right Click View in Spawn", element2);
                        }
                    }
                }
            }
        }
    }

    public String getText() {
        return this.text;
    }

    public boolean matchesModule2(Module module) {
        return this.itemSet.contains(module.getClass().getName());
    }

    private void handleJsonObject(JsonObject json, Setting setting) {
        if (setting instanceof BooleanSetting) {
            json.addProperty(setting.getName(), ((BooleanSetting)setting).isEnabled());
        } else if (setting instanceof NumberSetting) {
            json.addProperty(setting.getName(), ((NumberSetting)setting).getValue2());
        } else if (setting instanceof ModeSetting) {
            json.addProperty(setting.getName(), ((ModeSetting)setting).getText());
        } else if (setting instanceof MultiBoolSetting) {
            JsonArray jsonArray = new JsonArray();

            for (String str : ((MultiBoolSetting)setting).computeSet()) {
                jsonArray.add(new JsonPrimitive(str));
            }

            json.add(setting.getName(), jsonArray);
        } else if (setting instanceof ColorThemeSetting) {
            JsonObject json2 = new JsonObject();
            ColorThemeSetting colorThemeSetting = (ColorThemeSetting)setting;
            json2.addProperty("color", colorThemeSetting.getDefaultColor());
            json2.addProperty("secondaryColor", colorThemeSetting.getDefaultColor2());
            json2.addProperty("chroma", colorThemeSetting.isEnabled());
            json2.addProperty("mode", colorThemeSetting.getText());
            json2.addProperty("blendFactor", colorThemeSetting.getAmount());
            json2.addProperty("fadeSpeed", colorThemeSetting.getAmount2());
            json2.addProperty("fadeSpread", colorThemeSetting.getAmount3());
            json.add(setting.getName(), json2);
        } else if (setting instanceof ColorSetting) {
            JsonObject json3 = new JsonObject();
            ColorSetting colorSetting = (ColorSetting)setting;
            json3.addProperty("hue", colorSetting.getFactor());
            json3.addProperty("saturation", colorSetting.getSaturation());
            json3.addProperty("brightness", colorSetting.getFactor4());
            json3.addProperty("alpha", colorSetting.getAlpha());
            json.add(setting.getName(), json3);
        } else if (setting instanceof StringSetting) {
            json.addProperty(setting.getName(), ((StringSetting)setting).getValue());
        }
    }

    private static void handleJsonObject(JsonObject json) {
        if (json.get("Pizza") instanceof JsonObject json2 && json2.get("settings") instanceof JsonObject json3) {
            if (json3.get("ESP") instanceof JsonObject json4) {
                json3.remove("ESP");
                handleJsonObject(json4, "ESP", json3, "ESP");
                handleJsonObject(json4, "ESP Labels", json3, "ESP Labels");
            }

            if (json3.get("List") instanceof JsonObject json5) {
                handleJsonObject(json5, "Pizza List", json3, "Pizza HUD");
                handleJsonObject(json5, "List X", json3, "HUD X");
                handleJsonObject(json5, "List Y", json3, "HUD Y");
            }
        }
    }

    private static void handleJsonObject2(JsonObject json) {
        JsonElement element = json.get("Show Hunters And Victims");
        JsonElement element2 = json.get("Show Only Victims");
        if (element != null || element2 != null) {
            boolean flag = element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean() || element.getAsBoolean();
            boolean flag2 = element2 != null && element2.isJsonPrimitive() && element2.getAsJsonPrimitive().isBoolean() && element2.getAsBoolean();
            if (!json.has("Show Hunters")) {
                json.addProperty("Show Hunters", flag && !flag2);
            }

            if (!json.has("Show Victims")) {
                json.addProperty("Show Victims", flag);
            }
        }
    }

    public boolean matchesString(String str) {
        this.process3();
        File file = new File(this.computeFile(), str + ".json");
        if (!file.exists()) {
            return false;
        }
        try {
            boolean flag;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                if (json == null) {
                    return false;
                }

                EnabledHandler.handleJsonObject(json);
                SpeedHandler.handleJsonObject(json);
                ScaffoldHandler.handleJsonObject(json);
                WatermarkHandler.handleJsonObject(json);
                SettingsHandler.handleJsonObject(json);
                KeepSprintHandler.handleJsonObject(json);
                KillAuraHandler.handleJsonObject(json);
                WarAntiLauncherHandler.handleJsonObject(json);
                DropLocationHandler.handleJsonObject(json);
                if (!json.has("PitUtils") && json.has("Utils")) {
                    json.add("PitUtils", json.remove("Utils"));
                }

                handleJsonObject3(json);
                handleJsonObject(json);

                for (Entry entry : json.entrySet()) {
                    try {
                        Module module = Brutality.moduleManager.computeModule((String)entry.getKey());
                        if (module != null && !this.itemSet.contains(module.getClass().getName())) {
                            JsonObject asJsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();
                            if (asJsonObject.has("enabled")) {
                                boolean asBoolean = asJsonObject.get("enabled").getAsBoolean();
                                if (this.matchesModule(module)) {
                                    asBoolean = true;
                                }

                                if (asBoolean) {
                                    module.process30();
                                } else if (module.checkState44()) {
                                    module.process39();
                                }
                            }

                            if (asJsonObject.has("key")) {
                                int asInt = asJsonObject.get("key").getAsInt();
                                module.handleInt4(asInt);
                            }

                            if (asJsonObject.has("hidden")) {
                                boolean asBoolean2 = asJsonObject.get("hidden").getAsBoolean();
                                boolean flag2 = ModuleManager.matchesModule(module);
                                if (asBoolean2 != flag2) {
                                    ModuleManager.handleModule2(module);
                                }
                            }

                            if (asJsonObject.has("settings")) {
                                JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("settings");
                                if (module instanceof VenomedTimer) {
                                    handleJsonObject2(asJsonObject2);
                                }

                                for (Setting setting : Brutality.settingsManager.computeList(module)) {
                                    if (module instanceof FPSBooster
                                        && "Show KOS".equals(setting.getName())
                                        && !asJsonObject2.has("Show KOS")
                                        && asJsonObject2.has("Spawn KOS Only")) {
                                        asJsonObject2.add("Show KOS", asJsonObject2.get("Spawn KOS Only"));
                                    }

                                    if (module instanceof KillAura
                                        && "Horizontal Angle".equals(setting.getName())
                                        && !asJsonObject2.has("Horizontal Angle")
                                        && asJsonObject2.has("Angle")) {
                                        asJsonObject2.add("Horizontal Angle", asJsonObject2.get("Angle"));
                                    }

                                    if (asJsonObject2.has(setting.getName())) {
                                        try {
                                            this.handleSetting(setting, asJsonObject2.get(setting.getName()), module);
                                        } catch (Exception ignored) {
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                int n = 0;

                for (Entry entry2 : json.entrySet()) {
                    JsonObject asJsonObject3 = ((JsonElement)entry2.getValue()).getAsJsonObject();
                    if (asJsonObject3.has("hidden") && asJsonObject3.get("hidden").getAsBoolean()) {
                        n++;
                    }
                }

                this.setText(str);
                this.process8();
                this.process();
                flag = true;
            }

            return flag;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static Map<Integer, String> computeMap() {
        return new HashMap<>(lookupMap);
    }

    public static String computeString(int n) {
        return lookupMap.get(n);
    }

    public boolean matchesString2(String str) {
        File file = new File(this.computeFile(), str + ".json");

        try {
            JsonObject json = new JsonObject();
            int n = 0;

            for (Module module : ModuleManager.getEntries2()) {
                JsonObject json2 = new JsonObject();
                json2.addProperty("enabled", module.checkState44());
                json2.addProperty("key", module.getKey());
                boolean flag = ModuleManager.matchesModule(module);
                json2.addProperty("hidden", flag);
                if (flag) {
                    n++;
                }

                JsonObject json3 = new JsonObject();

                for (Setting setting : Brutality.settingsManager.computeList(module)) {
                    try {
                        this.handleJsonObject2(json3, setting);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                json2.add("settings", json3);
                json.add(module.getName(), json2);
            }

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            PrintWriter writer = new PrintWriter(new FileWriter(file));

            try {
                writer.println(gson.toJson((JsonElement)json));
            } catch (Throwable e2) {
                try {
                    writer.close();
                } catch (Throwable e3) {
                    e2.addSuppressed(e3);
                }

                throw e2;
            }

            writer.close();
            if (Brutality.scriptManager != null) {
                Brutality.scriptManager.process3();
            }

            this.setText(str);
            this.process8();
            ModuleEnabledHandler.handleString("Config Saved", "Saved " + str, timestamp);
            return true;
        } catch (Exception e4) {
            e4.printStackTrace();
            return false;
        }
    }

    public void process8() {
        this.flag = false;
    }

    public void process7() {
        try {
            JsonObject json = new JsonObject();

            for (Entry entry : lookupMap.entrySet()) {
                json.addProperty(String.valueOf(entry.getKey()), (String)entry.getValue());
            }

            File parentFile = file2.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file2))) {
                writer.println(gson.toJson((JsonElement)json));
            }
        } catch (Exception ignored) {
        }
    }
}
