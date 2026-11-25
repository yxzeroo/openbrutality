package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.module.impl.client.ClickGUI;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;

public class ClickGuiCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        ClickGUI instance = ClickGUI.getInstance();
        if (instance == null) {
            NotificationFeedHandler.handleString("§c[ClickGUI] Module is not loaded.");
        } else if (args.length != 1) {
            NotificationFeedHandler.handleString("§f[ClickGUI] Current design: §b" + instance.discordSetting.getText() + "§f. Usage: .clickgui <old|modern|discord>");
        } else {
            String str = computeString(args[0]);
            if (str == null) {
                NotificationFeedHandler.handleString("§c[ClickGUI] Unknown design. Use old, modern or discord.");
            } else {
                instance.discordSetting.handleString(str);
                NotificationFeedHandler.handleString("§f[ClickGUI] Design set to §b" + str + "§f.");
            }
        }
    }

    public ClickGuiCommand() {
        super("ClickGui", "Switches the ClickGUI design", ".clickgui <old|modern|discord>", "clickgui", "cgui");
    }

    private static String computeString(String str) {
        for (String str2 : new String[]{"Old", "Modern", "Discord"}) {
            if (str2.equalsIgnoreCase(str)) {
                return str2;
            }
        }

        return null;
    }
}
