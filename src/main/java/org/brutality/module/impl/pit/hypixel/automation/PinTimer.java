package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.player.GetPlayerEvent;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.inventory.MysticItemInfo;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class PinTimer extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static PinTimer instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1200.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 60.0, 0.0, 1200.0, 1.0);
    private final BooleanSetting show3DTimerSetting = new BooleanSetting("Show 3D Timer", this, true);
    private final BooleanSetting showMyPinsSetting = new BooleanSetting("Show My Pins", this, true);
    private final NumberSetting dTextScale3Setting = new NumberSetting("3D Text Scale", this, 1.0, 0.5, 3.0, 0.1);
    private final BooleanSetting showLabelSetting = new BooleanSetting("Show Label", this, true);
    private final ModeSetting modeSetting = new ModeSetting("Suffix Mode", this, "#", Arrays.asList("#", "#.#"));
    private final Map<String, Long> lookupMap = new HashMap<>();
    private int count = -1;
    private final TimerUtil timerUtil = new TimerUtil();

    private String computeString(long l) {
        double d = Math.max(0.0, l / 1000.0);
        return this.modeSetting.matchesString2("#") ? String.format(Locale.ROOT, "%.0f", d) : String.format(Locale.ROOT, "%.1f", d);
    }

    public boolean matchesString2(String str) {
        return this.matchesString(str);
    }

    private void process() {
        if (this.show3DTimerSetting.isEnabled() && this.showMyPinsSetting.isEnabled() && this.mc.theWorld != null && this.mc.thePlayer != null) {
            long now = System.currentTimeMillis();

            for (EntityPlayer player : EntityCache.getEntries()) {
                if (player != this.mc.thePlayer) {
                    long orDefault = this.lookupMap.getOrDefault(player.getName(), 0L);
                    if (now < orDefault) {
                        String str = (this.showLabelSetting.isEnabled() ? "PIN " : "") + this.computeString(orDefault - now);
                        double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * this.mc.timer.renderPartialTicks;
                        double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * this.mc.timer.renderPartialTicks + player.height;
                        double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * this.mc.timer.renderPartialTicks;
                        this.handleDouble(d, d2, d3, str);
                    }
                }
            }
        }
    }

    private void handleFloat(float f, float f2, String str, int n, FontRenderer fontRenderer) {
        int[] ints = HudDragHelper.computeIntArray();
        int n2 = ints[0];
        int n3 = ints[1];
        if (Mouse.isButtonDown(0)) {
            if (!HudDragHelper.matchesObject(this)) {
                int textWidth = fontRenderer.getStringWidth(str);
                if (HudDragHelper.matchesInt(n2, n3, f, f2, textWidth, n)) {
                    HudDragHelper.matchesObject(this, n2, n3, (int)f, (int)f2);
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

    public PinTimer() {
        super("Pin Timer", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.xPosSetting,
                this.yPosSetting,
                this.show3DTimerSetting,
                this.showMyPinsSetting,
                this.dTextScale3Setting,
                this.showLabelSetting,
                this.modeSetting
            }
        );
        instance = this;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.process2();
        HudDragHelper.handleObject(this);
    }

    private FontRenderer computeFontRenderer() {
        return computeFontRenderer(PinTimer.class);
    }

    private static int computeInt(ItemStack stack) {
        if (stack != null && stack.getItem() == Items.bow) {
            for (Entry entry : (new MysticItemInfo(stack)).lookupMap.entrySet()) {
                if (((String)entry.getKey()).toLowerCase(Locale.ROOT).contains("pin_down")) {
                    return (Integer)entry.getValue();
                }
            }

            return -1;
        }
        return -1;
    }

    private void handleDouble(double d, double d2, double d3, String str) {
        double d4 = d - this.mc.getRenderManager().viewerPosX;
        double d5 = d2 - this.mc.getRenderManager().viewerPosY;
        double d6 = d3 - this.mc.getRenderManager().viewerPosZ;
        double dist = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
        float f = Math.max(0.08F, (float)(dist * 0.012F)) * (float)this.dTextScale3Setting.computeDouble();
        GL11.glPushMatrix();
        GL11.glTranslated(d4, d5 + 0.3, d6);
        GL11.glRotatef(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(this.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-f, -f, f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        int textWidth = computeFontRenderer(PinTimer.class).getStringWidth(str);
        computeFontRenderer(PinTimer.class).drawStringWithShadow(str, -textWidth / 2, 0.0F, 65280);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void onRender() {
        EntityPlayerSP player = this.mc.thePlayer;
        if (player != null) {
            float f = (float)this.xPosSetting.computeDouble();
            float f2 = (float)this.yPosSetting.computeDouble();
            long now = System.currentTimeMillis();
            this.lookupMap.values().removeIf(val -> val <= now);
            boolean flag = this.mc.currentScreen instanceof GuiChat;
            boolean flag2 = !this.lookupMap.isEmpty();
            if (!flag && !flag2) {
                HudDragHelper.handleObject(this);
            } else {
                FontRenderer fontRenderer = this.computeFontRenderer();
                String str = this.showLabelSetting.isEnabled() ? EnumChatFormatting.GOLD + "Pin Timers" : "";
                int FONT_HEIGHT = fontRenderer.FONT_HEIGHT;
                if (!this.lookupMap.isEmpty()) {
                    FONT_HEIGHT += this.lookupMap.size() * (fontRenderer.FONT_HEIGHT + 2) + 5;
                }

                if (flag) {
                    this.handleFloat(f, f2, str, FONT_HEIGHT, fontRenderer);
                    f = (float)this.xPosSetting.computeDouble();
                    f2 = (float)this.yPosSetting.computeDouble();
                }

                fontRenderer.drawStringWithShadow(str, f, f2, 16777215);
                float f3 = f2 + fontRenderer.FONT_HEIGHT + 5.0F;
                if (this.lookupMap.isEmpty() && flag) {
                    fontRenderer.drawStringWithShadow(EnumChatFormatting.GRAY + "No pinned players", f, f3, 16777215);
                } else if (this.showMyPinsSetting.isEnabled()) {
                    for (Entry entry : this.lookupMap.entrySet()) {
                        EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
                        String key = (String)entry.getKey();
                        EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
                        String str2 = this.computeString((Long)entry.getValue() - now);
                        EnumChatFormatting enumChatFormatting = YELLOW;
                        var str3 = key;
                        String str4 = GREEN + str3 + ": " + enumChatFormatting + str2;
                        fontRenderer.drawStringWithShadow(str4, f, f3, 16777215);
                        f3 += fontRenderer.FONT_HEIGHT + 2;
                    }
                }
            }
        }
    }

    private void handleString(String str) {
        long l = computeLong(this.count);
        if (l != 0L && str != null && str.contains("IT'S A PIN!")) {
            String[] parts = str.trim().split("\\s+");
            int n = str.contains("removed Speed") ? 8 : 9;
            if (parts.length > n) {
                String str2 = parts[n].replace("!", "").trim();
                if (!str2.isEmpty()) {
                    long now = System.currentTimeMillis();

                    for (EntityPlayer playerEntity : this.mc.theWorld.playerEntities) {
                        String name = playerEntity.getName();
                        if (playerEntity != this.mc.thePlayer && name.equalsIgnoreCase(str2)) {
                            if (this.lookupMap.getOrDefault(name, 0L) > now) {
                                return;
                            }

                            long l2 = (long)(this.mc.thePlayer.getDistanceToEntity(playerEntity) * 33.0 + 800.0);
                            if (!this.timerUtil.matchesLong4(l2)) {
                                this.lookupMap.put(name, now + l);
                            }

                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.mc.thePlayer != null && this.mc.theWorld != null) {
                if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    long now = System.currentTimeMillis();
                    this.lookupMap.values().removeIf(val -> val <= now);
                }

                if (event instanceof GetPlayerEvent getPlayerEvent && getPlayerEvent.getPlayer() == this.mc.thePlayer && !getPlayerEvent.cancel2()) {
                    this.count = computeInt(getPlayerEvent.getItemStack());
                    if (this.count > 0) {
                        this.timerUtil.reset();
                    }
                }

                if (event instanceof ChatEvent chatEvent && chatEvent.byteVal != 2 && chatEvent.message != null) {
                    this.handleString(EnumChatFormatting.getTextWithoutFormattingCodes(chatEvent.message.getUnformattedText()));
                }

                if (event instanceof GetEntityEvent getEntityEvent && getEntityEvent.getEntity() instanceof EntityPlayer) {
                    this.lookupMap.remove(((EntityPlayer)getEntityEvent.getEntity()).getName());
                }

                if (event instanceof EventRender3D) {
                    this.process();
                }
            }
        }
    }

    @Generated
    public static PinTimer getInstance() {
        return instance;
    }

    @Override
    public void process2() {
        this.lookupMap.clear();
        this.count = -1;
    }

    public int computeInt(String str) {
        return (int)Math.max(0L, (this.lookupMap.getOrDefault(str, 0L) - System.currentTimeMillis()) / 1000L);
    }

    public boolean matchesString(String str) {
        return System.currentTimeMillis() < this.lookupMap.getOrDefault(str, 0L);
    }

    private static long computeLong(int n) {
        switch (n) {
            case 1:
                return 3000L;
            case 2:
                return 5000L;
            case 3:
                return 10000L;
            default:
                return 0L;
        }
    }
}
