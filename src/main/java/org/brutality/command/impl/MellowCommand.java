package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.mellow.commands.HelpHandler;
import org.brutality.utils.MessageUtils;

public class MellowCommand extends Command {
    public MellowCommand() {
        super("mellow", "Mellow commands", ".mellow <command>", "mw");
    }

    @Override
    public void onCommand(String[] args, String message) {
        boolean flag = HelpHandler.matchesStringArray(args);
        if (!flag) {
            MessageUtils.x(MessageUtils.text13 + "Command failed");
        }
    }
}
