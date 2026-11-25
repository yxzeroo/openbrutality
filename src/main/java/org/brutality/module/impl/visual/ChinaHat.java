package org.brutality.module.impl.visual;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.opengl.GL11;

public class ChinaHat extends Module {
    private final BooleanSetting showAllPlayersSetting = new BooleanSetting("Show All Players", this, true);
    private final BooleanSetting showInFirstPersonSetting = new BooleanSetting("Show In First Person", this, false);
    private static final long timestamp = 10000L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender3D) {
                for (EntityPlayer player : EntityCache.getEntries()) {
                    if ((this.showAllPlayersSetting.isEnabled() || player == mc.thePlayer)
                        && !player.isInvisible()
                        && !player.isDead
                        && (player != mc.thePlayer || mc.gameSettings.thirdPersonView != 0 || this.showInFirstPersonSetting.isEnabled())) {
                        this.handleEntityPlayer(player);
                    }
                }
            }
        }
    }

    public void handleEntityPlayer(EntityPlayer player) {
        double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
        double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks + player.height + 0.1 - mc.getRenderManager().renderPosY;
        double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
        GlStateManager.pushMatrix();
        GlStateManager.translate(d, d2, d3);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(6, DefaultVertexFormats.field_181705_e);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
        worldRenderer.func_181662_b(0.0, 0.3, 0.0).func_181675_d();

        for (int i = 0; i <= 360; i++) {
            float f = (float)(System.currentTimeMillis() % timestamp) / 10000.0F + i / 360.0F;
            Color hsbColor = Color.getHSBColor(f % 1.0F, 1.0F, 1.0F);
            GlStateManager.color(hsbColor.getRed() / 255.0F, hsbColor.getGreen() / 255.0F, hsbColor.getBlue() / 255.0F, 0.6F);
            double d4 = Math.cos(Math.toRadians(i)) * 0.7;
            double d5 = Math.sin(Math.toRadians(i)) * 0.7;
            worldRenderer.func_181662_b(d4, 0.0, d5).func_181675_d();
        }

        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public ChinaHat() {
        super("ChinaHat", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.showAllPlayersSetting, this.showInFirstPersonSetting});
    }
}
