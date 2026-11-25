package org.brutality.proxy;

public class ProxyConfig {
    private String name2;
    private String ip2;
    private int port2;
    private ProxyConfig.Socks4Mode type2;
    private boolean hasAuth;
    private String username2;
    private String password2;
    private String userID;
    private boolean usedOnBannedConnection;

    public ProxyConfig(String name, String ip, int port) {
        this.name2 = name;
        this.ip2 = ip;
        this.port2 = port;
        this.type2 = ProxyConfig.Socks4Mode.SOCKS5;
        this.hasAuth = false;
        this.username2 = "";
        this.password2 = "";
        this.userID = "";
    }

    public ProxyConfig(String name, String ip, int port, String username, String password) {
        this(name, ip, port);
        this.hasAuth = true;
        this.username2 = username != null ? username : "";
        this.password2 = password != null ? password : "";
    }

    public ProxyConfig(String name, String ip, int port, ProxyConfig.Socks4Mode type, String username, String password, String userID) {
        this.name2 = name;
        this.ip2 = ip;
        this.port2 = port;
        this.type2 = type != null ? type : ProxyConfig.Socks4Mode.SOCKS5;
        this.userID = userID != null ? userID : "";
        boolean flag = this.type2 == ProxyConfig.Socks4Mode.SOCKS4;
        this.hasAuth = flag ? !this.userID.isEmpty() : username != null && !username.isEmpty();
        this.username2 = username != null ? username : "";
        this.password2 = password != null ? password : "";
    }

    public String getName() {
        return this.name2;
    }

    public String computeString() {
        return this.ip2 != null ? this.ip2 : "";
    }

    public int getPort() {
        return this.port2;
    }

    public ProxyConfig.Socks4Mode computeSocks4Mode() {
        return this.type2 != null ? this.type2 : ProxyConfig.Socks4Mode.SOCKS5;
    }

    public boolean hasAuth() {
        return this.hasAuth;
    }

    public String getUsername() {
        return this.username2 != null ? this.username2 : "";
    }

    public String getPassword() {
        return this.password2 != null ? this.password2 : "";
    }

    public String computeString2() {
        return this.userID != null ? this.userID : "";
    }

    public boolean isUsedOnBannedConnection() {
        return this.usedOnBannedConnection;
    }

    public void process() {
        this.usedOnBannedConnection = true;
    }

    public void setName(String name) {
        this.name2 = name;
    }

    public void setIp2(String ip) {
        this.ip2 = ip;
    }

    public void setPort2(int port) {
        this.port2 = port;
    }

    public void setType2(ProxyConfig.Socks4Mode type) {
        this.type2 = type;
    }

    public void setHasAuth(boolean hasAuth) {
        this.hasAuth = hasAuth;
    }

    public void setUsername(String username) {
        this.username2 = username != null ? username : "";
    }

    public void setPassword(String password) {
        this.password2 = password != null ? password : "";
    }

    public void setUserID(String userID) {
        this.userID = userID != null ? userID : "";
    }

    public String getDisplayName() {
        return this.name2 + " (" + this.ip2 + ":" + this.port2 + ") [" + this.computeSocks4Mode().name() + "]";
    }

    @Override
    public String toString() {
        return this.getDisplayName();
    }

    public static enum Socks4Mode {
        SOCKS4,
        SOCKS5;

        private static ProxyConfig.Socks4Mode[] computeSocks4ModeArray() {
            return new ProxyConfig.Socks4Mode[]{SOCKS4, SOCKS5};
        }
    }
}
