package org.brutality.command.impl;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.command.Command;
import org.brutality.notification.old.ModuleEnabledHandler;
// VENDOR-DISABLED: import org.brutality.system.cloud.BrutalityCloudClient;
import org.brutality.utils.MessageUtils;

public class ScriptCommand extends Command {
    @Override
    public List<String> computeList(String[] parts) {
        ArrayList list = new ArrayList();
        if (parts.length == 1) {
            for (String str : new String[]{"load", "unload", "reload", "list", "folder", "loadall"}) {
                if (str.startsWith(parts[0].toLowerCase())) {
                    list.add(str);
                }
            }
        } else if (parts.length == 2) {
            String str2 = parts[0].toLowerCase();
            if ((str2.equals("load") || str2.equals("unload") || str2.equals("reload")) && Brutality.scriptManager != null) {
                for (String availableScriptFile : Brutality.scriptManager.getAvailableScriptFiles()) {
                    if (availableScriptFile.toLowerCase().startsWith(parts[1].toLowerCase())) {
                        list.add(availableScriptFile);
                    }
                }
            }
        }

        return list;
    }

    public ScriptCommand() {
        super("Script", "Manage scripts", ".script <load/unload/reload/list/folder> [name|id]", "scripts", "sc");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (Brutality.scriptManager == null) {
            MessageUtils.x("§cScript manager is not initialized!");
        } else if (args.length == 0) {
            this.process();
        } else {
            String str = args[0].toLowerCase();
            switch (str) {
                case "load":
                    if (args.length < 2) {
                        MessageUtils.x("§7Usage: §a.script load <filename|id>");
                        MessageUtils.x("§7Available scripts:");

                        for (String availableScriptFile : Brutality.scriptManager.getAvailableScriptFiles()) {
                            MessageUtils.x(" §7- §b" + availableScriptFile);
                        }

                        return;
                    }

                    String str2 = args[1];
                    String str3 = str2;
                    if (!str2.endsWith(".java")) {
                        str2 = str2 + ".java";
                    }

                    File file = new File(Brutality.scriptManager.text + File.separator + str2);
                    if (file.exists()) {
                        boolean flag = Brutality.scriptManager.matchesFile2(file);
                        this.handleString(str3, flag);
                        return;
                    }

                    // VENDOR-DISABLED: BrutalityCloudClient.downloadByIdAsync(...) remote cloud script download
                    MessageUtils.x("§cScript §b" + str3 + "§c not found locally.");
                    break;
                case "unload":
                    if (args.length < 2) {
                        MessageUtils.x("§7Usage: §a.script unload <name>");
                        var list = Brutality.scriptManager.computeList();
                        if (!list.isEmpty()) {
                            MessageUtils.x("§7Loaded scripts:");

                            for (String str5 : list) {
                                MessageUtils.x(" §7- " + str5);
                            }
                        }

                        return;
                    }

                    boolean flag4 = Brutality.scriptManager.matchesString3(args[1]);
                    if (flag4) {
                        ModuleEnabledHandler.handleString3("Script Unloaded", args[1], 2500L);
                        this.handleString("random.click", 0.5F, 0.9F);
                    }
                    break;
                case "reload":
                    if (args.length >= 2) {
                        Brutality.scriptManager.handleString2(args[1]);
                        ModuleEnabledHandler.handleString("Script Reloaded", args[1], 2500L);
                        this.handleString("random.levelup", 0.45F, 1.2F);
                    } else {
                        Brutality.scriptManager.reloadAll();
                        ModuleEnabledHandler.handleString("Scripts Reloaded", "All scripts reloaded", 2500L);
                        this.handleString("random.levelup", 0.45F, 1.2F);
                    }
                    break;
                case "list":
                    var list2 = Brutality.scriptManager.computeList();
                    if (list2.isEmpty()) {
                        MessageUtils.x("§7No scripts loaded. Use §a.script load <filename|id>");
                    } else {
                        MessageUtils.x("§7Loaded scripts (§b" + list2.size() + "§7):");

                        for (String str6 : list2) {
                            MessageUtils.x(" §7- " + str6);
                        }
                    }

                    var availableScriptFiles = Brutality.scriptManager.getAvailableScriptFiles();
                    if (!availableScriptFiles.isEmpty()) {
                        MessageUtils.x("§7Available in folder (§b" + availableScriptFiles.size() + "§7):");

                        for (String str7 : availableScriptFiles) {
                            MessageUtils.x(" §7- §e" + str7);
                        }
                    }

                    MessageUtils.x("§8[§9Info§8] Use §f.script load [name|id]§8 — Cloud ids work too");
                    break;
                case "folder":
                    try {
                        File file3 = new File(Brutality.scriptManager.text);
                        if (!file3.exists()) {
                            file3.mkdirs();
                        }

                        Desktop.getDesktop().open(file3);
                        MessageUtils.x("§7Opened scripts folder");
                    } catch (Exception e) {
                        MessageUtils.x("§7Scripts folder: §b" + Brutality.scriptManager.text);
                    }
                    break;
                case "loadall":
                    Brutality.scriptManager.loadAllScripts();
                    ModuleEnabledHandler.handleString("Scripts", "Loaded all available scripts", 2500L);
                    this.handleString("random.levelup", 0.5F, 1.15F);
                    break;
                default:
                    this.process();
            }
        }
    }

    private void handleString(String str, boolean flag) {
        if (flag) {
            MessageUtils.x("§8[§aScript§8] Successfully loaded §f[§a" + str + "§f]");
            ModuleEnabledHandler.handleString("Script Loaded", str, 3000L);
            this.handleString("note.pling", 0.7F, 1.4F);
        } else {
            MessageUtils.x("§8[§c!§8] Failed to load script §f[§c" + str + "§f]");
            ModuleEnabledHandler.handleString2("Script Failed", "Could not load " + str, 3500L);
            this.handleString("mob.villager.no", 0.6F, 1.0F);
        }
    }

    private void handleString(String str, float f, float f2) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null) {
            mc.thePlayer.playSound(str, f, f2);
        }
    }

    private void process() {
        MessageUtils.x("§7.script load <filename|id> §8- Load a script (Cloud id supported)");
        MessageUtils.x("§7.script unload <name> §8- Unload a script");
        MessageUtils.x("§7.script reload [name] §8- Reload one or all");
        MessageUtils.x("§7.script list §8- List loaded / available");
        MessageUtils.x("§7.script folder §8- Open scripts folder");
        MessageUtils.x("§7.script loadall §8- Load all scripts");
    }
}
