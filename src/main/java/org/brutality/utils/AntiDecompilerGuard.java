package org.brutality.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.brutality.bootstrap.PinnedTlsHttpClient;

public class AntiDecompilerGuard {
    private static boolean flag = false;
    private static final List<String> z = new CopyOnWriteArrayList<>();
    private static final List<String> entries = new CopyOnWriteArrayList<>();
    private static final String text = "https://brutality-backend-production.up.railway.app";
    private static final String text2 = "brutality_client_api_key_2024_super_secret_32_chars_minimum";
    private static final String[][] stringArray = new String[][]{
        {"recaf", "Recaf"},
        {"jd-gui", "JD-GUI"},
        {"jadx-gui", "JADX-GUI"},
        {"bytecodeviewer", "Bytecode Viewer"},
        {"luyten", "Luyten"},
        {"ghidra", "Ghidra"},
        {"ghidraRun", "Ghidra"},
        {"dnspy", "dnSpy"},
        {"ilspy", "ILSpy"},
        {"helios", "Helios"},
        {"enigma", "Enigma"},
        {"classyshark", "ClassyShark"},
        {"jadec", "JaDec"},
        {"decompiler", "Java Decompiler"},
        {"recompiler", "Recompiler"},
        {"jdec", "JDEC"},
        {"cfr", "CFR"},
        {"procyon", "Procyon"}
    };
    private static final String[][] stringArray2 = new String[][]{
        {"recaf", "Recaf (JAR)"},
        {"jd-gui", "JD-GUI (JAR)"},
        {"jadx", "JADX (JAR)"},
        {"bytecodeviewer", "Bytecode Viewer (JAR)"},
        {"cfr", "CFR (JAR)"},
        {"procyon", "Procyon (JAR)"},
        {"fernflower", "Fernflower"},
        {"vineflower", "Vineflower"},
        {"luyten", "Luyten (JAR)"},
        {"krakatau", "Krakatau"},
        {"helios", "Helios (JAR)"},
        {"enigma", "Enigma (JAR)"},
        {"classyshark", "ClassyShark (JAR)"},
        {"jd-core", "JD-Core"},
        {"java-decompiler", "Java Decompiler"},
        {"threadweaver", "ThreadWeaver"},
        {"jode", "JODE"},
        {"cavaj", "Cavaj"},
        {"recompiler", "Recompiler (JAR)"},
        {"jadec", "JaDec (JAR)"}
    };
    private static final String[][] stringArray3 = new String[][]{
        {"recaf", "Recaf (Window)"},
        {"jd-gui", "JD-GUI (Window)"},
        {"jadx", "JADX (Window)"},
        {"bytecode viewer", "Bytecode Viewer (Window)"},
        {"luyten", "Luyten (Window)"},
        {"ghidra", "Ghidra (Window)"},
        {"dnspy", "dnSpy (Window)"},
        {"ilspy", "ILSpy (Window)"},
        {"helios", "Helios (Window)"},
        {"enigma", "Enigma (Window)"},
        {"classyshark", "ClassyShark (Window)"},
        {"fernflower", "Fernflower (Window)"},
        {"procyon", "Procyon (Window)"},
        {"java decompiler", "Java Decompiler (Window)"},
        {"krakatau", "Krakatau (Window)"},
        {"vineflower", "Vineflower (Window)"}
    };

    public static void process() {
        z.clear();
        entries.clear();

        for (String[] parts : stringArray) {
            String str = parts[0];
            String str2 = parts[1];
            if (matchesString2(str)) {
                if (!z.contains(str2)) {
                    z.add(str2);
                }

                if (matchesString(str)) {
                    entries.add(str2);
                }
            }
        }

        for (AntiDecompilerGuard.Entry entry : computeList()) {
            String str3 = entry.text2.toLowerCase();

            for (String[] parts2 : stringArray2) {
                String str4 = parts2[0];
                String str5 = parts2[1];
                if (str3.contains(str4) && !z.contains(str5)) {
                    z.add(str5);
                    z(entry.text);
                    entries.add(str5);
                }
            }
        }

        for (String[] parts3 : computeList4()) {
            String str6 = parts3[1].toLowerCase();

            for (String[] parts4 : stringArray3) {
                String str7 = parts4[0];
                String str8 = parts4[1];
                if (str6.contains(str7) && !z.contains(str8)) {
                    z.add(str8);
                    z(parts3[0]);
                    entries.add(str8);
                }
            }
        }

        flag = !entries.isEmpty();
        if (flag) {
        }
    }

    private static String[] computeStringArray(String str) {
        List<String> list = new ArrayList<>();
        boolean flag = false;
        StringBuilder sb = new StringBuilder();

        for (char ch : str.toCharArray()) {
            if (ch == '"') {
                flag = !flag;
                sb.append(ch);
            } else if (ch == ',' && !flag) {
                list.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(ch);
            }
        }

        list.add(sb.toString());
        return list.toArray(new String[0]);
    }

