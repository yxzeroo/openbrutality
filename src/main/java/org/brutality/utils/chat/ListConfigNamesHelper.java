package org.brutality.utils.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.Brutality;
import org.brutality.command.CmdManager;
import org.brutality.command.Command;
import org.brutality.command.impl.ToggleCommand;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.utils.modules.AttemptDenickWithRetriesHelper;
import org.brutality.utils.player.PlayerHelper7;

public final class ListConfigNamesHelper {
    private static final String text = ".";

    private static boolean matchesCommand2(Command command) {
        if (command != null && command.name2 != null) {
            String str = command.name2.toLowerCase(Locale.ROOT);
            switch (str) {
                case "friend":
                case "friends":
                case "kos":
                case "truce":
                case "view":
                case "check":
                case "history":
                case "mute":
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    private ListConfigNamesHelper() {
    }

    private static List<String> computeList(String str, List<String> list, String str2, List<String> list2) {
        List list3 = new ArrayList();
        if (list.size() <= 2 && list2 != null && !list2.isEmpty()) {
            if (list.size() == 2 && str2.endsWith(" ")) {
                return list3;
            }
            String str3 = list.size() >= 1 ? ((String)list.get(0)).toLowerCase(Locale.ROOT) : "";

            for (String str4 : list2) {
                if (str4 != null && !str4.isEmpty() && (str3.isEmpty() || str4.toLowerCase(Locale.ROOT).startsWith(str3))) {
                    list3.add(str + " " + str4);
                }
            }

            Collections.sort(list3, String.CASE_INSENSITIVE_ORDER);
            return list3;
        }
        return list3;
    }

    public static boolean matchesString4(String str) {
        if (str != null && !str.isEmpty()) {
            String str2 = str.toLowerCase();
            var commands = CmdManager.getCommands();
            if (commands != null) {
                for (Command command : commands) {
                    if (command.name2 != null && command.name2.toLowerCase().startsWith(str2)) {
                        return true;
                    }

                    if (command.entries != null) {
                        for (String str3 : command.entries) {
                            if (str3 != null && str3.toLowerCase().startsWith(str2)) {
                                return true;
                            }
                        }
                    }
                }
            }

            for (Module module : ModuleManager.getEntries2()) {
                if (module.getName().toLowerCase().startsWith(str2)) {
                    return true;
                }
            }

            return false;
        }
        return true;
    }

    private static List<String> computeList2(String str, String str2, Command command) {
        List list = new ArrayList();
        var list2 = computeList(command);
        if (list2.isEmpty()) {
            return list;
        }
        var list3 = computeList(str2.trim());
        String str3 = "." + str;
        if (!list3.isEmpty()) {
            String str4 = (String)list3.get(0);
            String str5 = computeString(command, str4, list2);
            if (str5 != null) {
                if (matchesCommand(command, str5)) {
                    return list;
                }
                var list4 = computeList(command, str, str5, list3, str2);
                return (List<String>)(list4 != null ? list4 : list);
            }
            if (list3.size() > 1) {
                return list;
            }
            String str6 = str4.toLowerCase(Locale.ROOT);

            for (String str7 : list2) {
                if (str7.toLowerCase(Locale.ROOT).startsWith(str6)) {
                    list.add(str3 + " " + str7);
                }
            }

            Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
            return list;
        }
        for (String str8 : list2) {
            list.add(str3 + " " + str8);
        }

        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    private static String computeString(Command command, String str, List<String> list) {
        if (matchesList(list, str)) {
            return str;
        }
        String str2 = computeString(command, str);
        return matchesList(list, str2) ? str2 : null;
    }

    private static String computeString(Command command, String str) {
        if (command != null && command.name2 != null && str != null) {
            String str2 = command.name2.toLowerCase(Locale.ROOT);
            switch (str2) {
                case "party":
                    if (matchesString(str, "new")) {
                        return "create";
                    }

                    if (matchesString(str, "inv")) {
                        return "invite";
                    }

                    if (matchesString(str, "join")) {
                        return "accept";
                    }
                    break;
                case "mute":
                    if (matchesString(str, "unmute")) {
                        return "remove";
                    }
            }

            return str.toLowerCase(Locale.ROOT);
        }
        return str;
    }

    private static void addScriptSuggestions(List<String> list, String str, String str2) {
        if (Brutality.scriptManager != null) {
            File file = new File(Brutality.scriptManager.text);
            if (file.isDirectory()) {
                File[] files = file.listFiles((file2, str3) -> str3.endsWith(".java"));
                if (files != null) {
                    for (File file3 : files) {
                        String name = file3.getName();
                        if (name.length() > 5) {
                            String str4 = name.substring(0, name.length() - 5);
                            if (str2.isEmpty() || str4.toLowerCase().startsWith(str2.toLowerCase())) {
                                String str5 = str + " " + str4;
                                if (!list.contains(str5)) {
                                    list.add(str5);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean matchesCommand2(Command command, String str) {
        return command != null && command.name2 != null && str != null && command.name2.equalsIgnoreCase(str);
    }

    public static ListConfigNamesHelper.Entry2 computeEntry2(String str) {
        if (str != null && str.startsWith(".")) {
            String str2 = str.substring(".".length());
            if (str2.isEmpty()) {
                return null;
            }
            int n = str2.indexOf(32);
            String str3 = (n >= 0 ? str2.substring(0, n) : str2).trim();
            if (str3.isEmpty()) {
                return null;
            }
            int len = ".".length();
            int n2 = len + str3.length();
            return new ListConfigNamesHelper.Entry2(len, n2, str3, !matchesString4(str3));
        }
        return null;
    }

    private static List<String> computeList(Command command) {
        if (command != null && command.name2 != null) {
            String str = command.name2.toLowerCase();
            switch (str) {
                case "config":
                    return Arrays.asList("load", "save", "folder", "list");
                case "script":
                    return Arrays.asList("load", "unload", "reload", "list", "folder");
                case "denick":
                    return Arrays.asList("list", "clear", "add", "remove");
                case "party":
                    return Arrays.asList("create", "new", "invite", "inv", "accept", "join", "decline", "leave", "ping", "list");
                case "mute":
                    return Arrays.asList("list", "remove", "unmute");
                case "api":
                    return Arrays.asList("set", "remove", "get");
                case "profiler":
                    return Arrays.asList("reset", "report", "compact", "top", "realtime", "entities", "particles", "console", "copy");
                case "friends":
                case "koss":
                    return Arrays.asList("clear");
                case "bind":
                    return Arrays.asList("clear", "list");
                default:
                    return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

    private static Command computeCommand(String str) {
        if (str != null && !str.isEmpty()) {
            var commands = CmdManager.getCommands();
            if (commands == null) {
                return null;
            }
            for (Command command : commands) {
                if (command.name2 != null && command.name2.equalsIgnoreCase(str)) {
                    return command;
                }

                if (command.entries != null) {
                    for (String str2 : command.entries) {
                        if (str2 != null && str2.equalsIgnoreCase(str)) {
                            return command;
                        }
                    }
                }
            }

            return null;
        }
        return null;
    }

    private static List<String> computeList4() {
        List list = new ArrayList();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.theWorld != null) {
            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity != null && playerEntity != mc.thePlayer) {
                    String name = playerEntity.getName();
                    if (name != null && !name.isEmpty()) {
                        list.add(name);
                    }
                }
            }

            Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
            return list;
        }
        return list;
    }

    private static boolean matchesString(String str, String... parts) {
        for (String str2 : parts) {
            if (str2.equalsIgnoreCase(str)) {
                return true;
            }
        }

        return false;
    }

    private static List<String> computeList(Command command, String str, String str2, List<String> list, String str3) {
        if (command != null && command.name2 != null) {
            String str4 = list.isEmpty() ? str2 : (String)list.get(0);
            String str5 = "." + str + " " + str4;
            String str6 = command.name2.toLowerCase(Locale.ROOT);
            switch (str6) {
                case "config":
                    if (matchesString(str2, "load", "save")) {
                        return computeList(str5, list, str3, listConfigNames());
                    }
                    break;
                case "script":
                    if (matchesString(str2, "load")) {
                        return computeList(str5, list, str3, computeList());
                    }

                    if (matchesString(str2, "unload", "reload")) {
                        return computeList(str5, list, str3, computeList5());
                    }
                    break;
                case "denick":
                    if (matchesString(str2, "add")) {
                        return computeList(str5, list, str3, computeList6());
                    }

                    if (matchesString(str2, "remove")) {
                        return computeList(str5, list, str3, computeList2());
                    }
                    break;
                case "mute":
                    if (matchesString(str2, "remove", "unmute")) {
                        return computeList(str5, list, str3, computeList3());
                    }
                    break;
                case "party":
                    if (matchesString(str2, "invite", "inv", "accept", "join")) {
                        return computeList(str5, list, str3, computeList4());
                    }
            }

            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public static List<String> computeList2(String str) {
        List list = new ArrayList();
        if (str != null && str.startsWith(".")) {
            String str2 = str.substring(".".length());
            int n = str2.indexOf(32);
            if (n >= 0) {
                String str3 = str2.substring(0, n).trim();
                String str4 = str2.substring(n + 1);
                if (matchesString(str3)) {
                    return computeList(str3, str4);
                }
                Command command = computeCommand(str3);
                if (command == null) {
                    return list;
                }
                if (command instanceof ToggleCommand) {
                    return computeList(str3, str4, command);
                }
                if (matchesCommand(command)) {
                    return computeList2(str2.substring(0, n), str4);
                }
                if (matchesCommand2(command)) {
                    return computeList(str3, str4);
                }
                return (List<String>)(!computeList(command).isEmpty() ? computeList2(str3, str4, command) : list);
            }
            String str5 = str2.trim();
            if (!str5.isEmpty()) {
                if (matchesString(str5)) {
                    return computeList(str5, "");
                }

                Command command2 = computeCommand(str5);
                if (command2 instanceof ToggleCommand) {
                    return computeList(str5, "", command2);
                }

                if (command2 != null && !matchesCommand(command2) && matchesCommand2(command2, str5) && !computeList(command2).isEmpty()) {
                    return computeList2(str5, "", command2);
                }
            }

            String str6 = str2.toLowerCase().trim();
            int n2 = str6.indexOf(32);
            if (n2 >= 0) {
                str6 = str6.substring(0, n2);
            }

            var commands = CmdManager.getCommands();
            if (commands == null) {
                return list;
            }
            for (Command command3 : commands) {
                String name2 = command3.name2;
                if (name2 != null && (str6.isEmpty() || name2.toLowerCase().startsWith(str6))) {
                    list.add("." + name2);
                }
            }

            return list;
        }
        return list;
    }

    public static ListConfigNamesHelper.Entry computeEntry(String str) {
        if (str != null && str.startsWith(".")) {
            String str2 = str.substring(".".length());
            int n = str2.indexOf(32);
            if (n < 0) {
                return null;
            }
            String str3 = str2.substring(0, n).trim();
            if (!matchesString2(str3)) {
                return null;
            }
            String str4 = str2.substring(n + 1);
            var list = computeList(str4);
            if (list.isEmpty()) {
                return null;
            }
            String str5 = "";
            boolean flag = false;

            for (int i = 0; i < list.size() - 1; i++) {
                str5 = str5.isEmpty() ? (String)list.get(i) : str5 + " " + (String)list.get(i);
                if (matchesString3(str5)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                str5 = String.join(" ", list.subList(0, list.size() - 1));
            }

            int n2 = ".".length() + n + 1;
            int n3 = str.toLowerCase().indexOf(str5.toLowerCase(), n2);
            if (n3 < 0) {
                n3 = n2;
            }

            int n4 = n3 + str5.length();
            flag = matchesString3(str5);
            return new ListConfigNamesHelper.Entry(n3, n4, str5, flag);
        }
        return null;
    }

    private static List<String> computeList(String str, String str2) {
        var list = computeList(str2.trim());
        String str3 = "." + str;
        if (list.isEmpty()) {
            return computeList(str3, list, str2, computeList6());
        }
        if (((String)list.get(0)).equalsIgnoreCase("group") && !str.equalsIgnoreCase("truce")) {
            if (list.size() == 1) {
                return Collections.emptyList();
            }
            return list.size() == 2 && !str2.endsWith(" ") ? computeList(str3 + " group", list, str2, computeList6()) : Collections.emptyList();
        }
        return list.size() == 1 && !str2.endsWith(" ") ? computeList(str3, list, str2, computeList6()) : Collections.emptyList();
    }

    public static String computeString(String str, List<String> list, int n) {
        if (str != null && list != null && !list.isEmpty()) {
            String str2 = str.toLowerCase();
            if (n >= 0 && n < list.size()) {
                String str3 = (String)list.get(n);
                if (str3.toLowerCase().startsWith(str2) && str3.length() > str.length()) {
                    return str3;
                }
            }

            for (String str4 : list) {
                if (str4.toLowerCase().startsWith(str2) && str4.length() > str.length()) {
                    return str4;
                }
            }

            return null;
        }
        return null;
    }

    private static List<String> computeList() {
        if (Brutality.scriptManager == null) {
            return Collections.emptyList();
        }
        List list = new ArrayList();

        for (String availableScriptFile : Brutality.scriptManager.getAvailableScriptFiles()) {
            if (availableScriptFile != null && !availableScriptFile.isEmpty()) {
                if (availableScriptFile.endsWith(".java")) {
                    availableScriptFile = availableScriptFile.substring(0, availableScriptFile.length() - 5);
                }

                list.add(availableScriptFile);
            }
        }

        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    private static List<String> computeList6() {
        List list = new ArrayList();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null) {
            String name = mc.thePlayer.getName();
            if (mc.getNetHandler() != null) {
                for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    if (playerInfo != null && playerInfo.getGameProfile() != null) {
                        String name2 = playerInfo.getGameProfile().getName();
                        if (name2 != null && !name2.isEmpty() && !name2.equalsIgnoreCase(name)) {
                            list.add(name2);
                        }
                    }
                }
            }

            if (list.isEmpty() && mc.theWorld != null) {
                return computeList4();
            }
            Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
            return list;
        }
        return list;
    }

    public static boolean matchesString3(String str) {
        if (str != null && !str.isEmpty()) {
            if (Brutality.scriptManager != null && Brutality.scriptManager.computeModule(str) != null) {
                return true;
            }
            for (Module module : ModuleManager.getEntries2()) {
                if (module.getName().equalsIgnoreCase(str)) {
                    return true;
                }
            }

            if (Brutality.scriptManager != null) {
                File file = new File(Brutality.scriptManager.text, str + ".java");
                if (file.isFile()) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    private static boolean matchesList(List<String> list, String str) {
        if (str != null && !str.isEmpty()) {
            for (String str2 : list) {
                if (str2.equalsIgnoreCase(str)) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    private static List<String> listConfigNames() {
        List list = new ArrayList();
        File file = computeFile();
        if (file != null && file.isDirectory()) {
            File[] files = file.listFiles((file2, str) -> str.endsWith(".json"));
            if (files == null) {
                return list;
            }
            for (File file3 : files) {
                String name = file3.getName();
                if (name.length() > 5) {
                    list.add(name.substring(0, name.length() - 5));
                }
            }

            Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
            return list;
        }
        return list;
    }

    private static List<String> computeList2() {
        LinkedHashSet linkedHashSet = new LinkedHashSet();

        for (AttemptDenickWithRetriesHelper.Entry entry : AttemptDenickWithRetriesHelper.computeCollection()) {
            if (entry.text2 != null && !entry.text2.isEmpty()) {
                linkedHashSet.add(entry.text2);
            }
        }

        List list = new ArrayList(linkedHashSet);
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    private static boolean matchesString(String str) {
        Command command = computeCommand(str);
        return command != null && matchesCommand2(command);
    }

    private static List<String> computeList3() {
        List list = new ArrayList<>(PlayerHelper7.getLookupMap().values());
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    public static String computeString(List<String> list) {
        if (list != null && !list.isEmpty()) {
            String str = (String)list.get(0);

            for (int i = 1; i < list.size(); i++) {
                String str2 = (String)list.get(i);
                int n = Math.min(str.length(), str2.length());
                int n2 = 0;

                while (n2 < n && str.charAt(n2) == str2.charAt(n2)) {
                    n2++;
                }

                str = str.substring(0, n2);
                if (str.isEmpty()) {
                    break;
                }
            }

            return str;
        }
        return "";
    }

    private static boolean matchesCommand(Command command) {
        if (command == null || command.name2 == null) {
            return false;
        }
        return command.name2.equalsIgnoreCase("bind") ? true : command.entries != null && (command.entries.contains("bind") || command.entries.contains("b"));
    }

    public static List<String> computeList(String str) {
        List list = new ArrayList();
        if (str != null && !str.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean flag = false;

            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch == '"') {
                    flag = !flag;
                } else if (flag || ch != ' ') {
                    sb.append(ch);
                } else if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
            }

            if (sb.length() > 0 || flag) {
                list.add(sb.toString());
            }

            return list;
        }
        return list;
    }

    private static List<String> computeList5() {
        if (Brutality.scriptManager == null) {
            return Collections.emptyList();
        }
        List list = new ArrayList<>(Brutality.scriptManager.computeList());
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    private static boolean matchesCommand(Command command, String str) {
        if (command != null && command.name2 != null && str != null) {
            String str2 = command.name2.toLowerCase(Locale.ROOT);
            switch (str2) {
                case "config":
                    return matchesString(str, "list", "folder");
                case "script":
                    return matchesString(str, "list", "folder");
                case "denick":
                    return matchesString(str, "list", "clear");
                case "party":
                    return matchesString(str, "create", "new", "decline", "leave", "ping", "list");
                case "mute":
                    return matchesString(str, "list");
                case "api":
                    return matchesString(str, "set", "remove", "get");
                case "profiler":
                    return matchesString(str, "reset", "report", "compact", "realtime", "entities", "particles", "console", "copy");
                case "friends":
                case "koss":
                    return matchesString(str, "clear");
                case "bind":
                    return matchesString(str, "clear", "list");
                default:
                    return true;
            }
        } else {
            return true;
        }
    }

    private static List<String> computeList2(String str, String str2) {
        List list = new ArrayList();
        var list2 = computeList(str2);
        if (!matchesString(str2, list2)) {
            return list;
        }
        String str3 = list2.isEmpty() ? "" : (String)list2.get(list2.size() - 1);
        String str4 = "." + str;

        for (Module module : ModuleManager.getEntries2()) {
            String name = module.getName();
            if (name != null && (str3.isEmpty() || name.toLowerCase().startsWith(str3.toLowerCase()))) {
                list.add(str4 + " " + name);
            }
        }

        addScriptSuggestions(list, str4, str3);
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    private static File computeFile() {
        if (Brutality.instance != null && Brutality.appdataHandler != null) {
            return Brutality.appdataHandler.computeFile();
        }
        return Brutality.appdataHandler != null ? Brutality.appdataHandler.computeFile() : null;
    }

    private static boolean matchesString2(String str) {
        return str != null && (str.equalsIgnoreCase("bind") || str.equalsIgnoreCase("b"));
    }

    private static boolean matchesString(String str, List<String> list) {
        if (list.isEmpty()) {
            return true;
        }
        if (list.size() >= 2) {
            return false;
        }
        String str2 = (String)list.get(0);
        return matchesString3(str2) ? false : !str.endsWith(" ");
    }

    private static List<String> computeList(String str, String str2, Command command) {
        List list = new ArrayList();

        for (String str3 : command.computeList(str2.split(" ", -1))) {
            list.add("." + str + " " + str3);
        }

        return list;
    }

    public static final class Entry {
        public final int count;
        public final int count2;
        public final String text;
        public final boolean in;

        public boolean checkState() {
            return this.text != null && !this.text.isEmpty();
        }

        public Entry(int count, int n, String text, boolean in) {
            this.count = count;
            this.count2 = n;
            this.text = text;
            this.in = in;
        }
    }

    public static final class Entry2 {
        public final int count;
        public final int count2;
        public final String text;
        public final boolean flag;

        public boolean checkState() {
            return this.text != null && !this.text.isEmpty();
        }

        public Entry2(int count, int n, String text, boolean flag) {
            this.count = count;
            this.count2 = n;
            this.text = text;
            this.flag = flag;
        }
    }
}
