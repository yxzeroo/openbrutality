package org.brutality.alt.altmanager.shop;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public final class LocaltsApiClient {
    private final String text;
    private final LocaltsApiClient.Listener2 listener2;
    private final LocaltsApiClient.Listener listener;

    private JsonObject computeJsonObject(String str, String str2) throws InterruptedException, IOException {
        boolean flag = "GET".equals(str);

        for (int i = 0; i < (flag ? 4 : 1); i++) {
            LocaltsApiClient.BpgEntry bpgEntry;
            try {
                bpgEntry = this.listener2.send(str, str2, this.text);
            } catch (IOException e) {
                if (!flag) {
                    throw new LocaltsApiClient.Entry();
                }

                if (i == 3) {
                    throw new IOException("Localts connection failed. Try Refresh.");
                }

                this.listener.sleep(1500L << i);
                continue;
            }

            boolean flag2 = bpgEntry.bpg == 429 || bpgEntry.bpg == 408 || bpgEntry.bpg == 425 || bpgEntry.bpg >= 500;
            if (!flag || !flag2 || i >= 3) {
                if (!flag && flag2) {
                    throw new LocaltsApiClient.Entry();
                }

                JsonObject json;
                try {
                    json = new JsonParser().parse(bpgEntry.OO00o0).getAsJsonObject();
                } catch (RuntimeException e2) {
                    if (!flag && bpgEntry.bpg >= 200 && bpgEntry.bpg < 300) {
                        throw new LocaltsApiClient.Entry();
                    }

                    throw new IOException("Localts returned a non-JSON response (HTTP " + bpgEntry.bpg + ").");
                }

                if (flag
                    || bpgEntry.bpg < 200
                    || bpgEntry.bpg >= 300
                    || json.has("success") && json.get("success").isJsonPrimitive() && json.getAsJsonPrimitive("success").isBoolean()) {
                    if (bpgEntry.bpg >= 200
                        && bpgEntry.bpg < 300
                        && json.has("success")
                        && json.get("success").isJsonPrimitive()
                        && json.getAsJsonPrimitive("success").isBoolean()
                        && json.get("success").getAsBoolean()) {
                        return json;
                    }

                    String str3 = json.has("error") && json.get("error").isJsonPrimitive()
                        ? json.get("error").getAsString().replace(this.text, "[hidden]")
                        : "Localts rejected the request (HTTP " + bpgEntry.bpg + ").";
                    throw new IOException(str3);
                }

                throw new LocaltsApiClient.Entry();
            }

            long l = 1500L << i;

            try {
                if (bpgEntry.ll1IlI != null) {
                    l = (long)(Double.parseDouble(bpgEntry.ll1IlI) * 1000.0);
                }
            } catch (NumberFormatException ignored) {
            }

            this.listener.sleep(Math.max(1L, Math.min(15000L, l)));
        }

        throw new IOException("Localts is temporarily unavailable.");
    }

    public LocaltsApiClient.OO0Oo0Entry computeOO0Oo0Entry(String str) throws InterruptedException, IOException {
        JsonObject json = this.computeJsonObject("GET", "/v1/orders/get-order?id=" + encode(str));
        ArrayList list = new ArrayList();

        try {
            if (json.has("items") && json.get("items").isJsonArray()) {
                for (JsonElement element : json.getAsJsonArray("items")) {
                    JsonObject asJsonObject = element.getAsJsonObject();
                    list.add(new LocaltsApiClient.LlI1lIEntry(computeString2(asJsonObject, "id"), computeString(asJsonObject, "content")));
                }
            }
        } catch (RuntimeException e) {
            throw new IOException("Localts returned malformed delivery data. Resume the order later.");
        }

        return new LocaltsApiClient.OO0Oo0Entry(computeString(json, "status"), list, json);
    }

    public LocaltsApiClient.O0oO0OEntry computeO0oO0OEntry() throws InterruptedException, IOException {
        JsonObject json = this.computeJsonObject("GET", "/v1/me");

        try {
            return new LocaltsApiClient.O0oO0OEntry(computeString(json, "username"), new BigDecimal(computeString(json, "balance")));
        } catch (NumberFormatException e) {
            throw new IOException("Localts returned an invalid balance.");
        }
    }

    public LocaltsApiClient(String str, LocaltsApiClient.Listener2 listener2, LocaltsApiClient.Listener listener) {
        if (str != null && !str.trim().isEmpty() && !str.contains("\r") && !str.contains("\n")) {
            this.text = str.trim();
            this.listener2 = listener2;
            this.listener = listener;
        } else {
            throw new IllegalArgumentException("Enter a valid Localts API key.");
        }
    }

    private static String computeString(JsonObject json, String str) throws IOException {
        if (json.has(str) && json.get(str).isJsonPrimitive()) {
            return json.get(str).getAsString();
        }
        throw new IOException("Localts response is missing " + str + ".");
    }

    public LocaltsApiClient(String str) {
        this(str, (str2, str3, str4) -> {
            HttpURLConnection conn = (HttpURLConnection)new URL("https://localts.store" + str3).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod(str2);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("X-API-Key", str4);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Brutality/1.8.9");

            LocaltsApiClient.BpgEntry bpgEntry;
            try {
                if ("POST".equals(str2)) {
                    conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(0);
                    OutputStream outputStream = conn.getOutputStream();
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }

                int responseCode = conn.getResponseCode();
                InputStream in = responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream();
                String str5 = "";
                if (in != null) {
                    InputStream in2 = in;

                    try {
                        byte[] bytes = in2.readNBytes(2097153);
                        if (bytes.length > 2097152) {
                            throw new IOException("Localts response is too large.");
                        }

                        str5 = new String(bytes, StandardCharsets.UTF_8);
                    } catch (Throwable e) {
                        if (in != null) {
                            try {
                                in2.close();
                            } catch (Throwable e2) {
                                e.addSuppressed(e2);
                            }
                        }

                        throw e;
                    }

                    if (in != null) {
                        in.close();
                    }
                }

                bpgEntry = new LocaltsApiClient.BpgEntry(responseCode, str5, conn.getHeaderField("Retry-After"));
            } finally {
                conn.disconnect();
            }

            return bpgEntry;
        }, Thread::sleep);
    }

    public String computeString(String str, int n) throws IOException, InterruptedException {
        if (str != null && !str.isEmpty() && n >= 1) {
            JsonObject json = this.computeJsonObject("POST", "/v1/products/" + encode(str) + "/purchase?amount=" + n);

            try {
                String str2 = computeString(json, "orderId");
                if (str2.isEmpty()) {
                    throw new IOException();
                }
                return str2;
            } catch (IOException e) {
                throw new LocaltsApiClient.Entry();
            }
        } else {
            throw new IllegalArgumentException("Select a product and quantity.");
        }
    }

    private static String computeString2(JsonObject json, String str) {
        return json.has(str) && json.get(str).isJsonPrimitive() ? json.get(str).getAsString() : "";
    }

    public LocaltsApiClient.OO0Oo0Entry computeOO0Oo0Entry(String str, Consumer<String> callback) throws InterruptedException, IOException {
        int n = 0;

        for (int i = 0; i < 60; i++) {
            try {
                LocaltsApiClient.OO0Oo0Entry oO0Oo0Entry = this.computeOO0Oo0Entry(str);
                n = 0;
                if (oO0Oo0Entry.OO0Oo0.equalsIgnoreCase("PACKAGED")) {
                    if (oO0Oo0Entry.Il11.isEmpty()) {
                        throw new IOException("Localts packaged this order without delivered accounts. Resume the order later.");
                    }

                    return oO0Oo0Entry;
                }

                callback.accept("Order: " + oO0Oo0Entry.OO0Oo0);
            } catch (IOException e) {
                if (++n >= 5) {
                    throw new IOException("Order is not ready. Use Resume Order; do not purchase it again.");
                }

                this.listener.sleep(3000L * n);
                continue;
            }

            this.listener.sleep(2500L);
        }

        throw new IOException("Order packaging timed out. Use Resume Order; do not purchase it again.");
    }

    public static String encode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public List<LocaltsApiClient.Lll1I1Entry> computeList() throws IOException, InterruptedException {
        JsonObject json = this.computeJsonObject("GET", "/v1/products");
        if (json.has("products") && json.get("products").isJsonArray()) {
            ArrayList list = new ArrayList();

            try {
                for (JsonElement element : json.getAsJsonArray("products")) {
                    JsonObject asJsonObject = element.getAsJsonObject();
                    TreeMap treeMap = new TreeMap();
                    if (asJsonObject.has("quantityDiscounts") && asJsonObject.get("quantityDiscounts").isJsonObject()) {
                        for (Map.Entry entry : asJsonObject.getAsJsonObject("quantityDiscounts").entrySet()) {
                            try {
                                int n = Integer.parseInt((String)entry.getKey());
                                if (n > 0) {
                                    treeMap.put(n, ((JsonElement)entry.getValue()).getAsBigDecimal());
                                }
                            } catch (RuntimeException ignored) {
                            }
                        }
                    }

                    ArrayList list2 = new ArrayList();
                    if (asJsonObject.has("tags") && asJsonObject.get("tags").isJsonArray()) {
                        for (JsonElement element2 : asJsonObject.getAsJsonArray("tags")) {
                            if (element2.isJsonPrimitive()) {
                                list2.add(element2.getAsString().trim());
                            }
                        }
                    }

                    BigDecimal bigDecimal = new BigDecimal(computeString(asJsonObject, "priceInCredits"));
                    int n2 = Integer.parseInt(computeString(asJsonObject, "stock"));
                    if (bigDecimal.signum() < 0 || n2 < 0) {
                        throw new IOException("Localts returned invalid product pricing or stock.");
                    }

                    list.add(
                        new LocaltsApiClient.Lll1I1Entry(
                            computeString(asJsonObject, "id"),
                            computeString(asJsonObject, "name"),
                            computeString2(asJsonObject, "description"),
                            computeString2(asJsonObject, "category") + " " + computeString2(asJsonObject, "type"),
                            bigDecimal,
                            n2,
                            Collections.unmodifiableMap(treeMap),
                            Collections.unmodifiableList(list2)
                        )
                    );
                }
            } catch (RuntimeException e) {
                throw new IOException("Localts returned malformed products.");
            }

            list.sort(Comparator.comparing(LocaltsApiClient.Lll1I1Entry::III1Il, String.CASE_INSENSITIVE_ORDER));
            return list;
        }
        throw new IOException("Localts returned no product list.");
    }

    public record BpgEntry(int bpg, String OO00o0, String ll1IlI) {
    }

    public static final class Entry extends IOException {
        public Entry() {
            super("Purchase outcome is unknown. Check your Localts orders before buying again.");
        }
    }

    public interface Listener {
        void sleep(long millis) throws InterruptedException;
    }

    public interface Listener2 {
        LocaltsApiClient.BpgEntry send(String method, String path, String key) throws IOException;
    }

    public record LlI1lIEntry(String llI1lI, String IIl1lI) {
    }

    public record Lll1I1Entry(
        String lll1I1, String III1Il, String oo0oO0, String ll1llI, BigDecimal Il1I, int bpf, Map<Integer, BigDecimal> Il11, List<String> O00O
    ) {
        public boolean checkState() {
            String str = (this.III1Il + " " + this.oo0oO0 + " " + this.ll1llI + " " + String.join(" ", this.O00O)).toLowerCase(Locale.ROOT);
            return str.contains("refresh token") || str.contains("refresh-token") || str.contains("oauth token");
        }

        public boolean isCookie() {
            return this.O00O.stream().anyMatch(str -> str.equalsIgnoreCase("cookie"))
                || (this.III1Il + " " + this.oo0oO0 + " " + this.ll1llI).toLowerCase(Locale.ROOT).contains("cookie");
        }

        public BigDecimal computeBigDecimal(int n) {
            if (n < 1) {
                throw new IllegalArgumentException("Quantity must be at least one.");
            }
            BigDecimal ZERO = BigDecimal.ZERO;
            int n2 = 0;

            for (Map.Entry entry : this.Il11.entrySet()) {
                if (n >= (Integer)entry.getKey() && (Integer)entry.getKey() > n2) {
                    n2 = (Integer)entry.getKey();
                    ZERO = (BigDecimal)entry.getValue();
                }
            }

            ZERO = ZERO.max(BigDecimal.ZERO).min(new BigDecimal("100"));
            return this.Il1I.multiply(BigDecimal.valueOf((long)n)).multiply(BigDecimal.ONE.subtract(ZERO.movePointLeft(2)));
        }
    }

    public record O0oO0OEntry(String o0oO0O, BigDecimal l1l1) {
    }

    public record OO0Oo0Entry(String OO0Oo0, List<LocaltsApiClient.LlI1lIEntry> Il11, JsonObject l1l1) {
    }
}
