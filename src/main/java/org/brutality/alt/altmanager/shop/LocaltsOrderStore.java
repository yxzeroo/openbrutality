package org.brutality.alt.altmanager.shop;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class LocaltsOrderStore {
    private final Path path;

    public synchronized void handleString(String str, String str2, String str3) throws IOException {
        Files.createDirectories(this.path);
        JsonObject json = new JsonObject();
        json.addProperty("orderId", str);
        json.addProperty("kind", str2);
        json.addProperty("delivery", str3);
        Path path = this.computePath(str);
        Path path2 = Files.createTempFile(this.path, "order-", ".tmp");

        try {
            Files.writeString(path2, json.toString(), StandardCharsets.UTF_8);

            try {
                Files.move(path2, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
            }

            Files.writeString(this.path.resolve("latest.txt"), str, StandardCharsets.UTF_8);
        } finally {
            Files.deleteIfExists(path2);
        }
    }

    public String computeString() {
        try {
            return Files.readString(this.path.resolve("latest.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    public void process() throws IOException {
        Files.createDirectories(this.path);
        Path path = Files.createTempFile(this.path, "write-check-", ".tmp");
        Files.delete(path);
    }

    public String computeString(String str) throws IOException {
        Path path = this.computePath(str);
        if (!Files.exists(path)) {
            return "";
        }
        try {
            return new JsonParser().parse(Files.readString(path)).getAsJsonObject().get("kind").getAsString();
        } catch (RuntimeException e) {
            throw new IOException("Saved order information is invalid.");
        }
    }

    private Path computePath(String str) throws IOException {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(str.getBytes(StandardCharsets.UTF_8));
            return this.path.resolve(HexFormat.of().formatHex(bytes) + ".json");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
    }

    public LocaltsOrderStore(Path path) {
        this.path = path.resolve("Brutality/localts/orders");
    }
}
