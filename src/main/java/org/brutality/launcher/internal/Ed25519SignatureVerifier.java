package org.brutality.launcher.internal;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HexFormat;

final class Ed25519SignatureVerifier {
    private static final String text = "MCowBQYDK2VwAyEAgLxBhsVe8/ebUYQqxIZFGka2nOmvRtSY/KoEtmhDB7k=";

    private Ed25519SignatureVerifier() {
    }

    static void handleString(String str) {
        if (!"MCowBQYDK2VwAyEAgLxBhsVe8/ebUYQqxIZFGka2nOmvRtSY/KoEtmhDB7k=".equals(str)) {
            throw new SecurityException("payload public key is not pinned in this bootstrap");
        }
    }

    private static void handleIntArray(byte[] bytes, String str, String str2) throws Exception {
        PublicKey publicKey = KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(str2)));
        Signature instance = Signature.getInstance("Ed25519");
        instance.initVerify(publicKey);
        instance.update(bytes);
        if (!instance.verify(Base64.getDecoder().decode(str))) {
            throw new SecurityException("payload signature verification failed");
        }
    }

    static void handleIntArray(byte[] bytes, PayloadManifest payloadManifest) throws Exception {
        handleString(payloadManifest.text4);
        String str = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        if (!str.equalsIgnoreCase(payloadManifest.text2)) {
            throw new SecurityException("payload hash mismatch");
        }
        handleIntArray(bytes, payloadManifest.text3, payloadManifest.text4);
    }
}
