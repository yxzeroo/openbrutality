package org.brutality.module.impl.client;

import java.util.Arrays;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.utils.modules.CapeModeHelper;
// VENDOR-DISABLED: import org.brutality.utils.network.IrcClient;

public class Cape extends Module {
    private static final ResourceLocation resourceLocation = new ResourceLocation("brutality/capes/brutality.png");
    private static final ResourceLocation resourceLocation2 = new ResourceLocation("brutality/capes/cobalt.png");
    private static Cape instance;
    private final ModeSetting cobaltSetting = new ModeSetting("Cape Mode", this, "Brutality", Arrays.asList("Brutality", "Cobalt"));

    @Override
    public void onDisable() {
        super.onDisable();
        // VENDOR-DISABLED: IrcClient.checkState();
    }

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public void onEnable() {
        super.onEnable();
        // VENDOR-DISABLED: IrcClient.checkState();
    }

    public static String computeString() {
        return instance != null && instance.checkState44() ? instance.cobaltSetting.getText() : "None";
    }

    public static ResourceLocation computeResourceLocation(AbstractClientPlayer player) {
        if (player == null) {
            return null;
        }
        return mc != null && player == mc.thePlayer
            ? computeResourceLocation(computeString())
            : CapeModeHelper.instance.computeResourceLocation(player.getUniqueID(), player.getName());
    }

    public Cape() {
        super("Cape", 0, Category.CLIENT);
        instance = this;
        this.handleSettingArray(new Setting[]{this.cobaltSetting});
        // VENDOR-DISABLED: this.cobaltSetting.setCallback(IrcClient::checkState);
        this.handleFlag3(true);
    }

    public static ResourceLocation computeResourceLocation(String str) {
        if ("Brutality".equalsIgnoreCase(str)) {
            return resourceLocation;
        }
        return "Cobalt".equalsIgnoreCase(str) ? resourceLocation2 : null;
    }
}
