package org.brutality.command.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.brutality.command.Command;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.misc.WorldExporter;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;

public class WorldExportCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        WorldExporter worldExporter = this.computeWorldExporter();
        if (worldExporter == null) {
            NotificationFeedHandler.handleString("§cWorldExporter module not found!");
        } else if (args.length == 0) {
            this.handleWorldExporter4(worldExporter);
        } else {
            String str = args[0].toLowerCase();
            switch (str) {
                case "binary":
                    worldExporter.handleString3(args.length > 1 ? args[1] : "export");
                    break;
                case "clear":
                    worldExporter.process3();
                    break;
                case "info":
                    this.handleWorldExporter3(worldExporter);
                    break;
                case "status":
                    this.handleWorldExporter2(worldExporter);
                    break;
                default:
                    worldExporter.handleString(str);
            }
        }
    }

    private void handleWorldExporter3(WorldExporter worldExporter) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§8[§4WorldExporter§8] §fInformation"));
            mc.thePlayer.addChatMessage(new ChatComponentText("§7Captured Blocks: §f" + worldExporter.computeInt()));
            mc.thePlayer.addChatMessage(new ChatComponentText("§7Last Export: §f" + worldExporter.getCount3() + " blocks"));
            mc.thePlayer.addChatMessage(new ChatComponentText("§7Export File: §f" + (worldExporter.getText() != null ? worldExporter.getText() : "None")));
            mc.thePlayer.addChatMessage(new ChatComponentText("§7Export Directory: §fBrutality/WorldExports/"));
        }
    }

    private void handleWorldExporter2(WorldExporter worldExporter) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§8[§4WorldExporter§8] §fStatus"));
            this.handleWorldExporter(worldExporter);
        }
    }

    private void handleWorldExporter4(WorldExporter worldExporter) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§8§m────────────────────────────────§r §4WorldExporter §8§m────────────────────────────────"));
            mc.thePlayer.addChatMessage(new ChatComponentText("§aUsage:"));
            mc.thePlayer.addChatMessage(new ChatComponentText("  §f.export §7- Export to JSON with default name"));
            mc.thePlayer.addChatMessage(new ChatComponentText("  §f.export <name> §7- Export with custom filename"));
            mc.thePlayer.addChatMessage(new ChatComponentText("  §f.export binary §7- Export to compressed format"));
            mc.thePlayer.addChatMessage(new ChatComponentText("  §f.export clear §7- Clear captured data"));
            mc.thePlayer.addChatMessage(new ChatComponentText("  §f.export info §7- Show statistics"));
            mc.thePlayer.addChatMessage(new ChatComponentText(""));
            mc.thePlayer.addChatMessage(new ChatComponentText("§aCurrent Status:"));
            this.handleWorldExporter(worldExporter);
            mc.thePlayer.addChatMessage(new ChatComponentText("§8§m──────────────────────────────────────────────────────────────────"));
        }
    }

    private WorldExporter computeWorldExporter() {
        for (Module module : ModuleManager.getEntries2()) {
            if (module instanceof WorldExporter) {
                return (WorldExporter)module;
            }
        }

        return null;
    }

    private void handleWorldExporter(WorldExporter worldExporter) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            String str = worldExporter.isFlag() ? "§7Scanning: §e" + worldExporter.getCount() + "%" : "§aIdle";
            mc.thePlayer.addChatMessage(new ChatComponentText("  §7Status: " + str));
            mc.thePlayer.addChatMessage(new ChatComponentText("  §7Captured: §f" + worldExporter.computeInt() + " blocks"));
            mc.thePlayer.addChatMessage(new ChatComponentText("  §7Module: " + (worldExporter.checkState44() ? "§aEnabled" : "§cDisabled")));
        }
    }

    public WorldExportCommand() {
        super("WorldExport", "Exports captured blocks from WorldExporter module", ".export [filename|binary|clear|info]", "export", "we", "worldexport");
    }
}
