package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.mellow.Mellow;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class BwCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        if (args.length != 1) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("F", MessageUtils.text12)
                    + MessageUtils.computeString("] ", MessageUtils.text9)
                    + MessageUtils.computeString("Invalid usage! Use .bw <username>", MessageUtils.text13)
            );
        } else {
            String str = args[0];
            if (Mellow.clearCacheHandler != null) {
                Mellow.getInstance().requestStats(str);
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Fetching stats for: " + str, MessageUtils.text11)
                );
            } else {
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Mellow is not enabled!", MessageUtils.text13)
                );
            }
        }
    }

    public BwCommand() {
        super("bw", "Check bedwars stats", ".bw <username>");
    }
}
