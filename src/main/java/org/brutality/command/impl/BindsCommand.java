package org.brutality.command.impl;

import org.brutality.Brutality;
import org.brutality.command.Command;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class BindsCommand extends Command {
    public BindsCommand() {
        super("Binds", "Shows a list of all currently bound modules", ".binds", "binds");
    }

    @Override
    public void onCommand(String[] args, String message) {
        boolean flag = false;
        ModuleManager.handleString(
            MessageUtils.computeString("[", MessageUtils.text9)
                + MessageUtils.computeString("B", MessageUtils.text5)
                + MessageUtils.computeString("] - ", MessageUtils.text9)
                + MessageUtils.computeString("Current Binds:", MessageUtils.text11)
        );

        for (Module module : ModuleManager.getEntries2()) {
            if (module.getKey() != 0 && module.getKey() != 0) {
                String str = BindCommand.computeString(module.getKey());
                String name = module.getName();
                if (Brutality.scriptManager != null && Brutality.scriptManager.isScriptModule(module.getName())) {
                    name = name + " (script)";
                }

                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("B", MessageUtils.text5)
                        + MessageUtils.computeString("] - ", MessageUtils.text9)
                        + MessageUtils.computeString(name, MessageUtils.text5)
                        + MessageUtils.computeString(" - ", MessageUtils.text8)
                        + MessageUtils.computeString(str, MessageUtils.text12)
                );
                flag = true;
            }
        }

        if (!flag) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                    + MessageUtils.computeString("No modules are currently bound.", MessageUtils.text13)
            );
        }
    }
}
