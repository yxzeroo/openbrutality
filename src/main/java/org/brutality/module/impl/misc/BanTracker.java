package org.brutality.module.impl.misc;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.hypixel.RequestHelper;

public class BanTracker extends Module {
    private long timestamp = -1L;
    private long timestamp2;
    private int generation;

    @Override
    public void onDisable() {
        this.timestamp = -1L;
        this.timestamp2 = 0L;
        this.generation++;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        this.timestamp = -1L;
        this.timestamp2 = 0L;
        this.generation++;
        super.onEnable();
    }

    public BanTracker() {
        super("Ban Tracker", 0, Category.MISCELLANEOUS);
    }

    @Override
    public void onEvent(Event event) {
        // VENDOR-DISABLED: if (this.checkState44() && RuntimeAuthGuard.checkState2() && event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
        if (this.checkState44() && true && event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
            long now = System.currentTimeMillis();
            if (now - this.timestamp2 >= 60000L) {
                this.timestamp2 = now;
                int generation = this.generation;
                RequestHelper.request().whenComplete((entry, t) -> mc.addScheduledTask(() -> {
                    if (t == null && this.checkState44() && generation == this.generation && mc.thePlayer != null) {
                        if (entry.timestamp3 > 0L) {
                            this.handleString("Watchdog banned " + entry.timestamp3 + " player(s) in the last minute.");
                        }

                        if (this.timestamp >= 0L && entry.timestamp2 > this.timestamp) {
                            this.handleString("Staff banned " + (entry.timestamp2 - this.timestamp) + " player(s) since the previous sample.");
                        }

                        this.timestamp = entry.timestamp2;
                    }
                }));
            }
        }
    }

    private void handleString(String str) {
        EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
        EnumChatFormatting DARK_GRAY = EnumChatFormatting.DARK_GRAY;
        EnumChatFormatting RED = EnumChatFormatting.RED;
        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[" + RED + "BanTracker" + DARK_GRAY + "] " + GRAY + str));
    }
}
