package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class BPSGraph extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final NumberSetting widthSetting = new NumberSetting("Width", this, 80.0, 40.0, 200.0, 2.0);
    private final NumberSetting heightSetting = new NumberSetting("Height", this, 40.0, 20.0, 100.0, 2.0);
    private final NumberSetting radiusSetting = new NumberSetting("Radius", this, 4.0, 0.0, 12.0, 0.5);
    private final BooleanSetting showValueSetting = new BooleanSetting("Show Value", this, true);
    private final NumberSetting xSetting = new NumberSetting("X", this, 5.0, 0.0, 5000.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 100.0, 0.0, 5000.0, 1.0);
    private static final int count = 60;
    private final Deque<Float> deque = new ArrayDeque<>();
    private final float[] floatValueArray = new float[60];
    private float factor = 0.0F;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.mc.thePlayer != null) {
                if (event instanceof EventUpdate && EventState.onEvent(event)) {
                    double motionX = this.mc.thePlayer.motionX;
                    double motionZ = this.mc.thePlayer.motionZ;
                    this.factor = (float)Math.sqrt(motionX * motionX + motionZ * motionZ) * 20.0F;
                    this.deque.addLast(this.factor);

                    while (this.deque.size() > 60) {
                        this.deque.removeFirst();
                    }
                }
            }
        }
    }

    private void handleFloat(float f, float f2, float f3, float f4, float f5, Color color, int n) {
        if (n >= 2) {
            float f6 = color.getRed() / 255.0F;
            float f7 = color.getGreen() / 255.0F;
            float f8 = color.getBlue() / 255.0F;
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

            for (int i = 0; i < n; i++) {
                float f9 = f + i / 59.0F * f3;
                float f10 = f2 + f4 - this.floatValueArray[i] / f5 * f4;
                f10 = Math.max(f2, Math.min(f2 + f4, f10));
                GL11.glColor4f(f6, f7, f8, 0.35F);
                GL11.glVertex2f(f9, f10);
                GL11.glColor4f(f6, f7, f8, 0.0F);
                GL11.glVertex2f(f9, f2 + f4);
            }

            GL11.glEnd();
        }
    }

    private void handleFloat2(float f, float f2, float f3, float f4, float f5, Color color, int n) {
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 0; i < n; i++) {
            float f6 = f + i / 59.0F * f3;
            float f7 = f2 + f4 - this.floatValueArray[i] / f5 * f4;
            f7 = Math.max(f2, Math.min(f2 + f4, f7));
            GL11.glVertex2f(f6, f7);
        }

        GL11.glEnd();
    }

    public BPSGraph() {
        super("BPS Graph", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(
            new Setting[]{this.modeSetting, this.widthSetting, this.heightSetting, this.radiusSetting, this.showValueSetting, this.xSetting, this.ySetting}
        );
    }

    @Override
    public void onRender() {
        if (this.mc.thePlayer != null) {
            float f = (float)this.widthSetting.computeDouble();
            float f2 = (float)this.heightSetting.computeDouble();
            float f3 = (float)this.radiusSetting.computeDouble();
            float f4 = (float)this.xSetting.computeDouble();
            float f5 = (float)this.ySetting.computeDouble();
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
            float f6 = Math.min(f4, sr.getScaledWidth() - f);
            float f7 = Math.min(f5, sr.getScaledHeight() - f2);
            if (this.mc.currentScreen instanceof GuiChat) {
                int[] ints = HudDragHelper.computeIntArray();
                int n = ints[0];
                int n2 = ints[1];
                if (Mouse.isButtonDown(0)) {
                    if (!HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(n, n2, f6, f7, (int)f, (int)f2)) {
                        HudDragHelper.matchesObject(this, n, n2, (int)f6, (int)f7);
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                if (HudDragHelper.matchesObject(this)) {
                    int[] ints2 = HudDragHelper.computeIntArray(this);
                    if (ints2 != null) {
                        this.xSetting.handleDouble2(Math.max(0, Math.min(ints2[0], sr.getScaledWidth() - (int)f)));
                        this.ySetting.handleDouble2(Math.max(0, Math.min(ints2[1], sr.getScaledHeight() - (int)f2)));
                        f6 = (float)this.xSetting.computeDouble();
                        f7 = (float)this.ySetting.computeDouble();
                    }
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            OutlineThicknessHelper.handleFloat(f6, f7, f, f2, f3, new Color(15, 15, 15, 190));
            String str = "BPS";
            computeFontRenderer(BPSGraph.class).drawString(str, (int)(f6 + 4.0F), (int)(f7 + 3.0F), -5592406);
            if (this.showValueSetting.isEnabled()) {
                String str2 = String.format("%.1f", this.factor);
                int textWidth = computeFontRenderer(BPSGraph.class).getStringWidth(str2);
                computeFontRenderer(BPSGraph.class).drawString(str2, (int)(f6 + f - textWidth - 4.0F), (int)(f7 + 3.0F), -1);
            }

            float f8 = f6 + 4.0F;
            float f9 = f7 + 14.0F;
            float f10 = f - 8.0F;
            float f11 = f2 - 18.0F;
            if (this.deque.size() >= 2) {
                float f12 = 0.0F;
                int n3 = 0;

                for (float f13 : this.deque) {
                    this.floatValueArray[n3++] = f13;
                    if (f13 > f12) {
                        f12 = f13;
                    }
                }

                if (f12 < 5.0F) {
                    f12 = 5.0F;
                }

                BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.modeSetting);
                Color color = brutalityHelper != null ? brutalityHelper.getColor() : new Color(6730751);
                Color color2 = brutalityHelper != null ? brutalityHelper.getColor2() : new Color(5082342);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.disableTexture2D();
                GlStateManager.enableAlpha();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glLineWidth(1.2F);
                this.handleFloat(f8, f9, f10, f11, f12, color2, n3);
                this.handleFloat2(f8, f9, f10, f11, f12, color, n3);
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GlStateManager.enableTexture2D();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.deque.clear();
        HudDragHelper.handleObject(this);
    }
}
