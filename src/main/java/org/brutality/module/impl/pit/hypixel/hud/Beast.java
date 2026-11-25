package org.brutality.module.impl.pit.hypixel.hud;

import net.minecraft.entity.player.EntityPlayer;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.util.BEASTHelper;

public class Beast extends Module {
    private final PitCategory pitCategory = PitCategory.MAJOR_EVENT;

    public Beast() {
        super("Beast", 0, Category.PIT);
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        Module module = Brutality.moduleManager == null ? null : Brutality.moduleManager.computeModule("Beast");
        return module != null && module.checkState44() && BEASTHelper.matchesEntityPlayer(player);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }
}
