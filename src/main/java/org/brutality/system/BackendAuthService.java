package org.brutality.system;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.client.Minecraft;
import org.brutality.alt.altmanager.AccountManager;
import org.brutality.alt.altmanager.auth.AccountSession;
import org.brutality.bootstrap.PinnedTlsHttpClient;
import org.brutality.proxy.ProxyManager;
import org.brutality.system.prestige.PrestigeFormatter;
import org.brutality.system.prestige.PrestigeServerResolver;
import org.brutality.system.prestige.PrestigeWebhookResult;
import org.brutality.utils.AntiDecompilerGuard;

public class BackendAuthService {
    public static final File file = new File("Brutality/Auth");
    private static final String text = new File(file, "auth.ini").getPath();
    private static final File file2 = new File(file, "last-server.txt");
    private static volatile String text2 = computeString2();
    private static BackendAuthService instance;
    private final Gson gson = new Gson();
    private final SessionLeaseLock sessionLeaseLock = new SessionLeaseLock();
    public String sessionToken = null;
    public String username = null;
    public Integer integer = null;
    public String password = null;
    private volatile Thread thread = null;

    private void handleString(String str) {
        this.sessionLeaseLock.handlePath(Path.of(System.getProperty("user.home"), "Brutality", "sessions"), str);
    }

    private BackendAuthService() {
        this.init();
    }

