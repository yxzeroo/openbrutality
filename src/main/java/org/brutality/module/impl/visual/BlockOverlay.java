package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.ResetColorHelper;

public class BlockOverlay extends Module {
    private BooleanSetting outlineSetting = new BooleanSetting("Outline", this, true);
    private BooleanSetting filledSetting = new BooleanSetting("Filled", this, false);
    private BooleanSetting syncColorSetting = new BooleanSetting("Sync Color", this, false);
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final ModeSetting rainbowSetting = new ModeSetting("Color Mode", this, "Custom", Arrays.asList("Custom", "Rainbow"))
        .computeModeSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final ColorSetting customColorSetting = new ColorSetting("Custom Color", this, 0.0F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final KeybindListSetting colorSettingsSetting = new KeybindListSetting(
        "Color Settings", this, this.modeSetting, this.rainbowSetting, this.customColorSetting
    );
    private final KeybindListSetting outlineWidthSetting = new KeybindListSetting(
        "Rendering Options", this, new BooleanSetting("Through Walls", this, true), new NumberSetting("Outline Width", this, 2.0, 0.5, 5.0, 0.1)
    );
    private final KeybindListSetting saturationCyclingSetting = new KeybindListSetting(
        "Rainbow Settings",
        this,
        new BooleanSetting("Enable", this, false),
        new ModeSetting("Rainbow Speed", this, "Normal", Arrays.asList("Slow", "Normal", "Fast")),
        new BooleanSetting("Saturation Cycling", this, false)
    );
    private final KeybindListSetting breathingSetting = new KeybindListSetting(
        "Overlay Modes",
        this,
        new BooleanSetting("Standard", this, true),
        new BooleanSetting("Animated", this, false),
        new BooleanSetting("Pulsing", this, false),
        new BooleanSetting("Breathing", this, false)
    );
    private final KeybindListSetting smoothAnimationSetting = new KeybindListSetting(
        "Animation Settings",
        this,
        new ModeSetting("Animation Speed", this, "Normal", Arrays.asList("Slow", "Normal", "Fast", "Very Fast")),
        new ModeSetting("Pulse Style", this, "Alpha", Arrays.asList("Alpha", "Scale", "Both")),
        new BooleanSetting("Smooth Animation", this, true)
    );
    private final Minecraft mc = Minecraft.getMinecraft();

    public Color computeColor() {
        return this.computeColor3();
    }

    private Color computeColor(Color color, float f) {
        long now = System.currentTimeMillis();
        float f2 = this.computeFloat();
        BooleanSetting boolSetting = (BooleanSetting)this.breathingSetting.computeSetting("Pulsing");
        BooleanSetting boolSetting2 = (BooleanSetting)this.breathingSetting.computeSetting("Breathing");
        BooleanSetting boolSetting3 = (BooleanSetting)this.breathingSetting.computeSetting("Animated");
        Color color2 = color;
        if (boolSetting != null && boolSetting.isEnabled()) {
            color2 = this.computeColor(color, now, f2);
        }

        if (boolSetting2 != null && boolSetting2.isEnabled()) {
            color2 = this.computeColor2(color2, now, f2);
        }

        if (boolSetting3 != null && boolSetting3.isEnabled()) {
            color2 = this.computeColor3(color2, now, f2);
        }

        return color2;
    }

    private Color computeColor2() {
        ModeSetting modeSetting = (ModeSetting)this.saturationCyclingSetting.computeSetting("Rainbow Speed");
        BooleanSetting boolSetting = (BooleanSetting)this.saturationCyclingSetting.computeSetting("Saturation Cycling");
        float f = 1.0F;
        if (modeSetting != null) {
            String text = modeSetting.getText();
            switch (text) {
                case "Slow":
                    f = 0.5F;
                    break;
                case "Normal":
                    f = 1.0F;
                    break;
                case "Fast":
                    f = 2.0F;
            }
        }

        long now = System.currentTimeMillis();
        float f2 = (float)now * f % 3600.0F / 10.0F;
        float f3 = 1.0F;
        if (boolSetting != null && boolSetting.isEnabled()) {
            f3 = (float)(0.7F + 0.3F * Math.sin((float)now * f / 1000.0));
        }

        return Color.getHSBColor(f2 / 360.0F, f3, 1.0F);
    }

    public BlockOverlay() {
        super("BlockOverlay", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.outlineSetting,
                this.filledSetting,
                this.syncColorSetting,
                this.colorSettingsSetting,
                this.outlineWidthSetting,
                this.saturationCyclingSetting,
                this.breathingSetting,
                this.smoothAnimationSetting
            }
        );
    }

