package org.brutality.command.impl;

import java.util.List;
import java.util.Set;
import org.brutality.command.Command;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.WHelper;

public class KOSSCommand extends Command {
    public KOSSCommand() {
        super("KOSS", "Show all KOS", ".koss [clear]", "koss", "enemies", "targets", "opps");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
            if (WHelper.computeMap().isEmpty()) {
                ModuleManager.handleString(MessageUtils.text23 + MessageUtils.computeString("KOS list is already empty.", MessageUtils.text15));
            } else {
                int size = WHelper.computeMap().size();
                WHelper.process();
                ModuleManager.handleString(
                    MessageUtils.text23 + MessageUtils.computeString("Cleared " + size + " player(s) from KOS list.", MessageUtils.text11)
                );
            }
        } else {
            if (WHelper.computeMap().isEmpty()) {
                ModuleManager.handleString(MessageUtils.text23 + MessageUtils.computeString("You have no KOS players added.", MessageUtils.text13));
            } else {
                var set = WHelper.computeSet();
                ModuleManager.handleString(MessageUtils.text23 + MessageUtils.computeString("KOS list:", MessageUtils.text5));

                for (String str : set) {
                    var list = WHelper.computeList(str);
                    if (!list.isEmpty()) {
                        ModuleManager.handleString(MessageUtils.text23 + MessageUtils.computeString("[" + str + "]", MessageUtils.text12));

                        for (String str2 : list) {
                            ModuleManager.handleString(MessageUtils.text23 + "  " + MessageUtils.computeString(str2, MessageUtils.text15));
                        }
                    }
                }
            }
        }
    }
}
