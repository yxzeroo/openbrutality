package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;

public class DestructCommand extends Command {
    public DestructCommand() {
        super("Destruct", "Disables all enabled modules", ".destruct", "destruct", "panic", "disable");
    }

    @Override
    public void onCommand(String[] args, String message) {
        int n = 0;

        for (Module module : ModuleManager.getEntries2()) {
            if (module.getCategory() != Category.HIDDEN && module.checkState44()) {
                module.handleFlag3(false);
                n++;
            }
        }

        if (n > 0) {
            NotificationFeedHandler.handleString("§c[Destruct] Successfully disabled " + n + " modules!");
        } else {
            NotificationFeedHandler.handleString("§7[Destruct] No modules were enabled to disable.");
        }
    }
}
