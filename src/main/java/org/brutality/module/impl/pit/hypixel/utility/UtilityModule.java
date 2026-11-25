package org.brutality.module.impl.pit.hypixel.utility;

import org.brutality.core.itemstats.GetOrCreateHandler;
import org.brutality.core.itemstats.SerializeHandler;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class UtilityModule extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private long timestamp;
    private static final String text = "Item Stat Tracker";
    private static final long timestamp2 = 30000L;

    @Override
    public void onDisable() {
        super.onDisable();
        GetOrCreateHandler.save();
    }

    public UtilityModule() {
        super(text, 0, Category.PIT);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && mc.thePlayer != null) {
            if (event instanceof EventTick && System.currentTimeMillis() - this.timestamp >= timestamp2) {
                this.timestamp = System.currentTimeMillis();
                GetOrCreateHandler.save();
            } else if (event instanceof ChatEvent && FreshHelper.flag) {
                SerializeHandler orCreate = GetOrCreateHandler.getOrCreate(mc.thePlayer.getHeldItem());
                if (orCreate == null) {
                    return;
                }

                String str = ExecuteCommandHelper.computeString6(((ChatEvent)event).message.getUnformattedText());
                orCreate.handleInt(ExecuteCommandHelper.computeInt(str));
                orCreate.handleInt3(ExecuteCommandHelper.computeInt2(str));
                orCreate.handleInt2(ExecuteCommandHelper.computeInt5(str));
                orCreate.handleDouble2(ExecuteCommandHelper.computeDouble6(str));
                orCreate.handleDouble(ExecuteCommandHelper.computeDouble5(str));
            }
        }
    }
}
