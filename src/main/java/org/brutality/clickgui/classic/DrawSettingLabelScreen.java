package org.brutality.clickgui.classic;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ChatAllowedCharacters;
import org.apache.commons.io.FilenameUtils;
import org.brutality.Brutality;
import org.brutality.clickgui.classic.DrawScriptsScreen.ThemesMode;
import org.brutality.clickgui.newclick.CombatType;
// VENDOR-DISABLED: import org.brutality.clickgui.newclick.GuiCloudBrowser;
import org.brutality.clickgui.newclick.HypixelPitType;
import org.brutality.clickgui.newclick.LatestWipeScreen;
import org.brutality.clickgui.newclick.HudEditorGui;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.font.atlas.ClassicFontRegistry;
import org.brutality.gui.macro.ChatMacrosScreen;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.client.ClickGUI;
import org.brutality.notification.old.ModuleEnabledHandler;
import org.brutality.script.ScriptModule;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ColorThemeSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.MultiBoolSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
// VENDOR-DISABLED: import org.brutality.system.EntitlementState;
// VENDOR-DISABLED: import org.brutality.system.cloud.BrutalityCloudClient;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.render.BindTextureHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.LocationHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.shader.impl.DualKawaseBlurShader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class DrawSettingLabelScreen extends CustomGuiScreen {
    private static final Color color = new Color(12, 14, 26, 120);
    private static final Color color2 = new Color(24, 28, 50, 150);
    private static final Color color3 = new Color(60, 64, 96, 200);
    private static final Color color4 = new Color(236, 238, 248);
    private static final Color color5 = new Color(150, 154, 176);
    private static final int count = 114;
    private static final int count2 = 4;
    private static final int count3 = 18;
    private static final int count4 = 14;
    private static final int count5 = 12;
    private static final int count6 = 19;
    private static final int count7 = 18;
    private static final float factor = 7.0F;
    private static final int count8 = 7;
    private static final int count9 = 6;
    private static final int count10 = 6;
    private static final int count11 = 118;
    private static final int count12 = 34;
    private static final int count13 = 6;
    private static final float factor2 = 150.0F;
    private static final long timestamp = 230L;
    private static final float factor3 = 170.0F;
    private static final int count14 = 42;
    private static final int count15 = 6;
    private static final int count16 = 3;
    private static final int count17 = 57;
    private static final String[] stringArray = new String[]{"Combat", "Movement", "Player", "Visual", "Other", "Config", "Scripts", "Pit"};
    private static final String text = "Config";
    private final List<DrawSettingLabelScreen.Entry3> entries = new ArrayList<>();
    private final List<DrawSettingLabelScreen.Entry3> entries2 = new ArrayList<>();
    private boolean flag;
    private HypixelPitType hypixelPitType = HypixelPitType.HYPIXEL_PIT;
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;
    private long timestamp2;
    private long timestamp3;
    private float factor4 = 0.0F;
    private boolean flag5 = false;
    private long timestamp4 = 0L;
    private long timestamp5 = 0L;
    private long timestamp6;
    private float factor5 = 0.016666668F;
    private final Map<Object, Float> lookupMap = new HashMap<>();
    private final Map<Object, Float> lookupMap2 = new IdentityHashMap<>();
    private final Map<String, Float> lookupMap3 = new HashMap<>();
    private final Map<Setting, Float> lookupMap4 = new IdentityHashMap<>();
    private final Map<Setting, Long> lookupMap5 = new IdentityHashMap<>();
    private final Map<Setting, Long> lookupMap6 = new IdentityHashMap<>();
    private final Map<Setting, ClassicComponent> lookupMap7 = new IdentityHashMap<>();
    private final List<DrawSettingLabelScreen.Entry5> entries3 = new ArrayList<>();
    private boolean flag6 = true;
    private ClassicComponent classicComponent;
    private float factor6;
    private float factor7;
    private final Map<Setting, DrawSettingLabelScreen.Entry2> lookupMap8 = new IdentityHashMap<>();
    private final Map<Setting, Boolean> lookupMap9 = new IdentityHashMap<>();
    private final Map<Setting, Float> lookupMap10 = new IdentityHashMap<>();
    private Setting setting;
    private DrawSettingLabelScreen.Entry5 entry5;
    private final Set<String> itemSet = new HashSet<>();
    private DrawSettingLabelScreen.Entry4 entry4;
    private StringSetting stringSetting;
    private NumberSetting numberSetting;
    private String text2 = "";
    private boolean flag7;
    private boolean flag8;
    private String text3 = "";
    private int count18 = 0;
    private float factor8 = 0.0F;
    private long timestamp7 = 0L;
    private String text4 = null;
    private float factor9;
    private float factor10;
    private float factor11 = 0.0F;
    private boolean flag9 = false;
    private boolean flag10 = false;
    private String text5 = "";
    private static final Set<String> itemSet2 = new HashSet<>(Arrays.asList("alts", "license", "licence", "binds"));
    private float factor12;
    private float factor13;
    private float factor14;
    private float factor15;
    private float factor16;
    private Color color6 = new Color(120, 110, 255);
    private float factor17 = 1.0F;
    private final List<Object> entries4 = new ArrayList<>(); // VENDOR-DISABLED: BrutalityCloudClient.Entry3
    private String text6 = "";
    private boolean flag11;
    private int count19 = 0;
    private float factor18 = 0.0F;
    private long timestamp8 = 0L;
    private final List<Object> entries5 = new ArrayList<>(); // VENDOR-DISABLED: BrutalityCloudClient.Entry3
    private String text7 = "";
    private boolean flag12;
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    private List<String> entries6 = null;
    private static final String[] stringArray2 = new String[]{"General", "Autoblock", "Targeting", "Rotations", "Timing", "Render", "Colors"};

    private void handleSetting(Setting setting) {
        this.lookupMap6.put(setting, System.currentTimeMillis());
    }

    private void handleInt(int n, int n2, float f) {
        float f2 = this.text4 != null && !this.flag9 ? 1.0F : 0.0F;
        this.factor11 = this.computeFloat(this.factor11, f2, 32.0F);
        if (this.flag9 && this.factor11 < 0.02F) {
            this.text4 = null;
            this.flag9 = false;
            this.factor11 = 0.0F;
        }

        if (this.text4 != null && !(this.factor11 < 0.01F)) {
            String str = this.text4;
            float f3 = 114.0F;
            float f4 = 46.0F;
            float f5 = this.factor9;
            float f6 = this.factor10;
            float f7 = computeFloat(clamp(this.factor11, 0.0F, 1.0F));
            float f8 = f * f7;
            float f9 = (1.0F - f7) * 5.0F;
            this.handleFloat(0.0F, 0.0F, mc.displayWidth, mc.displayHeight, this::process3, this::process3);
            float f10 = 0.97F + 0.03F * f7;
            float f11 = f5 - f9;
            GlStateManager.pushMatrix();
            GlStateManager.translate(f11, f6, 0.0F);
            GlStateManager.scale(f10, f10, 1.0F);
            GlStateManager.translate(-f11, -f6, 0.0F);
            float f12 = f5 - f9;
            this.handleFloat2(f12, f6, f3, f4, 5.0F, f8 * 0.7F);
            LocationHelper.handleFloat2(f12, f6, f3, f4, 5.0F, computeColor(color, f8));
            LocationHelper.handleFloat2(f12, f6, f3, f4, 5.0F, computeColor(color2, 0.5F * f8));
            LocationHelper.handleFloat2(f12, f6, f3, f4, 5.0F, computeColor(this.color6, 0.1F * f8));
            String[] parts = new String[]{"Save", "Upload", "Delete"};

            for (int i = 0; i < 3; i++) {
                float f13 = f6 + 2.0F + i * 14;
                boolean flag = n >= f12 && n <= f12 + f3 && n2 >= f13 && n2 <= f13 + 14.0F;
                float f14 = this.computeFloat("context-" + i, flag ? 1.0F : 0.0F, 32.0F);
                this.handleFloat(f12, f13, f3, 14.0F, f14, f8);
                Color color3 = i == 2 ? new Color(235, 110, 110) : color4;
                ClassicFontRegistry.drawStringWithShadowComponent
                    .computeInt(parts[i], f12 + 7.0F + f14, f13 + 3.0F, computeColor(color3, f8).getRGB());
            }

            GlStateManager.popMatrix();
            if (this.factor11 > 0.6F && !this.flag9) {
                Runnable[] tasks = new Runnable[]{() -> {
                    if (Brutality.appdataHandler.matchesString2(str)) {
                        this.y();
                    } else {
                        ModuleEnabledHandler.handleString2("Config Save Failed", "Could not save " + str, 3000L);
                    }

                    this.process3();
                }, () -> {
                    this.handleString(str);
                    this.process3();
                }, () -> {
                    this.handleString2(str);
                    this.process3();
                }};

                for (int j = 0; j < 3; j++) {
                    this.handleFloat(f5, f6 + 2.0F + j * 14, f3, 14.0F, tasks[j], null);
                }
            }
        }
    }

    private String computeString(String str) {
        str = str.replace("_", " ");
        return str.isEmpty() ? str : str.charAt(0) + str.substring(1).toLowerCase();
    }

    private float computeFloat(DrawSettingLabelScreen.Entry3 entry3) {
        float f = 42.0F;
        if (this.count19 == 1) {
            if (!this.text7.isEmpty()) {
                f += 14.0F;
            }

            f += Math.max(1, this.entries5.size()) * 14;
        } else {
            for (DrawSettingLabelScreen.Entry4 entry4 : this.computeList(entry3)) {
                f += entry4.computeFloat(this);
            }
        }

        return f;
    }

    private void handleInt(int n, int n2, int n3, int n4, int n5, int n6, float f) {
        boolean flag = matchesInt(n5, n6, n, n2, n3, n4);
        float f2 = this.computeFloat("search-focus", !flag && this.text3.isEmpty() ? 0.0F : 1.0F, 22.0F);
        this.handleFloat2(n, n2, n3, n4, 7.0F + f2, f);
        if (f2 > 0.01F) {
            LocationHelper.handleFloat2(
                n - f2, n2 - f2, n3 + f2 * 2.0F, n4 + f2 * 2.0F, 7.0F + f2, computeColor(this.color6, f * 0.22F * f2)
            );
        }

        LocationHelper.handleFloat2(n, n2, n3, n4, 7.0F, computeColor(color, f));
        LocationHelper.handleFloat2(n, n2, n3, n4, 7.0F, computeColor(color2, 0.5F * f));
        String str = this.text3.isEmpty() ? "Search..." : this.text3;
        Color color3 = this.text3.isEmpty() ? computeColor2(color5, color4, f2 * 0.35F) : color4;
        ClassicFontRegistry.drawStringWithShadowComponent
            .computeInt(str, n + 12 + f2, n2 + n4 / 2.0F - 4.0F, computeColor(color3, f).getRGB());
    }

    private String computeString(Module module) {
        if (module != null && module.getCategory() != null) {
            Category category = module.getCategory();
            if (category == Category.COMBAT) {
                return "Combat";
            }
            if (category == Category.MOVEMENT) {
                return "Movement";
            }
            if (category == Category.PLAYER) {
                return "Player";
            }
            return category == Category.VISUAL ? "Visual" : "Other";
        }
        return "Other";
    }

    private String computeString3(int n) {
        switch (n) {
            case 2:
                return "Hypixel staff";
            case 3:
            default:
                return this.computeBrutalityHelper().getName();
            case 4:
                return "Configs & scripts";
            case 5:
                return "Latest wipes";
            case 6:
                return "Chat keybinds";
        }
    }

    private <K> float computeFloat(Map<K, Float> map, K val, float f, float f2) {
        float orDefault = map.getOrDefault(val, f);
        orDefault = this.computeFloat(orDefault, f, f2);
        map.put(val, orDefault);
        return orDefault;
    }

    private void handleFloat(float f, float f2, float f3, float f4, ClassicComponent classicComponent, float f5, float f6, Runnable task) {
        if (this.flag6) {
            if (!(f2 + f4 < this.factor12) && !(f2 > this.factor13)) {
                DrawSettingLabelScreen.Entry5 entry5 = new DrawSettingLabelScreen.Entry5();
                entry5.x = f;
                entry5.y = f2;
                entry5.w = f3;
                entry5.factor = f4;
                entry5.classicComponent = classicComponent;
                entry5.factor2 = f5;
                entry5.factor3 = f6;
                entry5.runnable2 = task;
                this.entries3.add(entry5);
            }
        }
    }

    private List<String> localConfigNames() {
        if (this.entries6 != null) {
            return this.entries6;
        }
        List list = new ArrayList();
        File file = Brutality.appdataHandler != null ? Brutality.appdataHandler.computeFile() : null;
        File[] files = file == null ? null : file.listFiles((file2, str) -> str.toLowerCase().endsWith(".json"));
        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));

            for (File file3 : files) {
                String str2 = FilenameUtils.removeExtension(file3.getName());
                if (!itemSet2.contains(str2.toLowerCase())) {
                    list.add(str2);
                }
            }
        }

        this.entries6 = list;
        return list;
    }

    private String computeString(Module module, Setting setting) {
        if (module != null && setting != null && Brutality.settingsManager != null) {
            for (Setting setting2 : Brutality.settingsManager.computeList2(module)) {
                if (setting2 instanceof KeybindListSetting && this.matchesKeybindListSetting((KeybindListSetting)setting2, setting)) {
                    return this.computeString(setting2);
                }
            }

            return null;
        }
        return null;
    }

    private float computeFloat(String str, float f, float f2) {
        return this.computeFloat(this.lookupMap3, str, f, f2);
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    private String computeString(Setting setting) {
        String str = setting.getName().replace("-", " ");
        return str.isEmpty() ? str : Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.entry4 != null && mouseButton >= 0) {
            this.entry4.module.handleInt4(mouseButton < 5 ? -mouseButton - 1 : ~mouseButton + 1 - 10);
            this.entry4 = null;
        } else {
            this.stringSetting = null;
            this.handleFlag(true);
            int n = Math.round(mouseX / this.factor17);
            int n2 = Math.round(mouseY / this.factor17);

            for (int i = this.entries3.size() - 1; i >= 0; i--) {
                DrawSettingLabelScreen.Entry5 entry5 = this.entries3.get(i);
                if (entry5.matchesInt(n, n2)) {
                    if (entry5.classicComponent != null && mouseButton == 0) {
                        this.classicComponent = entry5.classicComponent;
                        this.factor6 = entry5.factor2;
                        this.factor7 = entry5.factor3;
                        this.handleInt2(n);
                        return;
                    }

                    if (entry5.entry2 != null && mouseButton == 0) {
                        this.entry5 = entry5;
                        this.handleInt(n, n2);
                        return;
                    }

                    if (mouseButton == 0 && entry5.runnable != null) {
                        entry5.runnable.run();
                        return;
                    }

                    if (mouseButton == 1 && entry5.runnable2 != null) {
                        entry5.runnable2.run();
                        return;
                    }
                }
            }
        }
    }

    private void process3() {
        if (this.text4 != null) {
            this.flag9 = true;
        }
    }

    @Override
    public void onGuiClosed() {
        this.handleFlag(true);
        this.stringSetting = null;
        this.entry4 = null;
        this.classicComponent = null;
        this.entry5 = null;
        super.onGuiClosed();
    }

    private float computeFloat(int n, float f, int n2, int n3, float f2) {
        float f3 = this.computeFloat("< Back", true, null, n, f, n2, n3, f2);

        for (HypixelPitType hypixelPitType : HypixelPitType.values()) {
            f3 = this.computeFloat(hypixelPitType.getLabel(), false, hypixelPitType, n, f3, n2, n3, f2);
        }

        return f3;
    }

    private void handleInt(int n, int n2) {
        DrawSettingLabelScreen.Entry5 entry5 = this.entry5;
        if (entry5 != null && entry5.entry2 != null) {
            if (entry5.count == 1) {
                entry5.entry2.factor3 = (float)computeDouble((n - entry5.x) / entry5.w);
                entry5.entry2.factor4 = (float)computeDouble(1.0 - (n2 - entry5.y) / entry5.factor);
            } else {
                entry5.entry2.factor2 = (float)computeDouble((n - entry5.x) / entry5.w);
            }

            entry5.entry2.process();
        }
    }

    private void handleSetting2(Setting setting) {
        if (this.setting == setting) {
            this.setting = null;
        } else {
            this.setting = setting;
            this.lookupMap10.putIfAbsent(setting, 0.0F);
        }
    }

    private static float computeFloat2(float f) {
        f = clamp(f, 0.0F, 1.0F);
        return f < 0.5F ? 4.0F * f * f * f : 1.0F - (float)Math.pow(-2.0F * f + 2.0F, 3.0) / 2.0F;
    }

    private static float computeFloat(float f) {
        return 1.0F - (1.0F - f) * (1.0F - f);
    }

    private List<DrawSettingLabelScreen.Entry> computeList(
        List<DrawSettingLabelScreen.Entry3> entry3s, int n, int n2, float f, float f2, float f3, float f4
    ) {
        int size = entry3s.size();
        int n3 = size * 114 + (size - 1) * 4;
        int n4 = (n - n3) / 2;
        int n5 = n2 - 18 - 18 - 50;
        List list = new ArrayList();

        for (int i = 0; i < size; i++) {
            DrawSettingLabelScreen.Entry3 entry3 = (DrawSettingLabelScreen.Entry3)entry3s.get(i);
            float f5 = i * 0.05F;
            float f6 = computeFloat(clamp((f - f5) / (1.0F - f5), 0.0F, 1.0F));
            float f7 = i * 0.018F;
            float f8 = computeFloat(clamp((f4 - f7) / (1.0F - f7), 0.0F, 1.0F));
            float f9;
            if (entry3.text.equals("Config")) {
                f9 = this.computeFloat();
            } else if (entry3.text.equals("Scripts")) {
                f9 = this.computeFloat(entry3);
            } else {
                f9 = entry3.flag ? 14.0F : (entry3.flag2 ? 14 * (HypixelPitType.values().length + 1) : 0);

                for (DrawSettingLabelScreen.Entry4 entry4 : this.computeList(entry3)) {
                    f9 += entry4.computeFloat(this);
                }
            }

            int n6 = (int)Math.min(f9, (float)n5);
            float f10 = 18.0F + (1.0F - f6) * 14.0F + (1.0F - f8) * 6.0F;
            int n7 = Math.round(n4 + i * 118 + f2);
            list.add(new DrawSettingLabelScreen.Entry(entry3, n7, f10, n6, f9, f6 * f3 * f8, i));
        }

        return list;
    }

    private String computeString(String str, String str2) {
        String str3 = str != null && !str.trim().isEmpty() ? str.trim() : str2;
        return this.computeString2(str3, 101.0F);
    }

    private float drawSetting(Setting setting, int n, float f, int n2, int n3, float f2) {
        if (setting instanceof KeybindListSetting keybindListSetting) {
            boolean orDefault = this.lookupMap9.getOrDefault(setting, false);
            boolean flag = matchesInt(n2, n3, n, f, 114.0F, 12.0F) && this.matchesInt(n3);
            float f3 = this.computeFloat(this.lookupMap2, setting, flag ? 1.0F : 0.0F, 30.0F);
            this.handleFloat(n, f, 114.0F, 12.0F, f3, f2 * 0.55F);
            Gui.drawRect(n, (int)f, n + 2, (int)(f + 12.0F), computeColor(this.color6, f2).getRGB());
            this.drawSettingLabel(setting, n + 6 + 3 + f3 * 0.5F, f + 3.0F, 93, flag, computeColor(this.color6, f2).getRGB());
            ClassicFontRegistry.drawStringWithShadowComponent
                .computeInt(orDefault ? "-" : "+", n + 114 - 7 - 5, f + 3.0F, computeColor(this.color6, f2).getRGB());
            this.handleFloat(
                n,
                f,
                114.0F,
                12.0F,
                () -> this.lookupMap9.put(setting, !this.lookupMap9.getOrDefault(setting, false)),
                () -> this.lookupMap9.put(setting, !this.lookupMap9.getOrDefault(setting, false))
            );
            float f4 = f + 12.0F;
            if (orDefault) {
                for (Setting setting2 : keybindListSetting.getEntries()) {
                    if (setting2 != null && setting2.isVisible()) {
                        f4 = this.drawSetting(setting2, n + 2, f4, n2, n3, f2 * 0.95F);
                    }
                }
            }

            return f4;
        }
        int n4 = n + 6 + 3;
        int n5 = n + 114 - 7;
        float f5 = this.sliderFor(setting) != null ? 19.0F : 12.0F;
        boolean flag2 = matchesInt(n2, n3, n, f, 114.0F, f5) && this.matchesInt(n3);
        float f6 = this.computeFloat(this.lookupMap2, setting, flag2 ? 1.0F : 0.0F, 30.0F);
        this.handleFloat(n, f, 114.0F, f5, f6, f2 * 0.55F);
        if (setting instanceof BooleanSetting boolSetting) {
            float f7 = this.computeFloat(boolSetting, boolSetting.isEnabled() ? 1.0F : 0.0F);
            this.drawSettingLabel(
                setting, n4 + f6 * 0.5F, f + 3.0F, n5 - 16 - 2 - n4, flag2, computeColor(computeColor2(color5, color4, f7), f2).getRGB()
            );
            float f8 = 16.0F;
            float f9 = 8.0F;
            float f10 = n5 - f8;
            float f11 = f + 2.5F;
            LocationHelper.handleFloat2(f10, f11, f8, f9, f9 / 2.0F, computeColor(computeColor2(color3, this.color6, f7), f2));
            LocationHelper.handleFloat2(
                f10 + 1.5F + f7 * (f8 - f9), f11 + 1.5F, f9 - 3.0F, f9 - 3.0F, (f9 - 3.0F) / 2.0F, computeColor(Color.WHITE, f2)
            );
            this.handleFloat(n4, f, 98.0F, 12.0F, () -> {
                boolSetting.setEnabled(!boolSetting.isEnabled());
                this.handleSetting(setting);
            }, null);
            return f + 12.0F;
        }
        if (setting instanceof ModeSetting modeSetting) {
            String str = this.computeString(this.computeString(modeSetting.getText()), 49);
            float f12 = ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str);
            float f13 = this.computeFloat3(setting);
            this.drawSettingLabel(setting, n4 + f6 * 0.5F, f + 3.0F, (int)(n5 - f12 - 4.0F - n4), flag2, computeColor(color5, f2).getRGB());
            ClassicFontRegistry.drawStringWithShadowComponent
                .computeInt(str, n5 - f12 + (1.0F - f13) * 2.0F, f + 3.0F, computeColor(this.color6, f2 * (0.35F + 0.65F * f13)).getRGB());
            this.handleFloat(n4, f, 98.0F, 12.0F, () -> this.handleSetting2(modeSetting), () -> this.handleSetting2(modeSetting));
            float f14 = f + 12.0F;
            float f15 = f14;
            float f16 = this.computeFloat2(modeSetting);
            if (f16 > 0.005F) {
                float f17 = Math.min(this.factor16, f14 + modeSetting.computeList2().size() * 12 * f16);
                float f18 = Math.max(this.factor15, f14);
                if (f17 > f18) {
                    this.handleFloat(this.factor14, f18, 114.0F, f17 - f18);
                }

                for (String str2 : modeSetting.computeList2()) {
                    boolean flag3 = modeSetting.matchesString2(str2);
                    int rgb = computeColor(flag3 ? this.color6 : color5, f2 * f16).getRGB();
                    ClassicFontRegistry.drawStringWithShadowComponent
                        .computeInt((flag3 ? "> " : "  ") + this.computeString(str2), n4 + 2, f14 + 3.0F - (1.0F - f16) * 2.0F, rgb);
                    if (this.setting == modeSetting && f14 < f17) {
                        this.handleFloat(n4, f14, 98.0F, Math.min(12.0F, f17 - f14), () -> {
                            modeSetting.handleString(str2);
                            this.setting = null;
                            this.handleSetting(setting);
                        }, () -> {
                            modeSetting.handleString(str2);
                            this.setting = null;
                            this.handleSetting(setting);
                        });
                    }

                    f14 += 12.0F;
                }

                this.handleFloat(this.factor14, this.factor15, 114.0F, this.factor16 - this.factor15);
            }

            return f15 + modeSetting.computeList2().size() * 12 * f16;
        }
        if (setting instanceof MultiBoolSetting multiBoolSetting) {
            String str3 = this.computeString(multiBoolSetting.computeString(), 49);
            float f19 = ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str3);
            float f20 = this.computeFloat3(setting);
            this.drawSettingLabel(setting, n4 + f6 * 0.5F, f + 3.0F, (int)(n5 - f19 - 4.0F - n4), flag2, computeColor(color5, f2).getRGB());
            ClassicFontRegistry.drawStringWithShadowComponent
                .computeInt(str3, n5 - f19 + (1.0F - f20) * 2.0F, f + 3.0F, computeColor(this.color6, f2 * (0.35F + 0.65F * f20)).getRGB());
            this.handleFloat(n4, f, 98.0F, 12.0F, () -> this.handleSetting2(multiBoolSetting), () -> this.handleSetting2(multiBoolSetting));
            float f21 = f + 12.0F;
            float f22 = f21;
            float f23 = this.computeFloat2(multiBoolSetting);
            if (f23 > 0.005F) {
                float f24 = Math.min(this.factor16, f21 + multiBoolSetting.getEntries().size() * 12 * f23);
                float f25 = Math.max(this.factor15, f21);
                if (f24 > f25) {
                    this.handleFloat(this.factor14, f25, 114.0F, f24 - f25);
                }

                for (String str4 : multiBoolSetting.getEntries()) {
                    boolean flag4 = multiBoolSetting.matchesString(str4);
                    int rgb2 = computeColor(flag4 ? this.color6 : color5, f2 * f23).getRGB();
                    ClassicFontRegistry.drawStringWithShadowComponent
                        .computeInt((flag4 ? "> " : "  ") + this.computeString(str4), n4 + 2, f21 + 3.0F - (1.0F - f23) * 2.0F, rgb2);
                    if (this.setting == multiBoolSetting && f21 < f24) {
                        this.handleFloat(n4, f21, 98.0F, Math.min(12.0F, f24 - f21), () -> {
                            multiBoolSetting.handleString(str4);
                            this.handleSetting(setting);
                        }, () -> {
                            multiBoolSetting.handleString(str4);
                            this.handleSetting(setting);
                        });
                    }

                    f21 += 12.0F;
                }

                this.handleFloat(this.factor14, this.factor15, 114.0F, this.factor16 - this.factor15);
            }

            return f22 + multiBoolSetting.getEntries().size() * 12 * f23;
        }
        if (setting instanceof StringSetting stringSetting) {
            String str5 = this.computeString(stringSetting.getValue(), 49);
            if (this.stringSetting == stringSetting && System.currentTimeMillis() / 400L % 2L == 0L) {
                str5 = str5 + "|";
            }

            float f26 = ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str5);
            this.drawSettingLabel(setting, n4 + f6 * 0.5F, f + 3.0F, (int)(n5 - f26 - 4.0F - n4), flag2, computeColor(color5, f2).getRGB());
            ClassicFontRegistry.drawStringWithShadowComponent.computeInt(str5, n5 - f26, f + 3.0F, computeColor(this.color6, f2).getRGB());
            this.handleFloat(n4, f, 98.0F, 12.0F, () -> {
                this.stringSetting = stringSetting;
                this.flag8 = false;
            }, () -> {
                this.stringSetting = stringSetting;
                this.flag8 = false;
            });
            return f + 12.0F;
        }
        if (!this.matchesSetting(setting)) {
            ClassicComponent classicComponent = this.sliderFor(setting);
            if (classicComponent != null) {
                String str6 = this.numberSetting == setting ? this.text2 : classicComponent.computeString();
                if (this.numberSetting == setting && System.currentTimeMillis() / 400L % 2L == 0L) {
                    str6 = str6 + "|";
                }

                str6 = this.computeString(str6, 49);
                float f27 = ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str6);
                this.drawSettingLabel(setting, n4 + f6 * 0.5F, f + 2.0F, (int)(n5 - f27 - 4.0F - n4), flag2, computeColor(color5, f2).getRGB());
                ClassicFontRegistry.drawStringWithShadowComponent
                    .computeInt(str6, n5 - f27, f + 1.0F, computeColor(this.color6, f2).getRGB());
                float f28 = n4;
                float f29 = n5 - n4;
                float f30 = 4.0F;
                float f31 = f + 12.0F;
                float f32 = 7.0F;
                float f33 = f29 - f32;
                float f34 = (float)computeDouble((classicComponent.computeDouble3() - classicComponent.computeDouble()) / (classicComponent.computeDouble4() - classicComponent.computeDouble()));
                float f35 = this.classicComponent == classicComponent ? f34 : this.computeFloat(this.lookupMap4, setting, f34, 34.0F);
                if (this.classicComponent == classicComponent) {
                    this.lookupMap4.put(setting, f34);
                }

                float f36 = f28 + f33 * f35;
                float f37 = f36 + f32 / 2.0F;
                float f38 = f31 + f30 / 2.0F - f32 / 2.0F;
                LocationHelper.handleFloat2(f28, f31, f29, f30, f30 / 2.0F, computeColor(color3, f2));
                LocationHelper.handleFloat2(f28, f31, Math.max(f30, f37 - f28), f30, f30 / 2.0F, computeColor(this.color6, f2));
                LocationHelper.handleFloat2(
                    f36 - 1.0F, f38 - 1.0F, f32 + 2.0F, f32 + 2.0F, (f32 + 2.0F) / 2.0F, computeColor(this.color6, f2 * 0.35F)
                );
                LocationHelper.handleFloat2(f36, f38, f32, f32, f32 / 2.0F, computeColor(Color.WHITE, f2));
                this.handleFloat(n4, f, 98.0F, 19.0F, classicComponent, f28, f33, () -> {
                    this.numberSetting = (NumberSetting)setting;
                    this.text2 = String.valueOf(classicComponent.computeDouble3());
                    this.flag7 = true;
                });
                return f + 19.0F;
            }
            return f;
        }
        DrawSettingLabelScreen.Entry2 entry2 = this.computeEntry2(setting);
        this.drawSettingLabel(
            setting,
            n4 + f6 * 0.5F,
            f + 3.0F,
            n5 - 14 - 4 - n4,
            flag2,
            computeColor(computeColor2(color5, color4, entry2.factor), f2).getRGB()
        );
        LocationHelper.handleFloat2(
            n5 - 14, f + 2.0F, 14.0F, 8.0F, 2.0F, computeColor(new Color(-16777216 + (this.computeInt(setting) & ~-16777216), true), f2)
        );
        this.handleFloat(n4, f, 98.0F, 12.0F, () -> entry2.flag = !entry2.flag, () -> entry2.flag = !entry2.flag);
        float f39 = f + 12.0F;
        if (entry2.factor > 0.01F) {
            float f40 = 57.0F * entry2.factor;
            float f41 = Math.max(f39, this.factor15);
            float f42 = Math.min(f39 + f40, this.factor16);
            boolean flag5 = f42 > f41;
            if (flag5) {
                this.handleFloat(n, f41, 114.0F, f42 - f41);
                float f43 = n4;
                float f44 = n5 - n4;
                float f45 = f39 + 3.0F;
                float f46 = 42.0F;
                int n6 = Math.max(1, (int)f44);
                int n7 = Math.max(1, (int)f46);

                for (int i = 0; i < n6; i++) {
                    float f47 = n6 > 1 ? (float)i / (n6 - 1) : 1.0F;
                    Gui.drawRect((int)f43 + i, (int)f45, (int)f43 + i + 1, (int)f45 + n7, Color.HSBtoRGB(entry2.factor2, f47, 1.0F));
                }

                for (int j = 0; j < n7; j++) {
                    float f48 = n7 > 1 ? (float)j / (n7 - 1) : 1.0F;
                    Gui.drawRect((int)f43, (int)f45 + j, (int)f43 + n6, (int)f45 + j + 1, (int)(f48 * 255.0F) << 24);
                }

                float f49 = f43 + entry2.factor3 * f44;
                float f50 = f45 + (1.0F - entry2.factor4) * f46;
                LocationHelper.handleFloat2(f49 - 3.0F, f50 - 3.0F, 6.0F, 6.0F, 3.0F, computeColor(new Color(0, 0, 0, 170), f2));
                LocationHelper.handleFloat2(f49 - 2.0F, f50 - 2.0F, 4.0F, 4.0F, 2.0F, computeColor(Color.WHITE, f2));
                this.handleEntry2(entry2, 1, f43, f45, f44, f46);
                float f51 = f45 + f46 + 3.0F;
                int n8 = Math.max(1, (int)f44);

                for (int k = 0; k < n8; k++) {
                    float f52 = n8 > 1 ? (float)k / (n8 - 1) : 1.0F;
                    Gui.drawRect((int)f43 + k, (int)f51, (int)f43 + k + 1, (int)f51 + 6, Color.HSBtoRGB(f52, 1.0F, 1.0F));
                }

                float f53 = f43 + entry2.factor2 * f44;
                LocationHelper.handleFloat2(f53 - 1.5F, f51 - 1.0F, 3.0F, 8.0F, 1.5F, computeColor(Color.WHITE, f2));
                this.handleEntry2(entry2, 2, f43, f51, f44, 6.0F);
                this.handleFloat(n, this.factor15, 114.0F, this.factor16 - this.factor15);
                BindTextureHelper.resetColor();
            }

            f39 += f40;
        }

        return f39;
    }

    private void handleFloat(float f, float f2, float f3, float f4) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScaledResolution sr = new ScaledResolution(mc);
        double d = sr.getScaleFactor() * this.factor17;
        double d2 = sr.getScaledHeight() / this.factor17;
        double d3 = Math.max(0.0, f4 * d);
        double d4 = (d2 - f2) * d;
        GL11.glScissor((int)(f * d), (int)(d4 - d3), Math.max(0, (int)(f3 * d)), Math.max(0, (int)d3));
    }

    private ClassicComponent sliderFor(Setting setting) {
        return setting instanceof NumberSetting ? this.lookupMap7.computeIfAbsent(setting, setting2 -> new ClassicComponent((NumberSetting)setting2)) : null;
    }

    private List<DrawSettingLabelScreen.Entry3> computeList() {
        return this.flag ? this.entries2 : this.entries;
    }

    private void handleFloat2(float f, float f2, float f3, float f4, Runnable task, Runnable task2) {
        if (this.flag6) {
            DrawSettingLabelScreen.Entry5 entry5 = new DrawSettingLabelScreen.Entry5();
            entry5.x = f;
            entry5.y = f2;
            entry5.w = f3;
            entry5.factor = f4;
            entry5.runnable = task;
            entry5.runnable2 = task2;
            this.entries3.add(entry5);
        }
    }

    private static boolean matchesInt(int n, int n2, float f, float f2, float f3, float f4) {
        return n >= f && n <= f + f3 && n2 >= f2 && n2 <= f2 + f4;
    }

    private void drawScriptsBody(DrawSettingLabelScreen.Entry3 entry3, int n, float f, int n2, int n3, float f2) {
        float f3 = 57.0F;
        if (this.matchesFloat(f)) {
            float f4 = n + this.factor18 * f3;
            Gui.drawRect((int)f4, (int)f, (int)(f4 + f3), (int)(f + 14.0F), computeColor(this.color6, f2 * 0.38F).getRGB());

            for (int i = 0; i < 2; i++) {
                float f5 = n + i * f3;
                boolean flag = n2 >= f5 && n2 <= f5 + f3 && n3 >= f && n3 <= f + 14.0F && this.matchesInt(n3);
                String str = i == 0 ? "Local" : "Cloud";
                float f6 = ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str);
                float f7 = 1.0F - Math.min(1.0F, Math.abs(this.factor18 - i));
                Color color = this.computeColor(color5, color4, f7);
                ClassicFontRegistry.drawStringWithShadowComponent
                    .computeInt(str, f5 + (f3 - f6) / 2.0F, f + 3.0F, computeColor(color, flag ? Math.min(1.0F, f2 + 0.1F) : f2).getRGB());
                final int var9Final = i;
                this.handleFloat(f5, f, f3, 14.0F, () -> {
                    if (this.count19 != var9Final) {
                        this.count19 = var9Final;
                        this.timestamp8 = System.currentTimeMillis();
                    }

                    if (var9Final == 1 && this.entries5.isEmpty()) {
                        this.w();
                    }
                }, null);
            }
        }

        f += 14.0F;
        float f8 = clamp((float)(System.currentTimeMillis() - this.timestamp8) / 130.0F, 0.0F, 1.0F);
        float f9 = computeFloat(f8);
        float f10 = (1.0F - f9) * (this.count19 == 1 ? 12.0F : -12.0F);
        float f11 = f2 * (0.15F + 0.85F * f9);
        float f12 = n + 6 + f10;
        int rgb = computeColor(color4, f11).getRGB();
        int rgb2 = computeColor(color5, f11).getRGB();
        float f13 = this.computeFloat("Scripting API", () -> {
            if (mc.thePlayer != null) {
                // VENDOR-DISABLED: MessageUtils.x("Learn the API to start scripting: <vendor-host>");
            }
        }, n, f, n2, n3, f11, f10, computeColor(this.color6, f11).getRGB());
        if (this.matchesFloat(f13)) {
            this.handleString("scripts-refresh", "Refresh", n, f13, 57.0F, 14.0F, n2, n3, f11, f10);
            this.handleString("scripts-folder", "Folder", n + 57.0F, f13, 57.0F, 14.0F, n2, n3, f11, f10);
            this.handleFloat(n, f13, 57.0F, 14.0F, () -> {
                if (this.count19 == 1) {
                    this.w();
                } else {
                    Brutality.scriptManager.reloadAll();
                    entry3.entries.clear();

                    for (Module module : ModuleManager.getEntries2()) {
                        if (module instanceof ScriptModule) {
                            entry3.entries.add(new DrawSettingLabelScreen.Entry4(module));
                        }
                    }
                }
            }, null);
            this.handleFloat(n + 57, f13, 57.0F, 14.0F, () -> {
                try {
                    File file = new File(Brutality.scriptManager.text);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    Desktop.getDesktop().open(file);
                } catch (Exception ignored) {
                }
            }, null);
        }

        float f14 = f13 + 14.0F;
        if (this.count19 == 1) {
            String str2 = this.text7;
            if (!str2.isEmpty()) {
                if (this.matchesFloat(f14)) {
                    ClassicFontRegistry.drawStringWithShadowComponent.computeInt(str2, f12, f14 + 3.0F, rgb2);
                }

                f14 += 14.0F;
            }

            if (this.entries5.isEmpty()) {
                if (this.matchesFloat(f14)) {
                    ClassicFontRegistry.drawStringWithShadowComponent.computeInt("No scripts", f12, f14 + 3.0F, rgb2);
                }

                f14 += 14.0F;
            }

            // VENDOR-DISABLED: BrutalityCloudClient.Entry3 cloud scripts list
        } else {
            for (DrawSettingLabelScreen.Entry4 entry4 : this.computeList(entry3)) {
                f14 = this.drawEntry(entry4, n, f14, n2, n3, f2);
            }
        }
    }

    public void handleSetting(Setting setting, int n) {
        if (setting instanceof ColorThemeSetting colorThemeSetting) {
            int n2 = colorThemeSetting.getDefaultColor() >>> 24;
            if (n2 == 0) {
                n2 = 255;
            }

            colorThemeSetting.setDefaultColor((16777215 | ~n) - ~n + (n2 << 24 & ~((16777215 | ~n) - ~n)));
        } else if (setting instanceof ColorSetting colorSetting) {
            float[] floats = Color.RGBtoHSB(n >>> 16 & 0xFF, n >>> 8 & 0xFF, (0xFF | ~n) - ~n, null);
            colorSetting.handleFloat3(floats[0]);
            colorSetting.handleFloat4(floats[1]);
            colorSetting.handleFloat2(floats[2]);
        }
    }

    private void handleEntry(DrawSettingLabelScreen.Entry entry, int n, int n2) {
        DrawSettingLabelScreen.Entry3 entry3 = entry.entry3;
        float f = entry.factor3;
        int count = entry.count;
        float factor = entry.factor;
        int n3 = entry.count2;
        this.color6 = this.computeColor(entry.count3 * 2);
        int n4 = 18 + n3;
        this.handleFloat2(count, factor, 114.0F, n4, 7.0F, f);
        LocationHelper.handleFloat2(count, factor, 114.0F, n4, 7.0F, computeColor(color, f));
        this.handleFloat(count, factor, 114.0F, 18.0F);
        LocationHelper.handleFloat2(count, factor, 114.0F, 25.0F, 7.0F, computeColor(color2, f));
        BindTextureHelper.process();
        String str = String.valueOf(this.computeChar(entry3.text));
        float f2 = ClassicFontRegistry.drawStringWithShadowComponent4.computeFloat(str);
        float f3 = ClassicFontRegistry.drawStringWithShadowComponent4.computeFloat2(str);
        float f4 = ClassicFontRegistry.drawStringWithShadowComponent3.computeFloat(entry3.text);
        float f5 = 4.0F;
        float f6 = count + (114.0F - (f2 + f5 + f4)) / 2.0F;
        ClassicFontRegistry.drawStringWithShadowComponent4
            .computeInt(str, f6, factor + (18.0F - f3) / 2.0F, computeColor(this.color6, f).getRGB());
        ClassicFontRegistry.drawStringWithShadowComponent3
            .computeInt(entry3.text, f6 + f2 + f5, factor + 4.5F, computeColor(color4, f).getRGB());
        float f7 = factor + 18.0F;
        entry3.factor = this.computeFloat(entry3.factor, entry3.factor2, 26.0F);
        entry3.factor2 = clamp(entry3.factor2, 0.0F, Math.max(0, (int)entry.factor2 - n3));
        this.factor12 = f7;
        this.factor13 = f7 + n3;
        this.handleFloat(count, f7, 114.0F, n3);
        float f8 = f7 - entry3.factor;
        if (entry3.text.equals("Config")) {
            this.handleInt(count, f8, n, n2, f);
        } else if (entry3.text.equals("Scripts")) {
            this.drawScriptsBody(entry3, count, f8, n, n2, f);
        } else {
            if (entry3.flag) {
                f8 = this.computeFloat2(count, f8, n, n2, f);
            }

            if (entry3.flag2) {
                f8 = this.computeFloat(count, f8, n, n2, f);
            }

            for (DrawSettingLabelScreen.Entry4 entry4 : this.computeList(entry3)) {
                f8 = this.drawEntry(entry4, count, f8, n, n2, f);
            }
        }

        BindTextureHelper.process();
    }

    // VENDOR-DISABLED: computeString(BrutalityCloudClient.Entry3)

    private void w() {
        // VENDOR-DISABLED: BrutalityCloudClient.listAsync(SCRIPT)
        this.text7 = "Offline";
    }

    private static Color computeColor2(Color color, Color color2, float f) {
        f = Math.max(0.0F, Math.min(1.0F, f));
        return new Color(
            (int)(color.getRed() + (color2.getRed() - color.getRed()) * f),
            (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f),
            (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f)
        );
    }

    public List<Setting> computeList(DrawSettingLabelScreen.Entry4 entry4, String str) {
        List list = new ArrayList();

        for (Setting setting : entry4.computeList2(this)) {
            if (this.computeString(setting, entry4.module).equals(str)) {
                list.add(setting);
            }
        }

        return list;
    }

    private void handleList(
        List<DrawSettingLabelScreen.Entry> list, List<DrawSettingLabelScreen.Entry6> entry6s, int n, int n2, int n3, int n4, float f
    ) {
        if (!(f <= 0.01F)) {
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

                    for (DrawSettingLabelScreen.Entry entry : list) {
                        if (entry.factor3 > 0.01F) {
                            LocationHelper.handleFloat2(
                                entry.count * this.factor17,
                                entry.factor * this.factor17,
                                114.0F * this.factor17,
                                (18 + entry.count2) * this.factor17,
                                7.0F * this.factor17,
                                computeColor(Color.WHITE, entry.factor3)
                            );
                        }
                    }

                    for (DrawSettingLabelScreen.Entry6 entry6 : entry6s) {
                        if (entry6.factor2 > 0.01F) {
                            LocationHelper.handleFloat2(
                                entry6.count2 * this.factor17,
                                entry6.factor * this.factor17,
                                118.0F * this.factor17,
                                34.0F * this.factor17,
                                6.0F * this.factor17,
                                computeColor(Color.WHITE, entry6.factor2)
                            );
                        }
                    }

                    LocationHelper.handleFloat2(
                        n * this.factor17,
                        n2 * this.factor17,
                        n3 * this.factor17,
                        n4 * this.factor17,
                        7.0F * this.factor17,
                        computeColor(Color.WHITE, f)
                    );
                    framebuffer.unbindFramebuffer();
                    mc.getFramebuffer().bindFramebuffer(true);
                    DualKawaseBlurShader.handleInt(framebuffer.framebufferTexture, 2, 2);
                    BindTextureHelper.resetColor();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private float computeFloat(Object obj, float f) {
        float orDefault = this.lookupMap.getOrDefault(obj, f);
        orDefault = this.computeFloat(orDefault, f, 24.0F);
        this.lookupMap.put(obj, orDefault);
        return orDefault;
    }

    private float computeFloat2(int n, float f, int n2, int n3, float f2) {
        boolean flag = this.matchesInt(n2, n3, n, f);
        float f3 = this.computeFloat("pit-enter", flag ? 1.0F : 0.0F, 30.0F);
        this.handleFloat(n, f, 114.0F, 14.0F, f3, f2);
        ClassicFontRegistry.drawStringWithShadowComponent.computeInt("PAGE >", n + 6 + f3, f + 3.0F, computeColor(this.color6, f2).getRGB());
        this.handleFloat(n, f, 114.0F, 14.0F, () -> this.handleFlag2(true), null);
        return f + 14.0F;
    }

    private void handleEntry4(DrawSettingLabelScreen.Entry4 entry4) {
        entry4.flag = !entry4.flag;
    }

    private static double computeDouble(double d) {
        return Math.max(0.0, Math.min(1.0, d));
    }

    private void x() {
        this.flag10 = true;
        this.text5 = "";
        this.text4 = null;
    }

    private void y() {
        this.entries6 = null;
    }

    public void handleSetting(Setting setting, List<Setting> settings) {
        if (setting != null && setting.isVisible()) {
            if (!settings.contains(setting)) {
                settings.add(setting);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.numberSetting != null) {
            if (keyCode == 1) {
                this.handleFlag(false);
            } else if (keyCode == 28 || keyCode == 156) {
                this.handleFlag(true);
            } else if (isCtrlKeyDown() && keyCode == 30) {
                this.flag7 = true;
            } else if (isCtrlKeyDown() && keyCode == 46) {
                if (this.flag7) {
                    setClipboardString(this.text2);
                }
            } else {
                if (isCtrlKeyDown() && keyCode == 45) {
                    if (this.flag7) {
                        setClipboardString(this.text2);
                        this.text2 = "";
                    }
                } else if (isCtrlKeyDown() && keyCode == 47) {
                    String clipboardString = getClipboardString();
                    if (clipboardString.matches("[0-9eE+\\-.]*") && clipboardString.length() + (this.flag7 ? 0 : this.text2.length()) <= 32) {
                        this.text2 = (this.flag7 ? "" : this.text2) + clipboardString;
                    }
                } else if ((keyCode == 14 || keyCode == 211) && this.flag7) {
                    this.text2 = "";
                } else if (keyCode == 14 && !this.text2.isEmpty()) {
                    this.text2 = this.text2.substring(0, this.text2.length() - 1);
                } else if ((Character.isDigit(typedChar) || ".-+eE".indexOf(typedChar) >= 0) && this.text2.length() < 32) {
                    this.text2 = (this.flag7 ? "" : this.text2) + typedChar;
                }

                this.flag7 = false;
            }
        } else if (this.stringSetting != null) {
            if (keyCode != 1 && keyCode != 28) {
                String str = this.stringSetting.getValue() == null ? "" : this.stringSetting.getValue();
                if (isCtrlKeyDown() && keyCode == 30) {
                    this.flag8 = true;
                } else if (isCtrlKeyDown() && keyCode == 46) {
                    if (this.flag8) {
                        setClipboardString(str);
                    }
                } else {
                    if (isCtrlKeyDown() && keyCode == 45) {
                        if (this.flag8) {
                            setClipboardString(str);
                            this.stringSetting.setValue("");
                        }
                    } else if (isCtrlKeyDown() && keyCode == 47) {
                        this.stringSetting.setValue((this.flag8 ? "" : str) + ChatAllowedCharacters.filterAllowedCharacters(getClipboardString()));
                    } else if ((keyCode == 14 || keyCode == 211) && this.flag8) {
                        this.stringSetting.setValue("");
                    } else if (keyCode == 14 && !str.isEmpty()) {
                        this.stringSetting.setValue(str.substring(0, str.length() - 1));
                    } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        this.stringSetting.setValue((this.flag8 ? "" : str) + typedChar);
                    }

                    this.flag8 = false;
                    this.handleSetting(this.stringSetting);
                }
            } else {
                this.stringSetting = null;
            }
        } else if (this.flag10) {
            if (keyCode == 28 || keyCode == 156) {
                String str2 = this.text5.trim();
                if (!str2.isEmpty()) {
                    Brutality.appdataHandler.matchesString2(str2);
                    this.y();
                }

                this.flag10 = false;
                this.text5 = "";
            } else if (keyCode == 1) {
                this.flag10 = false;
                this.text5 = "";
            } else if (keyCode == 14) {
                if (!this.text5.isEmpty()) {
                    this.text5 = this.text5.substring(0, this.text5.length() - 1);
                }
            } else if ((Character.isLetterOrDigit(typedChar) || typedChar == '_' || typedChar == '-') && this.text5.length() < 24) {
                this.text5 = this.text5 + typedChar;
            }
        } else if (this.entry4 == null) {
            if (keyCode == 1) {
                this.process2();
            } else if (keyCode == 14) {
                if (!this.text3.isEmpty()) {
                    this.text3 = this.text3.substring(0, this.text3.length() - 1);
                }
            } else {
                if (typedChar >= ' ' && typedChar < 127 && this.text3.length() < 24) {
                    this.text3 = this.text3 + typedChar;
                }
            }
        } else {
            if (keyCode != 1 && keyCode != 14) {
                this.entry4.module.handleInt4(keyCode);
            } else {
                this.entry4.module.handleInt4(this.entry4.module instanceof ClickGUI ? 54 : 0);
            }

            this.entry4 = null;
        }
    }

    private void drawSettingLabel(Setting setting, float f, float f2, int n, boolean flag, int n2) {
        int n3 = Math.max(1, n);
        String str = this.computeString(setting);
        float f3 = ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str);
        if (f3 <= n3) {
            this.lookupMap5.remove(setting);
            ClassicFontRegistry.drawStringWithShadowComponent.computeInt(str, f, f2, n2);
        } else if (!flag) {
            this.lookupMap5.remove(setting);
            ClassicFontRegistry.drawStringWithShadowComponent.computeInt(this.computeString(str, n3), f, f2, n2);
        } else {
            long l = this.lookupMap5.computeIfAbsent(setting, setting2 -> System.currentTimeMillis());
            int n4 = Math.max(1, Math.round(f3 - n3));
            long l2 = Math.max(1L, n4 * 35L);
            long l3 = (System.currentTimeMillis() - l) % (l2 * 2L + 1000L);
            float f4 = l3 < 500L
                ? 0.0F
                : (
                    l3 < l2 + 500L
                        ? (float)((l3 - 500L) * n4) / (float)l2
                        : (l3 < l2 + 1000L ? n4 : n4 - (float)((l3 - l2 - 1000L) * n4) / (float)l2)
                );
            this.handleFloat(f, f2 - 2.0F, n3, 12.0F);
            ClassicFontRegistry.drawStringWithShadowComponent.computeInt(str, f - f4, f2, n2);
            this.handleFloat(this.factor14, this.factor15, 114.0F, this.factor16 - this.factor15);
        }
    }

    private void handleString(String str, String str2, float f, float f2, float f3, float f4, int n, int n2, float f5, float f6) {
        boolean flag = matchesInt(n, n2, f, f2, f3, f4) && this.matchesInt(n2);
        float f7 = this.computeFloat(str, flag ? 1.0F : 0.0F, 30.0F);
        this.handleFloat(f, f2, f3, f4, f7, f5);
        ClassicFontRegistry.drawStringWithShadowComponent
            .computeInt(
                this.computeString2(str2, (int)f3 - 6 - 7),
                f + 6.0F + f6 + f7,
                f2 + (f4 - 8.0F) / 2.0F,
                computeColor(this.color6, f5).getRGB()
            );
    }

    private void handleFloat2(float f, float f2, float f3, float f4, float f5, float f6) {
        LocationHelper.handleFloat(f, f2, f3, f4, f5, computeColor(new Color(0, 0, 0, 18), f6));
    }

    private float computeFloat(String str, Runnable task, Runnable task2, int n, float f, int n2, int n3, float f2, float f3, int n4) {
        if (this.matchesFloat(f)) {
            boolean flag = this.matchesInt(n2, n3, n, f);
            float f4 = this.computeFloat("config-row-" + this.count18 + "-" + str, flag ? 1.0F : 0.0F, 30.0F);
            this.handleFloat(n, f, 114.0F, 14.0F, f4, f2);
            String str2 = this.computeString2(str, 101.0F);
            ClassicFontRegistry.drawStringWithShadowComponent.computeInt(str2, n + 6 + f3 + f4, f + 3.0F, n4);
            this.handleFloat(n, f, 114.0F, 14.0F, task, task2);
        }

        return f + 14.0F;
    }

    private String computeString(String str, int n) {
        if (ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str) <= n) {
            return str;
        }
        while (str.length() > 1 && ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str + "..") > n) {
            str = str.substring(0, str.length() - 1);
        }

        return str + "..";
    }

    private String computeString2(String str, float f) {
        if (ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str) <= f) {
            return str;
        }
        while (str.length() > 1 && ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str + "..") > f) {
            str = str.substring(0, str.length() - 1);
        }

        return str + "..";
    }

    private void handleList(List<DrawSettingLabelScreen.Entry6> entry6s, int n, int n2) {
        for (DrawSettingLabelScreen.Entry6 entry6 : entry6s) {
            if (!(entry6.factor2 <= 0.01F)) {
                float f = entry6.count2;
                float factor = entry6.factor;
                float f2 = entry6.factor2;
                boolean flag = matchesInt(n, n2, f, factor, 118.0F, 34.0F);
                float f3 = this.computeFloat("quick-card-" + entry6.count, flag ? 1.0F : 0.0F, 30.0F);
                Color color3 = this.computeColor(12 + entry6.count * 2);
                this.handleFloat2(f, factor, 118.0F, 34.0F, 6.0F, f2 * 0.75F);
                if (f3 > 0.01F) {
                    LocationHelper.handleFloat2(f - 1.0F, factor - 1.0F, 120.0F, 36.0F, 7.0F, computeColor(color3, f2 * f3 * 0.24F));
                }

                LocationHelper.handleFloat2(f, factor, 118.0F, 34.0F, 6.0F, computeColor(computeColor2(color, color2, 0.22F + f3 * 0.16F), f2));
                this.handleFloat(f, factor + 21.0F, 118.0F, 13.0F);
                LocationHelper.handleFloat2(f, factor, 118.0F, 34.0F, 6.0F, computeColor(new Color(7, 9, 17, 178), f2));
                BindTextureHelper.process();
                String str = String.valueOf(this.computeChar(entry6.count));
                float f4 = ClassicFontRegistry.drawStringWithShadowComponent4.computeFloat(str);
                float f5 = ClassicFontRegistry.drawStringWithShadowComponent4.computeFloat2(str);
                LocationHelper.handleFloat2(f + 5.0F, factor + 4.0F, 15.0F, 14.0F, 4.0F, computeColor(color3, f2 * (0.18F + f3 * 0.14F)));
                ClassicFontRegistry.drawStringWithShadowComponent4
                    .computeInt(str, f + 12.5F - f4 / 2.0F, factor + 11.0F - f5 / 2.0F, computeColor(color3, f2).getRGB());
                String str2 = this.computeString(entry6.count);
                ClassicFontRegistry.drawStringWithShadowComponent3
                    .computeInt(str2, f + 25.0F + f3 * 0.75F, factor + 6.0F, computeColor(color4, f2).getRGB());
                String str3 = this.computeString3(entry6.count);
                float f6 = f + 7.0F;
                if (entry6.count == 3) {
                    BrutalityHelper brutalityHelper = this.computeBrutalityHelper();
                    LocationHelper.handleFloat2(f + 7.0F, factor + 24.0F, 8.0F, 4.0F, 1.0F, computeColor(brutalityHelper.getColor(), f2));
                    f6 = f + 19.0F;
                }

                ClassicFontRegistry.drawStringWithShadowComponent2
                    .computeInt(
                        this.computeString(str3, 118.0F - (f6 - f) - 6.0F), f6 + f3 * 0.5F, factor + 25.0F, computeColor(color5, f2).getRGB()
                    );
                this.handleFloat2(f, factor, 118.0F, 34.0F, () -> this.handleInt(entry6.count), null);
            }
        }
    }

    private String computeString(String str, float f) {
        if (ClassicFontRegistry.drawStringWithShadowComponent2.computeFloat(str) <= f) {
            return str;
        }
        while (str.length() > 1 && ClassicFontRegistry.drawStringWithShadowComponent2.computeFloat(str + "..") > f) {
            str = str.substring(0, str.length() - 1);
        }

        return str + "..";
    }

    public float computeFloat(Setting setting) {
        if (!(setting instanceof KeybindListSetting)) {
            if (setting instanceof ModeSetting) {
                return 12.0F + ((ModeSetting)setting).computeList2().size() * 12 * this.computeFloat2(setting);
            }
            if (setting instanceof MultiBoolSetting) {
                return 12.0F + ((MultiBoolSetting)setting).getEntries().size() * 12 * this.computeFloat2(setting);
            }
            if (this.matchesSetting(setting)) {
                return 12.0F + 57.0F * this.computeEntry2(setting).factor;
            }
            return this.sliderFor(setting) != null ? 19.0F : 12.0F;
        }
        float f = 12.0F;
        if (this.lookupMap9.getOrDefault(setting, false)) {
            for (Setting setting2 : ((KeybindListSetting)setting).getEntries()) {
                if (setting2 != null && setting2.isVisible()) {
                    f += this.computeFloat(setting2);
                }
            }
        }

        return f;
    }

    private float computeFloat2(Setting setting) {
        return this.lookupMap10.getOrDefault(setting, 0.0F);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.classicComponent = null;
        this.entry5 = null;
    }

    private void handleString(String str) {
        // VENDOR-DISABLED: BrutalityCloudClient.uploadAsync(CONFIG)
        this.text6 = "Offline";
    }

    private void z() {
        // VENDOR-DISABLED: BrutalityCloudClient.listAsync(CONFIG)
        this.text6 = "Offline";
    }

    private void handleInt(int n, int n2, int n3, int n4) {
        int dWheel = Mouse.getDWheel();
        if (dWheel != 0) {
            int n5 = dWheel > 0 ? -26 : 26;
            var entry3s = this.computeList();
            int size = entry3s.size();
            int n6 = size * 114 + (size - 1) * 4;
            int n7 = (n3 - n6) / 2;

            for (int i = 0; i < size; i++) {
                int n8 = n7 + i * 118;
                if (n >= n8 && n <= n8 + 114 && n2 >= 18) {
                    ((DrawSettingLabelScreen.Entry3)entry3s.get(i)).factor2 += n5;
                    return;
                }
            }
        }
    }

    private void handleFlag2(boolean flag) {
        if (!this.flag2 && this.flag != flag) {
            this.flag3 = this.flag;
            this.flag4 = flag;
            this.flag = flag;
            this.timestamp2 = System.currentTimeMillis();
            this.flag2 = true;
            this.timestamp3 = 0L;
            this.text3 = "";
            this.process3();
            this.classicComponent = null;
            this.entry5 = null;
        }
    }

    private void handleInt(int n, float f, int n2, int n3, float f2) {
        float f3 = 57.0F;
        if (this.matchesFloat(f)) {
            float f4 = n + this.factor8 * f3;
            Gui.drawRect((int)f4, (int)f, (int)(f4 + f3), (int)(f + 14.0F), computeColor(this.color6, f2 * 0.38F).getRGB());

            for (int i = 0; i < 2; i++) {
                float f5 = n + i * f3;
                boolean flag = n2 >= f5 && n2 <= f5 + f3 && n3 >= f && n3 <= f + 14.0F && this.matchesInt(n3);
                String str = i == 0 ? "Local" : "Cloud";
                float f6 = ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str);
                float f7 = 1.0F - Math.min(1.0F, Math.abs(this.factor8 - i));
                Color color = this.computeColor(color5, color4, f7);
                ClassicFontRegistry.drawStringWithShadowComponent
                    .computeInt(str, f5 + (f3 - f6) / 2.0F, f + 3.0F, computeColor(color, flag ? Math.min(1.0F, f2 + 0.1F) : f2).getRGB());
                final int var8Final = i;
                this.handleFloat(f5, f, f3, 14.0F, () -> {
                    if (this.count18 != var8Final) {
                        this.count18 = var8Final;
                        this.timestamp7 = System.currentTimeMillis();
                    }

                    if (var8Final == 1 && this.entries4.isEmpty()) {
                        this.z();
                    }
                }, null);
            }
        }

        float f8 = f + 14.0F;
        float f9 = clamp((float)(System.currentTimeMillis() - this.timestamp7) / 130.0F, 0.0F, 1.0F);
        float f10 = computeFloat(f9);
        float f11 = (1.0F - f10) * (this.count18 == 1 ? 12.0F : -12.0F);
        float f12 = f2 * (0.15F + 0.85F * f10);
        float f13 = n + 6 + f11;
        int rgb = computeColor(color4, f12).getRGB();
        int rgb2 = computeColor(color5, f12).getRGB();
        if (this.count18 == 1) {
            if (this.matchesFloat(f8)) {
                boolean flag2 = this.matchesInt(n2, n3, n, f8);
                float f14 = this.computeFloat("cloud-refresh", flag2 ? 1.0F : 0.0F, 30.0F);
                this.handleFloat(n, f8, 114.0F, 14.0F, f14, f2);
                ClassicFontRegistry.drawStringWithShadowComponent
                    .computeInt("Refresh", f13 + f14, f8 + 3.0F, computeColor(this.color6, f12).getRGB());
                this.handleFloat(n, f8, 114.0F, 14.0F, this::z, null);
            }

            float f15 = f8 + 14.0F;
            String str2 = this.text6;
            if (!str2.isEmpty()) {
                if (this.matchesFloat(f15)) {
                    ClassicFontRegistry.drawStringWithShadowComponent.computeInt(str2, f13, f15 + 3.0F, rgb2);
                }

                f15 += 14.0F;
            }

            if (this.entries4.isEmpty()) {
                if (this.matchesFloat(f15)) {
                    ClassicFontRegistry.drawStringWithShadowComponent.computeInt("No configs", f13, f15 + 3.0F, rgb2);
                }

                f15 += 14.0F;
            }

            // VENDOR-DISABLED: BrutalityCloudClient.Entry3 cloud configs list
        } else {
            if (this.matchesFloat(f8)) {
                boolean flag3 = this.matchesInt(n2, n3, n, f8);
                float f16 = this.computeFloat("config-new", !flag3 && !this.flag10 ? 0.0F : 1.0F, 30.0F);
                this.handleFloat(n, f8, 114.0F, 14.0F, f16, f2);
                String str3 = this.flag10 ? (this.text5.isEmpty() ? "type a name..." : this.text5 + "_") : "+ New config";
                ClassicFontRegistry.drawStringWithShadowComponent
                    .computeInt(this.computeString2(str3, 101.0F), f13 + f16, f8 + 3.0F, computeColor(this.color6, f12).getRGB());
                this.handleFloat(n, f8, 114.0F, 14.0F, this::x, null);
            }

            f = f8 + 14.0F;
            if (this.matchesFloat(f)) {
                float f17 = 57.0F;
                boolean flag4 = n3 >= f && n3 <= f + 14.0F && this.matchesInt(n3);
                boolean flag5 = flag4 && n2 >= n && n2 <= n + f17;
                boolean flag6 = flag4 && n2 >= n + f17 && n2 <= n + 114;
                float f18 = this.computeFloat("local-refresh", flag5 ? 1.0F : 0.0F, 30.0F);
                float f19 = this.computeFloat("local-folder", flag6 ? 1.0F : 0.0F, 30.0F);
                this.handleFloat(n, f, f17, 14.0F, f18, f2);
                this.handleFloat(n + f17, f, f17, 14.0F, f19, f2);
                ClassicFontRegistry.drawStringWithShadowComponent
                    .computeInt("Refresh", n + 6 + f11 + f18, f + 3.0F, computeColor(this.color6, f12).getRGB());
                ClassicFontRegistry.drawStringWithShadowComponent
                    .computeInt("Folder", n + f17 + 4.0F + f11 + f19, f + 3.0F, computeColor(this.color6, f12).getRGB());
                this.handleFloat(n, f, f17, 14.0F, this::y, null);
                this.handleFloat(n + f17, f, f17, 14.0F, () -> {
                    try {
                        Desktop.getDesktop().open(Brutality.appdataHandler.computeFile());
                    } catch (Exception ignored) {
                    }
                }, null);
            }

            float f20 = f + 14.0F;
            var list = this.localConfigNames();
            if (list.isEmpty()) {
                if (this.matchesFloat(f20)) {
                    ClassicFontRegistry.drawStringWithShadowComponent.computeInt("No configs", f13, f20 + 3.0F, rgb2);
                }

                f20 += 14.0F;
            }

            for (String str4 : list) {
                int n4 = (int)f20;
                f20 = this.computeFloat(str4, () -> this.handleString3(str4), () -> {
                    if (!this.itemSet.remove(str4)) {
                        this.itemSet.add(str4);
                    }
                }, n, f20, n2, n3, f12, f11, rgb);
                if (this.itemSet.contains(str4)) {
                    String[] parts = new String[]{"Save", "Upload", "Delete"};
                    Runnable[] tasks = new Runnable[]{() -> {
                        if (!Brutality.appdataHandler.matchesString2(str4)) {
                            ModuleEnabledHandler.handleString2("Config Save Failed", "Could not save " + str4, 3000L);
                        }

                        this.y();
                    }, () -> this.handleString(str4), () -> {
                        this.handleString2(str4);
                        this.itemSet.remove(str4);
                    }};

                    for (int j = 0; j < parts.length; j++) {
                        if (this.matchesFloat(f20)) {
                            this.handleString(
                                "config-action-" + str4 + "-" + parts[j], parts[j], n + 8, f20, 98.0F, 12.0F, n2, n3, f12, f11
                            );
                            this.handleFloat(n + 8, f20, 98.0F, 12.0F, tasks[j], null);
                        }

                        f20 += 12.0F;
                    }
                }
            }
        }
    }

    private String computeString2(int n) {
        if (n == 0) {
            return "NONE";
        }
        if (n >= 0) {
            String keyName = Keyboard.getKeyName(n);
            return keyName == null ? "NONE" : keyName;
        }
        int n2 = n >= -100 && n <= -85 ? n + 100 : (n >= -5 ? ~(n - 1) - 1 : -n - 10);
        return n2 == 0 ? "LMB" : (n2 == 1 ? "RMB" : (n2 == 2 ? "MMB" : "MB" + (n2 + 1)));
    }

    private void process2() {
        if (!this.flag5) {
            this.flag5 = true;
            this.timestamp5 = System.currentTimeMillis() - (long)((1.0F - this.factor4) * 150.0F);
        }
    }

    private boolean matchesFloat(float f) {
        return f + 14.0F > this.factor12 && f < this.factor13;
    }

    private float computeFloat3(Setting setting) {
        Long val = this.lookupMap6.get(setting);
        if (val == null) {
            return 1.0F;
        }
        float f = clamp((float)(System.currentTimeMillis() - val) / 150.0F, 0.0F, 1.0F);
        if (f >= 1.0F) {
            this.lookupMap6.remove(setting);
        }

        return computeFloat(f);
    }

    private float computeFloat(String str, Runnable task, int n, float f, int n2, int n3, float f2, float f3, int n4) {
        if (this.matchesFloat(f)) {
            boolean flag = this.matchesInt(n2, n3, n, f);
            float f4 = this.computeFloat("script-row-" + str, flag ? 1.0F : 0.0F, 30.0F);
            this.handleFloat(n, f, 114.0F, 14.0F, f4, f2);
            String str2 = this.computeString2(str, 101.0F);
            ClassicFontRegistry.drawStringWithShadowComponent.computeInt(str2, n + 6 + f3 + f4, f + 3.0F, n4);
            this.handleFloat(n, f, 114.0F, 14.0F, task, null);
        }

        return f + 14.0F;
    }

    private List<DrawSettingLabelScreen.Entry6> computeList(int n, int n2, float f, int[] ints) {
        List list = new ArrayList();
        if (f <= 0.001F) {
            return list;
        }
        int n3 = ints.length * 34 + (ints.length - 1) * 6;
        int n4 = n - 118 - 5;
        int n5 = n2 - n3 - 8;

        for (int i = 0; i < ints.length; i++) {
            float f2 = i * 0.025F;
            float f3 = computeFloat(clamp((f - f2) / (1.0F - f2), 0.0F, 1.0F));
            float f4 = n5 + i * 40 + (1.0F - f3) * 7.0F;
            list.add(new DrawSettingLabelScreen.Entry6(ints[i], n4, f4, f3));
        }

        return list;
    }

    private void handleString3(String str) {
        if (Brutality.appdataHandler.matchesString(str)) {
            ModuleEnabledHandler.handleString("Config Loaded", "Loaded " + str, 2500L);
        } else {
            ModuleEnabledHandler.handleString2("Config Load Failed", "Could not load " + str, 3000L);
        }
    }

    private void handleInt(int n, float f, Color color) {
        if (f + 14.0F < this.factor13 - 0.5F) {
            Gui.drawRect(n, (int)f, n + 114, (int)(f + 14.0F), color.getRGB());
        } else {
            float f2 = this.factor13 - f;
            float f3 = Math.min(7.0F, (f2 + 7.0F) / 2.0F);
            this.handleFloat(n, f, 114.0F, f2);
            LocationHelper.handleFloat2(n, f - 7.0F, 114.0F, f2 + 7.0F, f3, color);
            this.handleFloat(n, this.factor12, 114.0F, this.factor13 - this.factor12);
        }
    }

    private void handleFlag(boolean flag) {
        if (this.numberSetting != null) {
            if (flag) {
                try {
                    double d = Double.parseDouble(this.text2);
                    if (Double.isFinite(d)) {
                        this.numberSetting.handleDouble2(d);
                        this.handleSetting(this.numberSetting);
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            this.numberSetting = null;
            this.text2 = "";
        }
    }

    private float computeFloat2(String str, Runnable task, int n, float f, int n2, int n3, float f2, float f3, int n4) {
        return this.computeFloat(str, task, null, n, f, n2, n3, f2, f3, n4);
    }

    private float computeFloat() {
        int n = 1;
        if (this.count18 == 1) {
            n++;
            if (!this.text6.isEmpty()) {
                n++;
            }

            n += this.entries4.size();
            if (this.entries4.isEmpty()) {
                n++;
            }
        } else {
            n += 2;
            int size = this.localConfigNames().size();
            n += Math.max(1, size);
        }

        return n * 14 + (this.count18 == 0 ? this.itemSet.size() * 3 * 12 : 0);
    }

    private void handleString2(String str) {
        boolean flag = false;

        try {
            File file = new File(Brutality.appdataHandler.computeFile(), str + ".json");
            flag = !file.exists() || file.delete();
        } catch (Exception ignored) {
        }

        this.y();
        if (flag) {
            ModuleEnabledHandler.handleString("Config Deleted", "Deleted " + str, 2500L);
        } else {
            ModuleEnabledHandler.handleString2("Config Delete Failed", "Could not delete " + str, 3000L);
        }
    }

    private float drawMoreButton(DrawSettingLabelScreen.Entry4 entry4, int n, float f, float f2) {
        int n2 = entry4.computeList2(this).size() - 6;
        String str = entry4.flag2 ? "- Show less" : "+ More (" + n2 + ")";
        float f3 = ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str);
        ClassicFontRegistry.drawStringWithShadowComponent
            .computeInt(str, n + (114.0F - f3) / 2.0F, f + 2.0F, computeColor(this.color6, f2).getRGB());
        this.handleFloat(n + 6, f, 102.0F, 12.0F, () -> entry4.flag2 = !entry4.flag2, null);
        return f + 12.0F;
    }

    private void handleFloat(float f, float f2, float f3, float f4, float f5, float f6) {
        if (!(f5 <= 0.01F) && !(f6 <= 0.01F)) {
            Gui.drawRect((int)f, (int)f2, (int)(f + f3), (int)(f2 + f4), computeColor(new Color(255, 255, 255, 18), f6 * f5).getRGB());
        }
    }

    private List<DrawSettingLabelScreen.Entry4> computeList(DrawSettingLabelScreen.Entry3 entry3) {
        if (this.text3.isEmpty()) {
            return entry3.entries;
        }
        String str = this.text3.toLowerCase();
        List list = new ArrayList();

        for (DrawSettingLabelScreen.Entry4 entry4 : entry3.entries) {
            if (entry4.module.getName().toLowerCase().contains(str)) {
                list.add(entry4);
            }
        }

        return list;
    }

    private boolean matchesKeybindListSetting(KeybindListSetting keybindListSetting, Setting setting) {
        for (Setting setting2 : keybindListSetting.getEntries()) {
            if (setting2 == setting) {
                return true;
            }

            if (setting2 instanceof KeybindListSetting && this.matchesKeybindListSetting((KeybindListSetting)setting2, setting)) {
                return true;
            }
        }

        return false;
    }

    // VENDOR-DISABLED: handleEntry32(BrutalityCloudClient.Entry3)

    private void handleFloat(float f, float f2, float f3, float f4, Runnable task, Runnable task2) {
        if (this.flag6) {
            if (!(f2 + f4 < this.factor12) && !(f2 > this.factor13)) {
                DrawSettingLabelScreen.Entry5 entry5 = new DrawSettingLabelScreen.Entry5();
                entry5.x = f;
                entry5.y = f2;
                entry5.w = f3;
                entry5.factor = f4;
                entry5.runnable = task;
                entry5.runnable2 = task2;
                this.entries3.add(entry5);
            }
        }
    }

    private void process() {
        this.entries2.clear();
        CombatType[] combatTypes = new CombatType[]{
            CombatType.PIT_AUTOMATION,
            CombatType.PIT_HUD,
            CombatType.PIT_SWAP,
            CombatType.PIT_UTILITY,
            CombatType.PIT_MAJOR_EVENT,
            CombatType.PIT_MINOR_EVENT,
            CombatType.PIT_PREMIUM
        };

        for (CombatType combatType : combatTypes) {
            // VENDOR-DISABLED: EntitlementState.checkState()
            if (true) {
                DrawSettingLabelScreen.Entry3 entry3 = new DrawSettingLabelScreen.Entry3(combatType.getLabel());

                for (Module module : combatType.getModules("", true, this.hypixelPitType)) {
                    entry3.entries.add(new DrawSettingLabelScreen.Entry4(module));
                }

                this.entries2.add(entry3);
            }
        }

        DrawSettingLabelScreen.Entry3 entry32 = new DrawSettingLabelScreen.Entry3("Pit");
        entry32.flag2 = true;
        this.entries2.add(entry32);
    }

    private boolean matchesInt(int n) {
        return n >= this.factor12 && n <= this.factor13;
    }

    private void handleInt(int n) {
        switch (n) {
            case 2:
                mc.displayGuiScreen(new DrawScriptsScreen(this, ThemesMode.STAFF));
                break;
            case 3:
            default:
                mc.displayGuiScreen(new DrawScriptsScreen(this, ThemesMode.THEMES));
                break;
            case 4:
                // VENDOR-DISABLED: mc.displayGuiScreen(new GuiCloudBrowser(this));
                break;
            case 5:
                mc.displayGuiScreen(new LatestWipeScreen(this));
                break;
            case 6:
                mc.displayGuiScreen(new ChatMacrosScreen(this));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        int scaledWidth = sr.getScaledWidth();
        int scaledHeight = sr.getScaledHeight();
        int n = Math.max(this.entries.size(), this.entries2.size());
        float f = n * 114 + (n - 1) * 4;
        this.factor17 = Math.min(1.0F, (scaledWidth - 12.0F) / f);
        int n2 = Math.round(mouseX / this.factor17);
        int n3 = Math.round(mouseY / this.factor17);
        int n4 = Math.round(scaledWidth / this.factor17);
        int n5 = Math.round(scaledHeight / this.factor17);
        long now = System.currentTimeMillis();
        long now2 = System.nanoTime();
        if (this.timestamp6 != 0L) {
            this.factor5 = clamp((float)(now2 - this.timestamp6) / 1.0E9F, 0.004166667F, 0.05F);
        }

        this.timestamp6 = now2;
        if (this.timestamp4 == 0L) {
            this.timestamp4 = now;
        }

        if (this.flag5) {
            this.factor4 = clamp(1.0F - (float)(now - this.timestamp5) / 150.0F, 0.0F, 1.0F);
            if (this.factor4 <= 0.0F) {
                mc.displayGuiScreen(null);
                return;
            }
        } else {
            this.factor4 = clamp((float)(now - this.timestamp4) / 150.0F, 0.0F, 1.0F);
        }

        float f3 = computeFloat(this.factor4);
        this.color6 = this.computeColor(0);

        for (DrawSettingLabelScreen.Entry2 entry2 : this.lookupMap8.values()) {
            entry2.factor = this.computeFloat(entry2.factor, entry2.flag ? 1.0F : 0.0F, 22.0F);
        }

        for (Map.Entry entry : this.lookupMap10.entrySet()) {
            float f4 = this.setting == entry.getKey() ? 1.0F : 0.0F;
            entry.setValue(this.computeFloat((Float)entry.getValue(), f4, 28.0F));
        }

        this.factor8 = this.computeFloat(this.factor8, this.count18, 28.0F);
        this.factor18 = this.computeFloat(this.factor18, this.count19, 28.0F);
        if (this.classicComponent != null) {
            this.handleInt2(n2);
        }

        if (this.entry5 != null) {
            this.handleInt(n2, n3);
        }

        this.handleInt(n2, n3, n4, n5);
        List<DrawSettingLabelScreen.Entry> list = Collections.emptyList();
        List list2;
        float f5;
        if (this.flag2) {
            float f6 = clamp((float)(now - this.timestamp2) / 230.0F, 0.0F, 1.0F);
            float f7 = computeFloat2(f6);
            int n6 = this.flag4 ? 1 : -1;
            var entry3s = this.flag3 ? this.entries2 : this.entries;
            var entry3s2 = this.flag4 ? this.entries2 : this.entries;
            list = this.computeList(entry3s, n4, n5, f3, -n6 * 32.0F * f7, 1.0F - f7, 1.0F);
            list2 = this.computeList(entry3s2, n4, n5, f3, n6 * 40.0F * (1.0F - f7), f7, f6);
            f5 = this.flag4 ? 1.0F - f7 : f7;
            if (f6 >= 1.0F) {
                this.flag2 = false;
            }
        } else {
            float f8 = 1.0F;
            if (this.flag && this.timestamp3 != 0L) {
                f8 = clamp((float)(now - this.timestamp3) / 170.0F, 0.0F, 1.0F);
                if (f8 >= 1.0F) {
                    this.timestamp3 = 0L;
                }
            }

            list2 = this.computeList(this.computeList(), n4, n5, f3, 0.0F, 1.0F, f8);
            f5 = this.flag ? 0.0F : 1.0F;
        }

        short s = 240;
        byte b = 22;
        int n7 = (n4 - s) / 2;
        int n8 = n5 - 42;
        var entry6s = this.computeList(n4, n5, f3 * f5, new int[]{6, 4, 5, 2, 3});
        entry6s.addAll(this.computeList(n4, n5, f3 * (1.0F - f5), new int[]{6, 5}));
        List list3 = new ArrayList(list.size() + list2.size());
        list3.addAll(list);
        list3.addAll(list2);
        this.handleList(list3, entry6s, n7, n8, s, b, f3);
        this.entries3.clear();
        boolean flag = this.flag6;
        this.flag6 = false;
        GlStateManager.pushMatrix();
        if (this.factor17 != 1.0F) {
            GlStateManager.scale(this.factor17, this.factor17, 1.0F);
        }

        for (DrawSettingLabelScreen.Entry entry3 : list) {
            this.handleEntry(entry3, n2, n3);
        }

        this.flag6 = !this.flag2;

        for (DrawSettingLabelScreen.Entry entry4 : ((Iterable<DrawSettingLabelScreen.Entry>)(Object)(list2))) {
            this.handleEntry(entry4, n2, n3);
        }

        this.handleList(entry6s, n2, n3);
        this.drawEditGuiButton(n4, n5, n2, n3, f3);
        this.handleInt(n7, n8, s, b, n2, n3, f3);
        GlStateManager.popMatrix();
        this.flag6 = flag;
    }

    private static Color computeColor(Color color, float f) {
        int n = Math.round(color.getAlpha() * f);
        n = Math.max(4, Math.min(255, n));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), n);
    }

    private char computeChar(int n) {
        switch (n) {
            case 2:
                return 'F';
            case 3:
            default:
                return 'H';
            case 4:
                return 'L';
            case 5:
                return 'A';
            case 6:
                return 'K';
        }
    }

    private Color computeColor(Color color, Color color2, float f) {
        f = clamp(f, 0.0F, 1.0F);
        return new Color(
            (int)(color.getRed() + (color2.getRed() - color.getRed()) * f),
            (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f),
            (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f)
        );
    }

    private char computeChar(String str) {
        switch (str) {
            case "Combat":
                return 'A';
            case "Movement":
                return 'G';
            case "Player":
                return 'F';
            case "Visual":
                return 'H';
            case "HUD":
                return 'H';
            case "Automation":
                return 'K';
            case "Swap":
                return 'G';
            case "Utility":
                return 'K';
            case "Major Events":
                return 'A';
            case "Minor Events":
                return 'A';
            case "Pit":
                return 'A';
            case "Scripts":
                return 'K';
            case "Config":
                return 'L';
            default:
                return 'J';
        }
    }

    public int computeInt(Setting setting) {
        if (setting instanceof ColorThemeSetting) {
            return ((ColorThemeSetting)setting).getDefaultColor();
        }
        return setting instanceof ColorSetting ? ((ColorSetting)setting).computeInt() : Color.WHITE.getRGB();
    }

    private void drawEditGuiButton(int n, int n2, int n3, int n4, float f) {
        float f2 = 8.0F;
        float f3 = n2 - 42.0F;
        float f4 = 122.0F;
        float f5 = 34.0F;
        boolean flag = matchesInt(n3, n4, f2, f3, f4, f5);
        float f6 = this.computeFloat("edit-gui-button", flag ? 1.0F : 0.0F, 30.0F);
        Color color3 = this.computeColor(5);
        this.handleFloat2(f2, f3, f4, f5, 6.0F, f * 0.75F);
        LocationHelper.handleFloat2(f2, f3, f4, f5, 6.0F, computeColor(computeColor2(color, color2, 0.25F + f6 * 0.18F), f));
        ClassicFontRegistry.drawStringWithShadowComponent3.computeInt("Edit GUI", f2 + 12.0F, f3 + 8.0F, computeColor(color4, f).getRGB());
        ClassicFontRegistry.drawStringWithShadowComponent2
            .computeInt("Arrange HUD overlays", f2 + 12.0F, f3 + 22.0F, computeColor(color5, f).getRGB());
        this.handleFloat2(f2, f3, f4, f5, () -> mc.displayGuiScreen(new HudEditorGui()), null);
    }

    private Color computeColor(int n) {
        BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
        float f = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 700.0 + n * 0.35));
        return brutalityHelper != null ? brutalityHelper.computeColor(f) : new Color(120, 110, 255);
    }

    private float computeFloat(DrawSettingLabelScreen.Entry4 entry4, int n, float f, float f2) {
        boolean flag = this.entry4 == entry4;
        boolean flag2 = ModuleManager.matchesModule(entry4.module);
        String str = flag ? "..." : this.computeString2(entry4.module.getKey());
        ClassicFontRegistry.drawStringWithShadowComponent
            .computeInt(flag2 ? "Bind (Hidden)" : "Bind", n + 6 + 3, f + 2.0F, computeColor(flag2 ? color5 : color4, f2).getRGB());
        float f3 = ClassicFontRegistry.drawStringWithShadowComponent.computeFloat(str);
        ClassicFontRegistry.drawStringWithShadowComponent
            .computeInt(str, n + 114 - 7 - f3, f + 2.0F, computeColor(flag ? this.color6 : color5, f2).getRGB());
        this.handleFloat(n + 6, f, 102.0F, 12.0F, () -> this.entry4 = this.entry4 == entry4 ? null : entry4, () -> {
            ModuleManager.handleModule2(entry4.module);
            if (this.entry4 == entry4) {
                this.entry4 = null;
            }
        });
        return f + 12.0F;
    }

    private float drawEntry(DrawSettingLabelScreen.Entry4 entry4, int n, float f, int n2, int n3, float f2) {
        boolean flag = matchesInt(n2, n3, n, f, 114.0F, 14.0F);
        entry4.factor2 = this.computeFloat(entry4.factor2, flag ? 1.0F : 0.0F, 30.0F);
        float f3 = entry4.factor2;
        boolean flag2 = entry4.module.checkState44();
        entry4.factor3 = this.computeFloat(entry4.factor3, flag2 ? 1.0F : 0.0F, 24.0F);
        if (entry4.factor3 > 0.01F) {
            this.handleInt(n, f, computeColor(this.color6, 0.22F * f2 * entry4.factor3));
        }

        if (f3 > 0.01F) {
            this.handleInt(n, f, computeColor(Color.WHITE, 0.06F * f2 * f3 * (1.0F - entry4.factor3 * 0.5F)));
        }

        Color color = computeColor2(new Color(196, 199, 214), color4, entry4.factor3);
        ClassicFontRegistry.drawStringWithShadowComponent
            .computeInt(entry4.module.getName(), n + 6 + f3 * 0.75F, f + 2.5F, computeColor(color, f2).getRGB());
        boolean flag3 = true;
        if (flag3) {
            ClassicFontRegistry.drawStringWithShadowComponent
                .computeInt("...", n + 114 - 13, f + 2.5F - 2.0F, computeColor(computeColor2(color5, this.color6, entry4.factor), f2).getRGB());
        }

        this.handleFloat(n, f, 114.0F, 14.0F, () -> entry4.module.process28(), () -> {
            if (flag3) {
                this.handleEntry4(entry4);
            }
        });
        if (flag3) {
            this.handleFloat(n + 114 - 16, f, 16.0F, 14.0F, () -> this.handleEntry4(entry4), () -> this.handleEntry4(entry4));
        }

        float f4 = f + 14.0F;
        entry4.factor = this.computeFloat(entry4.factor, entry4.flag ? 1.0F : 0.0F, 22.0F);
        if (this.matchesEntry4(entry4)) {
            for (String str : this.computeList(entry4)) {
                float f5 = entry4.itemSet.contains(str) ? 1.0F : 0.0F;
                entry4.lookupMap.put(str, this.computeFloat(entry4.lookupMap.getOrDefault(str, 0.0F), f5, 24.0F));
            }
        }

        if (entry4.factor > 0.01F) {
            float f6 = entry4.computeFloat2(this);
            float f7 = f6 * entry4.factor;
            float f8 = Math.max(f4, this.factor12);
            float f9 = Math.min(f4 + f7, this.factor13);
            if (f9 > f8) {
                this.handleFloat(n, f8, 114.0F, f9 - f8);
                this.factor14 = n;
                this.factor15 = f8;
                this.factor16 = f9;
                float f10 = f2 * entry4.factor;
                float f11 = f4;
                if (!this.matchesEntry4(entry4)) {
                    for (Setting setting : entry4.computeList2(this)) {
                        f11 = this.drawSetting(setting, n, f11, n2, n3, f10);
                    }
                } else {
                    for (String str2 : this.computeList(entry4)) {
                        float f12 = this.drawSectionHeader(entry4, str2, n, f11, n2, n3, f10);
                        float orDefault = entry4.lookupMap.getOrDefault(str2, 0.0F);
                        float f13 = entry4.computeFloat(this, this.computeList(entry4, str2));
                        float f14 = f13 * orDefault;
                        if (f14 > 0.5F) {
                            float f15 = Math.max(f12, this.factor12);
                            float f16 = Math.min(f12 + f14, this.factor13);
                            if (f16 > f15) {
                                this.handleFloat(n, f15, 114.0F, f16 - f15);
                                this.factor14 = n;
                                this.factor15 = f15;
                                this.factor16 = f16;
                                float f17 = f12;

                                for (Setting setting2 : this.computeList(entry4, str2)) {
                                    f17 = this.drawSetting(setting2, n, f17, n2, n3, f10 * orDefault);
                                }

                                this.handleFloat(n, f8, 114.0F, f9 - f8);
                                this.factor14 = n;
                                this.factor15 = f8;
                                this.factor16 = f9;
                            }
                        }

                        f11 = f12 + f14;
                    }
                }

                this.computeFloat(entry4, n, f11, f10);
                this.handleFloat(n, this.factor12, 114.0F, this.factor13 - this.factor12);
            }

            f4 += f7;
        }

        return f4;
    }

    public List<String> computeList(DrawSettingLabelScreen.Entry4 entry4) {
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();

        for (Setting setting : entry4.computeList2(this)) {
            linkedHashSet.add(this.computeString(setting, entry4.module));
        }

        List list = new ArrayList();

        for (String str : stringArray2) {
            if (linkedHashSet.contains(str)) {
                list.add(str);
            }
        }

        for (String str2 : linkedHashSet) {
            if (!list.contains(str2)) {
                list.add(str2);
            }
        }

        return list;
    }

    private void handleString(String str, float f, int n) {
        this.text4 = str;
        this.flag9 = false;
        this.factor11 = 0.0F;
        this.factor9 = f;
        this.factor10 = clamp(n + 14 + 2, this.factor12, this.factor13 - 42.0F - 4.0F);
        this.flag10 = false;
    }

    private float computeFloat(String str, boolean flag, HypixelPitType hypixelPitType, int n, float f, int n2, int n3, float f2) {
        boolean flag2 = hypixelPitType != null && hypixelPitType == this.hypixelPitType;
        boolean flag3 = this.matchesInt(n2, n3, n, f);
        String str2 = flag ? "pit-back" : "pit-type-" + hypixelPitType.name();
        float f3 = this.computeFloat(str2, flag3 ? 1.0F : 0.0F, 30.0F);
        float f4 = this.computeFloat(str2 + "-active", flag2 ? 1.0F : 0.0F, 22.0F);
        if (f4 > 0.01F) {
            this.handleInt(n, f, computeColor(this.color6, f2 * 0.3F * f4));
        }

        this.handleFloat(n, f, 114.0F, 14.0F, f3 * (1.0F - f4), f2);
        ClassicFontRegistry.drawStringWithShadowComponent
            .computeInt(this.computeString2(str, 101.0F), n + 6 + f3, f + 3.0F, computeColor(!flag && !flag2 ? color5 : this.color6, f2).getRGB());
        this.handleFloat(n, f, 114.0F, 14.0F, () -> {
            if (flag) {
                this.handleFlag2(false);
            } else if (hypixelPitType != null && hypixelPitType != this.hypixelPitType) {
                this.hypixelPitType = hypixelPitType;
                this.process();
                this.timestamp3 = System.currentTimeMillis();
            }
        }, null);
        return f + 14.0F;
    }

    private DrawSettingLabelScreen.Entry2 computeEntry2(Setting setting) {
        return this.lookupMap8.computeIfAbsent(setting, setting2 -> new DrawSettingLabelScreen.Entry2(setting2));
    }

    private String computeString(Setting setting, Module module) {
        String str = this.computeString(module, setting);
        if (str != null) {
            return str;
        }
        if (this.matchesSetting(setting)) {
            return "Colors";
        }
        String str2 = setting.getName().toLowerCase();
        if (module == null
            || !module.getName().equalsIgnoreCase("KillAura")
            || !str2.contains("auto-block") && !str2.contains("blink") && !str2.equals("block-ratio")) {
            if (module != null && module.getName().equalsIgnoreCase("InvWalk")) {
                return "General";
            }
            if (str2.contains("color") || str2.contains("chroma") || str2.contains("rainbow")) {
                return "Colors";
            }
            if (str2.contains("rotation")
                || str2.contains("aim")
                || str2.contains("silent")
                || str2.contains("yaw")
                || str2.contains("pitch")
                || str2.contains("smooth")
                || str2.contains("rotate")) {
                return "Rotations";
            }
            if (str2.contains("target")
                || str2.contains("enemy")
                || str2.contains("friend")
                || str2.contains("player")
                || str2.contains("mob")
                || str2.contains("animal")
                || str2.contains("team")
                || str2.contains("bot")
                || str2.contains("self")
                || str2.contains("invis")
                || str2.contains("wall")
                || str2.contains("range")
                || str2.contains("reach")
                || str2.contains("hitbox")
                || str2.contains("priorit")
                || str2.contains("switch")
                || str2.contains("fov")
                || str2.contains("los")) {
                return "Targeting";
            }
            if (str2.contains("delay")
                || str2.contains("tick")
                || str2.contains("cps")
                || str2.contains("cooldown")
                || str2.contains("interval")
                || str2.contains("duration")
                || str2.endsWith("time")
                || str2.contains("-ms")
                || str2.contains("aps")
                || str2.contains("rate")) {
                return "Timing";
            }
            return !str2.contains("render")
                    && !str2.contains("esp")
                    && !str2.contains("chams")
                    && !str2.contains("hud")
                    && !str2.contains("nametag")
                    && !str2.contains("tracer")
                    && !str2.contains("scale")
                    && !str2.contains("opacity")
                    && !str2.contains("background")
                    && !str2.contains("shadow")
                    && !str2.contains("outline")
                    && !str2.contains("box")
                    && !str2.contains("glow")
                    && !str2.contains("size")
                    && !str2.contains("animation")
                    && !str2.contains("font")
                    && !str2.contains("bar")
                    && !str2.contains("gradient")
                    && !str2.contains("blur")
                    && !str2.contains("bloom")
                    && !str2.contains("indicator")
                    && !str2.contains("marker")
                    && !str2.contains("particle")
                    && !str2.contains("theme")
                    && !str2.contains("width")
                    && !str2.contains("height")
                    && !str2.contains("alpha")
                    && !str2.contains("visual")
                ? "General"
                : "Render";
        }
        return "Autoblock";
    }

    private BrutalityHelper computeBrutalityHelper() {
        return ClickGUI.instance != null && ClickGUI.instance.computeBrutalityHelper() != null
            ? ClickGUI.instance.computeBrutalityHelper()
            : BrutalityHelper.getInstance();
    }

    private void handleEntry2(DrawSettingLabelScreen.Entry2 entry2, int n, float f, float f2, float f3, float f4) {
        if (this.flag6) {
            if (!(f2 + f4 < this.factor12) && !(f2 > this.factor13)) {
                DrawSettingLabelScreen.Entry5 entry5 = new DrawSettingLabelScreen.Entry5();
                entry5.x = f;
                entry5.y = f2;
                entry5.w = f3;
                entry5.factor = f4;
                entry5.entry2 = entry2;
                entry5.count = n;
                this.entries3.add(entry5);
            }
        }
    }

    public DrawSettingLabelScreen() {
        ClassicFontRegistry.ensureInitialized();
        LinkedHashMap map = new LinkedHashMap();

        for (String str : stringArray) {
            map.put(str, new DrawSettingLabelScreen.Entry3(str));
        }

        for (Module module : ModuleManager.getEntries2()) {
            if (module.getCategory() != Category.PIT && module.getCategory() != Category.HIDDEN) {
                if (module.getCategory() == Category.SCRIPTS) {
                    ((DrawSettingLabelScreen.Entry3)map.get("Scripts")).entries.add(new DrawSettingLabelScreen.Entry4(module));
                } else {
                    ((DrawSettingLabelScreen.Entry3)map.get(this.computeString(module))).entries.add(new DrawSettingLabelScreen.Entry4(module));
                }
            }
        }

        Comparator<DrawSettingLabelScreen.Entry4> comparator = Comparator.comparing(entry4 -> entry4.module.getName().toLowerCase());

        for (DrawSettingLabelScreen.Entry3 entry3 : ((Iterable<DrawSettingLabelScreen.Entry3>)(Object)(map.values()))) {
            entry3.entries.sort(comparator);
            this.entries.add(entry3);
        }

        for (DrawSettingLabelScreen.Entry3 entry32 : this.entries) {
            if (entry32.text.equals("Pit")) {
                entry32.flag = true;
            }
        }

        this.process();
    }

    private void handleInt2(int n) {
        ClassicComponent classicComponent = this.classicComponent;
        if (classicComponent != null) {
            double d = Math.min(this.factor7, Math.max(0.0F, n - this.factor6));
            double d2 = d / this.factor7 * (classicComponent.computeDouble4() - classicComponent.computeDouble()) + classicComponent.computeDouble();
            double d3 = classicComponent.computeDouble2();
            if (d3 > 0.0) {
                d2 = Math.round(d2 / d3) * d3;
            }

            d2 = Math.max(classicComponent.computeDouble(), Math.min(classicComponent.computeDouble4(), computeDouble(d2, 3)));
            classicComponent.handleDouble(d2);
        }
    }

    private static double computeDouble(double d, int n) {
        return new BigDecimal(d).setScale(n, RoundingMode.HALF_UP).doubleValue();
    }

    private boolean matchesSetting(Setting setting) {
        return setting instanceof ColorThemeSetting || setting instanceof ColorSetting;
    }

    // VENDOR-DISABLED: handleEntry3(BrutalityCloudClient.Entry3)

    public boolean matchesEntry4(DrawSettingLabelScreen.Entry4 entry4) {
        return false;
    }

    private float computeFloat(float f, float f2, float f3) {
        float f4 = 1.0F - (float)Math.exp(-f3 * this.factor5);
        float f5 = f + (f2 - f) * f4;
        return Math.abs(f2 - f5) < 0.001F ? f2 : f5;
    }

    private String computeString(int n) {
        switch (n) {
            case 2:
                return "Staff Tracker";
            case 3:
            default:
                return "Themes";
            case 4:
                return "Cloud";
            case 5:
                return "Pit";
            case 6:
                return "Macros";
        }
    }

    private boolean matchesInt(int n, int n2, int n3, float f) {
        return n >= n3 && n <= n3 + 114 && n2 >= f && n2 <= f + 14.0F && n2 >= this.factor12 && n2 <= this.factor13;
    }

    private float drawSectionHeader(DrawSettingLabelScreen.Entry4 entry4, String str, int n, float f, int n2, int n3, float f2) {
        boolean flag = entry4.itemSet.contains(str);
        boolean flag2 = matchesInt(n2, n3, n, f, 114.0F, 12.0F) && this.matchesInt(n3);
        float f3 = this.computeFloat(entry4.lookupMap2.getOrDefault(str, 0.0F), flag2 ? 1.0F : 0.0F, 30.0F);
        entry4.lookupMap2.put(str, f3);
        this.handleFloat(n, f, 114.0F, 12.0F, f3, f2 * 0.75F);
        String str2 = flag ? "-" : "+";
        float orDefault = entry4.lookupMap.getOrDefault(str, 0.0F);
        ClassicFontRegistry.drawStringWithShadowComponent.computeInt(str2, n + 6 + f3, f + 2.0F, computeColor(this.color6, f2).getRGB());
        ClassicFontRegistry.drawStringWithShadowComponent3
            .computeInt(str, n + 6 + 8 + f3, f + 2.0F, computeColor(computeColor2(color5, color4, orDefault), f2).getRGB());
        this.handleFloat(n + 6, f, 102.0F, 12.0F, () -> {
            if (!entry4.itemSet.remove(str)) {
                entry4.itemSet.add(str);
            }
        }, null);
        return f + 12.0F;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static class Entry {
        public final DrawSettingLabelScreen.Entry3 entry3;
        public final int count;
        public final float factor;
        public final int count2;
        public final float factor2;
        public final float factor3;
        public final int count3;

        public Entry(DrawSettingLabelScreen.Entry3 entry3, int count, float factor, int n, float f, float f2, int n2) {
            this.entry3 = entry3;
            this.count = count;
            this.factor = factor;
            this.count2 = n;
            this.factor2 = f;
            this.factor3 = f2;
            this.count3 = n2;
        }
    }

    public class Entry2 {
        public final Setting setting;
        public boolean flag;
        public float factor;
        public float factor2;
        public float factor3;
        public float factor4;

        public Entry2(Setting setting) {
            this.setting = setting;
            int n = DrawSettingLabelScreen.this.computeInt(setting);
            float[] floats = Color.RGBtoHSB(n >> 16 & 0xFF, n >> 8 & 0xFF, n & 0xFF, null);
            this.factor2 = floats[0];
            this.factor3 = floats[1];
            this.factor4 = floats[2];
        }

        public void process() {
            DrawSettingLabelScreen.this.handleSetting(this.setting, Color.HSBtoRGB(this.factor2, this.factor3, this.factor4));
        }
    }

    public class Entry3 {
        public final String text;
        public final List<DrawSettingLabelScreen.Entry4> entries = new ArrayList<>();
        public float factor;
        public float factor2;
        public boolean flag;
        public boolean flag2;

        public Entry3(String text) {
            this.text = text;
        }
    }

    public class Entry4 {
        public final Module module;
        public boolean flag;
        public float factor;
        public float factor2;
        public float factor3;
        public boolean flag2;
        public float factor4;
        public final Set<String> itemSet = new HashSet<>();
        public final Map<String, Float> lookupMap = new HashMap<>();
        public final Map<String, Float> lookupMap2 = new HashMap<>();

        public Entry4(Module module) {
            this.module = module;
            this.factor3 = module.checkState44() ? 1.0F : 0.0F;
        }

        public List<Setting> computeList2(DrawSettingLabelScreen drawSettingLabelScreen) {
            List list = new ArrayList();
            if (Brutality.settingsManager != null) {
                for (Setting setting : Brutality.settingsManager.computeList2(this.module)) {
                    DrawSettingLabelScreen.this.handleSetting(setting, list);
                }
            }

            return list;
        }

        public float computeFloat2(DrawSettingLabelScreen drawSettingLabelScreen) {
            float f;
            if (!drawSettingLabelScreen.matchesEntry4(this)) {
                f = this.computeFloat(drawSettingLabelScreen, this.computeList2(drawSettingLabelScreen));
            } else {
                f = 0.0F;

                for (String str : drawSettingLabelScreen.computeList(this)) {
                    f += 12.0F;
                    float orDefault = this.lookupMap.getOrDefault(str, 0.0F);
                    f += this.computeFloat(drawSettingLabelScreen, drawSettingLabelScreen.computeList(this, str)) * orDefault;
                }
            }

            return f + 12.0F;
        }

        public float computeFloat(DrawSettingLabelScreen drawSettingLabelScreen) {
            return this.factor == 0.0F ? 14.0F : 14.0F + this.computeFloat2(drawSettingLabelScreen) * this.factor;
        }

        public List<Setting> computeList3(DrawSettingLabelScreen drawSettingLabelScreen) {
            var settings = this.computeList2(drawSettingLabelScreen);
            return settings.size() <= 6 ? settings : settings.subList(0, 6);
        }

        public boolean matchesDrawSettingLabelScreen(DrawSettingLabelScreen drawSettingLabelScreen) {
            return this.computeList2(drawSettingLabelScreen).size() > 6;
        }

        public float computeFloat(DrawSettingLabelScreen drawSettingLabelScreen, List<Setting> settings) {
            float f = 0.0F;

            for (Setting setting : settings) {
                f += drawSettingLabelScreen.computeFloat(setting);
            }

            return f;
        }

        public List<Setting> computeList(DrawSettingLabelScreen drawSettingLabelScreen) {
            var settings = this.computeList2(drawSettingLabelScreen);
            return (List<Setting>)(settings.size() <= 6 ? new ArrayList<>() : settings.subList(6, settings.size()));
        }
    }

    public static class Entry5 {
        public float x;
        public float y;
        public float w;
        public float factor;
        public float factor2;
        public float factor3;
        public Runnable runnable;
        public Runnable runnable2;
        public ClassicComponent classicComponent;
        public DrawSettingLabelScreen.Entry2 entry2;
        public int count;

        public boolean matchesInt(int n, int n2) {
            return n >= this.x && n <= this.x + this.w && n2 >= this.y && n2 <= this.y + this.factor;
        }
    }

    public static class Entry6 {
        public final int count;
        public final int count2;
        public final float factor;
        public final float factor2;

        public Entry6(int count, int n, float factor, float f) {
            this.count = count;
            this.count2 = n;
            this.factor = factor;
            this.factor2 = f;
        }
    }
}
