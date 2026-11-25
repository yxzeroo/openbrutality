package org.brutality.system;

import java.net.InetAddress;
import java.security.MessageDigest;

public class HardwareIdGenerator {
    public static String computeString() {
        try {
            String str = System.getProperty("os.name", "").toLowerCase();
            String str2;
            if (str.contains("win")) {
                str2 = computeString(System.getenv("COMPUTERNAME"))
                    + System.getProperty("user.name", "")
                    + computeString(System.getenv("PROCESSOR_IDENTIFIER"))
                    + computeString(System.getenv("PROCESSOR_LEVEL"));
            } else if (!str.contains("mac") && !str.contains("darwin")) {
                String str3 = computeString2();
                str2 = str3
                    + System.getProperty("user.name", "")
                    + System.getProperty("os.arch", "")
                    + Runtime.getRuntime().availableProcessors()
                    + System.getProperty("user.home", "");
            } else {
                String str4 = computeString2();
                str2 = str4
                    + System.getProperty("user.name", "")
                    + System.getProperty("os.arch", "")
                    + Runtime.getRuntime().availableProcessors()
                    + System.getProperty("user.home", "")
                    + System.getProperty("os.version", "");
            }

            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] bytes = instance.digest(str2.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();

            for (byte b : bytes) {
                String str5 = Integer.toHexString(255 & b);
                if (str5.length() == 1) {
                    sb.append('0');
                }

                sb.append(str5);
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    private static String computeString(String str) {
        return str != null ? str : "";
    }

    private static String computeString2() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            try {
                String str = System.getenv("HOSTNAME");
                if (str != null && !str.isEmpty()) {
                    return str;
                }
            } catch (Exception ignored) {
            }

            return "unknown-host";
        }
    }
}
