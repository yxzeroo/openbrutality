package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.mellow.Mellow;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class MinfkdrCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        if (args.length != 1) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("F", MessageUtils.text12)
                    + MessageUtils.computeString("] ", MessageUtils.text9)
                    + MessageUtils.computeString("Invalid usage! Use .minfkdr <number>", MessageUtils.text13)
            );
        } else {
            try {
                double d = Double.parseDouble(args[0]);
                Mellow.getInstance().getMinFKDRSetting().handleDouble2(d);
                Mellow.getInstance().process();
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Minimum FKDR set to: " + d, MessageUtils.text11)
                );
            } catch (NumberFormatException e) {
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Invalid number! Use a numeric value.", MessageUtils.text13)
                );
            }
        }
    }

    public MinfkdrCommand() {
        super("minfkdr", "Set minimum FKDR", ".minfkdr <number>");
    }
}
