package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.event.Event;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.ArrayListHelper;
import org.brutality.utils.render.RenderHelper2;

public final class TabGUI extends Module {
    public static TabGUI instance;
    private final BooleanSetting keyboardNavigationSetting = new BooleanSetting("Keyboard Navigation", this, true);
    private final BooleanSetting dockBelowWatermarkSetting = new BooleanSetting("Dock Below Watermark", this, true);
    private final BooleanSetting blurSetting = new BooleanSetting("Blur", this, true);
    private final BooleanSetting glowSetting = new BooleanSetting("Glow", this, false);
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 0.65, 0.45, 1.25, 0.05);
    private final ModeSetting arrayListSetting = new ModeSetting("Color", this, "Theme", Arrays.asList("Theme", "ArrayList"));
    private final NumberSetting xSetting = new NumberSetting("X", this, 6.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 6.0, 0.0, 1080.0, 1.0);
    private final Map<Category, BooleanSetting> lookupMap = new EnumMap<>(Category.class);
    private int count = -1;
    private int count2;
    private int count3;
    private boolean flag;
    private float factor;
    private long timestamp;
    private int count4 = Integer.MIN_VALUE;
    private int count5;
    private long timestamp2;
    private int count6 = -1;
    private final Map<Category, List<Module>> lookupMap2 = new EnumMap<>(Category.class);
    private List<Category> entries = Collections.emptyList();
    private static final int count7 = 108;
    private static final int count8 = 17;
    private static final int count9 = 10;
    private static final long timestamp3 = 500L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && mc.thePlayer != null && event instanceof SetShaderTypeEvent setShaderTypeEvent) {
            float f = (float)this.scaleSetting.computeDouble();
            this.process();
            if (setShaderTypeEvent.getShaderType() == SetShaderTypeEvent.BlurMode.BLUR && this.blurSetting.isEnabled() && !this.computeList().isEmpty()) {
                ArrayListHelper.handleFloat(
                    (float)this.xSetting.computeDouble(),
                    (float)this.ySetting.computeDouble() + this.computeInt() * f,
                    108.0F * f,
                    (this.computeList().size() * 17 + 8) * f,
                    4.0F * f,
                    Color.WHITE
                );
                if (this.flag) {
                    var categories = this.computeList();
                    if (!categories.isEmpty()) {
                        float f2 = (float)this.xSetting.computeDouble() + 113.0F * f;
                        if (f2 + 145.0F * f > InvalidateHelper.computeScaledResolution(mc).getScaledWidth()) {
                            f2 = Math.max(0.0F, (float)this.xSetting.computeDouble() - 150.0F * f);
                        }

                        ArrayListHelper.handleFloat(
                            f2,
                            (float)this.ySetting.computeDouble() + this.computeInt() * f,
                            145.0F * f,
                            (Math.min(10, this.computeList((Category)categories.get(Math.floorMod(this.count2, categories.size()))).size()) * 17 + 8) * f,
                            4.0F * f,
                            Color.WHITE
                        );
                    }
                }
            } else if (setShaderTypeEvent.getShaderType() == SetShaderTypeEvent.BlurMode.GLOW && this.glowSetting.isEnabled() && !this.computeList().isEmpty()) {
                ArrayListHelper.handleFloat(
                    (float)this.xSetting.computeDouble() + 3.0F * f,
                    (float)this.ySetting.computeDouble() + (this.computeInt() + 4 + this.factor) * f,
                    102.0F * f,
                    16.0F * f,
                    3.0F * f,
                    ArrayListHelper.computeColor(this, this.arrayListSetting.getText())
                );
            }
        }
    }

    public int computeInt3() {
        return (int)this.xSetting.computeDouble();
    }

    public TabGUI() {
        super("TabGUI", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.keyboardNavigationSetting,
                this.dockBelowWatermarkSetting,
                this.arrayListSetting,
                this.scaleSetting,
                this.blurSetting,
                this.glowSetting,
                this.xSetting,
                this.ySetting
            }
        );

        for (Category category : Category.values()) {
            if (category != Category.HIDDEN) {
                BooleanSetting boolSetting = new BooleanSetting("Show " + computeString(category), this, true);
                this.lookupMap.put(category, boolSetting);
                this.handleSettingArray(new Setting[]{boolSetting});
            }
        }

        instance = this;
    }

    private void handleRenderHelper2(RenderHelper2 renderHelper2, Category category, float f, float f2, Color color, Color color2, ScaledResolution sr, float f3) {
        var modules = this.computeList(category);
        if (!modules.isEmpty()) {
            this.count3 = Math.floorMod(this.count3, modules.size());
            int n = Math.max(0, Math.min(this.count3 - 5, modules.size() - 10));
            int n2 = Math.min(10, modules.size());
            float f4 = 145.0F * f3;
            if (f + f4 > sr.getScaledWidth()) {
                f = Math.max(0.0F, (float)this.xSetting.computeDouble() - f4 - 5.0F * f3);
            }

            ArrayListHelper.handleFloat(f, f2, f4, (n2 * 17 + 8) * f3, 4.0F * f3);
            ArrayListHelper.handleFloat(
                f + 3.0F * f3, f2 + (4 + (this.count3 - n) * 17) * f3, f4 - 6.0F * f3, 16.0F * f3, 3.0F * f3, color, color2
            );

            for (int i = 0; i < n2; i++) {
                Module module = (Module)modules.get(n + i);
                int n3 = n + i == this.count3 ? ArrayListHelper.computeInt(color, color2) : (module.checkState44() ? -1 : -5524539);
                renderHelper2.handleString(renderHelper2.computeString(module.getName(), 120.0F * f3), f + 8.0F * f3, f2 + (4 + i * 17) * f3, 16.0F * f3, n3);
                if (module.checkState44()) {
                    ArrayListHelper.handleFloat(f + f4 - 10.0F * f3, f2 + (10 + i * 17) * f3, 3.0F * f3, 3.0F * f3, f3, color);
                }
            }
        }
    }

    @Override
    public boolean onBlurMode(SetShaderTypeEvent.BlurMode blurMode) {
        return blurMode == SetShaderTypeEvent.BlurMode.BLUR
            ? this.blurSetting.isEnabled()
            : blurMode == SetShaderTypeEvent.BlurMode.GLOW && this.glowSetting.isEnabled();
    }

    public static boolean matchesInt2(int n) {
        TabGUI tabGUI = instance;
        return tabGUI != null
            && tabGUI.checkState44()
            && tabGUI.keyboardNavigationSetting.isEnabled()
            && mc.thePlayer != null
            && mc.currentScreen == null
            && ModuleManager.computeList(n) == null
            && (n == 200 || n == 208 || n == 203 || n == 205 || n == 28 && tabGUI.flag);
    }

    public static boolean matchesInt(int n) {
        if (!matchesInt2(n)) {
            return false;
        }
        instance.handleInt(n);
        return true;
    }

    @Override
    public void process2() {
        this.flag = false;
        this.timestamp = 0L;
        this.count4 = Integer.MIN_VALUE;
    }

    public List<Category> computeList() {
        this.refreshMenu();
        return this.entries;
    }

    private int computeInt() {
        return this.dockBelowWatermarkSetting.isEnabled() && Watermark.isVisible()
            ? Math.max(0, (int)Math.ceil((Watermark.instance.computeFloat() - this.ySetting.computeDouble()) / this.scaleSetting.computeDouble()))
            : 0;
    }

    @Override
    public void onDisable() {
        this.flag = false;
        this.timestamp = 0L;
        this.count4 = Integer.MIN_VALUE;
        HudDragHelper.handleObject(this);
    }

    private void process() {
        int n = mc.timer == null ? 0 : Float.floatToIntBits(mc.timer.renderPartialTicks);
        if (mc.timer == null || this.count4 != mc.thePlayer.ticksExisted || this.count5 != n) {
            this.count4 = mc.thePlayer.ticksExisted;
            this.count5 = n;
            long now = System.nanoTime();
            float f = this.timestamp == 0L ? 0.016666668F : Math.min(0.05F, (float)(now - this.timestamp) / 1.0E9F);
            this.timestamp = now;
            this.factor = this.factor + (this.count2 * 17 - this.factor) * (1.0F - (float)Math.exp(-18.0F * f));
        }
    }

    private List<Module> computeList(Category category) {
        this.refreshMenu();
        return this.lookupMap2.getOrDefault(category, Collections.emptyList());
    }

    private void handleInt(int n) {
        var categories = this.computeList();
        if (!categories.isEmpty()) {
            this.count2 = Math.floorMod(this.count2, categories.size());
            var modules = this.computeList((Category)categories.get(this.count2));
            if (n == 203) {
                this.flag = false;
            } else if (n == 205) {
                this.flag = true;
            } else if (n == 28) {
                if (!modules.isEmpty()) {
                    ((Module)modules.get(Math.floorMod(this.count3, modules.size()))).process28();
                }
            } else {
                int n2 = n == 200 ? -1 : 1;
                if (this.flag && !modules.isEmpty()) {
                    this.count3 = Math.floorMod(this.count3 + n2, modules.size());
                } else {
                    this.count2 = Math.floorMod(this.count2 + n2, categories.size());
                    this.count3 = 0;
                }
            }
        }
    }

    @Override
    public void onRender() {
        if (this.checkState44() && mc.thePlayer != null) {
            if (this.computeFontRenderer2() != null) {
                float f = (float)this.scaleSetting.computeDouble();
                RenderHelper2 renderHelper2 = new RenderHelper2(this, 18.0F, f);
                var categories = this.computeList();
                if (!categories.isEmpty()) {
                    this.count2 = Math.floorMod(this.count2, categories.size());
                }

                int n = (int)Math.ceil((this.computeInt() + categories.size() * 17 + 8) * f);
                int n2 = (int)Math.ceil(108.0F * f);
                HudDragHelper.handleObject(this, this.xSetting, this.ySetting, n2, n);
                ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
                int n3 = Math.max(0, Math.min((int)this.xSetting.computeDouble(), sr.getScaledWidth() - n2));
                int n4 = Math.max(0, Math.min((int)this.ySetting.computeDouble(), sr.getScaledHeight() - n));
                this.xSetting.handleDouble2(n3);
                this.ySetting.handleDouble2(n4);
                Color color = ArrayListHelper.computeColor(this, this.arrayListSetting.getText());
                Color color2 = ArrayListHelper.computeColor2(this, this.arrayListSetting.getText());
                if (!categories.isEmpty()) {
                    float f2 = n4 + this.computeInt() * f;
                    ArrayListHelper.handleFloat(n3, f2, 108.0F * f, (categories.size() * 17 + 8) * f, 4.0F * f);
                    this.process();
                    ArrayListHelper.handleFloat(n3 + 3.0F * f, f2 + (4.0F + this.factor) * f, 102.0F * f, 16.0F * f, 3.0F * f, color, color2);

                    for (int i = 0; i < categories.size(); i++) {
                        String str = computeString((Category)categories.get(i));
                        float f3 = f2 + (4 + i * 17) * f;
                        int n5 = i == this.count2 ? ArrayListHelper.computeInt(color, color2) : -4735539;
                        renderHelper2.handleString(str, n3 + 8.0F * f, f3, 16.0F * f, n5);
                    }

                    if (this.flag) {
                        this.handleRenderHelper2(renderHelper2, (Category)categories.get(this.count2), n3 + 113.0F * f, f2, color, color2, sr, f);
                    }
                }
            }
        }
    }

    public int computeInt2() {
        var categories = this.computeList();
        int size = categories.size();
        if (this.flag && !categories.isEmpty()) {
            size = Math.max(size, Math.min(10, this.computeList((Category)categories.get(Math.floorMod(this.count2, categories.size()))).size()));
        }

        return (int)Math.ceil(this.ySetting.computeDouble() + (this.computeInt() + size * 17 + 8) * this.scaleSetting.computeDouble());
    }

    private void refreshMenu() {
        long now = System.currentTimeMillis();
        var modules = ModuleManager.getEntries2();
        int n = 0;

        for (Entry entry : this.lookupMap.entrySet()) {
            if (((BooleanSetting)entry.getValue()).isEnabled()) {
                n |= 1 << ((Category)entry.getKey()).ordinal();
            }
        }

        if (modules.size() != this.count6 || n != this.count || now - this.timestamp2 >= timestamp3) {
            Category category = this.entries.isEmpty() ? null : this.entries.get(Math.floorMod(this.count2, this.entries.size()));
            this.count = n;
            this.timestamp2 = now;
            this.count6 = modules.size();
            this.lookupMap2.clear();

            for (Module module : modules) {
                if (module.getCategory() != Category.HIDDEN) {
                    this.lookupMap2.computeIfAbsent(module.getCategory(), category2 -> new java.util.ArrayList<>()).add(module);
                }
            }

            this.entries = Arrays.stream(Category.values())
                .filter(category3 -> this.lookupMap2.containsKey(category3) && this.lookupMap.get(category3).isEnabled())
                .collect(Collectors.toList());
            if (category != null && this.entries.contains(category)) {
                this.count2 = this.entries.indexOf(category);
            } else {
                this.count2 = 0;
                this.count3 = 0;
                this.flag = false;
                this.factor = 0.0F;
            }

            for (List list : this.lookupMap2.values()) {
                list.sort(Comparator.comparing(Module::getName, String.CASE_INSENSITIVE_ORDER));
            }
        }
    }

    private static String computeString(Category category) {
        String str = category.name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
