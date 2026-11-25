package org.brutality.system;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import net.minecraft.client.Minecraft;
import org.brutality.bootstrap.PinnedTlsHttpClient;

public class LicenseValidator {
    private static final boolean flag = "true".equalsIgnoreCase(System.getProperty("brut.dev"));

    public static LicenseValidator.Entry validateOrCrash() {
        if (flag) {
            return LicenseValidator.Entry.computeEntry("DEV-KEY", "Developer", "dev", null, 0L, "dev");
        }
        LicenseValidator.Entry entry = computeEntry();
        if (!entry.flag) {
            Thread thread = new Thread(() -> g(entry.text5), "LicenseFailWebhook");
            thread.setDaemon(true);
            thread.start();
            showFatalLicenseDialog(entry.text5);
            System.exit(1);
        }

        return entry;
    }

    private static void showFatalLicenseDialog(String str) {
        try {
            boolean flag = str != null && str.toLowerCase().contains("expired");
            String str2 = flag ? "Brutality — License Expired" : "Brutality — License Error";
            String str3;
            if (flag) {
                str3 = "Your Brutality license has expired.\n\nPlease renew your license from the portal to continue using the client.\n\n"
                    + (str != null && !str.isEmpty() ? "(" + str + ")\n\n" : "")
                    + "Click OK to exit.";
            } else {
                str3 = (str != null && !str.isEmpty() ? str : "License validation failed.") + "\n\nClick OK to exit.";
            }

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            if (SwingUtilities.isEventDispatchThread()) {
                JOptionPane.showMessageDialog(null, str3, str2, 0);
            } else {
                try {
                    SwingUtilities.invokeAndWait(() -> JOptionPane.showMessageDialog(null, str3, str2, 0));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, str3, str2, 0);
                }
            }
        } catch (Throwable ignored2) {
        }
    }

    public static LicenseValidator.Entry computeEntry() {
        if (flag) {
            return LicenseValidator.Entry.computeEntry("DEV-KEY", "Developer", "dev", null, 0L, "dev");
        }
        try {
            File file = computeFile();
            if (file != null && file.exists()) {
                String str = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                return computeEntry(str);
            }
            return LicenseValidator.Entry.computeEntry("License file not found. Please re-download from the portal.");
        } catch (IOException e) {
            return LicenseValidator.Entry.computeEntry("Failed to read license file: " + e.getMessage());
        } catch (Exception e2) {
            return LicenseValidator.Entry.computeEntry("License validation failed: " + e2.getMessage());
        }
    }

    public static LicenseValidator.Entry computeEntry(String jsonContent) {
        try {
            JsonObject asJsonObject = new JsonParser().parse(jsonContent).getAsJsonObject();
            if (asJsonObject.has("license") && asJsonObject.has("signature")) {
                JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("license");
                String asString = asJsonObject.get("signature").getAsString();
                String str = computeString(asJsonObject2);
                if (str == null) {
                    return LicenseValidator.Entry.computeEntry("Malformed license file — missing required fields.");
                }
                boolean flag = matchesString(str, asString);
                if (!flag) {
                    return LicenseValidator.Entry.computeEntry("Invalid license signature. File may be tampered.");
                }
                String str2 = asJsonObject2.has("key") ? asJsonObject2.get("key").getAsString() : null;
                String str3 = asJsonObject2.has("username") && !asJsonObject2.get("username").isJsonNull() ? asJsonObject2.get("username").getAsString() : null;
                String str4 = asJsonObject2.has("plan") ? asJsonObject2.get("plan").getAsString() : "premium";
                Long val = asJsonObject2.has("expires") && !asJsonObject2.get("expires").isJsonNull() ? asJsonObject2.get("expires").getAsLong() : null;
                long l = asJsonObject2.has("issued_at") ? asJsonObject2.get("issued_at").getAsLong() : 0L;
                String str5 = asJsonObject2.has("build") ? asJsonObject2.get("build").getAsString() : "unknown";
                if (str2 != null && !str2.isEmpty()) {
                    if (val != null && val > 0L) {
                        long l2 = System.currentTimeMillis() / 1000L;
                        if (val < l2) {
                            String str6;
                            try {
                                str6 = new SimpleDateFormat("yyyy-MM-dd").format(new Date(val * 1000L));
                            } catch (Exception e) {
                                str6 = String.valueOf(val);
                            }

                            return LicenseValidator.Entry.computeEntry("License key has expired (expired on " + str6 + ").");
                        }
                    }

                    return LicenseValidator.Entry.computeEntry(str2, str3, str4, val, l, str5);
                }
                return LicenseValidator.Entry.computeEntry("License file contains no key.");
            }
            return LicenseValidator.Entry.computeEntry("Malformed license file.");
        } catch (Exception e2) {
            return LicenseValidator.Entry.computeEntry("Failed to parse license file.");
        }
    }

    public static void reportLicenseFailure(String reason) {
        Thread thread = new Thread(() -> g(reason), "LicenseFailWebhook");
        thread.setDaemon(true);
        thread.start();
    }

    private static void g(String str) {
        try {
            String str2 = "unknown";

            try {
                str2 = HardwareIdGenerator.computeString();
            } catch (Exception ignored) {
            }

            String str3 = "unknown";

            try {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc != null && mc.getSession() != null) {
                    str3 = mc.getSession().getUsername();
                }
            } catch (Exception ignored2) {
            }

            String str4 = System.getProperty("os.name", "unknown")
                + " "
                + System.getProperty("os.version", "")
                + " ("
                + System.getProperty("os.arch", "unknown")
                + ")";
            String str5 = "unknown";

            try {
                str5 = "2.6";
            } catch (Exception ignored3) {
            }

            long l = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
            int n = Runtime.getRuntime().availableProcessors();
            String str6 = "unknown";

            try {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                str6 = screenSize.width + "x" + screenSize.height;
            } catch (Exception ignored4) {
            }

            String id = TimeZone.getDefault().getID();
            String str7 = System.getProperty("user.language", "unknown") + "_" + System.getProperty("user.country", "unknown");
            String str8 = "unknown";
            String str9 = "unknown";
            String str10 = "unknown";
            String str11 = "unknown";
            String str12 = "No";

            try {
                HttpURLConnection conn = (HttpURLConnection)new URL("http://ip-api.com/json/?fields=query,country,city,isp,proxy,hosting").openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(4000);
                conn.setReadTimeout(4000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();

                    String str13;
                    while ((str13 = reader.readLine()) != null) {
                        sb.append(str13);
                    }

                    reader.close();
                    str8 = computeString(sb.toString(), "query");
                    str9 = computeString(sb.toString(), "country");
                    str10 = computeString(sb.toString(), "city");
                    str11 = computeString(sb.toString(), "isp");
                    boolean flag = "true".equals(computeString(sb.toString(), "proxy"));
                    boolean flag2 = "true".equals(computeString(sb.toString(), "hosting"));
                    str12 = !flag && !flag2 ? "No" : "⚠️ Yes";
                }

                conn.disconnect();
            } catch (Exception ignored5) {
            }

            String str14 = str10.equals("unknown") ? "" : " / " + str10;
            String str15 = "**Reason:** `"
                + str
                + "`\n**MC Session:** `"
                + str3
                + "`\n**IP:** `"
                + str8
                + "` | **Country:** "
                + str9
                + str14
                + " | **ISP:** "
                + str11
                + " | **VPN:** "
                + str12
                + "\n**HWID:** `"
                + str2
                + "`\n**OS:** `"
                + str4
                + "`\n**RAM:** `"
                + l
                + " MB` | **CPU:** `"
                + n
                + " cores` | **Screen:** `"
                + str6
                + "`\n**Timezone:** `"
                + id
                + "` | **Language:** `"
                + str7
                + "`\n**Client Version:** `"
                + str5
                + "`\n⚡ *Client shut down before login was attempted.*";
            String str16 = "{\"content\":null,\"embeds\":[{\"title\":\"\ud83d\udeab  No Valid License\",\"description\":"
                + computeString(str15)
                + ",\"color\":16711680,\"footer\":{\"text\":\"Brutality License Monitor\"}}],\"username\":\"Brutality\"}";
            HttpURLConnection conn2 = (HttpURLConnection)new URL("https://brutality-backend-production.up.railway.app/api/client/license-failure")
                .openConnection();
            PinnedTlsHttpClient.handleHttpURLConnection(conn2);
            conn2.setRequestMethod("POST");
            conn2.setDoOutput(true);
            conn2.setConnectTimeout(5000);
            conn2.setReadTimeout(5000);
            conn2.setRequestProperty("Content-Type", "application/json");
            conn2.setRequestProperty("X-API-Key", "brutality_client_api_key_2024_super_secret_32_chars_minimum");
            conn2.setRequestProperty("User-Agent", "Mozilla/5.0");

            try (OutputStream outputStream = conn2.getOutputStream()) {
                outputStream.write(str16.getBytes(StandardCharsets.UTF_8));
            }

            conn2.getResponseCode();
            conn2.disconnect();
        } catch (Exception ignored6) {
        }
    }

    private static String computeString(String str, String str2) {
        try {
            String str3 = "\"" + str2 + "\":";
            int n = str.indexOf(str3);
            if (n == -1) {
                return "unknown";
            }
            int n2 = n + str3.length();

            while (n2 < str.length() && str.charAt(n2) == ' ') {
                n2++;
            }

            if (str.charAt(n2) == '"') {
                int n3 = str.indexOf(34, n2 + 1);
                return n3 == -1 ? "unknown" : str.substring(n2 + 1, n3);
            }
            int n4 = n2;

            while (n4 < str.length() && str.charAt(n4) != ',' && str.charAt(n4) != '}') {
                n4++;
            }

            return str.substring(n2, n4).trim();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static String computeString(JsonObject json) {
        if (json.has("key") && json.has("username") && json.has("plan") && json.has("expires") && json.has("issued_at") && json.has("build")) {
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"key\":").append(computeString(json.get("key").getAsString()));
            sb.append(",\"username\":");
            if (json.get("username").isJsonNull()) {
                sb.append("null");
            } else {
                sb.append(computeString(json.get("username").getAsString()));
            }

            sb.append(",\"plan\":").append(computeString(json.get("plan").getAsString()));
            sb.append(",\"expires\":");
            if (json.get("expires").isJsonNull()) {
                sb.append("null");
            } else {
                sb.append(json.get("expires").getAsLong());
            }

            sb.append(",\"issued_at\":").append(json.get("issued_at").getAsLong());
            sb.append(",\"build\":").append(computeString(json.get("build").getAsString()));
            sb.append("}");
            return sb.toString();
        }
        return null;
    }

    private static String computeString(String str) {
        return str == null
            ? "null"
            : "\"" + str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\"";
    }

    private static boolean matchesString(String str, String str2) {
        try {
            PublicKey publicKey = computePublicKey();
            Signature instance = Signature.getInstance("SHA256withRSA");
            instance.initVerify(publicKey);
            instance.update(str.getBytes(StandardCharsets.UTF_8));

            byte[] bytes;
            try {
                bytes = Base64.getDecoder().decode(str2);
            } catch (IllegalArgumentException e) {
                bytes = Base64.getUrlDecoder().decode(str2);
            }

            return instance.verify(bytes);
        } catch (Exception e2) {
            return false;
        }
    }

    private static PublicKey computePublicKey() throws Exception {
        String str = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuvB9pyhZ3HtywpPSFeuq\n2HzZ+MxBltZv9hLB9zcdNYep71WZiT32rhPW+f2mWh/tE7WQaf3uMr4GXoevPcJu\nN8tcgmGbDmhV8npIUoDULsbEhKg7v41gyr/ihQJjCT+B2gtwOc5ezDDyyJNI8YN3\nHbduiZzR7Y3r5Ls5GYWIzQ1DJuV6fkFLrFaNaU8Jw/kexz9wscCjorMt1t6oZaRf\nScAXfNu1hYXeu6Z4oKuhBZzUMLUIyg9QWaZHhageA4wFFvwNthRuzSsQzzTWC5wN\nAsDJCkIKBKH4hKq+jctTjeoh6dTpKEb7SrGtYs+nC2f3EdFP4yQdAAn+Fdf/XV5i\nzwIDAQAB\n-----END PUBLIC KEY-----"
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        byte[] bytes = Base64.getDecoder().decode(str);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(bytes);
        KeyFactory instance = KeyFactory.getInstance("RSA");
        return instance.generatePublic(x509EncodedKeySpec);
    }

    public static String computeString() {
        try {
            File file = computeFile();
            if (file != null && Files.size(file.toPath()) <= 16384L) {
                JsonObject asJsonObject = new JsonParser().parse(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8)).getAsJsonObject();
                return asJsonObject.has("download") && asJsonObject.has("download_signature") ? asJsonObject.toString() : null;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static File computeFile() {
        try {
            String path = LicenseValidator.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File parentFile = new File(path).getParentFile();
            if (parentFile != null) {
                File file = new File(parentFile, "license.dat");
                if (file.exists()) {
                    return file;
                }
            }
        } catch (Exception ignored) {
        }

        File file2 = new File("license.dat");
        if (file2.exists()) {
            return file2;
        }
        File file3 = new File("Brutality", "license.dat");
        if (file3.exists()) {
            return file3;
        }
        try {
            File mcDataDir = Minecraft.getMinecraft().mcDataDir;
            if (mcDataDir != null) {
                File file4 = new File(mcDataDir, "license.dat");
                if (file4.exists()) {
                    return file4;
                }

                File file5 = new File(mcDataDir, "Brutality" + File.separator + "license.dat");
                if (file5.exists()) {
                    return file5;
                }
            }
        } catch (Exception ignored2) {
        }

        try {
            String property = System.getProperty("user.home", "");
            String str = System.getProperty("os.name", "").toLowerCase();
            File file6 = null;
            if (str.contains("mac") || str.contains("darwin")) {
                file6 = new File(property, "Library/Application Support/minecraft");
            } else if (str.contains("win")) {
                String str2 = System.getenv("APPDATA");
                if (str2 != null) {
                    file6 = new File(str2, ".minecraft");
                }
            } else {
                file6 = new File(property, ".minecraft");
            }

            if (file6 != null) {
                File file7 = new File(file6, "license.dat");
                if (file7.exists()) {
                    return file7;
                }

                File file8 = new File(file6, "Brutality" + File.separator + "license.dat");
                if (file8.exists()) {
                    return file8;
                }

                File file9 = new File(file6, "mods" + File.separator + "license.dat");
                if (file9.exists()) {
                    return file9;
                }
            }
        } catch (Exception ignored3) {
        }

        try {
            String path2 = LicenseValidator.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File parentFile2 = new File(path2).getParentFile();
            if (parentFile2 != null && parentFile2.getParentFile() != null) {
                File file10 = new File(parentFile2.getParentFile(), "license.dat");
                if (file10.exists()) {
                    return file10;
                }
            }
        } catch (Exception ignored4) {
        }

        return null;
    }

    public static class Entry {
        public final boolean flag;
        public final String text;
        public final String text2;
        public final String text3;
        public final Long longValue;
        public final long timestamp;
        public final String text4;
        public final String text5;

        private Entry(boolean flag, String text, String str, String str2, Long longValue, long timestamp, String str3, String str4) {
            this.flag = flag;
            this.text = text;
            this.text2 = str;
            this.text3 = str2;
            this.longValue = longValue;
            this.timestamp = timestamp;
            this.text4 = str3;
            this.text5 = str4;
        }

        public static LicenseValidator.Entry computeEntry(String str, String str2, String str3, Long val, long l, String str4) {
            return new LicenseValidator.Entry(true, str, str2, str3, val, l, str4, null);
        }

        public static LicenseValidator.Entry computeEntry(String str) {
            return new LicenseValidator.Entry(false, null, null, null, null, 0L, null, str);
        }
    }
}
