package org.brutality.module.impl.visual.arraylist;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.LongToIntFunction;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.brutality.gui.font.api.BrutalityFont;
import org.brutality.gui.font.atlas.SFProApi;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.performance.InvalidateHelper;

public class ArraylistHandler {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Map<Module, ArraylistHandler.Entry> y = new HashMap<>();
    private final List<Module> entries = new ArrayList<>();
    private final Comparator<Module> comparator = Comparator.<Module>comparingInt(module -> this.y.get(module).width).reversed();
    private final ModeSetting modeSetting;
    private final NumberSetting numberSetting;
    private final NumberSetting numberSetting2;
    private final NumberSetting numberSetting3;
    private final BooleanSetting booleanSetting;
    private final NumberSetting numberSetting4;
    private final BooleanSetting booleanSetting2;
    private final NumberSetting numberSetting5;
    private final NumberSetting numberSetting6;
    private final ModeSetting modeSetting2;
    private final Function<Module, String> function;
    private final LongToIntFunction longToIntFunction;
    private final Predicate<Module> predicate;
    private BrutalityFont apiApi;
    private String text = "";

    public void clear() {
        this.y.clear();
        this.entries.clear();
    }

    private ArraylistHandler.Entry computeEntry(Module module) {
        ArraylistHandler.Entry entry = this.y.get(module);
        if (entry == null) {
            entry = new ArraylistHandler.Entry();
            this.y.put(module, entry);
        }

        return entry;
    }

    private List<Module> computeList(BrutalityFont apiApi) {
        var entries = this.entries;
        entries.clear();

        for (Module module : ModuleManager.getEntries2()) {
            if (!this.predicate.test(module) && (module.checkState44() || this.y.containsKey(module))) {
                ArraylistHandler.Entry entry = this.computeEntry(module);
                entry.text = this.function.apply(module);
                entry.width = apiApi.computeInt(entry.text);
                entries.add(module);
            }
        }

        entries.sort(this.comparator);
        return entries;
    }

    public void update() {
        BrutalityFont apiApi = this.computeApiApi();
        var modules = this.computeList(apiApi);
        float f = apiApi.getHeight() + (float)this.numberSetting3.computeDouble();
        float f2 = -apiApi.getHeight() + 1;
        double d = MathHelper.clamp_double(Minecraft.getDebugFPS() / 30.0, 1.0, 9999.0);
        double d2 = 0.2 / d;

        for (Module module : modules) {
            ArraylistHandler.Entry entry = this.computeEntry(module);
            float width = entry.width;
            if (module.checkState44()) {
                entry.offsetX = (float)(entry.offsetX + (width - entry.offsetX) * d2);
            } else {
                if (entry.offsetX < 0.0F) {
                    this.y.remove(module);
                    continue;
                }

                entry.offsetX = (float)(entry.offsetX + (-2.0F - entry.offsetX) * d2);
            }

            entry.offsetX = MathHelper.clamp_float(entry.offsetX, -2.0F, width);
            f2 += f;
            if (entry.flag) {
                entry.offsetY = (float)(entry.offsetY + (f2 - entry.offsetY) * d2);
            } else {
                entry.offsetY = f2;
                entry.flag = true;
            }
        }
    }

    private BrutalityFont computeApiApi() {
        String str = this.modeSetting.getText() + "@" + this.numberSetting.computeDouble();
        if (!str.equals(this.text) || this.apiApi == null) {
            this.apiApi = SFProApi.glyphFonts.computeNameApi(this.modeSetting.getText()).computeApiApi((float)this.numberSetting.computeDouble());
            this.text = str;
        }

        return this.apiApi;
    }

    public ArraylistHandler(
        ModeSetting modeSetting,
        NumberSetting numberSetting,
        NumberSetting numberSetting2,
        NumberSetting numberSetting3,
        BooleanSetting booleanSetting,
        NumberSetting numberSetting4,
        BooleanSetting booleanSetting2,
        NumberSetting numberSetting5,
        NumberSetting numberSetting6,
        ModeSetting modeSetting2,
        Function<Module, String> function,
        LongToIntFunction longToIntFunction,
        Predicate<Module> predicate
    ) {
        this.modeSetting = modeSetting;
        this.numberSetting = numberSetting;
        this.numberSetting2 = numberSetting2;
        this.numberSetting3 = numberSetting3;
        this.booleanSetting = booleanSetting;
        this.numberSetting4 = numberSetting4;
        this.booleanSetting2 = booleanSetting2;
        this.numberSetting5 = numberSetting5;
        this.numberSetting6 = numberSetting6;
        this.modeSetting2 = modeSetting2;
        this.function = function;
        this.longToIntFunction = longToIntFunction;
        this.predicate = predicate;
    }

    public void render() {
        BrutalityFont apiApi = this.computeApiApi();
        var modules = this.computeList(apiApi);
        if (!modules.isEmpty()) {
            float f = (float)this.numberSetting2.computeDouble();
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
            float f2 = sr.getScaledWidth() / f - ((float)this.numberSetting5.computeDouble() - 1.0F);
            float f3 = (float)this.numberSetting6.computeDouble() - 1.0F;
            GlStateManager.pushMatrix();
            GlStateManager.scale(f, f, f);
            boolean enabled = this.booleanSetting2.isEnabled();
            float f4 = 1.5F;
            float height = apiApi.getHeight();
            int rgb = new Color(0, 0, 0, (int)this.numberSetting4.computeDouble()).getRGB();
            long l = 0L;

            for (int i = 0; i < modules.size(); i++) {
                Module module = (Module)modules.get(i);
                ArraylistHandler.Entry entry = this.computeEntry(module);
                String text = entry.text;
                float f5 = entry.offsetY + f3;
                float f6 = f2 - entry.offsetX - 2.0F;
                int n = this.longToIntFunction.applyAsInt(l);
                boolean flag = i == 0;
                boolean flag2 = i == modules.size() - 1;
                if (enabled) {
                    ArraylistHandler2.handleFloat(
                        f2 - (entry.offsetX + 6.0F) - f4, f5 - (flag ? 2 : 0), f2 - (entry.offsetX + 5.0F) - f4, f5 + height + 2.0F, n
                    );
                    if (flag2) {
                        ArraylistHandler2.handleFloat(f2 - (entry.offsetX + 6.0F) - f4, f5 + height + 2.0F, f2, f5 + height + 3.0F, n);
                    } else {
                        float f7 = Math.abs(entry.width - this.y.get(modules.get(i + 1)).width);
                        ArraylistHandler2.handleFloat(
                            f2 - (entry.offsetX + 5.0F) - f4, f5 + height + 1.0F, f2 - (entry.offsetX + 5.0F) - f4 + f7, f5 + height + 2.0F, n
                        );
                    }
                }

                if (this.booleanSetting.isEnabled()) {
                    ArraylistHandler2.handleFloat(
                        f2 - (entry.offsetX + 4.0F) - f4 - (enabled ? 1 : 0), f5 - (flag ? 3 : 0), f2, f5 + height + (flag2 ? 2 : 1), rgb
                    );
                }

                apiApi.computeFloat(text, (double)(f6 - f4), (double)(f5 + 1.0F), n, true);
                l += this.modeSetting2.matchesString2("Up") ? 100L : -100L;
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    public static final class Entry {
        public String text;
        public int width;
        public float offsetX;
        public float offsetY;
        public boolean flag;
    }
}
