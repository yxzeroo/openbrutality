package org.brutality.module.impl.misc;

import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.StringSetting;
import org.brutality.utils.world.WorldDownloadController;

public final class WorldDownloader extends Module {
    public final StringSetting downloadedWorldSetting = new StringSetting("World Name", this, "Downloaded World");
    public final BooleanSetting saveEntitiesSetting = new BooleanSetting("Save Entities", this, false);

    @Override
    public void onDisable() {
        WorldDownloadController.stop();
    }

    @Override
    public void process2() {
        if (this.checkState44()) {
            WorldDownloadController.stop();
            this.handleFlag3(false);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate && this.checkState44() && !WorldDownloadController.isEntry()) {
            this.handleFlag3(false);
        }
    }

    public WorldDownloader() {
        super("World Downloader", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(new Setting[]{this.downloadedWorldSetting, this.saveEntitiesSetting});
    }

    @Override
    public void onEnable() {
        if (mc.theWorld == null
            || mc.thePlayer == null
            || !WorldDownloadController.matchesWorldClient(
                mc.theWorld, mc.thePlayer.getPosition(), this.downloadedWorldSetting.getValue(), this.saveEntitiesSetting.isEnabled()
            )) {
            this.handleFlag3(false);
        }
    }
}
