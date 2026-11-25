package org.brutality.utils.hypixel.pit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Predicate;

public class AcceptHelper {
    public static String computeString() {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://raw.githubusercontent.com/BrookeAFK/brookeafk-api/main/events.js");
            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Accept", "application/json");
            conn.setUseCaches(false);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String str;
                while ((str = reader.readLine()) != null) {
                    sb.append(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }

                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static long computeLong(String str, long l, Predicate<String> predicate) {
        try {
            for (DisplayHelper3.Entry entry : DisplayHelper3.parse(str, l)) {
                if (predicate.test(entry.text)) {
                    return entry.timestamp;
                }
            }

            return -1L;
        } catch (RuntimeException e) {
            return -1L;
        }
    }
}
