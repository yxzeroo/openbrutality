package org.brutality.module.impl.combat;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class AntiBot extends Module {
    public static boolean matchesEntity(Entity entity) {
        return !Brutality.moduleManager.computeModule("AntiBot").toggled ? false : !getTablist().contains(entity.getName());
    }

    public AntiBot() {
        super("AntiBot", 0, Category.COMBAT);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                ;
            }
        }
    }

    private static List<String> getTablist() {
        return mc.getNetHandler()
            .getPlayerInfoMap()
            .stream()
            .map(NetworkPlayerInfo::getGameProfile)
            .filter(gameProfile -> gameProfile.getId() != mc.thePlayer.getUniqueID())
            .map(GameProfile::getName)
            .collect(Collectors.toList());
    }
}