    public static boolean isFlag() {
        return flag;
    }

    public static List<String> computeList3() {
        return new ArrayList<>(entries);
    }

    private static String computeString(Process process) throws Exception {
        String str;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder sb = new StringBuilder();

            String str2;
            while ((str2 = reader.readLine()) != null) {
                sb.append(str2).append("\n");
            }

            str = sb.toString();
        }

        return str;
    }

    private static boolean matchesString2(String str) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("tasklist", "/FI", "IMAGENAME eq " + str + ".exe", "/NH");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            String str2 = computeString(process);
            return str2.toLowerCase().contains(str.toLowerCase() + ".exe");
        } catch (Exception e) {
            return false;
        }
    }

    public static void reportFlagToServer(String str, String str2, String str3) {
        if (!z.isEmpty()) {
            List list = new ArrayList<>(z);
            String str4 = str != null ? str : "unknown";
            String str5 = str2 != null ? str2 : "unknown";
            String str6 = str3 != null ? str3 : "unknown";
            Thread thread = new Thread(
                () -> {
                    try {
                        String str7 = computeString(list);
                        String str8 = computeString(str4);
                        String str9 = computeString(str5);
                        String str10 = computeString(str6);
                        String str11 = computeString(System.getProperty("os.name", "unknown"));
                        String str12 = str10;
                        String str13 = str9;
                        String str14 = str8;
                        String str15 = "{\"username\":"
                            + str14
                            + ",\"hwid\":"
                            + str13
                            + ",\"license_key\":"
                            + str12
                            + ",\"tools\":"
                            + str7
                            + ",\"os\":"
                            + str11
                            + "}";
                        URL url = new URL("https://brutality-backend-production.up.railway.app/api/auth/flag");
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        PinnedTlsHttpClient.handleHttpURLConnection(conn);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("X-API-Key", "brutality_client_api_key_2024_super_secret_32_chars_minimum");
                        conn.setConnectTimeout(5000);
                        conn.setReadTimeout(5000);
                        conn.setDoOutput(true);

                        try (OutputStream outputStream = conn.getOutputStream()) {
                            outputStream.write(str15.getBytes(StandardCharsets.UTF_8));
                        }

                        int responseCode = conn.getResponseCode();
                    } catch (Exception ignored) {
                    }
                },
                "Brutality-FlagReport"
            );
            thread.setDaemon(true);
            thread.start();
        }
    }

    private static boolean matchesString(String str) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "/F", "/IM", str + ".exe");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int n = process.waitFor();
            Thread.sleep(150L);
            return n == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static String computeString(List<String> list) {
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }

            sb.append(computeString((String)list.get(i)));
        }

        return sb.append("]").toString();
    }

    public static String computeString() {
        return entries.isEmpty() ? "No conflicting processes were closed." : "Closed " + String.join(", ", entries) + " before launch.";
    }

    public static boolean checkState() {
        return !z.isEmpty();
    }

    private static List<String[]> computeList4() {
        List list = new ArrayList();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("tasklist", "/V", "/FO", "CSV", "/NH");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            String str = computeString(process);

            for (String str2 : str.split("\n")) {
                String str3 = str2.trim();
                if (!str3.isEmpty()) {
                    String[] parts = computeStringArray(str3);
                    if (parts.length >= 9) {
                        String str4 = parts[1].replaceAll("\"", "").trim();
                        String str5 = parts[8].replaceAll("\"", "").trim();
                        if (!str5.isEmpty() && !str5.equalsIgnoreCase("N/A")) {
                            list.add(new String[]{str4, str5});
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return list;
    }

    private static String computeString(String str) {
        return str == null ? "null" : "\"" + str.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static void z(String str) {
        try {
            if (str == null || str.trim().isEmpty()) {
                return;
            }

            new ProcessBuilder("taskkill", "/F", "/PID", str.trim()).redirectErrorStream(true).start().waitFor();
            Thread.sleep(150L);
        } catch (Exception ignored) {
        }
    }

    private static List<AntiDecompilerGuard.Entry> computeList() {
        List list = new ArrayList();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("wmic", "process", "where", "name='java.exe'", "get", "ProcessId,CommandLine", "/format:csv");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            String str = computeString(process);

            for (String str2 : str.split("\n")) {
                String str3 = str2.trim();
                if (!str3.isEmpty() && !str3.startsWith("Node")) {
                    String[] parts = str3.split(",", 3);
                    if (parts.length >= 3) {
                        String str4 = parts[1].trim();
                        String str5 = parts[2].trim();
                        if (!str4.isEmpty()) {
                            list.add(new AntiDecompilerGuard.Entry(str5, str4));
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return list;
    }

    public static List<String> computeList2() {
        return new ArrayList<>(z);
    }

    public static class Entry {
        public String text;
        public String text2;

        public Entry(String text, String str) {
            this.text = text;
            this.text2 = str;
        }
    }
}
