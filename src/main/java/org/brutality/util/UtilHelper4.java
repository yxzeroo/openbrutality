package org.brutality.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.util.render.RenderHelper2;
import org.lwjgl.opengl.GL11;

public class UtilHelper4 {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final long timestamp = 4463547210500407295L;

    public static void handleItemStack(ItemStack stack, int n, int n2, String str) {
        if (stack != null) {
            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, n, n2);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, n, n2, str);
            RenderHelper.disableStandardItemLighting();
        }
    }

    public static void drawHorizontalLine(int n, int n2, int n3, int n4) {
        if (n2 < n) {
            int n5 = n;
            n = n2;
            n2 = n5;
        }

        Gui.drawRect(n, n3, n2 + 1, n3 + 1, n4);
    }

    private static void handleRenderHelper2(RenderHelper2 renderHelper2, boolean flag) {
        GL11.glPushAttrib((int)timestamp);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        if (flag) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }

        GL11.glDisable(GL11.GL_FOG);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glColor4f(renderHelper2.red, renderHelper2.green, renderHelper2.blue, renderHelper2.alpha);
    }

    public static void handleVec3(Vec3 vec, float f, float f2) {
        handleVec3(vec, 1.0, 1.0, 1.0, f, new RenderHelper2(1.0F, 0.0F, 0.0F, f2));
    }

    public static void handleVec3(Vec3 vec, float f, RenderHelper2 renderHelper2) {
        handleVec3(vec.addVector(0.5, 0.0, 0.5), 1.0, 1.0, 1.0, f, renderHelper2);
    }

    public static void handleEntityPlayer(EntityPlayer player, RenderHelper2 renderHelper2, double d, double d2, float f) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        double d3 = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * f;
        double d4 = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * f;
        double d5 = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * f;
        double d6 = player.prevPosX + (player.posX - player.prevPosX) * f - d3;
        double d7 = player.prevPosY + (player.posY - player.prevPosY) * f - d4;
        double d8 = player.prevPosZ + (player.posZ - player.prevPosZ) * f - d5;
        handleRenderHelper2(renderHelper2, true);
        worldRenderer.func_181668_a(5, DefaultVertexFormats.field_181705_e);

        for (int i = 0; i <= 8; i++) {
            double d9 = (Math.PI * 2) * i / 8.0;
            double cos = Math.cos(d9);
            double sin = Math.sin(d9);
            worldRenderer.func_181662_b(d6 + d2 * cos, d7, d8 + d2 * sin).func_181675_d();
            worldRenderer.func_181662_b(d6 + d * cos, d7, d8 + d * sin).func_181675_d();
        }

        tessellator.draw();
        process();
    }

    public static void handleDouble(double d, double d2, double d3, float f, RenderHelper2 renderHelper2, double d4) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        double d5 = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * f;
        double d6 = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * f;
        double d7 = d + 0.5 - d5;
        double d8 = d2 - d6;
        double d9 = d3 + 0.5 - (mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * f);
        double d10 = d8 + d4;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        GL11.glColor4f(renderHelper2.red, renderHelper2.green, renderHelper2.blue, renderHelper2.alpha);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d7 - 0.25, d8, d9 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.25, d10, d9 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.25, d10, d9 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.25, d8, d9 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.25, d8, d9 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.25, d10, d9 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.25, d10, d9 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.25, d8, d9 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.25, d8, d9 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.25, d10, d9 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.25, d10, d9 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.25, d8, d9 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.25, d8, d9 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.25, d10, d9 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.25, d10, d9 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.25, d8, d9 + 0.25).func_181675_d();
        tessellator.draw();
        GL11.glColor4f(renderHelper2.red, renderHelper2.green, renderHelper2.blue, Math.min(1.0F, renderHelper2.alpha * 1.5F));
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d7 - 0.1, d8, d9 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.1, d10, d9 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.1, d10, d9 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.1, d8, d9 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.1, d8, d9 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.1, d10, d9 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.1, d10, d9 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.1, d8, d9 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.1, d8, d9 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.1, d10, d9 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.1, d10, d9 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 - 0.1, d8, d9 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.1, d8, d9 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.1, d10, d9 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.1, d10, d9 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d7 + 0.1, d8, d9 + 0.1).func_181675_d();
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void handleVec3(Vec3 vec, float f, float f2, RenderHelper2 renderHelper2) {
        Vec3 vec2 = vec.addVector(0.5, 0.5, 0.5);
        handleDouble(vec2.xCoord, vec2.yCoord, vec2.zCoord, f, f2, renderHelper2);
    }

    private static void process() {
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopAttrib();
    }

    public static void handleVec3(Vec3 vec, float f, RenderHelper2 renderHelper2, RenderHelper2 renderHelper22) {
        handleVec3(vec, 0.4, 0.4, 0.4, f, renderHelper2);
        handleVec3(vec.addVector(-0.5, -0.3, -0.5), 0.4F, f, renderHelper22);
    }

    public static void handleVec3(Vec3 vec, double d, double d2, double d3, float f, RenderHelper2 renderHelper2) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        double d4 = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * f;
        double d5 = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * f;
        double d6 = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * f;
        double d7 = vec.xCoord - d / 2.0 - d4;
        double d8 = vec.yCoord - d5;
        double d9 = vec.zCoord - d2 / 2.0 - d6;
        double d10 = vec.xCoord + d / 2.0 - d4;
        double d11 = vec.yCoord + d3 - d5;
        double d12 = vec.zCoord + d2 / 2.0 - d6;
        handleRenderHelper2(renderHelper2, !BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, vec));
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d7, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d9).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d9).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d7, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d9).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d9).func_181675_d();
        tessellator.draw();
        process();
    }

    public static void handleDouble(double d, double d2, double d3, float f, float f2, RenderHelper2 renderHelper2) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        double d4 = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * f2;
        double d5 = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * f2;
        double d6 = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * f2;
        double d7 = d - f / 2.0F - d4;
        double d8 = d2 - f / 2.0F - d5;
        double d9 = d3 - f / 2.0F - d6;
        double d10 = d + f / 2.0F - d4;
        double d11 = d2 + f / 2.0F - d5;
        double d12 = d3 + f / 2.0F - d6;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(renderHelper2.red, renderHelper2.green, renderHelper2.blue, renderHelper2.alpha);
        worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d7, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d7, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d9).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d9).func_181675_d();
        worldRenderer.func_181662_b(d7, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d7, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d12).func_181675_d();
        worldRenderer.func_181662_b(d10, d8, d9).func_181675_d();
        worldRenderer.func_181662_b(d10, d11, d9).func_181675_d();
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    public static void handleDouble(double d, double d2, double d3, float f, RenderHelper2 renderHelper2) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        double d4 = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * f;
        double d5 = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * f;
        double d6 = d + 0.5 - d4;
        double d7 = d2 - d5;
        double d8 = d3 + 0.5 - (mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * f);
        double d9 = d7 + 256.0;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        GL11.glColor4f(renderHelper2.red, renderHelper2.green, renderHelper2.blue, renderHelper2.alpha);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d6 - 0.25, d7, d8 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.25, d9, d8 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.25, d9, d8 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.25, d7, d8 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.25, d7, d8 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.25, d9, d8 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.25, d9, d8 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.25, d7, d8 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.25, d7, d8 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.25, d9, d8 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.25, d9, d8 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.25, d7, d8 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.25, d7, d8 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.25, d9, d8 - 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.25, d9, d8 + 0.25).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.25, d7, d8 + 0.25).func_181675_d();
        tessellator.draw();
        GL11.glColor4f(renderHelper2.red, renderHelper2.green, renderHelper2.blue, Math.min(1.0F, renderHelper2.alpha * 1.5F));
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d6 - 0.1, d7, d8 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.1, d9, d8 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.1, d9, d8 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.1, d7, d8 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.1, d7, d8 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.1, d9, d8 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.1, d9, d8 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.1, d7, d8 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.1, d7, d8 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.1, d9, d8 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.1, d9, d8 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 - 0.1, d7, d8 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.1, d7, d8 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.1, d9, d8 - 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.1, d9, d8 + 0.1).func_181675_d();
        worldRenderer.func_181662_b(d6 + 0.1, d7, d8 + 0.1).func_181675_d();
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void handleInt(int n, int n2, int n3, int n4, int n5, int n6) {
        Gui.drawRect(n, n2, n + n3, n2 + n5, n6);
        Gui.drawRect(n, n2 + n4 - n5, n + n3, n2 + n4, n6);
        Gui.drawRect(n, n2, n + n5, n2 + n4, n6);
        Gui.drawRect(n + n3 - n5, n2, n + n3, n2 + n4, n6);
    }

    public static void handleVec3(Vec3 vec, double d, float f, RenderHelper2 renderHelper2, float f2) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        double d2 = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * f;
        double d3 = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * f;
        double d4 = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * f;
        double d5 = vec.xCoord - d / 2.0 - d2;
        double d6 = vec.yCoord - d / 2.0 - d3 + 0.5;
        double d7 = vec.zCoord - d / 2.0 - d4;
        double d8 = vec.xCoord + d / 2.0 - d2;
        double d9 = vec.yCoord + d / 2.0 - d3 + 1.0 + 0.5;
        double d10 = vec.zCoord + d / 2.0 - d4;
        handleRenderHelper2(renderHelper2, !BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, vec));
        GL11.glLineWidth(f2);
        worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d5, d6, d7).func_181675_d();
        worldRenderer.func_181662_b(d5, d6, d10).func_181675_d();
        worldRenderer.func_181662_b(d5, d6, d10).func_181675_d();
        worldRenderer.func_181662_b(d8, d6, d10).func_181675_d();
        worldRenderer.func_181662_b(d8, d6, d10).func_181675_d();
        worldRenderer.func_181662_b(d8, d6, d7).func_181675_d();
        worldRenderer.func_181662_b(d8, d6, d7).func_181675_d();
        worldRenderer.func_181662_b(d5, d6, d7).func_181675_d();
        worldRenderer.func_181662_b(d5, d9, d7).func_181675_d();
        worldRenderer.func_181662_b(d5, d9, d10).func_181675_d();
        worldRenderer.func_181662_b(d5, d9, d10).func_181675_d();
        worldRenderer.func_181662_b(d8, d9, d10).func_181675_d();
        worldRenderer.func_181662_b(d8, d9, d10).func_181675_d();
        worldRenderer.func_181662_b(d8, d9, d7).func_181675_d();
        worldRenderer.func_181662_b(d8, d9, d7).func_181675_d();
        worldRenderer.func_181662_b(d5, d9, d7).func_181675_d();
        worldRenderer.func_181662_b(d5, d6, d7).func_181675_d();
        worldRenderer.func_181662_b(d5, d9, d7).func_181675_d();
        worldRenderer.func_181662_b(d5, d6, d10).func_181675_d();
        worldRenderer.func_181662_b(d5, d9, d10).func_181675_d();
        worldRenderer.func_181662_b(d8, d6, d10).func_181675_d();
        worldRenderer.func_181662_b(d8, d9, d10).func_181675_d();
        worldRenderer.func_181662_b(d8, d6, d7).func_181675_d();
        worldRenderer.func_181662_b(d8, d9, d7).func_181675_d();
        tessellator.draw();
        process();
    }

    public static void drawVerticalLine(int n, int n2, int n3, int n4) {
        if (n3 < n2) {
            int n5 = n2;
            n2 = n3;
            n3 = n5;
        }

        Gui.drawRect(n, n2, n + 1, n3 + 1, n4);
    }
}
