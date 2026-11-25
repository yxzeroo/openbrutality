package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.player.PlayerHelper7;

public class MutesCommand extends Command {
    public MutesCommand() {
        super("Mutes", "Show all muted players", ".mutes", "mutes", "mutelist", "blocked");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (PlayerHelper7.getLookupMap().isEmpty()) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                    + MessageUtils.computeString("You have no muted players.", MessageUtils.text13)
            );
        } else {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                    + MessageUtils.computeString("Muted players:", MessageUtils.text11)
            );

            for (String str : PlayerHelper7.getLookupMap().values()) {
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("B", MessageUtils.text5)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString(str, MessageUtils.text15)
                );
            }
        }
    }
}
