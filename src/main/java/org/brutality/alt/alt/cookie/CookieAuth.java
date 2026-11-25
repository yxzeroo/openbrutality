package org.brutality.alt.alt.cookie;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;
import org.apache.commons.io.IOUtils;
import org.brutality.alt.altmanager.Account;

public enum CookieAuth {
    instance;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36";
    private static final URI SISU = URI.create(
        "https://sisu.xboxlive.com/connect/XboxLive/?state=login&cobrandId=8058f65d-ce06-4c30-9559-473c9275a65d&tid=896928775&ru=https://www.minecraft.net/en-us/login&aid=1142970254"
    );
    private static final String MINECRAFT_SERVICES = "https://api.minecraftservices.com";
    private static final String HTTP_ONLY_PREFIX = "#HttpOnly_";
    private final Gson gson = new Gson();
    private static final long timestamp = 1000L;

    private URI computeURI(URI uri, List<CookieAuth.Ll1Il1Entry> ll1Il1Entries, String str) throws IOException {
        if ("https".equalsIgnoreCase(uri.getScheme()) && matchesString(uri.getHost()) && uri.getUserInfo() == null) {
            HttpsURLConnection httpsURLConnection = null;

            URI uri2;
            try {
                httpsURLConnection = computeHttpsURLConnection(uri);
                httpsURLConnection.setRequestProperty(
                    "Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
                );
                httpsURLConnection.setRequestProperty("Accept-Encoding", "*");
                httpsURLConnection.setRequestProperty("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
                String str2 = uri.getHost().toLowerCase(Locale.ROOT);
                ArrayList list = new ArrayList();

                for (CookieAuth.Ll1Il1Entry ll1Il1Entry : ll1Il1Entries) {
                    if (ll1Il1Entry.matchesString(str2)) {
                        list.add(ll1Il1Entry.II1lIl() + "=" + ll1Il1Entry.oo00o0());
                    }
                }

                if (!list.isEmpty()) {
                    httpsURLConnection.setRequestProperty("Cookie", String.join("; ", list));
                }

                int responseCode = httpsURLConnection.getResponseCode();
                String headerField = httpsURLConnection.getHeaderField("Location");
                if (responseCode < 300 || responseCode >= 400 || headerField == null || headerField.isBlank()) {
                    throw new IOException(str);
                }

                uri2 = uri.resolve(headerField.replace(" ", "%20"));
            } catch (IllegalArgumentException | IOException e) {
                throw new IOException(str);
            } finally {
                if (httpsURLConnection != null) {
                    httpsURLConnection.disconnect();
                }
            }

            return uri2;
        }
        throw new IOException(str);
    }

    private List<CookieAuth.Ll1Il1Entry> computeList(File file) throws IOException {
        List list;
        try {
            list = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IOException("Could not read the cookie file");
        }

        ArrayList list2 = new ArrayList();
        long l = System.currentTimeMillis() / timestamp;
        Iterator it = list.iterator();

        while (true) {
            String[] parts;
            String str;
            while (true) {
                if (!it.hasNext()) {
                    if (list2.isEmpty()) {
                        throw new IOException("No valid Microsoft or Xbox cookies found in the Netscape file");
                    }

                    return list2;
                }

                String str2 = (String)it.next();
                if (str2.startsWith("\ufeff")) {
                    str2 = str2.substring(1);
                }

                String str3 = str2.trim();
                if (str3.startsWith("#HttpOnly_")) {
                    str3 = str3.substring("#HttpOnly_".length());
                }

                if (!str3.isEmpty() && !str3.startsWith("#")) {
                    parts = str3.split("\\t", -1);
                    if (parts.length >= 7) {
                        str = parts[0].trim().toLowerCase(Locale.ROOT);
                        if (str.startsWith(".")) {
                            str = str.substring(1);
                        }

                        if (matchesString(str)) {
                            try {
                                long l2 = Long.parseLong(parts[4].trim());
                                if (l2 <= 0L || l2 >= l) {
                                    break;
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
            }

            String str4 = parts[5].trim();
            String str5 = parts[6].trim();
            if (!str4.isEmpty() && !str5.isEmpty()) {
                list2.add(new CookieAuth.Ll1Il1Entry(str, "TRUE".equalsIgnoreCase(parts[1].trim()), str4, str5));
            }
        }
    }

    public Account computeAccount2(File file) throws IOException {
        List list = this.computeList(file);
        URI uri = this.computeURI(SISU, list, "Could not start Xbox sign-in");
        URI uri2 = this.computeURI(uri, list, "Microsoft did not accept the cookie file");
        URI uri3 = this.computeURI(uri2, list, "Xbox did not return an access token");
        String str = this.computeString(uri3);
        JsonObject json = new JsonObject();
        json.addProperty("identityToken", str);
        json.addProperty("ensureLegacyEnabled", true);
        JsonObject json2 = this.computeJsonObject("/authentication/login_with_xbox", null, json, "Minecraft did not accept the Xbox token");
        String str2 = computeString(json2, "access_token", "Minecraft did not return an access token");
        String str3 = "Could not load the Minecraft profile for this account";
        JsonObject json3 = this.computeJsonObject("/minecraft/profile", str2, null, str3);
        return new Account(str2, null, computeString(json3, "id", str3), computeString(json3, "name", str3));
    }

    private static boolean matchesString(String str) {
        if (str == null) {
            return false;
        }
        String str2 = str.toLowerCase(Locale.ROOT);

        for (String str3 : new String[]{
            "live.com", "microsoft.com", "microsoftonline.com", "xbox.com", "xboxlive.com", "minecraft.net", "minecraftservices.com"
        }) {
            if (str2.equals(str3) || str2.endsWith("." + str3)) {
                return true;
            }
        }

        return false;
    }

    private String computeString(URI uri) throws IOException {
        try {
            String[] parts = uri.toString().split("accessToken=", 2);
            String str = URLDecoder.decode(parts[1].split("[&#]", 2)[0].replace("+", "%2B"), StandardCharsets.UTF_8);
            String str2 = new String(Base64.getDecoder().decode(str), StandardCharsets.UTF_8).split(Pattern.quote("\"rp://api.minecraftservices.com/\","), 2)[1];
            String str3 = str2.split(Pattern.quote("\"Token\":\""), 2)[1].split("\"", 2)[0];
            String str4 = str2.split(Pattern.quote("{\"DisplayClaims\":{\"xui\":[{\"uhs\":\""), 2)[1].split("\"", 2)[0];
            if (!str3.isEmpty() && !str4.isEmpty()) {
                return "XBL3.0 x=" + str4 + ";" + str3;
            }
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            throw new IOException("Xbox returned an invalid access token");
        }

        throw new IOException("Xbox returned an invalid access token");
    }

    private static String computeString(JsonObject json, String str, String str2) throws IOException {
        if (json != null && json.has(str) && json.get(str).isJsonPrimitive()) {
            String asString = json.get(str).getAsString();
            if (!asString.isBlank()) {
                return asString;
            }
        }

        throw new IOException(str2);
    }

    private static HttpsURLConnection computeHttpsURLConnection(URI uri) throws IOException {
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection)uri.toURL().openConnection();
        httpsURLConnection.setInstanceFollowRedirects(false);
        httpsURLConnection.setConnectTimeout(10000);
        httpsURLConnection.setReadTimeout(15000);
        httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
        return httpsURLConnection;
    }

    public Account computeAccount(File file) {
        try {
            return this.computeAccount2(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Cookie Login", 0);
            return null;
        }
    }

    private static CookieAuth[] getInstance() {
        return new CookieAuth[]{instance};
    }

    private JsonObject computeJsonObject(String str, String str2, JsonObject json, String str3) throws IOException {
        HttpsURLConnection httpsURLConnection = null;

        try {
            httpsURLConnection = computeHttpsURLConnection(URI.create("https://api.minecraftservices.com" + str));
            httpsURLConnection.setRequestProperty("Accept", "application/json");
            if (str2 != null) {
                httpsURLConnection.setRequestProperty("Authorization", "Bearer " + str2);
            }

            if (json != null) {
                byte[] bytes = this.gson.toJson((JsonElement)json).getBytes(StandardCharsets.UTF_8);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setRequestProperty("Content-Type", "application/json");
                httpsURLConnection.setFixedLengthStreamingMode(bytes.length);

                try (OutputStream outputStream = httpsURLConnection.getOutputStream()) {
                    outputStream.write(bytes);
                }
            }

            if (httpsURLConnection.getResponseCode() == 200) {
                try (InputStream inputStream = httpsURLConnection.getInputStream()) {
                    return this.gson.fromJson(IOUtils.toString(inputStream, StandardCharsets.UTF_8), JsonObject.class);
                }
            }
        } catch (RuntimeException | IOException e) {
            throw new IOException(str3);
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }

        throw new IOException(str3);
    }

    public record Ll1Il1Entry(String ll1Il1, boolean oo0o, String II1lIl, String oo00o0) {
        public boolean matchesString(String str) {
            return str.equals(this.ll1Il1) || this.oo0o && str.endsWith("." + this.ll1Il1);
        }
    }
}
