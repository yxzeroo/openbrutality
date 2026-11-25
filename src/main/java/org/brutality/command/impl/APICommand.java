package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.utils.ApiTxtHelper;

public class APICommand extends Command {
    public APICommand() {
        super("API", "Manage Hypixel API key", ".api <set/remove/get> [key]", "apikey", "hypixelapi");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length < 1) {
            NotificationFeedHandler.handleString("§c[API] Usage: .api <set/remove/get> [key]");
        } else {
            String str = args[0].toLowerCase();
            switch (str) {
                case "set":
                    if (args.length < 2) {
                        NotificationFeedHandler.handleString("§c[API] Usage: .api set <your-api-key>");
                        return;
                    }

                    String str2 = args[1];
                    if (!this.matchesString(str2)) {
                        NotificationFeedHandler.handleString("§c[API] Invalid API key format! Hypixel API keys should be in UUID format.");
                        return;
                    }

                    if (ApiTxtHelper.matchesString(str2)) {
                        NotificationFeedHandler.handleString("§a[API] Successfully set Hypixel API key!");
                    } else {
                        NotificationFeedHandler.handleString("§c[API] Failed to save API key!");
                    }
                    break;
                case "remove":
                case "delete":
                case "clear":
                    if (ApiTxtHelper.checkState()) {
                        NotificationFeedHandler.handleString("§a[API] Successfully removed Hypixel API key!");
                    } else {
                        NotificationFeedHandler.handleString("§c[API] No API key found to remove!");
                    }
                    break;
                case "get":
                case "show":
                case "display":
                    String str3 = ApiTxtHelper.computeString();
                    if (str3 != null && !str3.isEmpty()) {
                        String str4 = str3.substring(0, 8) + "****-****-****-" + str3.substring(str3.length() - 4);
                        NotificationFeedHandler.handleString("§a[API] Current API key: " + str4);
                    } else {
                        NotificationFeedHandler.handleString("§c[API] No API key is currently set!");
                    }
                    break;
                case "test":
                case "verify":
                    String str5 = ApiTxtHelper.computeString();
                    if (str5 == null || str5.isEmpty()) {
                        NotificationFeedHandler.handleString("§c[API] No API key set to test!");
                        return;
                    }

                    NotificationFeedHandler.handleString("§e[API] Testing API key...");
                    if (ApiTxtHelper.checkState3()) {
                        NotificationFeedHandler.handleString("§a[API] API key is valid and working!");
                    } else {
                        NotificationFeedHandler.handleString("§c[API] API key test failed! Key may be invalid or expired.");
                    }
                    break;
                default:
                    NotificationFeedHandler.handleString("§c[API] Unknown action! Use: set, remove, get, or test");
            }
        }
    }

    private boolean matchesString(String str) {
        return str != null && str.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
    }
}
