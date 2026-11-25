package org.brutality.module.impl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.input.Keyboard;

public class QuickBuy extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final TimerUtil timerUtil = new TimerUtil();
    private final NumberSetting purchaseDelaySetting = new NumberSetting("Purchase Delay", this, 0.0, 100.0, 400.0, 10.0);
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private final KeybindListSetting knockbackStickTurboSetting = new KeybindListSetting(
        "Weapons",
        this,
        new NumberSetting("Stone Sword Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Stone Sword Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Stone Sword Turbo", this, false),
        new NumberSetting("Iron Sword Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Iron Sword Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Iron Sword Turbo", this, false),
        new NumberSetting("Diamond Sword Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Diamond Sword Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Diamond Sword Turbo", this, false),
        new NumberSetting("Knockback Stick Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Knockback Stick Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Knockback Stick Turbo", this, false)
    );
    private final KeybindListSetting shearsTurboSetting = new KeybindListSetting(
        "Tools",
        this,
        new NumberSetting("Pickaxe Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Pickaxe Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Pickaxe Turbo", this, false),
        new NumberSetting("Axe Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Axe Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Axe Turbo", this, false),
        new NumberSetting("Shears Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Shears Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Shears Turbo", this, false)
    );
    private final KeybindListSetting diamondArmorTurboSetting = new KeybindListSetting(
        "Armor",
        this,
        new NumberSetting("Chainmail Armor Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Chainmail Armor Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Chainmail Armor Turbo", this, false),
        new NumberSetting("Iron Armor Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Iron Armor Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Iron Armor Turbo", this, false),
        new NumberSetting("Diamond Armor Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Diamond Armor Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Diamond Armor Turbo", this, false)
    );
    private final KeybindListSetting woolTurboSetting = new KeybindListSetting(
        "Blocks",
        this,
        new NumberSetting("Wool Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Wool Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Wool Turbo", this, false)
    );
    private final KeybindListSetting arrowsTurboSetting = new KeybindListSetting(
        "Consumables",
        this,
        new NumberSetting("Golden Apple Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Golden Apple Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Golden Apple Turbo", this, false),
        new NumberSetting("Fireball Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Fireball Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Fireball Turbo", this, false),
        new NumberSetting("TNT Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("TNT Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("TNT Turbo", this, false),
        new NumberSetting("Ender Pearl Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Ender Pearl Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Ender Pearl Turbo", this, false),
        new NumberSetting("Arrows Key", this, 0.0, 0.0, 255.0, 1.0),
        new ModeSetting("Arrows Slot", this, "Disabled", Arrays.asList("Disabled", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
        new BooleanSetting("Arrows Turbo", this, false)
    );
    private final KeybindListSetting featherFallingKeySetting = new KeybindListSetting(
        "Upgrades",
        this,
        new NumberSetting("Sharpness Key", this, 0.0, 0.0, 255.0, 1.0),
        new NumberSetting("Protection Key", this, 0.0, 0.0, 255.0, 1.0),
        new NumberSetting("Mining Fatigue Key", this, 0.0, 0.0, 255.0, 1.0),
        new NumberSetting("Haste Key", this, 0.0, 0.0, 255.0, 1.0),
        new NumberSetting("Feather Falling Key", this, 0.0, 0.0, 255.0, 1.0)
    );
    private final Map<String, String> lookupMap = new HashMap<>();
    private final Map<String, Integer> lookupMap2 = new HashMap<>();
    private final Map<String, Long> lookupMap3 = new HashMap<>();
    private final Map<String, Boolean> lookupMap4 = new HashMap<>();
    private final List<QuickBuy.Entry> entries = new ArrayList<>();
    private final Set<String> itemSet = new HashSet<>(Arrays.asList("wooden_pickaxe", "iron_pickaxe", "golden_pickaxe", "diamond_pickaxe"));
    private final Set<String> itemSet2 = new HashSet<>(Arrays.asList("wooden_axe", "stone_axe", "iron_axe", "diamond_axe"));
    private final List<QuickBuy.Entry2> entries2 = new ArrayList<>();

    private void sendMessage(String str) {
        try {
            MessageUtils.x(MessageUtils.computeString(str, MessageUtils.text8));
        } catch (Exception e) {
            System.out.println("[QuickBuy] " + str);
        }
    }

    private NumberSetting computeNumberSetting(String str) {
        for (KeybindListSetting keybindListSetting : Arrays.asList(
            this.knockbackStickTurboSetting,
            this.shearsTurboSetting,
            this.diamondArmorTurboSetting,
            this.woolTurboSetting,
            this.arrowsTurboSetting,
            this.featherFallingKeySetting
        )) {
            NumberSetting numberSetting = (NumberSetting)keybindListSetting.computeSetting(str + " Key");
            if (numberSetting != null) {
                return numberSetting;
            }
        }

        return null;
    }

    private void handleEntry2(QuickBuy.Entry2 entry2, long l) {
        NumberSetting numberSetting = this.computeNumberSetting(entry2.text2);
        if (numberSetting != null) {
            int n = (int)numberSetting.computeDouble();
            if (n != 0) {
                boolean keyDown = Keyboard.isKeyDown(n);
                boolean orDefault = this.lookupMap4.getOrDefault(entry2.text, false);
                if (!keyDown) {
                    this.lookupMap4.put(entry2.text, false);
                } else {
                    ModeSetting modeSetting = this.computeModeSetting(entry2.text2);
                    BooleanSetting boolSetting = this.computeBooleanSetting(entry2.text2);
                    int n2 = -1;
                    if (modeSetting != null && !modeSetting.getText().equals("Disabled")) {
                        n2 = Integer.parseInt(modeSetting.getText()) - 1;
                    }

                    boolean flag = boolSetting != null && boolSetting.isEnabled();
                    long l2 = entry2.flag ? 300L : 90L;
                    long orDefault2 = this.lookupMap3.getOrDefault(entry2.text, 0L);
                    if (flag || !orDefault) {
                        if (l - orDefault2 >= l2) {
                            Integer integer = this.lookupMap2.get(entry2.text);
                            if (integer != null) {
                                this.lookupMap3.put(entry2.text, l);
                                this.lookupMap4.put(entry2.text, true);
                                this.entries.add(new QuickBuy.Entry(integer, n2));
                                if (this.debugSetting.isEnabled()) {
                                    this.sendMessage("Queued purchase: " + entry2.text2 + " (slot " + integer + ")");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private BooleanSetting computeBooleanSetting(String str) {
        for (KeybindListSetting keybindListSetting : Arrays.asList(
            this.knockbackStickTurboSetting, this.shearsTurboSetting, this.diamondArmorTurboSetting, this.woolTurboSetting, this.arrowsTurboSetting
        )) {
            BooleanSetting boolSetting = (BooleanSetting)keybindListSetting.computeSetting(str + " Turbo");
            if (boolSetting != null) {
                return boolSetting;
            }
        }

        return null;
    }

    private void handleContainerChest(ContainerChest containerChest, boolean flag, boolean flag2) {
        int sizeInventory = containerChest.getLowerChestInventory().getSizeInventory();
        Iterator it = this.entries2.iterator();

        while (true) {
            QuickBuy.Entry2 entry2;
            String str;
            while (true) {
                if (!it.hasNext()) {
                    return;
                }

                entry2 = (QuickBuy.Entry2)it.next();
                str = entry2.text;
                if (!flag || !entry2.flag) {
                    if (!flag2) {
                        break;
                    }

                    if (entry2.flag) {
                        str = entry2.text.substring(4);
                        break;
                    }
                }
            }

            Set set = null;
            if (flag) {
                if (str.equals("pickaxe")) {
                    set = this.itemSet;
                } else if (str.equals("axe")) {
                    set = this.itemSet2;
                }
            }

            int n = flag ? 18 : 9;
            int n2 = flag ? sizeInventory - 9 : 27;

            for (int i = n; i < n2; i++) {
                ItemStack stack = containerChest.getLowerChestInventory().getStackInSlot(i);
                if (stack != null) {
                    String str2 = Item.itemRegistry.getNameForObject(stack.getItem()).toString().replace("minecraft:", "");
                    if (set != null && set.contains(str2)) {
                        this.lookupMap2.put(entry2.text, i);
                        break;
                    }

                    if (set == null && str2.equals(str)) {
                        this.lookupMap2.put(entry2.text, i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && this.mc.thePlayer != null && this.mc.theWorld != null) {
                EventUpdate event2 = (EventUpdate)event;
                if (event2.getState() == EventState.PRE) {
                    if (!(this.mc.currentScreen instanceof GuiChest gui)) {
                        this.lookupMap2.clear();
                        this.entries.clear();
                    } else {
                        ContainerChest inventorySlots = (ContainerChest)gui.inventorySlots;
                        String text = inventorySlots.getLowerChestInventory().getDisplayName().getUnformattedText();
                        boolean flag = text.equals("Quick Buy");
                        boolean flag2 = text.equals("Upgrades & Traps");
                        if (flag || flag2) {
                            this.handleContainerChest(inventorySlots, flag, flag2);
                            long now = System.currentTimeMillis();

                            for (QuickBuy.Entry2 entry2 : this.entries2) {
                                if ((!flag || !entry2.flag) && (!flag2 || entry2.flag)) {
                                    this.handleEntry2(entry2, now);
                                }
                            }

                            if (this.timerUtil.matchesLong5((long)this.purchaseDelaySetting.computeDouble()) && !this.entries.isEmpty()) {
                                QuickBuy.Entry entry = this.entries.remove(0);
                                if (entry.count2 >= 0) {
                                    this.mc.playerController.windowClick(inventorySlots.windowId, entry.count, entry.count2, 2, this.mc.thePlayer);
                                } else {
                                    this.mc.playerController.windowClick(inventorySlots.windowId, entry.count, 0, 0, this.mc.thePlayer);
                                }

                                this.timerUtil.reset();
                                if (this.debugSetting.isEnabled()) {
                                    String str = entry.count2 >= 0 ? " to hotbar " + (entry.count2 + 1) : "";
                                    int count = entry.count;
                                    this.sendMessage("Clicked slot " + count + str);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public QuickBuy() {
        super("QuickBuy", 0, Category.PLAYER);
        this.process();
        this.process3();
        this.handleSettingArray(
            new Setting[]{
                this.purchaseDelaySetting,
                this.debugSetting,
                this.knockbackStickTurboSetting,
                this.shearsTurboSetting,
                this.diamondArmorTurboSetting,
                this.woolTurboSetting,
                this.arrowsTurboSetting,
                this.featherFallingKeySetting
            }
        );
    }

    private ModeSetting computeModeSetting(String str) {
        for (KeybindListSetting keybindListSetting : Arrays.asList(
            this.knockbackStickTurboSetting, this.shearsTurboSetting, this.diamondArmorTurboSetting, this.woolTurboSetting, this.arrowsTurboSetting
        )) {
            ModeSetting modeSetting = (ModeSetting)keybindListSetting.computeSetting(str + " Slot");
            if (modeSetting != null) {
                return modeSetting;
            }
        }

        return null;
    }

    private void process() {
        this.lookupMap.put("wool", "Wool");
        this.lookupMap.put("stone_sword", "Stone Sword");
        this.lookupMap.put("iron_sword", "Iron Sword");
        this.lookupMap.put("golden_apple", "Golden Apple");
        this.lookupMap.put("fire_charge", "Fireball");
        this.lookupMap.put("tnt", "TNT");
        this.lookupMap.put("ender_pearl", "Ender Pearl");
        this.lookupMap.put("pickaxe", "Pickaxe");
        this.lookupMap.put("axe", "Axe");
        this.lookupMap.put("shears", "Shears");
        this.lookupMap.put("chainmail_boots", "Chainmail Armor");
        this.lookupMap.put("iron_boots", "Iron Armor");
        this.lookupMap.put("diamond_boots", "Diamond Armor");
        this.lookupMap.put("upg iron_sword", "Sharpness");
        this.lookupMap.put("upg iron_chestplate", "Protection");
        this.lookupMap.put("upg iron_pickaxe", "Mining Fatigue");
        this.lookupMap.put("upg golden_pickaxe", "Haste");
        this.lookupMap.put("upg diamond_boots", "Feather Falling");
        this.lookupMap.put("diamond_sword", "Diamond Sword");
        this.lookupMap.put("stick", "Knockback Stick");
        this.lookupMap.put("arrow", "Arrows");
    }

    private void process3() {
        this.entries2.add(new QuickBuy.Entry2("wool", "Wool", false));
        this.entries2.add(new QuickBuy.Entry2("stone_sword", "Stone Sword", false));
        this.entries2.add(new QuickBuy.Entry2("iron_sword", "Iron Sword", false));
        this.entries2.add(new QuickBuy.Entry2("diamond_sword", "Diamond Sword", false));
        this.entries2.add(new QuickBuy.Entry2("stick", "Knockback Stick", false));
        this.entries2.add(new QuickBuy.Entry2("golden_apple", "Golden Apple", false));
        this.entries2.add(new QuickBuy.Entry2("fire_charge", "Fireball", false));
        this.entries2.add(new QuickBuy.Entry2("tnt", "TNT", false));
        this.entries2.add(new QuickBuy.Entry2("ender_pearl", "Ender Pearl", false));
        this.entries2.add(new QuickBuy.Entry2("arrow", "Arrows", false));
        this.entries2.add(new QuickBuy.Entry2("pickaxe", "Pickaxe", false));
        this.entries2.add(new QuickBuy.Entry2("axe", "Axe", false));
        this.entries2.add(new QuickBuy.Entry2("shears", "Shears", false));
        this.entries2.add(new QuickBuy.Entry2("chainmail_boots", "Chainmail Armor", false));
        this.entries2.add(new QuickBuy.Entry2("iron_boots", "Iron Armor", false));
        this.entries2.add(new QuickBuy.Entry2("diamond_boots", "Diamond Armor", false));
        this.entries2.add(new QuickBuy.Entry2("upg iron_sword", "Sharpness", true));
        this.entries2.add(new QuickBuy.Entry2("upg iron_chestplate", "Protection", true));
        this.entries2.add(new QuickBuy.Entry2("upg iron_pickaxe", "Mining Fatigue", true));
        this.entries2.add(new QuickBuy.Entry2("upg golden_pickaxe", "Haste", true));
        this.entries2.add(new QuickBuy.Entry2("upg diamond_boots", "Feather Falling", true));
    }

    public static class Entry {
        public final int count;
        public final int count2;

        public Entry(int count, int n) {
            this.count = count;
            this.count2 = n;
        }
    }

    public static class Entry2 {
        public final String text;
        public final String text2;
        public final boolean flag;

        public Entry2(String text, String str, boolean flag) {
            this.text = text;
            this.text2 = str;
            this.flag = flag;
        }
    }
}
