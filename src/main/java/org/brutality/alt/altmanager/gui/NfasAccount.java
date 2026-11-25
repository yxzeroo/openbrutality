package org.brutality.alt.altmanager.gui;

import org.brutality.alt.altmanager.auth.AccountSession;

public class NfasAccount {
    private String username2;
    private String text;
    private String accessToken2;
    private NfasAccount.CrackedMode crackedMode;
    private long timestamp;

    public NfasAccount(String username2, String text, String accessToken2, NfasAccount.CrackedMode crackedMode) {
        this.username2 = username2;
        this.text = text;
        this.accessToken2 = accessToken2;
        this.crackedMode = crackedMode;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public NfasAccount(String username2, String accessToken2, NfasAccount.CrackedMode crackedMode) {
        this.username2 = username2;
        this.text = "";
        this.accessToken2 = accessToken2;
        this.crackedMode = crackedMode;
        this.timestamp = System.currentTimeMillis();
    }

    public void setUsername(String username2) {
        this.username2 = username2;
    }

    public String getText() {
        return this.text;
    }

    public void setAccessToken(String accessToken2) {
        this.accessToken2 = accessToken2;
    }

    public void setCrackedMode(NfasAccount.CrackedMode crackedMode) {
        this.crackedMode = crackedMode;
    }

    public String getAccessToken() {
        return this.accessToken2;
    }

    public NfasAccount.CrackedMode getCrackedMode() {
        return this.crackedMode;
    }

    public NfasAccount(AccountSession accountSession, NfasAccount.CrackedMode crackedMode) {
        this.username2 = accountSession.getUsername();
        this.text = "";
        this.accessToken2 = accountSession.getAccessToken();
        this.crackedMode = crackedMode;
        this.timestamp = accountSession.getTimestamp();
    }

    public String getUsername() {
        return this.username2;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static enum CrackedMode {
        CRACKED,
        COOKIE,
        SESSION;

        private static NfasAccount.CrackedMode[] computeCrackedModeArray() {
            return new NfasAccount.CrackedMode[]{CRACKED, COOKIE, SESSION};
        }
    }
}
