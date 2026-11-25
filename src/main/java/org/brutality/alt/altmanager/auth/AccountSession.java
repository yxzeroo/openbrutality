package org.brutality.alt.altmanager.auth;

public class AccountSession {
    private String refreshToken2;
    private String accessToken2;
    private String username2;
    private long timestamp;

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public AccountSession(String refreshToken2, String accessToken2, String username2, long timestamp) {
        this.accessToken2 = accessToken2;
        this.refreshToken2 = refreshToken2;
        this.username2 = username2;
        this.timestamp = timestamp;
    }

    public AccountSession(String str, String str2) {
        this("", "", str, System.currentTimeMillis());
    }

    public String getAccessToken() {
        return this.accessToken2;
    }

    public void setUsername(String username2) {
        this.username2 = username2;
    }

    public void setRefreshToken2(String refreshToken2) {
        this.refreshToken2 = refreshToken2;
    }

    public void setAccessToken(String accessToken2) {
        this.accessToken2 = accessToken2;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getUsername() {
        return this.username2;
    }

    public String getRefreshToken() {
        return this.refreshToken2;
    }
}
