package org.brutality.module.impl.movement;

import org.brutality.event.Event;
import org.brutality.event.impl.movement.MovementEvent5;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.GetCurrentScreenHelper;

public class InstantStop extends Module {
    private final BooleanSetting onlyOnGroundSetting = new BooleanSetting("Only on ground", this, true);
    private final BooleanSetting disableWhenFlyingSetting = new BooleanSetting("Disable when flying", this, true);
    private float factor;
    private float b;

    @Override
    public void onDisable() {
        super.onDisable();
        this.factor = 0.0F;
        this.b = 0.0F;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (GetCurrentScreenHelper.checkState2()) {
                if (event instanceof MovementEvent5) {
                    if (mc.currentScreen == null) {
                        MovementEvent5 movementEvent5 = (MovementEvent5)event;
                        if (this.onlyOnGroundSetting.isEnabled() && !mc.thePlayer.onGround) {
                            this.factor = movementEvent5.getFactor();
                            this.b = movementEvent5.getFactor2();
                        } else if (this.disableWhenFlyingSetting.isEnabled() && mc.thePlayer.capabilities.isFlying) {
                            this.factor = movementEvent5.getFactor();
                            this.b = movementEvent5.getFactor2();
                        } else {
                            float factor = movementEvent5.getFactor();
                            float factor2 = movementEvent5.getFactor2();
                            if (factor == 0.0F && factor2 == 0.0F && (this.factor != 0.0F || this.b != 0.0F)) {
                                movementEvent5.setFactor(Math.signum(-this.factor));
                                movementEvent5.setFactor2(Math.signum(-this.b));
                            }

                            this.factor = factor;
                            this.b = factor2;
                        }
                    }
                }
            }
        }
    }

    public InstantStop() {
        super("Instant Stop", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.onlyOnGroundSetting, this.disableWhenFlyingSetting});
    }
}
