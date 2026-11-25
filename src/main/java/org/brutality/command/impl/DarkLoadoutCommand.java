package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.pit.hypixel.swap.DarkSwap;

public final class DarkLoadoutCommand extends Command {
    public DarkLoadoutCommand() {
        super("DarkLoadout", "Manually swap or restore the DarkSwap loadout", ".ds", "ds");
    }

    @Override
    public void onCommand(String[] args, String message) {
        for (Module module : ModuleManager.getEntries2()) {
            if (module instanceof DarkSwap) {
                ((DarkSwap)module).process();
                return;
            }
        }
    }
}
