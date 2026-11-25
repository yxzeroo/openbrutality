package org.brutality.system;

public class AuthCredentials {
    private static final AuthCredentials instance = new AuthCredentials();

    public static AuthCredentials getInstance() {
        return instance;
    }

    public static void process() {
        RuntimeAuthGuard.process2();
    }

    public boolean checkState() {
        return RuntimeAuthGuard.checkState2() && JarIntegrityVerifier.checkState();
    }

    public static boolean checkState2() {
        if (!JarIntegrityVerifier.checkState()) {
            RuntimeAuthGuard.process2();
            return false;
        }
        return RuntimeAuthGuard.checkState5();
    }
}
