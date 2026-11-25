package org.brutality.module.impl.pit.hypixel.hud;

import java.awt.Color;
import java.util.Arrays;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.ResetColorHelper;

public class RingTemplate extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static final BlockPos[] blockPosArray = new BlockPos[]{
        new BlockPos(6, 71, -7),
        new BlockPos(5, 71, -8),
        new BlockPos(4, 71, -8),
        new BlockPos(3, 71, -9),
        new BlockPos(2, 71, -9),
        new BlockPos(1, 71, -9),
        new BlockPos(0, 71, -9),
        new BlockPos(-1, 71, -9),
        new BlockPos(-3, 71, -8),
        new BlockPos(-2, 71, -9),
        new BlockPos(-5, 71, -7),
        new BlockPos(-4, 71, -8),
        new BlockPos(-6, 71, -6),
        new BlockPos(-7, 71, -5),
        new BlockPos(-8, 71, -4),
        new BlockPos(-8, 71, -3),
        new BlockPos(-9, 71, -1),
        new BlockPos(-9, 71, 0),
        new BlockPos(-9, 71, 1),
        new BlockPos(-9, 71, 3),
        new BlockPos(-9, 71, 2),
        new BlockPos(-9, 71, -2),
        new BlockPos(-8, 71, 4),
        new BlockPos(-8, 71, 5),
        new BlockPos(-7, 71, 6),
        new BlockPos(-6, 71, 7),
        new BlockPos(-5, 71, 8),
        new BlockPos(-4, 71, 9),
        new BlockPos(-3, 71, 9),
        new BlockPos(-2, 72, 10),
        new BlockPos(-1, 72, 10),
        new BlockPos(0, 72, 10),
        new BlockPos(1, 72, 10),
        new BlockPos(2, 72, 10),
        new BlockPos(3, 72, 10),
        new BlockPos(5, 71, 9),
        new BlockPos(4, 71, 9),
        new BlockPos(7, 71, 7),
        new BlockPos(6, 71, 8),
        new BlockPos(8, 71, 6),
        new BlockPos(9, 71, 5),
        new BlockPos(9, 71, 4),
        new BlockPos(10, 72, 3),
        new BlockPos(10, 72, 2),
        new BlockPos(10, 72, 1),
        new BlockPos(10, 72, 0),
        new BlockPos(10, 72, -1),
        new BlockPos(10, 72, -2),
        new BlockPos(9, 71, -3),
        new BlockPos(9, 71, -4),
        new BlockPos(8, 71, -5),
        new BlockPos(7, 71, -6)
    };
    private static final BlockPos[] blockPosArray2 = new BlockPos[]{
        new BlockPos(3, 82, -9),
        new BlockPos(2, 82, -9),
        new BlockPos(1, 82, -9),
        new BlockPos(0, 82, -9),
        new BlockPos(-1, 82, -9),
        new BlockPos(-2, 82, -9),
        new BlockPos(-3, 82, -8),
        new BlockPos(-4, 82, -8),
        new BlockPos(-5, 82, -7),
        new BlockPos(-6, 82, -6),
        new BlockPos(-7, 82, -5),
        new BlockPos(-8, 82, -4),
        new BlockPos(-8, 82, -3),
        new BlockPos(-9, 82, -2),
        new BlockPos(-9, 82, -1),
        new BlockPos(-9, 82, 0),
        new BlockPos(-9, 82, 1),
        new BlockPos(-9, 82, 2),
        new BlockPos(-9, 82, 3),
        new BlockPos(-3, 82, 9),
        new BlockPos(-4, 82, 9),
        new BlockPos(-5, 82, 8),
        new BlockPos(-6, 82, 7),
        new BlockPos(-7, 82, 6),
        new BlockPos(-8, 82, 5),
        new BlockPos(-8, 82, 4),
        new BlockPos(3, 82, 10),
        new BlockPos(2, 82, 10),
        new BlockPos(1, 82, 10),
        new BlockPos(0, 82, 10),
        new BlockPos(-1, 82, 10),
        new BlockPos(-2, 82, 10),
        new BlockPos(4, 82, 9),
        new BlockPos(5, 82, 9),
        new BlockPos(6, 82, 8),
        new BlockPos(7, 82, 7),
        new BlockPos(8, 82, 6),
        new BlockPos(9, 82, 5),
        new BlockPos(9, 82, 4),
        new BlockPos(10, 82, -2),
        new BlockPos(10, 82, -1),
        new BlockPos(10, 82, 0),
        new BlockPos(10, 82, 1),
        new BlockPos(10, 82, 2),
        new BlockPos(10, 82, 3),
        new BlockPos(4, 82, -8),
        new BlockPos(5, 82, -8),
        new BlockPos(6, 82, -7),
        new BlockPos(7, 82, -6),
        new BlockPos(8, 82, -5),
        new BlockPos(9, 82, -4),
        new BlockPos(9, 82, -3)
    };
    private static final BlockPos[] blockPosArray3 = new BlockPos[]{
        new BlockPos(-1, 82, 10),
        new BlockPos(0, 82, 10),
        new BlockPos(1, 82, 10),
        new BlockPos(2, 82, 10),
        new BlockPos(5, 82, 9),
        new BlockPos(3, 82, 10),
        new BlockPos(4, 82, 9),
        new BlockPos(7, 82, 7),
        new BlockPos(6, 82, 8),
        new BlockPos(8, 82, 6),
        new BlockPos(9, 82, 5),
        new BlockPos(9, 82, 4),
        new BlockPos(10, 82, 0),
        new BlockPos(10, 82, 1),
        new BlockPos(10, 82, 3),
        new BlockPos(10, 82, 2),
        new BlockPos(10, 82, -1),
        new BlockPos(10, 82, -2),
        new BlockPos(9, 82, -3),
        new BlockPos(9, 82, -4),
        new BlockPos(8, 82, -5),
        new BlockPos(7, 82, -6),
        new BlockPos(6, 82, -7),
        new BlockPos(5, 82, -8),
        new BlockPos(4, 82, -8),
        new BlockPos(3, 82, -9),
        new BlockPos(2, 82, -9),
        new BlockPos(1, 82, -9),
        new BlockPos(-1, 82, -9),
        new BlockPos(-2, 82, -9),
        new BlockPos(0, 82, -9),
        new BlockPos(-3, 82, -8),
        new BlockPos(-5, 82, -7),
        new BlockPos(-6, 82, -6),
        new BlockPos(-7, 82, -5),
        new BlockPos(-8, 82, -3),
        new BlockPos(-9, 82, 3),
        new BlockPos(-8, 82, 4),
        new BlockPos(-8, 82, 5),
        new BlockPos(-7, 82, 6),
        new BlockPos(-6, 82, 7),
        new BlockPos(-5, 82, 8),
        new BlockPos(-4, 82, 9),
        new BlockPos(-3, 82, 9),
        new BlockPos(-2, 82, 10),
        new BlockPos(-9, 82, 2),
        new BlockPos(-9, 82, 1),
        new BlockPos(-9, 82, 0),
        new BlockPos(-9, 82, -1),
        new BlockPos(-9, 82, -2),
        new BlockPos(-8, 82, -4),
        new BlockPos(-4, 82, -8)
    };
    private static final BlockPos[] blockPosArray4 = new BlockPos[]{
        new BlockPos(-1, 82, 10),
        new BlockPos(0, 82, 10),
        new BlockPos(1, 82, 10),
        new BlockPos(2, 82, 10),
        new BlockPos(5, 82, 9),
        new BlockPos(3, 82, 10),
        new BlockPos(4, 82, 9),
        new BlockPos(7, 82, 7),
        new BlockPos(6, 82, 8),
        new BlockPos(8, 82, 6),
        new BlockPos(9, 82, 5),
        new BlockPos(9, 82, 4),
        new BlockPos(10, 82, 0),
        new BlockPos(10, 82, 1),
        new BlockPos(10, 82, 3),
        new BlockPos(10, 82, 2),
        new BlockPos(10, 82, -1),
        new BlockPos(10, 82, -2),
        new BlockPos(9, 82, -3),
        new BlockPos(9, 82, -4),
        new BlockPos(8, 82, -5),
        new BlockPos(7, 82, -6),
        new BlockPos(6, 82, -7),
        new BlockPos(5, 82, -8),
        new BlockPos(4, 82, -8),
        new BlockPos(3, 82, -9),
        new BlockPos(2, 82, -9),
        new BlockPos(1, 82, -9),
        new BlockPos(-1, 82, -9),
        new BlockPos(-2, 82, -9),
        new BlockPos(0, 82, -9),
        new BlockPos(-3, 82, -8),
        new BlockPos(-5, 82, -7),
        new BlockPos(-6, 82, -6),
        new BlockPos(-7, 82, -5),
        new BlockPos(-8, 82, -3),
        new BlockPos(-9, 82, 3),
        new BlockPos(-8, 82, 4),
        new BlockPos(-8, 82, 5),
        new BlockPos(-7, 82, 6),
        new BlockPos(-6, 82, 7),
        new BlockPos(-5, 82, 8),
        new BlockPos(-4, 82, 9),
        new BlockPos(-3, 82, 9),
        new BlockPos(-2, 82, 10),
        new BlockPos(-9, 82, 2),
        new BlockPos(-9, 82, 1),
        new BlockPos(-9, 82, 0),
        new BlockPos(-9, 82, -1),
        new BlockPos(-9, 82, -2),
        new BlockPos(-8, 82, -4),
        new BlockPos(-4, 82, -8)
    };
    private static final BlockPos[] blockPosArray5 = new BlockPos[]{
        new BlockPos(11, 43, -1),
        new BlockPos(11, 43, -2),
        new BlockPos(11, 43, 0),
        new BlockPos(11, 43, 1),
        new BlockPos(11, 43, 2),
        new BlockPos(11, 43, 3),
        new BlockPos(10, 43, 4),
        new BlockPos(10, 43, 5),
        new BlockPos(9, 43, 6),
        new BlockPos(9, 43, 7),
        new BlockPos(8, 43, 8),
        new BlockPos(7, 43, 9),
        new BlockPos(6, 43, 9),
        new BlockPos(5, 43, 10),
        new BlockPos(4, 43, 10),
        new BlockPos(3, 43, 11),
        new BlockPos(2, 43, 11),
        new BlockPos(1, 43, 11),
        new BlockPos(0, 43, 11),
        new BlockPos(-1, 43, 11),
        new BlockPos(-2, 43, 11),
        new BlockPos(-3, 43, 10),
        new BlockPos(-4, 43, 10),
        new BlockPos(-5, 43, 9),
        new BlockPos(-6, 43, 9),
        new BlockPos(-7, 43, 8),
        new BlockPos(-8, 43, 7),
        new BlockPos(-8, 43, 6),
        new BlockPos(-9, 43, 5),
        new BlockPos(-9, 43, 4),
        new BlockPos(-10, 43, 3),
        new BlockPos(-10, 43, 2),
        new BlockPos(-10, 43, 1),
        new BlockPos(-10, 43, 0),
        new BlockPos(-10, 43, -1),
        new BlockPos(-10, 43, -2),
        new BlockPos(-9, 43, -3),
        new BlockPos(-9, 43, -4),
        new BlockPos(-8, 43, -5),
        new BlockPos(-8, 43, -6),
        new BlockPos(-7, 43, -7),
        new BlockPos(-6, 43, -8),
        new BlockPos(-4, 43, -9),
        new BlockPos(-3, 43, -9),
        new BlockPos(-5, 43, -8),
        new BlockPos(-2, 43, -10),
        new BlockPos(-1, 43, -10),
        new BlockPos(0, 43, -10),
        new BlockPos(1, 43, -10),
        new BlockPos(2, 43, -10),
        new BlockPos(3, 43, -10),
        new BlockPos(5, 43, -9),
        new BlockPos(4, 43, -9),
        new BlockPos(7, 43, -8),
        new BlockPos(6, 43, -8),
        new BlockPos(8, 43, -7),
        new BlockPos(10, 43, -4),
        new BlockPos(9, 43, -5),
        new BlockPos(9, 43, -6),
        new BlockPos(10, 43, -3)
    };
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final ModeSetting customSetting = new ModeSetting("Color Mode", this, "Rainbow", Arrays.asList("Rainbow", "Custom"))
        .computeModeSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final ColorSetting customColorSetting = new ColorSetting("Custom Color", this, 0.0F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final NumberSetting colorSpeedSetting = new NumberSetting("Color Speed", this, 5.0, 1.0, 20.0, 0.0);
    private final NumberSetting lineWidthSetting = new NumberSetting("Line Width", this, 1.5, 0.5, 5.0, 1.0);
    private BlockPos[] blockPosArray6 = new BlockPos[0];
    private BrutalityHelper brutalityHelper;

    private int computeInt(int n) {
        double d = this.colorSpeedSetting.computeDouble() / 5.0;
        long l = (long)(System.currentTimeMillis() * d);
        float f = (float)((l + n) % 3600L) / 3600.0F;
        return Color.HSBtoRGB(f, 0.8F, 0.7F);
    }

    private Color computeColor(int n) {
        this.process();
        if (ModuleSpecificHelper.matchesModeSetting(this.modeSetting) && this.brutalityHelper != null) {
            double d = this.colorSpeedSetting.computeDouble() / 5.0;
            long l = (long)(System.currentTimeMillis() * d / 10.0);
            int n2 = (int)((l + n * 15) % 360L);
            return GetColorHelper.computeColor(15, n2, this.brutalityHelper.getColor(), this.brutalityHelper.getColor3(), false);
        }
        String text = this.customSetting.getText();
        switch (text) {
            case "Rainbow":
                return new Color(this.computeInt(n * 100), true);
            case "Custom":
                Color color = this.customColorSetting.computeColor();
                float[] floats = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                float f = n * 0.02F % 1.0F;
                float f2 = (floats[0] + f) % 1.0F;
                return Color.getHSBColor(f2, floats[1], floats[2]);
            default:
                return new Color(this.computeInt(n * 100), true);
        }
    }

    private void process3() {
        String str = FreshHelper.computeString(mc.theWorld);
        if (str != null && !str.isEmpty()) {
            String str2 = str.toLowerCase();
            switch (str2) {
                case "genesis":
                    this.blockPosArray6 = blockPosArray5;
                    break;
                case "kings":
                    this.blockPosArray6 = blockPosArray;
                    break;
                case "seasons":
                    this.blockPosArray6 = blockPosArray4;
                    break;
                case "elemental":
                    this.blockPosArray6 = blockPosArray3;
                    break;
                case "corals":
                    this.blockPosArray6 = blockPosArray2;
                    break;
                default:
                    this.blockPosArray6 = new BlockPos[0];
            }
        } else {
            this.blockPosArray6 = new BlockPos[0];
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetPartialTicksEvent) {
                this.process3();

                for (int i = 0; i < this.blockPosArray6.length; i++) {
                    BlockPos pos = this.blockPosArray6[i];
                    Color color = this.computeColor(i);
                    ResetColorHelper.handleBlockPos2(pos, color, true, false, (float)this.lineWidthSetting.computeDouble());
                }
            }
        }
    }

    private void process() {
        this.brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.modeSetting);
    }

    public RingTemplate() {
        super("Ring Template", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.modeSetting, this.customSetting, this.customColorSetting, this.colorSpeedSetting, this.lineWidthSetting});
        this.process();
    }
}
