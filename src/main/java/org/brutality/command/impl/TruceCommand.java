package org.brutality.command.impl;

import java.util.ArrayList;
import java.util.List;
import org.brutality.command.Command;
import org.brutality.command.CommandHandler;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.TruceManager;

public class TruceCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        if (args.length == 1 && !args[0].trim().isEmpty()) {
            String str = args[0];
            TruceManager.matchesString2(str);
            MessageUtils.x(MessageUtils.text12 + str + (TruceManager.matchesString(str) ? " is now in truce." : " is no longer in truce."));
        } else {
            MessageUtils.x(MessageUtils.text13 + "Usage: " + this.text);
        }
    }

    @Override
    public List<String> computeList(String[] parts) {
        return (List<String>)(parts.length > 1 ? new ArrayList<>() : CommandHandler.computeList(parts, false));
    }

    public TruceCommand() {
        super("truce", "Toggle a player in your truce list", ".truce <name>");
    }
}
