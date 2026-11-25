package org.brutality.alt.altmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import org.brutality.alt.altmanager.auth.AccountSession;

public class AccountManager {
    private static Minecraft mc = null;
    private static final ArrayList<AccountSession> entries = new ArrayList<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static AccountSession accountSession = null;

    private static File computeFile() {
        Minecraft mc = getMc();
        return mc != null ? new File(mc.mcDataDir, "/Brutality/accounts") : new File("Brutality/accounts");
    }

    public static AccountSession computeAccountSession(int n) {
        return entries.get(n);
    }

    public static AccountSession getAccountSession() {
        return accountSession;
    }

    public static void swap(int n, int n2) {
        Collections.swap(entries, n, n2);
    }

    public static void save() {
        File file = computeFile();

        try {
            JsonArray jsonArray = new JsonArray();

            for (AccountSession accountSession : entries) {
                JsonObject json = new JsonObject();
                json.addProperty("refreshToken", accountSession.getRefreshToken());
                json.addProperty("accessToken", accountSession.getAccessToken());
                json.addProperty("username", accountSession.getUsername());
                json.addProperty("timestamp", accountSession.getTimestamp());
                jsonArray.add(json);
            }

            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.println(gson.toJson((JsonElement)jsonArray));
            writer.close();
        } catch (IOException ignored) {
        }
    }

    public static void setAccountSession(AccountSession accountSession2) {
        accountSession = accountSession2;
    }

    public static void start() {
        File file = computeFile();
        if (!file.exists()) {
            try {
                if ((file.getParentFile().exists() || file.getParentFile().mkdirs()) && file.createNewFile()) {
                }
            } catch (IOException ignored) {
            }
        }
    }

    private static Minecraft getMc() {
        if (mc == null) {
            try {
                mc = Minecraft.getMinecraft();
            } catch (Exception e) {
                mc = null;
            }
        }

        return mc;
    }

    public static ArrayList<AccountSession> computeArrayList() {
        return new ArrayList<>(entries);
    }

    public static void remove(int n) {
        entries.remove(n);
    }

    public static void clear() {
        entries.clear();
    }

    public static void load() {
        File file = computeFile();
        entries.clear();

        try {
            JsonElement element = new JsonParser().parse(new BufferedReader(new FileReader(file)));
            if (element instanceof JsonArray) {
                for (JsonElement element2 : element.getAsJsonArray()) {
                    JsonObject asJsonObject = element2.getAsJsonObject();
                    entries.add(
                        new AccountSession(
                            Optional.ofNullable(asJsonObject.get("refreshToken")).map(JsonElement::getAsString).orElse(""),
                            Optional.ofNullable(asJsonObject.get("accessToken")).map(JsonElement::getAsString).orElse(""),
                            Optional.ofNullable(asJsonObject.get("username")).map(JsonElement::getAsString).orElse(""),
                            Optional.ofNullable(asJsonObject.get("timestamp")).map(JsonElement::getAsLong).orElse(System.currentTimeMillis())
                        )
                    );
                }
            }
        } catch (FileNotFoundException ignored) {
        }
    }

    public static int size() {
        return entries.size();
    }

    public static void handleAccountSession(AccountSession accountSession) {
        entries.add(accountSession);
    }
}
