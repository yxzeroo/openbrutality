package org.brutality.alt.altmanager.auth;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.util.Session;
import net.minecraft.util.Session.Type;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.brutality.alt.altmanager.utils.BrowserLauncher;

public final class MicrosoftAuthenticator {
    public static final RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(30000)
        .setConnectTimeout(30000)
        .setSocketTimeout(30000)
        .build();
    public static final String text = "54fd49e4-2103-4044-9603-2b028c814ec3";
    public static final int count = 59125;

    private static String computeString(String str) {
        String str2 = StringUtils.defaultString(str).trim();

        for (int i = 0; i < 3; i++) {
            String str3 = str2;
            if (str2.length() >= 2 && (str2.startsWith("\"") && str2.endsWith("\"") || str2.startsWith("'") && str2.endsWith("'"))) {
                str2 = str2.substring(1, str2.length() - 1).trim();
            }

            if (str2.regionMatches(true, 0, "Bearer ", 0, 7)) {
                str2 = str2.substring(7).trim();
            }

            if (str2.equals(str3)) {
                break;
            }
        }

        if (str2.startsWith("{") && str2.endsWith("}")) {
            try {
                JsonObject asJsonObject = new JsonParser().parse(str2).getAsJsonObject();
                String[] parts = new String[]{"accessToken", "access_token", "token"};

                for (String str4 : parts) {
                    if (asJsonObject.has(str4) && asJsonObject.get(str4).isJsonPrimitive()) {
                        String asString = asJsonObject.get(str4).getAsString();
                        if (!StringUtils.isBlank(asString)) {
                            return computeString(asString);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return str2;
    }

    public static CompletableFuture<String> computeCompletableFuture(Executor executor) {
        return acquireMSAuthCode(BrowserLauncher::openWebLink, executor);
    }

    public static CompletableFuture<Session> loginComplete(Executor executor) {
        return computeCompletableFuture(executor)
            .thenCompose(str -> acquireMSAccessTokens(str, executor))
            .thenCompose(map -> acquireXboxAccessToken(map.get("access_token"), executor))
            .thenCompose(str2 -> acquireXboxXstsToken(str2, executor))
            .thenCompose(map2 -> acquireMCAccessToken(map2.get("Token"), map2.get("uhs"), executor))
            .thenCompose(str3 -> login(str3, executor))
            .exceptionally(t -> {
                if (t.getCause() != null) {
                    t.getCause().getMessage();
                } else {
                    t.getMessage();
                }

                throw new CompletionException(t);
            });
    }

    public static CompletableFuture<String> acquireXboxAccessTokenLegacy(String str, Executor executor) {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    String str2;
                    try (CloseableHttpClient closeableHttpClient = HttpClients.createMinimal()) {
                        HttpPost httpPost = new HttpPost(URI.create("https://user.auth.xboxlive.com/user/authenticate"));
                        JsonObject json = new JsonObject();
                        JsonObject json2 = new JsonObject();
                        json2.addProperty("AuthMethod", "RPS");
                        json2.addProperty("SiteName", "user.auth.xboxlive.com");
                        json2.addProperty("RpsTicket", String.format("t=%s", str));
                        json.add("Properties", json2);
                        json.addProperty("RelyingParty", "http://auth.xboxlive.com");
                        json.addProperty("TokenType", "JWT");
                        httpPost.setConfig(requestConfig);
                        httpPost.setHeader("Content-Type", "application/json");
                        httpPost.setEntity(new StringEntity(json.toString()));
                        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
                        JsonObject json3 = closeableHttpResponse.getStatusLine().getStatusCode() == 200
                            ? new JsonParser().parse(EntityUtils.toString(closeableHttpResponse.getEntity())).getAsJsonObject()
                            : new JsonObject();
                        str2 = Optional.ofNullable(json3.get("Token"))
                            .map(JsonElement::getAsString)
                            .filter(str3 -> !StringUtils.isBlank(str3))
                            .orElseThrow(() -> new Exception("Invalid Xbox t= response"));
                    }

                    return str2;
                } catch (Exception e) {
                    throw new CompletionException("Unable to acquire Xbox t= token", e);
                }
            },
            executor
        );
    }

    public static CompletableFuture<Map<String, String>> acquireXboxXstsToken(String str, Executor executor) {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    Map map;
                    try (CloseableHttpClient closeableHttpClient = HttpClients.createMinimal()) {
                        HttpPost httpPost = new HttpPost("https://xsts.auth.xboxlive.com/xsts/authorize");
                        JsonObject json = new JsonObject();
                        JsonObject json2 = new JsonObject();
                        JsonArray jsonArray = new JsonArray();
                        jsonArray.add(new JsonPrimitive(str));
                        json2.addProperty("SandboxId", "RETAIL");
                        json2.add("UserTokens", jsonArray);
                        json.add("Properties", json2);
                        json.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
                        json.addProperty("TokenType", "JWT");
                        httpPost.setConfig(requestConfig);
                        httpPost.setHeader("Content-Type", "application/json");
                        httpPost.setEntity(new StringEntity(json.toString()));
                        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
                        JsonObject json3 = computeJsonObject(closeableHttpResponse);
                        Map map2 = Optional.ofNullable(json3.get("Token"))
                            .map(JsonElement::getAsString)
                            .filter(str2 -> !StringUtils.isBlank(str2))
                            .map(
                                str3 -> {
                                    String asString = json3.get("DisplayClaims")
                                        .getAsJsonObject()
                                        .get("xui")
                                        .getAsJsonArray()
                                        .get(0)
                                        .getAsJsonObject()
                                        .get("uhs")
                                        .getAsString();
                                    HashMap map3 = new HashMap();
                                    map3.put("Token", str3);
                                    map3.put("uhs", asString);
                                    return map3;
                                }
                            )
                            .orElseThrow(
                                () -> new Exception(
                                    json3.has("XErr")
                                        ? String.format("%s: %s", json3.get("XErr").getAsString(), json3.get("Message").getAsString())
                                        : "There was no access token or error description present."
                                )
                            );
                        map = map2;
                    }

                    return map;
                } catch (InterruptedException e) {
                    throw new CancellationException("Xbox Live XSTS token acquisition was cancelled!");
                } catch (Exception e2) {
                    throw new CompletionException("Unable to acquire Xbox Live XSTS token!", e2);
                }
            },
            executor
        );
    }

    public static CompletableFuture<MicrosoftAuthenticator.Entry> loginWithRefreshToken(String str, Executor executor) {
        return refreshMSAccessTokens(str, executor)
            .thenComposeAsync(
                map -> {
                    String str2 = map.get("access_token");
                    String str3 = map.get("refresh_token");
                    return acquireXboxAccessToken(str2, executor)
                        .thenComposeAsync(str4 -> acquireXboxXstsToken(str4, executor), executor)
                        .thenComposeAsync(map2 -> acquireMCAccessToken(map2.get("Token"), map2.get("uhs"), executor), executor)
                        .thenComposeAsync(str5 -> login(str5, executor), executor)
                        .thenApply(session -> new MicrosoftAuthenticator.Entry(session, str3));
                },
                executor
            );
    }

    public static CompletableFuture<String> acquireXboxAccessToken(String str, Executor executor) {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    String str2;
                    try (CloseableHttpClient closeableHttpClient = HttpClients.createMinimal()) {
                        HttpPost httpPost = new HttpPost(URI.create("https://user.auth.xboxlive.com/user/authenticate"));
                        JsonObject json = new JsonObject();
                        JsonObject json2 = new JsonObject();
                        json2.addProperty("AuthMethod", "RPS");
                        json2.addProperty("SiteName", "user.auth.xboxlive.com");
                        json2.addProperty("RpsTicket", String.format("d=%s", str));
                        json.add("Properties", json2);
                        json.addProperty("RelyingParty", "http://auth.xboxlive.com");
                        json.addProperty("TokenType", "JWT");
                        httpPost.setConfig(requestConfig);
                        httpPost.setHeader("Content-Type", "application/json");
                        httpPost.setEntity(new StringEntity(json.toString()));
                        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
                        JsonObject json3 = computeJsonObject(closeableHttpResponse);
                        String str3 = Optional.ofNullable(json3.get("Token"))
                            .map(JsonElement::getAsString)
                            .filter(str4 -> !StringUtils.isBlank(str4))
                            .orElseThrow(
                                () -> new Exception(
                                    json3.has("XErr")
                                        ? String.format("%s: %s", json3.get("XErr").getAsString(), json3.get("Message").getAsString())
                                        : "There was no access token or error description present."
                                )
                            );
                        str2 = str3;
                    }

                    return str2;
                } catch (InterruptedException e) {
                    throw new CancellationException("Xbox Live access token acquisition was cancelled!");
                } catch (Exception e2) {
                    throw new CompletionException("Unable to acquire Xbox Live access token!", e2);
                }
            },
            executor
        );
    }

    public static CompletableFuture<String> acquireMCAccessToken(String str, String str2, Executor executor) {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    String str3;
                    try (CloseableHttpClient closeableHttpClient = HttpClients.createMinimal()) {
                        HttpPost httpPost = new HttpPost(URI.create("https://api.minecraftservices.com/authentication/login_with_xbox"));
                        httpPost.setConfig(requestConfig);
                        httpPost.setHeader("Content-Type", "application/json");
                        httpPost.setEntity(new StringEntity(String.format("{\"identityToken\": \"XBL3.0 x=%s;%s\"}", str2, str)));
                        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
                        JsonObject asJsonObject = new JsonParser().parse(EntityUtils.toString(closeableHttpResponse.getEntity())).getAsJsonObject();
                        String str4 = Optional.ofNullable(asJsonObject.get("access_token"))
                            .map(JsonElement::getAsString)
                            .filter(str5 -> !StringUtils.isBlank(str5))
                            .orElseThrow(
                                () -> new Exception(
                                    asJsonObject.has("error")
                                        ? String.format("%s: %s", asJsonObject.get("error").getAsString(), asJsonObject.get("errorMessage").getAsString())
                                        : "There was no access token or error description present."
                                )
                            );
                        str3 = str4;
                    }

                    return str3;
                } catch (InterruptedException e) {
                    throw new CancellationException("Minecraft access token acquisition was cancelled!");
                } catch (Exception e2) {
                    throw new CompletionException("Unable to acquire Minecraft access token!", e2);
                }
            },
            executor
        );
    }

    public static CompletableFuture<String> acquireMSAuthCode(Consumer<URI> callback, Executor executor) {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    String str = RandomStringUtils.randomAlphanumeric(8);
                    HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost", 59125), 0);
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    AtomicReference atomicReference = new AtomicReference(null);
                    AtomicReference atomicReference2 = new AtomicReference(null);
                    httpServer.createContext(
                        "/",
                        httpExchange -> {
                            String query = httpExchange.getRequestURI().getQuery();
                            if (query != null) {
                                Map map = URLEncodedUtils.parse(query, StandardCharsets.UTF_8)
                                    .stream()
                                    .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
                                if (!str.equals(map.get("state"))) {
                                    String str2 = String.format("State mismatch! Expected '%s' but got '%s'.", str, map.get("state"));
                                    atomicReference2.set(str2);
                                } else if (map.containsKey("code")) {
                                    atomicReference.set((String)map.get("code"));
                                } else if (map.containsKey("error")) {
                                    String str3 = String.format("%s: %s", map.get("error"), map.get("error_description"));
                                    atomicReference2.set(str3);
                                }
                            }

                            InputStream resourceAsStream = MicrosoftAuthenticator.class.getResourceAsStream("/callback.html");
                            byte[] bytes = resourceAsStream != null ? IOUtils.toByteArray(resourceAsStream) : new byte[0];
                            httpExchange.getResponseHeaders().add("Content-Type", "text/html");
                            httpExchange.sendResponseHeaders(200, bytes.length);
                            httpExchange.getResponseBody().write(bytes);
                            httpExchange.getResponseBody().close();
                            countDownLatch.countDown();
                        }
                    );
                    URIBuilder uriBuilder = new URIBuilder("https://login.live.com/oauth20_authorize.srf")
                        .addParameter("client_id", "54fd49e4-2103-4044-9603-2b028c814ec3")
                        .addParameter("response_type", "code")
                        .addParameter("scope", "XboxLive.signin XboxLive.offline_access")
                        .addParameter("redirect_uri", String.format("http://localhost:%d", 59125))
                        .addParameter("prompt", "select_account")
                        .addParameter("state", str);
                    URI uri = uriBuilder.build();
                    callback.accept(uri);

                    String str4;
                    try {
                        httpServer.start();
                        countDownLatch.await();
                        str4 = (String)Optional.ofNullable((String)atomicReference.get())
                            .filter(str5 -> !StringUtils.isBlank((CharSequence)str5))
                            .orElseThrow(
                                () -> new Exception(
                                    (String)Optional.ofNullable((String)atomicReference2.get()).orElse("There was no auth code or error description present.")
                                )
                            );
                    } finally {
                        httpServer.stop(2);
                    }

                    return str4;
                } catch (InterruptedException e) {
                    throw new CancellationException("Microsoft auth code acquisition was cancelled!");
                } catch (Exception e2) {
                    throw new CompletionException("Unable to acquire Microsoft auth code!", e2);
                }
            },
            executor
        );
    }

    public static CompletableFuture<Session> loginWithToken(String str, Executor executor) {
        String str2 = computeString(str);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return login(str2, executor).get(30L, TimeUnit.SECONDS);
            } catch (Exception e) {
                try {
                    String str3 = acquireXboxAccessToken(str2, executor).get(30L, TimeUnit.SECONDS);
                    Map map = acquireXboxXstsToken(str3, executor).get(30L, TimeUnit.SECONDS);
                    String str4 = acquireMCAccessToken((String)map.get("Token"), (String)map.get("uhs"), executor).get(30L, TimeUnit.SECONDS);
                    return login(str4, executor).get(30L, TimeUnit.SECONDS);
                } catch (Exception e2) {
                    try {
                        String str5 = acquireXboxAccessTokenLegacy(str2, executor).get(30L, TimeUnit.SECONDS);
                        Map map2 = acquireXboxXstsToken(str5, executor).get(30L, TimeUnit.SECONDS);
                        String str6 = acquireMCAccessToken((String)map2.get("Token"), (String)map2.get("uhs"), executor).get(30L, TimeUnit.SECONDS);
                        return login(str6, executor).get(30L, TimeUnit.SECONDS);
                    } catch (Exception e3) {
                        throw new CompletionException(new Exception("Token was not accepted as a Minecraft or Microsoft token"));
                    }
                }
            }
        }, executor);
    }

    public static CompletableFuture<Map<String, String>> refreshMSAccessTokens(String str, Executor executor) {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    HashMap map;
                    try (CloseableHttpClient closeableHttpClient = HttpClients.createMinimal()) {
                        HttpPost httpPost = new HttpPost(URI.create("https://login.live.com/oauth20_token.srf"));
                        httpPost.setConfig(requestConfig);
                        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                        httpPost.setEntity(
                            new UrlEncodedFormEntity(
                                Arrays.asList(
                                    new BasicNameValuePair("client_id", "54fd49e4-2103-4044-9603-2b028c814ec3"),
                                    new BasicNameValuePair("grant_type", "refresh_token"),
                                    new BasicNameValuePair("refresh_token", str),
                                    new BasicNameValuePair("redirect_uri", String.format("http://localhost:%d", 59125))
                                ),
                                "UTF-8"
                            )
                        );
                        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
                        JsonObject asJsonObject = new JsonParser().parse(EntityUtils.toString(closeableHttpResponse.getEntity())).getAsJsonObject();
                        String str2 = Optional.ofNullable(asJsonObject.get("access_token"))
                            .map(JsonElement::getAsString)
                            .filter(str3 -> !StringUtils.isBlank(str3))
                            .orElseThrow(
                                () -> new Exception(
                                    asJsonObject.has("error")
                                        ? String.format("%s: %s", asJsonObject.get("error").getAsString(), asJsonObject.get("error_description").getAsString())
                                        : "There was no Microsoft access token or error description present."
                                )
                            );
                        String str4 = Optional.ofNullable(asJsonObject.get("refresh_token"))
                            .map(JsonElement::getAsString)
                            .filter(str5 -> !StringUtils.isBlank(str5))
                            .orElseThrow(
                                () -> new Exception(
                                    asJsonObject.has("error")
                                        ? String.format("%s: %s", asJsonObject.get("error").getAsString(), asJsonObject.get("error_description").getAsString())
                                        : "There was no Microsoft refresh token or error description present."
                                )
                            );
                        HashMap map2 = new HashMap();
                        map2.put("access_token", str2);
                        map2.put("refresh_token", str4);
                        map = map2;
                    }

                    return map;
                } catch (InterruptedException e) {
                    throw new CancellationException("Microsoft access tokens acquisition was cancelled!");
                } catch (Exception e2) {
                    throw new CompletionException("Unable to acquire Microsoft access tokens!", e2);
                }
            },
            executor
        );
    }

    public static CompletableFuture<Map<String, String>> acquireMSAccessTokens(String str, Executor executor) {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    HashMap map;
                    try (CloseableHttpClient closeableHttpClient = HttpClients.createMinimal()) {
                        HttpPost httpPost = new HttpPost(URI.create("https://login.live.com/oauth20_token.srf"));
                        httpPost.setConfig(requestConfig);
                        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                        httpPost.setEntity(
                            new UrlEncodedFormEntity(
                                Arrays.asList(
                                    new BasicNameValuePair("client_id", "54fd49e4-2103-4044-9603-2b028c814ec3"),
                                    new BasicNameValuePair("grant_type", "authorization_code"),
                                    new BasicNameValuePair("code", str),
                                    new BasicNameValuePair("redirect_uri", String.format("http://localhost:%d", 59125))
                                ),
                                "UTF-8"
                            )
                        );
                        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
                        JsonObject asJsonObject = new JsonParser().parse(EntityUtils.toString(closeableHttpResponse.getEntity())).getAsJsonObject();
                        String str2 = Optional.ofNullable(asJsonObject.get("access_token"))
                            .map(JsonElement::getAsString)
                            .filter(str3 -> !StringUtils.isBlank(str3))
                            .orElseThrow(
                                () -> new Exception(
                                    asJsonObject.has("error")
                                        ? String.format("%s: %s", asJsonObject.get("error").getAsString(), asJsonObject.get("error_description").getAsString())
                                        : "There was no Microsoft access token or error description present."
                                )
                            );
                        String str4 = Optional.ofNullable(asJsonObject.get("refresh_token"))
                            .map(JsonElement::getAsString)
                            .filter(str5 -> !StringUtils.isBlank(str5))
                            .orElseThrow(
                                () -> new Exception(
                                    asJsonObject.has("error")
                                        ? String.format("%s: %s", asJsonObject.get("error").getAsString(), asJsonObject.get("error_description").getAsString())
                                        : "There was no Microsoft refresh token or error description present."
                                )
                            );
                        HashMap map2 = new HashMap();
                        map2.put("access_token", str2);
                        map2.put("refresh_token", str4);
                        map = map2;
                    }

                    return map;
                } catch (InterruptedException e) {
                    throw new CancellationException("Microsoft access tokens acquisition was cancelled!");
                } catch (Exception e2) {
                    throw new CompletionException("Unable to acquire Microsoft access tokens!", e2);
                }
            },
            executor
        );
    }

    public static JsonObject computeJsonObject(HttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        String str = response.getEntity() == null ? "" : EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        return XboxErrorMapper.computeJsonObject(statusCode, str);
    }

    public static CompletableFuture<Session> login(String str, Executor executor) {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    Session session;
                    try (CloseableHttpClient closeableHttpClient = HttpClients.createMinimal()) {
                        HttpGet httpGet = new HttpGet(URI.create("https://api.minecraftservices.com/minecraft/profile"));
                        httpGet.setConfig(requestConfig);
                        httpGet.setHeader("Authorization", "Bearer " + str);
                        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
                        JsonObject asJsonObject = new JsonParser().parse(EntityUtils.toString(closeableHttpResponse.getEntity())).getAsJsonObject();
                        Session session2 = Optional.ofNullable(asJsonObject.get("id"))
                            .map(JsonElement::getAsString)
                            .filter(str2 -> !StringUtils.isBlank(str2))
                            .map(str3 -> new Session(asJsonObject.get("name").getAsString(), str3, str, Type.MOJANG.toString()))
                            .orElseThrow(
                                () -> new Exception(
                                    asJsonObject.has("error")
                                        ? String.format("%s: %s", asJsonObject.get("error").getAsString(), asJsonObject.get("errorMessage").getAsString())
                                        : "There was no profile or error description present."
                                )
                            );
                        session = session2;
                    }

                    return session;
                } catch (InterruptedException e) {
                    throw new CancellationException("Minecraft profile fetching was cancelled!");
                } catch (Exception e2) {
                    throw new CompletionException("Unable to fetch Minecraft profile!", e2);
                }
            },
            executor
        );
    }

    public static final class Entry {
        public final Session session;
        public final String text;

        public Entry(Session session, String text) {
            this.session = session;
            this.text = text;
        }
    }
}
