package org.brutality.module.impl.misc;

import java.util.Arrays;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class ModuleSound extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private static ModuleSound instance;
    private ModeSetting modeSetting;
    private static final long timestamp = 50L;

    private void process() {
        this.modeSetting = new ModeSetting("Sound Mode", this, "Brutality", Arrays.asList("Brutality", "PrimeCheats", "Myau"));
        this.handleSettingArray(new Setting[]{this.modeSetting});
    }

    public String computeString() {
        return this.modeSetting.getText();
    }

    public void handleFlag(boolean flag) {
        if (this.checkState44()) {
            String text = this.modeSetting.getText();
            String str = text.toLowerCase();
            switch (str) {
                case "brutality":
                    this.mc.thePlayer.playSound("note.bass", 0.8F, flag ? 2.0F : 0.8F);
                    if (flag) {
                        new Thread(() -> {
                            try {
                                Thread.sleep(timestamp);
                                this.mc.thePlayer.playSound("note.harp", 0.3F, 1.8F);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }).start();
                    }
                    break;
                case "primecheats":
                    this.mc.thePlayer.playSound("note.harp", 1.0F, flag ? 1.5F : 1.0F);
                    break;
                case "myau":
                    this.mc.thePlayer.playSound("random.click", 1.0F, flag ? 1.0F : 0.8F);
                    break;
                default:
                    this.mc.thePlayer.playSound("note.bass", 0.8F, flag ? 2.0F : 0.8F);
            }
        }
    }

    @Generated
    public static ModuleSound getInstance() {
        return instance;
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

    public ModuleSound() {
        super("Module Sound", 0, Category.MISCELLANEOUS);
        this.process();
        instance = this;
    }

    public void handleFlag(boolean flag, float f) {
        if (this.checkState44()) {
            String text = this.modeSetting.getText();
            String str = text.toLowerCase();
            switch (str) {
                case "brutality":
                    this.mc.thePlayer.playSound("note.bass", f * 0.8F, flag ? 2.0F : 0.8F);
                    break;
                case "primecheats":
                    this.mc.thePlayer.playSound("note.harp", f, flag ? 1.5F : 1.0F);
                    break;
                case "myau":
                    this.mc.thePlayer.playSound("random.click", f, flag ? 1.0F : 0.8F);
                    break;
                default:
                    this.handleFlag(flag);
            }
        }
    }

    public boolean matchesString(String str) {
        return this.modeSetting.matchesString2(str);
    }
}
