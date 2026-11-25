package org.brutality.command.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import org.brutality.command.Command;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;

public class ToggleCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        if (args.length > 0 && !String.join(" ", args).trim().isEmpty()) {
            String str = String.join(" ", args).trim();
            boolean flag = false;

            for (Module module : ModuleManager.getEntries2()) {
                if (module.getCategory() != Category.HIDDEN && module.name2.equalsIgnoreCase(str)) {
                    module.process28();
                    String str2 = module.checkState44() ? "enabled" : "disabled";
                    String str3 = module.checkState44() ? "§a" : "§c";
                    String name2 = module.name2;
                    NotificationFeedHandler.handleString("§f[Toggle] " + name2 + " has been " + str3 + str2 + "§f.");
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                NotificationFeedHandler.handleString("§c[Toggle] Invalid Module Name!");
            }
        } else {
            NotificationFeedHandler.handleString("§c[Toggle] Usage: .toggle <module>");
        }
    }

    public ToggleCommand() {
        super("Toggle", "Toggles a module on or off", ".toggle <module>", "toggle", "t");
    }

    @Override
    public List<String> computeList(String[] parts) {
        String str = String.join(" ", parts).toLowerCase(Locale.ROOT);
        TreeSet treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (Module module : ModuleManager.getEntries2()) {
            if (module.getCategory() != Category.HIDDEN && module.getName() != null && module.getName().toLowerCase(Locale.ROOT).startsWith(str)) {
                treeSet.add(module.getName());
            }
        }

        return new ArrayList<>(treeSet);
    }
}
