package org.brutality.alt.alt;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.brutality.system.AuthHandler;
import org.brutality.system.AuthLoginDialog;
import org.brutality.system.RuntimeAuthGuard;

public class PreAuthGate {
    private static final long timestamp = 120L;

    public static void handleAuthHandler(AuthHandler authHandler) {
        boolean flag = false;

        try {
            if (!authHandler.checkState3()) {
                CompletableFuture future = AuthLoginDialog.authenticate();

                try {
                    flag = (Boolean)future.get(timestamp, TimeUnit.SECONDS);
                } catch (Exception e) {
                    flag = false;
                }
            } else {
                flag = true;
            }
        } catch (Exception e2) {
            flag = false;
        }

        if (!flag) {
            System.exit(0);
        } else if (!RuntimeAuthGuard.checkState5()) {
            System.exit(0);
        } else if (!RuntimeAuthGuard.checkState4()) {
            RuntimeAuthGuard.process2();
        }
    }
}
