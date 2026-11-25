package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Arrays;
import java.util.Locale;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.BasicEvent4;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.player.PlayerHelper9;
import org.lwjgl.input.Mouse;

public class AuraTimer extends Module {
    private static final long timestamp = 15000L;
    private final PitCategory pitCategory = PitCategory.HUD;
    private static AuraTimer instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 65.0, 0.0, 1080.0, 1.0);
    private final BooleanSetting showLabelSetting = new BooleanSetting("Show Label", this, true);
    private final ModeSetting modeSetting = new ModeSetting("Suffix Mode", this, "#", Arrays.asList("#", "#.#"));
    private long timestamp2 = 0L;
    private boolean flag = false;
    private boolean flag2 = false;
    private long timestamp3;

    public AuraTimer() {
        super("Aura Timer", 0, Category.PIT);
        this.process18();
        this.xPosSetting.computeNumberSetting(() -> false);
        this.yPosSetting.computeNumberSetting(() -> false);
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting, this.showLabelSetting, this.modeSetting});
        instance = this;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
        this.timestamp2 = 0L;
        this.flag = false;
    }

    private void process() {
        AutoAura instance = AutoAura.getInstance();
        if (instance != null && instance.checkState44() && instance.getTimestamp() > this.timestamp3) {
            long l = instance.getTimestamp() + 15000L;
            if (l > System.currentTimeMillis() && l > this.timestamp2) {
                this.handleLong(l);
            }
        }
    }

    public boolean dz() {
        return this.flag;
    }

    public void handleLong(long l) {
        this.timestamp2 = l;
        this.flag = true;
        this.flag2 = false;
    }

    private String computeString(long l) {
        double d = Math.max(0.0, l / 1000.0);
        return this.modeSetting.matchesString2("#") ? String.format(Locale.ROOT, "%.0f", d) : String.format(Locale.ROOT, "%.1f", d);
    }

    @Override
    public void onRender() {
        EntityPlayerSP player = this.mc.thePlayer;
        if (player != null) {
            float f = (float)this.xPosSetting.computeDouble();
            float f2 = (float)this.yPosSetting.computeDouble();
            boolean flag = this.mc.currentScreen instanceof GuiChat;
            boolean flag2 = this.flag || flag;
            if (!flag2) {
                HudDragHelper.handleObject(this);
            } else {
                if (flag) {
                    this.handleFloat(f, f2);
                    f = (float)this.xPosSetting.computeDouble();
                    f2 = (float)this.yPosSetting.computeDouble();
                }

                FontRenderer fontRenderer = computeFontRenderer(AuraTimer.class);
                var str = this.showLabelSetting.isEnabled() ? "Aura: " : "";
                if (this.flag && this.timestamp2 > System.currentTimeMillis()) {
                    EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
                    String str2 = EnumChatFormatting.WHITE + str + GREEN + this.computeString(this.timestamp2 - System.currentTimeMillis());
                    fontRenderer.drawStringWithShadow(str2, f, f2, 16777215);
                } else if (flag) {
                    fontRenderer.drawStringWithShadow((String)str, f, f2, 16777215);
                    fontRenderer.drawStringWithShadow(EnumChatFormatting.GREEN + this.computeString(0L), f + fontRenderer.getStringWidth((String)str), f2, 16777215);
                }

                if (this.timestamp2 <= System.currentTimeMillis()) {
                    this.flag = false;
                    this.flag2 = false;
                }
            }
        }
    }

    private void onGetPacketEvent(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = getPacketEvent.getPacket();
            String text = packet.getChatComponent().getUnformattedText();
            if (text.contains("DEATH!")) {
                this.timestamp2 = 0L;
                this.flag = false;
                AutoAura instance = AutoAura.getInstance();
                if (instance != null) {
                    this.timestamp3 = instance.getTimestamp();
                }
            }
        }
    }

    public long getTimestamp2() {
        return this.timestamp2;
    }

    private void handleFloat(float f, float f2) {
        int[] ints = HudDragHelper.computeIntArray();
        int n = ints[0];
        int n2 = ints[1];
        if (Mouse.isButtonDown(0)) {
            if (!HudDragHelper.matchesObject(this)) {
                String str = this.showLabelSetting.isEnabled() ? "Aura: " : "";
                String str2 = str + this.computeString(this.flag ? this.timestamp2 - System.currentTimeMillis() : 0L);
                int textWidth = computeFontRenderer(AuraTimer.class).getStringWidth(str2);
                int FONT_HEIGHT = computeFontRenderer(AuraTimer.class).FONT_HEIGHT;
                if (HudDragHelper.matchesInt(n, n2, f, f2, textWidth, FONT_HEIGHT)) {
                    HudDragHelper.matchesObject(this, n, n2, (int)f, (int)f2);
                }
            }
        } else {
            HudDragHelper.handleObject(this);
        }

        if (HudDragHelper.matchesObject(this)) {
            int[] ints2 = HudDragHelper.computeIntArray(this);
            if (ints2 != null) {
                this.xPosSetting.handleDouble2(ints2[0]);
                this.yPosSetting.handleDouble2(ints2[1]);
            }
        }
    }

    @Generated
    public static AuraTimer getInstance() {
        return instance;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof BasicEvent4 || event instanceof EventPacketSend) {
                    ItemStack stack = PlayerHelper9.onEvent(event);
                    if (stack != null && stack.getItem() == Items.slime_ball) {
                        this.timestamp2 = System.currentTimeMillis() + 15000L;
                        this.flag = true;
                        this.flag2 = true;
                    }
                } else if (event instanceof EventUpdate) {
                    EntityPlayerSP player = this.mc.thePlayer;
                    if (player == null) {
                        return;
                    }

                    this.process();
                }

                if (event instanceof GetPacketEvent) {
                    this.onGetPacketEvent((GetPacketEvent)event);
                }
            }
        }
    }
}
