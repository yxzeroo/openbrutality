package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.brutality.event.Event;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.interfaces.AtmosphereHandler;
import org.brutality.module.impl.visual.interfaces.BrutalityHandler;
import org.brutality.module.impl.visual.interfaces.GwapcheatsGgHandler;
import org.brutality.module.impl.visual.interfaces.PrimecheatsGgHandler;
import org.brutality.module.impl.visual.interfaces.ThemeHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.IsChoiceHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class InterfaceType extends Module {
    private static InterfaceType instance;
    private final ThemeHandler themeHandler;
    private final PrimecheatsGgHandler primecheatsGgHandler;
    private final AtmosphereHandler atmosphereHandler;
    private final GwapcheatsGgHandler gwapcheatsGgHandler;
    private final BrutalityHandler brutalityHandler;
    private final ModeSetting gwapCheatsSetting = new ModeSetting(
        "Mode", this, "Brutality", Arrays.asList("Brutality", "PrimeCheats", "Atmosphere", "Timewarp", "WeedHack", "Adjust", "Custom", "GwapCheats")
    );
    private final NumberSetting hudThemeColorSetting = new NumberSetting("HUD Theme Color", this, 0.0, 0.0, 9.0, 1.0);
    private final NumberSetting xSetting = new NumberSetting("X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 10.0, 0.0, 1080.0, 1.0);
    private final ModeSetting primeSetting = new ModeSetting("Watermark Color", this, "Normal", Arrays.asList("Normal", "Prime"));
    private final BooleanSetting followThemeSetting = new BooleanSetting("Follow Theme", this, false);
    private final ModeSetting atmosphereFontSetting;
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 2.0, 0.1);
    private final StringSetting brutalitySetting = new StringSetting("Text", this, "Brutality");
    private final ColorSetting color1Setting = new ColorSetting("Color 1", this, 0.0F);
    private final ColorSetting color2Setting = new ColorSetting("Color 2", this, 0.33F);
    private final ColorSetting color3Setting = new ColorSetting("Color 3", this, 0.66F);
    private final BooleanSetting chromaSetting = new BooleanSetting("Chroma", this, false);
    private final NumberSetting gradientSpeedSetting = new NumberSetting("Gradient Speed", this, 0.5, 0.001, 2.0, 3.0);
    private final BooleanSetting customFontSetting = new BooleanSetting("Custom Font", this, false);
    private final ModeSetting fontSetting = IsChoiceHelper.computeModeSetting("Font", this);
    private final NumberSetting fontSizeSetting = new NumberSetting("Font Size", this, 9.0, 7.0, 18.0, 0.5);
    private CustomFontRenderer drawStringWithShadowComponent = null;
    private String text = "";
    private double amount = -1.0;
    private float factor = 0.0F;
    private final String text2 = "weedhack premium beta";
    private final Color[] colorArray = new Color[]{
        new Color(50, 50, 50),
        new Color(255, 200, 200),
        new Color(99, 249, 255),
        new Color(231, 39, 24),
        new Color(215, 166, 231),
        new Color(255, 215, 0),
        new Color(240, 240, 240),
        new Color(125, 204, 241),
        new Color(160, 230, 225),
        new Color(17, 192, 45)
    };
    private float factor2 = 0.0F;
    private float factor3 = 0.3F;

    private void handleFloat(float f, float f2, FontRenderer fontRenderer) {
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
            HudDragHelper.handleObject(this);
        } else {
            int[] ints = HudDragHelper.computeIntArray();
            int n = Math.max(90, fontRenderer.getStringWidth("Interface preview"));
            if (Mouse.isButtonDown(0) && !HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(ints[0], ints[1], f, f2, n, fontRenderer.FONT_HEIGHT + 4)
                )
             {
                HudDragHelper.matchesObject(this, ints[0], ints[1], (int)f, (int)f2);
            } else if (!Mouse.isButtonDown(0)) {
                HudDragHelper.handleObject(this);
            }

            int[] ints2 = HudDragHelper.computeIntArray(this);
            if (ints2 != null) {
                this.xSetting.handleDouble2(ints2[0]);
                this.ySetting.handleDouble2(ints2[1]);
            }
        }
    }

    private Color computeColor(float f) {
        return Color.getHSBColor(f, 1.0F, 1.0F);
    }

    private void handleScaledResolution2(ScaledResolution sr, float f, float f2) {
        this.factor2 = (this.factor2 + 0.005F) % 1.0F;
        this.factor3 = (this.factor3 + 0.005F) % 1.0F;
        Color hsbColor = Color.getHSBColor(this.factor2, 0.7F, 1.0F);
        Color hsbColor2 = Color.getHSBColor(this.factor3, 0.7F, 1.0F);
        FontRenderer fontRenderer = this.computeFontRenderer();
        int n = fontRenderer.getStringWidth("weedhack premium beta") + 8;
        int n2 = fontRenderer.FONT_HEIGHT + 7;
        this.drawRect((int)f, (int)f2, (int)f + n, (int)f2 + n2, new Color(70, 70, 70, 255).getRGB());
        this.drawRect((int)f + 2, (int)f2 + 2, (int)f + n - 2, (int)f2 + n2 - 2, new Color(10, 10, 10, 255).getRGB());
        this.drawGradientRect((int)f + 3, (int)f2 + 3, (int)f + n - 4, (int)f2 + 4, hsbColor.getRGB(), hsbColor2.getRGB());
        fontRenderer.drawStringWithShadow("weedhack premium beta", f + 4.0F, f2 + 5.0F, Color.WHITE.getRGB());
    }

    private FontRenderer computeFontRenderer() {
        return (FontRenderer)(this.customFontSetting.isEnabled() && this.drawStringWithShadowComponent != null && !this.fontSetting.matchesString2("Minecraft")
            ? this.drawStringWithShadowComponent
            : mc.fontRendererObj);
    }

    private void handleScaledResolution(ScaledResolution sr, float f, float f2) {
        int n = (int)this.hudThemeColorSetting.computeDouble();
        Color color = this.colorArray[n];
        FontRenderer fontRenderer = this.computeFontRenderer();
        fontRenderer.drawStringWithShadow("A", f, f2, color.getRGB());
        int textWidth = fontRenderer.getStringWidth("A");
        fontRenderer.drawStringWithShadow("djust", f + textWidth + 2.0F, f2, Color.WHITE.getRGB());
    }

    public void process3() {
        this.process4();
    }

    private Color computeColor(Color color, Color color2, float f) {
        int n = (int)(color.getRed() + (color2.getRed() - color.getRed()) * f);
        int n2 = (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f);
        int n3 = (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f);
        return new Color(Math.max(0, Math.min(255, n)), Math.max(0, Math.min(255, n2)), Math.max(0, Math.min(255, n3)));
    }

    private void drawRect(int n, int n2, int n3, int n4, int n5) {
        Gui.drawRect(n, n2, n3, n4, n5);
    }

    @Override
    public boolean onBlurMode(SetShaderTypeEvent.BlurMode blurMode) {
        return blurMode == SetShaderTypeEvent.BlurMode.BLUR && this.gwapCheatsSetting.matchesString2("Timewarp") && !Watermark.isVisible();
    }

    private void handleScaledResolution3(ScaledResolution sr, float f, float f2) {
        this.factor = this.factor + (float)(this.gradientSpeedSetting.computeDouble() * 0.001F);
        if (this.factor > 1.0F) {
            this.factor = 0.0F;
        }

        String val = this.brutalitySetting.getValue();
        Color color;
        if (this.chromaSetting.isEnabled()) {
            color = this.computeColor(this.factor);
        } else {
            Color color2 = this.color1Setting.computeColor();
            Color color3 = this.color2Setting.computeColor();
            Color color4 = this.color3Setting.computeColor();
            color = this.computeColor(color2, color3, color4, this.factor);
        }

        this.computeFontRenderer().drawStringWithShadow(val, f, f2, color.getRGB());
    }

    public static InterfaceType getInstance() {
        return instance;
    }

    private void process4() {
        boolean flag = this.gwapCheatsSetting.matchesString2("Brutality");
        boolean flag2 = this.gwapCheatsSetting.matchesString2("Custom");
        boolean flag3 = this.gwapCheatsSetting.matchesString2("Atmosphere");
        boolean flag4 = this.gwapCheatsSetting.matchesString2("Timewarp");
        this.followThemeSetting.setVisible(flag);
        this.primeSetting.setVisible(flag && !this.followThemeSetting.isEnabled());
        this.brutalitySetting.setVisible(flag2);
        this.color1Setting.setVisible(flag2);
        this.color2Setting.setVisible(flag2);
        this.color3Setting.setVisible(flag2);
        this.chromaSetting.setVisible(flag2);
        this.gradientSpeedSetting.setVisible(flag2);
        this.atmosphereFontSetting.setVisible(flag3);
        this.scaleSetting.setVisible(flag3);
        this.customFontSetting.setVisible(!flag4);
        boolean flag5 = this.customFontSetting.isEnabled() && !flag4;
        this.fontSetting.setVisible(flag5 || flag4);
        this.fontSizeSetting.setVisible(false);
    }

    public InterfaceType() {
        super("Interface", 0, Category.VISUAL);
        this.themeHandler = new ThemeHandler(Minecraft.getMinecraft());
        this.primecheatsGgHandler = new PrimecheatsGgHandler(Minecraft.getMinecraft());
        this.atmosphereHandler = new AtmosphereHandler(Minecraft.getMinecraft());
        this.gwapcheatsGgHandler = new GwapcheatsGgHandler(Minecraft.getMinecraft());
        this.brutalityHandler = new BrutalityHandler(Minecraft.getMinecraft());
        this.atmosphereFontSetting = IsChoiceHelper.computeModeSetting("Atmosphere Font", this);
        this.handleSettingArray(
            new Setting[]{
                this.customFontSetting,
                this.gwapCheatsSetting,
                this.hudThemeColorSetting,
                this.xSetting,
                this.ySetting,
                this.primeSetting,
                this.followThemeSetting,
                this.atmosphereFontSetting,
                this.scaleSetting,
                this.brutalitySetting,
                this.color1Setting,
                this.color2Setting,
                this.color3Setting,
                this.chromaSetting,
                this.gradientSpeedSetting,
                this.fontSetting,
                this.fontSizeSetting
            }
        );
        this.process4();
        instance = this;
    }

    private void drawGradientRect(int n, int n2, int n3, int n4, int n5, int n6) {
        float f = (n5 >> 24 & 0xFF) / 255.0F;
        float f2 = (n5 >> 16 & 0xFF) / 255.0F;
        float f3 = (n5 >> 8 & 0xFF) / 255.0F;
        float f4 = (n5 & 0xFF) / 255.0F;
        float f5 = (n6 >> 24 & 0xFF) / 255.0F;
        float f6 = (n6 >> 16 & 0xFF) / 255.0F;
        float f7 = ((0xFF | ~(n6 >> 8)) - ~(n6 >> 8)) / 255.0F;
        float f8 = (n6 & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(n3, n2, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
        worldRenderer.func_181662_b(n, n2, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
        worldRenderer.func_181662_b(n, n4, 0.0).func_181666_a(f6, f7, f8, f5).func_181675_d();
        worldRenderer.func_181662_b(n3, n4, 0.0).func_181666_a(f6, f7, f8, f5).func_181675_d();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Override
    public void onEvent(Event event) {
        if (!Watermark.isVisible()) {
            if (this.checkState44()) {
                if (event instanceof SetShaderTypeEvent
                    && this.gwapCheatsSetting.matchesString2("Timewarp")
                    && ((SetShaderTypeEvent)event).getShaderType() == SetShaderTypeEvent.BlurMode.BLUR) {
                    this.brutalityHandler.setText(this.fontSetting.getText());
                    this.brutalityHandler.handleFloat((float)this.xSetting.computeDouble(), (float)this.ySetting.computeDouble());
                }
            }
        }
    }

    private Color computeColor(Color color, Color color2, Color color3, float f) {
        if (f < 0.33F) {
            return this.computeColor(color, color2, f / 0.33F);
        }
        return f < 0.66F ? this.computeColor(color2, color3, (f - 0.33F) / 0.33F) : this.computeColor(color3, color, (f - 0.66F) / 0.34F);
    }

    private void process() {
        if (!this.customFontSetting.isEnabled()) {
            this.drawStringWithShadowComponent = null;
        } else {
            String text = this.fontSetting.getText();
            double d = 20.0;
            if (!text.equals(this.text) || d != this.amount) {
                this.text = text;
                this.amount = d;
                if ("Minecraft".equals(text)) {
                    this.drawStringWithShadowComponent = null;
                } else {
                    this.drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(text, (float)d);
                }
            }
        }
    }

    @Override
    public void onRender() {
        if (this.checkState44()) {
            if (!Watermark.isVisible()) {
                this.process();
                this.process4();
                ScaledResolution sr = InvalidateHelper.computeScaledResolution(Minecraft.getMinecraft());
                float f = (float)this.xSetting.computeDouble();
                float f2 = (float)this.ySetting.computeDouble();
                FontRenderer fontRenderer = this.computeFontRenderer();
                this.handleFloat(f, f2, fontRenderer);
                f = (float)this.xSetting.computeDouble();
                f2 = (float)this.ySetting.computeDouble();
                this.themeHandler.setFontRenderer(fontRenderer);
                this.primecheatsGgHandler.setFontRenderer(fontRenderer);
                this.gwapcheatsGgHandler.setFontRenderer(fontRenderer);
                this.atmosphereHandler.setFontRenderer(fontRenderer);
                String text = this.gwapCheatsSetting.getText();
                switch (text) {
                    case "Brutality":
                        if (this.followThemeSetting.isEnabled()) {
                            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
                            this.themeHandler.setBrutalityHelper(brutalityHelper);
                            this.themeHandler.handleString("Theme");
                        } else {
                            this.themeHandler.handleString(this.primeSetting.getText());
                        }

                        this.themeHandler.handleScaledResolution(sr, f, f2);
                        break;
                    case "PrimeCheats":
                        this.primecheatsGgHandler.handleScaledResolution(sr, f, f2);
                        break;
                    case "Atmosphere":
                        String str = this.customFontSetting.isEnabled() ? this.fontSetting.getText() : this.atmosphereFontSetting.getText();
                        this.atmosphereHandler.handleScaledResolution(sr, f, f2, str, (float)this.scaleSetting.computeDouble());
                        break;
                    case "Timewarp":
                        this.brutalityHandler.setText(this.fontSetting.getText());
                        this.brutalityHandler.handleFloat(f, f2, ModuleSpecificHelper.computeBrutalityHelper());
                        break;
                    case "WeedHack":
                        this.handleScaledResolution2(sr, f, f2);
                        break;
                    case "Adjust":
                        this.handleScaledResolution(sr, f, f2);
                        break;
                    case "Custom":
                        this.handleScaledResolution3(sr, f, f2);
                        break;
                    case "GwapCheats":
                        this.gwapcheatsGgHandler.handleScaledResolution(sr, f, f2);
                }
            }
        }
    }

    public String computeString() {
        return this.gwapCheatsSetting.getText();
    }
}
