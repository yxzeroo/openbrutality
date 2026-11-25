package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.RenderHelper2;

public final class Watermark extends Module {
    public static Watermark instance;
    private final ModeSetting arrayListSetting = new ModeSetting("Color", this, "Theme", Arrays.asList("Theme", "ArrayList"));
    private final ModeSetting leftSetting = new ModeSetting("Wave Direction", this, "Right", Arrays.asList("Right", "Left"));
    private final NumberSetting waveSpeedSetting = new NumberSetting("Wave Speed", this, 1.0, 0.0, 5.0, 0.05);
    private final NumberSetting waveLengthSetting = new NumberSetting("Wave Length", this, 140.0, 20.0, 400.0, 5.0);
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 0.65, 0.4, 1.5, 0.05);
    private final BooleanSetting versionSetting = new BooleanSetting("Version", this, true);
    private final BooleanSetting shadowSetting = new BooleanSetting("Shadow", this, true);
    private final BooleanSetting glowSetting = new BooleanSetting("Glow", this, false);
    private final NumberSetting xSetting = new NumberSetting("X", this, 6.0, 0.0, 3840.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 6.0, 0.0, 2160.0, 1.0);

    public static Color computeColor(Color color, Color color2, double d, long l, double d2, double d3, boolean flag) {
        double d4 = l / 4000.0 * d2 % 1.0;
        double d5 = d / Math.max(1.0, d3) + (flag ? d4 : -d4);
        return computeColor(color, color2, (1.0 - Math.cos(d5 * Math.PI * 2.0)) * 0.5);
    }

    private void handleFlag(boolean flag) {
        float f = (float)this.scaleSetting.computeDouble();
        RenderHelper2 renderHelper2 = new RenderHelper2(this, 32.0F, f);
        RenderHelper2 renderHelper22 = new RenderHelper2(this, 18.0F, f);
        String str = "Brutality";
        String str2 = Brutality.version == null ? "" : Brutality.version;
        float f2 = renderHelper2.computeFloat(str);
        float f3 = 2.0F * f + f2 + 4.0F * f;
        float f4 = this.versionSetting.isEnabled() ? f3 + renderHelper22.computeFloat(str2) + 2.0F * f : f2 + 4.0F * f;
        float f5 = Math.max(renderHelper2.computeFloat(), renderHelper22.computeFloat() + f) + 2.0F * f;
        if (!flag) {
            HudDragHelper.handleObject(this, this.xSetting, this.ySetting, (int)Math.ceil(f4), (int)Math.ceil(f5));
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
            this.xSetting.handleDouble2(Math.max(0.0, Math.min(this.xSetting.computeDouble(), (double)(sr.getScaledWidth() - f4))));
            this.ySetting.handleDouble2(Math.max(0.0, Math.min(this.ySetting.computeDouble(), (double)(sr.getScaledHeight() - f5))));
        }

        float f6 = (float)this.xSetting.computeDouble();
        float f7 = (float)this.ySetting.computeDouble();
        Color[] colors = this.computeColorArray();
        long now = System.currentTimeMillis();

        for (int i = 0; i < str.length(); i++) {
            float f8 = renderHelper2.computeFloat(str.substring(0, i));
            int rgb = computeColor(
                    colors[0],
                    colors[1],
                    f8 / f,
                    now,
                    this.waveSpeedSetting.computeDouble(),
                    this.waveLengthSetting.computeDouble(),
                    this.leftSetting.matchesString2("Left")
                )
                .getRGB();
            renderHelper2.handleString(str.substring(i, i + 1), f6 + 2.0F * f + f8, f7, rgb, !flag && this.shadowSetting.isEnabled());
        }

        if (this.versionSetting.isEnabled()) {
            int rgb2 = computeColor(
                    colors[0],
                    colors[1],
                    f3 / f,
                    now,
                    this.waveSpeedSetting.computeDouble(),
                    this.waveLengthSetting.computeDouble(),
                    this.leftSetting.matchesString2("Left")
                )
                .getRGB();
            renderHelper22.handleString(str2, f6 + f3, f7 + f, rgb2, !flag && this.shadowSetting.isEnabled());
        }
    }

    public static Color computeColor(Color color, Color color2, double d) {
        return new Color(
            (int)Math.round(color.getRed() + (color2.getRed() - color.getRed()) * d),
            (int)Math.round(color.getGreen() + (color2.getGreen() - color.getGreen()) * d),
            (int)Math.round(color.getBlue() + (color2.getBlue() - color.getBlue()) * d)
        );
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof SetShaderTypeEvent
            && isVisible()
            && mc.thePlayer != null
            && ((SetShaderTypeEvent)event).getShaderType() == SetShaderTypeEvent.BlurMode.GLOW
            && this.glowSetting.isEnabled()) {
            this.handleFlag(true);
        }
    }

    @Override
    public boolean onBlurMode(SetShaderTypeEvent.BlurMode blurMode) {
        return blurMode == SetShaderTypeEvent.BlurMode.GLOW && this.glowSetting.isEnabled();
    }

    @Override
    public void onRender() {
        if (isVisible() && mc.thePlayer != null) {
            this.handleFlag(false);
        }
    }

    private Color[] computeColorArray() {
        Color[] colors;
        if (this.arrayListSetting.matchesString2("ArrayList")) {
            colors = ArrayList.computeColorArray(this);
        } else {
            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
            colors = new Color[]{brutalityHelper.getColor(), brutalityHelper.getColor2()};
        }

        if (colors[0].equals(colors[1])) {
            colors = colors[0].getRed() + colors[0].getGreen() + colors[0].getBlue() > 720
                ? new Color[]{computeColor(colors[0], Color.BLACK, 0.25), colors[0]}
                : new Color[]{colors[0], computeColor(colors[0], Color.WHITE, 0.42)};
        }

        return colors;
    }

    public static boolean isVisible() {
        return instance != null && instance.checkState44() && EditableModulesHelper.matchesModule2(instance) && !EditableModulesHelper.matchesModule(instance);
    }

    public float computeFloat() {
        return (float)(this.ySetting.computeDouble() + 25.0 * this.scaleSetting.computeDouble());
    }

    @Override
    public void onDisable() {
        HudDragHelper.handleObject(this);
    }

    public Watermark() {
        super("Watermark", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.arrayListSetting,
                this.waveSpeedSetting,
                this.waveLengthSetting,
                this.leftSetting,
                this.scaleSetting,
                this.versionSetting,
                this.shadowSetting,
                this.glowSetting,
                this.xSetting,
                this.ySetting
            }
        );
        instance = this;
    }
}
