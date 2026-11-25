package org.brutality.module.impl.visual;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.MovementUtils;

public class Crosshair extends Module {
    private final NumberSetting lengthSetting = new NumberSetting("Length", this, 7.0, 4.0, 10.0, 0.5);
    private final NumberSetting widthSetting = new NumberSetting("Width", this, 0.5, 0.5, 2.0, 0.5);
    private final NumberSetting staticSpaceSetting = new NumberSetting("Static space", this, 5.0, 0.0, 15.0, 1.0);
    private final NumberSetting movingSpaceSetting = new NumberSetting("Moving space", this, 7.0, 1.0, 16.0, 1.0);
    private final BooleanSetting outlineSetting = new BooleanSetting("Outline", this, true);
    private float factor;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender2D event2) {
                if (mc.gameSettings.thirdPersonView == 0) {
                    float f = MovementUtils.isMoving() ? (float)this.movingSpaceSetting.computeDouble() : (float)this.staticSpaceSetting.computeDouble();
                    this.factor = this.factor + (f - this.factor) * 0.2F;
                    ScaledResolution scaledResolution = event2.getScaledResolution();
                    float f2 = scaledResolution.getScaledHeight() / 2.0F;
                    float f3 = scaledResolution.getScaledWidth() / 2.0F;
                    float f4 = (float)this.widthSetting.computeDouble();
                    float f5 = (float)this.lengthSetting.computeDouble();
                    Color color = new Color(11766015);
                    if (this.outlineSetting.isEnabled()) {
                        Gui.drawRect(
                            (int)(f3 + this.factor - 1.0F),
                            (int)(f2 - f4 / 2.0F - 1.0F),
                            (int)(f3 + this.factor + f5 + 1.0F),
                            (int)(f2 + f4 / 2.0F + 1.0F),
                            -16777216
                        );
                        Gui.drawRect(
                            (int)(f3 - this.factor - f5 - 1.0F),
                            (int)(f2 - f4 / 2.0F - 1.0F),
                            (int)(f3 - this.factor + 1.0F),
                            (int)(f2 + f4 / 2.0F + 1.0F),
                            -16777216
                        );
                        Gui.drawRect(
                            (int)(f3 - f4 / 2.0F - 1.0F),
                            (int)(f2 + this.factor - 1.0F),
                            (int)(f3 + f4 / 2.0F + 1.0F),
                            (int)(f2 + this.factor + f5 + 1.0F),
                            -16777216
                        );
                        Gui.drawRect(
                            (int)(f3 - f4 / 2.0F - 1.0F),
                            (int)(f2 - this.factor - f5 - 1.0F),
                            (int)(f3 + f4 / 2.0F + 1.0F),
                            (int)(f2 - this.factor + 1.0F),
                            -16777216
                        );
                    }

                    Gui.drawRect(
                        (int)(f3 + this.factor), (int)(f2 - f4 / 2.0F), (int)(f3 + this.factor + f5), (int)(f2 + f4 / 2.0F), color.getRGB()
                    );
                    Gui.drawRect(
                        (int)(f3 - this.factor - f5), (int)(f2 - f4 / 2.0F), (int)(f3 - this.factor), (int)(f2 + f4 / 2.0F), color.getRGB()
                    );
                    Gui.drawRect(
                        (int)(f3 - f4 / 2.0F), (int)(f2 + this.factor), (int)(f3 + f4 / 2.0F), (int)(f2 + this.factor + f5), color.getRGB()
                    );
                    Gui.drawRect(
                        (int)(f3 - f4 / 2.0F), (int)(f2 - this.factor - f5), (int)(f3 + f4 / 2.0F), (int)(f2 - this.factor), color.getRGB()
                    );
                }
            }
        }
    }

    public Crosshair() {
        super("Crosshair", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.lengthSetting, this.widthSetting, this.staticSpaceSetting, this.movingSpaceSetting, this.outlineSetting});
    }
}
