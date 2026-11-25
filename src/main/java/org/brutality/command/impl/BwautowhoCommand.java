package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.mellow.Mellow;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class BwautowhoCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        if (args.length != 1) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("F", MessageUtils.text12)
                    + MessageUtils.computeString("] ", MessageUtils.text9)
                    + MessageUtils.computeString("Invalid usage! Use .bwautowho <on/off>", MessageUtils.text13)
            );
        } else {
            String str = args[0].toLowerCase();
            if (str.equals("on")) {
                Mellow.getInstance().getAutoWhoSetting().setEnabled(true);
                Mellow.getInstance().process();
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Auto WHO: ON", MessageUtils.text11)
                );
            } else if (str.equals("off")) {
                Mellow.getInstance().getAutoWhoSetting().setEnabled(false);
                Mellow.getInstance().process();
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Auto WHO: OFF", MessageUtils.text11)
                );
            } else {
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Invalid argument! Use on/off", MessageUtils.text13)
                );
            }
        }
    }

    public BwautowhoCommand() {
        super("bwautowho", "Toggle auto /who", ".bwautowho <on/off>");
    }
}
