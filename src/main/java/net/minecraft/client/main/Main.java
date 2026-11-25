package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.Session;
import org.brutality.automation.navigation.Log4jSecurityBootstrap;
import org.brutality.management.NativesExtractor; // VENDOR-DISABLED: import org.brutality.management.ClientPreAuthBootstrap;

public class Main {
    public static void main(String[] stringArray) {
        try {
            Log4jSecurityBootstrap.handleString(Main.class.getName(), stringArray);
            Log4jSecurityBootstrap.process();
        }
        catch (Exception exception) {
            System.err.println("Brutality could not start. Your launch profile has obsolete or unidentified Log4j. Reinstall the latest Brutality ZIP/profile or update the Brutality launcher.");
            System.exit(1);
            return;
        }
        NativesExtractor.extractNatives(); // VENDOR-DISABLED: ClientPreAuthBootstrap.process2();
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        optionParser.accepts("demo");
        optionParser.accepts("fullscreen");
        optionParser.accepts("checkGlErrors");
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec = optionParser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> argumentAcceptingOptionSpec2 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<File> argumentAcceptingOptionSpec3 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), (File[])new File[0]);
        ArgumentAcceptingOptionSpec<File> argumentAcceptingOptionSpec4 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> argumentAcceptingOptionSpec5 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec6 = optionParser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> argumentAcceptingOptionSpec7 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", (String[])new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec8 = optionParser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec9 = optionParser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec10 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L, (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec11 = optionParser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec12 = optionParser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec13 = optionParser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<Integer> argumentAcceptingOptionSpec14 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<Integer> argumentAcceptingOptionSpec15 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec16 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec17 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec18 = optionParser.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> argumentAcceptingOptionSpec19 = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy", (String[])new String[0]);
        NonOptionArgumentSpec<String> nonOptionArgumentSpec = optionParser.nonOptions();
        OptionSet optionSet = optionParser.parse(stringArray);
        List<String> list = optionSet.valuesOf(nonOptionArgumentSpec);
        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }
        String string = optionSet.valueOf(argumentAcceptingOptionSpec6);
        Proxy proxy = Proxy.NO_PROXY;
        if (string != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(string, (int)optionSet.valueOf(argumentAcceptingOptionSpec7)));
            }
            catch (Exception exception) {
            }
        }
        final String string2 = optionSet.valueOf(argumentAcceptingOptionSpec8);
        final String string3 = optionSet.valueOf(argumentAcceptingOptionSpec9);
        if (!proxy.equals(Proxy.NO_PROXY) && Main.isNullOrEmpty(string2) && Main.isNullOrEmpty(string3)) {
            Authenticator.setDefault(new Authenticator(){

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(string2, string3.toCharArray());
                }
            });
        }
        int n = optionSet.valueOf(argumentAcceptingOptionSpec14);
        int n2 = optionSet.valueOf(argumentAcceptingOptionSpec15);
        boolean bl = optionSet.has("fullscreen");
        boolean bl2 = optionSet.has("checkGlErrors");
        boolean bl3 = optionSet.has("demo");
        String string4 = optionSet.valueOf(argumentAcceptingOptionSpec13);
        Gson gson = new GsonBuilder().registerTypeAdapter((Type)((Object)PropertyMap.class), new PropertyMap.Serializer()).create();
        PropertyMap propertyMap = gson.fromJson(optionSet.valueOf(argumentAcceptingOptionSpec16), PropertyMap.class);
        PropertyMap propertyMap2 = gson.fromJson(optionSet.valueOf(argumentAcceptingOptionSpec17), PropertyMap.class);
        File file = optionSet.valueOf(argumentAcceptingOptionSpec3);
        File file2 = optionSet.has(argumentAcceptingOptionSpec4) ? optionSet.valueOf(argumentAcceptingOptionSpec4) : new File(file, "assets/");
        File file3 = optionSet.has(argumentAcceptingOptionSpec5) ? optionSet.valueOf(argumentAcceptingOptionSpec5) : new File(file, "resourcepacks/");
        String string5 = optionSet.has(argumentAcceptingOptionSpec11) ? (String)argumentAcceptingOptionSpec11.value(optionSet) : (String)argumentAcceptingOptionSpec10.value(optionSet);
        String string6 = optionSet.has(argumentAcceptingOptionSpec18) ? (String)argumentAcceptingOptionSpec18.value(optionSet) : null;
        String string7 = optionSet.valueOf(argumentAcceptingOptionSpec);
        Integer n3 = optionSet.valueOf(argumentAcceptingOptionSpec2);
        Session session = new Session((String)argumentAcceptingOptionSpec10.value(optionSet), string5, (String)argumentAcceptingOptionSpec12.value(optionSet), (String)argumentAcceptingOptionSpec19.value(optionSet));
        GameConfiguration gameConfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertyMap, propertyMap2, proxy), new GameConfiguration.DisplayInformation(n, n2, bl, bl2), new GameConfiguration.FolderInformation(file, file3, file2, string6), new GameConfiguration.GameInformation(bl3, string4), new GameConfiguration.ServerInformation(string7, n3));
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread"){

            @Override
            public void run() {
                Minecraft.stopIntegratedServer();
            }
        });
        Thread.currentThread().setName("Client thread");
        new Minecraft(gameConfiguration).run();
    }

    private static boolean isNullOrEmpty(String string) {
        return string != null && !string.isEmpty();
    }
}
