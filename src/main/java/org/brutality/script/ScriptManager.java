package org.brutality.script;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.settings.Setting;
import org.brutality.utils.MessageUtils;

public class ScriptManager {
    public final String text;
    public final String text2;
    private final String text3;
    public final JavaCompiler javaCompiler;
    public final List<String> entries;
    private final List<Script> g = new CopyOnWriteArrayList<>();
    private final Map<String, ScriptModule> lookupMap = new LinkedHashMap<>();
    private final Set<String> itemSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Float[] computeFloatTypeArray(String str, Script script, Object... objs) {
        return script == null ? null : script.invokeFloatArray(str, objs);
    }

    public void process() {
        try {
            if (Brutality.newClickGUI != null) {
                Brutality.newClickGUI.clearCache();
            }
        } catch (Exception ignored) {
        }
    }

    public void handleScript2(Script script) {
        script.invokeVoid("onEnable");
        this.process3();
    }

    private void handleFile(File file) {
        String str = "// Example Brutality Script\n// This script shows your coordinates on screen\n// Enable it from the SCRIPTS category in ClickGUI\n//\n// Settings (these show up in the Scripts GUI):\n//   BooleanSetting = booleanSetting(name, default)\n//   NumberSetting  = numberSetting(name, default, min, max, increment)\n//   ModeSetting    = modeSetting(name, default, modes...)\n//   StringSetting  = stringSetting(name, default)\n//\n// Available callbacks:\n//   onEnable()       - called when script is toggled on\n//   onDisable()      - called when script is toggled off\n//   onPreUpdate()    - called before game update\n//   onPostUpdate()   - called after game update\n//   onRender2D(float partialTicks) - called for 2D rendering\n//   onRender3D(float partialTicks) - called for 3D rendering\n//   boolean onPacketSend(String packetName) - return false to cancel\n//   boolean onPacketReceive(String packetName) - return false to cancel\n//   boolean onChat(String message) - return false to cancel\n//   onLoad()         - called when script is loaded\n//   onUnload()       - called when script is unloaded\n//\n// API: client, world, modules, render, gl, inventory, keybinds, util, timer, math, player\n\nBooleanSetting showCoords = booleanSetting(\"Show Coords\", true);\nNumberSetting textScale = numberSetting(\"Text Scale\", 1.0, 0.5, 2.0, 0.1);\nModeSetting coordColor = modeSetting(\"Coord Color\", \"White\", \"White\", \"Red\", \"Green\", \"Blue\");\n\npublic void onEnable() {\n    client.print(\"&aCoords HUD enabled!\");\n}\n\npublic void onDisable() {\n    client.print(\"&cCoords HUD disabled!\");\n}\n\npublic void onRender2D(float partialTicks) {\n    if (!showCoords.isEnabled() || !client.isInGame()) return;\n    String coords = String.format(\"X: %.1f Y: %.1f Z: %.1f\", client.getX(), client.getY(), client.getZ());\n    render.text(coords, 5, 5, render.color(255, 255, 255));\n}\n";
        String str2 = "// AutoSprint Script - Automatically sprints when moving forward\n\npublic void onEnable() {\n    client.print(\"&aAutoSprint enabled\");\n}\n\npublic void onDisable() {\n    client.print(\"&cAutoSprint disabled\");\n}\n\npublic void onPreUpdate() {\n    if (client.isMoving() && !client.isSneaking() && client.getFood() > 6) {\n        client.setSprinting(true);\n    }\n}\n";

        try {
            File file2 = new File(file, "ExampleScript.java");
            Files.write(file2.toPath(), str.getBytes());
            File file3 = new File(file, "AutoSprintScript.java");
            Files.write(file3.toPath(), str2.getBytes());
        } catch (IOException ignored) {
        }
    }

    private void init() {
        File file = new File(this.text);
        if (!file.exists()) {
            file.mkdirs();
        }

        File file2 = new File(this.text2);
        if (!file2.exists()) {
            file2.mkdirs();
        }

        File[] files = file.listFiles((file3, str) -> str.endsWith(".java"));
        if (files == null || files.length == 0) {
            this.handleFile(file);
        }
    }

