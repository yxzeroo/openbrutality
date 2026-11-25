package org.brutality.alt.alt;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public final class AltLoginThread extends Thread {
    private final String text;
    private String status;
    private final String text2;

    public AltLoginThread(String str, String text) {
        super("Alt Login Thread");
        this.text2 = str;
        this.text = text;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private Session computeSession(String str, String str2) {
        try {
            MicrosoftAuthResult microsoftAuthResult = new MicrosoftAuthenticator().loginWithCredentials(str, str2);
            return new Session(microsoftAuthResult.getProfile().getName(), microsoftAuthResult.getProfile().getId(), microsoftAuthResult.getAccessToken(), "microsoft");
        } catch (MicrosoftAuthenticationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void run() {
        if (this.text.isEmpty()) {
            Minecraft.getMinecraft().session = new Session(this.text2, "", "", "mojang");
            this.status = EnumChatFormatting.GREEN + "Logged in. (" + this.text2 + " - offline name)";
        } else {
            this.status = EnumChatFormatting.YELLOW + "Logging in...";
            Session session = this.computeSession(this.text2, this.text);
            if (session == null) {
                this.status = EnumChatFormatting.RED + "Login failed!";
            } else {
                this.status = EnumChatFormatting.GREEN + "Logged in as " + session.getUsername();
                Minecraft.getMinecraft().session = session;
            }
        }
    }
}
