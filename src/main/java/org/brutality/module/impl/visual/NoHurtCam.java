package org.brutality.module.impl.visual;

import lombok.Generated;
import org.brutality.event.Event;
import org.brutality.event.impl.render.RenderEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;

public class NoHurtCam extends Module {
    private static NoHurtCam instance;
    private final BooleanSetting disableHurtCamSetting = new BooleanSetting("Disable Hurt Cam", this, true);
    private final NumberSetting intensitySetting = new NumberSetting("Intensity", this, 1.0, 0.0, 2.0, 0.1);
    private final NumberSetting deathRotationSetting = new NumberSetting("Death Rotation", this, 40.0, 0.0, 180.0, 1.0);
    private final NumberSetting deathDivisorSetting = new NumberSetting("Death Divisor", this, 8000.0, 1000.0, 20000.0, 100.0);
    private final NumberSetting deathOffsetSetting = new NumberSetting("Death Offset", this, 200.0, 0.0, 1000.0, 10.0);
    private final NumberSetting hurtRotationMultiplierSetting = new NumberSetting("Hurt Rotation Multiplier", this, 14.0, 0.0, 50.0, 1.0);

    public NoHurtCam() {
        super("NoHurtCam", 0, Category.VISUAL);
        instance = this;
        this.handleSettingArray(
            new Setting[]{
                this.disableHurtCamSetting,
                this.intensitySetting,
                this.deathRotationSetting,
                this.deathDivisorSetting,
                this.deathOffsetSetting,
                this.hurtRotationMultiplierSetting
            }
        );
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent renderEvent) {
                if (this.disableHurtCamSetting.isEnabled()) {
                    renderEvent.cancel();
                    return;
                }

                renderEvent.setFactor((float)this.intensitySetting.computeDouble());
                renderEvent.setFactor2((float)this.deathRotationSetting.computeDouble());
                renderEvent.setFactor3((float)this.deathDivisorSetting.computeDouble());
                renderEvent.setFactor4((float)this.deathOffsetSetting.computeDouble());
                renderEvent.setFactor5((float)this.hurtRotationMultiplierSetting.computeDouble());
            }
        }
    }

    @Generated
    public static NoHurtCam getInstance() {
        return instance;
    }
}
