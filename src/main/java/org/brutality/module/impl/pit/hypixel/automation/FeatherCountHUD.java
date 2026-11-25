package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.input.Mouse;

public class FeatherCountHUD extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xSetting = new NumberSetting("X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 10.0, 0.0, 1080.0, 1.0);
    private final BooleanSetting showCurrentCountSetting = new BooleanSetting("Show Current Count", this, true);
    private final BooleanSetting showTotalLostSetting = new BooleanSetting("Show Total Lost", this, true);
    private final NumberSetting lossDisplayTimeSetting = new NumberSetting("Loss Display Time", this, 3000.0, 1000.0, 10000.0, 100.0);
    private final KeybindListSetting displaySettingsSetting = new KeybindListSetting(
        "Display Settings", this, this.showCurrentCountSetting, this.showTotalLostSetting, this.lossDisplayTimeSetting
    );
    private int count = 0;
    private int count2 = 0;
    private int count3 = 0;
    private int count4 = 0;
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean flag = false;

    @Override
    public void onRender() {
        if (this.mc.thePlayer != null) {
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
            FontRenderer fontRenderer = computeFontRenderer(FeatherCountHUD.class);
            float f = (float)this.xSetting.computeDouble();
            float f2 = (float)this.ySetting.computeDouble();
            short s = 200;
            byte b = 40;
            this.handleFloat(f, f2, s, b);
            if (this.showCurrentCountSetting.isEnabled()) {
                int count = this.count;
                EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
                String str = EnumChatFormatting.GRAY + "Feathers: " + WHITE + count;
                fontRenderer.drawStringWithShadow(str, f, f2, 16777215);
                f2 += 12.0F;
            }

            if (this.showTotalLostSetting.isEnabled()) {
                int n = this.count3;
                EnumChatFormatting RED = EnumChatFormatting.RED;
                String str2 = EnumChatFormatting.GRAY + "Total Lost: " + RED + n;
                fontRenderer.drawStringWithShadow(str2, f, f2, 16777215);
                f2 += 12.0F;
            }

            if (this.flag && !this.timerUtil.matchesLong4((long)this.lossDisplayTimeSetting.computeDouble())) {
                String str3 = "" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "-" + this.count4;
                long l = this.timerUtil.computeLong4();
                float f3 = (float)l / (float)this.lossDisplayTimeSetting.computeDouble();
                float f4 = f3 * 15.0F;
                int n2 = (int)((1.0F - f3) * 255.0F);
                n2 = Math.max(0, Math.min(255, n2));
                int n3 = n2 << 24 | 16733525;
                fontRenderer.drawStringWithShadow(str3, f + 50.0F, f2 - f4, n3);
            } else if (this.flag) {
                this.flag = false;
            }
        }
    }

    public FeatherCountHUD() {
        super("FeatherCountHUD", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xSetting, this.ySetting, this.displaySettingsSetting});
    }

    private void process3() {
        this.count2 = this.count;
        this.count = this.computeInt();
    }

    private void process() {
        int n = this.count2 - this.count;
        if (n > 0) {
            this.count4 = n;
            this.count3 += n;
            this.flag = true;
            this.timerUtil.reset();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
    }

    private int computeInt() {
        if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
            int n = 0;

            for (int i = 0; i < this.mc.thePlayer.inventory.getSizeInventory(); i++) {
                ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && this.matchesItemStack(stack)) {
                    n += stack.stackSize;
                }
            }

            return n;
        }
        return 0;
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        try {
            if (stack.getItem().getUnlocalizedName().toLowerCase().contains("feather")) {
                return true;
            }
        } catch (Exception ignored) {
        }

        try {
            String str = stack.getDisplayName().toLowerCase();
            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(str);
            if (textWithoutFormattingCodes.contains("feather")) {
                return true;
            }
        } catch (Exception ignored2) {
        }

        return false;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.mc.thePlayer != null && this.mc.theWorld != null) {
                if (event instanceof EventTick) {
                    this.process3();
                }

                if (event instanceof ChatEvent) {
                    String text = ((ChatEvent)event).message.getUnformattedText();
                    if (text.contains("DEATH!")) {
                        this.process();
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.count = this.computeInt();
        this.count2 = this.count;
        this.count3 = 0;
        this.count4 = 0;
        this.flag = false;
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
}
