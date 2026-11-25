package org.brutality.launcher.internal;

import java.io.IOException;

final class PayloadBootstrapException extends IOException {
    PayloadBootstrapException(String str) {
        super(str);
    }

    static PayloadBootstrapException computePayloadBootstrapException(int n, String str) {
        String str2;
        if (str == null) {
            str2 = null;
        } else {
            switch (str) {
                case "INVALID_KEY":
                    str2 = "License key was not accepted. Download using your current account key.";
                    break;
                case "KEY_BANNED":
                    str2 = "This license has been banned. Contact support.";
                    break;
                case "KEY_EXPIRED":
                    str2 = "This license has expired. Renew it before launching.";
                    break;
                case "KEY_COMBINED":
                    str2 = "This key was already combined. Download using the receiving account key.";
                    break;
                case "AUTH_UNAVAILABLE":
                    str2 = "License verification service is unavailable. Try again later or contact support.";
                    break;
                default:
                    str2 = null;
            }
        }

        String str3 = str2;
        return str3 == null
            ? computePayloadBootstrapException("Payload authorization", n)
            : new PayloadBootstrapException("Payload authorization (HTTP " + n + "): " + str3);
    }

    static PayloadBootstrapException computePayloadBootstrapException(String str, int n) {
        String str2 = switch (n) {
            case 403 -> "Access denied. Check your license or download the client again.";
            case 423 -> "Downloads are temporarily locked. Try again later.";
            case 503 -> "The server cannot provide the signed payload. Check its R2 file and configuration.";
            default -> "The server rejected the request.";
        };
        return new PayloadBootstrapException(str + " (HTTP " + n + "): " + str2);
    }
}
