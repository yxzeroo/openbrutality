package org.brutality.mint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.StringUtils;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.mellow.util.UnknownHelper;
import org.brutality.mellow.util.UtilHelper4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;

public class ResourceTracker extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static int count = 0;
    public static int count2 = 0;
    public static int count3 = 0;
    public static int count4 = 0;
    public static int count5 = 0;
    public static int count6 = 0;
    public static int count7 = 0;
    public static int count8 = 0;
    private static boolean active = false;
    private static Object object = null;
    private static ResourceTracker instance;
    private static int count9 = 0;
    private static int count10 = 0;
    private static int count11 = 0;
    private static int count12 = 0;
    private static boolean flag = false;
    private static int count13 = 0;
    private final BooleanSetting enabled = new BooleanSetting("Enabled", this, true);
    private final ModeSetting horizontalSetting = new ModeSetting("Layout", this, "Vertical", Arrays.asList("Vertical", "Horizontal"));
    private final BooleanSetting compactNumbersSetting = new BooleanSetting("Compact Numbers", this, true);
    private final BooleanSetting hideWhenZeroSetting = new BooleanSetting("Hide When Zero", this, true);
    private final BooleanSetting storageColorsSetting = new BooleanSetting("Storage Colors", this, true);
    private final BooleanSetting showIronSetting = new BooleanSetting("Show Iron", this, true);
    private final BooleanSetting showGoldSetting = new BooleanSetting("Show Gold", this, true);
    private final BooleanSetting showDiamondSetting = new BooleanSetting("Show Diamond", this, true);
    private final BooleanSetting showEmeraldSetting = new BooleanSetting("Show Emerald", this, true);
    private final BooleanSetting alwaysShowSetting = new BooleanSetting("Always Show", this, false);
    private final NumberSetting x = new NumberSetting("X", this, 576.0, 0.0, 1000.0, 1.0);
    private final NumberSetting y = new NumberSetting("Y", this, 447.0, 0.0, 1000.0, 1.0);
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 0.8, 0.5, 2.0, 0.1);
    private final StringSetting stringSetting = new StringSetting("Addition Label", this, "+");
    private final StringSetting stringSetting2 = new StringSetting("Equal Label", this, ":");
    private final int count14 = 15260098;
    private final int count15 = 12468223;
    private final int count16 = 16777215;
    private final int count17 = 7895160;
    private static final List<ResourceTracker.Entry2> entries = new ArrayList<>();

    public static boolean isActive() {
        return active;
    }

    public NumberSetting getX() {
        return this.x;
    }

    public BooleanSetting getShowIronSetting() {
        return this.showIronSetting;
    }

    private void process() {
        count4 = 0;
        count3 = 0;
        count2 = 0;
        count = 0;
        count8 = 0;
        count7 = 0;
        count6 = 0;
        count5 = 0;
        count12 = 0;
        count11 = 0;
        count10 = 0;
        count9 = 0;
        entries.clear();
        flag = false;
        count13 = 0;
    }

    public ModeSetting getHorizontalSetting() {
        return this.horizontalSetting;
    }

    public NumberSetting getScaleSetting() {
        return this.scaleSetting;
    }

    private ResourceTracker.Entry computeEntry() {
        ResourceTracker.Entry entry = new ResourceTracker.Entry();
        if (!(mc.thePlayer.openContainer instanceof ContainerChest containerChest)) {
            return entry;
        }
        IInventory lowerChestInventory = containerChest.getLowerChestInventory();

        for (int i = 0; i < lowerChestInventory.getSizeInventory(); i++) {
            ItemStack stack = lowerChestInventory.getStackInSlot(i);
            if (stack != null) {
                if (stack.getItem() == Items.iron_ingot) {
                    entry.count = entry.count + stack.stackSize;
                } else if (stack.getItem() == Items.gold_ingot) {
                    entry.count2 = entry.count2 + stack.stackSize;
                } else if (stack.getItem() == Items.diamond) {
                    entry.count3 = entry.count3 + stack.stackSize;
                } else if (stack.getItem() == Items.emerald) {
                    entry.count4 = entry.count4 + stack.stackSize;
                }
            }
        }

        return entry;
    }

    @Override
    public void onEvent(Event event) {
        if (this.enabled.isEnabled()) {
            if (event instanceof EventTick) {
                this.process3();
            } else if (event instanceof EventRender2D) {
                this.process4();
            } else if (event instanceof EventPacketReceive) {
                this.onEventPacketReceive((EventPacketReceive)event);
            }
        }
    }

    public static boolean checkState() {
        return instance != null && instance.alwaysShowSetting.isEnabled();
    }

    public ResourceTracker() {
        super("ResourceTracker", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.enabled,
                this.horizontalSetting,
                this.compactNumbersSetting,
                this.hideWhenZeroSetting,
                this.storageColorsSetting,
                this.showIronSetting,
                this.showGoldSetting,
                this.showDiamondSetting,
                this.showEmeraldSetting,
                this.alwaysShowSetting,
                this.x,
                this.y,
                this.scaleSetting,
                this.stringSetting,
                this.stringSetting2
            }
        );
        instance = this;
    }

    private void process4() {
        if (this.enabled.isEnabled() && mc.thePlayer != null) {
            if (active || this.alwaysShowSetting.isEnabled()) {
                UtilHelper4.handleInt(
                    (int)this.x.computeDouble(),
                    (int)this.y.computeDouble(),
                    (float)this.scaleSetting.computeDouble(),
                    this.horizontalSetting.getText().equals("Horizontal"),
                    this.compactNumbersSetting.isEnabled(),
                    this.hideWhenZeroSetting.isEnabled(),
                    this.storageColorsSetting.isEnabled(),
                    15260098,
                    12468223,
                    16777215,
                    7895160,
                    this.showIronSetting.isEnabled(),
                    this.showGoldSetting.isEnabled(),
                    this.showDiamondSetting.isEnabled(),
                    this.showEmeraldSetting.isEnabled(),
                    this.stringSetting.getValue(),
                    this.stringSetting2.getValue()
                );
            }
        }
    }

    public BooleanSetting getEnabled() {
        return this.enabled;
    }

    public BooleanSetting getShowGoldSetting() {
        return this.showGoldSetting;
    }

    public BooleanSetting getShowDiamondSetting() {
        return this.showDiamondSetting;
    }

    public BooleanSetting getCompactNumbersSetting() {
        return this.compactNumbersSetting;
    }

    private boolean checkState2() {
        if (!(mc.currentScreen instanceof GuiChest)) {
            return false;
        }
        if (!(mc.thePlayer.openContainer instanceof ContainerChest containerChest)) {
            return false;
        }
        IInventory lowerChestInventory = containerChest.getLowerChestInventory();
        String str = lowerChestInventory.getDisplayName() != null ? lowerChestInventory.getDisplayName().getUnformattedText() : "";
        str = StringUtils.stripControlCodes(str).toLowerCase();
        return str.contains("ender chest");
    }

    public BooleanSetting getShowEmeraldSetting() {
        return this.showEmeraldSetting;
    }

    private void process5() {
        Iterator it = entries.iterator();

        while (it.hasNext()) {
            ResourceTracker.Entry2 entry2 = (ResourceTracker.Entry2)it.next();
            int n = this.computeInt(entry2.item);
            int n2 = entry2.count - n;
            if (n2 > 0) {
                if (entry2.item == Items.iron_ingot) {
                    count9 += n2;
                } else if (entry2.item == Items.gold_ingot) {
                    count10 += n2;
                } else if (entry2.item == Items.diamond) {
                    count11 += n2;
                } else if (entry2.item == Items.emerald) {
                    count12 += n2;
                }

                it.remove();
            }
        }
    }

    private int computeInt(Item item) {
        int n = 0;
        if (mc.thePlayer != null && mc.thePlayer.inventory != null) {
            for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
                if (stack != null && stack.getItem() == item) {
                    n += stack.stackSize;
                }
            }

            return n;
        }
        return 0;
    }

    private void handleString(String str) {
        try {
            int n = str.lastIndexOf("(");
            int n2 = str.lastIndexOf(" Total)");
            if (n != -1 && n2 != -1 && n2 > n) {
                String str2 = str.substring(n + 1, n2);
                int n3 = Integer.parseInt(str2);
                if (str.contains("Iron")) {
                    count5 = n3;
                } else if (str.contains("Gold")) {
                    count6 = n3;
                } else if (str.contains("Diamond")) {
                    count7 = n3;
                } else if (str.contains("Emerald")) {
                    count8 = n3;
                }
            }
        } catch (Exception ignored) {
        }
    }

    public BooleanSetting getStorageColorsSetting() {
        return this.storageColorsSetting;
    }

    public BooleanSetting getHideWhenZeroSetting() {
        return this.hideWhenZeroSetting;
    }

    private void onEventPacketReceive(EventPacketReceive event) {
        if (event.getPacket() instanceof S02PacketChat packet) {
            String text = packet.getChatComponent().getUnformattedText();
            if (text.contains("Deposited") && text.contains("into Ender Chest")) {
                this.handleString(text);
            }
        }
    }

    private void process3() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (object != mc.theWorld) {
                object = mc.theWorld;
                this.process();
            }

            active = UnknownHelper.checkState();
            if (!active && !this.alwaysShowSetting.isEnabled()) {
                this.process();
            } else {
                ResourceTracker.Entry entry = this.computeEntry2();
                count = entry.count;
                count2 = entry.count2;
                count3 = entry.count3;
                count4 = entry.count4;
                this.process5();
                boolean flag2 = this.checkState2();
                if (flag2 && !flag) {
                    count13 = 2;
                }

                ResourceTracker.Entry entry2 = flag2 ? this.computeEntry() : null;
                if (count13 > 0) {
                    count13--;
                } else if (flag2 && entry2 != null) {
                    count9 = entry2.count;
                    count10 = entry2.count2;
                    count11 = entry2.count3;
                    count12 = entry2.count4;
                }

                count5 = count9;
                count6 = count10;
                count7 = count11;
                count8 = count12;
                flag = flag2;
            }
        } else {
            active = false;
            this.process();
            object = null;
        }
    }

    private ResourceTracker.Entry computeEntry2() {
        ResourceTracker.Entry entry = new ResourceTracker.Entry();
        if (mc.thePlayer != null && mc.thePlayer.inventory != null) {
            for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
                if (stack != null) {
                    if (stack.getItem() == Items.iron_ingot) {
                        entry.count = entry.count + stack.stackSize;
                    } else if (stack.getItem() == Items.gold_ingot) {
                        entry.count2 = entry.count2 + stack.stackSize;
                    } else if (stack.getItem() == Items.diamond) {
                        entry.count3 = entry.count3 + stack.stackSize;
                    } else if (stack.getItem() == Items.emerald) {
                        entry.count4 = entry.count4 + stack.stackSize;
                    }
                }
            }

            return entry;
        }
        return entry;
    }

    public NumberSetting getY() {
        return this.y;
    }

    public static class Entry {
        public int count = 0;
        public int count2 = 0;
        public int count3 = 0;
        public int count4 = 0;
    }

    public static class Entry2 {
        public Item item;
        public int count;

        public Entry2(Item item, int count) {
            this.item = item;
            this.count = count;
        }
    }
}
