package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.Brutality;
import org.brutality.alt.altmanager.gui.GuiAltManager;
import org.brutality.gui.ChangeSkinScreen;
import org.brutality.gui.NameChangerScreen;
import org.brutality.module.Module;
import org.brutality.module.impl.exploit.Crasher;
import org.brutality.proxy.gui.GuiProxyManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
    private static final AtomicInteger field_175373_f = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();
    private static final Random RANDOM = new Random();
    private float updateCounter;
    private String splashText;
    private GuiButton buttonResetDemo;
    private int panoramaTimer;
    private DynamicTexture viewportTexture;
    private boolean field_175375_v = true;
    private final Object threadLock = new Object();
    private String openGLWarning1;
    private String openGLWarning2;
    private String openGLWarningLink;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{
        new ResourceLocation("textures/gui/title/background/panorama_0.png"),
        new ResourceLocation("textures/gui/title/background/panorama_1.png"),
        new ResourceLocation("textures/gui/title/background/panorama_2.png"),
        new ResourceLocation("textures/gui/title/background/panorama_3.png"),
        new ResourceLocation("textures/gui/title/background/panorama_4.png"),
        new ResourceLocation("textures/gui/title/background/panorama_5.png")
    };
    public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private ResourceLocation backgroundTexture;
    private GuiButton realmsButton;
    private boolean field_183502_L;
    private GuiScreen field_183503_M;
    private GuiButton modButton;
    private GuiScreen modUpdateNotification;

    public GuiMainMenu() {
        this.openGLWarning2 = field_96138_a;
        this.field_183502_L = false;
        this.splashText = "missingno";
        BufferedReader reader = null;

        try {
            ArrayList list = Lists.newArrayList();
            reader = new BufferedReader(
                new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8)
            );

            String str;
            while ((str = reader.readLine()) != null) {
                String str2 = str.trim();
                if (!str2.isEmpty()) {
                    list.add(str2);
                }
            }

            if (!list.isEmpty()) {
                do {
                    this.splashText = (String)list.get(RANDOM.nextInt(list.size()));
                } while (this.splashText.hashCode() == 125780783);
            }
        } catch (IOException ignored) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored2) {
                }
            }
        }

        this.updateCounter = RANDOM.nextFloat();
        this.openGLWarning1 = "";
        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.openGLWarning1 = I18n.format("title.oldgl1");
            this.openGLWarning2 = I18n.format("title.oldgl2");
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    private boolean func_183501_a() {
        return Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && this.field_183503_M != null;
    }

    @Override
    public void updateScreen() {
        this.panoramaTimer++;
        if (this.func_183501_a()) {
            this.field_183503_M.updateScreen();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void initGui() {
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        if (instance.get(2) + 1 == 12 && instance.get(5) == 24) {
            this.splashText = "Merry X-mas!";
        } else if (instance.get(2) + 1 == 1 && instance.get(5) == 1) {
            this.splashText = "Happy new year!";
        } else if (instance.get(2) + 1 == 10 && instance.get(5) == 31) {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }

        int n = this.height / 4 + 48;
        if (mc.isDemo()) {
            this.addDemoButtons(n, 24);
        } else {
            this.addSingleplayerMultiplayerButtons(n, 24);
        }

        this.buttonList.add(new GuiButton(999, this.width / 2 - 100, n + 72, "Alt Manager"));
        this.buttonList.add(new GuiButton(1000, this.width / 2 - 100, n + 72 + 24, "Proxy Manager"));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, n + 72 + 48, 98, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, n + 72 + 48, 98, 20, I18n.format("menu.quit")));
        this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, n + 72 + 48));
        synchronized (this.threadLock) {
            this.field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
            this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
            int n2 = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.width - n2) / 2;
            this.field_92021_u = this.buttonList.get(0).yPosition - 24;
            this.field_92020_v = this.field_92022_t + n2;
            this.field_92019_w = this.field_92021_u + 24;
        }

        mc.func_181537_a(false);
        if (Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && !this.field_183502_L) {
            RealmsBridge realmsBridge = new RealmsBridge();
            this.field_183503_M = realmsBridge.getNotificationScreen(this);
            this.field_183502_L = true;
        }

        if (this.func_183501_a()) {
            this.field_183503_M.func_183500_a(this.width, this.height);
            this.field_183503_M.initGui();
        }
    }

    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer")));
        String str = Crasher.getInstance().checkState44() ? "Disable Crasher" : "Enable Crasher";
        this.buttonList.add(new GuiButton(343, 5, 30, 120, 20, str));
        this.buttonList.add(new GuiButton(800, this.width - 105, 5, 100, 20, "Name Changer"));
        this.buttonList.add(new GuiButton(801, this.width - 105, 30, 100, 20, "Skin Changer"));
        if (Reflector.GuiModList_Constructor.exists()) {
            this.buttonList
                .add(
                    this.realmsButton = new GuiButton(
                        14, this.width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("menu.online").replace("Minecraft", "").trim()
                    )
                );
            this.buttonList.add(this.modButton = new GuiButton(6, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("fml.menu.mods")));
        } else {
            this.buttonList.add(this.realmsButton = new GuiButton(14, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, I18n.format("menu.online")));
        }
    }

    private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
        this.buttonList.add(new GuiButton(11, this.width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo")));
        this.buttonList.add(this.buttonResetDemo = new GuiButton(12, this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo")));
        ISaveFormat saveLoader = mc.getSaveLoader();
        WorldInfo worldInfo = saveLoader.getWorldInfo("Demo_World");
        if (worldInfo == null) {
            this.buttonResetDemo.enabled = false;
        }
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }

        if (button.id == 5) {
            mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager()));
        }

        if (button.id == 1) {
            mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if (button.id == 2) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if (button.id == 14 && this.realmsButton.visible) {
            this.switchToRealms();
        }

        if (button.id == 4) {
            mc.shutdown();
        }

        if (button.id == 6 && Reflector.GuiModList_Constructor.exists()) {
            mc.displayGuiScreen((GuiScreen)Reflector.newInstance(Reflector.GuiModList_Constructor, this));
        }

        if (button.id == 11) {
            mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }

        if (button.id == 999) {
            mc.displayGuiScreen(new GuiAltManager(this));
        }

        if (button.id == 1000) {
            mc.displayGuiScreen(new GuiProxyManager(this));
        }

        if (button.id == 12) {
            ISaveFormat saveLoader = mc.getSaveLoader();
            WorldInfo worldInfo = saveLoader.getWorldInfo("Demo_World");
            if (worldInfo != null) {
                GuiYesNo gui = GuiSelectWorld.func_152129_a(this, worldInfo.getWorldName(), 12);
                mc.displayGuiScreen(gui);
            }
        }

        if (button.id == 343) {
            Module module = Brutality.moduleManager.computeModule("Crasher");
            if (module != null) {
                module.process28();
                button.displayString = module.checkState44() ? "Disable Crasher" : "Enable Crasher";
            }
        }

        if (button.id == 800) {
            mc.displayGuiScreen(new NameChangerScreen(this));
        }

        if (button.id == 801) {
            mc.displayGuiScreen(new ChangeSkinScreen(this));
        }
    }

    private void switchToRealms() {
        RealmsBridge realmsBridge = new RealmsBridge();
        realmsBridge.switchToRealms(this);
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (result && id == 12) {
            ISaveFormat saveLoader = mc.getSaveLoader();
            saveLoader.flushCache();
            saveLoader.deleteWorldDirectory("Demo_World");
            mc.displayGuiScreen(this);
        } else if (id == 13) {
            if (result) {
                try {
                    Class clazz = Class.forName("java.awt.Desktop");
                    Object obj = clazz.getMethod("getDesktop").invoke(null);
                    clazz.getMethod("browse", URI.class).invoke(obj, new URI(this.openGLWarningLink));
                } catch (Throwable e) {
                    logger.error("Couldn't open link", e);
                }
            }

            mc.displayGuiScreen(this);
        }
    }

    private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        int n = 8;
        int n2 = 64;
        CustomPanoramaProperties customPanoramaProperties = CustomPanorama.getCustomPanoramaProperties();
        if (customPanoramaProperties != null) {
            n2 = customPanoramaProperties.getBlur1();
        }

        for (int i = 0; i < n2; i++) {
            GlStateManager.pushMatrix();
            float f = ((float)(i % n) / n - 0.5F) / 64.0F;
            float f2 = ((float)(i / n) / n - 0.5F) / 64.0F;
            float f3 = 0.0F;
            GlStateManager.translate(f, f2, f3);
            GlStateManager.rotate(MathHelper.sin((this.panoramaTimer + p_73970_3_) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-(this.panoramaTimer + p_73970_3_) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int j = 0; j < 6; j++) {
                GlStateManager.pushMatrix();
                if (j == 1) {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 3) {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 4) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (j == 5) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                ResourceLocation[] locations = titlePanoramaPaths;
                if (customPanoramaProperties != null) {
                    locations = customPanoramaProperties.getPanoramaLocations();
                }

                mc.getTextureManager().bindTexture(locations[j]);
                worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
                int n3 = 255 / (i + 1);
                worldRenderer.func_181662_b(-1.0, -1.0, 1.0).func_181673_a(0.0, 0.0).func_181669_b(255, 255, 255, n3).func_181675_d();
                worldRenderer.func_181662_b(1.0, -1.0, 1.0).func_181673_a(1.0, 0.0).func_181669_b(255, 255, 255, n3).func_181675_d();
                worldRenderer.func_181662_b(1.0, 1.0, 1.0).func_181673_a(1.0, 1.0).func_181669_b(255, 255, 255, n3).func_181675_d();
                worldRenderer.func_181662_b(-1.0, 1.0, 1.0).func_181673_a(0.0, 1.0).func_181669_b(255, 255, 255, n3).func_181675_d();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        worldRenderer.setTranslation(0.0, 0.0, 0.0);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    private void rotateAndBlurSkybox(float p_73968_1_) {
        mc.getTextureManager().bindTexture(this.backgroundTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
        GlStateManager.disableAlpha();
        byte b = 3;
        int n = 3;
        CustomPanoramaProperties customPanoramaProperties = CustomPanorama.getCustomPanoramaProperties();
        if (customPanoramaProperties != null) {
            n = customPanoramaProperties.getBlur2();
        }

        for (int i = 0; i < n; i++) {
            float f = 1.0F / (i + 1);
            int width = this.width;
            int height = this.height;
            float f2 = (i - b / 2) / 256.0F;
            worldRenderer.func_181662_b(width, height, this.zLevel).func_181673_a(0.0F + f2, 1.0).func_181666_a(1.0F, 1.0F, 1.0F, f).func_181675_d();
            worldRenderer.func_181662_b(width, 0.0, this.zLevel).func_181673_a(1.0F + f2, 1.0).func_181666_a(1.0F, 1.0F, 1.0F, f).func_181675_d();
            worldRenderer.func_181662_b(0.0, 0.0, this.zLevel).func_181673_a(1.0F + f2, 0.0).func_181666_a(1.0F, 1.0F, 1.0F, f).func_181675_d();
            worldRenderer.func_181662_b(0.0, height, this.zLevel).func_181673_a(0.0F + f2, 0.0).func_181666_a(1.0F, 1.0F, 1.0F, f).func_181675_d();
        }

        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_) {
        mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        int n = 3;
        CustomPanoramaProperties customPanoramaProperties = CustomPanorama.getCustomPanoramaProperties();
        if (customPanoramaProperties != null) {
            n = customPanoramaProperties.getBlur3();
        }

        for (int i = 0; i < n; i++) {
            this.rotateAndBlurSkybox(p_73971_3_);
            this.rotateAndBlurSkybox(p_73971_3_);
        }

        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
        float f = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
        float f2 = this.height * f / 256.0F;
        float f3 = this.width * f / 256.0F;
        int width = this.width;
        int height = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
        worldRenderer.func_181662_b(0.0, height, this.zLevel).func_181673_a(0.5F - f2, 0.5F + f3).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).func_181675_d();
        worldRenderer.func_181662_b(width, height, this.zLevel).func_181673_a(0.5F - f2, 0.5F - f3).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).func_181675_d();
        worldRenderer.func_181662_b(width, 0.0, this.zLevel).func_181673_a(0.5F + f2, 0.5F - f3).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).func_181675_d();
        worldRenderer.func_181662_b(0.0, 0.0, this.zLevel).func_181673_a(0.5F + f2, 0.5F + f3).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).func_181675_d();
        tessellator.draw();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableAlpha();
        this.renderSkybox(mouseX, mouseY, partialTicks);
        GlStateManager.enableAlpha();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        int n = 274;
        int n2 = this.width / 2 - n / 2;
        byte b = 30;
        int n3 = -2130706433;
        int n4 = 16777215;
        int n5 = 0;
        int MIN_VALUE = Integer.MIN_VALUE;
        CustomPanoramaProperties customPanoramaProperties = CustomPanorama.getCustomPanoramaProperties();
        if (customPanoramaProperties != null) {
            n3 = customPanoramaProperties.getOverlay1Top();
            n4 = customPanoramaProperties.getOverlay1Bottom();
            n5 = customPanoramaProperties.getOverlay2Top();
            MIN_VALUE = customPanoramaProperties.getOverlay2Bottom();
        }

        if (n3 != 0 || n4 != 0) {
            this.drawGradientRect(0, 0, this.width, this.height, n3, n4);
        }

        if (n5 != 0 || MIN_VALUE != 0) {
            this.drawGradientRect(0, 0, this.width, this.height, n5, MIN_VALUE);
        }

        mc.getTextureManager().bindTexture(minecraftTitleTextures);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.updateCounter < 1.0E-4) {
            this.drawTexturedModalRect(n2 + 0, b + 0, 0, 0, 99, 44);
            this.drawTexturedModalRect(n2 + 99, b + 0, 129, 0, 27, 44);
            this.drawTexturedModalRect(n2 + 99 + 26, b + 0, 126, 0, 3, 44);
            this.drawTexturedModalRect(n2 + 99 + 26 + 3, b + 0, 99, 0, 26, 44);
            this.drawTexturedModalRect(n2 + 155, b + 0, 0, 45, 155, 44);
        } else {
            this.drawTexturedModalRect(n2 + 0, b + 0, 0, 0, 155, 44);
            this.drawTexturedModalRect(n2 + 155, b + 0, 0, 45, 155, 44);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.width / 2 + 90), 70.0F, 0.0F);
        GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
        float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
        f = f * 100.0F / (this.fontRendererObj.getStringWidth(this.splashText) + 32);
        GlStateManager.scale(f, f, f);
        this.drawCenteredString(this.fontRendererObj, this.splashText, 0, -8, -256);
        GlStateManager.popMatrix();
        String str = "Minecraft 1.8.9";
        if (mc.isDemo()) {
            str = str + " Demo";
        }

        if (Reflector.FMLCommonHandler_getBrandings.exists()) {
            Object obj = Reflector.call(Reflector.FMLCommonHandler_instance);
            List list = Lists.reverse((List)Reflector.call(obj, Reflector.FMLCommonHandler_getBrandings, true));

            for (int i = 0; i < list.size(); i++) {
                String str2 = (String)list.get(i);
                if (!Strings.isNullOrEmpty(str2)) {
                    this.drawString(this.fontRendererObj, str2, 2, this.height - (10 + i * (this.fontRendererObj.FONT_HEIGHT + 1)), 16777215);
                }
            }

            if (Reflector.ForgeHooksClient_renderMainMenu.exists()) {
                Reflector.call(Reflector.ForgeHooksClient_renderMainMenu, this, this.fontRendererObj, this.width, this.height);
            }
        } else {
            this.drawString(this.fontRendererObj, str, 2, this.height - 10, -1);
        }

        String str3 = "Copyright Mojang AB. Do not distribute!";
        this.drawString(this.fontRendererObj, str3, this.width - this.fontRendererObj.getStringWidth(str3) - 2, this.height - 10, -1);
        if (this.openGLWarning1 != null && this.openGLWarning1.length() > 0) {
            drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
            this.drawString(this.fontRendererObj, this.openGLWarning1, this.field_92022_t, this.field_92021_u, -1);
            this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.field_92024_r) / 2, this.buttonList.get(0).yPosition - 12, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.func_183501_a()) {
            this.field_183503_M.drawScreen(mouseX, mouseY, partialTicks);
        }

        if (this.modUpdateNotification != null) {
            this.modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        synchronized (this.threadLock) {
            if (this.openGLWarning1.length() > 0
                && mouseX >= this.field_92022_t
                && mouseX <= this.field_92020_v
                && mouseY >= this.field_92021_u
                && mouseY <= this.field_92019_w) {
                GuiConfirmOpenLink gui = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                gui.disableSecurityWarning();
                mc.displayGuiScreen(gui);
            }
        }

        if (this.func_183501_a()) {
            this.field_183503_M.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onGuiClosed() {
        if (this.field_183503_M != null) {
            this.field_183503_M.onGuiClosed();
        }
    }
}
