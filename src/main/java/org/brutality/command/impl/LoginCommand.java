package org.brutality.command.impl;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.util.Session;
import org.brutality.command.Command;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.MessageUtils;

public class LoginCommand extends Command {
    public LoginCommand() {
        super("Login", "To login to a microsoft account", ".login", "login");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length == 0) {
            MessageUtils.x("§cInvalid command usage! Please use §e.login EMAIL:PASSWORD");
        } else if (args.length == 1) {
            String[] parts = args[0].split(":");
            if (parts.length == 2) {
                MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator();

                try {
                    MicrosoftAuthResult microsoftAuthResult = microsoftAuthenticator.loginWithCredentials(args[0].split(":")[0], args[0].split(":")[1]);
                    GetCurrentScreenHelper.mc.session = new Session(microsoftAuthResult.getProfile().getName(), microsoftAuthResult.getProfile().getId(), microsoftAuthResult.getAccessToken(), "microsoft");
                    MessageUtils.x("Logged in as " + GetCurrentScreenHelper.mc.session.getUsername());
                } catch (MicrosoftAuthenticationException e) {
                    e.printStackTrace();
                }
            } else {
                GetCurrentScreenHelper.mc.session = new Session(args[0], "", "", "microsoft");
            }
        }
    }
}
