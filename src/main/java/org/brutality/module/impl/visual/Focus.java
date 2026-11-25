package org.brutality.module.impl.visual;

import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.render.GetEntityEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.TruceManager;
import org.brutality.utils.WHelper;

public class Focus extends Module {
    private static Focus instance;
    private final BooleanSetting hideSelfSetting = new BooleanSetting("Hide Self", this, false);
    private final NumberSetting opacitySetting = new NumberSetting("Opacity", this, 100.0, 0.0, 100.0, 1.0);
    private String text;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetEntityEvent && this.checkState44()) {
                GetEntityEvent getEntityEvent = (GetEntityEvent)event;
                Entity entity = getEntityEvent.getEntity();
                EventState event2 = getEntityEvent.getState();
                if (entity != null && event2 != null && mc != null && mc.thePlayer != null) {
                    if (entity instanceof EntityPlayer player) {
                        if (this.hideSelfSetting.isEnabled() && player == mc.thePlayer && mc.gameSettings != null && mc.gameSettings.thirdPersonView != 0) {
                            this.onGetEntityEvent(getEntityEvent);
                            return;
                        }

                        if (player != mc.thePlayer) {
                            try {
                                String name = player.getName();
                                if (name == null) {
                                    return;
                                }

                                boolean flag = DefaultHelper.matchesString(name);
                                boolean flag2 = WHelper.matchesString(name) || this.text != null && this.text.equalsIgnoreCase(name);
                                if (!flag && !flag2 && !TruceManager.matchesString(name)) {
                                    this.handleGetEntityEvent(getEntityEvent, this.text != null ? 0.0F : (float)this.opacitySetting.computeDouble() / 100.0F);
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean matchesString(String str) {
        return str != null
            && (
                DefaultHelper.matchesString(str)
                    || WHelper.matchesString(str)
                    || TruceManager.matchesString(str)
                    || this.text != null && this.text.equalsIgnoreCase(str)
            );
    }

    public boolean checkState() {
        return this.hideSelfSetting.isEnabled();
    }

    private void onGetEntityEvent(GetEntityEvent getEntityEvent) {
        this.handleGetEntityEvent(getEntityEvent, (float)this.opacitySetting.computeDouble() / 100.0F);
    }

    @Generated
    public static Focus getInstance() {
        return instance;
    }

    public void setText(String text) {
        this.text = text;
    }

    private void handleGetEntityEvent(GetEntityEvent getEntityEvent, float f) {
        if (f <= 0.0F) {
            getEntityEvent.setCancel(true);
        } else {
            getEntityEvent.setFactor(f);
        }
    }

    public double computeDouble() {
        return this.opacitySetting.computeDouble();
    }

    public Focus() {
        super("Focus", 0, Category.VISUAL);
        instance = this;
        this.handleSettingArray(new Setting[]{this.hideSelfSetting, this.opacitySetting});
    }
}
