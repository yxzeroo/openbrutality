package org.brutality.command.impl;

import org.brutality.command.CmdManager;
import org.brutality.command.Command;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class HelpCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        ModuleManager.handleString(
            MessageUtils.computeString("[", MessageUtils.text9)
                + MessageUtils.computeString("B", MessageUtils.text5)
                + MessageUtils.computeString("] - ", MessageUtils.text9)
                + MessageUtils.computeString("Available Commands:", MessageUtils.text11)
        );

        for (Command command : CmdManager.getCommands()) {
            String str = String.format("%s - %s (Syntax: %s)", command.name2, command.description2, command.text);
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                    + MessageUtils.computeString(command.name2, MessageUtils.text5)
                    + MessageUtils.computeString(" - ", MessageUtils.text9)
                    + MessageUtils.computeString(command.description2, MessageUtils.text8)
                    + MessageUtils.computeString(" (Syntax: ", MessageUtils.text9)
                    + MessageUtils.computeString(command.text, MessageUtils.text12)
                    + MessageUtils.computeString(")", MessageUtils.text9)
            );
        }
    }

    public HelpCommand() {
        super("Help", "Shows a list of all available commands", ".help", "help");
    }
}
