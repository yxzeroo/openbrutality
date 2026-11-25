package org.brutality.module.impl.pit.hypixel.hud;

import net.minecraft.potion.Potion;
import org.brutality.event.Event;

public final class RegenHUD extends HudModule {
    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
    }

    public RegenHUD() {
        super("Regen HUD", "Regen", Potion.regeneration, 90, -32576);
    }
}
