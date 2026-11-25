package org.brutality.launcher.internal;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.http.HttpTimeoutException;
import java.nio.file.Path;
import javax.net.ssl.SSLException;

public final class BootstrapConfig {
    private BootstrapConfig() {
    }

    public static void handleStringArray(String[] parts) {
        handleStringArray(parts, "net.minecraft.client.main.Main", parts);
    }

    static String computeString(String str, Throwable t) {
        for (int i = 0; i < 32; i++) {
            if (t instanceof PayloadBootstrapException) {
                return t.getMessage();
            }

            if (t.getCause() == null || t.getCause() == t) {
                break;
            }

            t = t.getCause();
        }

        if (t instanceof UnknownHostException || t instanceof ConnectException) {
            return str + ": Unable to connect to the server.";
        }
        if (t instanceof HttpTimeoutException) {
            return str + ": The server timed out. Try again.";
        }
        if (t instanceof SSLException) {
            return str + ": Secure connection failed. Check your system clock and Java installation.";
        }
        return "Checking logging runtime".equals(str)
            ? "Your launch profile has obsolete or unidentified Log4j. Reinstall the latest Brutality ZIP/profile or update the Brutality launcher."
            : str + " failed (" + t.getClass().getSimpleName() + ").";
    }

    public static void handleStringArray(String[] parts, String str, String[] parts2) {
        String str2 = "Checking logging runtime";

        try {
            HardwareFingerprint.handleString(str, parts2);
            HardwareFingerprint.process();
            str2 = "Reading license";
            String str3 = BootstrapHttpClient.b(parts);
            String str4 = ManifestJsonParser.computeString(str3, "key");
            str2 = "Creating machine identity";
            String str5 = SessionTokenReader.computeString();
            str2 = "Requesting signed manifest";
            PayloadManifest payloadManifest = new PayloadManifest(Log4jRuntimeValidator.computeString(str4, str5));
            str2 = "Downloading and verifying payload";
            Path path = PayloadDownloader.computePath(payloadManifest, str4, str5, str3);
            str2 = "Starting client";
            PayloadClassLoader.handlePath(path, parts);
        } catch (Throwable e) {
            System.err.println("Brutality could not start. " + computeString(str2, e));
            System.exit(1);
        }
    }
}
