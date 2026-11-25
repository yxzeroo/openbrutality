package org.brutality.utils.render;

import java.awt.Color;
import java.awt.Font;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.brutality.Brutality;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.utils.mcp.McpApi;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

public class ResetColorHelper {
    public static final Pattern pattern = Pattern.compile("(?i)§[0-9A-FK-OR]");
    public static double amount;
    public static Random random = new Random();
    public static RenderItem renderItem = McpApi.mc.getRenderItem();
    public static long tick;
    public static long timestamp = 0L;
    private static final RenderItem renderItem2 = McpApi.mc.getRenderItem();
    private static final Font font = computeFont("brutality.ttf", 20);
    private final Font font2 = computeFont("brutality-bold.ttf", 14);
    private static final Frustum frustum = new Frustum();
    private static final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);
    private static final IntBuffer intBuffer = BufferUtils.createIntBuffer(16);
    private static final FloatBuffer floatBuffer3 = BufferUtils.createFloatBuffer(3);
    private static final FloatBuffer floatBuffer4 = BufferUtils.createFloatBuffer(4);
    private static ScaledResolution scaledResolution;
    private static int count = -1;
    private static int count2 = -1;
    private static int count3 = -1;
    private static final CustomFontRenderer drawStringWithShadowComponent = new CustomFontRenderer(font, true, true);
    private final CustomFontRenderer drawStringWithShadowComponent2 = new CustomFontRenderer(this.font2, true, true);
    private static final long timestamp2 = 187L;

    public static void handleIterable(Iterable<Vec3> iterable, Color color) {
        GlStateManager.disableDepth();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        int n = 0;

        try {
            for (Vec3 vec : iterable) {
                n++;
                boolean flag = true;
                double d = vec.xCoord - McpApi.mc.getRenderManager().renderPosX;
                double d2 = vec.yCoord - McpApi.mc.getRenderManager().renderPosY;
                double d3 = vec.zCoord - McpApi.mc.getRenderManager().renderPosZ;
                double dist = McpApi.mc.thePlayer.getDistance(vec.xCoord, vec.yCoord - 1.0, vec.zCoord);
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
                    GL11.glRotated(-McpApi.mc.getRenderManager().playerViewY, 0.0, 1.0, 0.0);
                    GL11.glRotated(McpApi.mc.getRenderManager().playerViewX, 1.0, 0.0, 0.0);
                    handleFloat(0.0F, 0.0F, 0.7, color.hashCode(), n2);
                    if (dist < 4.0) {
                        handleFloat(0.0F, 0.0F, 1.4, new Color(color.getRed(), color.getGreen(), color.getBlue(), 50).hashCode(), n2);
                    }

                    if (dist < 20.0) {
                        handleFloat(0.0F, 0.0F, 2.3, new Color(color.getRed(), color.getGreen(), color.getBlue(), 30).hashCode(), n2);
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

    public static void handleFloat(float f, float f2, float f3) {
        GL11.glPushMatrix();
        GL11.glTranslatef(f, f2, 0.0F);
        GL11.glScalef(f3, f3, 1.0F);
        GL11.glTranslatef(-f, -f2, 0.0F);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, int n) {
        if (!(f3 <= f)) {
            float f6 = f3 - f;
            if (f6 < 3.0F) {
                f5 = Math.min(f5, f6 / 2.0F);
            }

            float f7 = (float)(f * 2.0);
            float f8 = (float)(f2 * 2.0);
            float f9 = (float)(f3 * 2.0);
            float f10 = (float)(f4 * 2.0);
            GL11.glPushAttrib(0);
            GL11.glScaled(0.5, 0.5, 0.5);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);
            handleInt(n);

            for (int i = 0; i <= 90; i += 3) {
                double d = i * (float) (Math.PI / 180.0);
                GL11.glVertex2d(f7 + f5 + Math.sin(d) * f5 * -1.0, f8 + f5 + Math.cos(d) * f5 * -1.0);
            }

            for (int j = 90; j <= 180; j += 3) {
                double d2 = j * (float) (Math.PI / 180.0);
                GL11.glVertex2d(f7 + f5 + Math.sin(d2) * f5 * -1.0, f10 - f5 + Math.cos(d2) * f5 * -1.0);
            }

            if (f9 - f7 >= 4.5) {
                for (int k = 0; k <= 90; k++) {
                    double d3 = k * (float) (Math.PI / 180.0);
                    GL11.glVertex2d(f9 - f5 + Math.sin(d3) * f5, f10 - f5 + Math.cos(d3) * f5);
                }

                for (int idx = 90; idx <= 180; idx++) {
                    double d4 = idx * (float) (Math.PI / 180.0);
                    GL11.glVertex2d(f9 - f5 + Math.sin(d4) * f5, f8 + f5 + Math.cos(d4) * f5);
                }
            }

            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glScaled(2.0, 2.0, 2.0);
            GlStateManager.popAttrib();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public static void handleFloat(float f, float f2, float f3, float f4) {
        ScaledResolution sr = new ScaledResolution(McpApi.mc);
        double scaleFactor = sr.getScaleFactor();
        double d = f4 * scaleFactor;
        double d2 = f3 * scaleFactor;
        double d3 = f * scaleFactor;
        double d4 = McpApi.mc.displayHeight - f2 * scaleFactor - d;
        GL11.glScissor((int)d3, (int)d4, (int)d2, (int)d);
    }

    public static void resetColor() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void handleInt(int n) {
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        GL11.glColor4f(f2, f3, f4, f);
    }

    public static boolean checkState() {
        return true;
    }

    public static void handleFloat(float f, float f2, float f3, int n) {
        float f4 = (n >> 24 & 0xFF) / 255.0F;
        float f5 = (n >> 16 & 0xFF) / 255.0F;
        float f6 = (n >> 8 & 0xFF) / 255.0F;
        float f7 = ((0xFF | ~n) - ~n) / 255.0F;
        double d = f + f3;
        double d2 = f2 + f3;
        double d3 = f + f3 * 2.0F;
        double d4 = f2 + f3 * 2.0F;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(1.0F);
        GlStateManager.color(f5, f6, f7, f4);
        GL11.glBegin(GL11.GL_POLYGON);
        double d5 = Math.PI / 180.0;

        for (double i = 0.0; i <= 90.0; i++) {
            GL11.glVertex2d(d3 + Math.sin(i * d5) * f3, d4 + Math.cos(i * d5) * f3);
        }

        for (double j = 90.0; j <= 180.0; j++) {
            GL11.glVertex2d(d3 + Math.sin(j * d5) * f3, d2 + Math.cos(j * d5) * f3);
        }

        for (double k = 180.0; k <= 270.0; k++) {
            GL11.glVertex2d(d + Math.sin(k * d5) * f3, d2 + Math.cos(k * d5) * f3);
        }

        for (double idx = 270.0; idx <= 360.0; idx++) {
            GL11.glVertex2d(d + Math.sin(idx * d5) * f3, d4 + Math.cos(idx * d5) * f3);
        }

        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.popMatrix();
    }

    private static Font computeFont(String str, int n) {
        return FontResourceLoader.computeFont(str, n);
    }

    public static void handleColor(Color color) {
        handleInt3(color.getRGB());
    }

    public static void handleFloat(float f, float f2, double d, int n, int n2) {
        float f3 = (n >> 24 & 0xFF) / 255.0F;
        float f4 = (n >> 16 & 0xFF) / 255.0F;
        float f5 = ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F;
        float f6 = ((0xFF | ~n) - ~n) / 255.0F;
        GL11.glColor4f(f4, f5, f6, f3);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / n2; i++) {
            double d2 = Math.sin(i * n2 * Math.PI / 180.0) * d;
            double d3 = Math.cos(i * n2 * Math.PI / 180.0) * d;
            GL11.glVertex2d(f + d2, f2 + d3);
        }

        GL11.glEnd();
    }

    private static int computeInt2(int n) {
        if (n == 2) {
            return 1884684117;
        }
        if (n == 3) {
            return 1879091882;
        }
        if (n == 4) {
            return 1890189312;
        }
        return n >= 5 ? 1895803392 : 1895825407;
    }

    public static Framebuffer computeFramebuffer(Framebuffer framebuffer) {
        return computeFramebuffer(framebuffer, false);
    }

    public static void handleFlag(boolean flag) {
        if (flag) {
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(1.0F);
    }

    public static void handleBlockPos(BlockPos pos, int n) {
        Minecraft mc = Minecraft.getMinecraft();
        double d = pos.getX() - mc.getRenderManager().renderPosX + 0.5;
        double d2 = pos.getY() - mc.getRenderManager().renderPosY + 0.5;
        double d3 = pos.getZ() - mc.getRenderManager().renderPosZ + 0.5;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(1.0F);
        float f = (float)(mc.thePlayer.posX - pos.getX());
        float f2 = (float)(mc.thePlayer.posY - pos.getY());
        float f3 = (n >> 16 & 0xFF) / 255.0F;
        float f4 = (n >> 8 & 0xFF) / 255.0F;
        float f5 = (n & 0xFF) / 255.0F;
        float f6 = (n >> 24 & 0xFF) / 255.0F;
        GL11.glColor4f(f3, f4, f5, f6);
        GL11.glLoadIdentity();
        boolean viewBobbing = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.orientCamera(mc.timer.renderPartialTicks);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(0.0, mc.thePlayer.getEyeHeight(), 0.0);
        GL11.glVertex3d(d, d2, d3);
        GL11.glVertex3d(d, d2, d3);
        GL11.glEnd();
        mc.gameSettings.viewBobbing = viewBobbing;
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static double[] computeDoubleArray(double d, double d2, double d3, float f) {
        RenderManager renderManager = McpApi.mc.getRenderManager();
        double viewerPosX = renderManager.viewerPosX;
        double viewerPosY = renderManager.viewerPosY;
        double viewerPosZ = renderManager.viewerPosZ;
        double d4 = d - viewerPosX;
        double d5 = d2 - viewerPosY;
        double d6 = d3 - viewerPosZ;
        float f2 = McpApi.mc.thePlayer.prevRotationYaw + (McpApi.mc.thePlayer.rotationYaw - McpApi.mc.thePlayer.prevRotationYaw) * f;
        float f3 = McpApi.mc.thePlayer.prevRotationPitch + (McpApi.mc.thePlayer.rotationPitch - McpApi.mc.thePlayer.prevRotationPitch) * f;
        double rad = Math.toRadians(f2);
        double rad2 = Math.toRadians(f3);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double cos2 = Math.cos(rad2);
        double sin2 = Math.sin(rad2);
        double d7 = d4 * cos + d6 * sin;
        double d8 = d5 * cos2 - (d4 * sin - d6 * cos) * sin2;
        double d9 = d5 * sin2 + (d4 * sin - d6 * cos) * cos2;
        if (d9 <= 0.0) {
            return null;
        }
        ScaledResolution sr = new ScaledResolution(McpApi.mc);
        double scaledWidth = sr.getScaledWidth();
        double scaledHeight = sr.getScaledHeight();
        double fovSetting = McpApi.mc.gameSettings.fovSetting;
        double rad3 = Math.toRadians(fovSetting);
        double d10 = scaledWidth / scaledHeight;
        double d11 = scaledWidth / 2.0 + d7 / d9 * (scaledWidth / (2.0 * Math.tan(rad3 / 2.0)));
        double d12 = scaledHeight / 2.0 - d8 / d9 * (scaledHeight / (2.0 * Math.tan(rad3 / 2.0)));
        return new double[]{d11, d12, d9};
    }

    public static Color computeColor(float f, float f2, float f3) {
        return Color.getHSBColor(f, f2, f3);
    }

    public static void handleDouble(double d, double d2, double d3, double d4, double d5, double d6, int n) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        EnableDepthHelper.process5();
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        handleInt3(n);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(d, d2);
        GL11.glVertex2d(d3, d4);
        GL11.glVertex2d(d5, d6);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        EnableDepthHelper.process6();
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
    }

    private void handleEntityLivingBase(EntityLivingBase living, Color color) {
        if (McpApi.mc.getRenderManager() != null) {
            double d = computeDouble(living.posX, living.lastTickPosX) - McpApi.mc.getRenderManager().renderPosX;
            double d2 = computeDouble(living.posY, living.lastTickPosY) - McpApi.mc.getRenderManager().renderPosY;
            double d3 = computeDouble(living.posZ, living.lastTickPosZ) - McpApi.mc.getRenderManager().renderPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GL11.glLineWidth(2.0F);
            AxisAlignedBB box = new AxisAlignedBB(d - living.width, d2, d3 - living.width, d + living.width, d2 + living.height, d3 + living.width);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
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
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }

    private static String computeString(int n) {
        if (n != 1) {
            if (n == 2) {
                return "§a";
            }

            if (n == 3) {
                return "§3";
            }

            if (n == 4) {
                return "§4";
            }

            if (n >= 5) {
                return "§e";
            }
        }

        return "§f";
    }

    public static void handleDouble2(double d, double d2, double d3, double d4, int n) {
        if (d < d3) {
            double d5 = d;
            d = d3;
            d3 = d5;
        }

        if (d2 < d4) {
            double d6 = d2;
            d2 = d4;
            d4 = d6;
        }

        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(f2, f3, f4, f);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d, d2, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
        worldRenderer.func_181662_b(d3, d2, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
        worldRenderer.func_181662_b(d3, d4, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
        worldRenderer.func_181662_b(d, d4, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static double[] computeDoubleArray2(double d, double d2, double d3, float f) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity renderViewEntity = mc.getRenderViewEntity();
        if (renderViewEntity == null) {
            return null;
        }
        double d4 = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * f;
        double d5 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * f;
        double d6 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * f;
        double d7 = d - d4;
        double d8 = d2 - d5;
        double d9 = d3 - d6;
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, floatBuffer);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, floatBuffer2);
        floatBuffer4.put(0, (float)d7);
        floatBuffer4.put(1, (float)d8);
        floatBuffer4.put(2, (float)d9);
        floatBuffer4.put(3, 1.0F);
        intBuffer.put(0, 0);
        intBuffer.put(1, 0);
        intBuffer.put(2, mc.displayWidth);
        intBuffer.put(3, mc.displayHeight);
        GLU.gluProject((float)d7, (float)d8, (float)d9, floatBuffer, floatBuffer2, intBuffer, floatBuffer4);
        if (floatBuffer4.get(3) <= 0.0F) {
            return null;
        }
        int guiScale = mc.gameSettings.guiScale;
        if (scaledResolution == null || count != mc.displayWidth || count2 != mc.displayHeight || count3 != guiScale) {
            scaledResolution = new ScaledResolution(mc);
            count = mc.displayWidth;
            count2 = mc.displayHeight;
            count3 = guiScale;
        }

        double scaledWidth = scaledResolution.getScaledWidth();
        double scaledHeight = scaledResolution.getScaledHeight();
        double d10 = floatBuffer4.get(0) / floatBuffer4.get(3);
        double d11 = (mc.displayHeight - floatBuffer4.get(1)) / floatBuffer4.get(3);
        d10 = d10 * scaledWidth / mc.displayWidth;
        d11 = d11 * scaledHeight / mc.displayHeight;
        double d12 = floatBuffer4.get(2) / floatBuffer4.get(3);
        return new double[]{d10, d11, d12};
    }

    public static void handleDouble(double d, double d2, double d3) {
        GlStateManager.translate(
            d - McpApi.mc.getRenderManager().viewerPosX, d2 - McpApi.mc.getRenderManager().viewerPosY, d3 - McpApi.mc.getRenderManager().viewerPosZ
        );
    }

    public static void handleDouble(double d, double d2, double d3, double d4, int n) {
        int n2 = (0xFF | ~(n >> 16)) - ~(n >> 16);
        int n3 = n >> 8 & 0xFF;
        int n4 = n & 0xFF;
        int n5 = n >> 24 & 0xFF;
        handleFloat((float)d, (float)d2, (float)d3, (float)d4, n2, n3, n4, n5);
    }

    public static boolean matchesAxisAlignedBB2(AxisAlignedBB box) {
        frustum.setPosition(McpApi.mc.thePlayer.posX, McpApi.mc.thePlayer.posY, McpApi.mc.thePlayer.posZ);
        return frustum.isBoundingBoxInFrustum(box);
    }

    public static void handleAxisAlignedBB3(AxisAlignedBB box) {
        GL11.glBegin(GL11.GL_QUADS);
        handleVec3(computeVec3(box.minX, box.minY, box.minZ));
        handleVec3(computeVec3(box.minX, box.maxY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.maxY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.maxZ));
        handleVec3(computeVec3(box.maxX, box.maxY, box.maxZ));
        handleVec3(computeVec3(box.minX, box.minY, box.maxZ));
        handleVec3(computeVec3(box.minX, box.maxY, box.maxZ));
        end();
        GL11.glBegin(GL11.GL_QUADS);
        handleVec3(computeVec3(box.maxX, box.maxY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.minZ));
        handleVec3(computeVec3(box.minX, box.maxY, box.minZ));
        handleVec3(computeVec3(box.minX, box.minY, box.minZ));
        handleVec3(computeVec3(box.minX, box.maxY, box.maxZ));
        handleVec3(computeVec3(box.minX, box.minY, box.maxZ));
        handleVec3(computeVec3(box.maxX, box.maxY, box.maxZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.maxZ));
        end();
        GL11.glBegin(GL11.GL_QUADS);
        handleVec3(computeVec3(box.minX, box.maxY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.maxY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.maxY, box.maxZ));
        handleVec3(computeVec3(box.minX, box.maxY, box.maxZ));
        handleVec3(computeVec3(box.minX, box.maxY, box.minZ));
        handleVec3(computeVec3(box.minX, box.maxY, box.maxZ));
        handleVec3(computeVec3(box.maxX, box.maxY, box.maxZ));
        handleVec3(computeVec3(box.maxX, box.maxY, box.minZ));
        end();
        GL11.glBegin(GL11.GL_QUADS);
        handleVec3(computeVec3(box.minX, box.minY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.maxZ));
        handleVec3(computeVec3(box.minX, box.minY, box.maxZ));
        handleVec3(computeVec3(box.minX, box.minY, box.minZ));
        handleVec3(computeVec3(box.minX, box.minY, box.maxZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.maxZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.minZ));
        end();
        GL11.glBegin(GL11.GL_QUADS);
        handleVec3(computeVec3(box.minX, box.minY, box.minZ));
        handleVec3(computeVec3(box.minX, box.maxY, box.minZ));
        handleVec3(computeVec3(box.minX, box.minY, box.maxZ));
        handleVec3(computeVec3(box.minX, box.maxY, box.maxZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.maxZ));
        handleVec3(computeVec3(box.maxX, box.maxY, box.maxZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.maxY, box.minZ));
        end();
        GL11.glBegin(GL11.GL_QUADS);
        handleVec3(computeVec3(box.minX, box.maxY, box.maxZ));
        handleVec3(computeVec3(box.minX, box.minY, box.maxZ));
        handleVec3(computeVec3(box.minX, box.maxY, box.minZ));
        handleVec3(computeVec3(box.minX, box.minY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.maxY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.minZ));
        handleVec3(computeVec3(box.maxX, box.maxY, box.maxZ));
        handleVec3(computeVec3(box.maxX, box.minY, box.maxZ));
        end();
    }

    public static void handleVec3(Vec3 vec) {
        GL11.glVertex3d(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static double[] computeDoubleArray3(double d, double d2, double d3, float f) {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, floatBuffer);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, floatBuffer2);
        GL11.glGetInteger(GL11.GL_VIEWPORT, intBuffer);
        boolean flag = GLU.gluProject((float)d, (float)d2, (float)d3, floatBuffer, floatBuffer2, intBuffer, floatBuffer3);
        if (!flag) {
            return null;
        }
        float f2 = floatBuffer3.get(0);
        float f3 = floatBuffer3.get(1);
        float f4 = floatBuffer3.get(2);
        ScaledResolution sr = new ScaledResolution(McpApi.mc);
        f2 = f2 / McpApi.mc.displayWidth * sr.getScaledWidth();
        f3 = (1.0F - f3 / McpApi.mc.displayHeight) * sr.getScaledHeight();
        return !(f4 < 0.0F) && !(f4 >= 1.0F) ? new double[]{f2, f3, f4} : null;
    }

    public static void process7() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    public static Vec3 computeVec3(double d, double d2, double d3) {
        d -= McpApi.mc.getRenderManager().renderPosX;
        d2 -= McpApi.mc.getRenderManager().renderPosY;
        d3 -= McpApi.mc.getRenderManager().renderPosZ;
        return new Vec3(d, d2, d3);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, int n, int n2, int n3, int n4) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(n / 255.0F, n2 / 255.0F, n3 / 255.0F, n4 / 255.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(f, f2);
        GL11.glVertex2f(f, f2 + f4);
        GL11.glVertex2f(f + f3, f2 + f4);
        GL11.glVertex2f(f + f3, f2);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static boolean matchesAxisAlignedBB(AxisAlignedBB box) {
        Entity renderViewEntity = McpApi.mc.getRenderViewEntity();
        frustum.setPosition(renderViewEntity.posX, renderViewEntity.posY, renderViewEntity.posZ);
        return frustum.isBoundingBoxInFrustum(box);
    }

    private static void process6() {
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(-0.5, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, -0.5);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.5, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.5);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glEnd();
        Cylinder cylinder = new Cylinder();
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        cylinder.setDrawStyle(100011);
        cylinder.draw(0.5F, 0.5F, 0.1F, 24, 1);
    }

    public static void bindTexture(int n) {
        GlStateManager.bindTexture(n);
    }

    public static float computeFloat(float f, float f2, float f3) {
        return f2 + (f - f2) * f3;
    }

    public void process5() {
        float renderPartialTicks = McpApi.mc.timer.renderPartialTicks;
        KillAura killAura = (KillAura)Brutality.moduleManager.computeModule("Kill Aura");
        EntityLivingBase entityLivingBase2 = KillAura.entityLivingBase2;
        if (McpApi.mc.getRenderManager() != null && entityLivingBase2 != null) {
            double d = entityLivingBase2.prevPosX + (entityLivingBase2.posX - entityLivingBase2.prevPosX) * renderPartialTicks - McpApi.mc.getRenderManager().renderPosX;
            double d2 = entityLivingBase2.prevPosY
                + (entityLivingBase2.posY - entityLivingBase2.prevPosY) * renderPartialTicks
                + Math.sin(System.currentTimeMillis() / 200.0)
                + 1.0
                - McpApi.mc.getRenderManager().renderPosY;
            double d3 = entityLivingBase2.prevPosZ + (entityLivingBase2.posZ - entityLivingBase2.prevPosZ) * renderPartialTicks - McpApi.mc.getRenderManager().renderPosZ;
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.disableCull();
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            Color WHITE = Color.WHITE;

            for (float i = 0.0F; i <= 6.53451271946677; i += 0.25132743F) {
                double d4 = d + 0.67 * Math.cos(i);
                double d5 = d3 + 0.67 * Math.sin(i);
                handleColor(GetColorHelper.computeColor2(WHITE, 63));
                GL11.glVertex3d(d4, d2, d5);
            }

            for (float j = 0.0F; j <= 6.53451271946677; j = (float)(j + 0.25132741228718347)) {
                double d6 = d + 0.67 * Math.cos(j);
                double d7 = d3 + 0.67 * Math.sin(j);
                handleColor(GetColorHelper.computeColor2(WHITE, 63));
                GL11.glVertex3d(d6, d2, d7);
                handleColor(GetColorHelper.computeColor2(WHITE, 0));
                GL11.glVertex3d(d6, d2 - Math.cos(System.currentTimeMillis() / 200.0) / 2.0, d7);
            }

            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glDepthMask(true);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableCull();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
        }
    }

    public static void process4() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
    }

    public static void handleItemStack(ItemStack stack, int n, int n2) {
        if (stack != null) {
            GlStateManager.pushMatrix();
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableDepth();
            renderItem2.zLevel = 200.0F;
            renderItem2.renderItemAndEffectIntoGUI(stack, n, n2);
            renderItem2.renderItemOverlayIntoGUI(McpApi.mc.fontRendererObj, stack, n, n2, null);
            renderItem2.zLevel = 0.0F;
            GlStateManager.disableDepth();
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }

    public static void handleFloat2(float f, float f2, float f3, float f4, int n, int n2) {
        float f5 = (n >> 24 & 0xFF) / 255.0F;
        float f6 = (n >> 16 & 0xFF) / 255.0F;
        float f7 = (n >> 8 & 0xFF) / 255.0F;
        float f8 = (n & 0xFF) / 255.0F;
        float f9 = (n2 >> 24 & 0xFF) / 255.0F;
        float f10 = (n2 >> 16 & 0xFF) / 255.0F;
        float f11 = ((0xFF | ~(n2 >> 8)) - ~(n2 >> 8)) / 255.0F;
        float f12 = (n2 & 0xFF) / 255.0F;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glVertex2f(f, f2);
        GL11.glVertex2f(f, f2 + f4);
        GL11.glColor4f(f10, f11, f12, f9);
        GL11.glVertex2f(f + f3, f2 + f4);
        GL11.glVertex2f(f + f3, f2);
        GL11.glEnd();
    }

    public static float getTimestamp() {
        return (float)timestamp / 1000.0F;
    }

    public static double computeDouble(double d, double d2) {
        return d2 + (d - d2) * McpApi.mc.timer.renderPartialTicks;
    }

    public static void handleFloat2(float f, float f2, float f3, float f4, int n) {
        Gui.drawRect((int)f, (int)f2, (int)f3, (int)f4, n);
    }

    public static void handleItemStack(ItemStack stack, float f, float f2) {
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
        if (stack.getItem() instanceof ItemArmor) {
            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
            int enchantmentLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            int enchantmentLevel3 = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
            if (enchantmentLevel > 0) {
                computeFloat(String.valueOf(enchantmentLevel), computeFloat("P", f, f2, -16777216, -1) + 2.0F, f2, computeInt2(enchantmentLevel), computeInt(enchantmentLevel));
                f2 += 5.0F;
            }

            if (enchantmentLevel2 > 0) {
                computeFloat(String.valueOf(enchantmentLevel2), computeFloat("U", f, f2, -16777216, -1) + 2.0F, f2, computeInt2(enchantmentLevel2), computeInt(enchantmentLevel2));
                f2 += 5.0F;
            }

            if (enchantmentLevel3 > 0) {
                computeFloat(String.valueOf(enchantmentLevel3), computeFloat("T", f, f2, -16777216, -1) + 2.0F, f2, computeInt2(enchantmentLevel3), computeInt(enchantmentLevel3));
                f2 += 5.0F;
            }
        }

        if (stack.getItem() instanceof ItemBow) {
            int enchantmentLevel4 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            int enchantmentLevel5 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            int enchantmentLevel6 = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
            int enchantmentLevel7 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (enchantmentLevel4 > 0) {
                computeFloat(String.valueOf(enchantmentLevel4), computeFloat("Pow", f, f2, -16777216, -1) + 2.0F, f2, computeInt2(enchantmentLevel4), computeInt(enchantmentLevel4));
                f2 += 5.0F;
            }

            if (enchantmentLevel5 > 0) {
                computeFloat(String.valueOf(enchantmentLevel5), computeFloat("Pun", f, f2, -16777216, -1) + 2.0F, f2, computeInt2(enchantmentLevel5), computeInt(enchantmentLevel5));
                f2 += 5.0F;
            }

            if (enchantmentLevel6 > 0) {
                computeFloat(String.valueOf(enchantmentLevel6), computeFloat("F", f, f2, -16777216, -1) + 2.0F, f2, computeInt2(enchantmentLevel6), computeInt(enchantmentLevel6));
                f2 += 5.0F;
            }

            if (enchantmentLevel7 > 0) {
                computeFloat(String.valueOf(enchantmentLevel7), computeFloat("U", f, f2, -16777216, -1) + 2.0F, f2, computeInt2(enchantmentLevel7), computeInt(enchantmentLevel7));
                f2 += 5.0F;
            }
        }

        if (stack.getItem() instanceof ItemSword) {
            int enchantmentLevel8 = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            int enchantmentLevel9 = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack);
            int enchantmentLevel10 = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
            int enchantmentLevel11 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (enchantmentLevel8 > 0) {
                computeFloat(String.valueOf(enchantmentLevel8), computeFloat("S", f, f2, -16777216, -1) + 2.0F, f2, computeInt2(enchantmentLevel8), computeInt(enchantmentLevel8));
                f2 += 5.0F;
            }

            if (enchantmentLevel9 > 0) {
                computeFloat(String.valueOf(enchantmentLevel9), computeFloat("K", f, f2, -16777216, -1) + 2.0F, f2, computeInt2(enchantmentLevel9), computeInt(enchantmentLevel9));
                f2 += 5.0F;
            }

            if (enchantmentLevel10 > 0) {
                computeFloat(String.valueOf(enchantmentLevel10), computeFloat("F", f, f2, -16777216, -1) + 2.0F, f2, computeInt2(enchantmentLevel10), computeInt(enchantmentLevel10));
                f2 += 5.0F;
            }

            if (enchantmentLevel11 > 0) {
                computeFloat(String.valueOf(enchantmentLevel11), computeFloat("U", f, f2, -16777216, -1) + 2.0F, f2, computeInt2(enchantmentLevel11), computeInt(enchantmentLevel11));
            }
        }

        GlStateManager.enableDepth();
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
    }

    public static void handleBlockPos(BlockPos pos, Color color, boolean flag, boolean flag2, float f) {
        handleBlockPos2(pos, color, flag, flag2, f);
    }

    public static boolean matchesFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null
            || framebuffer.framebufferWidth != McpApi.mc.displayWidth
            || framebuffer.framebufferHeight != McpApi.mc.displayHeight
            || OpenGlHelper.isFramebufferEnabled() && framebuffer.framebufferObject < 0;
    }

    public static void process3() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, int n, boolean flag) {
        float f6 = f;
        float f7 = f2;
        float f8 = f3;
        float f9 = f4;
        if (!flag) {
            f6 = f - f5;
            f7 = f2 - f5;
            f8 = f3 + f5;
            f9 = f4 + f5;
        }

        handleFloat2(f6, f7, f8, f7 + f5, n);
        handleFloat2(f6, f9 - f5, f8, f9, n);
        handleFloat2(f6, f7 + f5, f6 + f5, f9 - f5, n);
        handleFloat2(f8 - f5, f7 + f5, f8, f9 - f5, n);
    }

    public static void handleBlockPos2(BlockPos pos, Color color, boolean flag, boolean flag2, float f) {
        double d = pos.getX() - McpApi.mc.getRenderManager().renderPosX;
        double d2 = pos.getY() - McpApi.mc.getRenderManager().renderPosY;
        double d3 = pos.getZ() - McpApi.mc.getRenderManager().renderPosZ;
        AxisAlignedBB box = new AxisAlignedBB(d, d2, d3, d + 1.0, d2 + 1.0, d3 + 1.0);
        Block block = McpApi.mc.theWorld.getBlockState(pos).getBlock();
        if (block != null) {
            double d4 = McpApi.mc.thePlayer.lastTickPosX
                + (McpApi.mc.thePlayer.posX - McpApi.mc.thePlayer.lastTickPosX) * McpApi.mc.timer.renderPartialTicks;
            double d5 = McpApi.mc.thePlayer.lastTickPosY
                + (McpApi.mc.thePlayer.posY - McpApi.mc.thePlayer.lastTickPosY) * McpApi.mc.timer.renderPartialTicks;
            double d6 = McpApi.mc.thePlayer.lastTickPosZ
                + (McpApi.mc.thePlayer.posZ - McpApi.mc.thePlayer.lastTickPosZ) * McpApi.mc.timer.renderPartialTicks;
            box = block.getSelectedBoundingBox(McpApi.mc.theWorld, pos).expand(0.002F, 0.002F, 0.002F).offset(-d4, -d5, -d6);
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        if (flag2) {
            GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            handleAxisAlignedBB(box);
        }

        if (flag) {
            GL11.glLineWidth(f);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            handleAxisAlignedBB2(box);
        }

        GlStateManager.resetColor();
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void handleEntity(Entity entity, double[] doubles, double d, double d2, double d3) {
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        if (doubles.length >= 4) {
            if (doubles[3] <= 0.1) {
                return;
            }

            GL11.glColor4d(doubles[0], doubles[1], doubles[2], doubles[3]);
        } else {
            GL11.glColor3d(doubles[0], doubles[1], doubles[2]);
        }

        GL11.glLineWidth(1.5F);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0.0, McpApi.mc.thePlayer.getEyeHeight(), 0.0);
        GL11.glVertex3d(d, d2, d3);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static void handleAxisAlignedBB2(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(3, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        tessellator.draw();
        worldRenderer.func_181668_a(3, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        tessellator.draw();
        worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        tessellator.draw();
    }

    public static void process2() {
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.disableBlend();
    }

    public static void handleFloat(float f, float f2, float f3, float f4, int n) {
        Gui.drawRect((int)f, (int)f2, (int)(f + f3), (int)(f2 + f4), n);
    }

    public static void process() {
        GL11.glPopMatrix();
    }

    public static void handleAxisAlignedBB(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        tessellator.draw();
    }

    public static void handleDouble(
        double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4, double d7
    ) {
        double d8 = d - d7;
        double d9 = d4 + d7;
        double d10 = d2 - d7;
        double d11 = d5 + d7;
        double d12 = d3 - d7;
        double d13 = d6 + d7;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(f, f2, f3, f4);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(d8, d10, d12);
        GL11.glVertex3d(d9, d10, d12);
        GL11.glVertex3d(d9, d10, d13);
        GL11.glVertex3d(d8, d10, d13);
        GL11.glVertex3d(d8, d11, d12);
        GL11.glVertex3d(d9, d11, d12);
        GL11.glVertex3d(d9, d11, d13);
        GL11.glVertex3d(d8, d11, d13);
        GL11.glVertex3d(d8, d10, d12);
        GL11.glVertex3d(d9, d10, d12);
        GL11.glVertex3d(d9, d11, d12);
        GL11.glVertex3d(d8, d11, d12);
        GL11.glVertex3d(d8, d10, d13);
        GL11.glVertex3d(d9, d10, d13);
        GL11.glVertex3d(d9, d11, d13);
        GL11.glVertex3d(d8, d11, d13);
        GL11.glVertex3d(d8, d10, d12);
        GL11.glVertex3d(d8, d10, d13);
        GL11.glVertex3d(d8, d11, d13);
        GL11.glVertex3d(d8, d11, d12);
        GL11.glVertex3d(d9, d10, d12);
        GL11.glVertex3d(d9, d10, d13);
        GL11.glVertex3d(d9, d11, d13);
        GL11.glVertex3d(d9, d11, d12);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void process11() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    public static void process12() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        McpApi.mc.entityRenderer.setupCameraTransform(McpApi.mc.timer.renderPartialTicks, 0);
    }

    public static void handleInt(int n, float f) {
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        GlStateManager.color(f2, f3, f4, f);
    }

    public static void process10() {
        OpenGlHelper.glUseProgram(0);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(0);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
    }

    public static void handleBlockPos(BlockPos pos, Color color, float f, boolean flag, boolean flag2) {
        double d = pos.getX() - McpApi.mc.getRenderManager().renderPosX;
        double d2 = pos.getY() - McpApi.mc.getRenderManager().renderPosY;
        double d3 = pos.getZ() - McpApi.mc.getRenderManager().renderPosZ;
        AxisAlignedBB box = new AxisAlignedBB(d, d2, d3, d + 1.0, d2 + 0.5625, d3 + 1.0);
        Block block = McpApi.mc.theWorld.getBlockState(pos).getBlock();
        if (block instanceof BlockBed) {
            double d4 = McpApi.mc.thePlayer.lastTickPosX
                + (McpApi.mc.thePlayer.posX - McpApi.mc.thePlayer.lastTickPosX) * McpApi.mc.timer.renderPartialTicks;
            double d5 = McpApi.mc.thePlayer.lastTickPosY
                + (McpApi.mc.thePlayer.posY - McpApi.mc.thePlayer.lastTickPosY) * McpApi.mc.timer.renderPartialTicks;
            double d6 = McpApi.mc.thePlayer.lastTickPosZ
                + (McpApi.mc.thePlayer.posZ - McpApi.mc.thePlayer.lastTickPosZ) * McpApi.mc.timer.renderPartialTicks;
            box = block.getSelectedBoundingBox(McpApi.mc.theWorld, pos).expand(0.002, 0.002, 0.002).offset(-d4, -d5, -d6);
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        if (flag) {
            GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            handleAxisAlignedBB(box);
        }

        if (flag2) {
            GL11.glLineWidth(f);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            handleAxisAlignedBB2(box);
        }

        GlStateManager.resetColor();
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        handleFloat2(f, f2 - f4 / 4.0F, f3, f4 / 4.0F, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0).getRGB(), color.getRGB());
        handleFloat2(f, f2 + f4, f3, f4 / 4.0F, color.getRGB(), new Color(color.getRed(), color.getGreen(), color.getBlue(), 0).getRGB());
        handleFloat(f - f3 / 4.0F, f2, f3 / 4.0F, f4, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0).getRGB(), color.getRGB());
        handleFloat(f + f3, f2, f3 / 4.0F, f4, color.getRGB(), new Color(color.getRed(), color.getGreen(), color.getBlue(), 0).getRGB());
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void handleDouble(double d, int n, int n2, int n3) {
        handleFloat2(
            (float)(McpApi.mc.displayWidth / 4 - 80),
            (float)(McpApi.mc.displayHeight / 4 + 20),
            (float)(McpApi.mc.displayWidth / 4 + 80),
            (float)(McpApi.mc.displayHeight / 4 + 33),
            10.0F,
            n3
        );
        handleFloat(
            McpApi.mc.displayWidth / 4 - 80,
            McpApi.mc.displayHeight / 4 + 20,
            (float)(McpApi.mc.displayWidth / 4 - 70 + d * 150.0),
            McpApi.mc.displayHeight / 4 + 33,
            10.0F,
            n,
            n,
            n2,
            n2
        );
    }

    public static void handleDouble(double d, ColorSetting colorSetting) {
        for (Entity entity : McpApi.mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityTNTPrimed entity2) {
                double d2 = entity2.posX - McpApi.mc.getRenderManager().renderPosX;
                double d3 = entity2.posY - McpApi.mc.getRenderManager().renderPosY;
                double d4 = entity2.posZ - McpApi.mc.getRenderManager().renderPosZ;
                GL11.glPushMatrix();
                GL11.glTranslated(d2, d3, d4);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                Color color = colorSetting.computeColor();
                float f = color.getRed() / 255.0F;
                float f2 = color.getGreen() / 255.0F;
                float f3 = color.getBlue() / 255.0F;
                GL11.glColor4d(f, f2, f3, 0.2);
                Sphere sphere = new Sphere();
                sphere.setDrawStyle(100012);
                sphere.draw(8.0F, 15, 15);
                float f4 = 8.0F;
                Vec3 vec = new Vec3(entity2.posX, entity2.posY, entity2.posZ);
                if (!McpApi.mc.thePlayer.isImmuneToExplosions()) {
                    double d5 = McpApi.mc.thePlayer.getDistance(entity2.posX, entity2.posY, entity2.posZ) / f4;
                    if (d5 <= 1.0) {
                        double d6 = McpApi.mc.thePlayer.posX - entity2.posX;
                        double d7 = McpApi.mc.thePlayer.posY + McpApi.mc.thePlayer.getEyeHeight() - entity2.posY;
                        double d8 = McpApi.mc.thePlayer.posZ - entity2.posY;
                        double d9 = MathHelper.sqrt_double(d6 * d6 + d7 * d7 + d8 * d8);
                        if (d9 != 0.0) {
                            double blockDensity = McpApi.mc.theWorld.getBlockDensity(vec, McpApi.mc.thePlayer.getEntityBoundingBox());
                            double d10 = (1.0 - d5) * blockDensity;
                            d += (int)((d10 * d10 + d10) / 2.0 * 8.0 * f4 + 1.0);
                        }
                    }
                }

                GL11.glColor4d(f, f2, f3, 1.0);
                Sphere sphere2 = new Sphere();
                sphere2.setDrawStyle(100011);
                sphere2.draw(8.1F, 15, 15);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }
    }

    public static void handleFloat(float f, float f2, float f3, int n, int n2) {
        GL11.glPushMatrix();
        float f4 = f * 2.0F;
        float f5 = f2 * 2.0F;
        float f6 = ((0xFF | ~(n2 >> 24)) - ~(n2 >> 24)) / 255.0F;
        float f7 = (n2 >> 16 & 0xFF) / 255.0F;
        float f8 = ((0xFF | ~(n2 >> 8)) - ~(n2 >> 8)) / 255.0F;
        float f9 = (n2 & 0xFF) / 255.0F;
        float f10 = (float)(6.2831852 / n);
        float cos = (float)Math.cos(f10);
        float sin = (float)Math.sin(f10);
        float f11;
        float f12 = f11 = f3 * 2.0F;
        float f13 = 0.0F;
        process3();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glColor4f(f7, f8, f9, f6);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int i = 0; i < n; i++) {
            GL11.glVertex2f(f12 + f4, f13 + f5);
            float f14 = f12;
            f12 = cos * f12 - sin * f13;
            f13 = sin * f14 + cos * f13;
        }

        GL11.glEnd();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        process4();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public static void handleDouble2(double d, double d2, double d3, double d4, double d5, double d6, int n) {
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(d, d2, d3);
        GL11.glVertex3d(d4, d2, d3);
        GL11.glVertex3d(d4, d2, d3);
        GL11.glVertex3d(d4, d2, d6);
        GL11.glVertex3d(d4, d2, d6);
        GL11.glVertex3d(d, d2, d6);
        GL11.glVertex3d(d, d2, d6);
        GL11.glVertex3d(d, d2, d3);
        GL11.glVertex3d(d, d5, d3);
        GL11.glVertex3d(d4, d5, d3);
        GL11.glVertex3d(d4, d5, d3);
        GL11.glVertex3d(d4, d5, d6);
        GL11.glVertex3d(d4, d5, d6);
        GL11.glVertex3d(d, d5, d6);
        GL11.glVertex3d(d, d5, d6);
        GL11.glVertex3d(d, d5, d3);
        GL11.glVertex3d(d, d2, d3);
        GL11.glVertex3d(d, d5, d3);
        GL11.glVertex3d(d4, d2, d3);
        GL11.glVertex3d(d4, d5, d3);
        GL11.glVertex3d(d4, d2, d6);
        GL11.glVertex3d(d4, d5, d6);
        GL11.glVertex3d(d, d2, d6);
        GL11.glVertex3d(d, d5, d6);
        GL11.glEnd();
    }

    public static void handleDouble4(double d, double d2, double d3, double d4, int n) {
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(f2, f3, f4, f);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d, d4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(d3, d4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(d3, d2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(d, d2, 0.0).func_181675_d();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void handleBlockPos3(BlockPos pos, Color color, boolean flag, boolean flag2, float f) {
        double d = pos.getX() - McpApi.mc.getRenderManager().renderPosX;
        double d2 = pos.getY() - McpApi.mc.getRenderManager().renderPosY;
        double d3 = pos.getZ() - McpApi.mc.getRenderManager().renderPosZ;
        AxisAlignedBB box = new AxisAlignedBB(d, d2, d3, d + 1.0, d2 + 0.5625, d3 + 1.0);
        Block block = McpApi.mc.theWorld.getBlockState(pos).getBlock();
        if (block instanceof BlockBed) {
            double d4 = McpApi.mc.thePlayer.lastTickPosX
                + (McpApi.mc.thePlayer.posX - McpApi.mc.thePlayer.lastTickPosX) * McpApi.mc.timer.renderPartialTicks;
            double d5 = McpApi.mc.thePlayer.lastTickPosY
                + (McpApi.mc.thePlayer.posY - McpApi.mc.thePlayer.lastTickPosY) * McpApi.mc.timer.renderPartialTicks;
            double d6 = McpApi.mc.thePlayer.lastTickPosZ
                + (McpApi.mc.thePlayer.posZ - McpApi.mc.thePlayer.lastTickPosZ) * McpApi.mc.timer.renderPartialTicks;
            box = block.getSelectedBoundingBox(McpApi.mc.theWorld, pos).expand(0.002F, 0.002F, 0.002F).offset(-d4, -d5, -d6);
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        if (flag2) {
            GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            handleAxisAlignedBB(box);
        }

        if (flag) {
            GL11.glLineWidth(f);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            handleAxisAlignedBB2(box);
        }

        GlStateManager.resetColor();
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void handleEntityLivingBase(EntityLivingBase living, float f, float f2, float f3, float f4, int n) {
        if (living instanceof AbstractClientPlayer player) {
            BindTextureHelper2.process4();
            handleFloat3(f, f2, f3, f3, f4, -1);
            BindTextureHelper2.handleInt2(1);
            handleInt3(n);
            DisableDepthHelper.process6();
            McpApi.mc.getTextureManager().bindTexture(player.getLocationSkin());
            Gui.drawScaledCustomSizeModalRect((int)f, (int)f2, 8.0F, 8.0F, 8, 8, (int)f3, (int)f3, 64.0F, 64.0F);
            DisableDepthHelper.process5();
            BindTextureHelper2.process();
        }
    }

    public static void process9() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void handleFloat2(float f, float f2, float f3, float f4, float f5, int n) {
        if (!(f3 <= f)) {
            float f6 = f3 - f;
            float f7 = f4 - f2;
            if (!(f5 <= 1.5F) && !(f6 < 6.0F) && !(f7 < 6.0F)) {
                if (f6 < 5.0F) {
                    f5 = Math.min(f5, f6 / 2.0F);
                }

                float f8 = (float)(f * 2.0);
                float f9 = (float)(f2 * 2.0);
                float f10 = (float)(f3 * 2.0);
                float f11 = (float)(f4 * 2.0);
                GL11.glPushAttrib(0);
                GL11.glScaled(0.5, 0.5, 0.5);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glBegin(GL11.GL_POLYGON);
                handleInt(n);

                for (int i = 0; i <= 90; i += 3) {
                    double d = i * (float) (Math.PI / 180.0);
                    GL11.glVertex2d(f8 + f5 + Math.sin(d) * f5 * -1.0, f9 + f5 + Math.cos(d) * f5 * -1.0);
                }

                for (int j = 90; j <= 180; j += 3) {
                    double d2 = j * (float) (Math.PI / 180.0);
                    GL11.glVertex2d(f8 + f5 + Math.sin(d2) * f5 * -1.0, f11 - f5 + Math.cos(d2) * f5 * -1.0);
                }

                if (f10 - f8 >= 4.5) {
                    for (int k = 0; k <= 90; k++) {
                        double d3 = k * (float) (Math.PI / 180.0);
                        GL11.glVertex2d(f10 - f5 + Math.sin(d3) * f5, f11 - f5 + Math.cos(d3) * f5);
                    }

                    for (int idx = 90; idx <= 180; idx++) {
                        double d4 = idx * (float) (Math.PI / 180.0);
                        GL11.glVertex2d(f10 - f5 + Math.sin(d4) * f5, f9 + f5 + Math.cos(d4) * f5);
                    }
                }

                GL11.glEnd();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glScaled(2.0, 2.0, 2.0);
                GlStateManager.popAttrib();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                handleFloat(f, f2, f6, f7, n);
            }
        }
    }

    public static void handleBlockPos(BlockPos pos, int n, double d, boolean flag, boolean flag2) {
        handleBlockPos(pos, new Color(n), flag, flag2, (float)d);
    }

    private static void handleFloat(float f) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)McpApi.mc.thePlayer.posX, (float)McpApi.mc.thePlayer.posY, (float)McpApi.mc.thePlayer.posZ);
        int n = 100;
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int i = 0; i < n; i++) {
            double d = (Math.PI * 2) * i / n;
            double d2 = f * Math.cos(d);
            double d3 = f * Math.sin(d);
            GL11.glVertex3d(d2, 0.0, d3);
        }

        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void handleDouble(double d, double d2, double d3, double d4, double d5, int n, int n2) {
        handleDouble3(d + d5, d2 + d5, d3 - d5, d4 - d5, n);
        handleDouble3(d + d5, d2, d3 - d5, d2 + d5, n2);
        handleDouble3(d, d2, d + d5, d4, n2);
        handleDouble3(d3 - d5, d2, d3, d4, n2);
        handleDouble3(d + d5, d4 - d5, d3 - d5, d4, n2);
    }

    public static void handleEntity(Entity entity, double d, double d2, double d3, float f, float f2) {
        amount = (System.nanoTime() - tick) / 3000000.0;
        if (!McpApi.mc.inGameHasFocus) {
            amount = 0.0;
        }

        EntityItem entity2;
        ItemStack stack;
        if ((stack = (entity2 = (EntityItem)entity).getEntityItem()).getItem() != null) {
            random.setSeed(timestamp2);
            boolean flag = false;
            if (TextureMap.locationBlocksTexture != null) {
                McpApi.mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                McpApi.mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
                flag = true;
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.pushMatrix();
            IBakedModel itemModel = renderItem.getItemModelMesher().getItemModel(stack);
            int n = func_177077_a(entity2, d, d2, d3, f2, itemModel);
            BlockPos pos = new BlockPos(entity2);
            if (entity2.rotationPitch > 360.0F) {
                entity2.rotationPitch = 0.0F;
            }

            if (entity2 != null && !Double.isNaN(entity2.getAge()) && !Double.isNaN(entity2.getAir()) && !Double.isNaN(entity2.getEntityId()) && entity2.getPosition() != null
                )
             {
                if (entity2.onGround) {
                    if (entity2.rotationPitch != 0.0F && entity2.rotationPitch != 90.0F && entity2.rotationPitch != 180.0F && entity2.rotationPitch != 270.0F) {
                        double d4 = computeDouble(entity2.rotationPitch);
                        double d5 = computeDouble(entity2.rotationPitch - 90.0F);
                        double d6 = computeDouble(entity2.rotationPitch - 180.0F);
                        double d7 = computeDouble(entity2.rotationPitch - 270.0F);
                        if (d4 <= d5 && d4 <= d6 && d4 <= d7) {
                            if (entity2.rotationPitch < 0.0F) {
                                entity2.rotationPitch = (float)(entity2.rotationPitch + amount);
                            } else {
                                entity2.rotationPitch = (float)(entity2.rotationPitch - amount);
                            }
                        }

                        if (d5 < d4 && d5 <= d6 && d5 <= d7) {
                            if (entity2.rotationPitch - 90.0F < 0.0F) {
                                entity2.rotationPitch = (float)(entity2.rotationPitch + amount);
                            } else {
                                entity2.rotationPitch = (float)(entity2.rotationPitch - amount);
                            }
                        }

                        if (d6 < d5 && d6 < d4 && d6 <= d7) {
                            if (entity2.rotationPitch - 180.0F < 0.0F) {
                                entity2.rotationPitch = (float)(entity2.rotationPitch + amount);
                            } else {
                                entity2.rotationPitch = (float)(entity2.rotationPitch - amount);
                            }
                        }

                        if (d7 < d5 && d7 < d6 && d7 < d4) {
                            if (entity2.rotationPitch - 270.0F < 0.0F) {
                                entity2.rotationPitch = (float)(entity2.rotationPitch + amount);
                            } else {
                                entity2.rotationPitch = (float)(entity2.rotationPitch - amount);
                            }
                        }
                    }
                } else {
                    BlockPos pos2 = new BlockPos(entity2);
                    pos2.add(0, 1, 0);
                    Material material = entity2.worldObj.getBlockState(pos2).getBlock().getMaterial();
                    Material material2 = entity2.worldObj.getBlockState(pos).getBlock().getMaterial();
                    int n2 = entity2.isInsideOfMaterial(Material.water) ? 1 : 0;
                    int n3 = entity2.inWater ? 1 : 0;
                    if (n3
                            + (
                                (material2 == Material.water ? 1 : 0)
                                        + (
                                            (material == Material.water ? 1 : 0) + (n2 & ~(material == Material.water ? 1 : 0))
                                                & ~(material2 == Material.water ? 1 : 0)
                                        )
                                    & ~n3
                            )
                        != 0) {
                        entity2.rotationPitch = (float)(entity2.rotationPitch + amount / 4.0);
                    } else {
                        entity2.rotationPitch = (float)(entity2.rotationPitch + amount * 2.0);
                    }
                }
            }

            GL11.glRotatef(entity2.rotationYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(entity2.rotationPitch + 90.0F, 1.0F, 0.0F, 0.0F);

            for (int i = 0; i < n; i++) {
                if (itemModel.isAmbientOcclusion()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    renderItem.renderItem(stack, itemModel);
                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.pushMatrix();
                    if (i > 0 && checkState()) {
                        GlStateManager.translate(0.0F, 0.0F, 0.046875F * i);
                    }

                    renderItem.renderItem(stack, itemModel);
                    if (!checkState()) {
                        GlStateManager.translate(0.0F, 0.0F, 0.046875F);
                    }

                    GlStateManager.popMatrix();
                }
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            McpApi.mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            if (flag) {
                McpApi.mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
            }
        }
    }

    public static double computeDouble(double d, double d2, double d3) {
        return d2 + (d - d2) * d3;
    }

    public static void process8() {
        int n = 5;
        int n2 = 5;
        int n3 = 18;

        for (int i = 3; i >= 0; i--) {
            ItemStack stack = McpApi.mc.thePlayer.inventory.armorInventory[i];
            if (stack != null) {
                handleItemStack(stack, (int)n, (int)n2);
                n2 += n3;
            }
        }

        ItemStack heldItem = McpApi.mc.thePlayer.getHeldItem();
        if (heldItem != null) {
            handleItemStack(heldItem, (int)n, (int)n2);
        }
    }

    public static int func_177077_a(EntityItem entity, double d, double d2, double d3, float f, IBakedModel iBakedModel) {
        ItemStack entityItem = entity.getEntityItem();
        Item item = entityItem.getItem();
        if (item == null) {
            return 0;
        }
        boolean ambientOcclusion = iBakedModel.isAmbientOcclusion();
        int n = func_177078_a(entityItem);
        float f3 = 0.0F;
        GlStateManager.translate((float)d, (float)d2 + f3 + 0.25F, (float)d3);
        float f4 = 0.0F;
        if (ambientOcclusion || McpApi.mc.getRenderManager().renderEngine != null && McpApi.mc.gameSettings.fancyGraphics) {
            GlStateManager.rotate(f4, 0.0F, 1.0F, 0.0F);
        }

        if (!ambientOcclusion) {
            f4 = -0.0F * (n - 1) * 0.5F;
            float f5 = -0.0F * (n - 1) * 0.5F;
            float f6 = -0.046875F * (n - 1) * 0.5F;
            GlStateManager.translate(f4, f5, f6);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        return n;
    }

    public static void handleInt2(int n) {
        boolean flag = false;
        float f = 0.0F;
        float f2 = 1.5F;
        float f3 = 0.99F;
        if (McpApi.mc.thePlayer.getHeldItem() != null) {
            Item item = McpApi.mc.thePlayer.getHeldItem().getItem();
            float f4;
            float f5;
            if (item instanceof ItemBow) {
                flag = true;
                f4 = 0.05F;
                f5 = 0.3F;
                float f6 = McpApi.mc.thePlayer.getItemInUseCount() / 20.0F;
                float f7 = (f6 * f6 + f6 * 2.0F) / 3.0F;
                if (f7 < 0.1) {
                    return;
                }

                if (f7 > 1.0F) {
                    f7 = 1.0F;
                }

                f2 = f7 * 3.0F;
            } else if (item instanceof ItemFishingRod) {
                f4 = 0.04F;
                f5 = 0.25F;
                f3 = 0.92F;
            } else if (McpApi.mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion
                && ItemPotion.isSplash(McpApi.mc.thePlayer.getHeldItem().getItemDamage())) {
                f4 = 0.05F;
                f5 = 0.25F;
                f = -20.0F;
                f2 = 0.5F;
            } else {
                if (!(item instanceof ItemSnowball) && !(item instanceof ItemEnderPearl) && !(item instanceof ItemEgg)) {
                    return;
                }

                f4 = 0.03F;
                f5 = 0.25F;
            }

            double d = McpApi.mc.getRenderManager().renderPosX - MathHelper.cos(McpApi.mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
            double d2 = McpApi.mc.getRenderManager().renderPosY + McpApi.mc.thePlayer.getEyeHeight() - 0.1F;
            double d3 = McpApi.mc.getRenderManager().renderPosZ - MathHelper.sin(McpApi.mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
            double d4 = -MathHelper.sin(McpApi.mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
                    * MathHelper.cos(McpApi.mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
                * (flag ? 1.0 : 0.4);
            double d5 = -MathHelper.sin((McpApi.mc.thePlayer.rotationPitch + f) / 180.0F * (float) Math.PI) * (flag ? 1.0 : 0.4);
            double d6 = MathHelper.cos(McpApi.mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
                    * MathHelper.cos(McpApi.mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
                * (flag ? 1.0 : 0.4);
            float f8 = MathHelper.sqrt_double(d4 * d4 + d5 * d5 + d6 * d6);
            d4 /= f8;
            d5 /= f8;
            d6 /= f8;
            double d7 = d4 * f2;
            double d8 = d5 * f2;
            double d9 = d6 * f2;
            MovingObjectPosition hit = null;
            boolean flag2 = false;
            boolean flag3 = false;
            handleFlag(true);
            handleInt(n);
            GL11.glLineWidth(2.0F);
            GL11.glBegin(GL11.GL_LINE_STRIP);

            while (!flag2 && d2 > 0.0) {
                Vec3 vec = new Vec3(d, d2, d3);
                Vec3 vec2 = new Vec3(d + d7, d2 + d8, d3 + d9);
                hit = McpApi.mc.theWorld.rayTraceBlocks(vec, vec2, false, true, false);
                vec = new Vec3(d, d2, d3);
                Vec3 vec3 = new Vec3(d + d7, d2 + d8, d3 + d9);
                if (hit != null) {
                    flag2 = true;
                    vec3 = new Vec3(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
                }

                AxisAlignedBB box = new AxisAlignedBB(d - f5, d2 - f5, d3 - f5, d + f5, d2 + f5, d3 + f5);
                var entities = computeList(box.addCoord(d7, d8, d9).expand(1.0, 1.0, 1.0));

                for (int i = 0; i < entities.size(); i++) {
                    Entity entity = (Entity)entities.get(i);
                    if (entity.canBeCollidedWith() && entity != McpApi.mc.thePlayer) {
                        AxisAlignedBB box2 = entity.getEntityBoundingBox().expand(f5, f5, f5);
                        MovingObjectPosition hit2 = box2.calculateIntercept(vec, vec3);
                        if (hit2 != null) {
                            flag3 = true;
                            flag2 = true;
                            hit = hit2;
                        }
                    }
                }

                d += d7;
                d2 += d8;
                d3 += d9;
                BlockPos pos = new BlockPos(d, d2, d3);
                Block block = McpApi.mc.theWorld.getBlockState(pos).getBlock();
                if (block.getMaterial() == Material.water) {
                    d7 *= 0.6;
                    d5 = d8 * 0.6;
                    d9 *= 0.6;
                } else {
                    d7 *= f3;
                    d5 = d8 * f3;
                    d9 *= f3;
                }

                d8 = d5 - f4;
                GL11.glVertex3d(
                    d - McpApi.mc.getRenderManager().renderPosX,
                    d2 - McpApi.mc.getRenderManager().renderPosY,
                    d3 - McpApi.mc.getRenderManager().renderPosZ
                );
            }

            GL11.glEnd();
            GL11.glPushMatrix();
            GL11.glTranslated(
                d - McpApi.mc.getRenderManager().renderPosX,
                d2 - McpApi.mc.getRenderManager().renderPosY,
                d3 - McpApi.mc.getRenderManager().renderPosZ
            );
            if (hit != null) {
                switch (hit.sideHit.getAxis()) {
                    case X:
                        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                        break;
                    case Z:
                        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (flag3) {
                    handleInt(-65536);
                }
            }

            process6();
            GL11.glPopMatrix();
            handleFlag2(true);
        }
    }

    public static void handleDouble(double d, double d2, double d3, double d4) {
        d3 = Math.max(d3, 0.1);
        ScaledResolution sr = new ScaledResolution(McpApi.mc);
        double scaleFactor = sr.getScaleFactor();
        d2 = sr.getScaledHeight() - d2;
        d *= scaleFactor;
        d2 *= scaleFactor;
        d3 *= scaleFactor;
        d4 *= scaleFactor;
        GL11.glScissor((int)d, (int)(d2 - d4), (int)d3, (int)d4);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, double d) {
        GlStateManager.translate(f + f3 / 2.0F, f2 + f4 / 2.0F, 0.0F);
        GL11.glRotated(d, 0.0, 0.0, 1.0);
        GlStateManager.translate(-f - f3 / 2.0F, -f2 - f4 / 2.0F, 0.0F);
    }

    public static void handleEntity(Entity entity, int n, int n2, int n3, int n4) {
        Color color = new Color(255, 255, 255, n4);
        double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * McpApi.mc.timer.renderPartialTicks - McpApi.mc.getRenderManager().viewerPosX;
        double d2 = entity.lastTickPosY
            + (entity.posY - entity.lastTickPosY) * McpApi.mc.timer.renderPartialTicks
            - McpApi.mc.getRenderManager().viewerPosY
            + entity.getEyeHeight();
        double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * McpApi.mc.timer.renderPartialTicks - McpApi.mc.getRenderManager().viewerPosZ;
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Minecraft.getEntityRenderer().orientCamera(McpApi.mc.timer.renderPartialTicks);
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        handleInt(color.getRGB());
        GL11.glLineWidth(1.5F);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(0.0, McpApi.mc.thePlayer.getEyeHeight(), 0.0);
        GL11.glVertex3d(d, d2, d3);
        GL11.glEnd();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GL11.glPopMatrix();
    }

    public static void handleFloat2(float f, float f2, float f3, int n) {
        handleFloat3(f - f3, f2 - f3, f + f3, f2 + f3, f3 * 2.0F, n);
    }

    public static double computeDouble(float f) {
        return f > 0.0F ? f : -f;
    }

    public static boolean matchesEntity(Entity entity) {
        return matchesAxisAlignedBB(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    private static float computeFloat(String str, float f, float f2, int n, int n2) {
        drawStringWithShadowComponent.computeInt3(str, f, f2 - 0.35F, n);
        drawStringWithShadowComponent.computeInt3(str, f, f2 + 0.35F, n);
        drawStringWithShadowComponent.computeInt3(str, f - 0.35F, f2, n);
        drawStringWithShadowComponent.computeInt3(str, f + 0.35F, f2, n);
        drawStringWithShadowComponent.computeInt3(str, f, f2, n2);
        return f + drawStringWithShadowComponent.getStringWidth(str) - 2.0F;
    }

    public static void handleBlockPos(BlockPos pos, Color color, float f) {
        double d = pos.getX() - McpApi.mc.getRenderManager().viewerPosX;
        double d2 = pos.getY() - McpApi.mc.getRenderManager().viewerPosY;
        double d3 = pos.getZ() - McpApi.mc.getRenderManager().viewerPosZ;
        AxisAlignedBB box = new AxisAlignedBB(d, d2, d3, d + 1.0, d2 + 1.0, d3 + 1.0);
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GL11.glLineWidth(f);
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        RenderGlobal.func_181561_a(box);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popMatrix();
    }

    public static String stripColor(String str) {
        return pattern.matcher(str).replaceAll("");
    }

    public static void handleAxisAlignedBB4(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        tessellator.draw();
    }

    public static void handleFloat3(float f, float f2, float f3, float f4, float f5, int n) {
        float f6 = f + f3;
        float f7 = f2 + f4;
        float f8 = (n >> 24 & 0xFF) / 255.0F;
        float f9 = (n >> 16 & 0xFF) / 255.0F;
        float f10 = (n >> 8 & 0xFF) / 255.0F;
        float f11 = (n & 0xFF) / 255.0F;
        if (f > f6) {
            float f12 = f;
            f = f6;
            f6 = f12;
        }

        if (f2 > f7) {
            float f13 = f2;
            f2 = f7;
            f7 = f13;
        }

        double d = f + f5;
        double d2 = f2 + f5;
        double d3 = f6 - f5;
        double d4 = f7 - f5;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(1.0F);
        GlStateManager.color(f9, f10, f11, f8);
        GL11.glBegin(GL11.GL_POLYGON);
        double d5 = Math.PI / 180.0;

        for (double i = 0.0; i <= 90.0; i++) {
            GL11.glVertex2d(d3 + Math.sin(i * d5) * f5, d4 + Math.cos(i * d5) * f5);
        }

        for (double j = 90.0; j <= 180.0; j++) {
            GL11.glVertex2d(d3 + Math.sin(j * d5) * f5, d2 + Math.cos(j * d5) * f5);
        }

        for (double k = 180.0; k <= 270.0; k++) {
            GL11.glVertex2d(d + Math.sin(k * d5) * f5, d2 + Math.cos(k * d5) * f5);
        }

        for (double idx = 270.0; idx <= 360.0; idx++) {
            GL11.glVertex2d(d + Math.sin(idx * d5) * f5, d4 + Math.cos(idx * d5) * f5);
        }

        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.popMatrix();
    }

    public static void handleInt3(int n) {
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = ((0xFF | ~n) - ~n) / 255.0F;
        GL11.glColor4f(f2, f3, f4, f);
    }

    public static void handleDouble(double d, double d2, double d3, float f) {
        GL11.glPushMatrix();
        GL11.glLineWidth(3.5F);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int i = 0; i < 360; i++) {
            handleInt3(Color.HSBtoRGB(1.0F, 0.0F, f));
            GL11.glVertex2d(d, d2);
            handleInt3(Color.HSBtoRGB(i / 360.0F, 1.0F, f));
            GL11.glVertex2d(d + Math.sin(Math.toRadians(i)) * d3, d2 + Math.cos(Math.toRadians(i)) * d3);
        }

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
    }

    private static List<Entity> computeList(AxisAlignedBB box) {
        List list = new ArrayList();
        int n = MathHelper.floor_double((box.minX - 2.0) / 16.0);
        int n2 = MathHelper.floor_double((box.maxX + 2.0) / 16.0);
        int n3 = MathHelper.floor_double((box.minZ - 2.0) / 16.0);
        int n4 = MathHelper.floor_double((box.maxZ + 2.0) / 16.0);

        for (int i = n; i <= n2; i++) {
            for (int j = n3; j <= n4; j++) {
                McpApi.mc.theWorld.getChunkFromChunkCoords(i, j).getEntitiesWithinAABBForEntity(McpApi.mc.thePlayer, box, list, null);
            }
        }

        return list;
    }

    public static void handleFloat2(float f) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, (float)(f * 0.01));
    }

    public static void handleFloat(float f, float f2, float f3, float f4, int n, int n2) {
        float f5 = ((0xFF | ~(n >> 24)) - ~(n >> 24)) / 255.0F;
        float f6 = (n >> 16 & 0xFF) / 255.0F;
        float f7 = (n >> 8 & 0xFF) / 255.0F;
        float f8 = (n & 0xFF) / 255.0F;
        float f9 = (n2 >> 24 & 0xFF) / 255.0F;
        float f10 = ((0xFF | ~(n2 >> 16)) - ~(n2 >> 16)) / 255.0F;
        float f11 = ((0xFF | ~(n2 >> 8)) - ~(n2 >> 8)) / 255.0F;
        float f12 = (n2 & 0xFF) / 255.0F;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glVertex2f(f, f2);
        GL11.glVertex2f(f, f2 + f4);
        GL11.glColor4f(f10, f11, f12, f9);
        GL11.glVertex2f(f + f3, f2 + f4);
        GL11.glVertex2f(f + f3, f2);
        GL11.glEnd();
    }

    public static Framebuffer computeFramebuffer(Framebuffer framebuffer, boolean flag) {
        if (matchesFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }

            return new Framebuffer(McpApi.mc.displayWidth, McpApi.mc.displayHeight, flag);
        }
        return framebuffer;
    }

    public static void drawRect(int n, int n2, int n3, int n4, int n5) {
        Gui.drawRect(n, n2, n + n3, n2 + n4, n5);
    }

    public static void handleDouble(double d, double d2, double d3, double d4, float f, int n, int n2) {
        handleFloat((float)d, (float)d2, (float)d3, (float)d4, n2);
        float f2 = (n >> 24 & 0xFF) / 255.0F;
        float f3 = (n >> 16 & 0xFF) / 255.0F;
        float f4 = (n >> 8 & 0xFF) / 255.0F;
        float f5 = (n & 0xFF) / 255.0F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glPushMatrix();
        GL11.glColor4f(f3, f4, f5, f2);
        GL11.glLineWidth(f);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(d, d2);
        GL11.glVertex2d(d, d4);
        GL11.glVertex2d(d3, d4);
        GL11.glVertex2d(d3, d2);
        GL11.glVertex2d(d, d2);
        GL11.glVertex2d(d3, d2);
        GL11.glVertex2d(d, d4);
        GL11.glVertex2d(d3, d4);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
        GL11.glColor4f(255.0F, 1.0F, 1.0F, 255.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static void handleInt(int n, int n2, int n3) {
    }

    public static int func_177078_a(ItemStack stack) {
        int n = 1;
        if (stack.animationsToGo > 48) {
            n = 5;
        } else if (stack.animationsToGo > 32) {
            n = 4;
        } else if (stack.animationsToGo > 16) {
            n = 3;
        } else if (stack.animationsToGo > 1) {
            n = 2;
        }

        return n;
    }

    private static int computeInt(int n) {
        return n == 4 ? -5636096 : -1;
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void handleFloat(float f, float f2, float f3, float f4, AbstractClientPlayer player) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        McpApi.mc.getTextureManager().bindTexture(player.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect((int)f, (int)f2, 8.0F, 8.0F, 8, 8, (int)f3, (int)f4, 64.0F, 64.0F);
        GlStateManager.disableBlend();
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, int n, int n2, int n3) {
        float f6 = f * 2.0F;
        float f7 = f2 * 2.0F;
        float f8 = f3 * 2.0F;
        float f9 = f4 * 2.0F;
        GL11.glPushAttrib(1);
        GL11.glScaled(0.5, 0.5, 0.5);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBegin(GL11.GL_POLYGON);
        handleInt(n);

        for (int i = 0; i <= 90; i += 3) {
            double d = i * (float) (Math.PI / 180.0);
            GL11.glVertex2d(f6 + f5 + Math.sin(d) * f5 * -1.0, f7 + f5 + Math.cos(d) * f5 * -1.0);
        }

        for (int j = 90; j <= 180; j += 3) {
            double d2 = j * (float) (Math.PI / 180.0);
            GL11.glVertex2d(f6 + f5 + Math.sin(d2) * f5 * -1.0, f9 - f5 + Math.cos(d2) * f5 * -1.0);
        }

        for (int k = 0; k <= 90; k += 3) {
            double d3 = k * (float) (Math.PI / 180.0);
            GL11.glVertex2d(f8 - f5 + Math.sin(d3) * f5, f9 - f5 + Math.cos(d3) * f5);
        }

        for (int idx = 90; idx <= 180; idx += 3) {
            double d4 = idx * (float) (Math.PI / 180.0);
            GL11.glVertex2d(f8 - f5 + Math.sin(d4) * f5, f7 + f5 + Math.cos(d4) * f5);
        }

        GL11.glEnd();
        GL11.glPushMatrix();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(2.0F);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        if (n2 != 0L) {
            handleInt(n2);
        }

        for (int idx2 = 0; idx2 <= 90; idx2 += 3) {
            double d5 = idx2 * (float) (Math.PI / 180.0);
            GL11.glVertex2d(f6 + f5 + Math.sin(d5) * f5 * -1.0, f7 + f5 + Math.cos(d5) * f5 * -1.0);
        }

        for (int idx3 = 90; idx3 <= 180; idx3 += 3) {
            double d6 = idx3 * (float) (Math.PI / 180.0);
            GL11.glVertex2d(f6 + f5 + Math.sin(d6) * f5 * -1.0, f9 - f5 + Math.cos(d6) * f5 * -1.0);
        }

        if (n3 != 0) {
            handleInt(n3);
        }

        for (int idx4 = 0; idx4 <= 90; idx4 += 3) {
            double d7 = idx4 * (float) (Math.PI / 180.0);
            GL11.glVertex2d(f8 - f5 + Math.sin(d7) * f5, f9 - f5 + Math.cos(d7) * f5);
        }

        for (int idx5 = 90; idx5 <= 180; idx5 += 3) {
            double d8 = idx5 * (float) (Math.PI / 180.0);
            GL11.glVertex2d(f8 - f5 + Math.sin(d8) * f5, f7 + f5 + Math.cos(d8) * f5);
        }

        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glScaled(2.0, 2.0, 2.0);
        GlStateManager.popAttrib();
        GL11.glLineWidth(1.0F);
        GL11.glShadeModel(GL11.GL_FLAT);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void handleDouble(double d, double d2, double d3, int n) {
        GL11.glPushMatrix();
        handleInt3(n);
        GL11.glBegin(GL11.GL_POLYGON);

        for (int i = 0; i < 360; i++) {
            GL11.glVertex2d(d + Math.sin(Math.toRadians(i)) * d3, d2 + Math.cos(Math.toRadians(i)) * d3);
        }

        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void handleDouble3(double d, double d2, double d3, double d4, int n) {
        if (d < d3) {
            double d5 = d;
            d = d3;
            d3 = d5;
        }

        if (d2 < d4) {
            double d6 = d2;
            d2 = d4;
            d4 = d6;
        }

        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = ((0xFF | ~n) - ~n) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(f2, f3, f4, f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(d, d4, 0.0);
        GL11.glVertex3d(d3, d4, 0.0);
        GL11.glVertex3d(d3, d2, 0.0);
        GL11.glVertex3d(d, d2, 0.0);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, int n, int n2, int n3, int n4) {
        if (!(f3 <= f)) {
            float f6 = f3 - f;
            if (f6 < 5.0F) {
                f5 = Math.min(f5, f6 / 2.0F);
            }

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glPushAttrib(0);
            GL11.glScaled(0.5, 0.5, 0.5);
            float f7 = (float)(f * 2.0);
            float f8 = (float)(f2 * 2.0);
            float f9 = (float)(f3 * 2.0);
            float f10 = (float)(f4 * 2.0);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            handleInt(n);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);

            for (int i = 0; i <= 90; i += 3) {
                double d = i * (float) (Math.PI / 180.0);
                GL11.glVertex2d(f7 + f5 + Math.sin(d) * f5 * -1.0, f8 + f5 + Math.cos(d) * f5 * -1.0);
            }

            handleInt(n2);

            for (int j = 90; j <= 180; j += 3) {
                double d2 = j * (float) (Math.PI / 180.0);
                GL11.glVertex2d(f7 + f5 + Math.sin(d2) * f5 * -1.0, f10 - f5 + Math.cos(d2) * f5 * -1.0);
            }

            if (f9 - f7 >= 4.5) {
                handleInt(n3);

                for (int k = 0; k <= 90; k += 3) {
                    double d3 = k * (float) (Math.PI / 180.0);
                    GL11.glVertex2d(f9 - f5 + Math.sin(d3) * f5, f10 - f5 + Math.cos(d3) * f5);
                }

                handleInt(n4);

                for (int idx = 90; idx <= 180; idx += 3) {
                    double d4 = idx * (float) (Math.PI / 180.0);
                    GL11.glVertex2d(f9 - f5 + Math.sin(d4) * f5, f8 + f5 + Math.cos(d4) * f5);
                }
            }

            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glScaled(2.0, 2.0, 2.0);
            GlStateManager.popAttrib();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glShadeModel(GL11.GL_FLAT);
        }
    }

    public static void handleFlag2(boolean flag) {
        if (flag) {
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
