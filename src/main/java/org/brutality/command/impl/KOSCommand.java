package org.brutality.command.impl;

import java.util.List;
import org.brutality.command.Command;
import org.brutality.command.CommandHandler;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.WHelper;

public class KOSCommand extends Command {
    public KOSCommand() {
        super("KOS", "Manage KOS list", ".kos <username> [group]", "k", "target", "e", "enemy", "o", "opp");
    }

    @Override
    public List<String> computeList(String[] parts) {
        return CommandHandler.computeList(parts, false);
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length < 1) {
            MessageUtils.x(MessageUtils.computeString("Usage: .kos <username> [group]", MessageUtils.text13));
            MessageUtils.x(MessageUtils.computeString("  .kos <name> - toggle KOS", MessageUtils.text8));
            MessageUtils.x(MessageUtils.computeString("  .kos <name> <group> - add to group", MessageUtils.text8));
            MessageUtils.x(MessageUtils.computeString("  .kos group <name> <group> - set group", MessageUtils.text8));
        } else if (args[0].equalsIgnoreCase("group") && args.length >= 3) {
            String str = args[1];
            String str2 = args[2];
            if (WHelper.matchesString(str)) {
                WHelper.handleString2(str, str2);
                NotificationFeedHandler.handleString("§c[KOS] " + str + " moved to group: " + str2);
                MessageUtils.x(
                    MessageUtils.computeString(str, MessageUtils.text5)
                        + MessageUtils.text8
                        + " moved to group "
                        + MessageUtils.computeString(str2, MessageUtils.text12)
                );
            } else {
                MessageUtils.x(MessageUtils.computeString(str + " is not on KOS list.", MessageUtils.text13));
            }
        } else {
            String str3 = args[0];
            String str4 = args.length >= 2 ? args[1] : "default";
            if (WHelper.matchesString(str3)) {
                WHelper.handleString(str3);
                NotificationFeedHandler.handleString("§8[§c-§8] §c" + str3 + " §fremoved from KOS.");
                MessageUtils.x(MessageUtils.computeString(str3, MessageUtils.text8) + MessageUtils.text8 + " has been removed from your KOS list.");
            } else {
                WHelper.handleString(str3, str4);
                String str5;
                if (str4.equals("default")) {
                    str5 = "";
                } else {
                    str5 = MessageUtils.text8;
                    String str6 = MessageUtils.computeString(str4, MessageUtils.text12);
                    String str7 = MessageUtils.text8;
                    String str8 = str6;
                    str5 = str5 + " (group: " + str8 + str7 + ")";
                }

                String str9 = str5;
                NotificationFeedHandler.handleString("§8[§a+§8] §c" + str3 + " §fadded to KOS.");
                MessageUtils.x(MessageUtils.computeString(str3, MessageUtils.text5) + MessageUtils.text8 + " has been added to your KOS list." + str9);
            }
        }
    }
}
