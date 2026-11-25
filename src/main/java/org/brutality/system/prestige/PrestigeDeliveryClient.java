package org.brutality.system.prestige;

import java.io.File;
import org.brutality.system.BackendAuthService;

public final class PrestigeDeliveryClient {
    private PrestigeDeliveryClient() {
    }

    public static boolean matchesInt(int newPrestige, int delta, String minecraftUsername, String serverIp, String brutalityUsername, int previous, String chatLine, File screenshot, int totalPrestiges) {
        return BackendAuthService.getInstance().computePrestigeWebhookResult(newPrestige, previous, delta, minecraftUsername, serverIp).checkState();
    }
}