    public static void process4() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null || mc.isSingleplayer() || mc.getCurrentServerData() == null) {
                return;
            }

            String serverIp = mc.getCurrentServerData().serverIP;
            if (serverIp == null || serverIp.trim().isEmpty()) {
                return;
            }

            String str = serverIp.trim();
            if (str.equals(text2)) {
                return;
            }

            text2 = str;
            Files.createDirectories(file2.toPath().getParent());
            Files.writeString(file2.toPath(), str, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
    }

    private static String computeString2() {
        try {
            if (file2.isFile()) {
                String str = Files.readString(file2.toPath(), StandardCharsets.UTF_8).trim();
                if (!str.isEmpty()) {
                    return str;
                }
            }
        } catch (Exception ignored) {
        }

        return "unknown";
    }

    private static String computeString5() {
        process4();
        String str = text2;
        return str != null && !str.trim().isEmpty() ? str : "unknown";
    }

    public void init() {
        if (!file.exists()) {
            file.mkdirs();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (this.sessionToken != null && this.username != null) {
                    String str = "unknown";

                    try {
                        AccountSession accountSession = AccountManager.getAccountSession();
                        if (accountSession != null && accountSession.getUsername() != null && !accountSession.getUsername().isEmpty()) {
                            str = accountSession.getUsername();
                        } else {
                            Minecraft mc = Minecraft.getMinecraft();
                            if (mc != null && mc.getSession() != null) {
                                str = mc.getSession().getUsername();
                            }
                        }
                    } catch (Exception ignored) {
                    }

                    JsonObject json = new JsonObject();
                    json.addProperty("minecraft_user", str);
                    json.addProperty("license_key", computeString());
                    json.addProperty("server", computeString5());
                    String str2 = json.toString();
                    String sessionToken = this.sessionToken;
                    this.sessionLeaseLock.handleString(sessionToken, () -> this.computeEntry("POST", "/auth/logout", str2, sessionToken));
                }
            } catch (Exception ignored2) {
            }
        }, "Brutality-ShutdownLogout"));
    }

    public static synchronized BackendAuthService getInstance() {
        if (instance == null) {
            instance = new BackendAuthService();
        }

        return instance;
    }

    private static String computeString() {
        try {
            if ("true".equalsIgnoreCase(System.getProperty("brut.dev"))) {
                return "DEV-MODE";
            }

            LicenseValidator.Entry entry = LicenseValidator.computeEntry();
            if (entry != null && entry.flag && entry.text != null) {
                return entry.text;
            }
        } catch (Exception ignored) {
        }

        return "unknown";
    }

    public void process3() {
        Thread thread = new Thread(() -> {
            try {
                String str = "unknown";

                try {
                    AccountSession accountSession = AccountManager.getAccountSession();
                    if (accountSession != null && accountSession.getUsername() != null && !accountSession.getUsername().isEmpty()) {
                        str = accountSession.getUsername();
                    } else {
                        try {
                            Minecraft mc = Minecraft.getMinecraft();
                            if (mc != null && mc.getSession() != null) {
                                str = mc.getSession().getUsername();
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                } catch (Throwable ignored2) {
                }

                String str2 = "unknown";

                try {
                    str2 = HardwareIdGenerator.computeString();
                } catch (Throwable ignored3) {
                }

                String property = System.getProperty("os.name", "unknown");
                String property2 = System.getProperty("os.version", "unknown");
                String property3 = System.getProperty("os.arch", "unknown");
                String str3 = property + " " + property2 + " (" + property3 + ")";
                String str4 = "2.6";
                JsonObject json = new JsonObject();
                json.addProperty("minecraft_user", str);
                json.addProperty("hwid", str2);
                json.addProperty("os", str3);
                json.addProperty("client_version", str4);
                json.addProperty("license_key", computeString());
                this.computeEntry("POST", "/auth/launch", json.toString());
            } catch (Throwable ignored4) {
            }
        }, "Brutality-LaunchPing");
        thread.setDaemon(true);
        thread.start();
    }

    public BackendAuthService.Entry3 computeEntry32(String key, String username, String password) {
        try {
            String str = HardwareIdGenerator.computeString();
            if (str == null || str.isEmpty()) {
                return new BackendAuthService.Entry3(false, "Hardware ID could not be determined", null);
            }
            if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
                return new BackendAuthService.Entry3(false, "Username must be 3-20 characters, alphanumeric and underscore only", null);
            }
            if (password.length() >= 6 && password.length() <= 100) {
                String str2 = "unknown";

                try {
                    AccountSession accountSession = AccountManager.getAccountSession();
                    if (accountSession != null && accountSession.getUsername() != null && !accountSession.getUsername().isEmpty()) {
                        str2 = accountSession.getUsername();
                    } else {
                        Minecraft mc = Minecraft.getMinecraft();
                        if (mc != null && mc.getSession() != null) {
                            str2 = mc.getSession().getUsername();
                        }
                    }
                } catch (Exception ignored) {
                }

                JsonObject json = new JsonObject();
                json.addProperty("key", key);
                json.addProperty("username", username);
                json.addProperty("password", password);
                json.addProperty("hwid", str);
                json.addProperty("minecraft_user", str2);
                json.addProperty("os", System.getProperty("os.name", "unknown") + " " + System.getProperty("os.version", ""));
                json.addProperty("license_key", computeString());
                BackendAuthService.Entry entry = this.computeEntry("POST", "/auth/register", json.toString());
                if (entry.success) {
                    JsonObject asJsonObject = new JsonParser().parse(entry.body).getAsJsonObject();
                    String str3 = asJsonObject.has("message") ? asJsonObject.get("message").getAsString() : "Registration successful";
                    return new BackendAuthService.Entry3(true, str3, null);
                }
                return new BackendAuthService.Entry3(false, entry.errorMessage, null);
            }
            return new BackendAuthService.Entry3(false, "Password must be 6-100 characters long", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BackendAuthService.Entry3(false, "Registration failed: " + e.getMessage(), null);
        }
    }

    public BackendAuthService.Entry3 computeEntry3(String username, String password, boolean rememberMe) {
        try {
            EntitlementState.clear();
            String str = HardwareIdGenerator.computeString();
            if (str != null && !str.isEmpty()) {
                AntiDecompilerGuard.reportFlagToServer(username, str, computeString());
                JsonObject json = new JsonObject();
                json.addProperty("username", username);
                json.addProperty("password", password);
                json.addProperty("hwid", str);
                json.addProperty("remember_me", rememberMe);
                json.addProperty("allow_hwid_reset", true);
                json.addProperty("client_version", "2.6");
                json.addProperty("client_hash", computeString3());

                try {
                    AccountSession accountSession = AccountManager.getAccountSession();
                    String str2 = accountSession != null && accountSession.getUsername() != null && !accountSession.getUsername().isEmpty()
                        ? accountSession.getUsername()
                        : (Minecraft.getMinecraft().getSession() != null ? Minecraft.getMinecraft().getSession().getUsername() : "unknown");
                    json.addProperty("minecraft_user", str2);
                } catch (Exception e) {
                    json.addProperty("minecraft_user", "unknown");
                }

                json.addProperty("os", System.getProperty("os.name", "unknown") + " " + System.getProperty("os.version", ""));
                json.addProperty("license_key", computeString());
                String str3 = LicenseValidator.computeString();
                if (str3 != null) {
                    json.addProperty("download_proof", str3);
                }

                BackendAuthService.Entry entry = this.computeEntry("POST", "/auth/login", json.toString());
                if (!entry.success) {
                    return new BackendAuthService.Entry3(false, entry.errorMessage, null);
                }
                JsonObject asJsonObject = new JsonParser().parse(entry.body).getAsJsonObject();
                if (asJsonObject.has("banned") && asJsonObject.get("banned").getAsBoolean()) {
                    return new BackendAuthService.Entry3(false, "This account has been banned", null);
                }
                String asString = asJsonObject.get("token").getAsString();
                String str4 = asJsonObject.has("message") ? asJsonObject.get("message").getAsString() : "Authentication successful";
                boolean flag = asJsonObject.has("hwid_reset") && asJsonObject.get("hwid_reset").getAsBoolean();
                if (flag) {
                    str4 = "Authentication successful - Hardware ID updated";
                }

                this.handleString(asString);
                this.sessionToken = asString;
                this.username = username;
                this.password = password;
                this.integer = this.computeInteger(asJsonObject, asString);
                this.handleJsonObject(asJsonObject);
                if (rememberMe) {
                    this.handleString2(username, password);
                } else {
                    this.process2();
                }

                return new BackendAuthService.Entry3(true, str4, asString);
            }
            return new BackendAuthService.Entry3(false, "Hardware ID could not be determined", null);
        } catch (Exception e2) {
            e2.printStackTrace();
            return new BackendAuthService.Entry3(false, "Authentication failed: " + e2.getMessage(), null);
        }
    }

    public boolean matchesString(String username, String sessionToken) {
        try {
            if (username != null && sessionToken != null && !username.trim().isEmpty() && !sessionToken.trim().isEmpty()) {
                String str = HardwareIdGenerator.computeString();
                if (str != null && !str.isEmpty()) {
                    JsonObject json = new JsonObject();
                    json.addProperty("hwid", str);
                    json.addProperty("client_version", "2.6");
                    json.addProperty("client_hash", computeString3());
                    BackendAuthService.Entry entry = this.computeEntry2("POST", "/auth/validate", json.toString(), sessionToken);
                    if (!entry.success) {
                        return false;
                    }
                    JsonObject asJsonObject = new JsonParser().parse(entry.body).getAsJsonObject();
                    boolean flag = asJsonObject.has("valid") && asJsonObject.get("valid").getAsBoolean();
                    if (flag) {
                        this.handleString(sessionToken);
                        this.sessionToken = sessionToken;
                        this.username = username;
                        this.integer = this.computeInteger(asJsonObject, sessionToken);
                        this.handleJsonObject(asJsonObject);
                    }

                    return flag;
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean matchesString(String identifier) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("identifier", identifier);
            BackendAuthService.Entry entry = this.computeEntry("POST", "/auth/rate-limit", json.toString());
            if (!entry.success) {
                return false;
            }
            JsonObject asJsonObject = new JsonParser().parse(entry.body).getAsJsonObject();
            return asJsonObject.has("allowed") && asJsonObject.get("allowed").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public BackendAuthService.Entry2 computeEntry2() {
        try {
            File file = new File(text);
            if (!file.exists()) {
                return null;
            }
            Properties properties = new Properties();
            FileReader fileReader = new FileReader(file);

            try {
                properties.load(fileReader);
            } catch (Throwable e) {
                try {
                    fileReader.close();
                } catch (Throwable e2) {
                    e.addSuppressed(e2);
                }

                throw e;
            }

            fileReader.close();
            String property = properties.getProperty("info.username");
            String property2 = properties.getProperty("info.password");
            String property3 = properties.getProperty("info.encrypted", "true");
            if (property == null || property2 == null) {
                return null;
            }
            String str;
            if ("true".equalsIgnoreCase(property3)) {
                try {
                    str = this.computeString2(property2);
                } catch (Exception e3) {
                    this.process2();
                    return null;
                }
            } else {
                str = property2;
            }

            return new BackendAuthService.Entry2(property, str, null);
        } catch (Exception e4) {
            this.process2();
            return null;
        }
    }

    public void handleString(String username, String sessionToken) {
        try {
            if (sessionToken != null && !sessionToken.trim().isEmpty()) {
                String str = "unknown";

                try {
                    AccountSession accountSession = AccountManager.getAccountSession();
                    if (accountSession != null && accountSession.getUsername() != null && !accountSession.getUsername().isEmpty()) {
                        str = accountSession.getUsername();
                    } else {
                        Minecraft mc = Minecraft.getMinecraft();
                        if (mc != null && mc.getSession() != null) {
                            str = mc.getSession().getUsername();
                        }
                    }
                } catch (Exception ignored) {
                }

                String str2 = computeString5();
                JsonObject json = new JsonObject();
                json.addProperty("minecraft_user", str);
                json.addProperty("server", str2);
                json.addProperty("license_key", computeString());
                String str3 = json.toString();
                BackendAuthService.Entry entry = this.computeEntry2("POST", "/auth/logout", str3, sessionToken);
                if (entry.success) {
                }
            }

            this.sessionToken = null;
            this.username = null;
            this.password = null;
            this.integer = null;
            EntitlementState.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BackendAuthService.Entry4 computeEntry4() {
        try {
            if (!this.checkState2()) {
                return new BackendAuthService.Entry4(false, "HWID consistency check failed");
            }
            return !this.checkState()
                ? new BackendAuthService.Entry4(false, "Failed to connect to authentication server")
                : new BackendAuthService.Entry4(true, "Security checks passed");
        } catch (Exception e) {
            e.printStackTrace();
            return new BackendAuthService.Entry4(false, "Security check failed: " + e.getMessage());
        }
    }

    private BackendAuthService.Entry computeEntry(String str, String str2, String str3) {
        return this.computeEntry(str, str2, str3, null);
    }

    private BackendAuthService.Entry computeEntry2(String str, String str2, String str3, String str4) {
        return this.computeEntry(str, str2, str3, str4);
    }

    private BackendAuthService.Entry computeEntry(String str, String str2, String str3, String str4) {
        HttpURLConnection conn = null;

        BackendAuthService.Entry entry;
        try {
            URL url = new URL("https://brutality-backend-production.up.railway.app/api" + str2);
            if (ProxyManager.checkState() && ProxyManager.computeProxy() != null) {
                conn = (HttpURLConnection)url.openConnection(ProxyManager.computeProxy());
            } else {
                conn = (HttpURLConnection)url.openConnection();
            }

            PinnedTlsHttpClient.handleHttpURLConnection(conn);
            conn.setRequestMethod(str);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-API-Key", "brutality_client_api_key_2024_super_secret_32_chars_minimum");
            conn.setRequestProperty("User-Agent", "Brutality-Client/1.0");
            if (str4 != null && !str4.trim().isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + str4);
            }

            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);
            if (str3 != null && !str3.trim().isEmpty()) {
                conn.setDoOutput(true);

                try (OutputStream outputStream = conn.getOutputStream()) {
                    byte[] bytes = str3.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(bytes, 0, bytes.length);
                }
            }

            int responseCode = conn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                String str5 = this.readInputStream(conn.getErrorStream());
                String str6 = this.computeString(str5, responseCode);
                return new BackendAuthService.Entry(false, str5, str6);
            }

            String str7 = this.readInputStream(conn.getInputStream());
            entry = new BackendAuthService.Entry(true, str7, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new BackendAuthService.Entry(false, null, "Network error: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return entry;
    }

    private String readInputStream(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        String str;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();

            String str2;
            while ((str2 = reader.readLine()) != null) {
                sb.append(str2);
            }

            str = sb.toString();
        }

        return str;
    }

    private String computeString(String str, int n) {
        try {
            if (str != null && !str.trim().isEmpty()) {
                JsonObject asJsonObject = new JsonParser().parse(str).getAsJsonObject();
                if (asJsonObject.has("error")) {
                    return asJsonObject.get("error").getAsString();
                }
            }
        } catch (Exception ignored) {
        }

        switch (n) {
            case 400:
                return "Bad request - Invalid input data";
            case 401:
                return "Unauthorized - Invalid credentials";
            case 403:
                return "Forbidden - Access denied";
            case 404:
                return "Service not found";
            case 429:
                return "Too many requests - Please try again later";
            case 500:
                return "Server error - Please try again later";
            default:
                return "Request failed with code: " + n;
        }
    }

    private void handleString2(String str, String str2) {
        try {
            if (!file.exists()) {
                file.mkdirs();
            }

            Properties properties = new Properties();
            properties.setProperty("info.username", str);

            try {
                properties.setProperty("info.password", this.computeString(str2));
                properties.setProperty("info.encrypted", "true");
            } catch (Exception e) {
                properties.setProperty("info.password", str2);
                properties.setProperty("info.encrypted", "false");
            }

            properties.setProperty("info.saved_at", String.valueOf(Instant.now().getEpochSecond()));
            File file2 = new File(text);

            try (FileWriter fileWriter = new FileWriter(file2)) {
                properties.store(fileWriter, "Brutality Saved Credentials");
            }
        } catch (Exception ignored) {
        }
    }

    private void process2() {
        try {
            File file = new File(text);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ignored) {
        }
    }

    private SecretKeySpec computeSecretKeySpec(String str) throws Exception {
        MessageDigest instance = MessageDigest.getInstance("SHA-256");
        byte[] bytes = instance.digest(str.getBytes(StandardCharsets.UTF_8));
        byte[] bytes2 = new byte[16];
        System.arraycopy(bytes, 0, bytes2, 0, 16);
        return new SecretKeySpec(bytes2, "AES");
    }

    private String computeString(String str) throws Exception {
        try {
            SecretKeySpec secretKeySpec = this.computeSecretKeySpec("BrutalityAuth2024SecretKey123!");
            Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
            instance.init(1, secretKeySpec);
            byte[] bytes = instance.doFinal(str.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String computeString2(String str) throws Exception {
        try {
            SecretKeySpec secretKeySpec = this.computeSecretKeySpec("BrutalityAuth2024SecretKey123!");
            Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
            instance.init(2, secretKeySpec);
            byte[] bytes = instance.doFinal(Base64.getDecoder().decode(str));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            try {
                return new String(Base64.getDecoder().decode(str), StandardCharsets.UTF_8);
            } catch (Exception e2) {
                throw new Exception("Unable to decrypt data", e2);
            }
        }
    }

    private boolean checkState3() {
        try {
            String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            File file = new File(path);
            return file.exists();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkState2() {
        try {
            String str = HardwareIdGenerator.computeString();
            return str != null && str.length() > 10;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkState() {
        try {
            BackendAuthService.Entry entry = this.computeEntry("GET", "/health", null);
            if (entry.success) {
                JsonObject asJsonObject = new JsonParser().parse(entry.body).getAsJsonObject();
                String str = asJsonObject.has("status") ? asJsonObject.get("status").getAsString() : "unknown";
                return "ok".equals(str);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public PrestigeFormatter computePrestigeFormatter(String minecraftUsername, int previousPrestige, int newPrestige) {
        if (this.checkState4() && this.sessionToken != null) {
            try {
                JsonObject json = new JsonObject();
                json.addProperty("minecraft_username", minecraftUsername);
                json.addProperty("minecraft_user", minecraftUsername);
                json.addProperty("previous_prestige", previousPrestige);
                json.addProperty("new_prestige", newPrestige);
                json.addProperty("brutality_username", this.username);
                json.addProperty("username", this.username);
                json.addProperty("license_key", computeString());
                BackendAuthService.Entry entry = this.computeEntry2("POST", "/prestige/record", json.toString(), this.sessionToken);
                if (!entry.success) {
                    return PrestigeFormatter.computePrestigeFormatter(entry.errorMessage != null ? entry.errorMessage : "API error");
                }
                JsonObject asJsonObject = new JsonParser().parse(entry.body).getAsJsonObject();
                JsonObject json2 = asJsonObject;
                if (asJsonObject.has("data") && asJsonObject.get("data").isJsonObject()) {
                    json2 = asJsonObject.getAsJsonObject("data");
                }

                if (json2 == asJsonObject && asJsonObject.has("result") && asJsonObject.get("result").isJsonObject()) {
                    json2 = asJsonObject.getAsJsonObject("result");
                }

                int n = newPrestige;
                if (json2.has("total_prestiges")) {
                    n = json2.get("total_prestiges").getAsInt();
                } else if (json2.has("prestige_level")) {
                    n = json2.get("prestige_level").getAsInt();
                } else if (asJsonObject.has("total_prestiges")) {
                    n = asJsonObject.get("total_prestiges").getAsInt();
                } else if (asJsonObject.has("prestige_level")) {
                    n = asJsonObject.get("prestige_level").getAsInt();
                }

                String str = null;

                for (String str2 : new String[]{"discord_id", "discordId", "discord_user_id", "discord", "discordUserId"}) {
                    if (json2.has(str2)) {
                        str = json2.get(str2).getAsString();
                        break;
                    }
                }

                if (str == null) {
                    for (String str3 : new String[]{"discord_id", "discordId", "discord_user_id", "discord", "discordUserId"}) {
                        if (asJsonObject.has(str3)) {
                            str = asJsonObject.get(str3).getAsString();
                            break;
                        }
                    }
                }

                if (str == null && entry.body != null) {
                    try {
                        Matcher matcher = Pattern.compile("\"discord[_]?id\"\\s*:\\s*\"?(\\d{10,})\"?", 2).matcher(entry.body);
                        if (matcher.find()) {
                            str = matcher.group(1);
                        }
                    } catch (Exception ignored) {
                    }
                }

                String username = this.username;
                if (json2.has("brutality_username")) {
                    username = json2.get("brutality_username").getAsString();
                } else if (json2.has("username")) {
                    username = json2.get("username").getAsString();
                } else if (asJsonObject.has("brutality_username")) {
                    username = asJsonObject.get("brutality_username").getAsString();
                } else if (asJsonObject.has("username")) {
                    username = asJsonObject.get("username").getAsString();
                }

                return PrestigeFormatter.computePrestigeFormatter(n, str, username);
            } catch (Exception e) {
                return PrestigeFormatter.computePrestigeFormatter(e.getMessage());
            }
        } else {
            return PrestigeFormatter.computePrestigeFormatter("Not authenticated");
        }
    }

    public void handleString(String minecraftUsername, String minecraftUuid, long deltaMs) {
        if (this.checkState4() && this.sessionToken != null && deltaMs > 0L) {
            if (minecraftUsername != null && !minecraftUsername.trim().isEmpty()) {
                String str = minecraftUsername.trim();
                String sessionToken = this.sessionToken;
                Thread thread = new Thread(() -> {
                    try {
                        JsonObject json = new JsonObject();
                        json.addProperty("minecraft_username", str);
                        json.addProperty("minecraft_user", str);
                        if (minecraftUuid != null && !minecraftUuid.trim().isEmpty()) {
                            json.addProperty("minecraft_uuid", minecraftUuid);
                        }

                        json.addProperty("delta_ms", deltaMs);
                        json.addProperty("brutality_username", this.username);
                        json.addProperty("username", this.username);
                        json.addProperty("license_key", computeString());
                        json.addProperty("last_seen", System.currentTimeMillis());
                        this.computeEntry2("POST", "/playtime/record", json.toString(), sessionToken);
                    } catch (Exception ignored) {
                    }
                }, "Brutality-PlaytimeSync");
                thread.setDaemon(true);
                thread.setPriority(1);
                thread.start();
            }
        }
    }

    public String getSessionToken() {
        return this.sessionToken;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Integer getInteger() {
        return this.integer;
    }

    public Integer getInteger2() {
        return this.integer;
    }

    public String computeString4() {
        if (this.username == null || this.username.isEmpty()) {
            return null;
        }
        return this.integer == null ? this.username : this.username + " #" + this.integer;
    }

    public boolean checkState4() {
        return this.sessionToken != null && this.username != null;
    }

    public void handleString3(String user, String token) {
        this.username = user;
        this.handleString(token);
        this.sessionToken = token;
        if (this.integer == null && token != null) {
            this.integer = this.computeInteger(null, token);
        }
    }

    public boolean matchesInt(int prestige, int previousPrestige, int delta, String minecraftUsername, String serverIp) {
        return this.computePrestigeWebhookResult(prestige, previousPrestige, delta, minecraftUsername, serverIp).checkState();
    }

    public PrestigeWebhookResult computePrestigeWebhookResult(int prestige, int previousPrestige, int delta, String minecraftUsername, String serverIp) {
        if (this.sessionToken != null && !this.sessionToken.isEmpty()) {
            JsonObject json = new JsonObject();
            json.addProperty("prestige", prestige);
            json.addProperty("previous_prestige", Math.max(0, previousPrestige));
            json.addProperty("delta", Math.max(1, delta));
            json.addProperty("minecraft_username", minecraftUsername == null ? "Unknown" : minecraftUsername);
            json.addProperty("server_ip", serverIp == null ? "Unknown" : serverIp);
            json.addProperty("network", PrestigeServerResolver.computeString(serverIp));
            BackendAuthService.Entry entry = this.computeEntry2("POST", "/client/prestige", json.toString(), this.sessionToken);
            return PrestigeWebhookResult.computePrestigeWebhookResult(entry.body);
        }
        return PrestigeWebhookResult.computePrestigeWebhookResult(null);
    }

    public BackendAuthService.Entry3 computeEntry3(String username, String key, String newPassword) {
        if (newPassword != null && newPassword.length() >= 6 && newPassword.length() <= 100) {
            try {
                JsonObject json = new JsonObject();
                json.addProperty("username", username);
                json.addProperty("key", key);
                json.addProperty("new_password", newPassword);
                BackendAuthService.Entry entry = this.computeEntry("POST", "/auth/reset-password", json.toString());
                return new BackendAuthService.Entry3(entry.success, entry.success ? "Password updated. Please log in." : entry.errorMessage, null);
            } catch (Exception e) {
                return new BackendAuthService.Entry3(false, "Password reset failed: " + e.getMessage(), null);
            }
        } else {
            return new BackendAuthService.Entry3(false, "Password must be 6-100 characters long", null);
        }
    }

    private static String computeString3() {
        try {
            URL location = BackendAuthService.class.getProtectionDomain().getCodeSource().getLocation();
            Path path = Path.of(location.toURI());
            if (!Files.isRegularFile(path)) {
                return "DEV-CLASSES";
            }
            MessageDigest instance = MessageDigest.getInstance("SHA-256");

            try (InputStream in = Files.newInputStream(path)) {
                byte[] bytes = new byte[8192];

                int n;
                while ((n = in.read(bytes)) >= 0) {
                    if (n > 0) {
                        instance.update(bytes, 0, n);
                    }
                }
            }

            StringBuilder sb = new StringBuilder(64);

            for (byte b : instance.digest()) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private void handleJsonObject(JsonObject json) {
        try {
            JsonObject json2 = json != null && json.has("user") && json.get("user").isJsonObject() ? json.getAsJsonObject("user") : json;
            JsonObject json3 = json2 != null && json2.has("premium") && json2.get("premium").isJsonObject() ? json2.getAsJsonObject("premium") : null;
            boolean flag = json3 != null && json3.has("active") && json3.get("active").getAsBoolean();
            boolean flag2 = json3 != null && json3.has("lifetime") && json3.get("lifetime").getAsBoolean();
            long l = 0L;
            if (json2 != null && json2.has("expires_in") && !json2.get("expires_in").isJsonNull()) {
                l = json2.get("expires_in").getAsLong();
            }

            long l2 = 0L;
            if (json3 != null && json3.has("expires_at") && !json3.get("expires_at").isJsonNull()) {
                String asString = json3.get("expires_at").getAsString();

                try {
                    l2 = Long.parseLong(asString);
                    if (l2 > 100000000000L) {
                        l2 /= 1000L;
                    }
                } catch (NumberFormatException e) {
                    l2 = Instant.parse(asString).getEpochSecond();
                }
            }

            EntitlementState.handleFlag(true, l, flag, flag2, l2);
        } catch (Exception e2) {
            EntitlementState.handleFlag(true, 0L, false, false, 0L);
        }
    }

    private Integer computeInteger(JsonObject json, String str) {
        Integer integer = this.computeInteger(json);
        if (integer != null) {
            return integer;
        }
        if (json != null && json.has("user") && json.get("user").isJsonObject()) {
            integer = this.computeInteger(json.getAsJsonObject("user"));
            if (integer != null) {
                return integer;
            }
        }

        return this.computeInteger(str);
    }

    private Integer computeInteger(JsonObject json) {
        if (json == null) {
            return null;
        }
        for (String str : new String[]{"buyer_count", "buyerCount", "customer_id", "customerId"}) {
            if (json.has(str) && !json.get(str).isJsonNull()) {
                try {
                    if (json.get(str).isJsonPrimitive() && json.get(str).getAsJsonPrimitive().isNumber()) {
                        return json.get(str).getAsInt();
                    }

                    String str2 = json.get(str).getAsString().trim();
                    if (!str2.isEmpty() && str2.matches("\\d{1,6}")) {
                        return Integer.parseInt(str2);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return null;
    }

    private Integer computeInteger(String str) {
        if (str != null && str.contains(".")) {
            try {
                String[] parts = str.split("\\.");
                if (parts.length < 2) {
                    return null;
                }
                String str2 = parts[1];
                int n = (4 - str2.length() % 4) % 4;
                StringBuilder sb = new StringBuilder(str2);

                for (int i = 0; i < n; i++) {
                    sb.append('=');
                }

                String str3 = new String(Base64.getUrlDecoder().decode(sb.toString()), StandardCharsets.UTF_8);
                JsonObject asJsonObject = new JsonParser().parse(str3).getAsJsonObject();
                return this.computeInteger(asJsonObject);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void handleString(String token, String hwid, BackendAuthService.Listener callback) {
        if (token != null && hwid != null) {
            this.process();
            this.thread = new Thread(
                () -> {
                    HeartbeatLeasePolicy heartbeatLeasePolicy = new HeartbeatLeasePolicy(System.nanoTime());

                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            Thread.sleep(30000L);
                            HeartbeatLeasePolicy.AcceptedMode acceptedMode = this.computeAcceptedMode(token, hwid);
                            if (Thread.currentThread().isInterrupted()) {
                                return;
                            }

                            if (heartbeatLeasePolicy.matchesAcceptedMode(acceptedMode, System.nanoTime())) {
                                if (callback != null) {
                                    callback.onKicked(
                                        acceptedMode == HeartbeatLeasePolicy.AcceptedMode.REVOKED
                                            ? "Your session is no longer authorized. Sign in again."
                                            : "Authentication service has been unavailable for ten minutes. Reconnect and sign in again."
                                    );
                                }

                                return;
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        } catch (Exception e2) {
                            if (Thread.currentThread().isInterrupted()) {
                                return;
                            }

                            if (heartbeatLeasePolicy.matchesAcceptedMode(HeartbeatLeasePolicy.AcceptedMode.UNAVAILABLE, System.nanoTime())) {
                                if (callback != null) {
                                    callback.onKicked("Authentication service has been unavailable for ten minutes. Reconnect and sign in again.");
                                }

                                return;
                            }
                        }
                    }
                },
                "Brutality-Heartbeat"
            );
            this.thread.setDaemon(true);
            this.thread.setPriority(1);
            this.thread.start();
        }
    }

    public void process() {
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
    }

    private HeartbeatLeasePolicy.AcceptedMode computeAcceptedMode(String str, String str2) throws Exception {
        HttpURLConnection conn = null;

        HeartbeatLeasePolicy.AcceptedMode acceptedMode;
        try {
            URL url = new URL("https://brutality-backend-production.up.railway.app/api/auth/heartbeat");
            if (ProxyManager.checkState() && ProxyManager.computeProxy() != null) {
                conn = (HttpURLConnection)url.openConnection(ProxyManager.computeProxy());
            } else {
                conn = (HttpURLConnection)url.openConnection();
            }

            PinnedTlsHttpClient.handleHttpURLConnection(conn);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-API-Key", "brutality_client_api_key_2024_super_secret_32_chars_minimum");
            conn.setRequestProperty("Authorization", "Bearer " + str);
            conn.setRequestProperty("User-Agent", "Brutality-Client/1.0");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            String str3 = "{\"hwid\":\"" + str2 + "\",\"client_version\":\"2.6\",\"build\":\"" + JarIntegrityVerifier.computeString() + "\"}";

            try (OutputStream outputStream = conn.getOutputStream()) {
                outputStream.write(str3.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 401 || responseCode == 403) {
                return HeartbeatLeasePolicy.AcceptedMode.REVOKED;
            }

            if (responseCode < 200 || responseCode >= 300) {
                return HeartbeatLeasePolicy.AcceptedMode.UNAVAILABLE;
            }

            String str4 = this.readInputStream(conn.getInputStream());
            JsonObject asJsonObject = new JsonParser().parse(str4).getAsJsonObject();
            if (!asJsonObject.has("kicked") || !asJsonObject.get("kicked").getAsBoolean()) {
                return asJsonObject.has("ok") && asJsonObject.get("ok").getAsBoolean()
                    ? HeartbeatLeasePolicy.AcceptedMode.ACCEPTED
                    : HeartbeatLeasePolicy.AcceptedMode.UNAVAILABLE;
            }

            acceptedMode = HeartbeatLeasePolicy.AcceptedMode.REVOKED;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return acceptedMode;
    }

    public void close() {
        this.process();
    }

    public static class Entry {
        public final boolean success;
        public final String body;
        public final String errorMessage;

        public Entry(boolean success, String body, String errorMessage) {
            this.success = success;
            this.body = body;
            this.errorMessage = errorMessage;
        }
    }

    public static class Entry2 {
        public final String username;
        public final String password;
        public final String sessionToken;

        public Entry2(String username, String sessionToken) {
            this.username = username;
            this.password = null;
            this.sessionToken = sessionToken;
        }

        public Entry2(String username, String password, String sessionToken) {
            this.username = username;
            this.password = password;
            this.sessionToken = sessionToken;
        }
    }

    public static class Entry3 {
        public final boolean success;
        public final String message;
        public final String sessionToken;

        public Entry3(boolean success, String message, String sessionToken) {
            this.success = success;
            this.message = message;
            this.sessionToken = sessionToken;
        }
    }

    public static class Entry4 {
        public final boolean passed;
        public final String message;

        public Entry4(boolean passed, String message) {
            this.passed = passed;
            this.message = message;
        }
    }

    public interface Listener {
        void onKicked(String reason);
    }
}
