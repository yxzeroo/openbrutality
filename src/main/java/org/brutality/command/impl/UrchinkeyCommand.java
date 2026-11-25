package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.mellow.Mellow;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class UrchinkeyCommand extends Command {
    public UrchinkeyCommand() {
        super("urchinkey", "Set Urchin API key", ".urchinkey <key>");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length != 1) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("F", MessageUtils.text12)
                    + MessageUtils.computeString("] ", MessageUtils.text9)
                    + MessageUtils.computeString("Invalid usage! Use .urchinkey <apikey>", MessageUtils.text13)
            );
        } else {
            String str = args[0];
            Mellow.getInstance().getUrchinKeySetting().setValue(str);
            Mellow.getInstance().process();
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("F", MessageUtils.text12)
                    + MessageUtils.computeString("] ", MessageUtils.text9)
                    + MessageUtils.computeString("Urchin API Key set to: " + str, MessageUtils.text11)
            );
        }
    }
}
