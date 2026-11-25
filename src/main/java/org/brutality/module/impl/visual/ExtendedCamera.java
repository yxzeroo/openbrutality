package org.brutality.module.impl.visual;

import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;

public class ExtendedCamera extends Module {
    public final NumberSetting amountBlocksSetting = new NumberSetting("Amount (Blocks)", this, 4.0, 1.0, 100.0, 1.0);
    private float factor;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick) {
                try {
                    float f = (float)this.amountBlocksSetting.computeDouble();
                    if (this.factor != f) {
                        this.setThirdPersonDistance(this.factor = f);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageUtils.x("bruh try again why ts no work :rose:");
                }
            }
        }
    }

    public ExtendedCamera() {
        super("Extended Camera", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.amountBlocksSetting});
    }

    @Override
    public void onEnable() {
        this.setThirdPersonDistance((float)this.amountBlocksSetting.computeDouble());
    }

    private void setThirdPersonDistance(float f) {
        mc.entityRenderer.setThirdPersonDistance(f);
    }

    @Override
    public void onDisable() {
        this.setThirdPersonDistance(4.0F);
    }
}
