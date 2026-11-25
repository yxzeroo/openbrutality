package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.lwjgl.opengl.GL11;

public class Breadcrumbs extends Module {
    private final ArrayDeque<Vec3> arrayDeque = new ArrayDeque<>();
    private final BooleanSetting timeoutSetting = new BooleanSetting("Timeout", this, true);
    private final NumberSetting timeSetting = new NumberSetting("Time", this, 15.0, 1.0, 150.0, 1.0);
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final ColorSetting customColorSetting = new ColorSetting("Custom Color", this, 0.0F, 1.0F, 1.0F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private BrutalityHelper brutalityHelper;

    @Override
    public void onEnable() {
        this.arrayDeque.clear();
    }

    private void process3() {
        GlStateManager.disableDepth();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        int n = 0;

        try {
            for (Vec3 vec : this.arrayDeque) {
                n++;
                boolean flag = true;
                double d = vec.xCoord - mc.getRenderManager().renderPosX;
                double d2 = vec.yCoord - mc.getRenderManager().renderPosY;
                double d3 = vec.zCoord - mc.getRenderManager().renderPosZ;
                double dist = mc.thePlayer.getDistance(vec.xCoord, vec.yCoord - 1.0, vec.zCoord);
                int n2 = (int)(dist * 4.0 + 10.0);
                if (n2 > 350) {
                    n2 = 350;
                }

                if (n % 10 != 0 && dist > 25.0) {
                    flag = false;
                }

                if (n % 3 == 0 && dist > 15.0) {
                    flag = false;
                }

                if (flag) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(d, d2, d3);
                    GL11.glScalef(-0.04F, -0.04F, -0.04F);
                    GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0, 1.0, 0.0);
                    GL11.glRotated(mc.getRenderManager().playerViewX, 1.0, 0.0, 0.0);
                    Color color = this.computeColor();
                    this.handleDouble(0.0, 0.0, 0.7, color.getRGB(), n2);
                    if (dist < 4.0) {
                        this.handleDouble(0.0, 0.0, 1.4, new Color(color.getRed(), color.getGreen(), color.getBlue(), 50).getRGB(), n2);
                    }

                    if (dist < 20.0) {
                        this.handleDouble(0.0, 0.0, 2.3, new Color(color.getRed(), color.getGreen(), color.getBlue(), 30).getRGB(), n2);
                    }

                    GL11.glScalef(0.8F, 0.8F, 0.8F);
                    GL11.glPopMatrix();
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.enableDepth();
        GL11.glColor3d(255.0, 255.0, 255.0);
    }

    private void process() {
        this.brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.modeSetting);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventPreMotion) {
                if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX
                    || mc.thePlayer.lastTickPosY != mc.thePlayer.posY
                    || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ) {
                    this.arrayDeque.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
                }

                if (this.timeoutSetting.isEnabled()) {
                    while (this.arrayDeque.size() > (int)this.timeSetting.computeDouble()) {
                        this.arrayDeque.removeFirst();
                    }
                }
            } else if (event instanceof EventRender3D) {
                this.process();
                this.process3();
            }
        }
    }

    private void handleDouble(double d, double d2, double d3, int n, int n2) {
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = ((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = ((0xFF | ~n) - ~n) / 255.0F;
        GlStateManager.color(f2, f3, f4, f);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(6, DefaultVertexFormats.field_181705_e);

        for (int i = 0; i <= n2; i++) {
            double d4 = i * Math.PI * 2.0 / n2;
            double d5 = d + Math.cos(d4) * d3;
            double d6 = d2 + Math.sin(d4) * d3;
            worldRenderer.func_181662_b(d5, d6, 0.0).func_181675_d();
        }

        tessellator.draw();
    }

    public Breadcrumbs() {
        super("Breadcrumbs", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.timeoutSetting, this.timeSetting, this.modeSetting, this.customColorSetting});
        this.process();
    }

    private Color computeColor() {
        return ModuleSpecificHelper.matchesModeSetting(this.modeSetting) && this.brutalityHelper != null
            ? this.brutalityHelper.getColor()
            : this.customColorSetting.computeColor();
    }
}
