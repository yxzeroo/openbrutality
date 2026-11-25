package org.brutality.alt.altmanager.auth;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import net.minecraft.util.Session;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public final class XboxLiveAuthHelper {
    private static final CloseableHttpClient closeableHttpClient = HttpClients.custom()
        .disableAuthCaching()
        .disableCookieManagement()
        .disableRedirectHandling()
        .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000).setSocketTimeout(15000).build())
        .build();
    private static final Gson gson = new Gson();
    private static final long timestamp = 1000L;

    private static String computeString2(String str, String str2) {
        try {
            HttpPost httpPost = new HttpPost("https://api.minecraftservices.com/authentication/login_with_xbox");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity("{ \"identityToken\": \"XBL3.0 x=" + str + ";" + str2 + "\", \"ensureLegacyEnabled\": true }"));
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            String str3 = EntityUtils.toString(closeableHttpResponse.getEntity());
            closeableHttpResponse.close();
            if (closeableHttpResponse.getStatusLine().getStatusCode() == 429) {
                return null;
            }

            JsonObject asJsonObject = new Gson().fromJson(str3, JsonElement.class).getAsJsonObject();
            if (asJsonObject.has("access_token")) {
                return asJsonObject.get("access_token").getAsString();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static boolean matchesString(String str) {
        try {
            HttpGet httpGet = new HttpGet("https://api.minecraftservices.com/entitlements/license?requestId=checker");
            httpGet.setHeader("Authorization", "Bearer " + str);
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            String str2 = EntityUtils.toString(closeableHttpResponse.getEntity());
            closeableHttpResponse.close();
            JsonObject asJsonObject = new Gson().fromJson(str2, JsonElement.class).getAsJsonObject();

            for (JsonElement element : asJsonObject.get("items").getAsJsonArray()) {
                String asString = element.getAsJsonObject().get("source").getAsString();
                if (asString.equals("PURCHASE") || asString.equals("MC_PURCHASE")) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    private static String computeString(String str, String str2) {
        if (str == null) {
            return null;
        }
        try {
            HttpGet httpGet = new HttpGet(str);
            httpGet.setHeader("Cookie", str2);
            CloseableHttpResponse closeableHttpResponse = computeCloseableHttpResponse(httpGet);
            String str3 = computeString(closeableHttpResponse, "Location");
            if (matchesCloseableHttpResponse(closeableHttpResponse) && str3 != null && str3.contains("code=")) {
                return computeString2(str3);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static XboxLiveAuthHelper.Entry computeEntry(String str, boolean flag) {
        if (str != null && !str.trim().isEmpty()) {
            try {
                String str2 = computeString(str);
                if (str2 == null) {
                    return XboxLiveAuthHelper.Entry.computeEntry("Cookies are expired or the file format is unsupported");
                }
                JsonElement element = computeJsonElement(str2);
                JsonObject json = findRelyingParty(element);
                if (json == null) {
                    throw new IllegalArgumentException("Minecraft relying party missing");
                }
                String asString = json.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString();
                String asString2 = json.get("Token").getAsString();
                String str3 = computeString2(asString, asString2);
                if (str3 == null) {
                    return XboxLiveAuthHelper.Entry.computeEntry("Minecraft token request failed");
                }
                if (!matchesString(str3)) {
                    return XboxLiveAuthHelper.Entry.computeEntry("This account does not own Minecraft");
                }
                String[] parts = computeStringArray(str3);
                if (parts == null) {
                    return XboxLiveAuthHelper.Entry.computeEntry("Minecraft profile request failed");
                }
                if (flag) {
                    SessionHolder.setSession(new Session(parts[0], parts[1], str3, "mojang"));
                }

                return XboxLiveAuthHelper.Entry.computeEntry(parts[0], parts[1], str3);
            } catch (IOException e) {
                return XboxLiveAuthHelper.Entry.computeEntry("Could not reach Microsoft. Check your connection and retry.");
            } catch (Exception e2) {
                return XboxLiveAuthHelper.Entry.computeEntry("Microsoft returned an unexpected cookie login response");
            }
        } else {
            return XboxLiveAuthHelper.Entry.computeEntry("No cookies given");
        }
    }

    private static JsonObject findRelyingParty(JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            if (element.isJsonObject()) {
                JsonObject asJsonObject = element.getAsJsonObject();
                if (asJsonObject.has("rp://api.minecraftservices.com/") && asJsonObject.get("rp://api.minecraftservices.com/").isJsonObject()) {
                    return asJsonObject.getAsJsonObject("rp://api.minecraftservices.com/");
                }

                if (asJsonObject.has("Token") && asJsonObject.has("DisplayClaims")) {
                    return asJsonObject;
                }

                for (JsonElement element2 : asJsonObject.entrySet().stream().map(Map.Entry::getValue).toArray(JsonElement[]::new)) {
                    JsonObject json = findRelyingParty(element2);
                    if (json != null) {
                        return json;
                    }
                }
            } else if (element.isJsonArray()) {
                for (JsonElement element3 : element.getAsJsonArray()) {
                    JsonObject json2 = findRelyingParty(element3);
                    if (json2 != null) {
                        return json2;
                    }
                }
            }

            return null;
        }
        return null;
    }

    private static String computeString(CloseableHttpResponse closeableHttpResponse, String str) {
        return closeableHttpResponse.getFirstHeader(str) == null ? null : closeableHttpResponse.getFirstHeader(str).getValue();
    }

    public static XboxLiveAuthHelper.Entry computeEntry2(String str) {
        return computeEntry(str, true);
    }

    public static String computeString5(String str) {
        if (str == null) {
            return null;
        }
        if (str.startsWith("\ufeff")) {
            str = str.substring(1);
        }

        String str2 = str.trim();
        if (str2.startsWith("[") || str2.startsWith("{")) {
            try {
                JsonElement element = new JsonParser().parse(str2);
                JsonArray jsonArray = element.isJsonArray() ? element.getAsJsonArray() : element.getAsJsonObject().getAsJsonArray("cookies");
                if (jsonArray != null) {
                    StringBuilder sb = new StringBuilder();

                    for (JsonElement element2 : jsonArray) {
                        if (element2.isJsonObject()) {
                            JsonObject asJsonObject = element2.getAsJsonObject();
                            String str3 = asJsonObject.has("domain") ? asJsonObject.get("domain").getAsString() : "";
                            String str4 = asJsonObject.has("name") ? asJsonObject.get("name").getAsString() : "";
                            String str5 = asJsonObject.has("value") ? asJsonObject.get("value").getAsString() : "";
                            if (matchesString2(str3) && !str4.isEmpty() && !str5.isEmpty()) {
                                sb.append(str4).append('=').append(str5).append("; ");
                            }
                        }
                    }

                    if (sb.length() >= 2) {
                        return sb.substring(0, sb.length() - 2);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if (!str2.contains("\n") && str2.contains("=")) {
            return str2;
        }
        StringBuilder sb2 = new StringBuilder();

        for (String str6 : str.split("\n")) {
            String str7 = str6.trim();
            if (str7.startsWith("#HttpOnly_")) {
                str7 = str7.substring("#HttpOnly_".length());
            }

            if (!str7.isEmpty() && !str7.startsWith("#")) {
                String[] parts = str7.split("\\t", -1);
                if (parts.length < 7) {
                    parts = str7.split("\\s+", 7);
                }

                if (parts.length >= 7) {
                    String str8 = parts[0];
                    if (matchesString2(str8)) {
                        try {
                            long l = Long.parseLong(parts[4]);
                            if (l > 0L && l < System.currentTimeMillis() / timestamp) {
                                continue;
                            }
                        } catch (NumberFormatException ignored2) {
                        }

                        String str9 = parts[5];
                        String str10 = parts[6];
                        if (!str9.isEmpty() && !str10.isEmpty()) {
                            sb2.append(str9).append("=").append(str10).append("; ");
                        }
                    }
                }
            }
        }

        return sb2.length() < 2 ? null : sb2.substring(0, sb2.length() - 2);
    }

    private static String computeString7(String str) {
        try {
            HttpGet httpGet = new HttpGet(
                "https://sisu.xboxlive.com/connect/XboxLive/?state=login&cobrandId=8058f65d-ce06-4c30-9559-473c9275a65d&tid=896928775&ru=https://www.minecraft.net/en-us/login&aid=1142970254"
            );
            httpGet.setHeader("Cookie", str);
            CloseableHttpResponse closeableHttpResponse = computeCloseableHttpResponse(httpGet);
            String str2 = computeString(closeableHttpResponse, "Location");
            if (matchesCloseableHttpResponse(closeableHttpResponse) && str2 != null && str2.contains("oauth20_authorize.srf")) {
                return computeString2(str2);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static boolean matchesCloseableHttpResponse(CloseableHttpResponse closeableHttpResponse) {
        int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
        return statusCode == 301 || statusCode == 302 || statusCode == 303 || statusCode == 307 || statusCode == 308;
    }

    private static CloseableHttpResponse computeCloseableHttpResponse(HttpGet httpGet) throws IOException {
        return closeableHttpClient.execute(httpGet);
    }

    private static JsonElement computeJsonElement(String str) {
        String str2 = str.trim();
        int n = str2.length() % 4;
        if (n != 0) {
            str2 = str2 + "====".substring(n);
        }

        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(str2);
        } catch (IllegalArgumentException e) {
            bytes = Base64.getUrlDecoder().decode(str2);
        }

        return gson.fromJson(new String(bytes, StandardCharsets.UTF_8), JsonElement.class);
    }

    private XboxLiveAuthHelper() {
    }

    public static String computeString6(String str) throws Exception {
        return String.join("\n", Files.readAllLines(Paths.get(str)));
    }

    private static boolean matchesString2(String str) {
        if (str == null) {
            return false;
        }
        String str2 = str.toLowerCase(Locale.ROOT);

        for (String str3 : new String[]{
            "live.com", "microsoft.com", "microsoftonline.com", "minecraft.net", "minecraftservices.com", "xbox.com", "xboxlive.com", "mojang.com"
        }) {
            if (str2.equals(str3) || str2.endsWith("." + str3)) {
                return true;
            }
        }

        return false;
    }

    private static String[] computeStringArray(String str) {
        try {
            HttpGet httpGet = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
            httpGet.addHeader("Authorization", "Bearer " + str);
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            String str2 = EntityUtils.toString(closeableHttpResponse.getEntity());
            closeableHttpResponse.close();
            JsonObject asJsonObject = new Gson().fromJson(str2, JsonElement.class).getAsJsonObject();
            if (asJsonObject.has("id") && asJsonObject.has("name")) {
                return new String[]{asJsonObject.get("name").getAsString(), asJsonObject.get("id").getAsString()};
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static String computeString3(String str, String str2) {
        try {
            HttpGet httpGet = new HttpGet(str);
            httpGet.setHeader("Cookie", str2);
            CloseableHttpResponse closeableHttpResponse = computeCloseableHttpResponse(httpGet);
            String str3 = computeString(closeableHttpResponse, "Location");
            String str4 = computeString4(str3);
            if (str4 != null) {
                return str4;
            }

            str4 = computeString4(EntityUtils.toString(closeableHttpResponse.getEntity()));
            if (str4 != null) {
                return str4;
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static String computeString(String str) throws IOException {
        String str2 = "https://sisu.xboxlive.com/connect/XboxLive/?state=login&cobrandId=8058f65d-ce06-4c30-9559-473c9275a65d&tid=896928775&ru=https://www.minecraft.net/en-us/login&aid=1142970254";

        for (int i = 0; i < 15; i++) {
            URI uri = URI.create(str2);
            if (!"https".equalsIgnoreCase(uri.getScheme()) || !matchesString2(uri.getHost())) {
                return null;
            }

            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("Cookie", str);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120 Safari/537.36");

            try (CloseableHttpResponse closeableHttpResponse = computeCloseableHttpResponse(httpGet)) {
                String str3 = computeString(closeableHttpResponse, "Location");
                String str4 = computeString4(str3);
                if (str4 == null && closeableHttpResponse.getEntity() != null) {
                    str4 = computeString4(EntityUtils.toString(closeableHttpResponse.getEntity()));
                }

                if (str4 != null) {
                    return str4;
                }

                if (!matchesCloseableHttpResponse(closeableHttpResponse) || str3 == null || str3.trim().isEmpty()) {
                    return null;
                }

                str2 = uri.resolve(computeString2(str3)).toString();
            }
        }

        return null;
    }

    private static String computeString4(String str) {
        if (str == null) {
            return null;
        }
        String str2 = str.replace("&amp;", "&");
        int n = str2.indexOf("accessToken=");
        if (n < 0) {
            try {
                String str3 = URLDecoder.decode(str2, StandardCharsets.UTF_8.name());
                if (!str3.equals(str2)) {
                    return computeString4(str3);
                }
            } catch (Exception ignored) {
            }

            return null;
        }
        int n2 = n + "accessToken=".length();

        int n3;
        for (n3 = n2; n3 < str2.length(); n3++) {
            char ch = str2.charAt(n3);
            if (ch == '&' || ch == '#' || ch == '"' || ch == '\'' || ch == '<' || ch == '>' || Character.isWhitespace(ch)) {
                break;
            }
        }

        if (n3 == n2) {
            return null;
        }
        try {
            return URLDecoder.decode(str2.substring(n2, n3).replace("+", "%2B"), StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return str2.substring(n2, n3);
        }
    }

    public static XboxLiveAuthHelper.Entry computeEntry(String str) {
        return computeEntry(str, false);
    }

    public static String computeString3(String str) throws Exception {
        return computeString5(computeString6(str));
    }

    private static String computeString2(String str) {
        return str.replaceAll(" ", "%20");
    }

    public static class Entry {
        public boolean flag;
        public String username;
        public String text;
        public String text2;
        public String error;

        public static XboxLiveAuthHelper.Entry computeEntry(String str) {
            XboxLiveAuthHelper.Entry entry = new XboxLiveAuthHelper.Entry();
            entry.flag = false;
            entry.error = str;
            return entry;
        }

        public static XboxLiveAuthHelper.Entry computeEntry(String str, String str2, String str3) {
            XboxLiveAuthHelper.Entry entry = new XboxLiveAuthHelper.Entry();
            entry.flag = true;
            entry.username = str;
            entry.text = str2;
            entry.text2 = str3;
            return entry;
        }
    }
}
