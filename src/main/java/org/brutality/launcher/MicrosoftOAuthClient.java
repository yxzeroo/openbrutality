package org.brutality.launcher;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.brutality.launcher.MicrosoftOAuthClient.HttpTransport;
import org.brutality.launcher.MicrosoftOAuthClient.MinecraftAccount;

final class MicrosoftOAuthClient {
    static final String text = "54fd49e4-2103-4044-9603-2b028c814ec3";
    static final String b = "http://localhost:59125";
    static final String text2 = "https://login.live.com/oauth20_token.srf";
    private final HttpTransport httpTransport;

    MicrosoftOAuthClient() {
        this(
            (str, str2, str3) -> {
                Builder builder = HttpRequest.newBuilder(URI.create(str)).timeout(Duration.ofSeconds(30L));
                if (str3 != null) {
                    builder.header("Authorization", "Bearer " + str3);
                }

                if (str2 == null) {
                    builder.GET();
                } else {
                    builder.header(
                            "Content-Type", str.equals("https://login.live.com/oauth20_token.srf") ? "application/x-www-form-urlencoded" : "application/json"
                        )
                        .POST(BodyPublishers.ofString(str2));
                }

                HttpResponse response = Launcher.httpClient.send(builder.build(), BodyHandlers.ofString());
                if (response.statusCode() / 100 != 2) {
                    throw new IOException(
                        str.equals("https://login.live.com/oauth20_token.srf")
                            ? "Microsoft login expired or was rejected. Sign in again."
                            : "Xbox or Minecraft rejected sign-in. Check game ownership and Xbox account permissions."
                    );
                }
                try {
                    return JsonParser.parseString((String)response.body()).getAsJsonObject();
                } catch (RuntimeException e) {
                    throw new IOException("Microsoft login returned an invalid response.");
                }
            }
        );
    }

    MicrosoftOAuthClient(HttpTransport httpTransport) {
        this.httpTransport = httpTransport;
    }

    static String computeString(Map<String, String> map) {
        return map.entrySet()
            .stream()
            .map(
                entry -> URLEncoder.encode((String)entry.getKey(), StandardCharsets.UTF_8)
                    + "="
                    + URLEncoder.encode((String)entry.getValue(), StandardCharsets.UTF_8)
            )
            .collect(Collectors.joining("&"));
    }

    private static String computeString() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String computeString(JsonObject json, String str) throws IOException {
        if (json != null && json.has(str) && !json.get(str).isJsonNull() && !json.get(str).getAsString().isBlank()) {
            return json.get(str).getAsString();
        }
        throw new IOException("Microsoft login returned an incomplete response.");
    }

    MinecraftAccount computeMinecraftAccount(String str) throws Exception {
        JsonObject json = this.httpTransport
            .send(
                "https://login.live.com/oauth20_token.srf",
                computeString(
                    Map.of(
                        "client_id",
                        "54fd49e4-2103-4044-9603-2b028c814ec3",
                        "grant_type",
                        "refresh_token",
                        "refresh_token",
                        str,
                        "scope",
                        "XboxLive.signin XboxLive.offline_access"
                    )
                ),
                null
            );
        return this.b(json, json.has("refresh_token") ? computeString(json, "refresh_token") : str);
    }

    MinecraftAccount computeMinecraftAccount(Consumer<URI> callback) throws Exception {
        String str = computeString();
        String str2 = computeString();
        String str3 = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(MessageDigest.getInstance("SHA-256").digest(str.getBytes(StandardCharsets.US_ASCII)));
        CompletableFuture future = new CompletableFuture();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost", 59125), 0);
        httpServer.createContext("/", httpExchange -> {
            short s = 400;
            String str4 = "Invalid login callback. Return to the launcher.";

            try {
                Map map = b(httpExchange.getRequestURI().getRawQuery());
                if ("GET".equals(httpExchange.getRequestMethod()) && str2.equals(map.get("state"))) {
                    if (map.containsKey("error")) {
                        future.completeExceptionally(new IOException("Microsoft sign-in was cancelled or denied."));
                    } else if (map.containsKey("code") && !((String)map.get("code")).isBlank()) {
                        future.complete((String)map.get("code"));
                        s = 200;
                        str4 = "Login received. You can close this page and return to Brutality.";
                    }
                }
            } catch (RuntimeException ignored) {
            }

            httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            httpExchange.getResponseHeaders().set("Cache-Control", "no-store");
            byte[] bytes = str4.getBytes(StandardCharsets.UTF_8);

            try {
                httpExchange.sendResponseHeaders(s, bytes.length);
                httpExchange.getResponseBody().write(bytes);
            } finally {
                httpExchange.close();
            }
        });

        MinecraftAccount minecraftAccount;
        try {
            httpServer.start();
            callback.accept(
                URI.create(
                    "https://login.live.com/oauth20_authorize.srf?"
                        + computeString(
                            Map.of(
                                "client_id",
                                "54fd49e4-2103-4044-9603-2b028c814ec3",
                                "response_type",
                                "code",
                                "redirect_uri",
                                "http://localhost:59125",
                                "scope",
                                "XboxLive.signin XboxLive.offline_access",
                                "prompt",
                                "select_account",
                                "state",
                                str2,
                                "code_challenge",
                                str3,
                                "code_challenge_method",
                                "S256"
                            )
                        )
                )
            );
            String str5 = (String)future.get(3L, TimeUnit.MINUTES);
            JsonObject json = this.httpTransport
                .send(
                    "https://login.live.com/oauth20_token.srf",
                    computeString(
                        Map.of(
                            "client_id",
                            "54fd49e4-2103-4044-9603-2b028c814ec3",
                            "grant_type",
                            "authorization_code",
                            "code",
                            str5,
                            "redirect_uri",
                            "http://localhost:59125",
                            "code_verifier",
                            str
                        )
                    ),
                    null
                );
            minecraftAccount = this.b(json, computeString(json, "refresh_token"));
        } catch (TimeoutException e) {
            throw new IOException("Microsoft sign-in timed out. Try again.");
        } finally {
            httpServer.stop(0);
        }

        return minecraftAccount;
    }

