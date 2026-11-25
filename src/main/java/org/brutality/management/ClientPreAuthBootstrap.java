package org.brutality.management;

import java.util.concurrent.CompletableFuture;
import net.minecraft.client.main.Main;
import org.brutality.system.AuthLoginDialog;

public class ClientPreAuthBootstrap {
    private static void process() {
        if (!Boolean.getBoolean("brut.dev")) {
            try {
                CompletableFuture future = AuthLoginDialog.authenticate();
                if (!Boolean.TRUE.equals(future.get())) {
                    throw new IllegalStateException("Authentication cancelled or failed");
                }
                System.setProperty("brutality.preAuthenticated", "true");
            } catch (Exception e) {
                throw new IllegalStateException("Authentication failed", e);
            }
        }
    }

    public static void main(String[] parts) {
        Main.main(parts);
    }

    static {
        NativesExtractor.extractNatives();
    }

    public static void process2() {
        process();
    }
}
