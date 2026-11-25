package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.visual.arraylist.ArraylistHandler;
import org.brutality.module.impl.visual.notifications.AutoGrinderHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.animations.ForwardsType;
import org.brutality.utils.animations.impl.ImplAnimation;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.IsChoiceHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ArrayList extends Module {
    private static ArrayList instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<ArrayList.Entry3> entries = new java.util.ArrayList<>();
    private final Map<String, ArrayList.Entry2> lookupMap = new HashMap<>();
    private final List<ArrayList.Entry> entries2 = new java.util.ArrayList<>();
    private final Map<String, String> lookupMap2 = new HashMap<>();
    private final Map<String, Float> lookupMap3 = new HashMap<>();
    private final Map<String, String> lookupMap4 = new HashMap<>();
    private boolean flag = false;
    private final ModeSetting novolineSetting = new ModeSetting("HUD Style", this, "Brutality", Arrays.asList("Brutality", "PrimeCheats", "Modern", "Novoline"));
    private final ModeSetting w = ModuleSpecificHelper.computeModeSetting(this).computeModeSetting(() -> !this.novolineSetting.matchesString2("Modern"));
    private final ModeSetting themeSetting = new ModeSetting("Color Mode", this, "Gradient", Arrays.asList("Static", "Rainbow", "Gradient", "Chroma", "Theme"))
        .computeModeSetting(() -> this.checkState2());
    private final ColorSetting color1Setting = new ColorSetting("Color 1", this, 0.0F, 1.0F, 1.0F)
        .computeColorSetting(() -> this.checkState2() && !ModuleSpecificHelper.matchesModeSetting(this.w) && !this.themeSetting.matchesString2("Theme"));
    private final ColorSetting color2Setting = new ColorSetting("Color 2", this, 0.6F, 1.0F, 1.0F)
        .computeColorSetting(() -> this.checkState2() && !ModuleSpecificHelper.matchesModeSetting(this.w) && !this.themeSetting.matchesString2("Theme"));
    private final ModeSetting quanProSetting = IsChoiceHelper.computeModeSetting("Prime Font", this, "Quan Pro")
        .computeModeSetting(() -> this.novolineSetting.matchesString2("PrimeCheats"));
    private final NumberSetting primeBGAlphaSetting = new NumberSetting("Prime BG Alpha", this, 50.0, 0.0, 100.0, 1.0)
        .computeNumberSetting(() -> this.novolineSetting.matchesString2("PrimeCheats"));
    private AutoGrinderHandler autoGrinderHandler;
    private final ModeSetting downSetting = new ModeSetting("Direction", this, "Down", Arrays.asList("Up", "Down")).computeModeSetting(this::z);
    private final NumberSetting waveSpeedSetting = new NumberSetting("Wave Speed", this, 3.0, 0.1, 10.0, 0.1).computeNumberSetting(() -> this.checkState2());
    private final ModeSetting scaleCenterSetting = new ModeSetting("Animation Mode", this, "Scale Right", Arrays.asList("Scale Right", "Scale Center"))
        .computeModeSetting(() -> this.novolineSetting.matchesString2("Brutality"));
    private final NumberSetting animationSpeedSetting = new NumberSetting("Animation Speed", this, 150.0, 0.0, 2000.0, 10.0)
        .computeNumberSetting(() -> this.novolineSetting.matchesString2("Brutality"));
    private final ModeSetting fontSetting = IsChoiceHelper.computeModeSetting("Font", this)
        .computeModeSetting(() -> !this.novolineSetting.matchesString2("Modern") && !this.novolineSetting.matchesString2("PrimeCheats"));
    private final NumberSetting fontSizeSetting = new NumberSetting("Font Size", this, 20.5, 8.0, 40.0, 0.5)
        .computeNumberSetting(() -> !this.novolineSetting.matchesString2("Modern"));
    private final BooleanSetting customFontSetting = new BooleanSetting("Custom Font", this, false)
        .computeBooleanSetting(() -> this.novolineSetting.matchesString2("Brutality") && !ModuleSpecificHelper.matchesModeSetting(this.w));
    private final BooleanSetting lowercaseSetting = new BooleanSetting("Lowercase", this, true).computeBooleanSetting(() -> this.checkState2());
    private final BooleanSetting showSuffixSetting = new BooleanSetting("Show Suffix", this, true).computeBooleanSetting(() -> this.checkState2());
    private final ModeSetting noneSetting = new ModeSetting(
            "Suffix Mode", this, "Dash", Arrays.asList("Dash", "Pipe", "Equals", "Bracket", "Parenthesis", "None")
        )
        .computeModeSetting(() -> this.checkState2());
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 2.0, 0.1).computeNumberSetting(() -> this.checkState2());
    private final NumberSetting spacingSetting = new NumberSetting("Spacing", this, 1.0, 0.0, 10.0, 0.5).computeNumberSetting(() -> this.checkState2());
    private final NumberSetting xOffsetSetting = new NumberSetting("X Offset", this, 1.0, 0.0, 50.0, 1.0).computeNumberSetting(() -> this.checkState2());
    private final NumberSetting yOffsetSetting = new NumberSetting("Y Offset", this, 1.0, 0.0, 50.0, 1.0).computeNumberSetting(() -> this.checkState2());
    private final BooleanSetting backgroundSetting = new BooleanSetting("Background", this, true).computeBooleanSetting(() -> this.checkState2());
    private final NumberSetting backgroundAlphaSetting = new NumberSetting("Background Alpha", this, 150.0, 0.0, 255.0, 5.0)
        .computeNumberSetting(() -> this.checkState2());
    private final BooleanSetting outlineSetting = new BooleanSetting("Outline", this, true).computeBooleanSetting(() -> this.checkState2());
    private final NumberSetting outlineWidthSetting = new NumberSetting("Outline Width", this, 2.0, 0.5, 5.0, 0.5)
        .computeNumberSetting(() -> this.novolineSetting.matchesString2("Brutality"));
    private final BooleanSetting textGlowSetting = new BooleanSetting("Text Glow", this, false)
        .computeBooleanSetting(() -> this.novolineSetting.matchesString2("Brutality"));
    private final NumberSetting glowIntensitySetting = new NumberSetting("Glow Intensity", this, 2.0, 1.0, 4.0, 1.0)
        .computeNumberSetting(() -> this.novolineSetting.matchesString2("Brutality") && this.textGlowSetting.isEnabled());
    private final NumberSetting glowAlphaSetting = new NumberSetting("Glow Alpha", this, 60.0, 10.0, 150.0, 5.0)
        .computeNumberSetting(() -> this.novolineSetting.matchesString2("Brutality") && this.textGlowSetting.isEnabled());
    private final ModeSetting rightSetting = new ModeSetting("Horizontal Position", this, "Right", Arrays.asList("Left", "Right"))
        .computeModeSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final ModeSetting bottomSetting = new ModeSetting("Vertical Position", this, "Top", Arrays.asList("Top", "Bottom"))
        .computeModeSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final NumberSetting xPaddingSetting = new NumberSetting("X Padding", this, 2.0, 0.0, 255.0, 1.0)
        .computeNumberSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final NumberSetting yPaddingSetting = new NumberSetting("Y Padding", this, 2.0, 0.0, 255.0, 1.0)
        .computeNumberSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final NumberSetting backgroundOpacitySetting = new NumberSetting("Background Opacity", this, 25.0, 0.0, 100.0, 1.0)
        .computeNumberSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final BooleanSetting accentBarSetting = new BooleanSetting("Accent Bar", this, true)
        .computeBooleanSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final ModeSetting outlineSetting2 = new ModeSetting("Accent Bar Mode", this, "Side", Arrays.asList("Side", "Top", "Outline"))
        .computeModeSetting(() -> this.novolineSetting.matchesString2("Modern") && this.accentBarSetting.isEnabled());
    private final ModeSetting typefaceSetting = IsChoiceHelper.computeModeSetting("Typeface", this)
        .computeModeSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final BooleanSetting textShadowSetting = new BooleanSetting("Text Shadow", this, true)
        .computeBooleanSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final ModeSetting dashSetting = new ModeSetting("Suffix Style", this, "Simple", Arrays.asList("None", "Simple", "Bracket", "Dash"))
        .computeModeSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final BooleanSetting lowercaseSetting2 = new BooleanSetting("Lowercase", this, false)
        .computeBooleanSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final BooleanSetting smoothAnimationsSetting = new BooleanSetting("Smooth Animations", this, true)
        .computeBooleanSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final NumberSetting rowSpacingSetting = new NumberSetting("Row Spacing", this, 0.0, 0.0, 10.0, 1.0)
        .computeNumberSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final ModeSetting atmosphereSetting = new ModeSetting(
            "Color Theme",
            this,
            "Warm",
            Arrays.asList(
                "Rainbow",
                "Chroma",
                "Astolfo",
                "Warm",
                "Flawless",
                "Violet",
                "Cosmic",
                "Watery",
                "Fiery",
                "Bloody",
                "Pleasant",
                "Lightweight",
                "Winter",
                "Shadow",
                "Purple",
                "Atmosphere"
            )
        )
        .computeModeSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private final NumberSetting colorSpeedSetting = new NumberSetting("Color Speed", this, 1.0, 0.5, 1.5, 0.1)
        .computeNumberSetting(() -> this.novolineSetting.matchesString2("Modern"));
    private ArraylistHandler arraylistHandler;
    private final BooleanSetting excludeCombatSetting = new BooleanSetting("Exclude Combat", this, false);
    private final BooleanSetting excludeMovementSetting = new BooleanSetting("Exclude Movement", this, false);
    private final BooleanSetting excludePlayerSetting = new BooleanSetting("Exclude Player", this, false);
    private final BooleanSetting excludeVisualSetting = new BooleanSetting("Exclude Visual", this, false);
    private final BooleanSetting excludeMiscellaneousSetting = new BooleanSetting("Exclude Miscellaneous", this, false);
    private final BooleanSetting excludeExploitSetting = new BooleanSetting("Exclude Exploit", this, false);
    private final BooleanSetting excludeScriptsSetting = new BooleanSetting("Exclude Scripts", this, false);
    private final BooleanSetting excludePitSetting = new BooleanSetting("Exclude Pit", this, false);
    private final KeybindListSetting categoryExclusionSetting = new KeybindListSetting(
        "Category Exclusion",
        this,
        this.excludeCombatSetting,
        this.excludeMovementSetting,
        this.excludePlayerSetting,
        this.excludeVisualSetting,
        this.excludeMiscellaneousSetting,
        this.excludeExploitSetting,
        this.excludeScriptsSetting,
        this.excludePitSetting
    );
    private CustomFontRenderer drawStringWithShadowComponent;
    private CustomFontRenderer drawStringWithShadowComponent2;
    private Color color;
    private Color color2;
    private Color color3;
    private int count = 0;
    private String text = "";
    private String text2 = "";
    private boolean flag2 = true;
    private boolean flag3 = true;
    private double amount = 1.0;
    private String text3 = "";
    private double amount2 = 0.0;
    private boolean flag4 = false;
    private final List<Module> entries3 = new java.util.ArrayList<>();
    private final Map<Module, ImplAnimation> lookupMap5 = new HashMap<>();
    private final Map<Module, Float> lookupMap6 = new HashMap<>();
    private final Map<Module, Float> lookupMap7 = new HashMap<>();
    private final Map<Module, Float> lookupMap8 = new HashMap<>();
    private final Map<Module, String> b = new HashMap<>();
    private final Map<Module, Integer> lookupMap9 = new HashMap<>();
    private final List<int[]> entries4 = new java.util.ArrayList<>();
    private final List<int[]> entries5 = new java.util.ArrayList<>();
    private boolean flag5;
    private int count2;
    private int count3;
    private int count4;
    private int count5;

    public static Color[] computeColorArray(Module module) {
        ArrayList list = instance;
        if (list == null) {
            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
            return new Color[]{brutalityHelper.getColor(), brutalityHelper.getColor2()};
        }
        if (list.novolineSetting.matchesString2("PrimeCheats")) {
            Color color = new Color(list.autoGrinderHandler.computeInt2(module), true);
            return new Color[]{color, color};
        }
        if (list.novolineSetting.matchesString2("Modern")) {
            if (!list.atmosphereSetting.matchesString2("Rainbow")
                && !list.atmosphereSetting.matchesString2("Chroma")
                && !list.atmosphereSetting.matchesString2("Astolfo")) {
                return list.computeColorArray();
            }
            long now = System.currentTimeMillis();
            return new Color[]{list.computeColor(now, 0.0F), list.computeColor(now, 0.25F)};
        }
        list.process12();
        if (ModuleSpecificHelper.matchesModeSetting(list.w) || list.themeSetting.matchesString2("Theme")) {
            BrutalityHelper brutalityHelper2 = ModuleSpecificHelper.computeBrutalityHelper();
            return new Color[]{brutalityHelper2.getColor(), brutalityHelper2.getColor2()};
        }
        if (list.themeSetting.matchesString2("Gradient")) {
            return new Color[]{list.color, list.color2};
        }
        return list.themeSetting.matchesString2("Static")
            ? new Color[]{list.color3, list.color3}
            : new Color[]{new Color(list.computeInt(0L), true), new Color(list.computeInt(500L), true)};
    }

    private boolean checkState() {
        return ModuleSpecificHelper.matchesModeSetting(this.w) || this.customFontSetting.isEnabled();
    }

    private void handleString2(String str, float f, float f2, float f3, int n) {
        if (this.checkState() && this.drawStringWithShadowComponent != null) {
            int n2 = str.indexOf("§7");
            if (n2 != -1) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(f, f2, 0.0F);
                GlStateManager.scale(f3, f3, 1.0F);
                String str2 = str.substring(0, n2);
                String str3 = str.substring(n2 + 2);
                this.drawStringWithShadowComponent.drawStringWithShadow(str2, 0.0F, 0.0F, n);
                float textWidth = this.drawStringWithShadowComponent.getStringWidth(str2);
                this.drawStringWithShadowComponent.drawStringWithShadow(str3, textWidth, 0.0F, -8355712);
                GlStateManager.popMatrix();
            } else {
                this.handleString(str, f, f2, f3, n);
            }
        } else {
            this.handleString(str, f, f2, f3, n);
        }
    }

    private static Color computeColor(float f, Color color, Color color2) {
        float f2 = Math.max(0.0F, Math.min(1.0F, f));
        return new Color(
            (int)(color.getRed() + f2 * (color2.getRed() - color.getRed())),
            (int)(color.getGreen() + f2 * (color2.getGreen() - color.getGreen())),
            (int)(color.getBlue() + f2 * (color2.getBlue() - color.getBlue()))
        );
    }

    private void handleString(String str, float f, float f2, float f3, int n) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(f, f2, 0.0F);
        GlStateManager.scale(f3, f3, 1.0F);
        if (this.textGlowSetting.isEnabled()) {
            int n2 = (int)this.glowAlphaSetting.computeDouble();
            int n3 = (int)this.glowIntensitySetting.computeDouble();
            int n4 = n2 << 24 | n & 16777215;

            for (int i = ~(n3 - 1); i <= n3; i++) {
                for (int j = -n3; j <= n3; j++) {
                    if (i != 0 || j != 0) {
                        if (this.checkState() && this.drawStringWithShadowComponent != null) {
                            this.drawStringWithShadowComponent.drawString(str, i, j, n4, false);
                        } else {
                            this.mc.fontRendererObj.drawString(str, i, j, n4, false);
                        }
                    }
                }
            }
        }

        if (this.checkState() && this.drawStringWithShadowComponent != null) {
            this.drawStringWithShadowComponent.drawStringWithShadow(str, 0.0F, 0.0F, n);
        } else {
            this.mc.fontRendererObj.drawStringWithShadow(str, 0.0F, 0.0F, n);
        }

        GlStateManager.popMatrix();
    }

    private int computeInt(Module module) {
        Integer integer = this.lookupMap9.get(module);
        if (integer != null) {
            return integer;
        }
        String orDefault = this.b.getOrDefault(module, this.computeString3(module));
        int n = this.computeInt(orDefault);
        this.lookupMap9.put(module, n);
        return n;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.entries.clear();
        this.lookupMap2.clear();
        this.lookupMap3.clear();
        this.process4();
    }

    private String computeString(double d) {
        return d == (long)d ? String.valueOf((long)d) : String.valueOf(d);
    }

    private int computeInt3() {
        CustomFontRenderer drawStringWithShadowComponent = this.computeDrawStringWithShadowComponent();
        return drawStringWithShadowComponent == null ? this.mc.fontRendererObj.FONT_HEIGHT : drawStringWithShadowComponent.getHeight();
    }

    private void process8() {
        this.count++;
        if (this.novolineSetting.matchesString2("PrimeCheats")) {
            this.autoGrinderHandler.tick();
        } else if (this.novolineSetting.matchesString2("Modern")) {
            this.process10();
        } else {
            this.process15();
            if (this.count == 1 || this.count % 5 == 0 || this.flag) {
                this.process5();
            }

            this.process3();
            if (this.flag) {
                this.process11();
                this.flag = false;
            }
        }
    }

    private String computeString4(Module module) {
        if (Brutality.settingsManager == null) {
            return "";
        }
        List<Setting> settings = Brutality.settingsManager.computeList(module);

        for (Setting setting : settings) {
            if (setting instanceof ModeSetting) {
                return this.computeString3(((ModeSetting)setting).getText());
            }
        }

        for (Setting setting2 : ((Iterable<Setting>)(Object)(settings))) {
            if (setting2 instanceof NumberSetting) {
                double d = ((NumberSetting)setting2).computeDouble();
                String str;
                if (d == (long)d) {
                    str = String.valueOf((long)d);
                } else {
                    str = String.format("%.1f", d);
                }

                return this.computeString3(str);
            }
        }

        return "";
    }

    private String computeString2() {
        String text = this.noneSetting.getText();
        switch (text) {
            case "Bracket":
                return "]";
            case "Parenthesis":
                return ")";
            default:
                return "";
        }
    }

    private boolean matchesModule(Module module) {
        boolean flag = ModuleManager.matchesModule(module);
        if (module.getCategory() != Category.HIDDEN && !flag) {
            return switch (module.getCategory()) {
                case COMBAT -> this.excludeCombatSetting.isEnabled();
                case MOVEMENT -> this.excludeMovementSetting.isEnabled();
                case PLAYER -> this.excludePlayerSetting.isEnabled();
                case VISUAL -> this.excludeVisualSetting.isEnabled();
                case MISCELLANEOUS -> this.excludeMiscellaneousSetting.isEnabled();
                case EXPLOIT -> this.excludeExploitSetting.isEnabled();
                case PIT -> this.excludePitSetting.isEnabled();
                case SCRIPTS -> this.excludeScriptsSetting.isEnabled();
                default -> false;
            };
        }
        return true;
    }

    private void process10() {
        this.entries3.clear();

        for (Module module : ModuleManager.getEntries2()) {
            if (!this.matchesModule(module)) {
                this.entries3.add(module);
            }
        }

        for (Module module2 : this.entries3) {
            String str = this.computeString3(module2);
            this.b.put(module2, str);
            this.lookupMap9.put(module2, this.computeInt(str));
            ImplAnimation implAnimation = this.lookupMap5.get(module2);
            if (implAnimation == null) {
                implAnimation = new ImplAnimation(200, 1.0);
                implAnimation.computeIsDoneHelper(module2.checkState44() ? ForwardsType.FORWARDS : ForwardsType.BACKWARDS);
                this.lookupMap5.put(module2, implAnimation);
                this.lookupMap6.put(module2, module2.checkState44() ? this.computeInt(module2) : -2.0F);
                this.lookupMap7.put(module2, 0.0F);
                this.lookupMap8.put(module2, 0.0F);
            }

            implAnimation.computeIsDoneHelper(module2.checkState44() ? ForwardsType.FORWARDS : ForwardsType.BACKWARDS);
            implAnimation.setDuration(this.smoothAnimationsSetting.isEnabled() ? 200 : 0);
        }

        this.lookupMap5.keySet().removeIf(module3 -> !this.entries3.contains(module3));
        this.lookupMap6.keySet().removeIf(module4 -> !this.entries3.contains(module4));
        this.lookupMap7.keySet().removeIf(module5 -> !this.entries3.contains(module5));
        this.lookupMap8.keySet().removeIf(module6 -> !this.entries3.contains(module6));
        this.b.keySet().removeIf(module7 -> !this.entries3.contains(module7));
        this.lookupMap9.keySet().removeIf(module8 -> !this.entries3.contains(module8));
        this.entries3.sort(Comparator.<Module>comparingInt(this::computeInt).reversed());
    }

    private static void handleInt(int n, int n2, int n3, int n4, int n5, int n6) {
        float f = (n5 >> 16 & 0xFF) / 255.0F;
        float f2 = ((0xFF | ~(n5 >> 8)) - ~(n5 >> 8)) / 255.0F;
        float f3 = (n5 & 0xFF) / 255.0F;
        float f4 = ((0xFF | ~(n5 >> 24)) - ~(n5 >> 24)) / 255.0F;
        float f5 = ((0xFF | ~(n6 >> 16)) - ~(n6 >> 16)) / 255.0F;
        float f6 = (n6 >> 8 & 0xFF) / 255.0F;
        float f7 = ((0xFF | ~n6) - ~n6) / 255.0F;
        float f8 = (n6 >> 24 & 0xFF) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(f, f2, f3, f4);
        GL11.glVertex2f(n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glColor4f(f5, f6, f7, f8);
        GL11.glVertex2f(n, n4);
        GL11.glVertex2f(n3, n4);
        GL11.glEnd();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    private String computeString(Module module) {
        if (!this.showSuffixSetting.isEnabled()) {
            return "";
        }
        if (module.isSuffix2() && module.getSuffix() != null) {
            return module.getSuffix();
        }
        String str = this.computeString4(module);
        return str == null ? "" : str;
    }

    private void process15() {
        this.process12();
        this.process6();
        boolean flag = false;
        if (!this.noneSetting.getText().equals(this.text2)) {
            flag = true;
        }

        if (this.showSuffixSetting.isEnabled() != this.flag2) {
            flag = true;
        }

        if (this.lowercaseSetting.isEnabled() != this.flag3) {
            flag = true;
        }

        if (Double.compare(this.scaleSetting.computeDouble(), this.amount) != 0) {
            flag = true;
        }

        if (!this.fontSetting.getText().equals(this.text3)) {
            flag = true;
        }

        if (Double.compare(this.fontSizeSetting.computeDouble(), this.amount2) != 0) {
            flag = true;
        }

        if (this.customFontSetting.isEnabled() != this.flag4) {
            flag = true;
        }

        if (flag) {
            this.lookupMap2.clear();
            this.lookupMap3.clear();
            this.lookupMap4.clear();
            this.flag = true;
        }

        this.text2 = this.noneSetting.getText();
        this.flag2 = this.showSuffixSetting.isEnabled();
        this.flag3 = this.lowercaseSetting.isEnabled();
        this.amount = this.scaleSetting.computeDouble();
        this.text3 = this.fontSetting.getText();
        this.amount2 = this.fontSizeSetting.computeDouble();
        this.flag4 = this.customFontSetting.isEnabled();
    }

    private void handleString(String str, String str2, String str3, String[] parts) {
        ArrayList.Entry entry = new ArrayList.Entry();
        entry.text = str;
        entry.alias = str2.isEmpty() ? null : str2;
        entry.text2 = str3;
        entry.stringArray2 = parts;
        entry.type = "strings";
        this.entries2.add(entry);
        this.handleEntry(entry);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.mc.thePlayer != null && this.mc.theWorld != null) {
                if (event instanceof EventUpdate) {
                    this.process8();
                } else if (event instanceof EventRender2D) {
                    this.process7();
                } else if (event instanceof SetShaderTypeEvent) {
                    this.onSetShaderTypeEvent2((SetShaderTypeEvent)event);
                }
            }
        }
    }

    private void updateAnimations() {
        long now = System.currentTimeMillis();

        for (ArrayList.Entry3 entry3 : this.entries) {
            if (entry3.flag) {
                long timestamp = entry3.timestamp;
                float f = (float)(now - timestamp) / (float)this.animationSpeedSetting.computeDouble();
                if (f >= 1.0F) {
                    f = 1.0F;
                    entry3.flag = false;
                }

                entry3.factor = f;
                float f2 = this.computeFloat(f) * this.computeInt2();
                float f3 = this.computeFloat(f);
                if (entry3.flag2) {
                    entry3.factor2 = this.computeInt2() - f2;
                    entry3.scale = 1.0F - f3;
                } else {
                    entry3.factor2 = f2;
                    entry3.scale = f3;
                }
            }
        }
    }

    private String computeString(String str) {
        String str2 = this.lookupMap2.get(str);
        if (str2 != null) {
            return str2;
        }
        String str3 = this.computeString2(str);
        if (this.lowercaseSetting.isEnabled()) {
            str3 = str3.toLowerCase();
        }

        this.lookupMap2.put(str, str3);
        return str3;
    }

    private float computeFloat(float f) {
        return f < 0.5F ? 2.0F * f * f : -1.0F + (4.0F - 2.0F * f) * f;
    }

    private String computeString(Module module, String str) {
        for (Setting setting : Brutality.settingsManager.computeList(module)) {
            if (setting.getName().equalsIgnoreCase(str)) {
                if (setting instanceof NumberSetting) {
                    double d = ((NumberSetting)setting).computeDouble();
                    return this.computeString(d);
                }

                if (setting instanceof ModeSetting) {
                    return ((ModeSetting)setting).getText();
                }

                if (setting instanceof BooleanSetting) {
                    return String.valueOf(((BooleanSetting)setting).isEnabled());
                }
            }
        }

        return "";
    }

    private void handleString(String str, String str2, String str3, String str4, String[] parts) {
        ArrayList.Entry entry = new ArrayList.Entry();
        entry.text = str;
        entry.alias = str2.isEmpty() ? null : str2;
        entry.text2 = str3;
        entry.displayString = str4;
        entry.stringArray = parts;
        entry.type = "conditional";
        this.entries2.add(entry);
        this.handleEntry(entry);
    }

    private int computeInt(long l) {
        String text = this.themeSetting.getText();
        switch (text) {
            case "Static":
                return this.color3.getRGB();
            case "Rainbow":
                return this.computeInt((float)this.waveSpeedSetting.computeDouble(), 1.0F, 1.0F, l);
            case "Gradient":
                if (this.color != null && this.color2 != null) {
                    double d = this.computeDouble((float)this.waveSpeedSetting.computeDouble(), l);
                    return this.computeColor(this.color, this.color2, d).getRGB();
                }

                this.color3 = this.color1Setting.computeColor();
                this.color = this.color3;
                this.color2 = this.color2Setting.computeColor();
                return 16777215;
            case "Chroma":
                float f = (float)(System.currentTimeMillis() % 2000L) / 2000.0F;
                f = (f + (float)l / 10000.0F) % 1.0F;
                return Color.getHSBColor(f, 1.0F, 1.0F).getRGB();
            case "Theme":
                BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
                if (brutalityHelper != null) {
                    double d2 = this.computeDouble((float)this.waveSpeedSetting.computeDouble(), l);
                    return this.computeColor(brutalityHelper.getColor(), brutalityHelper.getColor2(), d2).getRGB();
                }

                return 16777215;
            default:
                return 16777215;
        }
    }

    private void process14() {
        float f = 0.0F;
        int n = this.computeInt();

        for (Module module : this.entries3) {
            ImplAnimation implAnimation = this.lookupMap5.get(module);
            if (implAnimation != null) {
                float orDefault = this.lookupMap6.getOrDefault(module, -2.0F);
                float f2 = this.computeInt(module);
                float f3 = module.checkState44() ? f2 : -2.0F;
                float f4 = f3 - orDefault;
                float f5 = orDefault + f4 * 0.15F;
                if (Math.abs(f4) < 0.1F) {
                    f5 = f3;
                }

                orDefault = Math.max(-2.0F, Math.min(f2, f5));
                this.lookupMap6.put(module, orDefault);
                if (module.checkState44() || (float)implAnimation.computeDouble() > 0.01F) {
                    this.lookupMap8.put(module, f);
                    f += n;
                }

                float orDefault2 = this.lookupMap7.getOrDefault(module, 0.0F);
                float orDefault3 = this.lookupMap8.getOrDefault(module, 0.0F);
                float f6 = orDefault3 - orDefault2;
                float f7 = orDefault2 + f6 * 0.15F;
                if (Math.abs(f6) < 0.1F) {
                    f7 = orDefault3;
                }

                this.lookupMap7.put(module, f7);
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.entries2.isEmpty()) {
            this.process13();
        }

        if (this.novolineSetting.matchesString2("Brutality")) {
            this.process3();
            this.process11();
        } else if (this.novolineSetting.matchesString2("Modern")) {
            this.process10();
        }
    }

    private void handleInt(int n, float f, float f2, float f3, float f4, int n2, float f5) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        float f6 = (float)this.outlineWidthSetting.computeDouble();
        this.handleFloat(f - f6, f2, f, f4, n2);
        ArrayList.Entry3 entry3 = null;

        for (int i = n + 1; i < this.entries.size(); i++) {
            ArrayList.Entry3 entry32 = this.entries.get(i);
            if (entry32.visible || entry32.flag && entry32.scale > 0.05F) {
                entry3 = entry32;
                break;
            }
        }

        boolean flag = entry3 == null;
        if (flag) {
            this.handleFloat(f - f6, f4 - f6, f3, f4, n2);
        } else if (entry3 != null) {
            String str = this.computeString(entry3.name);
            float f7 = this.computeFloat(str, entry3.name);
            float f8 = entry3.scale * (float)this.scaleSetting.computeDouble();
            float f9 = f7 * f8;
            float f10 = this.computeFloat(f5, f7, f9, f8);
            float f11 = f10 - 2.0F;
            if (f < f11) {
                this.handleFloat(f - f6, f4 - f6, f11, f4, n2);
            } else {
                this.handleFloat(f - f6, f4 - f6, f, f4, n2);
            }
        }

        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private String computeString3(String str) {
        String str2 = this.computeString2();
        return !str2.isEmpty() ? str + str2 : str;
    }

    private void handleFloat(float f, float f2, float f3, float f4, int n) {
        float f5 = ((0xFF | ~(n >> 24)) - ~(n >> 24)) / 255.0F;
        float f6 = (n >> 16 & 0xFF) / 255.0F;
        float f7 = ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F;
        float f8 = ((0xFF | ~n) - ~n) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(f6, f7, f8, f5);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(f, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f, f2, 0.0).func_181675_d();
        tessellator.draw();
    }

    private String computeString3(Module module) {
        String name = module.getName();
        String str = this.computeString2(module);
        if (this.lowercaseSetting2.isEnabled()) {
            name = name.toLowerCase(Locale.ROOT);
            str = str.toLowerCase(Locale.ROOT);
        }

        if (this.dashSetting.matchesString2("None") || str.isEmpty()) {
            return name;
        }
        if (this.dashSetting.matchesString2("Bracket")) {
            return name + " §7[" + str + "]";
        }
        return this.dashSetting.matchesString2("Dash") ? name + " §7- " + str : name + " §7" + str;
    }

    private CustomFontRenderer computeDrawStringWithShadowComponent() {
        return FontResourceLoader.computeDrawStringWithShadowComponent2(this.typefaceSetting.getText(), 19.0F);
    }

    private Color computeColor(Color color, Color color2, double d) {
        int n = this.clamp((int)(color.getRed() * d + color2.getRed() * (1.0 - d)), 0, 255);
        int n2 = this.clamp((int)(color.getGreen() * d + color2.getGreen() * (1.0 - d)), 0, 255);
        int n3 = this.clamp((int)(color.getBlue() * d + color2.getBlue() * (1.0 - d)), 0, 255);
        return new Color(n, n2, n3);
    }

    private float computeFloat(String str, String str2) {
        String str3 = str2 + ":" + (this.lowercaseSetting.isEnabled() ? "low" : "up");
        Float val = this.lookupMap3.get(str3);
        if (val != null) {
            return val * (float)this.scaleSetting.computeDouble();
        }
        float f = this.computeFloat(str);
        this.lookupMap3.put(str3, f);
        return f * (float)this.scaleSetting.computeDouble();
    }

    @Override
    public boolean onBlurMode(SetShaderTypeEvent.BlurMode blurMode) {
        return true;
    }

    private Color[] computeColorArray() {
        if (this.atmosphereSetting.matchesString2("Flawless")) {
            return computeColorArray(244, 221, 87, 253, 16, 241);
        }
        if (this.atmosphereSetting.matchesString2("Violet")) {
            return computeColorArray(190, 187, 238, 107, 61, 157);
        }
        if (this.atmosphereSetting.matchesString2("Cosmic")) {
            return computeColorArray(2, 205, 253, 17, 15, 249);
        }
        if (this.atmosphereSetting.matchesString2("Watery")) {
            return computeColorArray(108, 167, 242, 11, 59, 212);
        }
        if (this.atmosphereSetting.matchesString2("Fiery")) {
            return computeColorArray(244, 208, 60, 253, 15, 89);
        }
        if (this.atmosphereSetting.matchesString2("Bloody")) {
            return computeColorArray(197, 56, 112, 87, 21, 12);
        }
        if (this.atmosphereSetting.matchesString2("Pleasant")) {
            return computeColorArray(15, 185, 242, 236, 14, 206);
        }
        if (this.atmosphereSetting.matchesString2("Lightweight")) {
            return computeColorArray(1, 231, 99, 1, 36, 239);
        }
        if (this.atmosphereSetting.matchesString2("Winter")) {
            return new Color[]{Color.WHITE, Color.WHITE};
        }
        if (this.atmosphereSetting.matchesString2("Shadow")) {
            return computeColorArray(97, 131, 255, 206, 212, 255);
        }
        if (this.atmosphereSetting.matchesString2("Purple")) {
            Color color = new Color(5391249);
            return new Color[]{color, color.brighter()};
        }
        if (this.atmosphereSetting.matchesString2("Atmosphere")) {
            Color color2 = new Color(139, 122, 255);
            return new Color[]{color2, color2.brighter()};
        }
        return computeColorArray(228, 131, 71, 104, 86, 204);
    }

    private double computeDouble(float f, long l) {
        float f2 = (float)((System.currentTimeMillis() + l) % (int)(f * 1000.0F)) / (f * 1000.0F);
        return f2 <= 0.5 ? f2 * 2.0F : 2.0F - f2 * 2.0F;
    }

    private void process13() {
        if (Brutality.moduleManager != null) {
            this.entries2.clear();
            this.lookupMap.clear();
            this.handleString2("AutoClicker", "AutoClicker", "%v1", new String[]{"Target CPS"});
            this.handleString("KillAura", "", "Targets", new String[]{"Single", "Single", "Switch"});
            this.handleString2("AntiKnockback", "Velocity", "%v1% %v2%", new String[]{"Horizontal", "Vertical"});
            this.handleString("Velocity", "", "Mode", "%v1% %v2%", new String[]{"Horizontal", "Vertical"});
            this.handleString2("FastMine", "", "%v1x", new String[]{"Break speed"});
            this.handleString("NoSlow", "", "Mode", new String[]{"Vanilla", "Float", "Interact", "Invalid", "Jump", "Sneak"});
            this.handleString("NoFall", "", "Mode", new String[]{"Spoof", "Single", "Extra", "NoGround A", "NoGround B", "Precision", "Position"});
            this.handleString("BedAura", "", "Break mode", new String[]{"Legit", "Instant", "Swap"});
            this.handleString2("LagRange", "", "%v1ms", new String[]{"Maximum delay"});
            this.handleString("HitSelect", "", "Mode", new String[]{"Last", "Criticals"});
        }
    }

    private void onSetShaderTypeEvent2(SetShaderTypeEvent setShaderTypeEvent) {
        if (this.novolineSetting.matchesString2("Modern")) {
            this.onSetShaderTypeEvent(setShaderTypeEvent);
        } else if (!this.entries.isEmpty()) {
            SetShaderTypeEvent.BlurMode shaderType = setShaderTypeEvent.getShaderType();
            if (shaderType == SetShaderTypeEvent.BlurMode.GLOW || shaderType == SetShaderTypeEvent.BlurMode.SHADOW || shaderType == SetShaderTypeEvent.BlurMode.BLUR) {
                ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
                float f = sr.getScaledWidth() - (float)this.xOffsetSetting.computeDouble();
                float f2 = (float)this.yOffsetSetting.computeDouble();
                this.handleFloat2(f, f2);
            }
        }
    }

    private String computeString() {
        String text = this.noneSetting.getText();
        switch (text) {
            case "Dash":
                return " §7- ";
            case "Pipe":
                return " §7| ";
            case "Equals":
                return " §7= ";
            case "Bracket":
                return " §7[";
            case "Parenthesis":
                return " §7(";
            case "None":
                return " §7";
            default:
                return " §7- ";
        }
    }

    private float computeFloat(float f, float f2, float f3, float f4) {
        String text = this.scaleCenterSetting.getText();
        switch (text) {
            case "Scale Center":
                return f - (float)this.xOffsetSetting.computeDouble() - f2 / 2.0F - f2 * f4 / (2.0F * (float)this.scaleSetting.computeDouble());
            case "Scale Right":
            default:
                return f - f3 / (float)this.scaleSetting.computeDouble() - (float)this.xOffsetSetting.computeDouble() + (1.0F - f4);
        }
    }

    private static Color[] computeColorArray(int n, int n2, int n3, int n4, int n5, int n6) {
        return new Color[]{new Color(n, n2, n3), new Color(n4, n5, n6)};
    }

    private void handleScaledResolution(ScaledResolution sr) {
        if (!(this.mc.currentScreen instanceof GuiChat)) {
            this.flag5 = false;
        } else {
            int n = Mouse.getX() * sr.getScaledWidth() / this.mc.displayWidth;
            int n2 = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / this.mc.displayHeight - 1;
            if (!Mouse.isButtonDown(0)) {
                this.flag5 = false;
            } else {
                boolean flag = this.rightSetting.matchesString2("Right");
                boolean flag2 = this.bottomSetting.matchesString2("Top");
                int n3 = 0;
                float f = 0.0F;

                for (Module module : this.entries3) {
                    if (module.checkState44()) {
                        n3++;
                        f = Math.max(f, (float)this.computeInt(module));
                    }
                }

                if (n3 == 0) {
                    n3 = 1;
                    f = Math.max(f, 60.0F);
                }

                float f2 = f + 6.0F;
                float f3 = n3 * this.computeInt();
                float f4 = flag ? sr.getScaledWidth() - (float)this.xPaddingSetting.computeDouble() - f2 : (float)this.xPaddingSetting.computeDouble();
                float f5 = flag2 ? (float)this.yPaddingSetting.computeDouble() : sr.getScaledHeight() - (float)this.yPaddingSetting.computeDouble() - f3;
                if (!this.flag5 && n >= f4 && n <= f4 + f2 && n2 >= f5 && n2 <= f5 + f3) {
                    this.flag5 = true;
                    this.count2 = n;
                    this.count3 = n2;
                    this.count4 = (int)this.xPaddingSetting.computeDouble();
                    this.count5 = (int)this.yPaddingSetting.computeDouble();
                }

                if (this.flag5) {
                    int n4 = n - this.count2;
                    int n5 = n2 - this.count3;
                    this.xPaddingSetting.handleDouble2(Math.max(0, Math.min(255, this.count4 + (flag ? -n4 : n4))));
                    this.yPaddingSetting.handleDouble2(Math.max(0, Math.min(255, this.count5 + (flag2 ? n5 : ~(n5 - 1)))));
                }
            }
        }
    }

    private void handleFloat(float f, float f2) {
        float f3 = f2;
        long l = 0L;

        for (int i = 0; i < this.entries.size(); i++) {
            ArrayList.Entry3 entry3 = this.entries.get(i);
            if (entry3.visible || entry3.flag) {
                String str = this.computeString(entry3.name);
                float f4 = entry3.scale * (float)this.scaleSetting.computeDouble();
                float f5 = this.computeFloat(str, entry3.name);
                float f6 = f5 * f4;
                float f7 = this.computeFloat(f, f5, f6, f4);
                float f8 = f7 - 2.0F;
                float f9 = f7 + f5 / (float)this.scaleSetting.computeDouble() * f4 + 2.0F;
                float f10 = f3 + this.computeInt2() * f4 + 2.0F;
                int n = this.computeInt(l);
                if (this.backgroundSetting.isEnabled()) {
                    float f11 = f3 + this.computeInt2() * f4 + 1.0F;
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableTexture2D();
                    this.handleFloat(f8, f3, f9, f11, new Color(0, 0, 0, (int)this.backgroundAlphaSetting.computeDouble()).getRGB());
                    GlStateManager.enableTexture2D();
                    GlStateManager.popMatrix();
                }

                if (this.outlineSetting.isEnabled()) {
                    this.handleInt(i, f8, f3, f9, f10, n, f);
                }

                this.handleString2(str, f7, f3 + 1.0F, f4, n);
                f3 += this.computeInt2() * f4 + 1.0F;
                l = (long)((float)l + (this.downSetting.matchesString2("Up") ? 100.0F * f4 : -100.0F * f4));
            }
        }
    }

    private void process3() {
        HashSet<String> set = new HashSet<>();

        for (Module module : ModuleManager.getEntries2()) {
            if (module.checkState44() && !this.matchesModule(module)) {
                set.add(module.getName());
                String str = this.computeString(module);
                String str2 = this.lookupMap4.get(module.getName());
                if (str2 == null || !str2.equals(str)) {
                    this.lookupMap4.put(module.getName(), str);
                    this.lookupMap2.remove(module.getName());
                    this.invalidateWidthCache(module.getName());
                    this.flag = true;
                }
            }
        }

        for (ArrayList.Entry3 entry3 : this.entries) {
            boolean flag = set.contains(entry3.name);
            if (entry3.visible != flag) {
                entry3.visible = flag;
                entry3.flag = true;
                entry3.flag2 = !flag;
                float factor = entry3.factor;
                factor = factor >= 1.0F ? 0.0F : (factor > 0.0F ? 1.0F - factor : factor);
                long l = System.currentTimeMillis() - (long)(this.animationSpeedSetting.computeDouble() * factor);
                entry3.timestamp = l;
                this.lookupMap2.remove(entry3.name);
                this.invalidateWidthCache(entry3.name);
            }

            set.remove(entry3.name);
        }

        for (String str3 : set) {
            ArrayList.Entry3 entry32 = new ArrayList.Entry3();
            entry32.name = str3;
            entry32.visible = true;
            entry32.flag = true;
            entry32.flag2 = false;
            entry32.timestamp = System.currentTimeMillis();
            entry32.factor = 0.0F;
            entry32.factor2 = 0.0F;
            entry32.scale = 0.0F;
            this.entries.add(entry32);
            this.flag = true;
        }
    }

    private int computeInt(String str) {
        CustomFontRenderer drawStringWithShadowComponent = this.computeDrawStringWithShadowComponent();
        return drawStringWithShadowComponent == null ? this.mc.fontRendererObj.getStringWidth(str) : drawStringWithShadowComponent.getStringWidth(str);
    }

    private void process() {
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
        this.handleScaledResolution(sr);
        this.process14();
        int scaledWidth = sr.getScaledWidth();
        int scaledHeight = sr.getScaledHeight();
        boolean flag = this.bottomSetting.matchesString2("Top");
        boolean flag2 = this.rightSetting.matchesString2("Right");
        int n = this.computeInt();
        int n2 = n - 2;
        long now = System.currentTimeMillis();
        int n3 = 0;
        boolean flag3 = false;
        int n4 = -1;
        int MIN_VALUE = Integer.MIN_VALUE;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        this.entries4.clear();
        this.entries5.clear();

        for (Module module : this.entries3) {
            ImplAnimation implAnimation = this.lookupMap5.get(module);
            if (implAnimation != null && (module.checkState44() || !implAnimation.matchesForwardsType(ForwardsType.BACKWARDS))) {
                float f = (float)implAnimation.computeDouble();
                if (!(f <= 0.01F)) {
                    String orDefault = this.b.getOrDefault(module, this.computeString3(module));
                    float f2 = this.computeInt(module);
                    int rgb = this.computeColor(now, n3 * 0.05F).getRGB();
                    float orDefault2 = this.lookupMap6.getOrDefault(module, -2.0F);
                    float orDefault3 = this.lookupMap7.getOrDefault(module, 0.0F);
                    int n8 = (int)this.xPaddingSetting.computeDouble();
                    int n9 = (int)this.yPaddingSetting.computeDouble();
                    int n10 = flag ? (int)(n9 + orDefault3) : (int)(scaledHeight - n2 - n9 - orDefault3);
                    float f3 = f2 - orDefault2;
                    int n11;
                    int n12;
                    int n13;
                    int n14;
                    int n15;
                    if (flag2) {
                        n11 = (int)(scaledWidth - orDefault2 - n8 - (this.accentBarSetting.isEnabled() ? 2 : 1));
                        n12 = (int)(scaledWidth - orDefault2 - n8 - (this.accentBarSetting.isEnabled() ? 3 : 2));
                        n13 = scaledWidth - n8;
                        n14 = scaledWidth - n8 - 1;
                        n15 = scaledWidth - n8;
                    } else {
                        n11 = (int)(n8 + (this.accentBarSetting.isEnabled() ? 2 : 1) + f3);
                        n12 = (int)(n8 + f3);
                        n13 = (int)(n8 + orDefault2 + (this.accentBarSetting.isEnabled() ? 3 : 2));
                        n14 = (int)(n8 + f3);
                        n15 = (int)(n8 + 1 + f3);
                    }

                    int n16 = n10 - 2;
                    int n17 = n10 + n2;
                    int n18 = (int)(this.backgroundOpacitySetting.computeDouble() / 100.0 * f * 255.0);
                    this.entries4.add(new int[]{n12, n16, n13, n17});
                    this.entries5.add(new int[]{n12, n16, n13, n17, rgb});
                    if (this.backgroundOpacitySetting.computeDouble() > 0.0) {
                        Gui.drawRect(n12, n16, n13, n17, new Color(0, 0, 0, Math.max(0, Math.min(255, n18))).getRGB());
                    }

                    Color color = new Color(rgb);
                    int rgb2 = new Color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, f).getRGB();
                    this.handleString(orDefault, n11, n10 - 1, rgb2, this.textShadowSetting.isEnabled());
                    if (this.accentBarSetting.isEnabled()) {
                        Color color2 = this.computeColor(now, (n3 + 1) * 0.05F);
                        int rgb3 = new Color(color2.getRed() / 255.0F, color2.getGreen() / 255.0F, color2.getBlue() / 255.0F, f).getRGB();
                        if (this.outlineSetting2.matchesString2("Side")) {
                            handleInt(n14, n16, n15, n17, rgb2, rgb3);
                        } else if (this.outlineSetting2.matchesString2("Top")) {
                            handleInt(n14, n16, n15, n17, rgb2, rgb3);
                            if (!flag3) {
                                Gui.drawRect(n12, n16, n13, n16 + 1, rgb2);
                                flag3 = true;
                            }
                        } else {
                            handleInt(n14, n16, n15, n17, rgb2, rgb3);
                            handleInt(n12, n16, n12 + 1, n17, rgb2, rgb3);
                            if (n4 != -1 && n4 < n12) {
                                Gui.drawRect(n4, n16, n12, n16 + 1, rgb2);
                            } else if (!flag3) {
                                Gui.drawRect(n12, n16, n13, n16 + 1, rgb2);
                                flag3 = true;
                            }

                            MIN_VALUE = n17;
                            n5 = n12;
                            n6 = n13;
                            n7 = rgb2;
                        }
                    }

                    if (module.checkState44() || f > 0.1F) {
                        n3++;
                        n4 = n12;
                    }
                }
            }
        }

        if (this.accentBarSetting.isEnabled() && this.outlineSetting2.matchesString2("Outline") && MIN_VALUE != Integer.MIN_VALUE) {
            Gui.drawRect(n5, MIN_VALUE - 1, n6, MIN_VALUE, n7);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private boolean checkState2() {
        return this.novolineSetting.matchesString2("Brutality") || this.novolineSetting.matchesString2("Novoline");
    }

    private void handleString2(String str, String str2, String str3, String[] parts) {
        ArrayList.Entry entry = new ArrayList.Entry();
        entry.text = str;
        entry.alias = str2.isEmpty() ? null : str2;
        entry.displayString = str3;
        entry.stringArray = parts;
        entry.type = "placeholders";
        this.entries2.add(entry);
        this.handleEntry(entry);
    }

    private void handleString(String str, float f, float f2, int n, boolean flag) {
        CustomFontRenderer drawStringWithShadowComponent = this.computeDrawStringWithShadowComponent();
        if (drawStringWithShadowComponent == null) {
            this.mc.fontRendererObj.drawString(str, (int)f, (int)f2, n, flag);
        } else if (flag) {
            drawStringWithShadowComponent.drawStringWithShadow(str, f, f2, n);
        } else {
            drawStringWithShadowComponent.computeInt3(str, f, f2, n);
        }
    }

    private void handleFloat2(float f, float f2) {
        float f3 = f2;
        long l = 0L;

        for (int i = 0; i < this.entries.size(); i++) {
            ArrayList.Entry3 entry3 = this.entries.get(i);
            if (entry3.visible || entry3.flag) {
                String str = this.computeString(entry3.name);
                float f4 = entry3.scale * (float)this.scaleSetting.computeDouble();
                float f5 = this.computeFloat(str, entry3.name);
                float f6 = f5 * f4;
                float f7 = this.computeFloat(f, f5, f6, f4);
                float f8 = f7 - 2.0F;
                float f9 = f7 + f5 / (float)this.scaleSetting.computeDouble() * f4 + 2.0F;
                float f10 = f3 + this.computeInt2() * f4 + 2.0F;
                int n = this.computeInt(l);
                if (this.backgroundSetting.isEnabled()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableTexture2D();
                    this.handleFloat(f8, f3, f9, f10, new Color(0, 0, 0, (int)this.backgroundAlphaSetting.computeDouble()).getRGB());
                    GlStateManager.enableTexture2D();
                    GlStateManager.popMatrix();
                }

                if (this.outlineSetting.isEnabled()) {
                    this.handleInt(i, f8, f3, f9, f10, n, f);
                }

                this.handleString2(str, f7, f3 + 1.0F, f4, n);
                f3 += this.computeInt2() * f4 + 1.0F;
                l = (long)((float)l + (this.downSetting.matchesString2("Up") ? 100.0F * f4 : -100.0F * f4));
            }
        }
    }

    private int clamp(int n, int n2, int n3) {
        return Math.max(n2, Math.min(n3, n));
    }

    private void invalidateWidthCache(String str) {
        String str2 = str + ":";
        this.lookupMap3.keySet().removeIf(str3 -> str3.startsWith(str2));
    }

    private void process6() {
        try {
            String str;
            if (ModuleSpecificHelper.matchesModeSetting(this.w)) {
                BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
                str = brutalityHelper != null ? brutalityHelper.getText() : this.fontSetting.getText();
            } else {
                str = this.fontSetting.getText();
            }

            String str2 = str + "@" + this.fontSizeSetting.computeDouble();
            if (str2.equals(this.text) && this.drawStringWithShadowComponent != null) {
                return;
            }

            this.text = str2;
            this.drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(str, (float)this.fontSizeSetting.computeDouble());
        } catch (Exception e) {
            this.drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(
                "brutality", (float)this.fontSizeSetting.computeDouble()
            );
            e.printStackTrace();
        }
    }

    private void process7() {
        if (this.novolineSetting.matchesString2("PrimeCheats")) {
            this.autoGrinderHandler.render();
        } else if (this.novolineSetting.matchesString2("Modern")) {
            this.process();
        } else if (this.novolineSetting.matchesString2("Novoline")) {
            this.arraylistHandler.update();
            this.arraylistHandler.render();
        } else if (!this.entries.isEmpty()) {
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
            float f = sr.getScaledWidth() - (float)this.xOffsetSetting.computeDouble();
            float f2 = (float)this.yOffsetSetting.computeDouble();
            this.updateAnimations();
            this.handleFloat(f, f2);
        }
    }

    private void process5() {
        for (ArrayList.Entry entry : this.entries2) {
            this.handleEntry(entry);
        }
    }

    private void handleEntry(ArrayList.Entry entry) {
        if (Brutality.moduleManager != null) {
            String text = entry.text;
            String str = entry.alias != null ? entry.alias : text;
            String str2 = "";
            String type = entry.type;
            switch (type) {
                case "conditional":
                    Module module = Brutality.moduleManager.computeModule(text);
                    if (module != null) {
                        String str3 = this.computeString(module, entry.text2);
                        if ("Velocity".equalsIgnoreCase(text)) {
                            if (!"Packet".equalsIgnoreCase(str3) && !"Motion".equalsIgnoreCase(str3)) {
                                str2 = str3;
                            } else {
                                String displayString = entry.displayString;
                                String[] stringArray = entry.stringArray;

                                for (int i = 0; i < stringArray.length; i++) {
                                    String str4 = "%v" + (i + 1);
                                    String str5 = this.computeString(module, stringArray[i]);
                                    displayString = displayString.replace(str4, str5);
                                }

                                str2 = displayString;
                            }
                        } else if ("Reduce".equalsIgnoreCase(str3)) {
                            str2 = "Reduce";
                        } else {
                            String displayString2 = entry.displayString;
                            String[] stringArray2 = entry.stringArray;

                            for (int j = 0; j < stringArray2.length; j++) {
                                String str6 = "%v" + (j + 1);
                                String str7 = this.computeString(module, stringArray2[j]);
                                displayString2 = displayString2.replace(str6, str7);
                            }

                            str2 = displayString2;
                        }
                    }
                    break;
                case "placeholders":
                    String displayString3 = entry.displayString;
                    String[] stringArray3 = entry.stringArray;
                    Module module2 = Brutality.moduleManager.computeModule(text);
                    if (module2 != null) {
                        for (int k = 0; k < stringArray3.length; k++) {
                            String str8 = "%v" + (k + 1);
                            String str9 = this.computeString(module2, stringArray3[k]);
                            displayString3 = displayString3.replace(str8, str9);
                        }
                    }

                    str2 = displayString3;
                    break;
                case "strings":
                    Module module3 = Brutality.moduleManager.computeModule(text);
                    if (module3 != null && entry.stringArray2 != null) {
                        String str10 = this.computeString(module3, entry.text2);

                        try {
                            int n = Integer.parseInt(str10);
                            if (n >= 0 && n < entry.stringArray2.length) {
                                str2 = entry.stringArray2[n];
                            }
                        } catch (NumberFormatException e) {
                            for (String str11 : entry.stringArray2) {
                                if (str11.equalsIgnoreCase(str10)) {
                                    str2 = str11;
                                    break;
                                }
                            }
                        }
                    }
            }

            ArrayList.Entry2 entry2 = new ArrayList.Entry2();
            entry2.alias = str;
            entry2.text = str2;
            this.lookupMap.put(text, entry2);
            this.lookupMap2.remove(text);
            this.invalidateWidthCache(text);
        }
    }

    private float computeFloat(String str) {
        return this.checkState() && this.drawStringWithShadowComponent != null
            ? this.drawStringWithShadowComponent.getStringWidth(str)
            : this.mc.fontRendererObj.getStringWidth(str);
    }

    private float computeFloat(long l, float f) {
        float f2 = (float)this.colorSpeedSetting.computeDouble();
        long l2 = (long)(3000.0 / Math.pow(Math.min(Math.max(0.5F, f2), 1.5F), 3.0));
        float f3 = 1.0F - (float)(Math.abs(l) % l2) / (float)l2;
        float f4 = f3 - f;
        return f4 - (float)Math.floor(f4);
    }

    public ArrayList() {
        super("ArrayList", 0, Category.VISUAL);
        instance = this;
        this.handleSettingArray(
            new Setting[]{
                this.novolineSetting,
                this.w,
                this.color1Setting,
                this.color2Setting,
                this.themeSetting,
                this.downSetting,
                this.waveSpeedSetting,
                this.scaleCenterSetting,
                this.animationSpeedSetting,
                this.fontSetting,
                this.fontSizeSetting,
                this.customFontSetting,
                this.lowercaseSetting,
                this.showSuffixSetting,
                this.noneSetting,
                this.scaleSetting,
                this.xOffsetSetting,
                this.yOffsetSetting,
                this.backgroundSetting,
                this.backgroundAlphaSetting,
                this.outlineSetting,
                this.outlineWidthSetting,
                this.textGlowSetting,
                this.glowIntensitySetting,
                this.glowAlphaSetting,
                this.primeBGAlphaSetting,
                this.quanProSetting,
                this.rightSetting,
                this.bottomSetting,
                this.xPaddingSetting,
                this.yPaddingSetting,
                this.backgroundOpacitySetting,
                this.accentBarSetting,
                this.outlineSetting2,
                this.typefaceSetting,
                this.textShadowSetting,
                this.dashSetting,
                this.lowercaseSetting2,
                this.smoothAnimationsSetting,
                this.rowSpacingSetting,
                this.atmosphereSetting,
                this.colorSpeedSetting,
                this.categoryExclusionSetting
            }
        );
        this.arraylistHandler = new ArraylistHandler(
            this.fontSetting,
            this.fontSizeSetting,
            this.scaleSetting,
            this.spacingSetting,
            this.backgroundSetting,
            this.backgroundAlphaSetting,
            this.outlineSetting,
            this.xOffsetSetting,
            this.yOffsetSetting,
            this.downSetting,
            module -> this.computeString(module.getName()),
            this::computeInt,
            this::matchesModule
        );
        this.autoGrinderHandler = new AutoGrinderHandler(
            this.primeBGAlphaSetting,
            this.quanProSetting,
            this.excludeCombatSetting,
            this.excludeMovementSetting,
            this.excludePlayerSetting,
            this.excludeVisualSetting,
            this.excludeMiscellaneousSetting,
            this.excludeExploitSetting,
            this.excludeScriptsSetting,
            this.excludePitSetting
        );
        this.process6();
        this.text2 = this.noneSetting.getText();
        this.flag2 = this.showSuffixSetting.isEnabled();
        this.flag3 = this.lowercaseSetting.isEnabled();
        this.amount = this.scaleSetting.computeDouble();
        this.text3 = this.fontSetting.getText();
        this.amount2 = this.fontSizeSetting.computeDouble();
        this.flag4 = this.customFontSetting.isEnabled();
    }

    private int computeInt(float f, float f2, float f3, long l) {
        float f4 = (float)((System.currentTimeMillis() + l) % (int)(f * 1000.0F)) / (f * 1000.0F);
        return Color.HSBtoRGB(f4, f2, f3);
    }

    private int computeInt() {
        return Math.max(3, this.computeInt3() - 2 + (int)this.rowSpacingSetting.computeDouble());
    }

    private Color computeColor(long l, float f) {
        float f2 = this.computeFloat(l, f);
        if (this.atmosphereSetting.matchesString2("Rainbow")) {
            return new Color(Color.HSBtoRGB(f2, 1.0F, 1.0F));
        }
        if (this.atmosphereSetting.matchesString2("Chroma")) {
            return new Color(Color.HSBtoRGB(this.computeFloat(l / 3L, 0.0F), 1.0F, 1.0F));
        }
        if (this.atmosphereSetting.matchesString2("Astolfo")) {
            if (f2 % 1.0F < 0.5F) {
                f2 = 1.0F - f2 % 1.0F;
            }

            return new Color(Color.HSBtoRGB(f2, 1.0F, 1.0F));
        }
        Color[] colors = this.computeColorArray();
        if (colors[0].equals(colors[1])) {
            return colors[0];
        }
        float f3 = (float)(2.0 * Math.abs(f2 - Math.floor(f2 + 0.5)));
        return computeColor(f3, colors[0], colors[1]);
    }

    private void process4() {
        this.entries3.clear();
        this.lookupMap5.clear();
        this.lookupMap6.clear();
        this.lookupMap7.clear();
        this.lookupMap8.clear();
        this.b.clear();
        this.lookupMap9.clear();
        this.entries4.clear();
        this.entries5.clear();
        this.flag5 = false;
    }

    private void process12() {
        if (ModuleSpecificHelper.matchesModeSetting(this.w)) {
            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
            if (brutalityHelper != null) {
                this.color3 = brutalityHelper.getColor();
                this.color = brutalityHelper.getColor();
                this.color2 = brutalityHelper.getColor2();
            } else {
                this.color3 = this.color1Setting.computeColor();
                this.color = this.color3;
                this.color2 = this.color2Setting.computeColor();
            }
        } else {
            this.color3 = this.color1Setting.computeColor();
            if (this.themeSetting.matchesString2("Gradient")) {
                this.color = this.color3;
                this.color2 = this.color2Setting.computeColor();
            }
        }
    }

    private boolean z() {
        return this.checkState2();
    }

    private String computeString2(Module module) {
        if (module.isSuffix2() && module.getSuffix() != null) {
            return module.getSuffix();
        }
        if (Brutality.settingsManager == null) {
            return "";
        }
        List<Setting> settings = Brutality.settingsManager.computeList(module);

        for (Setting setting : settings) {
            if (setting instanceof ModeSetting) {
                return ((ModeSetting)setting).getText();
            }
        }

        for (Setting setting2 : ((Iterable<Setting>)(Object)(settings))) {
            if (setting2 instanceof NumberSetting) {
                double d = ((NumberSetting)setting2).computeDouble();
                return d == (long)d ? String.valueOf((long)d) : String.format(Locale.ROOT, "%.1f", d);
            }
        }

        return "";
    }

    private String computeString2(String str) {
        if (this.lookupMap.containsKey(str)) {
            ArrayList.Entry2 entry2 = this.lookupMap.get(str);
            String str2 = entry2.alias != null ? entry2.alias : str;
            if (this.showSuffixSetting.isEnabled() && !entry2.text.isEmpty()) {
                str2 = str2 + this.computeString() + entry2.text;
            }

            return str2;
        }
        if (!this.showSuffixSetting.isEnabled()) {
            return str;
        }
        Module module = null;
        if (Brutality.moduleManager != null) {
            for (Module module2 : ModuleManager.getEntries2()) {
                if (module2.getName().equals(str)) {
                    module = module2;
                    break;
                }
            }
        }

        if (module != null) {
            if (module.isSuffix2() && module.getSuffix() != null) {
                return str + this.computeString() + module.getSuffix();
            }

            String str3 = this.computeString4(module);
            if (!str3.isEmpty()) {
                return str + this.computeString() + str3;
            }
        }

        return str;
    }

    private void process11() {
        this.entries.sort((entry3, entry32) -> {
            float f = this.computeFloat(this.computeString(entry3.name), entry3.name);
            float f2 = this.computeFloat(this.computeString(entry32.name), entry32.name);
            return Float.compare(f2, f);
        });
    }

    public static Color computeColor(Module module) {
        ArrayList list = instance;
        if (list == null) {
            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
            return brutalityHelper == null ? Color.WHITE : brutalityHelper.getColor();
        }
        if (list.novolineSetting.matchesString2("PrimeCheats")) {
            return new Color(list.autoGrinderHandler.computeInt2(module), true);
        }
        if (list.novolineSetting.matchesString2("Modern")) {
            return list.computeColor(System.currentTimeMillis(), 0.0F);
        }
        list.process12();
        return new Color(list.computeInt(0L), true);
    }

    private void onSetShaderTypeEvent(SetShaderTypeEvent setShaderTypeEvent) {
        if (setShaderTypeEvent.getShaderType() == SetShaderTypeEvent.BlurMode.GLOW) {
            for (int[] ints : this.entries5) {
                Gui.drawRect(ints[0], ints[1], ints[2], ints[3], ints[4] | 0xFF000000);
            }
        } else if (setShaderTypeEvent.getShaderType() == SetShaderTypeEvent.BlurMode.BLUR || setShaderTypeEvent.getShaderType() == SetShaderTypeEvent.BlurMode.SHADOW) {
            for (int[] ints2 : this.entries4) {
                Gui.drawRect(ints2[0], ints2[1], ints2[2], ints2[3], -1);
            }
        }
    }

    private int computeInt2() {
        return this.checkState() && this.drawStringWithShadowComponent != null
            ? this.drawStringWithShadowComponent.getHeight()
            : this.mc.fontRendererObj.FONT_HEIGHT;
    }

    public static class Entry {
        public String text;
        public String alias;
        public String displayString;
        public String[] stringArray;
        public String text2;
        public String[] stringArray2;
        public String text3;
        public String type;
    }

    public static class Entry2 {
        public String alias;
        public String text;
    }

    public static class Entry3 {
        public String name;
        public boolean visible = false;
        public boolean flag = false;
        public boolean flag2 = false;
        public long timestamp = 0L;
        public float factor = 0.0F;
        public float factor2 = 0.0F;
        public float scale = 0.0F;
    }
}
