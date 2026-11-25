package org.brutality.module.impl.pit.hypixel.hud;

import net.minecraft.potion.Potion;
import org.brutality.event.Event;

public final class SpeedHUD extends HudModule {
    private static final long timestamp = 2649913590904389631L;

    public SpeedHUD() {
        super("SpeedHUD", "Speed", Potion.moveSpeed, 104, (int)timestamp);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
    }
}
