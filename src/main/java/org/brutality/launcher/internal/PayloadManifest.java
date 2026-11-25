package org.brutality.launcher.internal;

import java.net.URI;

final class PayloadManifest {
    final String text;
    final String b;
    final String text2;
    final String text3;
    final String text4;

    PayloadManifest(String str) throws Exception {
        this.text = ManifestJsonParser.b(str, "version");
        this.b = ManifestJsonParser.b(str, "payload_url");
        URI uri = URI.create(this.b);
        if ("https".equals(uri.getScheme())
            && "bru3y2ty22n2n2nsy6y2hb3b3g2.up.railway.app".equals(uri.getHost())
            && uri.getRawUserInfo() == null
            && (uri.getPort() == -1 || uri.getPort() == 443)
            && uri.getPath().startsWith("/api/client/payload/")) {
            this.text2 = ManifestJsonParser.b(str, "sha256");
            this.text3 = ManifestJsonParser.b(str, "signature");
            this.text4 = ManifestJsonParser.b(str, "public_key");
            Ed25519SignatureVerifier.handleString(this.text4);
        } else {
            throw new SecurityException("Untrusted payload endpoint");
        }
    }
}
