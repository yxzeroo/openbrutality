package org.brutality.launcher.internal;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ManifestJsonParser {
    private ManifestJsonParser() {
    }

    static String computeString(String str, String str2) {
        Matcher matcher = Pattern.compile("\\\"" + Pattern.quote(str2) + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(str);
        return matcher.find() ? matcher.group(1) : null;
    }

    static String computeString(String str) {
        return "\"" + str.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    static String b(String str, String str2) throws IOException {
        String str3 = computeString(str, str2);
        if (str3 != null && !str3.isBlank()) {
            return str3;
        }
        throw new PayloadBootstrapException("The server returned an incomplete payload manifest.");
    }
}
