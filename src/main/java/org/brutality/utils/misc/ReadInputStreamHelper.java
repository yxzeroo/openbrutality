package org.brutality.utils.misc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadInputStreamHelper {
    public static String readInputStream(InputStream in) {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
