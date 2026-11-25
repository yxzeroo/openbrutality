package org.brutality.module.impl.pit.hypixel.utility;

import java.awt.Color;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.module.impl.visual.ESP;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.utils.hypixel.pit.FreshHelper;

public final class VenomedESP extends Module {
    private static VenomedESP instance;
    private final PitCategory pitCategory = PitCategory.HUD;
    private final BooleanSetting victimsSetting = new BooleanSetting("Victims", this, true);
    private final BooleanSetting huntersSetting = new BooleanSetting("Hunters", this, true);
    private final ColorSetting victimColorSetting = new ColorSetting("Victim Color", this, 0.8F);
    private final ColorSetting hunterColorSetting = new ColorSetting("Hunter Color", this, 0.0F);
    private final BooleanSetting labelsSetting = new BooleanSetting("Labels", this, true);

    public static boolean isActive() {
        return instance != null && instance.checkState44();
    }

    public VenomedESP() {
        super("VenomedESP", 0, Category.PIT);
        instance = this;
        this.handleSettingArray(new Setting[]{this.victimsSetting, this.huntersSetting, this.victimColorSetting, this.hunterColorSetting, this.labelsSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            VenomedTimer instance = VenomedTimer.getInstance();
            if (instance != null) {
                if (!instance.checkState44()) {
                    instance.onEvent(event);
                }

                if (event instanceof GetPartialTicksEvent && mc.theWorld != null && mc.thePlayer != null) {
                    float partialTicks = ((GetPartialTicksEvent)event).getPartialTicks();

                    for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                        if (playerEntity != mc.thePlayer
                            && playerEntity.isEntityAlive()
                            && !FreshHelper.matchesWorld3(mc.theWorld, playerEntity)
                            && instance.computeInt3(playerEntity.getName()) > 0) {
                            boolean flag = instance.matchesString(playerEntity.getName());
                            if (flag ? this.huntersSetting.isEnabled() : this.victimsSetting.isEnabled()) {
                                Color color = (flag ? this.hunterColorSetting : this.victimColorSetting).computeColor();
                                int rgb = color.getRGB();
                                String str = this.labelsSetting.isEnabled()
                                    ? playerEntity.getName() + (flag ? " Hunter " : " Victim ") + instance.computeString(playerEntity.getName()) + "s"
                                    : null;
                                ESP.handleEntityPlayer(playerEntity, partialTicks, rgb, str);
                            }
                        }
                    }
                }
            }
        }
    }
}
