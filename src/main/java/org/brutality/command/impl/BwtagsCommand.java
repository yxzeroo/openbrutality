package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.mellow.Mellow;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class BwtagsCommand extends Command {
    public BwtagsCommand() {
        super("bwtags", "Toggle BedWars tags", ".bwtags <info/on/off>");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length != 1) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("F", MessageUtils.text12)
                    + MessageUtils.computeString("] ", MessageUtils.text9)
                    + MessageUtils.computeString("Invalid usage! Use .bwtags <info/on/off>", MessageUtils.text13)
            );
        } else {
            String str = args[0].toLowerCase();
            if (str.equals("info")) {
                ModuleManager.handleString(MessageUtils.computeString("§b§lfon§9§lta§3§line", ""));
                ModuleManager.handleString(MessageUtils.computeString("§bmade by§e melissalmao", ""));
                ModuleManager.handleString("");
                ModuleManager.handleString(MessageUtils.computeString("§3Tags information:", ""));
                ModuleManager.handleString(MessageUtils.computeString("N = Suspicious name", ""));
                ModuleManager.handleString(MessageUtils.computeString("W = Winstreak while low star (1+ WS when 1-6 star)", ""));
                ModuleManager.handleString(MessageUtils.computeString("F = High FKDR while low star (4+ when 1-6 star)", ""));
                ModuleManager.handleString(MessageUtils.computeString("0F = 0 final kills 0 final deaths", ""));
                ModuleManager.handleString("");
            } else if (str.equals("on")) {
                Mellow.getInstance().getShowTagsSetting().setEnabled(true);
                Mellow.getInstance().process();
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Tags toggled: ON", MessageUtils.text11)
                );
            } else if (str.equals("off")) {
                Mellow.getInstance().getShowTagsSetting().setEnabled(false);
                Mellow.getInstance().process();
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Tags toggled: OFF", MessageUtils.text11)
                );
            } else {
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("F", MessageUtils.text12)
                        + MessageUtils.computeString("] ", MessageUtils.text9)
                        + MessageUtils.computeString("Invalid argument! Use info/on/off", MessageUtils.text13)
                );
            }
        }
    }
}
