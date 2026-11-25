package org.brutality.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;

public class ProxyManager {
    private static final String text = "proxies.json";
    private static List<ProxyConfig> entries = new ArrayList<>();
    private static ProxyConfig proxyConfig = null;
    private static boolean flag = false;
    private static final Map<String, ProxyConfig> lookupMap = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static File computeFile(String name) {
        Minecraft mc = Minecraft.getMinecraft();
        return mc != null && mc.mcDataDir != null ? new File(mc.mcDataDir, name) : new File(name);
    }

    public static void load() {
        process2();
        File file = computeFile("proxies.json");
        if (!file.exists()) {
            entries = new ArrayList<>();
            save();
        } else {
            try (FileReader fileReader = new FileReader(file)) {
                Type type = (new TypeToken<ArrayList<ProxyConfig>>() {}).getType();
                List list = gson.fromJson(fileReader, type);
                entries = (List<ProxyConfig>)(list != null ? list : new ArrayList<>());
            } catch (IOException e) {
                e.printStackTrace();
                entries = new ArrayList<>();
            }
        }
    }

    private static void process2() {
        lookupMap.clear();
        File file = computeFile("ServerProxies.json");
        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                Type type = (new TypeToken<Map<String, ProxyConfig>>() {}).getType();
                Map map = gson.fromJson(fileReader, type);
                if (map != null) {
                    lookupMap.putAll(map);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static void process() {
        try (FileWriter fileWriter = new FileWriter(computeFile("ServerProxies.json"))) {
            gson.toJson(lookupMap, fileWriter);
        } catch (IOException ignored) {
        }
    }

    private static String computeString(String address) {
        return address == null ? "" : address.trim().toLowerCase(Locale.ROOT);
    }

    public static void handleString(String address, ProxyConfig proxy) {
        String str = computeString(address);
        if (!str.isEmpty() && proxy != null) {
            lookupMap.put(str, proxy);
            process();
        }
    }

    public static void handleString(String address) {
        if (lookupMap.remove(computeString(address)) != null) {
            process();
        }
    }

    public static ProxyConfig computeProxyConfig(String address) {
        return lookupMap.get(computeString(address));
    }

    public static Map<String, ProxyConfig> computeMap() {
        return new HashMap<>(lookupMap);
    }

    public static void save() {
        File file = computeFile("proxies.json");

        try (FileWriter fileWriter = new FileWriter(file)) {
            gson.toJson(entries, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ProxyConfig> computeList() {
        return new ArrayList<>(entries);
    }

    public static void handleProxyConfig2(ProxyConfig proxy) {
        entries.add(proxy);
        save();
    }

    public static void handleInt(int index) {
        if (index >= 0 && index < entries.size()) {
            ProxyConfig proxyConfig2 = entries.get(index);
            if (proxyConfig2 == proxyConfig) {
                process4();
            }

            entries.remove(index);
            save();
        }
    }

    public static void handleInt(int index, ProxyConfig proxy) {
        if (index >= 0 && index < entries.size()) {
            if (hasBanWarning(proxy)) {
                proxy.process();
            }

            entries.set(index, proxy);
            save();
        }
    }

    public static boolean hasBanWarning(ProxyConfig proxy) {
        return proxy == null
            ? false
            : proxy.isUsedOnBannedConnection() || entries.stream().anyMatch(proxyConfig -> proxyConfig.isUsedOnBannedConnection() && matchesProxyConfig(proxyConfig, proxy));
    }

    public static void handleProxyConfig3(ProxyConfig proxy) {
        if (proxy != null) {
            proxy.process();

            for (ProxyConfig proxyConfig : entries) {
                if (matchesProxyConfig(proxyConfig, proxy)) {
                    proxyConfig.process();
                }
            }

            boolean flag = false;

            for (ProxyConfig proxyConfig2 : lookupMap.values()) {
                if (matchesProxyConfig(proxyConfig2, proxy)) {
                    proxyConfig2.process();
                    flag = true;
                }
            }

            save();
            if (flag) {
                process();
            }
        }
    }

    private static boolean matchesProxyConfig(ProxyConfig first, ProxyConfig second) {
        return first.getPort() == second.getPort() && first.computeString().equalsIgnoreCase(second.computeString());
    }

    public static void handleProxyConfig(final ProxyConfig proxy) {
        if (hasBanWarning(proxy)) {
            ProxyBanToast.handleString(proxy.getDisplayName());
        }

        if (proxy != null) {
            proxyConfig = proxy;
            flag = true;
            if (proxy.computeSocks4Mode() == ProxyConfig.Socks4Mode.SOCKS4) {
                System.setProperty("socksProxyVersion", "4");
                String str = proxy.computeString2();
                if (!str.isEmpty()) {
                    System.setProperty("java.net.socks.username", str);
                } else {
                    System.clearProperty("java.net.socks.username");
                }

                System.clearProperty("java.net.socks.password");
                Authenticator.setDefault(null);
            } else {
                System.setProperty("socksProxyVersion", "5");
                if (proxy.hasAuth()) {
                    System.setProperty("java.net.socks.username", proxy.getUsername());
                    System.setProperty("java.net.socks.password", proxy.getPassword());
                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(proxy.getUsername(), proxy.getPassword().toCharArray());
                        }
                    });
                } else {
                    System.clearProperty("java.net.socks.username");
                    System.clearProperty("java.net.socks.password");
                    Authenticator.setDefault(null);
                }
            }

            ProxyBanToast.handleString2(proxy.getDisplayName());
        }
    }

    public static void process4() {
        handleString2("manual disconnect");
    }

    public static void handleString2(String reason) {
        if (proxyConfig != null) {
            ProxyBanToast.handleString(proxyConfig.getDisplayName(), reason);
        }

        proxyConfig = null;
        flag = false;
        process5();
    }

    public static ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public static boolean checkState() {
        return flag && proxyConfig != null;
    }

    public static Proxy computeProxy() {
        return checkState() && proxyConfig != null
            ? new Proxy(java.net.Proxy.Type.SOCKS, new InetSocketAddress(proxyConfig.computeString(), proxyConfig.getPort()))
            : null;
    }

    public static void process3() {
        if (checkState() && proxyConfig != null) {
            final ProxyConfig proxyConfig2 = proxyConfig;
            if (proxyConfig2.computeSocks4Mode() == ProxyConfig.Socks4Mode.SOCKS4) {
                System.setProperty("socksProxyVersion", "4");
                String str = proxyConfig2.computeString2();
                if (!str.isEmpty()) {
                    System.setProperty("java.net.socks.username", str);
                } else {
                    System.clearProperty("java.net.socks.username");
                }

                System.clearProperty("java.net.socks.password");
            } else {
                System.setProperty("socksProxyVersion", "5");
                if (proxyConfig2.hasAuth()) {
                    System.setProperty("java.net.socks.username", proxyConfig2.getUsername());
                    System.setProperty("java.net.socks.password", proxyConfig2.getPassword());
                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(proxyConfig2.getUsername(), proxyConfig2.getPassword().toCharArray());
                        }
                    });
                } else {
                    System.clearProperty("java.net.socks.username");
                    System.clearProperty("java.net.socks.password");
                    Authenticator.setDefault(null);
                }
            }
        }
    }

    public static void process5() {
        System.clearProperty("socksProxyVersion");
        System.clearProperty("java.net.socks.username");
        System.clearProperty("java.net.socks.password");
        Authenticator.setDefault(null);
    }

    public static void clear() {
        if (checkState()) {
            process4();
        }

        entries.clear();
        save();
    }

    public static CompletableFuture<Boolean> testProxy(ProxyConfig proxy) {
        return CompletableFuture.<Boolean>supplyAsync(() -> {
            try {
                Socket socket = new Socket();
                socket.setSoTimeout(5000);
                socket.connect(new InetSocketAddress(proxy.computeString(), proxy.getPort()), 5000);
                socket.close();
                return true;
            } catch (Exception e) {
                return false;
            }
        }).exceptionally(t -> false);
    }
}
