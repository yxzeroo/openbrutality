package org.brutality.module.impl.visual;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class InventoryGUI extends Module {
    private final NumberSetting positionXSetting = new NumberSetting("Position X", this, 400.0, 0.0, 1920.0, 1.0);
    private final NumberSetting positionYSetting = new NumberSetting("Position Y", this, 300.0, 0.0, 1080.0, 1.0);
    private final BooleanSetting syncThemeSetting = new BooleanSetting("Sync Theme", this, false);
    private final ColorSetting outlineColorSetting = new ColorSetting("Outline Color", this, 0.0F)
        .computeColorSetting(() -> !this.syncThemeSetting.isEnabled());
    private boolean flag = false;

    private Color computeColor(int n) {
        Color color;
        if (this.syncThemeSetting.isEnabled()) {
            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
            color = brutalityHelper != null ? brutalityHelper.getColor() : new Color(200, 50, 50);
        } else {
            color = this.outlineColorSetting.computeColor();
        }

        return new Color(color.getRed(), color.getGreen(), color.getBlue(), n);
    }

    private void process() {
        if (this.checkState44()) {
            int n = (int)this.positionXSetting.computeDouble();
            int n2 = (int)this.positionYSetting.computeDouble();
            byte b = 18;
            byte b2 = 4;
            byte b3 = 9;
            byte b4 = 3;
            int n3 = b * b3 + b2 * 2;
            int n4 = b * b4 + b2 * 2 + 20;
            byte b5 = 6;
            Color color = this.computeColor(255);
            OutlineThicknessHelper.handleFloat(n, n2, n3, n4, b5, 2.0F, new Color(0, 0, 0, 0), color);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof SetShaderTypeEvent setShaderTypeEvent && setShaderTypeEvent.getShaderType() == SetShaderTypeEvent.BlurMode.GLOW) {
                this.process();
            }
        }
    }

    public InventoryGUI() {
        super("InventoryGUI", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(new Setting[]{this.positionXSetting, this.positionYSetting, this.syncThemeSetting, this.outlineColorSetting});
    }

    @Override
    public boolean onBlurMode(SetShaderTypeEvent.BlurMode blurMode) {
        return blurMode == SetShaderTypeEvent.BlurMode.GLOW;
    }

    @Override
    public void onRender() {
        if (this.checkState44()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null) {
                int n = (int)this.positionXSetting.computeDouble();
                int n2 = (int)this.positionYSetting.computeDouble();
                byte b = 18;
                byte b2 = 4;
                byte b3 = 9;
                byte b4 = 3;
                int n3 = b * b3 + b2 * 2;
                int n4 = b * b4 + b2 * 2 + 20;
                byte b5 = 6;
                int[] ints = HudDragHelper.computeIntArray();
                int n5 = ints[0];
                int n6 = ints[1];
                if (HudDragHelper.matchesInt(n5, n6, n, n2, n3, n4)
                    && Mouse.isButtonDown(0)
                    && !this.flag
                    && HudDragHelper.matchesObject(this, n5, n6, n, n2)) {
                    this.flag = true;
                }

                if (this.flag) {
                    if (Mouse.isButtonDown(0)) {
                        int[] ints2 = HudDragHelper.computeIntArray(this);
                        if (ints2 != null) {
                            this.positionXSetting.handleDouble2(ints2[0]);
                            this.positionYSetting.handleDouble2(ints2[1]);
                            n = ints2[0];
                            n2 = ints2[1];
                        }
                    } else {
                        HudDragHelper.handleObject(this);
                        this.flag = false;
                    }
                }

                Color color = new Color(35, 10, 10, 200);
                Color color2 = new Color(80, 15, 15, 200);
                OutlineThicknessHelper.handleFloat(n, n2, n3, n4, b5, color, color2);
                Color color3 = this.computeColor(200);
                OutlineThicknessHelper.handleFloat(n, n2, n3, n4, b5, 2.0F, new Color(0, 0, 0, 0), color3);
                Color color4 = new Color(60, 15, 15, 220);
                OutlineThicknessHelper.handleFloat(n + 2, n2 + 2, n3 - 4, 16.0F, 4.0F, color4);
                computeFontRenderer(InventoryGUI.class).drawStringWithShadow("Inventory", n + b2 + 2, n2 + b2 + 2, new Color(255, 200, 200).getRGB());
                int n7 = n2 + 22;
                int n8 = n + b2;
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.pushMatrix();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

                for (int i = 9; i < 36; i++) {
                    ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
                    int n9 = (i - 9) / b3;
                    int n10 = (i - 9) % b3;
                    int n11 = n8 + n10 * b;
                    int n12 = n7 + n9 * b;
                    Color color5 = new Color(20, 5, 5, 180);
                    OutlineThicknessHelper.handleFloat(n11, n12, b - 2, b - 2, 2.0F, color5);
                    if (stack != null) {
                        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, n11 + 1, n12 + 1);
                        mc.getRenderItem().renderItemOverlayIntoGUI(computeFontRenderer(InventoryGUI.class), stack, n11 + 1, n12 + 1, null);
                    }
                }

                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderHelper.disableStandardItemLighting();
            }
        }
    }
}
