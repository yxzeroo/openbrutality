package org.brutality.utils.misc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class AuthorizationHelper {
    public static int computeInt2(String str, String str2) throws IOException {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.minecraftservices.com/minecraft/profile/skins");
        httpPost.setHeader("Authorization", "Bearer " + str2);
        httpPost.setHeader("Content-Type", "application/json");
        String str3 = String.format("{ \"variant\": \"classic\", \"url\": \"%s\"}", str);
        httpPost.setEntity(new StringEntity(str3));
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
        return closeableHttpResponse.getStatusLine().getStatusCode();
    }

    public static int computeInt(String str, String str2) throws IOException {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut("https://api.minecraftservices.com/minecraft/profile/name/" + str);
        httpPut.setHeader("Authorization", "Bearer " + str2);
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPut);
        return closeableHttpResponse.getStatusLine().getStatusCode();
    }

    public static String[] computeStringArray(String str) throws IOException {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
        httpGet.setHeader("Authorization", "Bearer " + str);
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        String str2 = EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8);
        JsonObject asJsonObject = new JsonParser().parse(str2).getAsJsonObject();
        String asString = asJsonObject.get("name").getAsString();
        String asString2 = asJsonObject.get("id").getAsString();
        return new String[]{asString, asString2};
    }

    public static Boolean computeBooleanType(String str) {
        try {
            CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("https://api.slothpixel.me/api/players/" + str);
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            String str2 = EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8);
            JsonObject asJsonObject = new JsonParser().parse(str2).getAsJsonObject();
            return asJsonObject.get("online").getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
