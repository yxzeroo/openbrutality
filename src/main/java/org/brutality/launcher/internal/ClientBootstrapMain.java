package org.brutality.launcher.internal;

import java.net.HttpURLConnection;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;

public final class ClientBootstrapMain {
    private static final Set<String> itemSet = Set.of("brutality-backend-production.up.railway.app", "bru3y2ty22n2n2nsy6y2hb3b3g2.up.railway.app");
    private static final Set<String> b = Set.of("HLb3HRWGSbybWXPPGKsaL3NwYvwWHrTYTCYU15lfeQ4=", "s/tdAOmUzd8syaTuqfgGvFcn6DzA5Cmb+Vby1ST+U3Y=");
    private static final SSLContext sslContext = b();

    private ClientBootstrapMain() {
    }

    public static SSLContext getSslContext() {
        return sslContext;
    }

    static void handleX509CertificateArray(X509Certificate[] x509Certificates, String str) throws CertificateException {
        if (str == null) {
            throw new CertificateException("Missing TLS peer identity");
        }
        if (itemSet.contains(str.toLowerCase(Locale.ROOT))) {
            try {
                if (x509Certificates.length == 0) {
                    throw new CertificateException("Missing TLS certificate chain");
                }

                X509Certificate x509Certificate = x509Certificates[0];
                HashSet set = new HashSet();

                while (x509Certificate != null && set.add(x509Certificate)) {
                    String str2 = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(x509Certificate.getPublicKey().getEncoded()));
                    if (b.contains(str2)) {
                        return;
                    }

                    X509Certificate x509Certificate2 = null;

                    for (X509Certificate x509Certificate3 : x509Certificates) {
                        if (!x509Certificate3.equals(x509Certificate) && x509Certificate.getIssuerX500Principal().equals(x509Certificate3.getSubjectX500Principal())) {
                            try {
                                x509Certificate.verify(x509Certificate3.getPublicKey());
                                x509Certificate2 = x509Certificate3;
                                break;
                            } catch (GeneralSecurityException ignored) {
                            }
                        }
                    }

                    x509Certificate = x509Certificate2;
                }
            } catch (GeneralSecurityException e) {
                throw new CertificateException("TLS pin validation failed", e);
            }

            throw new CertificateException("Authentication TLS public key mismatch");
        }
    }

    public static void handleHttpURLConnection(HttpURLConnection conn) {
        if (conn instanceof HttpsURLConnection && itemSet.contains(conn.getURL().getHost().toLowerCase(Locale.ROOT))) {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)conn;
            httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setInstanceFollowRedirects(false);
        } else {
            throw new SecurityException("Untrusted authentication endpoint");
        }
    }

    private static SSLContext b() {
        try {
            TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            instance.init((KeyStore)null);
            final X509ExtendedTrustManager x509ExtendedTrustManager = Arrays.stream(instance.getTrustManagers())
                .filter(X509ExtendedTrustManager.class::isInstance)
                .map(X509ExtendedTrustManager.class::cast)
                .findFirst()
                .orElseThrow();
            SSLContext instance2 = SSLContext.getInstance("TLS");
            instance2.init(null, new TrustManager[]{new X509ExtendedTrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String str, SSLEngine sslEngine) throws CertificateException {
                    x509ExtendedTrustManager.checkClientTrusted(x509Certificates, str, sslEngine);
                }

                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates2, String str2, Socket socket) throws CertificateException {
                    x509ExtendedTrustManager.checkClientTrusted(x509Certificates2, str2, socket);
                }

                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates3, String str3) throws CertificateException {
                    x509ExtendedTrustManager.checkClientTrusted(x509Certificates3, str3);
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates4, String str4, SSLEngine sslEngine2) throws CertificateException {
                    x509ExtendedTrustManager.checkServerTrusted(x509Certificates4, str4, sslEngine2);
                    ClientBootstrapMain.handleX509CertificateArray(x509Certificates4, sslEngine2.getPeerHost());
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates5, String str5, Socket socket2) throws CertificateException {
                    x509ExtendedTrustManager.checkServerTrusted(x509Certificates5, str5, socket2);
                    if (!(socket2 instanceof SSLSocket)) {
                        throw new CertificateException("TLS socket required");
                    }
                    SSLSession handshakeSession = ((SSLSocket)socket2).getHandshakeSession();
                    ClientBootstrapMain.handleX509CertificateArray(x509Certificates5, handshakeSession == null ? null : handshakeSession.getPeerHost());
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates6, String str6) throws CertificateException {
                    throw new CertificateException("TLS peer identity required");
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return x509ExtendedTrustManager.getAcceptedIssuers();
                }
            }}, null);
            return instance2;
        } catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
