package org.brutality.clickgui.classic;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.brutality.Brutality;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.font.atlas.ClassicFontRegistry;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.client.ClickGUI;
import org.brutality.script.ScriptModule;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
// VENDOR-DISABLED: import org.brutality.system.stafftracker.StaffTrackerService;
import org.brutality.utils.render.BindTextureHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.LocationHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.shader.impl.DualKawaseBlurShader;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public final class DrawScriptsScreen extends CustomGuiScreen {
    private static final Color color = new Color(12, 14, 26, 178);
    private static final Color color2 = new Color(24, 28, 50, 188);
    private static final Color color3 = new Color(18, 21, 37, 165);
    private static final Color color4 = new Color(60, 64, 96, 200);
    private static final Color color5 = new Color(236, 238, 248);
    private static final Color color6 = new Color(150, 154, 176);
    private static final float factor = 8.0F;
    private static final float factor2 = 150.0F;
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    private final GuiScreen guiScreen;
    private final DrawScriptsScreen.ThemesMode themesMode;
    private final List<DrawScriptsScreen.Entry> entries = new ArrayList<>();
    private final List<DrawScriptsScreen.Entry2> entries2 = new ArrayList<>();
    private final List<Object> entries3 = new CopyOnWriteArrayList<>(); // VENDOR-DISABLED: StaffTrackerService.Entry2 list
    private final Map<Object, Float> lookupMap = new IdentityHashMap<>();
    private long timestamp;
    private long timestamp2;
    private long timestamp3;
    private float factor3 = 0.016666668F;
    private boolean flag;
    private float factor4;
    private float factor5;
    private float factor6;
    private float factor7;
    private float factor8;
    private boolean flag2;
    private String text = "Loading...";
    private static final float factor9 = 36.0F;
    private static final float factor10 = 6.0F;
    private static final float factor11 = 16.0F;
    private static final int count = 3;
    private static final long timestamp4 = 2200L;

    private float drawScriptSetting(Setting setting, float f, float f2, float f3, int n, int n2, float f4) {
        float f5 = setting instanceof NumberSetting ? 18.0F : 14.0F;
        boolean flag = matchesFloat(n, n2, f + 4.0F, f2, f3 - 8.0F, f5);
        float f6 = this.computeFloat(setting, flag ? 1.0F : 0.0F, 30.0F);
        if (f6 > 0.01F) {
            Gui.drawRect((int)f, (int)f2, (int)(f + f3), (int)(f2 + f5), computeColor(Color.WHITE, f4 * f6 * 0.045F).getRGB());
        }

        String str = this.computeString(setting.getName());
        if (setting instanceof BooleanSetting boolSetting) {
            float f7 = boolSetting.isEnabled() ? 1.0F : 0.0F;
            ClassicFontRegistry.drawStringWithShadowComponent2
                .computeInt(this.computeString(str, f3 - 34.0F), f + 8.0F, f2 + 4.0F, computeColor(color6, f4).getRGB());
            LocationHelper.handleFloat2(
                f + f3 - 24.0F, f2 + 3.0F, 16.0F, 8.0F, 4.0F, computeColor(computeColor(color4, this.computeColor(), f7), f4)
            );
            LocationHelper.handleFloat2(f + f3 - 22.5F + f7 * 8.0F, f2 + 4.5F, 5.0F, 5.0F, 2.5F, computeColor(Color.WHITE, f4));
            this.handleFloat(f + 4.0F, f2, f3 - 8.0F, f5, () -> boolSetting.setEnabled(!boolSetting.isEnabled()), null);
        } else if (setting instanceof ModeSetting modeSetting) {
            String text = modeSetting.getText();
            float f8 = ClassicFontRegistry.drawStringWithShadowComponent2.computeFloat(text);
            ClassicFontRegistry.drawStringWithShadowComponent2
                .computeInt(this.computeString(str, f3 - f8 - 20.0F), f + 8.0F, f2 + 4.0F, computeColor(color6, f4).getRGB());
            ClassicFontRegistry.drawStringWithShadowComponent2
                .computeInt(text, f + f3 - f8 - 8.0F, f2 + 4.0F, computeColor(this.computeColor(), f4).getRGB());
            this.handleFloat(f + 4.0F, f2, f3 - 8.0F, f5, modeSetting::process2, modeSetting::process3);
        } else if (setting instanceof NumberSetting numberSetting) {
            String str2 = numberSetting.toString();
            float f9 = ClassicFontRegistry.drawStringWithShadowComponent2.computeFloat(str2);
            ClassicFontRegistry.drawStringWithShadowComponent2
                .computeInt(this.computeString(str, f3 - f9 - 20.0F), f + 8.0F, f2 + 2.0F, computeColor(color6, f4).getRGB());
            ClassicFontRegistry.drawStringWithShadowComponent2
                .computeInt(str2, f + f3 - f9 - 8.0F, f2 + 2.0F, computeColor(this.computeColor(), f4).getRGB());
            float f10 = f + 8.0F;
            float f11 = f3 - 16.0F;
            float f12 = f2 + 13.0F;
            double minValue = numberSetting.getMinValue();
            double maxValue = numberSetting.getMaxValue();
            float f13 = maxValue == minValue ? 0.0F : (float)((numberSetting.computeDouble() - minValue) / (maxValue - minValue));
            LocationHelper.handleFloat2(f10, f12, f11, 3.0F, 1.5F, computeColor(color4, f4));
            LocationHelper.handleFloat2(f10, f12, Math.max(3.0F, f11 * f13), 3.0F, 1.5F, computeColor(this.computeColor(), f4));
            this.handleFloat(f + 4.0F, f2, f3 - 8.0F, f5, () -> this.handleNumberSetting(numberSetting, n, f10, f11), null);
        } else if (setting instanceof StringSetting) {
            String val = ((StringSetting)setting).getValue();
            ClassicFontRegistry.drawStringWithShadowComponent2
                .computeInt(this.computeString(str + ": " + val, f3 - 16.0F), f + 8.0F, f2 + 4.0F, computeColor(color6, f4).getRGB());
        }

        return f2 + f5;
    }

    private float computeFloat(int n) {
        float f = 0.0F;

        for (List list : this.computeList()) {
            if (!list.isEmpty()) {
                int n2 = (list.size() + 3 - 1) / 3;
                f += 16.0F + n2 * 42.0F + 2.0F;
            }
        }

        return f;
    }

    private String computeString(String str, float f) {
        if (str == null) {
            return "";
        }
        if (ClassicFontRegistry.drawStringWithShadowComponent2.computeFloat(str) <= f) {
            return str;
        }
        String str2 = str;

        while (str2.length() > 1 && ClassicFontRegistry.drawStringWithShadowComponent2.computeFloat(str2 + "..") > f) {
            str2 = str2.substring(0, str2.length() - 1);
        }

        return str2 + "..";
    }

    private void handleFloat(float f, float f2, float f3, float f4, float f5) {
        if (OpenGlHelper.isFramebufferEnabled() && OpenGlHelper.shadersSupported) {
            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GlStateManager.colorMask(true, true, true, true);
                framebuffer = BindTextureHelper.computeFramebuffer(framebuffer);
                framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
                framebuffer.framebufferClear();
                framebuffer.bindFramebuffer(false);
                LocationHelper.handleFloat2(f, f2, f3, f4, 8.0F, computeColor(Color.WHITE, f5));
                framebuffer.unbindFramebuffer();
                mc.getFramebuffer().bindFramebuffer(true);
                DualKawaseBlurShader.handleInt(framebuffer.framebufferTexture, 2, 2);
                BindTextureHelper.resetColor();
            } catch (Exception ignored) {
            }
        }
    }

    private static float computeFloat(float f) {
        return 1.0F - (1.0F - f) * (1.0F - f);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.timestamp = System.currentTimeMillis();
        this.flag = false;
        if (this.themesMode == DrawScriptsScreen.ThemesMode.STAFF && this.entries3.isEmpty()) {
            this.process2();
        }
    }

    private void drawThemeCard(BrutalityHelper brutalityHelper, float f, float f2, float f3, int n, int n2, float f4, boolean flag) {
        boolean flag2 = matchesFloat(n, n2, f, f2, f3, 36.0F);
        float f5 = this.computeFloat(brutalityHelper, flag2 ? 1.0F : 0.0F, 30.0F);
        float f6 = f2 - f5;
        Color color = brutalityHelper.getColor();
        Color color22 = brutalityHelper.getColor2();
        float f7 = flag ? 1.0F : f5 * 0.6F;
        if (f7 > 0.01F) {
            for (int i = 3; i >= 1; i--) {
                float f8 = i * 2.2F;
                LocationHelper.handleFloat2(
                    f - f8,
                    f6 - f8 + 1.0F,
                    f3 + f8 * 2.0F,
                    36.0F + f8 * 2.0F,
                    4.0F + f8,
                    computeColor(brutalityHelper.computeColor(0.5F), f4 * f7 * (0.16F / i))
                );
            }

            LocationHelper.handleFloat2(
                f - 1.0F, f6 - 1.0F, f3 + 2.0F, 38.0F, 5.0F, computeColor(flag ? color : this.computeColor(), f4 * (flag ? 0.6F : f5 * 0.3F))
            );
        }

        LocationHelper.handleFloat2(f, f6, f3, 36.0F, 4.0F, computeColor(computeColor(color3, color2, f5 * 0.35F), f4));
        BindTextureHelper.handleDouble((double)f, (double)f6, (double)f3, 15.0);
        LocationHelper.handleFloat2(f, f6, f3, 18.0F, 4.0F, computeColor(color, f4));
        BindTextureHelper.handleInt(
            (int)f, (int)f6, (int)(f + f3), (int)(f6 + 15.0F), computeColor(color, f4).getRGB(), computeColor(color22, f4).getRGB()
        );
        float f9 = !flag && !(f5 > 0.01F) ? -1.0F : (float)(System.currentTimeMillis() % timestamp4) / 2200.0F;
        if (f9 >= 0.0F) {
            float f10 = f - 30.0F + (f3 + 60.0F) * f9;
            float f11 = f4 * (flag ? 0.3F : 0.22F * f5);
            BindTextureHelper.handleInt(
                (int)f10,
                (int)f6,
                (int)(f10 + 15.0F),
                (int)(f6 + 15.0F),
                computeColor(new Color(255, 255, 255, 0), f4).getRGB(),
                computeColor(new Color(255, 255, 255, (int)(255.0F * f11)), 1.0F).getRGB()
            );
            BindTextureHelper.handleInt(
                (int)(f10 + 15.0F),
                (int)f6,
                (int)(f10 + 30.0F),
                (int)(f6 + 15.0F),
                computeColor(new Color(255, 255, 255, (int)(255.0F * f11)), 1.0F).getRGB(),
                computeColor(new Color(255, 255, 255, 0), f4).getRGB()
            );
        }

        Gui.drawRect((int)f, (int)(f6 + 14.0F), (int)(f + f3), (int)(f6 + 15.0F), computeColor(new Color(0, 0, 0, 60), f4).getRGB());
        BindTextureHelper.handleDouble(0.0, (double)this.factor7, (double)this.width, (double)(this.factor8 - this.factor7));
        ClassicFontRegistry.drawStringWithShadowComponent2
            .computeInt(
                this.computeString(brutalityHelper.getName(), f3 - 22.0F),
                f + 6.0F,
                f6 + 21.0F,
                computeColor(flag ? color5 : computeColor(color6, color5, f5), f4).getRGB()
            );
        if (flag) {
            LocationHelper.handleFloat2(f + f3 - 11.0F, f6 + 22.0F, 5.0F, 5.0F, 2.5F, computeColor(color, f4));
            LocationHelper.handleFloat2(f + f3 - 12.5F, f6 + 20.5F, 8.0F, 8.0F, 4.0F, computeColor(color, f4 * 0.25F));
        }

        this.handleFloat(f, f6, f3, 36.0F, () -> {
            if (ClickGUI.instance != null) {
                ClickGUI.instance.handleBrutalityHelper(brutalityHelper);
            }
        }, null);
    }

    public static boolean matchesFloat(float f, float f2, float f3, float f4, float f5, float f6) {
        return f >= f3 && f <= f3 + f5 && f2 >= f4 && f2 <= f4 + f6;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            this.factor6 += eventDWheel > 0 ? -28.0F : 28.0F;
        }
    }

    private void handleFloat(float f, float f2, int n, int n2, int n3, float f3) {
        String str = this.flag2 ? "Loading" : "Refresh";
        float f4 = ClassicFontRegistry.drawStringWithShadowComponent2.computeFloat(str) + 12.0F;
        boolean flag = matchesFloat(n2, n3, f + n - f4, f2, f4, 16.0F);
        float f5 = this.computeFloat(this.entries3, flag ? 1.0F : 0.0F, 30.0F);
        LocationHelper.handleFloat2(f + n - f4, f2, f4, 16.0F, 4.0F, computeColor(computeColor(color3, this.computeColor(), f5 * 0.2F), f3));
        ClassicFontRegistry.drawStringWithShadowComponent2
            .computeInt(str, f + n - f4 + 6.0F, f2 + 5.0F, computeColor(flag ? color5 : color6, f3).getRGB());
        this.handleFloat(f + n - f4, f2, f4, 16.0F, this::process2, null);
        float f6 = f2 + 21.0F;
        // VENDOR-DISABLED: StaffTrackerService.Entry2 rendering loop
        ClassicFontRegistry.drawStringWithShadowComponent.computeInt(this.text, f + 5.0F, f6 + 4.0F, computeColor(color6, f3).getRGB());
    }

    private float computeFloat(float f, float f2, float f3) {
        float f4 = 1.0F - (float)Math.exp(-f3 * this.factor3);
        float f5 = f + (f2 - f) * f4;
        return Math.abs(f2 - f5) < 0.001F ? f2 : f5;
    }

    private List<List<BrutalityHelper>> computeList() {
        List list = new ArrayList();
        List list2 = new ArrayList();

        for (BrutalityHelper brutalityHelper : BrutalityHelper.computeList2()) {
            (brutalityHelper.isFlag() ? list : list2).add(brutalityHelper);
        }

        List list3 = new ArrayList();
        list3.add(list);
        list3.add(list2);
        return list3;
    }

    private String computeString(String str) {
        if (str != null && !str.isEmpty()) {
            str = str.replace('-', ' ');
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
        return "";
    }

    private static Color computeColor(Color color, float f) {
        int n = Math.max(4, Math.min(255, Math.round(color.getAlpha() * f)));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), n);
    }

    public DrawScriptsScreen(GuiScreen guiScreen, DrawScriptsScreen.ThemesMode themesMode) {
        this.guiScreen = guiScreen;
        this.themesMode = themesMode;
        ClassicFontRegistry.ensureInitialized();
        if (themesMode == DrawScriptsScreen.ThemesMode.SCRIPTS && Brutality.moduleManager != null) {
            for (Module module : ModuleManager.getEntries2()) {
                if (module instanceof ScriptModule) {
                    this.entries2.add(new DrawScriptsScreen.Entry2((ScriptModule)module));
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.process3();
        long now = System.currentTimeMillis();
        float f;
        if (this.flag) {
            f = clamp(1.0F - (float)(now - this.timestamp2) / 150.0F, 0.0F, 1.0F);
            if (f <= 0.0F) {
                mc.displayGuiScreen(this.guiScreen);
                return;
            }
        } else {
            f = clamp((float)(now - this.timestamp) / 150.0F, 0.0F, 1.0F);
        }

        this.factor4 = f;
        float f2 = computeFloat(f);
        int n = this.themesMode == DrawScriptsScreen.ThemesMode.THEMES ? Math.min(480, this.width - 36) : Math.min(360, this.width - 36);
        int n2 = this.themesMode == DrawScriptsScreen.ThemesMode.THEMES ? 360 : (this.themesMode == DrawScriptsScreen.ThemesMode.SCRIPTS ? 380 : 340);
        int n3 = Math.min(n2, this.height - 36);
        int n4 = (this.width - n) / 2;
        float f3 = (this.height - n3) / 2.0F + (1.0F - f2) * 12.0F;
        Gui.drawRect(0, 0, this.width, this.height, computeColor(new Color(0, 0, 0, 68), f2).getRGB());
        this.handleFloat(n4, f3, n, n3, f2);
        this.handleFloat2(n4, f3, n, n3, f2);
        LocationHelper.handleFloat2(n4, f3, n, n3, 8.0F, computeColor(color, f2));
        BindTextureHelper.handleDouble((double)n4, (double)f3, (double)n, 24.0);
        LocationHelper.handleFloat2(n4, f3, n, 32.0F, 8.0F, computeColor(color2, f2));
        BindTextureHelper.process();
        Color color3 = this.computeColor();
        String str = this.computeString();
        String str2 = String.valueOf(this.computeChar());
        float f4 = ClassicFontRegistry.drawStringWithShadowComponent4.computeFloat(str2);
        float f5 = ClassicFontRegistry.drawStringWithShadowComponent4.computeFloat2(str2);
        ClassicFontRegistry.drawStringWithShadowComponent4
            .computeInt(str2, n4 + 10.0F - f4 / 2.0F, f3 + 12.0F - f5 / 2.0F, computeColor(color3, f2).getRGB());
        ClassicFontRegistry.drawStringWithShadowComponent3.computeInt(str, n4 + 20.0F, f3 + 7.0F, computeColor(color5, f2).getRGB());
        this.entries.clear();
        String str3 = "< Back";
        float f6 = ClassicFontRegistry.drawStringWithShadowComponent2.computeFloat(str3);
        boolean flag = matchesFloat(mouseX, mouseY, n4 + n - f6 - 14.0F, f3, f6 + 10.0F, 24.0F);
        float f7 = this.computeFloat(this, flag ? 1.0F : 0.0F, 28.0F);
        ClassicFontRegistry.drawStringWithShadowComponent2
            .computeInt(str3, n4 + n - f6 - 9.0F - f7, f3 + 8.0F, computeColor(computeColor(color6, color3, f7), f2).getRGB());
        this.handleFloat2(n4 + n - f6 - 14.0F, f3, f6 + 14.0F, 24.0F, this::process, null);
        this.factor7 = f3 + 28.0F;
        this.factor8 = f3 + n3 - 8.0F;
        this.process4();
        float f8 = Math.max(0.0F, this.computeFloat2(n - 16) - (this.factor8 - this.factor7));
        this.factor6 = clamp(this.factor6, 0.0F, f8);
        this.factor5 = this.computeFloat(this.factor5, this.factor6, 26.0F);
        BindTextureHelper.handleDouble((double)(n4 + 6), (double)this.factor7, (double)(n - 12), (double)(this.factor8 - this.factor7));
        if (this.themesMode == DrawScriptsScreen.ThemesMode.THEMES) {
            this.handleFloat2(n4 + 8, this.factor7 + 1.0F - this.factor5, n - 16, mouseX, mouseY, f2);
        } else if (this.themesMode == DrawScriptsScreen.ThemesMode.SCRIPTS) {
            this.drawScripts(n4 + 8, this.factor7 + 1.0F - this.factor5, n - 16, mouseX, mouseY, f2);
        } else {
            this.handleFloat(n4 + 8, this.factor7 + 1.0F - this.factor5, n - 16, mouseX, mouseY, f2);
        }

        BindTextureHelper.process();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void handleFloat2(float f, float f2, float f3, float f4, Runnable task, Runnable task2) {
        DrawScriptsScreen.Entry entry = new DrawScriptsScreen.Entry();
        entry.x = f;
        entry.y = f2;
        entry.width = f3;
        entry.height = f4;
        entry.runnable = task;
        entry.runnable2 = task2;
        this.entries.add(entry);
    }

    private static Color computeColor(Color color, Color color2, float f) {
        f = clamp(f, 0.0F, 1.0F);
        return new Color(
            (int)(color.getRed() + (color2.getRed() - color.getRed()) * f),
            (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f),
            (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f)
        );
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.process();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    private void handleNumberSetting(NumberSetting numberSetting, int n, float f, float f2) {
        double d = clamp((n - f) / f2, 0.0F, 1.0F);
        double d2 = numberSetting.getMinValue() + d * (numberSetting.getMaxValue() - numberSetting.getMinValue());
        double increment = numberSetting.getIncrement();
        if (increment > 0.0) {
            d2 = Math.round(d2 / increment) * increment;
        }

        numberSetting.handleDouble2(d2);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (int i = this.entries.size() - 1; i >= 0; i--) {
            DrawScriptsScreen.Entry entry = this.entries.get(i);
            if (entry.matchesInt(mouseX, mouseY)) {
                if (mouseButton == 0 && entry.runnable != null) {
                    entry.runnable.run();
                } else if (mouseButton == 1 && entry.runnable2 != null) {
                    entry.runnable2.run();
                }

                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void handleFloat2(float f, float f2, float f3, float f4, float f5) {
        for (int i = 4; i >= 1; i--) {
            float f6 = i * 1.5F;
            LocationHelper.handleFloat2(
                f - f6, f2 - f6 + 2.0F, f3 + f6 * 2.0F, f4 + f6 * 2.0F, 8.0F + f6, computeColor(new Color(0, 0, 0, 18), f5)
            );
        }
    }

    private char computeChar() {
        if (this.themesMode == DrawScriptsScreen.ThemesMode.THEMES) {
            return 'H';
        }
        return (char)(this.themesMode == DrawScriptsScreen.ThemesMode.SCRIPTS ? 'K' : 'F');
    }

    private String computeString() {
        if (this.themesMode == DrawScriptsScreen.ThemesMode.THEMES) {
            return "Themes";
        }
        return this.themesMode == DrawScriptsScreen.ThemesMode.SCRIPTS ? "Scripts" : "Staff Tracker";
    }

    private void process3() {
        long now = System.nanoTime();
        if (this.timestamp3 != 0L) {
            this.factor3 = clamp((float)(now - this.timestamp3) / 1.0E9F, 0.004166667F, 0.05F);
        }

        this.timestamp3 = now;
    }

    private void handleFloat2(float f, float f2, int n, int n2, int n3, float f3) {
        BrutalityHelper brutalityHelper = this.computeBrutalityHelper();
        float f4 = (n - 12.0F) / 3.0F;
        String[] parts = new String[]{"Blended", "Classic"};
        var list = this.computeList();

        for (int i = 0; i < list.size(); i++) {
            var list2 = (List)list.get(i);
            if (!list2.isEmpty()) {
                if (f2 + 16.0F >= this.factor7 && f2 <= this.factor8) {
                    ClassicFontRegistry.drawStringWithShadowComponent2
                        .computeInt(parts[i].toUpperCase(), f + 1.0F, f2 + 3.0F, computeColor(color6, f3).getRGB());
                    float f5 = ClassicFontRegistry.drawStringWithShadowComponent2.computeFloat(parts[i].toUpperCase());
                    Gui.drawRect(
                        (int)(f + f5 + 8.0F),
                        (int)(f2 + 7.0F),
                        (int)(f + n),
                        (int)(f2 + 8.0F),
                        computeColor(new Color(255, 255, 255, 22), f3).getRGB()
                    );
                }

                float f6 = f2 + 16.0F;

                for (int j = 0; j < list2.size(); j++) {
                    BrutalityHelper brutalityHelper2 = (BrutalityHelper)list2.get(j);
                    float f7 = f + j % 3 * (f4 + 6.0F);
                    float f8 = f6 + j / 3 * 42.0F;
                    if (!(f8 + 36.0F < this.factor7 - 4.0F) && !(f8 > this.factor8 + 4.0F)) {
                        this.drawThemeCard(brutalityHelper2, f7, f8, f4, n2, n3, f3, brutalityHelper.getName().equalsIgnoreCase(brutalityHelper2.getName()));
                    }
                }

                f2 = f6 + ((list2.size() + 3 - 1) / 3 * 42.0F + 2.0F);
            }
        }
    }

    private void process4() {
        if (this.themesMode == DrawScriptsScreen.ThemesMode.SCRIPTS) {
            for (DrawScriptsScreen.Entry2 entry2 : this.entries2) {
                entry2.factor = this.computeFloat(entry2.factor, entry2.flag ? 1.0F : 0.0F, 22.0F);
            }
        }
    }

    private void process2() {
        // VENDOR-DISABLED: StaffTrackerService.handleConsumer(...) remote staff tracker fetch
        this.text = "Unavailable offline";
    }

    private void drawScripts(float f, float f2, int n, int n2, int n3, float f3) {
        if (this.entries2.isEmpty()) {
            ClassicFontRegistry.drawStringWithShadowComponent
                .computeInt("No scripts loaded", f + 5.0F, f2 + 5.0F, computeColor(color6, f3).getRGB());
        } else {
            float f4 = f2;

            for (DrawScriptsScreen.Entry2 entry2 : this.entries2) {
                float f5 = 18.0F;
                float f6 = entry2.computeFloat() * entry2.factor;
                if (f4 + f5 + f6 >= this.factor7 && f4 <= this.factor8) {
                    boolean flag = matchesFloat(n2, n3, f, f4, n, f5);
                    entry2.factor2 = this.computeFloat(entry2.factor2, flag ? 1.0F : 0.0F, 30.0F);
                    entry2.factor3 = this.computeFloat(entry2.factor3, entry2.scriptModule.checkState44() ? 1.0F : 0.0F, 24.0F);
                    Color color = computeColor(color3, this.computeColor(), entry2.factor3 * 0.22F + entry2.factor2 * 0.08F);
                    LocationHelper.handleFloat2(f, f4, n, f5, 4.0F, computeColor(color, f3));
                    ClassicFontRegistry.drawStringWithShadowComponent
                        .computeInt(
                            entry2.scriptModule.getName(),
                            f + 6.0F + entry2.factor2 * 0.75F,
                            f4 + 5.0F,
                            computeColor(computeColor(color6, color5, entry2.factor3), f3).getRGB()
                        );
                    String str = "...";
                    ClassicFontRegistry.drawStringWithShadowComponent
                        .computeInt(
                            str, f + n - 12.0F, f4 + 3.0F, computeColor(computeColor(color6, this.computeColor(), entry2.factor), f3).getRGB()
                        );
                    this.handleFloat(f, f4, n, f5, entry2.scriptModule::process28, () -> entry2.flag = !entry2.flag);
                    if (entry2.factor > 0.01F) {
                        float f7 = f4 + f5;
                        float f8 = f7 + f6;
                        BindTextureHelper.handleDouble(
                            (double)f,
                            (double)Math.max(f7, this.factor7),
                            (double)n,
                            (double)Math.max(0.0F, Math.min(f8, this.factor8) - Math.max(f7, this.factor7))
                        );

                        for (Setting setting : entry2.entries) {
                            f7 = this.drawScriptSetting(setting, f, f7, n, n2, n3, f3 * entry2.factor);
                        }

                        BindTextureHelper.handleDouble(0.0, (double)this.factor7, (double)this.width, (double)(this.factor8 - this.factor7));
                    }
                }

                f4 += f5 + f6 + 5.0F;
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private BrutalityHelper computeBrutalityHelper() {
        return ClickGUI.instance != null && ClickGUI.instance.computeBrutalityHelper() != null
            ? ClickGUI.instance.computeBrutalityHelper()
            : BrutalityHelper.getInstance();
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    private Color computeColor() {
        BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
        float f = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 700.0));
        return brutalityHelper == null ? new Color(120, 110, 255) : brutalityHelper.computeColor(f);
    }

    private void handleFloat(float f, float f2, float f3, float f4, Runnable task, Runnable task2) {
        if (!(f2 + f4 < this.factor7) && !(f2 > this.factor8)) {
            this.handleFloat2(f, f2, f3, f4, task, task2);
        }
    }

    private float computeFloat(Object obj, float f, float f2) {
        float orDefault = this.lookupMap.getOrDefault(obj, 0.0F);
        orDefault = this.computeFloat(orDefault, f, f2);
        this.lookupMap.put(obj, orDefault);
        return orDefault;
    }

    private float computeFloat2(int n) {
        if (this.themesMode == DrawScriptsScreen.ThemesMode.THEMES) {
            return this.computeFloat(n);
        }
        if (this.themesMode != DrawScriptsScreen.ThemesMode.SCRIPTS) {
            return 21.0F + Math.max(17.0F, this.entries3.size() * 21.0F);
        }
        float f = 0.0F;

        for (DrawScriptsScreen.Entry2 entry2 : this.entries2) {
            f += 23.0F + entry2.computeFloat() * entry2.factor;
        }

        return f;
    }

    private void process() {
        if (!this.flag) {
            this.flag = true;
            this.timestamp2 = System.currentTimeMillis() - (long)((1.0F - this.factor4) * 150.0F);
        }
    }

    public static final class Entry {
        public float x;
        public float y;
        public float width;
        public float height;
        public Runnable runnable;
        public Runnable runnable2;

        public boolean matchesInt(int n, int n2) {
            return DrawScriptsScreen.matchesFloat(n, n2, this.x, this.y, this.width, this.height);
        }
    }

    public final class Entry2 {
        public final ScriptModule scriptModule;
        public final List<Setting> entries = new ArrayList<>();
        public boolean flag;
        public float factor;
        public float factor2;
        public float factor3;

        public float computeFloat() {
            float f = 0.0F;

            for (Setting setting : this.entries) {
                f += setting instanceof NumberSetting ? 18.0F : 14.0F;
            }

            return f;
        }

        public Entry2(ScriptModule scriptModule) {
            this.scriptModule = scriptModule;
            this.factor3 = scriptModule.checkState44() ? 1.0F : 0.0F;
            if (Brutality.settingsManager != null) {
                var settings = Brutality.settingsManager.computeList(scriptModule);
                if (settings != null) {
                    for (Setting setting : settings) {
                        this.handleSetting(setting, this.entries);
                    }
                }
            }
        }

        private void handleSetting(Setting setting, List<Setting> settings) {
            if (setting != null && setting.isVisible()) {
                if (setting instanceof KeybindListSetting) {
                    for (Setting setting2 : ((KeybindListSetting)setting).getEntries()) {
                        this.handleSetting(setting2, settings);
                    }
                } else {
                    settings.add(setting);
                }
            }
        }
    }

    public static enum ThemesMode {
        THEMES,
        SCRIPTS,
        STAFF;

        private static DrawScriptsScreen.ThemesMode[] computeThemesModeArray() {
            return new DrawScriptsScreen.ThemesMode[]{THEMES, SCRIPTS, STAFF};
        }
    }
}
