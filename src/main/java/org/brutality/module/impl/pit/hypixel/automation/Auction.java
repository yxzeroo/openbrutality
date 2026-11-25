package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class Auction extends Module {
    private final PitCategory pitCategory = PitCategory.MINOR_EVENT;
    private static final int count = 15;
    private static final int count2 = 11;
    private final NumberSetting bidTimeSetting = new NumberSetting("Bid Time", this, 2.0, 1.0, 30.0, 1.0);
    private final NumberSetting clickDelaySetting = new NumberSetting("Click Delay", this, 75.0, 50.0, 500.0, 25.0);
    private final NumberSetting maxBidPerItemSetting = new NumberSetting("Max Bid (per item)", this, 5000.0, 100.0, 500000.0, 100.0);
    private final BooleanSetting autoDisableSetting = new BooleanSetting("Auto Disable", this, false);
    private final NumberSetting goldThresholdSetting = new NumberSetting("Gold Threshold", this, 75000.0, 10000.0, 500000.0, 5000.0);
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private long timestamp = 0L;
    private long lastClickTime = 0L;
    private long timestamp2 = 0L;
    private long timestamp3 = 0L;

    private void debug(String str) {
        if (this.debugSetting.isEnabled()) {
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "[Auction] " + str));
        }
    }

    private int computeInt3(GuiContainer gui) {
        try {
            ItemStack stack = gui.inventorySlots.getSlot(15).getStack();
            if (stack == null) {
                return -1;
            }

            for (String str : stack.getTooltip(mc.thePlayer, false)) {
                String str2 = str.replaceAll("§[0-9a-fk-or]", "").toLowerCase();
                if (str2.startsWith("time left")) {
                    String[] parts = str2.split(": ");
                    if (parts.length >= 2) {
                        String str3 = parts[1].trim();
                        String[] parts2 = str3.split(":");
                        if (parts2.length == 2) {
                            return Integer.parseInt(parts2[0].trim()) * 60 + Integer.parseInt(parts2[1].trim());
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return -1;
    }

    public Auction() {
        super("Auction", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.bidTimeSetting, this.maxBidPerItemSetting, this.clickDelaySetting, this.autoDisableSetting, this.goldThresholdSetting, this.debugSetting
            }
        );
    }

    private void handleGuiContainer2(GuiContainer gui, long lastClickTime) {
        if (lastClickTime - this.lastClickTime >= (long)this.clickDelaySetting.computeDouble()) {
            if (!this.matchesGuiContainer(gui)) {
                ItemStack stack = gui.inventorySlots.getSlot(11).getStack();
                if (stack != null) {
                    int n = this.computeInt3(gui);
                    if (n >= 0 && n <= (int)this.bidTimeSetting.computeDouble()) {
                        int n2 = this.computeInt2(gui);
                        int n3 = (int)(this.maxBidPerItemSetting.computeDouble() * this.computeInt(stack));
                        if (n2 <= n3) {
                            this.debug("Placing bid: " + n2 + "g (max: " + n3 + "g)");
                            mc.playerController.windowClick(gui.inventorySlots.windowId, 15, 0, 0, mc.thePlayer);
                            this.lastClickTime = lastClickTime;
                        }
                    }
                }
            }
        }
    }

    private boolean matchesGuiContainer(GuiContainer gui) {
        try {
            ItemStack stack = gui.inventorySlots.getSlot(15).getStack();
            if (stack == null) {
                return false;
            }

            for (String str : stack.getTooltip(mc.thePlayer, false)) {
                String str2 = str.replaceAll("§[0-9a-fk-or]", "").toLowerCase();
                if (str2.contains("you are top bid")) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    private void onChatEvent(ChatEvent chatEvent) {
        String str = chatEvent.message.getUnformattedText().replaceAll("§[0-9a-fk-or]", "");
        if (str.contains("AUCTION!")) {
            this.timestamp = System.currentTimeMillis();
            this.debug("Auction detected, timer reset");
        }
    }

    private int computeInt(GuiContainer gui) {
        try {
            ItemStack stack = gui.inventorySlots.getSlot(11).getStack();
            if (stack == null) {
                return -1;
            }

            for (String str : stack.getTooltip(mc.thePlayer, false)) {
                String str2 = str.replaceAll("§[0-9a-fk-or]", "").toLowerCase();
                if (str2.startsWith("bid:")) {
                    return this.computeInt(str2);
                }
            }
        } catch (Exception ignored) {
        }

        return -1;
    }

    private int computeInt(String str) {
        try {
            String[] parts = str.split(": ");
            if (parts.length < 2) {
                return -1;
            }
            String str2 = parts[parts.length - 1].replaceAll(",", "").replaceAll("g", "").trim();
            return (int)Double.parseDouble(str2);
        } catch (Exception e) {
            return -1;
        }
    }

    private void process() {
        long now = System.currentTimeMillis();
        if (this.autoDisableSetting.isEnabled() && !this.checkState()) {
            float f = this.computeFloat();
            if (f > 0.0F && f < this.goldThresholdSetting.computeDouble()) {
                double d = this.goldThresholdSetting.computeDouble();
                this.debug("Gold below threshold (" + f + " < " + d + "), disabling");
                this.handleFlag3(false);
                return;
            }
        }

        if (this.checkState() && now >= this.timestamp3) {
            if (mc.currentScreen == null) {
                if (now - this.timestamp2 >= 3000L) {
                    mc.thePlayer.sendChatMessage("/viewauction");
                    this.timestamp2 = now;
                }
            } else if (mc.currentScreen instanceof GuiContainer gui) {
                String simpleName = gui.getClass().getSimpleName();
                if (this.matchesString2(simpleName)) {
                    this.handleGuiContainer(gui, now);
                } else if (this.matchesString(simpleName)) {
                    this.handleGuiContainer2(gui, now);
                } else {
                    mc.thePlayer.closeScreen();
                }
            }
        }
    }

    private void handleGuiContainer(GuiContainer gui, long lastClickTime) {
        if (lastClickTime - this.lastClickTime >= (long)this.clickDelaySetting.computeDouble()) {
            int n = this.computeInt(gui);
            int n2 = (int)this.maxBidPerItemSetting.computeDouble();
            if (n > n2) {
                this.debug("Bid exceeded max, canceling");
                mc.playerController.windowClick(gui.inventorySlots.windowId, 15, 0, 0, mc.thePlayer);
                this.lastClickTime = lastClickTime;
            } else {
                this.debug("Confirming bid: " + n + "g");
                mc.playerController.windowClick(gui.inventorySlots.windowId, 11, 0, 0, mc.thePlayer);
                this.lastClickTime = lastClickTime;
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (FreshHelper.computeString(mc.theWorld) != null) {
                    if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                        if (event instanceof ChatEvent) {
                            this.onChatEvent((ChatEvent)event);
                        }

                        if (event instanceof EventTick) {
                            this.process();
                        }
                    }
                }
            }
        }
    }

    private boolean matchesString2(String str) {
        return str != null && (str.toLowerCase().contains("confirm") || str.toLowerCase().contains("bid"));
    }

    private int computeInt2(GuiContainer gui) {
        try {
            ItemStack stack = gui.inventorySlots.getSlot(15).getStack();
            if (stack == null) {
                return -1;
            }

            for (String str : stack.getTooltip(mc.thePlayer, false)) {
                String str2 = str.replaceAll("§[0-9a-fk-or]", "").toLowerCase();
                if (str2.startsWith("new bid") || str2.startsWith("minimum bid")) {
                    return this.computeInt(str2);
                }
            }
        } catch (Exception ignored) {
        }

        return -1;
    }

    private int computeInt(ItemStack stack) {
        try {
            String str = stack.getDisplayName().replaceAll("§[0-9a-fk-or]", "").toLowerCase();

            for (int i = 2; i <= 9; i++) {
                if (str.contains("x" + i) || str.contains("+" + i + "x") || str.contains(i + "x")) {
                    return i;
                }
            }
        } catch (Exception ignored) {
        }

        return 1;
    }

    private boolean matchesString(String str) {
        return str != null && str.toLowerCase().contains("auction");
    }

    private boolean checkState() {
        return System.currentTimeMillis() - this.timestamp < 60000L;
    }

    private float computeFloat() {
        return -1.0F;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.timestamp3 = 0L;
    }
}
