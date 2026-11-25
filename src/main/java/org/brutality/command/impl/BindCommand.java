package org.brutality.command.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.brutality.Brutality;
import org.brutality.command.Command;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.mcp.McpApi;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class BindCommand extends Command {
    public static final int count = -1;
    public static final int count2 = -2;
    public static final int count3 = -3;
    public static final int count4 = -4;
    public static final int count5 = -5;
    private static final Map<Integer, Boolean> lookupMap = new HashMap<>();
    private static final Map<Integer, Boolean> lookupMap2 = new HashMap<>();
    public static BindCommand instance;
    private static boolean registered = false;

    private static void handleString2(String str) {
        MessageUtils.x("§8[§c!§8] " + str + "§f. Bound to §fNone");
    }

    @Override
    public List<String> computeList(String[] parts) {
        List list = new ArrayList();
        String str = parts.length > 0 ? parts[parts.length - 1].toLowerCase() : "";
        if (parts.length <= 1) {
            for (Module module : ModuleManager.getEntries2()) {
                if (module.getName().toLowerCase().startsWith(str)) {
                    list.add(module.getName());
                }
            }

            if (Brutality.scriptManager != null) {
                for (String str2 : Brutality.scriptManager.computeList()) {
                    if (str2.toLowerCase().startsWith(str)) {
                        list.add(str2);
                    }
                }
            }

            if ("clear".startsWith(str)) {
                list.add("clear");
            }

            if ("list".startsWith(str)) {
                list.add("list");
            }
        }

        return list;
    }

    private void handleModule(Module module, String str) {
        int n;
        String str2;
        if (!str.equalsIgnoreCase("none") && !str.equalsIgnoreCase("null")) {
            if (!str.toLowerCase().startsWith("mouse") && !str.toLowerCase().startsWith("m")) {
                n = Keyboard.getKeyIndex(str.toUpperCase());
                if (n == 0) {
                    module.handleInt4(0);
                    handleModule(module);
                    handleString(str);
                    return;
                }

                str2 = Keyboard.getKeyName(n);
            } else {
                BindCommand.Entry entry = this.computeEntry(str);
                n = entry.count;
                str2 = entry.text;
                if (entry.flag) {
                    module.handleInt4(0);
                    handleModule(module);
                    return;
                }
            }
        } else {
            n = 0;
            str2 = "None";
        }

        module.handleInt4(n);
        handleModule(module);
        var modules = ModuleManager.computeList(n);
        String str3 = "";
        if (modules != null && modules.size() > 1) {
            StringBuilder sb = new StringBuilder(" §7(Shared with: ");

            for (Module module2 : modules) {
                if (module2 != module) {
                    if (sb.length() > 15) {
                        sb.append(", ");
                    }

                    sb.append(module2.name2);
                }
            }

            sb.append(")");
            str3 = sb.toString();
        }

        String str4 = Brutality.scriptManager != null && Brutality.scriptManager.isScriptModule(module.getName()) ? " §7(script)" : "";
        String name2 = module.name2;
        MessageUtils.x("§8[§aBind§8] §f" + name2 + str4 + " §abound to §f" + str2 + str3);
    }

    public void onTick(EventTick event) {
        HashSet set = new HashSet();

        for (Module module : ModuleManager.getEntries2()) {
            int key = module.getKey();
            if (key < 0 && !set.contains(key)) {
                boolean flag = this.matchesInt(key);
                boolean flag2 = this.matchesInt2(key);
                if (McpApi.mc.currentScreen == null && flag && !flag2) {
                    var modules = ModuleManager.computeList(key);
                    if (modules != null) {
                        for (Module module2 : modules) {
                            if (!module2.isRequiresInGame() || McpApi.mc.thePlayer != null && McpApi.mc.theWorld != null) {
                                module2.process28();
                            }
                        }
                    }
                }

                this.handleInt(key, flag);
                set.add(key);
            }
        }
    }

    public BindCommand() {
        super("Bind", "Binds a module or script to a key", "bind <module/script> <key> | clear | list", "bind", "b");
        instance = this;
        if (!registered) {
            registered = true;
        }
    }

    private Module computeModule(String str) {
        if (Brutality.scriptManager != null) {
            Module module = Brutality.scriptManager.computeModule(str);
            if (module != null) {
                return module;
            }
        }

        for (Module module2 : ModuleManager.getEntries2()) {
            if (module2.getName().equalsIgnoreCase(str)) {
                return module2;
            }
        }

        return null;
    }

    public static String computeString(int n) {
        if (n == 0) {
            return "None";
        }
        switch (n) {
            case -5:
                return "Mouse 5";
            case -4:
                return "Mouse 4";
            case -3:
                return "Mouse Middle";
            case -2:
                return "Mouse Right";
            case -1:
                return "Mouse Left";
            default:
                return n < -10 ? "Mouse " + (-n - 10) : Keyboard.getKeyName(n);
        }
    }

    private static void handleModule(Module module) {
        if (Brutality.scriptManager != null && Brutality.scriptManager.isScriptModule(module.getName())) {
            Brutality.scriptManager.process3();
        }
    }

    public void handleInt2(int n, boolean flag) {
        if (n >= 0) {
            for (int n2 : new int[]{n < 5 ? -n - 1 : -n - 10, n - 100}) {
                boolean flag2 = this.matchesInt2(n2);
                this.handleInt(n2, flag);
                if (flag && !flag2 && McpApi.mc.currentScreen == null) {
                    var modules = ModuleManager.computeList(n2);
                    if (modules != null) {
                        for (Module module : modules) {
                            if (!module.isRequiresInGame() || McpApi.mc.thePlayer != null && McpApi.mc.theWorld != null) {
                                module.process28();
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleInt(int n, boolean flag) {
        if (n >= 0) {
            lookupMap.put(n, flag);
        } else {
            lookupMap2.put(n, flag);
        }
    }

    public static BindCommand getInstance() {
        if (instance == null) {
            instance = new BindCommand();
        }

        return instance;
    }

    static {
        if (instance == null) {
            instance = new BindCommand();
        }
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length >= 2) {
            String str = args[args.length - 1];
            String str2 = String.join(" ", Arrays.copyOfRange(args, 0, args.length - 1));
            Module module = this.computeModule(str2);
            if (module == null) {
                MessageUtils.x("§8[§c!§8] Module/script not found: §f" + str2);
            } else {
                this.handleModule(module, str);
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
            for (Module module2 : ModuleManager.getEntries2()) {
                module2.handleInt4(0);
            }

            if (Brutality.scriptManager != null) {
                Brutality.scriptManager.process3();
            }

            MessageUtils.x("§8[§aBind§8] All keybinds cleared");
        } else if (args.length != 0 && (args.length != 1 || !args[0].equalsIgnoreCase("list"))) {
            MessageUtils.x("§8[§c!§8] Usage: " + this.text);
        } else {
            MessageUtils.x("§8[§aBind§8] Use §f.bind <module|script> <key>§7 (e.g. §f.bind hitfix g§7)");
        }
    }

    private boolean matchesInt(int n) {
        if (n >= 0) {
            return Keyboard.isKeyDown(n);
        }
        if (n >= -100 && n <= -85) {
            return Mouse.isButtonDown(n + 100);
        }
        switch (n) {
            case -5:
                return Mouse.isButtonDown(4);
            case -4:
                return Mouse.isButtonDown(3);
            case -3:
                return Mouse.isButtonDown(2);
            case -2:
                return Mouse.isButtonDown(1);
            case -1:
                return Mouse.isButtonDown(0);
            default:
                return n < -10 ? Mouse.isButtonDown(-n - 10) : false;
        }
    }

    private BindCommand.Entry computeEntry(String str) {
        try {
            String str2 = str.toLowerCase().replaceAll("[^0-9]", "");
            if (str2.isEmpty()) {
                if (str.toLowerCase().contains("left")) {
                    return new BindCommand.Entry(-1, "Mouse Left");
                }
                if (str.toLowerCase().contains("right")) {
                    return new BindCommand.Entry(-2, "Mouse Right");
                }
                if (str.toLowerCase().contains("middle")) {
                    return new BindCommand.Entry(-3, "Mouse Middle");
                }
                handleString2("Invalid mouse button");
                return new BindCommand.Entry(0, "None", true);
            }
            int n = Integer.parseInt(str2);
            switch (n) {
                case 0:
                case 1:
                    return new BindCommand.Entry(-1, "Mouse Left");
                case 2:
                    return new BindCommand.Entry(-2, "Mouse Right");
                case 3:
                    return new BindCommand.Entry(-3, "Mouse Middle");
                case 4:
                    return new BindCommand.Entry(-4, "Mouse 4");
                case 5:
                    return new BindCommand.Entry(-5, "Mouse 5");
                default:
                    if (n > 5) {
                        return new BindCommand.Entry(-n - 10, "Mouse " + n);
                    } else {
                        handleString2("Invalid mouse button: §f" + n);
                        return new BindCommand.Entry(0, "None", true);
                    }
            }
        } catch (NumberFormatException e) {
            handleString2("Invalid mouse button format");
            return new BindCommand.Entry(0, "None", true);
        }
    }

    private static void handleString(String str) {
        MessageUtils.x("§8[§c!§8] Invalid key: §f" + str + "§f. Bound to §fNone");
    }

    private boolean matchesInt2(int n) {
        return n >= 0 ? lookupMap.getOrDefault(n, false) : lookupMap2.getOrDefault(n, false);
    }

    public static final class Entry {
        public final int count;
        public final String text;
        public final boolean flag;

        public Entry(int count, String text, boolean flag) {
            this.count = count;
            this.text = text;
            this.flag = flag;
        }

        public Entry(int n, String str) {
            this(n, str, false);
        }
    }
}
