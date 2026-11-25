package org.brutality.alt.altmanager.shop;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.Session;
import org.brutality.alt.altmanager.auth.MicrosoftAuthenticator;
import org.brutality.alt.altmanager.auth.XboxLiveAuthHelper;

public final class LocaltsCookieImporter {
    private static final long timestamp = 120L;

    private LocaltsCookieImporter() {
    }

    public static String computeString(String str) {
        String str2 = null;

        for (String str3 : str.split("[\\s:]+")) {
            if (str3.startsWith("M.") && (str2 == null || str3.length() > str2.length())) {
                str2 = str3;
            }
        }

        return str2;
    }

    public static LocaltsCookieImporter.Il1IEntry computeIl1IEntry(String str, String str2) throws Exception {
        String str3 = str2.toLowerCase(Locale.ROOT);
        if (!str3.contains("cookie") && !str.contains("login.live.com") && !str.trim().startsWith("[{")) {
            String str4 = computeString(str);
            if (str4 != null) {
                MicrosoftAuthenticator.Entry entry = MicrosoftAuthenticator.loginWithRefreshToken(str4, ForkJoinPool.commonPool())
                    .get(timestamp, TimeUnit.SECONDS);
                return new LocaltsCookieImporter.Il1IEntry(entry.session, entry.text);
            }
            String[] parts = str.trim().split(":", 2);
            if (parts.length == 2 && parts[0].contains("@") && !parts[1].isEmpty()) {
                MicrosoftAuthResult microsoftAuthResult = new fr.litarvan.openauth.microsoft.MicrosoftAuthenticator().loginWithCredentials(parts[0], parts[1]);
                return new LocaltsCookieImporter.Il1IEntry(
                    new Session(microsoftAuthResult.getProfile().getName(), microsoftAuthResult.getProfile().getId(), microsoftAuthResult.getAccessToken(), "microsoft"), microsoftAuthResult.getRefreshToken()
                );
            }
            throw new IOException("This delivery format needs manual import. The order has been saved.");
        }
        XboxLiveAuthHelper.Entry entry2 = XboxLiveAuthHelper.computeEntry(XboxLiveAuthHelper.computeString5(str));
        if (!entry2.flag) {
            throw new IOException("Cookie login failed. Your delivered account has been saved.");
        }
        return new LocaltsCookieImporter.Il1IEntry(new Session(entry2.username, entry2.text, entry2.text2, "mojang"), "");
    }

    public record Il1IEntry(Session Il1I, String II1llI) {
    }
}
