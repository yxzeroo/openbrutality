package org.brutality.utils.misc;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class HardwareAddressUtil {
    public static String computeString() {
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface)networkInterfaces.nextElement();
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if (hardwareAddress != null) {
                    for (byte b : hardwareAddress) {
                        sb.append(String.format("%02X", b));
                    }
                    break;
                }
            }

            sb.append(System.getProperty("os.name"));
            sb.append(System.getProperty("os.arch"));
            return sb.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
    }
}
