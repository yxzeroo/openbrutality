package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.lwjgl.opengl.GL11;

public class Hotbar extends Module {
    private static final ResourceLocation resourceLocation = new ResourceLocation("textures/gui/widgets.png");
    public final ModeSetting traditionalSetting = new ModeSetting("Style", this, "Rise", Arrays.asList("Rise", "Traditional"));
    private float factor = Float.NaN;
    private long timestamp;
    private static final long timestamp2 = 100L;

    public static boolean checkState() {
        try {
            for (Module module : ModuleManager.entries3) {
                if (module instanceof Hotbar) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    @Override
    public void onEnable() {
        this.factor = Float.NaN;
        this.timestamp = 0L;
    }

    @Override
    public String getSuffix() {
        return this.traditionalSetting.getText();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender2D event2) {
                if (mc.getRenderViewEntity() != null) {
                    if (mc.getRenderViewEntity() instanceof EntityPlayer && !mc.playerController.isSpectator()) {
                        EntityPlayer renderViewEntity = (EntityPlayer)mc.getRenderViewEntity();
                        ScaledResolution scaledResolution = event2.getScaledResolution();
                        int n = scaledResolution.getScaledWidth() / 2;
                        int scaledHeight = scaledResolution.getScaledHeight();
                        float f = n - 91;
                        float f2 = f - 1.0F + renderViewEntity.inventory.currentItem * 20;
                        long now = System.currentTimeMillis();
                        float f3 = this.timestamp == 0L ? 16.0F : (float)Math.min(timestamp2, now - this.timestamp);
                        this.timestamp = now;
                        if (Float.isNaN(this.factor)) {
                            this.factor = f2;
                        }

                        this.factor = this.factor + (f2 - this.factor) * Math.min(1.0F, 0.03F * f3);
                        GlStateManager.disableLighting();
                        GlStateManager.disableDepth();
                        GlStateManager.enableTexture2D();
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        int n2;
                        if (this.traditionalSetting.getText().equals("Traditional")) {
                            int n3 = scaledHeight - 22;
                            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                            mc.getTextureManager().bindTexture(resourceLocation);
                            Gui.drawModalRectWithCustomSizedTexture((int)f, n3, 0.0F, 0.0F, 182, 22, 256.0F, 256.0F);
                            GlStateManager.pushMatrix();
                            GlStateManager.translate(this.factor, (float)(n3 - 1), 0.0F);
                            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 22.0F, 24, 22, 256.0F, 256.0F);
                            GlStateManager.popMatrix();
                            n2 = scaledHeight - 19;
                        } else {
                            int n4 = scaledHeight - 26;
                            Gui.drawRect((int)f, n4, (int)f + 182, n4 + 22, new Color(20, 20, 20, 210).getRGB());
                            int n5 = (int)(this.factor + 1.0F);
                            int n6 = (int)(this.factor + 23.0F);
                            int n7 = n4 + 22;
                            int rgb = new Color(11766015).getRGB();
                            Gui.drawRect(n5, n4, n6, n7, (16777215 | ~rgb) - ~rgb | -1107296256);
                            n2 = scaledHeight - 23;
                        }

                        GlStateManager.enableRescaleNormal();
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.enableDepth();
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                        RenderHelper.enableGUIStandardItemLighting();

                        for (int i = 0; i < 9; i++) {
                            ItemStack stack = renderViewEntity.inventory.mainInventory[i];
                            if (stack != null) {
                                int n8 = n - 90 + i * 20 + 2;
                                float f4 = stack.animationsToGo - event2.partialTicks2;
                                if (f4 > 0.0F) {
                                    GlStateManager.pushMatrix();
                                    float f5 = 1.0F + f4 / 5.0F;
                                    GlStateManager.translate((float)(n8 + 8), (float)(n2 + 12), 0.0F);
                                    GlStateManager.scale(1.0F / f5, (f5 + 1.0F) / 2.0F, 1.0F);
                                    GlStateManager.translate((float)(-(n8 + 8)), (float)(-(n2 + 12)), 0.0F);
                                }

                                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, n8, n2);
                                if (f4 > 0.0F) {
                                    GlStateManager.popMatrix();
                                }

                                mc.getRenderItem().renderItemOverlays(computeFontRenderer(Hotbar.class), stack, n8, n2);
                            }
                        }

                        RenderHelper.disableStandardItemLighting();
                        GlStateManager.disableRescaleNormal();
                        GlStateManager.disableBlend();
                        GlStateManager.enableTexture2D();
                    }
                }
            }
        }
    }

    public Hotbar() {
        super("Hotbar", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(new Setting[]{this.traditionalSetting});
    }
}
