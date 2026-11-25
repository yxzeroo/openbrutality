package org.brutality.command.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import org.brutality.command.Command;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.WHelper;
import org.brutality.utils.modules.AttemptDenickWithRetriesHelper;

public class DenickCommand extends Command {
    public static String computeString(String str) {
        return AttemptDenickWithRetriesHelper.computeString(str);
    }

    private EntityPlayer computeEntityPlayer(String str) {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.theWorld != null ? mc.theWorld.getPlayerEntityByName(str) : null;
    }

    public static void handleEntry(AttemptDenickWithRetriesHelper.Entry entry) {
        boolean flag = false;
        boolean flag2 = false;
        boolean flag3 = WHelper.matchesString(entry.text2);
        boolean flag4 = WHelper.matchesString(entry.text);
        if (flag3 || flag4) {
            if (!flag3) {
                WHelper.w(entry.text2);
                handleString2("§8[§4B§8] §7- §6Auto-added §c" + entry.text2 + " §6to KOS §8(real name was in KOS)");
                flag = true;
            }

            if (!flag4) {
                WHelper.w(entry.text);
                handleString2("§8[§4B§8] §7- §6Auto-added §c" + entry.text + " §6to KOS §8(nick was in KOS)");
                flag = true;
            }
        }

        boolean flag5 = DefaultHelper.matchesString(entry.text2);
        boolean flag6 = DefaultHelper.matchesString(entry.text);
        if (flag5 || flag6) {
            if (!flag5) {
                DefaultHelper.handleString2(entry.text2);
                handleString2("§8[§4B§8] §7- §6Auto-added §a" + entry.text2 + " §6to Friends §8(real name was in Friends)");
                flag2 = true;
            }

            if (!flag6) {
                DefaultHelper.handleString2(entry.text);
                handleString2("§8[§4B§8] §7- §6Auto-added §a" + entry.text + " §6to Friends §8(nick was in Friends)");
                flag2 = true;
            }
        }

        if (flag && flag2) {
            handleString2("§8[§4B§8] §7- §eWarning: player was in both KOS and Friends lists!");
        }
    }

    private void handleString(String str) {
        this.sendMessage("§8[§4B§8] §7- §9Denicking §f" + str + "§9...");
        EntityPlayer player = this.computeEntityPlayer(str);
        if (player == null) {
            this.sendMessage("§8[§4B§8] §7- §c" + str + " §7is not in the current world.");
        } else {
            AttemptDenickWithRetriesHelper.tryDenickPlayerWithValidation(str, player, str2 -> Minecraft.getMinecraft().addScheduledTask(() -> {
                this.sendMessage(str2);
                AttemptDenickWithRetriesHelper.Entry entry = AttemptDenickWithRetriesHelper.computeEntry(str);
                if (entry != null) {
                    handleEntry(entry);
                }
            }));
        }
    }

    public static int computeInt() {
        return AttemptDenickWithRetriesHelper.computeInt();
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length == 0) {
            this.sendMessage("§8[§4B§8] §7- §7Usage: .denick <username> §8| §7list §8| §7clear §8| §7remove <name>");
        } else {
            String str = args[0].toLowerCase();
            switch (str) {
                case "list":
                    this.process();
                    return;
                case "clear":
                    AttemptDenickWithRetriesHelper.process();
                    this.sendMessage("§8[§4B§8] §7- §aDenick cache cleared.");
                    return;
                case "add":
                    if (args.length < 2) {
                        this.sendMessage("§8[§4B§8] §7- §7Usage: .denick add <username>");
                        return;
                    }

                    this.handleString(args[1]);
                    return;
                case "remove":
                    if (args.length < 2) {
                        this.sendMessage("§8[§4B§8] §7- §7Usage: .denick remove <username>");
                        return;
                    }

                    if (AttemptDenickWithRetriesHelper.matchesString3(args[1])) {
                        this.sendMessage("§8[§4B§8] §7- §aRemoved §f" + args[1] + " §afrom cache.");
                    } else {
                        this.sendMessage("§8[§4B§8] §7- §c" + args[1] + " §7not found in cache.");
                    }

                    return;
                default:
                    this.handleString(args[0]);
            }
        }
    }

    private static void handleString2(String str) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(str));
        }
    }

    public DenickCommand() {
        super("Denick", "Denicks a nicked player on Hypixel Pit", "denick <username|list|clear|remove>", "dn", "enick");
    }

    private void process() {
        var collection = AttemptDenickWithRetriesHelper.computeCollection();
        if (collection.isEmpty()) {
            this.sendMessage("§8[§4B§8] §7- §7No players denicked this session.");
        } else {
            this.sendMessage("§8[§4B§8] §7- §7Denicked players this session §8(§7" + collection.size() + "§8):");

            for (AttemptDenickWithRetriesHelper.Entry entry : collection) {
                String str = entry.text3;
                String text = entry.text;
                String str2 = entry.text2;
                this.sendMessage("  §8- §f" + str2 + " §8→ §a" + text + " §8(§7" + str + "§8)");
            }
        }
    }

    public static String computeString2(String str) {
        return AttemptDenickWithRetriesHelper.computeString2(str);
    }

    public static boolean matchesString(String str) {
        return AttemptDenickWithRetriesHelper.matchesString(str);
    }

    @Override
    public List<String> computeList(String[] parts) {
        List list = new ArrayList();
        String str = parts.length > 0 ? parts[parts.length - 1].toLowerCase() : "";
        Minecraft mc = Minecraft.getMinecraft();
        if (parts.length <= 1) {
            for (String str2 : new String[]{"list", "clear", "add", "remove"}) {
                if (str2.startsWith(str)) {
                    list.add(str2);
                }
            }
        }

        if (mc.theWorld != null) {
            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity != mc.thePlayer && playerEntity.getName().toLowerCase().startsWith(str)) {
                    list.add(playerEntity.getName());
                }
            }
        }

        return list;
    }

    public static Collection<AttemptDenickWithRetriesHelper.Entry> computeCollection() {
        return AttemptDenickWithRetriesHelper.computeCollection();
    }

    public static void process2() {
        AttemptDenickWithRetriesHelper.process();
    }

    private void sendMessage(String str) {
        MessageUtils.x(str);
    }

    public static AttemptDenickWithRetriesHelper.Entry computeEntry(String str) {
        return AttemptDenickWithRetriesHelper.computeEntry(str);
    }

    public static boolean matchesString2(String str) {
        return AttemptDenickWithRetriesHelper.matchesString3(str);
    }
}
