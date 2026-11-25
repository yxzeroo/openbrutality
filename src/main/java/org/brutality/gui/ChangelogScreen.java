package org.brutality.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.brutality.Brutality;
import org.brutality.alt.altmanager.gui.GuiAccountSelector;
import org.brutality.gui.ChangeLog.Entry;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.BaseComponent.ThemesMode;
import org.brutality.gui.mainmenu.effects.EffectsComponent;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.brutality.gui.mainmenu.effects.NebulaComponent;
import org.brutality.gui.mainmenu.selector.OpenScreen;
import org.brutality.gui.mainmenu.theme.DisposeComponent;
import org.brutality.proxy.gui.GuiProxyManager;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ChangelogScreen extends CustomGuiScreen {
    private OpenScreen openScreen;
    private static final String text = "Brutality";
    private static final long timestamp = 120L;
    private static final long timestamp2 = 600L;
    private static final long timestamp3 = 180L;
    private static final int count = 6;
    private int count2 = 0;
    private int count3 = 0;
    private int count4 = 0;
    private long timestamp4 = 0L;
    private long timestamp5 = 0L;
    private static final String[] stringArray = new String[]{
        "\"uwu marki garlic\" - pittacts 2023",
        "\"your skin color is not difficult\" - masiul 2024",
        "\"im literally legit tank\" - huys 2024",
        "\"go crazy on da opps\" - sick 2024",
        "\"the best hypixel pit client in the solar system\" - sick 2025",
        "\"agays pants\" - masiul 2024",
        "\"tominator\" - masiul 2024",
        "\"SHUT UP WITH YOUR EXE MOC, SHUT THE FUCK UP YOU FUCKING FAKE\" - pittacts 2024",
        "\"Finally the blood key\" - sick 2024",
        "\"I would be useless without venoms\" - sick 2024",
        "\"Fold behaviour\" - smoothing 2024",
        "\"God is GOOOD\" - sick 2024",
        "\"I like boys\" - sick 2024",
        "\"MEOWWWWWW\" - sick 2024",
        "\"YOO WHY YOU SO FUCKING TANKY\" - siii 2024",
        "\"N*GGER N*GGER N*GGER\" - limitless 2024",
        "\"Hol up\" - sick 2024"
    };
    private int count5 = 0;
    private long timestamp6 = 0L;
    private static final long timestamp7 = 5000L;
    private int count6;
    private int count7;
    private int count8;
    private float[] floatValueArray = new float[8];
    private boolean[] flagArray = new boolean[8];
    private final String[] stringArray2 = new String[]{"SINGLEPLAYER", "MULTIPLAYER", "ALT MANAGER", "PROXY MANAGER", "NAME CHANGER", "SKIN CHANGER"};
    private static final String[] stringArray3 = new String[]{"Singleplayer", "Multiplayer", "Alt Manager", "Proxy Manager", "Name Changer", "Skin Changer"};
    private CustomFontRenderer drawStringWithShadowComponent;
    private CustomFontRenderer drawStringWithShadowComponent2;
    private CustomFontRenderer drawStringWithShadowComponent3;
    private CustomFontRenderer drawStringWithShadowComponent4;
    private CustomFontRenderer drawStringWithShadowComponent5;
    private CustomFontRenderer drawStringWithShadowComponent6;
    private String text2 = "2.0";
    private int count9 = 0;
    private int count10 = 0;
    private boolean flag = false;
    private int count11 = 0;
    private long timestamp8 = 0L;
    private long timestamp9 = 0L;
    private static final int count12 = 150;
    private static final int count13 = 200;
    private static final String text3 = "brutality.lol";
    private long timestamp10 = System.nanoTime();
    private float factor;
    private float factor2 = 0.0F;
    private float factor3 = 0.0F;
    private boolean flag2 = false;
    private int scaledWidth;
    private int scaledHeight;
    private boolean flag3 = true;
    private static boolean flag4;
    private long timestamp11;
    private boolean flag5;
    private static final long timestamp12 = 900000000L;
    private long timestamp13 = System.nanoTime();
    private int count14 = -1;
    private int count15 = -1;
    private static final int[][] intValueArray = new int[][]{
        {255, 100, 50},
        {160, 180, 255},
        {50, 200, 230},
        {220, 180, 100},
        {80, 230, 150},
        {255, 50, 200},
        {120, 200, 255},
        {225, 110, 60},
        {245, 140, 70},
        {255, 200, 70},
        {160, 60, 255},
        {170, 110, 255},
        {130, 210, 240},
        {255, 175, 100},
        {100, 170, 230},
        {160, 140, 255},
        {255, 160, 200},
        {190, 145, 235},
        {245, 165, 85}
    };
    private boolean flag6 = false;
    private static final String text4 = "Opacity";
    private static int count16 = -1;
    private boolean flag7;
    private float factor4;
    private static final int count17 = 20;
    private float factor5;
    private float factor6;
    private static final int count18 = 3;
    private static final int count19 = 5;

    private Rectangle computeRectangle2() {
        String str = this.computeString();
        if (str.isEmpty()) {
        } else {
            int n = this.drawStringWithShadowComponent6.getStringWidth(str) + 6;
        }

        return new Rectangle(
            6, 6, MainMenuIconRenderer.computeInt("Changelog", str, this.drawStringWithShadowComponent5, this.drawStringWithShadowComponent6, true), 18
        );
    }

    private void process2() {
        if (!this.flag6) {
            this.flag6 = true;
            boolean flag = GL11.glGetBoolean(GL11.GL_FOG);
            boolean flag2 = GL11.glGetBoolean(GL11.GL_LIGHTING);
            boolean flag3 = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
            boolean flag4 = GL11.glGetBoolean(GL11.GL_CULL_FACE);
            boolean flag5 = GL11.glGetBoolean(GL11.GL_ALPHA_TEST);
            boolean flag6 = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
            int n = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
            int n2 = GL11.glGetInteger(34016);
        }
    }

    private Rectangle computeRectangle5() {
        return new Rectangle(this.scaledWidth - 8 - 40 - 5, this.scaledHeight - 8 - 20, 20, 20);
    }

    private void handleInt(int n, int n2) {
        if (this.drawStringWithShadowComponent2 != null) {
            long now = System.currentTimeMillis();
            Color color = new Color(11141120);
            Color color2 = new Color(16733525);
            Color color3 = new Color(16755200);
            if (this.flag) {
                if (now - this.timestamp9 >= 200L) {
                    this.count11++;
                    if (this.count11 >= 4) {
                        this.flag = false;
                        this.count11 = 0;
                        this.count10 = 0;
                        this.count9 = 0;
                        this.timestamp8 = now;
                    } else {
                        this.timestamp9 = now;
                    }
                }

                Color color4 = this.count11 % 2 == 0 ? color2 : color;
                this.handleInt(n, n2, color4, color4, color4, "brutality.lol".length());
            } else {
                if (now - this.timestamp8 >= 150L) {
                    this.count9++;
                    if (this.count9 > "brutality.lol".length()) {
                        this.count9 = 0;
                        this.count10++;
                        if (this.count10 >= 2) {
                            this.flag = true;
                            this.timestamp9 = now;
                            this.count11 = 0;
                            this.count10 = 0;
                            this.handleInt(n, n2, color2, color2, color3, "brutality.lol".length());
                            return;
                        }
                    }

                    this.timestamp8 = now;
                }

                Color color5 = this.count10 == 0 ? color : color2;
                Color color6 = this.count10 == 0 ? color2 : color;
                this.handleInt(n, n2, color5, color6, color3, this.count9);
            }
        }
    }

    private int computeInt() {
        return Math.max(78, Math.min(108, (this.scaledWidth - 40 - 10) / 3));
    }

    private void handleInt4(int n, int n2) {
        if (this.flag7) {
            if (Mouse.isButtonDown(0)) {
                count16 = this.computeInt2(n);
            } else {
                this.flag7 = false;
                process3();
            }
        }

        Rectangle rect = this.computeRectangle4();
        boolean flag = rect.contains(n, n2);
        this.factor4 = this.factor4 + ((flag ? 1.0F : 0.0F) - this.factor4) * this.factor;
        MainMenuIconRenderer.handleRectangle(rect, "Opacity", computeInt2(), this.drawStringWithShadowComponent6, this.factor4, this.flag7, this.computeIntArray());
    }

    private static int computeInt(int n) {
        return Math.max(0, Math.min(255, n));
    }

    private Rectangle computeRectangle(int n) {
        int n2 = this.computeInt();
        int n3 = n / 3;
        int n4 = n % 3;
        int n5 = Math.min(3, this.stringArray2.length - n3 * 3);
        int n6 = n5 * n2 + (n5 - 1) * 5;
        int n7 = (this.scaledWidth - n6) / 2 + n4 * (n2 + 5);
        int n8 = this.computeInt3() + n3 * (this.count7 + 5);
        return new Rectangle(n7, n8, n2, this.count7);
    }

    private Rectangle computeRectangle() {
        int n = MainMenuIconRenderer.computeInt("Themes", "", this.drawStringWithShadowComponent5, this.drawStringWithShadowComponent6, true);
        return new Rectangle(this.scaledWidth - n - 6, 6, n, 18);
    }

    private int computeInt3() {
        int n = (this.stringArray2.length + 3 - 1) / 3;
        int n2 = n * this.count7 + (n - 1) * 5;
        int n3 = Math.round(this.scaledHeight / 2.35F);
        return Math.max(64, Math.min(n3, this.scaledHeight - n2 - 60));
    }

    private int computeInt2(int n) {
        int[] ints = MainMenuIconRenderer.computeIntArray(this.computeRectangle4(), "Opacity", this.drawStringWithShadowComponent6);
        float f = (float)(n - ints[0]) / ints[1];
        return Math.round(Math.max(0.0F, Math.min(1.0F, f)) * 100.0F);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (this.flag7) {
            count16 = this.computeInt2(mouseX);
            this.flag7 = false;
            process3();
        }
    }

    private void handleInt(int n, int n2, int n3, int n4, int n5) {
        if (n > n3) {
            int n6 = n;
            n = n3;
            n3 = n6;
        }

        if (n2 > n4) {
            int n7 = n2;
            n2 = n4;
            n4 = n7;
        }

        float f = (n5 >> 24 & 0xFF) / 255.0F;
        float f2 = ((0xFF | ~(n5 >> 16)) - ~(n5 >> 16)) / 255.0F;
        float f3 = (n5 >> 8 & 0xFF) / 255.0F;
        float f4 = ((0xFF | ~n5) - ~n5) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n4);
        GL11.glVertex2f(n3, n4);
        GL11.glVertex2f(n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mouseButton == 0) {
            if (this.timestamp11 != 0L) {
                this.timestamp11 = 0L;
            } else if (this.openScreen.isVisible()) {
                try {
                    this.openScreen.mouseClicked(mouseX, mouseY, mouseButton);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } else if (this.computeRectangle4().contains(mouseX, mouseY)) {
                count16 = this.computeInt2(mouseX);
                this.flag7 = true;
            } else if (this.computeRectangle().contains(mouseX, mouseY)) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                this.openScreen.r();
            } else if (this.computeRectangle5().contains(mouseX, mouseY)) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
            } else if (this.computeRectangle3().contains(mouseX, mouseY)) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                mc.shutdown();
            } else if (this.computeRectangle2().contains(mouseX, mouseY)) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                mc.displayGuiScreen(new RELEASESScreen(this));
            } else {
                for (int i = 0; i < this.stringArray2.length; i++) {
                    Rectangle rect = this.computeRectangle(i);
                    int n = rect.x;
                    int n2 = rect.y;
                    if (rect.contains(mouseX, mouseY)) {
                        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                        String str = this.stringArray2[i];
                        switch (str) {
                            case "SINGLEPLAYER":
                                mc.displayGuiScreen(new GuiSelectWorld(this));
                                return;
                            case "MULTIPLAYER":
                                mc.displayGuiScreen(new GuiMultiplayer(this));
                                return;
                            case "ALT MANAGER":
                                mc.displayGuiScreen(new GuiAccountSelector(this));
                                return;
                            case "PROXY MANAGER":
                                mc.displayGuiScreen(new GuiProxyManager(this));
                                return;
                            case "NAME CHANGER":
                                mc.displayGuiScreen(new NameChangerScreen(this));
                                return;
                            case "SKIN CHANGER":
                                mc.displayGuiScreen(new ChangeSkinScreen(this));
                                return;
                            case "SETTINGS":
                                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                                return;
                            case "QUIT GAME":
                                mc.shutdown();
                                return;
                            default:
                                return;
                        }
                    }
                }
            }
        }
    }

    private static void process3() {
        Path path = computePath();
        if (path != null) {
            try {
                Files.createDirectories(path.getParent());
                Files.writeString(path, Integer.toString(count16));
            } catch (IOException e) {
                LogManager.getLogger(ChangelogScreen.class).warn("Could not save menu opacity", (Throwable)e);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            ScaledResolution sr = new ScaledResolution(mc);
            this.scaledWidth = sr.getScaledWidth();
            this.scaledHeight = sr.getScaledHeight();
            this.process2();
            EffectsComponent.handleInt(mc.displayWidth, mc.displayHeight);
            GL20.glUseProgram(0);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.disableFog();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableAlpha();
            OpenGlHelper.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
            GlStateManager.disableTexture2D();
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            long now = System.nanoTime();
            float f = Math.min(0.05F, (float)(now - this.timestamp10) / 1.0E9F);
            this.timestamp10 = now;
            this.factor = 1.0F - (float)Math.exp(-14.0F * f);
            Gui.drawRect(0, 0, this.scaledWidth, this.scaledHeight, -16249321);
            boolean visible = this.openScreen.isVisible();
            if (!visible) {
                NebulaComponent.computeMainMenuScene2().handleInt(this.scaledWidth, this.scaledHeight, 0.0F, mouseX, mouseY);
            }

            if (!visible) {
                this.handleInt3(mouseX, mouseY);
                this.handleInt5(mouseX, mouseY);
                this.process4();
                this.handleInt7(mouseX, mouseY);
                this.handleInt4(mouseX, mouseY);
                this.handleInt2(mouseX, mouseY);
            }

            this.handleInt6(mouseX, mouseY);
            if (visible) {
                this.openScreen.drawScreen(mouseX, mouseY, partialTicks);
            }

            if (!visible) {
                super.drawScreen(mouseX, mouseY, partialTicks);
                this.process();
            }
        }
    }

    private void handleInt(int n, int n2, Color color, Color color2, Color color3, int n3) {
        if (this.drawStringWithShadowComponent2 != null) {
            int n4 = n;

            for (int i = 0; i < "brutality.lol".length(); i++) {
                String str = String.valueOf("brutality.lol".charAt(i));
                Color color4;
                if (i == n3 && n3 < "brutality.lol".length()) {
                    color4 = color3;
                } else {
                    color4 = i < n3 ? color : color2;
                }

                int textWidth = this.drawStringWithShadowComponent2.getStringWidth(str);
                this.drawStringWithShadowComponent2
                    .drawString(str, n4 + 1, n2 + 1, new Color(color4.getRed() / 4, color4.getGreen() / 4, color4.getBlue() / 4, 160).getRGB());
                this.drawStringWithShadowComponent2.drawString(str, n4, n2, color4.getRGB() | 0xFF000000);
                n4 += textWidth;
            }
        }
    }

    private Color computeColor(Color color, float f) {
        return new Color(
            (int)Math.min(255.0F, color.getRed() + (255 - color.getRed()) * f),
            (int)Math.min(255.0F, color.getGreen() + (255 - color.getGreen()) * f),
            (int)Math.min(255.0F, color.getBlue() + (255 - color.getBlue()) * f)
        );
    }

    @Override
    public void initGui() {
        NebulaComponent.init();
        ScaledResolution sr = new ScaledResolution(mc);
        int scaledWidth = sr.getScaledWidth();
        int scaledHeight = sr.getScaledHeight();
        boolean flag = this.count14 != scaledWidth || this.count15 != scaledHeight;
        this.count14 = scaledWidth;
        this.count15 = scaledHeight;
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
        this.count6 = Math.max(1, Math.min(250, this.scaledWidth - 40));
        this.count7 = Math.max(15, Math.min(20, this.scaledHeight / 30));
        this.count8 = Math.max(3, Math.min(6, this.scaledHeight / 100));
        int n = this.scaledHeight / 9;
        int n2 = Math.max(18, this.scaledHeight / 30);
        int n3 = Math.max(14, this.scaledHeight / 42);
        FontComponent.handleInt(n, n2, n3);
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2();
        this.drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent();
        this.drawStringWithShadowComponent3 = FontComponent.computeDrawStringWithShadowComponent3();
        int n4 = Math.max(14, this.scaledHeight / 42);
        this.drawStringWithShadowComponent4 = FontComponent.computeDrawStringWithShadowComponent2(n4, false);
        this.drawStringWithShadowComponent5 = FontComponent.computeDrawStringWithShadowComponent2(16, false);
        this.drawStringWithShadowComponent6 = FontComponent.computeDrawStringWithShadowComponent2(14, false);
        if (flag || this.flag3) {
            NebulaComponent.computeMainMenuScene2().handleInt(this.scaledWidth, this.scaledHeight);
        }

        if (this.openScreen == null) {
            this.openScreen = new OpenScreen();
        }

        OpenScreen.mc = mc;
        this.openScreen.width = this.scaledWidth;
        this.openScreen.height = this.scaledHeight;
        if (flag || this.flag3) {
            this.openScreen.initGui();
        }

        for (int i = 0; i < this.floatValueArray.length; i++) {
            this.floatValueArray[i] = 0.0F;
            this.flagArray[i] = false;
        }

        this.text2 = computeString2();
        this.timestamp10 = System.nanoTime();
        this.factor2 = 0.0F;
        this.factor3 = 0.0F;
        this.factor4 = 0.0F;
        this.flag7 = false;
        this.flag2 = false;
        if (this.flag3) {
            this.count2 = 0;
            this.count3 = 0;
            this.count4 = 0;
            this.timestamp4 = System.currentTimeMillis();
            this.timestamp5 = System.currentTimeMillis();
            this.timestamp6 = System.currentTimeMillis();
            this.flag3 = false;
        }
    }

    private void handleLong(long l, int[] ints) {
        int n = "Brutality".length() + this.text2.length();
        switch (this.count2) {
            case 0:
                if (this.count3 <= n) {
                    if (l - this.timestamp5 >= 120L) {
                        this.count3++;
                        this.timestamp5 = l;
                    }

                    if (this.count3 > n) {
                        this.count2 = 1;
                        this.timestamp4 = l;
                    }
                }
                break;
            case 1:
                if (l - this.timestamp4 >= 600L) {
                    this.count2 = 2;
                    this.count4 = 0;
                    this.timestamp4 = l;
                    this.timestamp5 = l;
                }
                break;
            case 2:
                if (l - this.timestamp5 >= 180L) {
                    this.count4++;
                    this.timestamp5 = l;
                    if (this.count4 >= 6) {
                        this.count2 = 3;
                        this.count3 = 0;
                        this.timestamp4 = l;
                        this.timestamp5 = l;
                    }
                }
                break;
            case 3:
                if (l - this.timestamp5 >= 120L) {
                    this.count3++;
                    this.timestamp5 = l;
                    if (this.count3 > n) {
                        this.count2 = 0;
                        this.count3 = 0;
                        this.count4 = 0;
                        this.timestamp4 = l;
                        this.timestamp5 = l;
                    }
                }
        }
    }

    private Rectangle computeRectangle3() {
        return new Rectangle(this.scaledWidth - 8 - 20, this.scaledHeight - 8 - 20, 20, 20);
    }

    private void handleInt7(int n, int n2) {
        Rectangle rect = this.computeRectangle2();
        boolean flag = rect.contains(n, n2);
        this.factor2 = this.factor2 + ((flag ? 1.0F : 0.0F) - this.factor2) * this.factor;
        MainMenuIconRenderer.handleRectangle(
            rect,
            "Changelog",
            this.computeString(),
            this.drawStringWithShadowComponent5,
            this.drawStringWithShadowComponent6,
            this.factor2,
            false,
            this.computeIntArray(),
            ThemesMode.CHANGELOG
        );
    }

    private Color computeColor(int n, Color color, Color color2, Color color3) {
        int n2 = "Brutality".length() + this.text2.length();
        switch (this.count2) {
            case 0:
                if (n == this.count3 && this.count3 < n2) {
                    return color3;
                } else {
                    if (n < this.count3) {
                        return color;
                    }

                    return color2;
                }
            case 1:
                return color;
            case 2:
                return this.count4 % 2 == 0 ? color2 : color;
            case 3:
                if (n == this.count3 && this.count3 < n2) {
                    return color3;
                } else {
                    if (n < this.count3) {
                        return color2;
                    }

                    return color;
                }
            default:
                return color2;
        }
    }

    private static String computeString2() {
        String version = Brutality.version;
        if (version != null && !version.isEmpty()) {
            int n = version.indexOf(32);
            return n > 0 ? version.substring(0, n) : version;
        }
        return "2.0";
    }

    private String computeString() {
        Entry entry = ChangeLog.computeEntry();
        return entry == null ? "" : "v" + entry.text;
    }

    private void handleInt(int n, int n2, int[] ints) {
        long now = System.currentTimeMillis();
        if (this.timestamp6 == 0L) {
            this.timestamp6 = now;
        }

        if (now - this.timestamp6 >= 5000L) {
            int n3 = this.count5;

            while (n3 == this.count5 && stringArray.length > 1) {
                n3 = (int)(Math.random() * stringArray.length);
            }

            this.count5 = n3;
            this.timestamp6 = now;
        }

        String str = stringArray[this.count5];
        float f = Math.min(1.0F, (float)(now - this.timestamp6) / 5000.0F);
        float f2 = Math.min(1.0F, f * 25.0F);
        float f3 = Math.min(1.0F, (1.0F - f) * 25.0F);
        float f4 = Math.max(0.0F, Math.min(f2, f3));
        float f5 = 1.0F + 0.06F * (float)Math.sin(now % 800L / 800.0 * Math.PI * 2.0);
        float f6 = 2.0F * Math.min(n - 12, this.scaledWidth - n - 12);
        float f7 = Math.min(f6, this.count6 * 1.25F) / Math.max(1.0F, this.drawStringWithShadowComponent2.getStringWidth(str) * 1.06F);
        float f8 = Math.min(0.86F, f7) * f5 * f4;
        if (!(f8 < 1.0E-4F)) {
            Color color = new Color(computeInt(220 + (ints[0] - 220) / 4), computeInt(200), 80, (int)(255.0F * f4));
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)n, (float)n2, 0.0F);
            GlStateManager.rotate(-6.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(f8, f8, 1.0F);
            GlStateManager.translate(-this.drawStringWithShadowComponent2.getStringWidth(str) / 2.0F, 0.0F, 0.0F);
            this.drawStringWithShadowComponent2.drawString(str, 1, 1, new Color(0, 0, 0, (int)(140.0F * f4)).getRGB());
            this.drawStringWithShadowComponent2.drawString(str, 0, 0, color.getRGB());
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (this.openScreen.isVisible()) {
            this.openScreen.handleMouseInput();
        }
    }

    private void process4() {
        String str = "Made with <3 for the Hypixel Pit community";
        int n = this.scaledHeight - this.drawStringWithShadowComponent2.FONT_HEIGHT - 6;
        int n2 = n - this.drawStringWithShadowComponent2.FONT_HEIGHT - 2;
        int n3 = 14;
        this.handleInt(n3, n2);
        this.drawStringWithShadowComponent2.drawStringWithShadow(str, n3, n, new Color(200, 200, 200).getRGB());
    }

    private static Path computePath() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc != null && mc.mcDataDir != null ? mc.mcDataDir.toPath().resolve("Brutality/menu-opacity.txt") : null;
    }

    private void handleInt3(int n, int n2) {
        long now = System.nanoTime();
        float f = Math.min(0.05F, (float)(now - this.timestamp13) / 1.0E9F);
        this.timestamp13 = now;
        float f2 = 1.0F - (float)Math.exp(-14.0F * f);
        if (NebulaComponent.computeMainMenuScene2() != null) {
            CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(Math.max(15, this.scaledHeight / 34), false);
            float f3 = computeInt2() / 100.0F;

            for (int i = 0; i < this.stringArray2.length; i++) {
                Rectangle rect = this.computeRectangle(i);
                boolean flag = rect.contains(n, n2);
                this.flagArray[i] = flag;
                this.floatValueArray[i] = this.floatValueArray[i] + ((flag ? 1.0F : 0.0F) - this.floatValueArray[i]) * f2;
                MainMenuIconRenderer.handleInt(rect.x, rect.y, rect.width, rect.height, stringArray3[i], drawStringWithShadowComponent, this.floatValueArray[i], f3);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    private Color computeColor2(Color color, float f) {
        return new Color((int)Math.max(0.0F, color.getRed() * f), (int)Math.max(0.0F, color.getGreen() * f), (int)Math.max(0.0F, color.getBlue() * f));
    }

    private int[] computeIntArray() {
        int n = NebulaComponent.computeInt(NebulaComponent.computeMainMenuScene2());
        if (n < 0 || n >= intValueArray.length) {
            n = 0;
        }

        return intValueArray[n];
    }

    private Rectangle computeRectangle4() {
        Rectangle rect = this.computeRectangle();
        int n = this.drawStringWithShadowComponent6.getStringWidth("Opacity") + this.drawStringWithShadowComponent6.getStringWidth("100%") + 36 + 44;
        return new Rectangle(rect.x - n - 5, rect.y, n, rect.height);
    }

    private void handleInt(int n, int n2, Color color, Color color2, Color color3) {
        if (this.drawStringWithShadowComponent4 != null) {
            String str = this.text2;
            if (str != null && !str.isEmpty()) {
                int n3 = n;
                int n4 = n2;

                for (int i = 0; i < str.length(); i++) {
                    String str2 = String.valueOf(str.charAt(i));
                    int textWidth = this.drawStringWithShadowComponent4.getStringWidth(str2);
                    Color color4 = this.computeColor("Brutality".length() + i, color, color2, color3);
                    this.drawStringWithShadowComponent4.drawString(str2, n3 + 1, n4 + 1, new Color(0, 0, 0, 160).getRGB());
                    this.drawStringWithShadowComponent4.drawString(str2, n3, n4, color4.getRGB());
                    n3 += textWidth;
                }
            }
        }
    }

    private void handleInt6(int n, int n2) {
        Rectangle rect = this.computeRectangle();
        this.flag2 = rect.contains(n, n2);
        this.factor3 = this.factor3 + ((this.flag2 ? 1.0F : 0.0F) - this.factor3) * this.factor;
        MainMenuIconRenderer.handleRectangle(
            rect,
            "Themes",
            "",
            this.drawStringWithShadowComponent5,
            this.drawStringWithShadowComponent6,
            this.factor3,
            this.openScreen.isVisible(),
            this.computeIntArray(),
            ThemesMode.THEMES
        );
    }

    private static int computeInt2() {
        if (count16 >= 0) {
            return count16;
        }
        count16 = 100;
        Path path = computePath();

        try {
            if (path != null && Files.isRegularFile(path) && Files.size(path) < 16L) {
                count16 = Math.max(0, Math.min(100, Integer.parseInt(Files.readString(path).trim())));
            }
        } catch (NumberFormatException | IOException ignored) {
        }

        return count16;
    }

    private void handleInt5(int n, int n2) {
        int n3 = Math.max(34, this.computeInt3() - 16 - this.drawStringWithShadowComponent.FONT_HEIGHT);
        int n4 = 0;

        for (int i = 0; i < "Brutality".length(); i++) {
            n4 += this.drawStringWithShadowComponent.getStringWidth(String.valueOf("Brutality".charAt(i)));
        }

        int n5 = (this.scaledWidth - n4) / 2;
        int[] ints = this.computeIntArray();
        this.handleLong(System.currentTimeMillis(), ints);
        Color color = new Color(ints[0], ints[1], ints[2]);
        Color color2 = new Color(238, 247, 255);
        Color color3 = this.computeColor(color, 0.7F);
        int n6 = n5;

        for (int j = 0; j < "Brutality".length(); j++) {
            String str = String.valueOf("Brutality".charAt(j));
            this.drawStringWithShadowComponent.drawString(str, n6 + 1, n3 + 1, -1610612736);
            this.drawStringWithShadowComponent.drawString(str, n6, n3, this.computeColor(j, color, color2, color3).getRGB());
            n6 += this.drawStringWithShadowComponent.getStringWidth(str);
        }

        this.handleInt(n5 + n4 + 5, n3 + 3, color, color2, color3);
        this.handleInt(n5 + n4, n3 - 18, ints);
    }

    private void handleInt2(int n, int n2) {
        Rectangle rect = this.computeRectangle5();
        Rectangle rect2 = this.computeRectangle3();
        this.factor5 = this.factor5 + ((rect.contains(n, n2) ? 1.0F : 0.0F) - this.factor5) * this.factor;
        this.factor6 = this.factor6 + ((rect2.contains(n, n2) ? 1.0F : 0.0F) - this.factor6) * this.factor;
        MainMenuIconRenderer.handleInt(rect.x, rect.y, 20, ThemesMode.SETTINGS, this.factor5);
        MainMenuIconRenderer.handleInt(rect2.x, rect2.y, 20, ThemesMode.QUIT, this.factor6);
        String str = this.factor5 > this.factor6 ? "Settings" : "Quit Game";
        float f = Math.max(this.factor5, this.factor6);
        if (f > 0.03F) {
            GlStateManager.enableTexture2D();
            this.drawStringWithShadowComponent5
                .drawString(
                    str,
                    rect.x - 8 - this.drawStringWithShadowComponent5.getStringWidth(str),
                    rect.y + (20 - this.drawStringWithShadowComponent5.FONT_HEIGHT) / 2 + 1,
                    new Color(220, 220, 225, (int)(220.0F * f)).getRGB()
                );
        }
    }

    private void process() {
        if (!this.flag5) {
            this.flag5 = true;
            if (!flag4) {
                flag4 = true;
                this.timestamp11 = System.nanoTime();
            }
        }

        if (this.timestamp11 != 0L) {
            boolean flag = "Halloween".equals(NebulaComponent.computeMainMenuScene2().getName());
            long l = flag ? 1650000000L : 900000000L;
            float f = Math.min(1.0F, Math.max(0.0F, (float)(System.nanoTime() - this.timestamp11) / (float)l));
            if (f >= 1.0F) {
                this.timestamp11 = 0L;
            } else if (flag) {
                float f2 = 1.0F - Math.min(1.0F, f / 0.38F);
                f2 = f2 * f2 * (3.0F - 2.0F * f2);
                Gui.drawRect(0, 0, this.scaledWidth, this.scaledHeight, (int)(235.0F * f2) << 24);
                if (NebulaComponent.computeMainMenuScene2() instanceof DisposeComponent) {
                    ((DisposeComponent)NebulaComponent.computeMainMenuScene2()).handleInt(this.scaledWidth, this.scaledHeight, f);
                }
            } else {
                float f3 = 1.0F - (float)Math.pow(1.0F - f, 3.0);
                int n = Math.round(this.scaledHeight * 0.5F * (1.0F - f3));
                Gui.drawRect(0, 0, this.scaledWidth, n, -16249321);
                Gui.drawRect(0, this.scaledHeight - n, this.scaledWidth, this.scaledHeight, -16249321);
                if (n > 0) {
                    Color color = OceanComponent.computeColor();
                    Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(90.0F * (1.0F - f)));
                    OutlineThicknessHelper.handleFloat3(0.0F, n, this.scaledWidth, 2.0F, 0.0F, new Color(0, 0, 0, 0), color2);
                    OutlineThicknessHelper.handleFloat3(0.0F, this.scaledHeight - n - 2, this.scaledWidth, 2.0F, 0.0F, color2, new Color(0, 0, 0, 0));
                }

                Gui.drawRect(0, 0, this.scaledWidth, this.scaledHeight, (int)(100.0F * (1.0F - f3)) << 24);
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.openScreen.isVisible()) {
            this.openScreen.updateScreen();
        }
    }
}
