package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.mellow.Mellow;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class UrchinCommand extends Command {
    public UrchinCommand() {
        super("urchin", "Toggle Urchin API", ".urchin <on/off>");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length != 1) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("F", MessageUtils.text12)
                    + MessageUtils.computeString("] ", MessageUtils.text9)
                    + MessageUtils.computeString("Invalid usage! Use .urchin <on/off>", MessageUtils.text13)
            );
        } else {
            String str = args[0].toLowerCase();
            if (str.equals("on")) {
                Mellow.getInstance().getUrchinEnabledSetting().setEnabled(true);
                Mellow.getInstance().process();
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Urchin API toggled: ON", MessageUtils.text11)
                );
            } else if (str.equals("off")) {
                Mellow.getInstance().getUrchinEnabledSetting().setEnabled(false);
                Mellow.getInstance().process();
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Urchin API toggled: OFF", MessageUtils.text11)
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
}
