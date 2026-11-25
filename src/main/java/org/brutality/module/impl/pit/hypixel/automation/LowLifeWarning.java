package org.brutality.module.impl.pit.hypixel.automation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class LowLifeWarning extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xSetting = new NumberSetting("X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 200.0, 0.0, 1080.0, 1.0);
    private final NumberSetting itemSpacingSetting = new NumberSetting("Item Spacing", this, 12.0, 8.0, 24.0, 1.0);
    private final BooleanSetting showListDisplaySetting = new BooleanSetting("Show List Display", this, true);
    private final BooleanSetting colorInventorySetting = new BooleanSetting("Color Inventory", this, true);
    private final BooleanSetting showItemNameSetting = new BooleanSetting("Show Item Name", this, true);
    private final BooleanSetting showLivesCountSetting = new BooleanSetting("Show Lives Count", this, true);
    private final NumberSetting criticalLivesSetting = new NumberSetting("Critical Lives", this, 3.0, 1.0, 5.0, 1.0);
    private final NumberSetting warningLivesSetting = new NumberSetting("Warning Lives", this, 3.0, 2.0, 7.0, 1.0);
    private final NumberSetting safeLivesSetting = new NumberSetting("Safe Lives", this, 7.0, 5.0, 10.0, 1.0);
    private final BooleanSetting autoSpawnSetting = new BooleanSetting("Auto Spawn", this, false);
    private final BooleanSetting autoSpawnOnLeaveSetting = new BooleanSetting("Auto Spawn On Leave", this, false);
    private final BooleanSetting showWarningTitleSetting = new BooleanSetting("Show Warning Title", this, true);
    private final BooleanSetting deathWarningOverlaySetting = new BooleanSetting("Death Warning Overlay", this, true);
    private final KeybindListSetting displaySettingsSetting = new KeybindListSetting(
        "Display Settings",
        this,
        this.showListDisplaySetting,
        this.colorInventorySetting,
        this.showItemNameSetting,
        this.showLivesCountSetting,
        this.itemSpacingSetting
    );
    private final KeybindListSetting thresholdSettingsSetting = new KeybindListSetting(
        "Threshold Settings", this, this.criticalLivesSetting, this.warningLivesSetting, this.safeLivesSetting
    );
    private final KeybindListSetting autoSpawnSettingsSetting = new KeybindListSetting(
        "Auto Spawn Settings", this, this.autoSpawnSetting, this.autoSpawnOnLeaveSetting, this.showWarningTitleSetting, this.deathWarningOverlaySetting
    );
    private final TimerUtil timerUtil = new TimerUtil();
    private static final Pattern pattern = Pattern.compile("\\((\\d+)s left\\)");
    private long timestamp = 0L;
    private long timestamp2;
    private boolean flag = false;
    private boolean flag2 = false;
    private final Map<String, LowLifeWarning.Entry> lookupMap = new LinkedHashMap<>();

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.mc.thePlayer != null && this.mc.theWorld != null) {
                if (event instanceof EventTick) {
                    this.process5();
                    this.process();
                    this.process4();
                }

                if (event instanceof GetEntityEvent && ((GetEntityEvent)event).getEntity() == this.mc.thePlayer && this.deathWarningOverlaySetting.isEnabled()) {
                    this.process5();
                    if (this.lookupMap.values().stream().anyMatch(entry -> entry.count <= this.warningLivesSetting.computeDouble())) {
                        this.timestamp2 = System.currentTimeMillis() + 5000L;
                        this.mc.thePlayer.playSound("random.anvil_land", 0.7F, 1.0F);
                    }
                }

                if (event instanceof ChatEvent) {
                    String text = ((ChatEvent)event).message.getUnformattedText();
                    if (text.contains("HOLD UP! Can't /respawn while fighting")) {
                        this.handleString(text);
                    }

                    if (text.contains("you cannot /respawn here!")) {
                        new Thread(() -> {
                            try {
                                Thread.sleep(500L);
                                if (this.checkState44() && this.mc.thePlayer != null) {
                                    this.mc.thePlayer.sendChatMessage("/respawn");
                                }
                            } catch (InterruptedException ignored) {
                            }
                        }).start();
                    }
                }
            }
        }
    }

    private String computeString(ItemStack stack, int n) {
        return n + "_" + stack.getDisplayName() + "_" + System.identityHashCode(stack);
    }

    private void handleInt(int n, int n2, int n3, int n4, int n5, int n6) {
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glColor4f(n3 / 255.0F, n4 / 255.0F, n5 / 255.0F, n6 / 255.0F);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(n, n2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(n + 16, n2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(n + 16, n2 + 16, 0.0).func_181675_d();
        worldRenderer.func_181662_b(n, n2 + 16, 0.0).func_181675_d();
        tessellator.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
    }

    private void process6() {
        if (this.mc.thePlayer != null) {
            int n = (int)this.lookupMap.values().stream().filter(entry -> entry.count <= this.criticalLivesSetting.computeDouble()).count();
            String str = EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD.toString() + "⚠ LOW LIFE ITEMS ⚠";
            String str2 = EnumChatFormatting.YELLOW.toString() + n + " critical item(s)";
            this.mc.ingameGUI.displayTitle(str, str2, 10, 40, 10);
        }
    }

    private void handleFloat(float f, float f2, int n, int n2) {
        if (this.mc.currentScreen instanceof GuiChat) {
            int[] ints = HudDragHelper.computeIntArray();
            int n3 = ints[0];
            int n4 = ints[1];
            if (Mouse.isButtonDown(0)) {
                if (!HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(n3, n4, f, f2, n, n2)) {
                    HudDragHelper.matchesObject(this, n3, n4, (int)f, (int)f2);
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            if (HudDragHelper.matchesObject(this)) {
                int[] ints2 = HudDragHelper.computeIntArray(this);
                if (ints2 != null) {
                    this.xSetting.handleDouble2(ints2[0]);
                    this.ySetting.handleDouble2(ints2[1]);
                }
            }
        } else {
            HudDragHelper.handleObject(this);
        }
    }

    private void process5() {
        this.lookupMap.clear();
        if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
            for (int i = 0; i < this.mc.thePlayer.inventory.getSizeInventory(); i++) {
                ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null) {
                    int n = this.computeInt(stack);
                    if (this.matchesItemStack(stack, n)) {
                        String str = this.computeString(stack, i);
                        this.lookupMap.put(str, new LowLifeWarning.Entry(stack, n, i));
                    }
                }
            }
        }
    }

    private void process() {
        if (this.autoSpawnSetting.isEnabled() && this.mc.thePlayer != null) {
            boolean criticalItems = this.hasCriticalItems();
            if (criticalItems && !FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer) && !this.flag) {
                this.process3();
                if (this.showWarningTitleSetting.isEnabled()) {
                    this.process6();
                }
            }

            if (this.flag && this.timestamp > 0L && this.timerUtil.matchesLong4(this.timestamp + 100L)) {
                this.process3();
                this.timestamp = 0L;
            }

            if (this.flag && FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer)) {
                this.flag = false;
            }
        }
    }

    private int computeInt(int n) {
        if (n <= this.criticalLivesSetting.computeDouble()) {
            return 16733525;
        }
        if (n <= this.warningLivesSetting.computeDouble()) {
            return 16755200;
        }
        return n >= this.safeLivesSetting.computeDouble() ? 5635925 : 16777045;
    }

    private void renderItemsList() {
        if (!this.lookupMap.isEmpty()) {
            new ScaledResolution(this.mc);
            FontRenderer fontRenderer = computeFontRenderer(LowLifeWarning.class);
            float f = (float)this.xSetting.computeDouble();
            float f2 = (float)this.ySetting.computeDouble();
            int n = (int)this.itemSpacingSetting.computeDouble();
            short s = 250;
            int n2 = (this.lookupMap.size() + 1) * n + 4;
            this.handleFloat(f, f2, s, n2);
            String str = EnumChatFormatting.BOLD + "Low Life Items";
            fontRenderer.drawStringWithShadow(str, f, f2, 16777215);
            float f3 = f2 + n;
            ArrayList<LowLifeWarning.Entry> list = new ArrayList<>(this.lookupMap.values());
            list.sort(Comparator.comparingInt(entry -> entry.count));

            for (LowLifeWarning.Entry entry2 : list) {
                f3 = this.computeFloat(fontRenderer, f, f3, n, entry2);
            }
        }
    }

    @Override
    public void onRender() {
        if (this.checkState44()) {
            if (this.mc.thePlayer != null) {
                this.process5();
                if (this.timestamp2 > System.currentTimeMillis()) {
                    ScaledResolution sr = new ScaledResolution(this.mc);
                    FontRenderer fontRenderer = computeFontRenderer(LowLifeWarning.class);
                    String str = EnumChatFormatting.YELLOW + "WARNING: You have a low-life item!";
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(sr.getScaledWidth() / 2.0F, sr.getScaledHeight() / 2.0F + 10.0F, 0.0F);
                    GlStateManager.scale(1.5F, 1.5F, 1.0F);
                    fontRenderer.drawStringWithShadow(str, -fontRenderer.getStringWidth(str) / 2.0F, 0.0F, -1);
                    GlStateManager.popMatrix();
                }

                if (this.showListDisplaySetting.isEnabled()) {
                    this.renderItemsList();
                }
            }
        }
    }

    private void process4() {
        if (this.autoSpawnOnLeaveSetting.isEnabled() && this.mc.thePlayer != null) {
            boolean flag = FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer);
            if (this.flag2 && !flag && this.hasCriticalItems()) {
                this.process3();
                if (this.showWarningTitleSetting.isEnabled()) {
                    this.process6();
                }

                MessageUtils.x(EnumChatFormatting.RED + "⚠ Warning: Low life items detected! Attempting to spawn.");
            }

            this.flag2 = flag;
        }
    }

    private EnumChatFormatting computeEnumChatFormatting(int n) {
        if (n <= this.criticalLivesSetting.computeDouble()) {
            return EnumChatFormatting.RED;
        }
        if (n <= this.warningLivesSetting.computeDouble()) {
            return EnumChatFormatting.GOLD;
        }
        return n >= this.safeLivesSetting.computeDouble() ? EnumChatFormatting.GREEN : EnumChatFormatting.YELLOW;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap.clear();
        HudDragHelper.handleObject(this);
    }

    private float computeFloat(FontRenderer fontRenderer, float f, float f2, int n, LowLifeWarning.Entry entry) {
        StringBuilder sb = new StringBuilder();
        EnumChatFormatting enumChatFormatting = this.computeEnumChatFormatting(entry.count);
        if (this.showItemNameSetting.isEnabled()) {
            String displayName = entry.itemStack.getDisplayName();
            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(displayName);
            if (textWithoutFormattingCodes.length() > 20) {
                textWithoutFormattingCodes = textWithoutFormattingCodes.substring(0, 17) + "...";
            }

            sb.append(enumChatFormatting.toString()).append(textWithoutFormattingCodes);
        }

        if (this.showLivesCountSetting.isEnabled()) {
            if (sb.length() > 0) {
                sb.append(EnumChatFormatting.GRAY.toString()).append(" - ");
            }

            String str = entry.count + " " + (entry.count == 1 ? "Life" : "Lives");
            sb.append(enumChatFormatting.toString()).append(EnumChatFormatting.BOLD.toString()).append(str);
        }

        if (entry.count <= this.criticalLivesSetting.computeDouble()) {
            sb.append(" ").append(EnumChatFormatting.RED.toString()).append(EnumChatFormatting.BOLD.toString()).append("⚠");
        }

        int n2 = this.computeInt(entry.count);
        fontRenderer.drawStringWithShadow(sb.toString(), f, f2, n2);
        return f2 + n;
    }

    private boolean hasCriticalItems() {
        int n = (int)this.criticalLivesSetting.computeDouble();
        return this.lookupMap.values().stream().anyMatch(entry -> entry.count <= n);
    }

    private void handleString(String str) {
        try {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                String str2 = matcher.group(1);
                this.timestamp = Long.parseLong(str2) * 1000L;
                this.timerUtil.reset();
                this.flag = true;
                MessageUtils.x(EnumChatFormatting.YELLOW + "Combat timer detected, retrying /spawn in " + str2 + " seconds...");
            } else {
                this.timestamp = 0L;
            }
        } catch (Exception e) {
            this.timestamp = 0L;
        }
    }

    public void handleInt(int n, int n2, int n3) {
        if (this.colorInventorySetting.isEnabled() && this.mc.thePlayer != null && n >= 0 && n < 9) {
            ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(n);
            if (stack != null) {
                int n4 = this.computeInt(stack);
                if (n4 > 0) {
                    int n5 = this.computeInt(n4);
                    this.handleInt(n2, n3, n5 >> 16 & 0xFF, n5 >> 8 & 0xFF, (0xFF | ~n5) - ~n5, 70);
                }
            }
        }
    }

    private boolean matchesItemStack(ItemStack stack, int n) {
        if (stack != null && n > 0 && !(n > this.warningLivesSetting.computeDouble())) {
            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
            return textWithoutFormattingCodes != null && !textWithoutFormattingCodes.contains("Fresh") ? textWithoutFormattingCodes.contains("Mystic Sword") || textWithoutFormattingCodes.contains("Mystic Bow") : false;
        }
        return false;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.lookupMap.clear();
        this.flag2 = false;
        this.flag = false;
        this.timestamp = 0L;
        this.timestamp2 = 0L;
    }

    private void process3() {
        if (this.mc.thePlayer != null) {
            this.mc.thePlayer.sendChatMessage("/spawn");
            this.timerUtil.reset();
            this.flag = true;
            int n = (int)this.lookupMap.values().stream().filter(entry -> entry.count <= this.criticalLivesSetting.computeDouble()).count();
            EnumChatFormatting GOLD = EnumChatFormatting.GOLD;
            EnumChatFormatting RED = EnumChatFormatting.RED;
            String str = String.valueOf(n);
            EnumChatFormatting enumChatFormatting = RED;
            MessageUtils.x(GOLD + "Low Life Warning: " + enumChatFormatting + str + " critical item(s) detected. Executing /spawn...");
        }
    }

    private int computeInt(ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            return 0;
        }
        try {
            if (tagCompound.hasKey("ExtraAttributes", 10)) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("ExtraAttributes");
                String[] parts = new String[]{"lives", "Lives", "maxLives", "MaxLives"};

                for (String str : parts) {
                    if (compoundTag.hasKey(str)) {
                        try {
                            return compoundTag.getInteger(str);
                        } catch (Exception e) {
                            try {
                                String str2 = compoundTag.getString(str);
                                if (str2 != null && !str2.isEmpty()) {
                                    String str3 = str2.replaceAll("[^0-9]", "");
                                    if (!str3.isEmpty()) {
                                        return Integer.parseInt(str3);
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored2) {
        }

        if (tagCompound.hasKey("display", 10)) {
            NBTTagCompound compoundTag2 = tagCompound.getCompoundTag("display");
            if (compoundTag2.hasKey("Lore", 9)) {
                NBTTagList tagList = compoundTag2.getTagList("Lore", 8);

                for (int i = 0; i < tagList.tagCount(); i++) {
                    String stringTagAt = tagList.getStringTagAt(i);
                    if (stringTagAt != null) {
                        String str4 = EnumChatFormatting.getTextWithoutFormattingCodes(stringTagAt).toLowerCase().trim();
                        if (str4.contains("lives")) {
                            String str5 = str4.replaceAll("[^0-9]", "");
                            if (!str5.isEmpty()) {
                                try {
                                    return Integer.parseInt(str5);
                                } catch (NumberFormatException ignored3) {
                                }
                            }
                        }

                        if (stringTagAt.contains("❤") || stringTagAt.contains("♥")) {
                            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(stringTagAt);
                            String str6 = textWithoutFormattingCodes.replaceAll("[^0-9]", "");
                            if (!str6.isEmpty()) {
                                try {
                                    return Integer.parseInt(str6);
                                } catch (NumberFormatException ignored4) {
                                }
                            }
                        }
                    }
                }
            }
        }

        return 0;
    }

    public LowLifeWarning() {
        super("Low Life Warning", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{this.xSetting, this.ySetting, this.displaySettingsSetting, this.thresholdSettingsSetting, this.autoSpawnSettingsSetting}
        );
    }

    public void handleSlot(Slot slot, int n, int n2) {
        if (this.colorInventorySetting.isEnabled() && slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            if (stack != null) {
                if (slot.inventory == this.mc.thePlayer.inventory) {
                    int n3 = this.computeInt(stack);
                    if (n3 > 0) {
                        int n4 = this.computeInt(n3);
                        int n5 = n4 >> 16 & 0xFF;
                        int n6 = (0xFF | ~(n4 >> 8)) - ~(n4 >> 8);
                        int n7 = (0xFF | ~n4) - ~n4;
                        byte b = 80;
                        GlStateManager.disableDepth();
                        GlStateManager.disableLighting();
                        GlStateManager.enableBlend();
                        GlStateManager.disableTexture2D();
                        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                        GL11.glColor4f(n5 / 255.0F, n6 / 255.0F, n7 / 255.0F, b / 255.0F);
                        Tessellator tessellator = Tessellator.getInstance();
                        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
                        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
                        worldRenderer.func_181662_b(n, n2 + 16, 0.0).func_181675_d();
                        worldRenderer.func_181662_b(n + 16, n2 + 16, 0.0).func_181675_d();
                        worldRenderer.func_181662_b(n + 16, n2, 0.0).func_181675_d();
                        worldRenderer.func_181662_b(n, n2, 0.0).func_181675_d();
                        tessellator.draw();
                        GlStateManager.enableTexture2D();
                        GlStateManager.disableBlend();
                    }
                }
            }
        }
    }

    public static class Entry {
        public final ItemStack itemStack;
        public final int count;
        public final int count2;

        public Entry(ItemStack itemStack, int count, int n) {
            this.itemStack = itemStack;
            this.count = count;
            this.count2 = n;
        }
    }
}
