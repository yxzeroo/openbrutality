package org.brutality.module.impl.misc;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.render.ResetColorHelper;

public class TPDeath extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting showMarkerSetting = new BooleanSetting("Show Marker", this, true);
    private final BooleanSetting autoTPBackSetting = new BooleanSetting("Auto TP Back", this, false);
    private final NumberSetting tpDelaySSetting = new NumberSetting("TP Delay (s)", this, 3.0, 1.0, 15.0, 1.0);
    private double amount;
    private double amount2;
    private double amount3;
    private boolean flag = false;
    private double amount4;
    private double amount5;
    private double amount6;
    private boolean flag2 = false;
    private boolean flag3 = false;
    private long timestamp = 0L;

    public double getAmount6() {
        return this.amount6;
    }

    public double getAmount5() {
        return this.amount5;
    }

    public boolean isFlag2() {
        return this.flag2;
    }

    private void process() {
        BlockPos pos = new BlockPos(this.amount4, this.amount5, this.amount6);
        ResetColorHelper.handleBlockPos2(pos, new Color(220, 40, 40, 180), true, false, 2.0F);
        double d = this.amount4 - mc.getRenderManager().viewerPosX;
        double d2 = this.amount5 + 2.0 - mc.getRenderManager().viewerPosY;
        double d3 = this.amount6 - mc.getRenderManager().viewerPosZ;
        String str = EnumChatFormatting.DARK_RED + "☠ " + EnumChatFormatting.WHITE + "DEATH";
        int textWidth = computeFontRenderer(TPDeath.class).getStringWidth(str);
        GlStateManager.pushMatrix();
        GlStateManager.translate(d, d2, d3);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        computeFontRenderer(TPDeath.class).drawString(str, (int)(-textWidth / 2.0F), 0, -1);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventTick) {
                    if (mc.thePlayer == null || mc.theWorld == null) {
                        return;
                    }

                    if (((EventTick)event).getState() != EventState.PRE) {
                        return;
                    }

                    float health = mc.thePlayer.getHealth();
                    if (health > 0.0F) {
                        this.amount = mc.thePlayer.posX;
                        this.amount2 = mc.thePlayer.posY;
                        this.amount3 = mc.thePlayer.posZ;
                        this.flag = true;
                        if (this.flag3) {
                            this.flag3 = false;
                            if (this.autoTPBackSetting.isEnabled() && this.flag2) {
                                this.timestamp = System.currentTimeMillis();
                            }
                        }
                    } else if (!this.flag3) {
                        this.amount4 = mc.thePlayer.posX;
                        this.amount5 = mc.thePlayer.posY;
                        this.amount6 = mc.thePlayer.posZ;
                        this.flag2 = true;
                        this.flag3 = true;
                        String str = MessageUtils.text13;
                        String str2 = MessageUtils.text16;
                        String str3 = MessageUtils.text15;
                        String str4 = String.format("%.1f, %.1f, %.1f", this.amount4, this.amount5, this.amount6);
                        String str5 = str3;
                        String str6 = str2;
                        MessageUtils.x(str + "[TPDeath] " + str6 + "Death position marked: " + str5 + str4);
                    }

                    if (this.autoTPBackSetting.isEnabled() && this.timestamp != 0L && this.flag) {
                        long l = System.currentTimeMillis() - this.timestamp;
                        if (l >= (long)(this.tpDelaySSetting.computeDouble() * 1000.0)) {
                            this.timestamp = 0L;
                            this.process3();
                        }
                    }
                }

                if (event instanceof EventRender3D && this.showMarkerSetting.isEnabled() && this.flag2) {
                    this.process();
                }

                if (event instanceof RenderEvent4) {
                    this.flag2 = false;
                    this.flag3 = false;
                    this.timestamp = 0L;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.flag2 = false;
            this.flag = false;
            this.flag3 = false;
            this.timestamp = 0L;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.flag2 = false;
        this.flag = false;
        this.flag3 = false;
    }

    private void process3() {
        if (this.flag) {
            mc.getNetHandler()
                .addToSendQueue(
                    new C03PacketPlayer.C06PacketPlayerPosLook(
                        this.amount, this.amount2, this.amount3, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true
                    )
                );
            MessageUtils.x(MessageUtils.text15 + "[TPDeath] " + MessageUtils.text16 + "Teleported back to last alive position.");
        }
    }

    public TPDeath() {
        super("TPDeath", 0, Category.MISCELLANEOUS);
        this.process18();
        this.handleSettingArray(new Setting[]{this.showMarkerSetting, this.autoTPBackSetting, this.tpDelaySSetting});
    }

    public double getAmount4() {
        return this.amount4;
    }
}