    public boolean checkState() {
        BooleanSetting boolSetting = (BooleanSetting)this.outlineWidthSetting.computeSetting("Through Walls");
        return boolSetting != null && boolSetting.isEnabled();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender3D event2) {
                this.handleFloat(event2.getFactor());
            }
        }
    }

    private Color computeColor(Color color, long l, float f) {
        ModeSetting modeSetting = (ModeSetting)this.smoothAnimationSetting.computeSetting("Pulse Style");
        double d = Math.sin((float)l * f / 500.0) * 0.5 + 0.5;
        if (modeSetting != null) {
            String text = modeSetting.getText();
            switch (text) {
                case "Alpha":
                    int n = (int)(d * 255.0);
                    return new Color(color.getRed(), color.getGreen(), color.getBlue(), n);
                case "Scale":
                    return color;
                case "Both":
                    int n2 = (int)(d * 255.0);
                    return new Color(color.getRed(), color.getGreen(), color.getBlue(), n2);
            }
        }

        return color;
    }

    private Color computeColor3(Color color, long l, float f) {
        float f2 = (float)l * f / 2000.0F % 1.0F;
        float[] floats = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float f3 = (floats[0] + f2 * 0.1F) % 1.0F;
        return Color.getHSBColor(f3, floats[1], floats[2]);
    }

    private void handleFloat(float f) {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            MovingObjectPosition objectMouseOver = this.mc.objectMouseOver;
            BlockPos pos = objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectType.BLOCK ? objectMouseOver.getBlockPos() : null;
            Block block = this.computeBlock(pos);
            if (block == null
                && this.mc.playerController != null
                && this.mc.currentScreen == null
                && this.mc.inGameHasFocus
                && this.mc.gameSettings.keyBindAttack.isKeyDown()) {
                pos = this.mc.playerController.getActiveMiningBlock();
                block = this.computeBlock(pos);
            }

            if (block != null) {
                Color color = this.computeColor3();
                int rgb = color.getRGB();
                if (this.checkState4()) {
                    rgb = this.computeColor(color, f).getRGB();
                }

                NumberSetting numberSetting = (NumberSetting)this.outlineWidthSetting.computeSetting("Outline Width");
                float f2 = numberSetting != null ? (float)numberSetting.computeDouble() : 2.0F;
                block.setBlockBoundsBasedOnState(this.mc.theWorld, pos);
                ResetColorHelper.handleBlockPos(pos, color, this.outlineSetting.isEnabled(), this.filledSetting.isEnabled(), f2);
            }
        }
    }

    private Color computeColor2(Color color, long l, float f) {
        double d = Math.sin((float)l * f / 1000.0) * 0.3 + 0.7;
        int n = (int)(color.getRed() * d);
        int n2 = (int)(color.getGreen() * d);
        int n3 = (int)(color.getBlue() * d);
        return new Color(Math.max(0, Math.min(255, n)), Math.max(0, Math.min(255, n2)), Math.max(0, Math.min(255, n3)), color.getAlpha());
    }

    private float computeFloat() {
        ModeSetting modeSetting = (ModeSetting)this.smoothAnimationSetting.computeSetting("Animation Speed");
        if (modeSetting == null) {
            return 1.0F;
        }
        String text = modeSetting.getText();
        switch (text) {
            case "Slow":
                return 0.5F;
            case "Normal":
                return 1.0F;
            case "Fast":
                return 2.0F;
            case "Very Fast":
                return 4.0F;
            default:
                return 1.0F;
        }
    }

    private Block computeBlock(BlockPos pos) {
        if (pos != null && this.mc.theWorld.isBlockLoaded(pos)) {
            Block block = this.mc.theWorld.getBlockState(pos).getBlock();
            return block.getMaterial() == Material.air ? null : block;
        }
        return null;
    }

    public boolean checkState2() {
        return this.outlineSetting.isEnabled();
    }

    public boolean checkState5() {
        return this.checkState4();
    }

    private Color computeColor3() {
        if (this.syncColorSetting.isEnabled()) {
            return BrutalityHelper.computeBrutalityHelper("Brutality").getColor();
        }
        BooleanSetting boolSetting = (BooleanSetting)this.saturationCyclingSetting.computeSetting("Enable");
        if (boolSetting != null && boolSetting.isEnabled()) {
            return this.computeColor2();
        }
        if (ModuleSpecificHelper.matchesModeSetting(this.modeSetting)) {
            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
            return brutalityHelper != null ? brutalityHelper.getColor() : Color.WHITE;
        }
        ModeSetting rainbowSetting = this.rainbowSetting;
        if (rainbowSetting == null) {
            return Color.WHITE;
        }
        String text = rainbowSetting.getText();
        switch (text) {
            case "Custom":
                return this.customColorSetting.computeColor();
            case "Rainbow":
                return this.computeColor2();
            default:
                return Color.WHITE;
        }
    }

    public boolean checkState6() {
        return this.filledSetting.isEnabled();
    }

    private boolean checkState4() {
        BooleanSetting boolSetting = (BooleanSetting)this.breathingSetting.computeSetting("Animated");
        BooleanSetting boolSetting2 = (BooleanSetting)this.breathingSetting.computeSetting("Pulsing");
        BooleanSetting boolSetting3 = (BooleanSetting)this.breathingSetting.computeSetting("Breathing");
        return boolSetting != null && boolSetting.isEnabled() || boolSetting2 != null && boolSetting2.isEnabled() || boolSetting3 != null && boolSetting3.isEnabled();
    }
}
