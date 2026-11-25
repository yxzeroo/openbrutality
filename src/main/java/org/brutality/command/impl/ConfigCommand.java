package org.brutality.command.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.command.Command;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
// VENDOR-DISABLED: import org.brutality.system.cloud.BrutalityCloudClient;
import org.brutality.utils.MessageUtils;

public class ConfigCommand extends Command {
    private void handleString2(String str) {
        if (Brutality.appdataHandler.matchesString3(str)) {
            this.handleString(str);
        } else {
            // VENDOR-DISABLED: BrutalityCloudClient.downloadByIdAsync(...) remote cloud config download
            MessageUtils.x("§8[§c!§8] Config §f[§c" + str + "§f] §fnot found locally.");
        }
    }

    public ConfigCommand() {
        super("Config", "Manages configurations", ".config [save/load/folder/list] [name|id]", "config", "c");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length < 1) {
            MessageUtils.x("§8[§c!§8] Usage: .config [save/load/folder/list] [name|id]");
        } else {
            String str = args[0].toLowerCase();
            if (str.equals("folder")) {
                try {
                    File file = this.computeFile();
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    Desktop.getDesktop().open(file);
                    MessageUtils.x("§8[§aConfig§8] Opened configs folder");
                } catch (IOException e) {
                    MessageUtils.x("§8[§c!§8] Failed to open configs folder: " + e.getMessage());
                }
            } else if (str.equals("list")) {
                this.listConfigs();
            } else if (args.length < 2) {
                MessageUtils.x("§8[§c!§8] Usage: .config [save/load] [name|id]");
            } else {
                String str2 = args[1];
                switch (str) {
                    case "save":
                        if (Brutality.appdataHandler.matchesString2(str2)) {
                            MessageUtils.x("§8[§aConfig§8] Successfully saved config §f[§a" + str2 + "§f]");
                        } else {
                            MessageUtils.x("§8[§c!§8] Failed to save configuration.");
                        }
                        break;
                    case "load":
                        this.handleString2(str2);
                        break;
                    default:
                        MessageUtils.x("§8[§c!§8] Invalid action. Use save, load, folder, or list.");
                }
            }
        }
    }

    @Override
    public List<String> computeList(String[] parts) {
        ArrayList list = new ArrayList();
        if (parts.length == 1) {
            for (String str : new String[]{"save", "load", "list", "folder"}) {
                if (str.startsWith(parts[0].toLowerCase())) {
                    list.add(str);
                }
            }
        } else if (parts.length == 2 && (parts[0].equalsIgnoreCase("load") || parts[0].equalsIgnoreCase("save"))) {
            File file = this.computeFile();
            if (file.exists()) {
                File[] files = file.listFiles((file2, str2) -> str2.endsWith(".json"));
                if (files != null) {
                    for (File file3 : files) {
                        String str3 = file3.getName().replace(".json", "");
                        if (str3.toLowerCase().startsWith(parts[1].toLowerCase())) {
                            list.add(str3);
                        }
                    }
                }
            }
        }

        return list;
    }

    private File computeFile() {
        if (Brutality.instance != null && Brutality.appdataHandler != null) {
            File file = Brutality.appdataHandler.computeFile();
            if (file != null) {
                return file;
            }
        }

        String str = System.getenv("APPDATA");
        return str != null && !str.isEmpty() ? new File(str, ".minecraft/Brutality/Configs") : new File("Brutality/Configs");
    }

    private void handleString(String str) {
        for (Module module : ModuleManager.getEntries2()) {
            if (module.checkState44()) {
                module.handleFlag3(false);
            }
        }

        if (Brutality.appdataHandler.matchesString(str)) {
            MessageUtils.x("§8[§aConfig§8] Successfully loaded config §f[§a" + str + "§f]");
        } else {
            MessageUtils.x("§8[§c!§8] Failed to load config §f[§c" + str + "§f]");
        }
    }

    private void listConfigs() {
        File file = this.computeFile();
        if (!file.exists()) {
            MessageUtils.x("§8[§c!§8] No configs found.");
        } else {
            File[] files = file.listFiles((file2, str) -> str.endsWith(".json"));
            if (files != null && files.length != 0) {
                MessageUtils.x("§8[§aConfig§8] §fAvailable Configs:");

                for (int i = 0; i < files.length; i++) {
                    String str2 = files[i].getName().replace(".json", "");
                    String str3 = i % 2 == 0 ? "§a" : "§f";
                    int n = i + 1;
                    String str4 = i % 2 == 0 ? "a" : "f";
                    int n2 = n;
                    MessageUtils.x(str3 + "  [§7" + n2 + "§" + str4 + "] " + str2);
                }

                MessageUtils.x("§8[§9Info§8] Use §f.config load [name|id]§8 — Cloud ids work too");
            } else {
                MessageUtils.x("§8[§c!§8] No configs found.");
            }
        }
    }
}
