package org.brutality.command.impl;

import java.util.List;
import java.util.Set;
import org.brutality.command.Command;
import org.brutality.module.ModuleManager;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.MessageUtils;

public class FriendsCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
            if (DefaultHelper.computeMap().isEmpty()) {
                ModuleManager.handleString(MessageUtils.text23 + MessageUtils.computeString("Friends list is already empty.", MessageUtils.text15));
            } else {
                int size = DefaultHelper.computeMap().size();
                DefaultHelper.process2();
                ModuleManager.handleString(MessageUtils.text23 + MessageUtils.computeString("Cleared " + size + " friend(s).", MessageUtils.text11));
            }
        } else {
            if (DefaultHelper.computeMap().isEmpty()) {
                ModuleManager.handleString(MessageUtils.text23 + MessageUtils.computeString("You have no friends added.", MessageUtils.text13));
            } else {
                var set = DefaultHelper.computeSet();
                ModuleManager.handleString(MessageUtils.text23 + MessageUtils.computeString("Friends list:", MessageUtils.text11));

                for (String str : set) {
                    var list = DefaultHelper.computeList(str);
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

    public FriendsCommand() {
        super("Friends", "Show all friends", ".friends [clear]", "friends", "buddys", "pals", "mates");
    }
}
