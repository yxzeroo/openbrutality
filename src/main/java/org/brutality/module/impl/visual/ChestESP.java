package org.brutality.module.impl.visual;

import java.awt.Color;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.ResetColorHelper;

public class ChestESP extends Module {
    private final BooleanSetting outlineSetting = new BooleanSetting("Outline", this, true);
    private final BooleanSetting fillSetting = new BooleanSetting("Fill", this, true);
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final ColorSetting colorSetting = new ColorSetting("Color", this, 0.13F, 1.0F, 1.0F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final ColorSetting enderColorSetting = new ColorSetting("Ender Color", this, 0.77F, 1.0F, 1.0F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final NumberSetting fillOpacitySetting = new NumberSetting("Fill Opacity", this, 100.0, 0.0, 255.0, 5.0);

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetPartialTicksEvent) {
                this.handleFloat(mc.timer.renderPartialTicks);
            }
        }
    }

    public ChestESP() {
        super("ChestESP", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{this.outlineSetting, this.fillSetting, this.modeSetting, this.colorSetting, this.enderColorSetting, this.fillOpacitySetting}
        );
    }

    private Color computeColor(Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)this.fillOpacitySetting.computeDouble());
    }

    private void handleFloat(float f) {
        BrutalityHelper brutalityHelper = ModuleSpecificHelper.matchesModeSetting(this.modeSetting) ? ModuleSpecificHelper.computeBrutalityHelper() : null;
        Color color = brutalityHelper != null ? brutalityHelper.getColor() : this.colorSetting.computeColor();
        Color color2 = brutalityHelper != null ? brutalityHelper.getColor() : this.enderColorSetting.computeColor();

        for (Object obj : mc.theWorld.loadedTileEntityList) {
            if (obj instanceof TileEntityChest tileEntityChest) {
                BlockPos pos = tileEntityChest.getPos();
                ResetColorHelper.handleBlockPos2(pos, this.computeColor(color), this.outlineSetting.isEnabled(), this.fillSetting.isEnabled(), 1.5F);
            } else if (obj instanceof TileEntityEnderChest tileEntityEnderChest) {
                BlockPos pos2 = tileEntityEnderChest.getPos();
                ResetColorHelper.handleBlockPos2(pos2, this.computeColor(color2), this.outlineSetting.isEnabled(), this.fillSetting.isEnabled(), 1.5F);
            }
        }
    }
}
