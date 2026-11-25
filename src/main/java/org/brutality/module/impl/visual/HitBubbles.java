package org.brutality.module.impl.visual;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public class HitBubbles extends Module {
    private static final java.util.ArrayList<HitBubbles.Entry> entries = new java.util.ArrayList<>();
    private final Tessellator tessellator = Tessellator.getInstance();
    private final WorldRenderer worldRenderer = this.tessellator.getWorldRenderer();
    private final ResourceLocation resourceLocation = new ResourceLocation("brutality/images/bubble.png");
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final ColorSetting customColorSetting = new ColorSetting("Custom Color", this, 0.0F, 1.0F, 1.0F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private BrutalityHelper brutalityHelper;
    private static final long timestamp = 6084659208802828292L;

    private void handleEntry(HitBubbles.Entry entry, float f) {
        float f2 = 50.0F * entry.computeFloat() * (1.0F - entry.computeFloat());
        byte b = 3;
        float f3 = (float)(System.currentTimeMillis() % (3600 / b)) / 10.0F * b;
        ResetColorHelper.handleFloat(-1.0F, -1.0F, 2.0F, 2.0F, (double)(-f3));
        this.worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
        this.worldRenderer.func_181662_b(0.0, 0.0, 0.0).func_181673_a(0.0, 0.0).color(GetColorHelper.computeInt(this.computeInt(0), f)).func_181675_d();
        this.worldRenderer.func_181662_b(0.0, f2, 0.0).func_181673_a(0.0, 1.0).color(GetColorHelper.computeInt(this.computeInt(90), f)).func_181675_d();
        this.worldRenderer.func_181662_b(f2, f2, 0.0).func_181673_a(1.0, 1.0).color(GetColorHelper.computeInt(this.computeInt(180), f)).func_181675_d();
        this.worldRenderer.func_181662_b(f2, 0.0, 0.0).func_181673_a(1.0, 0.0).color(GetColorHelper.computeInt(this.computeInt(270), f)).func_181675_d();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
        GlStateManager.translate(-f2 / 2.0F, -f2 / 2.0F, 0.0F);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
        this.tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.translate(f2 / 2.0F, f2 / 2.0F, 0.0F);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private int computeInt(int n) {
        Color color;
        if (ModuleSpecificHelper.matchesModeSetting(this.modeSetting) && this.brutalityHelper != null) {
            color = this.brutalityHelper.getColor();
        } else {
            color = this.customColorSetting.computeColor();
        }

        return color.getRGB();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                this.removeAuto();
            }

            if (event instanceof EventUpdate) {
                if (KillAura.entityLivingBase2 != null && KillAura.entityLivingBase2.isEntityAlive() && KillAura.entityLivingBase2.hurtTime == 9) {
                    Vec3 vec = KillAura.entityLivingBase2.getPositionVector().addVector(0.0, KillAura.entityLivingBase2.height / 1.6F, 0.0);
                    handleVec3(vec);
                }
            } else if (event instanceof EventRender3D) {
                this.process();
                float f = this.computeFloat();
                if (f < 0.05) {
                    return;
                }

                if (entries.isEmpty()) {
                    return;
                }

                this.removeAuto();
                this.handleRunnable(() -> entries.forEach(entry -> {
                    if (entry != null && entry.computeFloat() <= 1.0F) {
                        this.handleEntry2(entry, f);
                    }
                }));
            }
        }
    }

    public static float computeFloat2() {
        return 1000.0F;
    }

    private void process() {
        this.brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.modeSetting);
    }

    private void removeAuto() {
        entries.removeIf(entry -> entry.computeFloat() >= 1.0F);
    }

    private void handleEntry2(HitBubbles.Entry entry, float f) {
        GL11.glPushMatrix();
        GL11.glTranslated(entry.vec3.xCoord, entry.vec3.yCoord, entry.vec3.zCoord);
        float f2 = entry.computeFloat();
        GlStateManager.translate(
            -Math.sin(Math.toRadians(entry.factor3)) * f2 / 3.0,
            Math.sin(Math.toRadians(entry.factor2)) * f2 / 2.0,
            -Math.cos(Math.toRadians(entry.factor3)) * f2 / 3.0
        );
        GL11.glNormal3d(1.0, 1.0, 1.0);
        GL11.glRotated(entry.factor3, 0.0, 1.0, 0.0);
        GL11.glRotated(entry.factor2, mc.gameSettings.thirdPersonView == 2 ? -1.0 : 1.0, 0.0, 0.0);
        GL11.glScaled(-0.1, -0.1, 0.1);
        this.handleEntry(entry, f);
        GL11.glPopMatrix();
    }

    private static void handleVec3(Vec3 vec) {
        RenderManager renderManager = mc.getRenderManager();
        entries.add(new HitBubbles.Entry(renderManager.playerViewX, -renderManager.playerViewY, vec));
    }

    @Override
    public void onDisable() {
        entries.clear();
    }

    private void handleRunnable(Runnable task) {
        RenderManager renderManager = mc.getRenderManager();
        Vec3 vec = new Vec3(renderManager.renderPosX, renderManager.renderPosY, renderManager.renderPosZ);
        boolean flag = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GlStateManager.pushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
        if (flag) {
            GlStateManager.disableLighting();
        }

        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, (int)timestamp, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glTranslated(-vec.xCoord, -vec.yCoord, -vec.zCoord);
        mc.getTextureManager().bindTexture(this.resourceLocation);
        task.run();
        GL11.glTranslated(vec.xCoord, vec.yCoord, vec.zCoord);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.resetColor();
        GL11.glShadeModel(GL11.GL_FLAT);
        if (flag) {
            GlStateManager.enableLighting();
        }

        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    private float computeFloat() {
        return 1.0F;
    }

    @Override
    public void process2() {
        entries.clear();
    }

    public HitBubbles() {
        super("HitBubbles", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.modeSetting, this.customColorSetting});
        this.process();
    }

    public static final class Entry {
        public Vec3 vec3;
        public long time = System.currentTimeMillis();
        public float factor = HitBubbles.computeFloat2();
        public float factor2;
        public float factor3;

        public Entry(float f, float f2, Vec3 vec3) {
            this.factor2 = f;
            this.factor3 = f2;
            this.vec3 = vec3;
        }

        public float computeFloat() {
            return (float)(System.currentTimeMillis() - this.time) / this.factor;
        }
    }
}
