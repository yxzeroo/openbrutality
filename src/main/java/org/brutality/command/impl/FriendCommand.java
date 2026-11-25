package org.brutality.command.impl;

import java.util.List;
import org.brutality.command.Command;
import org.brutality.command.CommandHandler;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.MessageUtils;

public class FriendCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        if (args.length < 1) {
            MessageUtils.x(MessageUtils.computeString("Usage: .friend <username> [group]", MessageUtils.text13));
            MessageUtils.x(MessageUtils.computeString("  .friend <name> - toggle friend", MessageUtils.text8));
            MessageUtils.x(MessageUtils.computeString("  .friend <name> <group> - add to group", MessageUtils.text8));
            MessageUtils.x(MessageUtils.computeString("  .friend group <name> <group> - set group", MessageUtils.text8));
        } else if (args[0].equalsIgnoreCase("group") && args.length >= 3) {
            String str = args[1];
            String str2 = args[2];
            if (DefaultHelper.matchesString(str)) {
                DefaultHelper.handleString2(str, str2);
                NotificationFeedHandler.handleString("§a[Friend] " + str + " moved to group: " + str2);
                MessageUtils.x(
                    MessageUtils.computeString(str, MessageUtils.text11)
                        + MessageUtils.text8
                        + " moved to group "
                        + MessageUtils.computeString(str2, MessageUtils.text12)
                );
            } else {
                MessageUtils.x(MessageUtils.computeString(str + " is not a friend.", MessageUtils.text13));
            }
        } else {
            String str3 = args[0];
            String str4 = args.length >= 2 ? args[1] : "default";
            if (DefaultHelper.matchesString(str3)) {
                DefaultHelper.handleString(str3);
                NotificationFeedHandler.handleString("§8[§c-§8] §a" + str3 + " §fremoved from friends.");
                MessageUtils.x(MessageUtils.computeString(str3, MessageUtils.text13) + MessageUtils.text8 + " has been removed from your friends list.");
            } else {
                DefaultHelper.handleString(str3, str4);
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
                NotificationFeedHandler.handleString("§8[§a+§8] §a" + str3 + " §fadded to friends.");
                MessageUtils.x(MessageUtils.computeString(str3, MessageUtils.text11) + MessageUtils.text8 + " has been added to your friends list." + str9);
            }
        }
    }

    @Override
    public List<String> computeList(String[] parts) {
        return CommandHandler.computeList(parts, false);
    }

    public FriendCommand() {
        super("Friend", "Manage friends", ".friend <username> [group]", "f", "b", "buddy", "p", "pal", "m", "mate", "a", "alliance");
    }
}
