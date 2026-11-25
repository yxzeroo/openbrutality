package net.minecraft.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;
import net.optifine.CustomLoadingScreen;
import net.optifine.CustomLoadingScreens;
import net.optifine.reflect.Reflector;
import org.brutality.gui.OceanComponent;
import org.lwjgl.opengl.GL11;

public class LoadingScreenRenderer implements IProgressUpdate {
    private String message = "";
    private Minecraft mc;
    private String currentlyDisplayedText = "";
    private long systemTime = Minecraft.getSystemTime();
    private boolean field_73724_e;
    private ScaledResolution scaledResolution;
    private Framebuffer framebuffer;

    public LoadingScreenRenderer(Minecraft mcIn) {
        this.mc = mcIn;
        this.scaledResolution = new ScaledResolution(mcIn);
        this.framebuffer = new Framebuffer(mcIn.displayWidth, mcIn.displayHeight, false);
        this.framebuffer.setFramebufferFilter(9728);
    }

    @Override
    public void resetProgressAndMessage(String message) {
        this.field_73724_e = false;
        this.displayString(message);
    }

    @Override
    public void displaySavingString(String message) {
        this.field_73724_e = true;
        this.displayString(message);
    }

    private void displayString(String message) {
        this.currentlyDisplayedText = message;
        if (!this.mc.running) {
            if (!this.field_73724_e) {
                throw new MinecraftError();
            }
        } else {
            GlStateManager.clear(256);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            if (OpenGlHelper.isFramebufferEnabled()) {
                int scaleFactor = this.scaledResolution.getScaleFactor();
                GlStateManager.ortho(0.0, this.scaledResolution.getScaledWidth() * scaleFactor, this.scaledResolution.getScaledHeight() * scaleFactor, 0.0, 100.0, 300.0);
            } else {
                ScaledResolution sr = new ScaledResolution(this.mc);
                GlStateManager.ortho(0.0, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0, 100.0, 300.0);
            }

            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -200.0F);
        }
    }

    @Override
    public void displayLoadingString(String message) {
        if (!this.mc.running) {
            if (!this.field_73724_e) {
                throw new MinecraftError();
            }
        } else {
            this.systemTime = 0L;
            this.message = message;
            this.setLoadingProgress(-1);
            this.systemTime = 0L;
        }
    }

    @Override
    public void setLoadingProgress(int progress) {
        if (!this.mc.running) {
            if (!this.field_73724_e) {
                throw new MinecraftError();
            }
        } else {
            long systemTime = Minecraft.getSystemTime();
            if (systemTime - this.systemTime >= 100L) {
                this.systemTime = systemTime;
                ScaledResolution sr = new ScaledResolution(this.mc);
                int scaleFactor = sr.getScaleFactor();
                int scaledWidth = sr.getScaledWidth();
                int scaledHeight = sr.getScaledHeight();
                if (OpenGlHelper.isFramebufferEnabled()) {
                    this.framebuffer.framebufferClear();
                } else {
                    GlStateManager.clear(256);
                }

                this.framebuffer.bindFramebuffer(false);
                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0.0, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0, 100.0, 300.0);
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.loadIdentity();
                GlStateManager.translate(0.0F, 0.0F, -200.0F);
                if (!OpenGlHelper.isFramebufferEnabled()) {
                    GlStateManager.clear(16640);
                }

                boolean flag = true;
                if (Reflector.FMLClientHandler_handleLoadingScreen.exists()) {
                    Object obj = Reflector.call(Reflector.FMLClientHandler_instance);
                    if (obj != null) {
                        flag = !Reflector.callBoolean(obj, Reflector.FMLClientHandler_handleLoadingScreen, sr);
                    }
                }

                if (flag) {
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
                    CustomLoadingScreen customLoadingScreen = CustomLoadingScreens.getCustomLoadingScreen();
                    if (customLoadingScreen != null) {
                        customLoadingScreen.drawBackground(sr.getScaledWidth(), sr.getScaledHeight());
                    } else if (!OceanComponent.matchesInt(sr.getScaledWidth(), sr.getScaledHeight())) {
                        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
                        float f = 32.0F;
                        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
                        worldRenderer.func_181662_b(0.0, scaledHeight, 0.0).func_181673_a(0.0, scaledHeight / f).func_181669_b(64, 64, 64, 255).func_181675_d();
                        worldRenderer.func_181662_b(scaledWidth, scaledHeight, 0.0).func_181673_a(scaledWidth / f, scaledHeight / f).func_181669_b(64, 64, 64, 255).func_181675_d();
                        worldRenderer.func_181662_b(scaledWidth, 0.0, 0.0).func_181673_a(scaledWidth / f, 0.0).func_181669_b(64, 64, 64, 255).func_181675_d();
                        worldRenderer.func_181662_b(0.0, 0.0, 0.0).func_181673_a(0.0, 0.0).func_181669_b(64, 64, 64, 255).func_181675_d();
                        tessellator.draw();
                    }

                    if (progress >= 0) {
                        byte b = 100;
                        byte b2 = 2;
                        int n = scaledWidth / 2 - b / 2;
                        int n2 = scaledHeight / 2 + 16;
                        GlStateManager.disableTexture2D();
                        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
                        worldRenderer.func_181662_b(n, n2, 0.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                        worldRenderer.func_181662_b(n, n2 + b2, 0.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                        worldRenderer.func_181662_b(n + b, n2 + b2, 0.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                        worldRenderer.func_181662_b(n + b, n2, 0.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                        worldRenderer.func_181662_b(n, n2, 0.0).func_181669_b(128, 255, 128, 255).func_181675_d();
                        worldRenderer.func_181662_b(n, n2 + b2, 0.0).func_181669_b(128, 255, 128, 255).func_181675_d();
                        worldRenderer.func_181662_b(n + progress, n2 + b2, 0.0).func_181669_b(128, 255, 128, 255).func_181675_d();
                        worldRenderer.func_181662_b(n + progress, n2, 0.0).func_181669_b(128, 255, 128, 255).func_181675_d();
                        tessellator.draw();
                        GlStateManager.enableTexture2D();
                    }

                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    this.mc
                        .fontRendererObj
                        .drawStringWithShadow(
                            this.currentlyDisplayedText,
                            (scaledWidth - this.mc.fontRendererObj.getStringWidth(this.currentlyDisplayedText)) / 2,
                            scaledHeight / 2 - 4 - 16,
                            16777215
                        );
                    this.mc
                        .fontRendererObj
                        .drawStringWithShadow(this.message, (scaledWidth - this.mc.fontRendererObj.getStringWidth(this.message)) / 2, scaledHeight / 2 - 4 + 8, 16777215);
                }

                this.framebuffer.unbindFramebuffer();
                if (OpenGlHelper.isFramebufferEnabled()) {
                    this.framebuffer.framebufferRender(scaledWidth * scaleFactor, scaledHeight * scaleFactor);
                }

                this.mc.updateDisplay();

                try {
                    Thread.yield();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void setDoneWorking() {
    }
}
