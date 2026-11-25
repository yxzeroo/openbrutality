package org.brutality.module.impl.visual;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import org.brutality.event.Event;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.opengl.GL11;

public class ItemESP extends Module {
    private BooleanSetting allItemsSetting = new BooleanSetting("All Items", this, true);
    private BooleanSetting goldenIngotOnlySetting = new BooleanSetting("Golden Ingot Only", this, false);
    private BooleanSetting raffleTicketsOnlySetting = new BooleanSetting("Raffle Tickets Only", this, false);
    private BooleanSetting showCountSetting = new BooleanSetting("Show Count", this, true);
    private NumberSetting fillOpacitySetting = new NumberSetting("Fill Opacity", this, 0.3, 0.1, 1.0, 0.1);
    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetPartialTicksEvent) {
                if (this.mc.theWorld == null || this.mc.thePlayer == null) {
                    return;
                }

                for (EntityItem entity : this.mc.theWorld.getEntities(EntityItem.class, entity2 -> true)) {
                    if (!(entity.getDistanceToEntity(this.mc.thePlayer) > 50.0F) && this.matchesEntityItem(entity)) {
                        Color color = this.computeColor(entity.getEntityItem());
                        this.handleEntityItem(entity, color);
                    }
                }
            }
        }
    }

    public ItemESP() {
        super("ItemESP", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(
            new Setting[]{this.allItemsSetting, this.goldenIngotOnlySetting, this.raffleTicketsOnlySetting, this.showCountSetting, this.fillOpacitySetting}
        );
    }

    private double computeDouble(double d, double d2) {
        return d2 + (d - d2) * this.mc.timer.renderPartialTicks;
    }

    private void handleEntityItem(EntityItem entity, Color color) {
        if (this.mc.getRenderManager() != null) {
            double d = this.computeDouble(entity.posX, entity.lastTickPosX) - this.mc.getRenderManager().renderPosX;
            double d2 = this.computeDouble(entity.posY, entity.lastTickPosY) - this.mc.getRenderManager().renderPosY;
            double d3 = this.computeDouble(entity.posZ, entity.lastTickPosZ) - this.mc.getRenderManager().renderPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            AxisAlignedBB box = new AxisAlignedBB(d - 0.2, d2 - 0.1, d3 - 0.2, d + 0.2, d2 + 0.3, d3 + 0.2);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            float f = (float)this.fillOpacitySetting.computeDouble();
            GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, f);
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
            worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
            tessellator.draw();
            GL11.glLineWidth(2.0F);
            GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
            worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
            worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
            tessellator.draw();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
            if (this.showCountSetting.isEnabled() && entity.getEntityItem() != null) {
                ItemStack entityItem = entity.getEntityItem();
                if (entityItem.stackSize > 1) {
                    String str = String.valueOf(entityItem.stackSize);
                    FontRenderer fontRenderer = computeFontRenderer(ItemESP.class);
                    GlStateManager.pushMatrix();
                    GlStateManager.disableDepth();
                    GlStateManager.enableBlend();
                    double d4 = d2 + 0.1;
                    GlStateManager.translate(d, d4, d3);
                    GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(this.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(-0.08F, -0.08F, 0.08F);
                    int textWidth = fontRenderer.getStringWidth(str);
                    fontRenderer.drawString(str, (~textWidth + 1) / 2 + 1, 1, 0);
                    fontRenderer.drawString(str, -textWidth / 2, 0, color.getRGB());
                    GlStateManager.enableDepth();
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    private boolean matchesEntityItem(EntityItem entity) {
        ItemStack entityItem = entity.getEntityItem();
        if (this.raffleTicketsOnlySetting.isEnabled()) {
            return entityItem != null && entityItem.getDisplayName() != null ? entityItem.getDisplayName().toLowerCase().contains("raffle") : false;
        }
        if (this.goldenIngotOnlySetting.isEnabled()) {
            if (entityItem == null) {
                return false;
            }
            Item item = entityItem.getItem();
            return Item.getIdFromItem(item) == 266;
        }
        return this.allItemsSetting.isEnabled();
    }

    private Color computeColor(ItemStack stack) {
        if (stack == null) {
            return Color.WHITE;
        }
        if (Item.getIdFromItem(stack.getItem()) == 266) {
            return new Color(255, 215, 0);
        }
        try {
            int n = this.computeInt(stack.getRarity().rarityColor.getColorIndex());
            if (n >= 0) {
                return new Color(n);
            }
        } catch (Exception ignored) {
        }

        return Color.WHITE;
    }

    private int computeInt(int n) {
        if (n >= 0 && n <= 15) {
            int n2 = ((1 | ~(n >> 3)) - ~(n >> 3)) * 85;
            int n3 = ((1 | ~(n >> 2)) - ~(n >> 2)) * 170 + n2;
            int n4 = (n >> 1 & 1) * 170 + n2;
            int n5 = ((1 | ~n) - ~n) * 170 + n2;
            if (n == 6) {
                n3 += 85;
            }

            return (0xFF | ~n3) - ~n3 << 16 | (n4 & 0xFF) << 8 | (0xFF | ~n5) - ~n5;
        }
        return -1;
    }
}
