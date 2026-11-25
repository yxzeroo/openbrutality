package org.brutality.module.impl.misc;

import lombok.Generated;
import net.minecraft.client.Minecraft;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class Spammer extends Module {
    private static Spammer instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final StringSetting playPitSetting = new StringSetting("Text", this, "/play pit");
    private final NumberSetting delaySSetting = new NumberSetting("Delay (s)", this, 3.5, 0.0, 3600.0, 0.1);
    private final NumberSetting randomCharsSetting = new NumberSetting("Random Chars", this, 0.0, 0.0, 10.0, 1.0);
    private long timestamp = 0L;
    private int count = 19968;

    public NumberSetting getDelaySSetting() {
        return this.delaySSetting;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventRender2D) {
                    this.process();
                }
            }
        }
    }

    public void process3() {
        this.timestamp = 0L;
        this.process();
    }

    @Generated
    public static Spammer getInstance() {
        return instance;
    }

    public void resetTimer() {
        this.timestamp = 0L;
    }

    public NumberSetting getRandomCharsSetting() {
        return this.randomCharsSetting;
    }

    private void process() {
        if (this.mc.thePlayer != null) {
            long now = System.currentTimeMillis();
            long l = (long)(this.delaySSetting.computeDouble() * 1000.0);
            if (now - this.timestamp >= l) {
                this.timestamp = now;
                String val = this.playPitSetting.getValue();
                if (this.randomCharsSetting.computeDouble() > 0.0) {
                    val = val + " ";

                    for (int i = 0; i < this.randomCharsSetting.computeDouble(); i++) {
                        val = val + (char)this.count;
                        this.count++;
                        if (this.count > 40959) {
                            this.count = 19968;
                        }
                    }
                }

                this.mc.thePlayer.sendChatMessage(val);
            }
        }
    }

    public StringSetting getPlayPitSetting() {
        return this.playPitSetting;
    }

    public Spammer() {
        super("Spammer", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(new Setting[]{this.playPitSetting, this.delaySSetting, this.randomCharsSetting});
        instance = this;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.timestamp = 0L;
    }
}
