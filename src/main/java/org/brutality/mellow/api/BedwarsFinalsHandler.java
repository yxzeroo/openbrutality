package org.brutality.mellow.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.brutality.mellow.Mellow;

public class BedwarsFinalsHandler {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void handleString(String str, int n, BedwarsFinalsHandler.Listener listener) {
        this.handleString("bedwars_finals", str, 1000, n, listener);
    }

    public void handleString(String str, double d, BedwarsFinalsHandler.Listener listener) {
        this.handleString("bedwars_fkdr", str, 1000, (int)d, listener);
    }

    public void handleString(String str, String str2, int n, int n2, BedwarsFinalsHandler.Listener listener) {
        this.executorService.submit(() -> {
            try {
                String str3 = this.computeString();
                if (str3 == null || str3.isEmpty()) {
                    listener.onResult(null);
                    return;
                }

                String str4 = "https://bordic.xyz/api/v2/resources/lookup/" + str + "?key=" + str3 + "&value=" + str2 + "&range=" + n + "&max=" + n2;
                URL url = new URL(str4);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Brutality-Client");
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();

                    String str5;
                    while ((str5 = reader.readLine()) != null) {
                        sb.append(str5);
                    }

                    reader.close();
                    BedwarsFinalsHandler.Entry entry = new Gson().fromJson(sb.toString(), BedwarsFinalsHandler.Entry.class);
                    listener.onResult(entry);
                } else {
                    listener.onResult(null);
                }
            } catch (Exception e) {
                listener.onResult(null);
            }
        });
    }

    private String computeString() {
        try {
            return Mellow.getInstance().getAuroraKeySetting().getValue();
        } catch (Exception e) {
            return "";
        }
    }

    public static class Entry {
        @SerializedName("success")
        public boolean flag;
        @SerializedName("data")
        public List<BedwarsFinalsHandler.Entry2> entries;
    }

    public static class Entry2 {
        @SerializedName("name")
        public String name;
        @SerializedName("distance")
        public int count;
    }

    public interface Listener {
        void onResult(BedwarsFinalsHandler.Entry result);
    }
}
