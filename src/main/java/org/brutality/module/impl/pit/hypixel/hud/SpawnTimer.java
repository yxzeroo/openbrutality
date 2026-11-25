package org.brutality.module.impl.pit.hypixel.hud;

import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.player.GetHealthEvent;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.hypixel.pit.PruneExpiredCombatHelper;
import org.brutality.utils.hypixel.pit.StripColorHelper;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class SpawnTimer extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1200.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 40.0, 0.0, 1200.0, 1.0);
    private final BooleanSetting showLabelSetting = new BooleanSetting("Show Label", this, true);
    private final ModeSetting modeSetting = new ModeSetting("Suffix Mode", this, "#", Arrays.asList("#", "#.#"));
    private final BooleanSetting showInChatSetting = new BooleanSetting("Show In Chat", this, true);
    private final BooleanSetting detectOthersSetting = new BooleanSetting("Detect Others", this, true);
    private final BooleanSetting kosOnlySetting = new BooleanSetting("KOS Only", this, true);
    private final NumberSetting bodyScaleSetting = new NumberSetting("Body Scale", this, 0.1, 0.05, 0.5, 0.01);
    private boolean flag;
    private boolean flag2;

    private void handleDouble(double d, double d2, double d3, String str) {
        double d4 = d - this.mc.getRenderManager().viewerPosX;
        double d5 = d2 - this.mc.getRenderManager().viewerPosY;
        double d6 = d3 - this.mc.getRenderManager().viewerPosZ;
        float f = 0.025F * (float)this.bodyScaleSetting.computeDouble();
        GlStateManager.pushMatrix();
        GlStateManager.translate(d4, d5, d6);
        GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(this.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f, -f, f);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.enableTexture2D();
        int textWidth = computeFontRenderer(SpawnTimer.class).getStringWidth(str);
        computeFontRenderer(SpawnTimer.class).drawStringWithShadow(str, (~textWidth + 1) / 2.0F, 0.0F, 16777215);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            StripColorHelper.onEvent(event);
            if (event instanceof RenderEvent4 renderEvent4) {
                if (renderEvent4.world != renderEvent4.lastWorld) {
                    this.flag = false;
                    StripColorHelper.reset();
                    PruneExpiredCombatHelper.reset();
                }
            } else {
                if (event instanceof GetHealthEvent && this.detectOthersSetting.isEnabled()) {
                    this.process4();
                    PruneExpiredCombatHelper.onEvent(event);
                }

                if (event instanceof EventUpdate || event instanceof EventTick) {
                    this.process();
                    this.process3();
                    if (this.detectOthersSetting.isEnabled()) {
                        this.process4();
                        PruneExpiredCombatHelper.onEvent(event);
                    }
                }

                if (this.detectOthersSetting.isEnabled()) {
                    if (event instanceof GetEntityEvent) {
                        if (((GetEntityEvent)event).getEntity() instanceof EntityPlayer) {
                            PruneExpiredCombatHelper.handleEntityPlayer2((EntityPlayer)((GetEntityEvent)event).getEntity());
                        }
                    } else if (event instanceof EventRender3D) {
                        this.handleFloat(((EventRender3D)event).getFactor());
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.flag = false;
        this.flag2 = false;
        StripColorHelper.process4();
        this.process4();
        if (this.detectOthersSetting.isEnabled()) {
            PruneExpiredCombatHelper.process();
            this.flag2 = true;
        }
    }

    private void process() {
        if (this.mc.thePlayer != null) {
            if (this.mc.thePlayer.getHealth() > 0.0F) {
                if (this.flag) {
                    StripColorHelper.reset();
                    PruneExpiredCombatHelper.reset();
                    this.flag = false;
                }
            } else {
                this.flag = true;
            }
        }
    }

    private void handleFloat(float f, float f2, FontRenderer fontRenderer, String str) {
        int[] ints = HudDragHelper.computeIntArray();
        int n = ints[0];
        int n2 = ints[1];
        if (Mouse.isButtonDown(0)) {
            if (!HudDragHelper.matchesObject(this)) {
                int textWidth = fontRenderer.getStringWidth(str);
                if (HudDragHelper.matchesInt(n, n2, f, f2, textWidth, fontRenderer.FONT_HEIGHT)) {
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

    @Override
    public void onDisable() {
        super.onDisable();
        StripColorHelper.release();
        if (this.flag2) {
            PruneExpiredCombatHelper.release();
        }

        HudDragHelper.handleObject(this);
        this.flag = false;
        this.flag2 = false;
    }

    private void process3() {
        boolean enabled = this.detectOthersSetting.isEnabled();
        if (enabled && !this.flag2) {
            PruneExpiredCombatHelper.process();
            this.flag2 = true;
        } else if (!enabled && this.flag2) {
            PruneExpiredCombatHelper.release();
            this.flag2 = false;
        }
    }

    private String computeString2(long l) {
        var str = this.computeString(l);
        if (this.showLabelSetting.isEnabled()) {
            EnumChatFormatting RED = EnumChatFormatting.RED;
            EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
            return EnumChatFormatting.WHITE + "Spawn" + GRAY + ": " + RED + str;
        }
        return EnumChatFormatting.RED + str;
    }

    @Override
    public void onRender() {
        EntityPlayerSP player = this.mc.thePlayer;
        if (player != null) {
            float f = (float)this.xPosSetting.computeDouble();
            float f2 = (float)this.yPosSetting.computeDouble();
            boolean flag = this.mc.currentScreen instanceof GuiChat;
            long l = StripColorHelper.computeLong2();
            boolean flag2 = l > 0L;
            boolean flag3 = flag2 || flag && this.showInChatSetting.isEnabled();
            if (!flag3) {
                HudDragHelper.handleObject(this);
            } else {
                FontRenderer fontRenderer = this.computeFontRenderer();
                String str = this.computeString2(flag2 ? l : 0L);
                if (flag) {
                    this.handleFloat(f, f2, fontRenderer, str);
                    f = (float)this.xPosSetting.computeDouble();
                    f2 = (float)this.yPosSetting.computeDouble();
                }

                fontRenderer.drawStringWithShadow(str, f, f2, 16777215);
            }
        }
    }

    public SpawnTimer() {
        super("SpawnTimer", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.xPosSetting,
                this.yPosSetting,
                this.showLabelSetting,
                this.modeSetting,
                this.showInChatSetting,
                this.detectOthersSetting,
                this.kosOnlySetting,
                this.bodyScaleSetting
            }
        );
    }

    private void handleFloat(float f) {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            for (EntityPlayer player : EntityCache.getEntries()) {
                if (player != null
                    && player != this.mc.thePlayer
                    && !player.isDead
                    && PruneExpiredCombatHelper.matchesEntityPlayer2(player)
                    && !(this.mc.thePlayer.getDistanceToEntity(player) > 48.0F)) {
                    double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * f;
                    double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * f;
                    double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * f;
                    double d4 = d2 + player.height * 0.4;
                    if (PruneExpiredCombatHelper.matchesEntityPlayer(player)) {
                        this.handleDouble(d, d4, d3, EnumChatFormatting.RED + "Can't Spawn!");
                    } else {
                        long l = PruneExpiredCombatHelper.computeLong(player);
                        if (l > 0L) {
                            String str = EnumChatFormatting.RED + this.computeString(l);
                            this.handleDouble(d, d4, d3, str);
                        }
                    }
                }
            }
        }
    }

    private String computeString(long l) {
        double d = Math.max(0.0, l / 1000.0);
        return this.modeSetting.matchesString2("#") ? String.format(Locale.ROOT, "%.0f", d) : String.format(Locale.ROOT, "%.1f", d);
    }

    private void process4() {
        PruneExpiredCombatHelper.setFlag(this.kosOnlySetting.isEnabled());
    }

    private FontRenderer computeFontRenderer() {
        return computeFontRenderer(SpawnTimer.class);
    }
}