    public void process3() {
        JsonObject json = new JsonObject();

        for (Entry entry : this.lookupMap.entrySet()) {
            ScriptModule val = (ScriptModule)entry.getValue();
            String str = val.getScript().file != null ? val.getScript().file.getName() : (String)entry.getKey();
            JsonObject json2 = new JsonObject();
            json2.addProperty("enabled", val.checkState44());
            json2.addProperty("key", val.getKey());
            json2.addProperty("hidden", ModuleManager.matchesModule(val));
            json.add(str, json2);
        }

        try {
            File file = new File(this.text3);

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(gson.toJson((JsonElement)json));
            }
        } catch (IOException ignored) {
        }
    }

    public boolean matchesString(String str) {
        if (!str.endsWith(".java")) {
            str = str + ".java";
        }

        return this.itemSet.contains(str.toLowerCase());
    }

    public void handleFile2(File file) {
        if (this.javaCompiler == null) {
            MessageUtils.x("§cJava compiler not available! Run with JDK, not JRE.");
        } else {
            String name = file.getName();
            if (this.lookupMap.containsKey(name.toLowerCase())) {
                this.handleString3(name);
            } else if (this.itemSet.contains(name.toLowerCase())) {
                MessageUtils.x("§7Already compiling: §b" + computeString(name));
            } else {
                String str;
                try {
                    str = new String(Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    MessageUtils.x("§cFailed to read: " + name);
                    return;
                }

                this.itemSet.add(name.toLowerCase());
                MessageUtils.x("§7[§4B§7] Compiling §b" + computeString(name) + "§7...");
                Script script = new Script(name);
                script.file = file;
                script.handleString(str);
                Thread thread = new Thread(() -> {
                    boolean flag = script.checkState();
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        this.itemSet.remove(name.toLowerCase());
                        if (flag) {
                            script.invokeVoid("onLoad");
                            ScriptModule scriptModule = new ScriptModule(script);
                            ModuleManager.handleModule3(scriptModule);
                            this.g.add(script);
                            this.lookupMap.put(name.toLowerCase(), scriptModule);
                            scriptModule.handleFlag3(true);
                            this.process();
                            this.process3();
                            MessageUtils.x("§7[§4B§7] Script §b" + computeString(name) + " §7loaded");
                        }
                    });
                }, "Script-Compiler");
                thread.setDaemon(true);
                thread.start();
            }
        }
    }

    public void handleScript(Script script) {
        script.invokeVoid("onDisable");
        this.process3();
    }

    public List<String> computeList() {
        ArrayList list = new ArrayList();

        for (Entry entry : this.lookupMap.entrySet()) {
            String str = computeString((String)entry.getKey());
            boolean flag = ((ScriptModule)entry.getValue()).checkState44();
            list.add((flag ? "§a" : "§c") + str);
        }

        return list;
    }

    public boolean isScriptModule(String str) {
        for (ScriptModule scriptModule : this.lookupMap.values()) {
            if (scriptModule.getName().equalsIgnoreCase(str)) {
                return true;
            }
        }

        File file = new File(this.text);
        File[] files = file.listFiles((file2, str2) -> str2.endsWith(".java"));
        if (files != null) {
            for (File file3 : files) {
                if (computeString(file3.getName()).equalsIgnoreCase(str)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void process2() {
        File file = new File(this.text3);
        if (file.exists()) {
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    if (json != null) {
                        int n = 0;

                        for (Entry entry : json.entrySet()) {
                            String key = (String)entry.getKey();
                            JsonObject asJsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();
                            File file2 = new File(this.text + File.separator + key);
                            if (file2.exists() && this.matchesFile(file2)) {
                                n++;
                                ScriptModule scriptModule = this.lookupMap.get(key.toLowerCase());
                                if (scriptModule != null) {
                                    if (asJsonObject.has("key")) {
                                        scriptModule.handleInt4(asJsonObject.get("key").getAsInt());
                                    }

                                    if (asJsonObject.has("enabled") && asJsonObject.get("enabled").getAsBoolean()) {
                                        scriptModule.handleFlag3(true);
                                    }

                                    if (asJsonObject.has("hidden") && asJsonObject.get("hidden").getAsBoolean() && !ModuleManager.matchesModule(scriptModule)) {
                                        ModuleManager.handleModule2(scriptModule);
                                    }
                                }
                            }
                        }

                        if (n > 0) {
                        }

                        return;
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    public boolean matchesString3(String str) {
        if (!str.endsWith(".java")) {
            str = str + ".java";
        }

        ScriptModule scriptModule = this.lookupMap.get(str.toLowerCase());
        if (scriptModule == null) {
            MessageUtils.x("§cScript §b" + str + "§c is not loaded.");
            return false;
        }
        if (scriptModule.checkState44()) {
            scriptModule.handleFlag3(false);
        }

        for (Setting setting : new ArrayList<>(Brutality.settingsManager.computeList(scriptModule))) {
            Brutality.settingsManager.handleSetting(setting);
        }

        Script script = scriptModule.getScript();
        script.invokeVoid("onUnload");
        script.delete();
        this.g.remove(script);
        this.lookupMap.remove(str.toLowerCase());
        ModuleManager.handleModule(scriptModule);
        this.process();
        this.process3();
        MessageUtils.x("§7[§4B§7] Script §b" + computeString(str) + " §7unloaded");
        return true;
    }

    private boolean matchesFile(File file, boolean flag) {
        if (this.javaCompiler == null) {
            if (flag) {
                MessageUtils.x("§cJava compiler not available! Run with JDK, not JRE.");
            }

            return false;
        }
        try {
            String str = new String(Files.readAllBytes(file.toPath()));
            String name = file.getName();
            if (this.lookupMap.containsKey(name.toLowerCase())) {
                if (flag) {
                    MessageUtils.x("§7Script §b" + name + "§7 is already loaded. Use §a.script reload§7 to reload.");
                }

                return false;
            }
            Script script = new Script(name);
            script.file = file;
            script.handleString(str);
            if (!script.checkState()) {
                return false;
            }
            script.invokeVoid("onLoad");
            ScriptModule scriptModule = new ScriptModule(script);
            ModuleManager.handleModule3(scriptModule);
            this.g.add(script);
            this.lookupMap.put(name.toLowerCase(), scriptModule);
            this.process();
            this.process3();
            if (flag) {
                MessageUtils.x("§7[§4B§7] Script §b" + computeString(name) + " §7loaded successfully");
            }

            return true;
        } catch (IOException e) {
            if (flag) {
                MessageUtils.x("§cFailed to read script file: " + file.getName());
            }

            return false;
        }
    }

    public void handleString(String str) {
        if (!str.endsWith(".java")) {
            str = str + ".java";
        }

        ScriptModule scriptModule = this.lookupMap.get(str.toLowerCase());
        if (scriptModule != null && scriptModule.checkState44()) {
            scriptModule.handleFlag3(false);
        }
    }

    public void loadAllScripts() {
        File file = new File(this.text);
        File[] files = file.listFiles((file2, str) -> str.endsWith(".java"));
        if (files != null && files.length != 0) {
            int n = 0;

            for (File file3 : files) {
                if (this.matchesFile2(file3)) {
                    n++;
                }
            }

            if (n > 0) {
                MessageUtils.x("§7[§4B§7] Loaded §b" + n + "§7 script(s)");
            }
        }
    }

    public List<String> getAvailableScriptFiles() {
        ArrayList list = new ArrayList();
        File file = new File(this.text);
        File[] files = file.listFiles((file2, str) -> str.endsWith(".java"));
        if (files != null) {
            for (File file3 : files) {
                list.add(file3.getName());
            }
        }

        return list;
    }

    public void handleString2(String str) {
        if (!str.endsWith(".java")) {
            str = str + ".java";
        }

        ScriptModule scriptModule = this.lookupMap.get(str.toLowerCase());
        boolean flag = false;
        int n = 0;
        if (scriptModule != null) {
            flag = scriptModule.checkState44();
            n = scriptModule.getKey();
            this.matchesString3(str);
        }

        File file = new File(this.text + File.separator + str);
        if (!file.exists()) {
            MessageUtils.x("§cScript file §b" + str + "§c not found.");
        } else {
            if (this.matchesFile2(file)) {
                ScriptModule scriptModule2 = this.lookupMap.get(str.toLowerCase());
                if (scriptModule2 != null) {
                    if (n != 0) {
                        scriptModule2.handleInt4(n);
                    }

                    if (flag) {
                        scriptModule2.handleFlag3(true);
                    }

                    this.process3();
                }
            }
        }
    }

    public void handleString3(String str) {
        if (!str.endsWith(".java")) {
            str = str + ".java";
        }

        ScriptModule scriptModule = this.lookupMap.get(str.toLowerCase());
        if (scriptModule != null && !scriptModule.checkState44()) {
            scriptModule.handleFlag3(true);
        }
    }

    private static String computeString(String str) {
        return str.endsWith(".java") ? str.substring(0, str.length() - 5) : str;
    }

    public boolean ensureLoaded(String str) {
        for (ScriptModule scriptModule : this.lookupMap.values()) {
            if (scriptModule.getName().equalsIgnoreCase(str)) {
                return true;
            }
        }

        File file = new File(this.text);
        File[] files = file.listFiles((file2, str2) -> str2.endsWith(".java"));
        if (files == null) {
            return false;
        }
        for (File file3 : files) {
            String str3 = computeString(file3.getName());
            if (str3.equalsIgnoreCase(str)) {
                return this.matchesFile(file3);
            }
        }

        return false;
    }

    public ScriptManager() {
        this.text = "Brutality" + File.separator + "Scripts";
        this.text2 = "Brutality" + File.separator + "Scripts" + File.separator + "compiled";
        this.text3 = "Brutality" + File.separator + "Scripts" + File.separator + "scripts.json";
        this.javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (this.javaCompiler == null) {
        }

        this.entries = new CopyOnWriteArrayList<>(
            Arrays.asList(
                "net.minecraft.client.Minecraft",
                "net.minecraft.client.entity.EntityPlayerSP",
                "net.minecraft.client.multiplayer.WorldClient",
                "net.minecraft.client.gui.*",
                "net.minecraft.client.renderer.*",
                "net.minecraft.entity.*",
                "net.minecraft.entity.player.*",
                "net.minecraft.entity.monster.*",
                "net.minecraft.entity.passive.*",
                "net.minecraft.item.*",
                "net.minecraft.block.*",
                "net.minecraft.block.state.*",
                "net.minecraft.init.*",
                "net.minecraft.network.*",
                "net.minecraft.network.play.client.*",
                "net.minecraft.network.play.server.*",
                "net.minecraft.util.*",
                "net.minecraft.potion.*",
                "net.minecraft.enchantment.*",
                "net.minecraft.nbt.*",
                "net.minecraft.client.settings.KeyBinding",
                "org.lwjgl.input.Keyboard",
                "org.lwjgl.input.Mouse",
                "org.lwjgl.opengl.GL11",
                "org.brutality.Brutality",
                "org.brutality.module.Module",
                "org.brutality.module.ModuleManager",
                "org.brutality.utils.MessageUtils",
                "org.brutality.settings.impl.*",
                "org.brutality.script.ScriptDefaults",
                "java.util.*",
                "java.awt.Color"
            )
        );
        this.init();
    }

    public int computeInt(String str, Script script, Object... objs) {
        return script == null ? -1 : script.invokeBoolean(str, objs);
    }

    public boolean matchesString2(String str) {
        if (!str.endsWith(".java")) {
            str = str + ".java";
        }

        ScriptModule scriptModule = this.lookupMap.get(str.toLowerCase());
        return scriptModule != null && scriptModule.checkState44();
    }

    private boolean matchesFile(File file) {
        return this.matchesFile(file, false);
    }

    public boolean matchesFile2(File file) {
        return this.matchesFile(file, true);
    }

    public Module computeModule(String str) {
        if (str != null && !str.isEmpty()) {
            String str2 = str.trim();
            if (str2.endsWith(".java")) {
                str2 = str2.substring(0, str2.length() - 5);
            }

            for (ScriptModule scriptModule : this.lookupMap.values()) {
                if (scriptModule.getName().equalsIgnoreCase(str2)) {
                    return scriptModule;
                }
            }

            File file = new File(this.text, str2 + ".java");
            if (!file.isFile()) {
                return null;
            }
            String str3 = file.getName().toLowerCase();
            if (!this.lookupMap.containsKey(str3)) {
                this.matchesFile2(file);
            }

            return this.lookupMap.get(str3);
        }
        return null;
    }

    public boolean matchesString4(String str) {
        if (!str.endsWith(".java")) {
            str = str + ".java";
        }

        return this.lookupMap.containsKey(str.toLowerCase());
    }

    public void reloadAll() {
        HashMap map = new HashMap();
        HashMap map2 = new HashMap();

        for (Entry entry : this.lookupMap.entrySet()) {
            map.put((String)entry.getKey(), ((ScriptModule)entry.getValue()).checkState44());
            map2.put((String)entry.getKey(), ((ScriptModule)entry.getValue()).getKey());
        }

        for (String str : new ArrayList<>(this.lookupMap.keySet())) {
            this.matchesString3(str);
        }

        File file = new File(this.text);
        File[] files = file.listFiles((file2, str2) -> str2.endsWith(".java"));
        if (files != null) {
            int n = 0;

            for (File file3 : files) {
                if (this.matchesFile2(file3)) {
                    n++;
                    String str3 = file3.getName().toLowerCase();
                    ScriptModule scriptModule = this.lookupMap.get(str3);
                    if (scriptModule != null) {
                        Integer integer = (Integer)map2.get(str3);
                        if (integer != null && integer != 0) {
                            scriptModule.handleInt4(integer);
                        }

                        Boolean val = (Boolean)map.get(str3);
                        if (val != null && val) {
                            scriptModule.handleFlag3(true);
                        }
                    }
                }
            }

            this.process3();
            MessageUtils.x("§7[§4B§7] Reloaded §b" + n + "§7 script(s)");
        }
    }
}
