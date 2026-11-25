package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class ModulesCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        ModuleManager.handleString(
            MessageUtils.computeString("[", MessageUtils.text9)
                + MessageUtils.computeString("B", MessageUtils.text5)
                + MessageUtils.computeString("] - ", MessageUtils.text9)
                + MessageUtils.computeString("Available Modules:", MessageUtils.text7)
        );

        for (Module module : ModuleManager.getEntries2()) {
            if (module.getCategory() != Category.HIDDEN) {
                String str = module.checkState44() ? "ON" : "OFF";
                ModuleManager.handleString(
                    MessageUtils.computeString("� ", MessageUtils.text7)
                        + MessageUtils.computeString(module.getName(), MessageUtils.text15)
                        + " ("
                        + MessageUtils.computeString(str, module.checkState44() ? MessageUtils.text11 : MessageUtils.text13)
                        + ")"
                );
            }
        }
    }

    public ModulesCommand() {
        super("Modules", "Displays a list of all available modules", ".modules", "modules");
    }
}
