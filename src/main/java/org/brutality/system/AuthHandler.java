package org.brutality.system;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class AuthHandler {
    private BackendAuthService backendAuthService;
    private boolean flag = false;
    private String username = null;
    private String token = null;
    private long timestamp = 0L;
    private static final long timestamp2 = 300000L;
    private static final int count = 3;
    private static final int count2 = 2000;

    public AuthHandler() {
        try {
            this.backendAuthService = BackendAuthService.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkState3() {
        try {
            if (this.backendAuthService == null) {
                try {
                    this.backendAuthService = BackendAuthService.getInstance();
                } catch (Exception e) {
                    return false;
                }
            }

            BackendAuthService.Entry4 entry4 = this.backendAuthService.computeEntry4();
            return entry4.passed;
        } catch (Exception e2) {
            return false;
        }
    }

    private boolean matchesString(String str, String str2, int n) {
        int n2 = 2000;

        for (int i = 1; i <= n; i++) {
            try {
                boolean flag = this.backendAuthService.matchesString(str, str2);
                if (flag) {
                    return true;
                }

                return false;
            } catch (Exception e) {
                String str3 = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                boolean flag2 = str3.contains("502")
                    || str3.contains("503")
                    || str3.contains("504")
                    || str3.contains("network error")
                    || str3.contains("connection")
                    || str3.contains("timeout");
                if (flag2 && i < n) {
                    try {
                        Thread.sleep(n2);
                        n2 *= 2;
                    } catch (InterruptedException e2) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else if (!flag2) {
                    return false;
                }
            }
        }

        return false;
    }

    private boolean checkState2() {
        try {
            CompletableFuture future = AuthLoginDialog.authenticate();
            boolean flag = (Boolean)future.get(300L, TimeUnit.SECONDS);
            if (flag) {
                try {
                    Thread.sleep(1000L);
                    BackendAuthService.Entry2 entry2 = this.backendAuthService.computeEntry2();
                    if (entry2 != null) {
                    }

                    if (entry2 != null && entry2.username != null && entry2.sessionToken != null) {
                        this.flag = true;
                        this.username = entry2.username;
                        this.token = entry2.sessionToken;
                        this.timestamp = System.currentTimeMillis();

                        try {
                            String str = HardwareIdGenerator.computeString();
                            RuntimeAuthGuard.handleString(this.username, entry2.password, str);
                        } catch (Exception ignored) {
                        }

                        return true;
                    }

                    if (entry2 != null) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean checkState() {
        if (this.flag && this.username != null && this.token != null) {
            long now = System.currentTimeMillis();
            if (now - this.timestamp < 300000L) {
                return true;
            }
            if (this.backendAuthService == null) {
                this.process();
                return false;
            }
            if (!this.backendAuthService.checkState()) {
                return true;
            }
            boolean flag = this.matchesString(this.username, this.token, 3);
            if (flag) {
                this.timestamp = now;
            } else {
                this.process();
            }

            return flag;
        }
        return false;
    }

    public BackendAuthService.Entry3 computeEntry3(String username, String password, boolean rememberMe) {
        if (this.backendAuthService == null) {
            return new BackendAuthService.Entry3(false, "Authentication system unavailable", null);
        }
        try {
            String str = HardwareIdGenerator.computeString();
            if (str == null || str.isEmpty()) {
                return new BackendAuthService.Entry3(false, "Hardware identification failed", null);
            }
            if (!this.backendAuthService.matchesString(str)) {
                return new BackendAuthService.Entry3(false, "Too many login attempts. Try again later.", null);
            }
            BackendAuthService.Entry3 entry3 = this.backendAuthService.computeEntry3(username, password, rememberMe);
            if (entry3.success) {
                this.flag = true;
                this.username = username;
                this.token = entry3.sessionToken;
                this.timestamp = System.currentTimeMillis();

                try {
                    String str2 = HardwareIdGenerator.computeString();
                    RuntimeAuthGuard.handleString(username, password, str2);
                } catch (Exception ignored) {
                }

                try {
                    String str3 = HardwareIdGenerator.computeString();
                    this.backendAuthService.handleString(entry3.sessionToken, str3, str4 -> {
                        this.process();
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, str4, "Brutality — Session Revoked", 2);
                            System.exit(0);
                        });
                    });
                } catch (Exception ignored2) {
                }
            }

            return entry3;
        } catch (Exception e) {
            e.printStackTrace();
            return new BackendAuthService.Entry3(false, "Login error: " + e.getMessage(), null);
        }
    }

    public BackendAuthService.Entry3 computeEntry3(String key, String username, String password) {
        if (this.backendAuthService == null) {
            return new BackendAuthService.Entry3(false, "Authentication system unavailable", null);
        }
        try {
            String str = HardwareIdGenerator.computeString();
            if (str == null || str.isEmpty()) {
                return new BackendAuthService.Entry3(false, "Hardware identification failed", null);
            }
            return !this.backendAuthService.matchesString(str)
                ? new BackendAuthService.Entry3(false, "Too many registration attempts. Try again later.", null)
                : this.backendAuthService.computeEntry32(key, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return new BackendAuthService.Entry3(false, "Registration error: " + e.getMessage(), null);
        }
    }

    public void process() {
        try {
            if (this.username != null && this.token != null && this.backendAuthService != null) {
                this.backendAuthService.handleString(this.username, this.token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.flag = false;
            this.username = null;
            this.token = null;
            this.timestamp = 0L;
        }
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void handleString(String user, String token) {
        this.flag = true;
        this.username = user;
        this.token = token;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUsername() {
        return this.username;
    }

    public BackendAuthService getBackendAuthService() {
        return this.backendAuthService;
    }

    public void close() {
        try {
            if (this.backendAuthService != null) {
                this.backendAuthService.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
