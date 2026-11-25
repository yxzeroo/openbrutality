package org.brutality.launcher.internal;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

final class PayloadClassLoader {
    private PayloadClassLoader() {
    }

    static void handlePath(Path path, String[] parts) throws Exception {
        BackendTlsContext backendTlsContext = new BackendTlsContext(new URL[]{path.toUri().toURL()}, PayloadClassLoader.class.getClassLoader());
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread thread = new Thread(() -> {
            try {
                backendTlsContext.close();
            } catch (IOException ignored) {
            }

            PinnedX509TrustManager.handlePath(path);
        }, "Brutality-Client-Cleanup");
        Runtime.getRuntime().addShutdownHook(thread);

        try {
            Thread.currentThread().setContextClassLoader(backendTlsContext);
            Class.forName("net.minecraft.client.main.Main", true, backendTlsContext).getMethod("main", String[].class).invoke(null, parts);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);

            try {
                Runtime.getRuntime().removeShutdownHook(thread);
            } catch (IllegalStateException ignored2) {
            }

            try {
                backendTlsContext.close();
            } finally {
                PinnedX509TrustManager.handlePath(path);
            }
        }
    }
}
