package org.brutality.module.impl.pit.hypixel.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class PitSessionRecorder implements AutoCloseable {
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
    public final long dx = System.nanoTime();
    public final Path path;
    public final String text;
    public final int count;
    private final BufferedWriter bufferedWriter;
    private final Mac mac;
    private final Map<UUID, String> lookupMap = new HashMap<>();
    private final Map<String, Integer> lookupMap2 = new TreeMap<>();
    private static final long dy = 1000000L;

    @Override
    public void close() throws IOException {
        this.bufferedWriter.close();
    }

    public String computeString(String str) {
        byte[] bytes = this.mac.doFinal(str.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(Arrays.copyOf(bytes, 12));
    }

    public PitSessionRecorder(Path path, String text, int count) throws IOException, GeneralSecurityException {
        Path path2 = Files.createDirectories(path.resolve("Brutality"));
        Path path3 = Files.createDirectories(path2.resolve("logs").resolve("pit-recorder"));
        Path path4 = path2.resolve("pit-recorder.key");
        if (Files.notExists(path4)) {
            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);

            try {
                Files.write(path4, bytes, StandardOpenOption.CREATE_NEW);
            } catch (FileAlreadyExistsException ignored) {
            }
        }

        byte[] bytes2 = Files.readAllBytes(path4);
        if (bytes2.length != 32) {
            throw new IOException("Invalid Pit recorder key");
        }
        this.mac = Mac.getInstance("HmacSHA256");
        this.mac.init(new SecretKeySpec(bytes2, "HmacSHA256"));
        this.text = text;
        this.count = count;
        String str = Instant.now().toString().replace(':', '-');
        this.path = path3.resolve(text + "-" + str + "-" + UUID.randomUUID() + ".jsonl");
        this.bufferedWriter = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }

    public void flush() throws IOException {
        this.bufferedWriter.flush();
    }

    public String computeString(UUID uuid) {
        return uuid == null ? null : this.lookupMap.computeIfAbsent(uuid, uuid2 -> this.computeString("player:" + uuid2));
    }

    public JsonObject computeJsonObject(String str) {
        JsonObject json = new JsonObject();
        json.addProperty("zone", this.text);
        json.addProperty("reason", str);
        json.addProperty("uniquePlayers", this.lookupMap.size());
        json.add("counts", gson.toJsonTree(this.lookupMap2));
        return json;
    }

    public long computeLong() {
        return (System.nanoTime() - this.dx) / dy;
    }

    public void handleString(String str, String str2, boolean flag, long l, String str3, String str4, JsonObject json) throws IOException {
        json.addProperty("event", str);
        json.addProperty("source", str2);
        json.addProperty("inferred", flag);
        json.addProperty("tick", l);
        json.addProperty("time", System.currentTimeMillis());
        json.addProperty("elapsedMs", this.computeLong());
        json.addProperty("map", str3);
        json.addProperty("lobby", str4);
        this.bufferedWriter.write(gson.toJson((JsonElement)json));
        this.bufferedWriter.newLine();
        this.lookupMap2.merge(str, 1, Integer::sum);
    }
}
