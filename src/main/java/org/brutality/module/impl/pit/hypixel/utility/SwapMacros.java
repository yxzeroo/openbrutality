package org.brutality.module.impl.pit.hypixel.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.brutality.clickgui.swap.InventorySwapMacrosScreen;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.player.EventKey;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.player.ActiveHelper;
import org.brutality.utils.player.FindIdentityHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.lwjgl.input.Keyboard;

public class SwapMacros extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static SwapMacros instance;
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 75.0, 0.0, 500.0, 25.0);
    private final BooleanSetting swapBackSetting = new BooleanSetting("Swap Back", this, true);
    private final BooleanSetting safeModeSetting = new BooleanSetting("Safe Mode", this, true);
    private final ModeSetting silentSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Silent"));
    private final List<SwapMacros.Entry> entries = new ArrayList<>();
    private long timestamp;
    private boolean flag;
    private static Object object;
    private static final long timestamp2 = 250L;

    private void handleEntry(SwapMacros.Entry entry) {
        if (FindIdentityHelper.matchesFlag(true) && !entry.activeHelper.checkState() && System.currentTimeMillis() - this.timestamp >= timestamp2) {
            this.timestamp = System.currentTimeMillis();
            if (entry.activeHelper.active()) {
                if (this.swapBackSetting.isEnabled()) {
                    entry.activeHelper.process4();
                }
            } else {
                int n = FindIdentityHelper.findIdentity(entry.text, true);
                if (n < 0) {
                    MessageUtils.x("§c[SwapMacros] Item not found: §f" + entry.text2);
                } else {
                    ArrayList list = new ArrayList();
                    ArrayList list2 = new ArrayList();
                    if (entry.count >= 5 && entry.count <= 8) {
                        list.add(new StartHotbarMovesHelper.Entry3(entry.count - 5, n));
                    } else {
                        if (entry.count < 36 || entry.count > 44) {
                            return;
                        }

                        list2.add(new StartHotbarMovesHelper.Entry(n, entry.count - 36));
                    }

                    entry.activeHelper
                        .handleList(
                            list, list2, this.silentSetting.matchesString2("Silent"), this.safeModeSetting.isEnabled(), (long)this.delaySetting.computeDouble()
                        );
                }
            }
        }
    }

    public static boolean matchesInt(int n) {
        if (instance != null && instance.checkState44()) {
            for (SwapMacros.Entry entry : instance.entries) {
                if (entry.enabled && entry.key == n && n != 0) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    private void save() throws IOException {
        if (this.flag) {
            throw new IOException("InventoryMacros.json could not be read. Repair or rename it before saving macros.");
        }
        Path path = this.computePath();
        Files.createDirectories(path.getParent());
        Path path2 = Files.createTempFile(path.getParent(), "inventory-macros-", ".tmp");

        try {
            Files.write(path2, new GsonBuilder().setPrettyPrinting().create().toJson(this.entries).getBytes(StandardCharsets.UTF_8));

            try {
                Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(path2);
        }
    }

    public static String computeString(int n) {
        return n < 0 ? "Mouse" + (n + 101) : (n == 0 ? "Unbound" : Keyboard.getKeyName(n));
    }

    public void handleEntry(SwapMacros.Entry entry, boolean flag) throws IOException {
        boolean enabled = entry.enabled;
        entry.enabled = flag;

        try {
            this.save();
        } catch (IOException e) {
            entry.enabled = enabled;
            throw e;
        }
    }

    public static SwapMacros getInstance() {
        return instance;
    }

    private Path computePath() {
        return mc.mcDataDir.toPath().resolve("Brutality/InventoryMacros.json");
    }

    public void handleString(String str, int n, int n2) throws IOException {
        ItemStack stack = mc.thePlayer == null ? null : mc.thePlayer.getHeldItem();
        if (stack == null) {
            throw new IllegalArgumentException("Hold the item to bind first.");
        }
        if (str != null && str.matches("[a-zA-Z0-9_-]{1,32}")) {
            if (this.entries.size() >= 64) {
                throw new IllegalArgumentException("Maximum 64 macros.");
            }
            for (SwapMacros.Entry entry : this.entries) {
                if (entry.name.equalsIgnoreCase(str) || n != 0 && entry.key == n) {
                    throw new IllegalArgumentException("That name or key already belongs to a macro.");
                }
            }

            if (n2 >= 5 && n2 <= 8 && (!(stack.getItem() instanceof ItemArmor) || ((ItemArmor)stack.getItem()).armorType != n2 - 5)) {
                throw new IllegalArgumentException("This item does not fit that armor slot.");
            }
            if (n2 >= 5 && n2 <= 8 || n2 >= 36 && n2 <= 44) {
                SwapMacros.Entry entry2 = new SwapMacros.Entry();
                entry2.name = str;
                entry2.key = n;
                entry2.count = n2;
                entry2.text = FindIdentityHelper.computeString(stack);
                entry2.text2 = stack.getDisplayName();
                this.entries.add(entry2);

                try {
                    this.save();
                } catch (IOException e) {
                    this.entries.remove(entry2);
                    throw e;
                }
            } else {
                throw new IllegalArgumentException("Invalid target slot.");
            }
        } else {
            throw new IllegalArgumentException("Use a name with 1-32 letters, numbers, underscores or hyphens.");
        }
    }

    public static void process() {
        if (object != null) {
            Object obj = object;
            object = null;
            if (mc.thePlayer == obj && mc.currentScreen == null) {
                mc.displayGuiScreen(new InventorySwapMacrosScreen());
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof RenderEvent4)) {
            if (mc.currentScreen == null) {
                if (event instanceof EventKey && matchesInt2(((EventKey)event).getKey())) {
                    event.cancel();
                }

                if (event instanceof BasicEvent3 basicEvent3 && basicEvent3.count6 >= 0 && basicEvent3.flag && matchesInt2(basicEvent3.count6 - 100)) {
                    event.cancel();
                }
            }
        } else {
            for (SwapMacros.Entry entry : this.entries) {
                entry.activeHelper.clear();
            }
        }
    }

    public static int computeInt2(String str) {
        String str2 = str.toLowerCase(Locale.ROOT);
        switch (str2) {
            case "helmet":
                return 5;
            case "chest":
            case "chestplate":
                return 6;
            case "pants":
            case "leggings":
                return 7;
            case "boots":
                return 8;
            default:
                int n = Integer.parseInt(str);
                if (n >= 1 && n <= 9) {
                    return n + 35;
                } else {
                    throw new IllegalArgumentException("Target must be helmet, chest, pants, boots, or hotbar 1-9.");
                }
        }
    }

    public static int computeInt(String str) {
        String str2 = str.toUpperCase(Locale.ROOT);
        if (str2.startsWith("MOUSE")) {
            int n = Integer.parseInt(str2.substring(5));
            if (n >= 1 && n <= 16) {
                return n - 101;
            }
            throw new IllegalArgumentException("Mouse button must be 1-16.");
        }
        int keyIndex = Keyboard.getKeyIndex(str2);
        if (keyIndex == 0) {
            throw new IllegalArgumentException("Unknown key: " + str);
        }
        return keyIndex;
    }

    private void load() {
        if (Files.exists(this.computePath())) {
            try {
                SwapMacros.Entry[] entries = new Gson()
                    .fromJson(new String(Files.readAllBytes(this.computePath()), StandardCharsets.UTF_8), SwapMacros.Entry[].class);
                if (entries == null || entries.length > 64) {
                    throw new IllegalArgumentException();
                }

                HashSet set = new HashSet();
                HashSet set2 = new HashSet();

                for (SwapMacros.Entry entry : entries) {
                    if (entry == null
                        || entry.name == null
                        || entry.text == null
                        || entry.text2 == null
                        || !set.add(entry.name.toLowerCase(Locale.ROOT))
                        || entry.key != 0 && !set2.add(entry.key)
                        || (entry.count < 5 || entry.count > 8) && (entry.count < 36 || entry.count > 44)) {
                        throw new IllegalArgumentException();
                    }

                    entry.activeHelper = new ActiveHelper();
                }

                this.entries.addAll(Arrays.asList(entries));
            } catch (Exception e) {
                this.flag = true;
                System.err.println("[SwapMacros] Could not read InventoryMacros.json; original file preserved.");
            }
        }
    }

    public void handleEntry2(SwapMacros.Entry entry) throws IOException {
        int n = this.entries.indexOf(entry);
        if (n >= 0) {
            this.entries.remove(n);

            try {
                this.save();
            } catch (IOException e) {
                this.entries.add(n, entry);
                throw e;
            }

            entry.activeHelper.process4();
        }
    }

    public SwapMacros() {
        super("SwapMacros", 0, Category.PIT);
        instance = this;
        this.handleSettingArray(new Setting[]{this.delaySetting, this.swapBackSetting, this.safeModeSetting, this.silentSetting});
        this.load();
    }

    public static boolean matchesInt2(int n) {
        if (!matchesInt(n)) {
            return false;
        }
        for (SwapMacros.Entry entry : instance.entries) {
            if (entry.enabled && entry.key == n) {
                instance.handleEntry(entry);
                break;
            }
        }

        return true;
    }

    public static void process3() {
        object = mc.thePlayer;
    }

    public List<SwapMacros.Entry> entries() {
        return Collections.unmodifiableList(this.entries);
    }

    public static final class Entry {
        public String name;
        public String text;
        public String text2;
        public int key;
        public int count;
        public boolean enabled = true;
        public transient ActiveHelper activeHelper = new ActiveHelper();
    }
}