    static Map<String, String> b(String str) {
        HashMap map = new HashMap();
        if (str != null) {
            for (String str2 : str.split("&")) {
                String[] parts = str2.split("=", 2);
                String str3 = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                if (map.putIfAbsent(str3, parts.length == 2 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "") != null) {
                    throw new IllegalArgumentException("Duplicate callback parameter");
                }
            }
        }

        return map;
    }

    private MinecraftAccount b(JsonObject json, String str) throws Exception {
        JsonObject json2 = this.httpTransport
            .send(
                "https://user.auth.xboxlive.com/user/authenticate",
                new Gson()
                    .toJson(
                        Map.of(
                            "Properties",
                            Map.of("AuthMethod", "RPS", "SiteName", "user.auth.xboxlive.com", "RpsTicket", "d=" + computeString(json, "access_token")),
                            "RelyingParty",
                            "http://auth.xboxlive.com",
                            "TokenType",
                            "JWT"
                        )
                    ),
                null
            );
        JsonObject json3 = this.httpTransport
            .send(
                "https://xsts.auth.xboxlive.com/xsts/authorize",
                new Gson()
                    .toJson(
                        Map.of(
                            "Properties",
                            Map.of("SandboxId", "RETAIL", "UserTokens", List.<String>of(computeString(json2, "Token"))),
                            "RelyingParty",
                            "rp://api.minecraftservices.com/",
                            "TokenType",
                            "JWT"
                        )
                    ),
                null
            );
        String str2 = computeString(json3.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject(), "uhs");
        JsonObject json4 = this.httpTransport
            .send(
                "https://api.minecraftservices.com/authentication/login_with_xbox",
                new Gson().toJson(Map.of("identityToken", "XBL3.0 x=" + str2 + ";" + computeString(json3, "Token"))),
                null
            );
        String str3 = computeString(json4, "access_token");
        JsonObject json5 = this.httpTransport.send("https://api.minecraftservices.com/entitlements/mcstore", null, str3);
        if (json5.has("items") && !json5.getAsJsonArray("items").isEmpty()) {
            JsonObject json6 = this.httpTransport.send("https://api.minecraftservices.com/minecraft/profile", null, str3);
            String str4 = computeString(json6, "name");
            String str5 = computeString(json6, "id");
            if (str4.matches("[A-Za-z0-9_]{1,16}") && str5.matches("[a-fA-F0-9]{32}")) {
                long l = json4.has("expires_in") ? json4.get("expires_in").getAsLong() : 3600L;
                String str6 = null;
                if (json6.has("skins") && !json6.getAsJsonArray("skins").isEmpty()) {
                    String str7 = computeString(json6.getAsJsonArray("skins").get(0).getAsJsonObject(), "url");
                    URI uri = URI.create(str7);
                    if ("textures.minecraft.net".equals(uri.getHost())) {
                        str6 = "https://textures.minecraft.net" + uri.getRawPath();
                    }
                }

                return new MinecraftAccount(str4, str5, str3, str, Instant.now().getEpochSecond() + Math.max(0L, l), str6);
            }
            throw new IOException("Minecraft profile is incomplete.");
        }
        throw new IOException("This Microsoft account does not own Minecraft Java Edition.");
    }

    record MinecraftAccount(String name, String id, String accessToken, String refreshToken, long expiresAt, String skinUrl) {
    }

    @FunctionalInterface
    interface HttpTransport {
        com.google.gson.JsonObject send(String url, String body, String bearerToken) throws Exception;
    }
}
