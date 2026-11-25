package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.lwjgl.opengl.GL11;

public class HologramDebugger extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final long timestamp = 2471159103322324992L;

    private void handleString(String str, double d, double d2, double d3) {
        FontRenderer fontRenderer = mc.fontRendererObj;
        String[] parts = str.split("\n");
        int n = 0;

        for (String str2 : parts) {
            n = Math.max(n, fontRenderer.getStringWidth(str2));
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(d, d2, d3);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.016666668F, -0.016666668F, 0.016666668F);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.55F);
        Gui.drawRect(-n / 2 - 2, -2, n / 2 + 2, parts.length * (fontRenderer.FONT_HEIGHT + 1), (int)timestamp);
        GlStateManager.enableTexture2D();

        for (int i = 0; i < parts.length; i++) {
            fontRenderer.drawStringWithShadow(parts[i], ~(fontRenderer.getStringWidth(parts[i]) - 1) / 2.0F, i * (fontRenderer.FONT_HEIGHT + 1), -1);
        }

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public HologramDebugger() {
        super("Hologram Debugger", 0, Category.PIT);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender3D && mc.thePlayer != null && mc.theWorld != null) {
            float factor = ((EventRender3D)event).getFactor();

            for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                if (entity instanceof EntityArmorStand && entity.hasCustomName()) {
                    var customNameTag = entity.getCustomNameTag();
                    if (customNameTag != null && !customNameTag.trim().isEmpty()) {
                        double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * factor;
                        double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * factor;
                        double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * factor;
                        EnumChatFormatting RESET = EnumChatFormatting.RESET;
                        EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
                        String str = customNameTag.replace('§', '&');
                        EnumChatFormatting GRAY2 = EnumChatFormatting.GRAY;
                        String str2 = String.format("XYZ: %.2f, %.2f, %.2f", d, d2, d3);
                        EnumChatFormatting GRAY3 = EnumChatFormatting.GRAY;
                        String str3 = FreshHelper.computeString(d, d2, d3, mc.theWorld);
                        EnumChatFormatting enumChatFormatting = GRAY3;
                        String str4 = str2;
                        EnumChatFormatting enumChatFormatting2 = GRAY2;
                        String str5 = str;
                        EnumChatFormatting enumChatFormatting3 = GRAY;
                        EnumChatFormatting enumChatFormatting4 = RESET;
                        String str6 = customNameTag + enumChatFormatting4 + "\n" + enumChatFormatting3 + "Codes: " + str5 + "\n" + enumChatFormatting2 + str4 + "\n" + enumChatFormatting + "Location: " + str3;
                        this.handleString(
                            str6,
                            d - mc.getRenderManager().viewerPosX,
                            d2 + entity.height + 0.25 - mc.getRenderManager().viewerPosY,
                            d3 - mc.getRenderManager().viewerPosZ
                        );
                    }
                }
            }
        }
    }
}
