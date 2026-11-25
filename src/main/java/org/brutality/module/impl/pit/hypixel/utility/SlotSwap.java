package org.brutality.module.impl.pit.hypixel.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.AttackEvent;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.PlayerHelper12;
import org.lwjgl.input.Mouse;

public class SlotSwap extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private final NumberSetting slot1Setting = new NumberSetting("Slot #1", this, 1.0, 1.0, 9.0, 1.0);
    private final NumberSetting slot2Setting = new NumberSetting("Slot #2", this, 2.0, 1.0, 9.0, 1.0);
    private final NumberSetting delayMsSetting = new NumberSetting("Delay (ms)", this, 50.0, 0.0, 1000.0, 50.0);
    private final BooleanSetting onlyOnHitSetting = new BooleanSetting("Only On Hit", this, false);
    private final BooleanSetting holdLeftClickSetting = new BooleanSetting("Hold Left Click", this, false);
    private long timestamp = System.currentTimeMillis();
    private int count;
    private boolean flag = false;

    public void setFlag(Entity entity) {
        if (entity != null) {
            this.flag = true;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof AttackEvent && ((AttackEvent)event).getEntityPlayer() == mc.thePlayer) {
                    this.flag = true;
                }

                if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    Minecraft mc2 = Minecraft.getMinecraft();
                    long now = System.currentTimeMillis();
                    if (this.onlyOnHitSetting.isEnabled() && !this.flag) {
                        return;
                    }

                    if (now - this.timestamp >= this.delayMsSetting.computeDouble()) {
                        if (this.holdLeftClickSetting.isEnabled() && (!Mouse.isCreated() || !Mouse.isButtonDown(0)) && !AutoGrinder.checkState15()) {
                            return;
                        }

                        this.process();
                        this.timestamp = now;
                        if (this.onlyOnHitSetting.isEnabled()) {
                            this.flag = false;
                        }
                    }
                }
            }
        }
    }

    public SlotSwap() {
        super("SlotSwap", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.slot1Setting, this.slot2Setting, this.delayMsSetting, this.onlyOnHitSetting, this.holdLeftClickSetting});
    }

    @Override
    public void onEnable() {
        this.timestamp = System.currentTimeMillis();
        this.count = (int)this.slot1Setting.computeDouble() - 1;
        this.flag = false;
    }

    private void process() {
        Minecraft mc = Minecraft.getMinecraft();
        int n = (int)this.slot1Setting.computeDouble() - 1;
        int n2 = (int)this.slot2Setting.computeDouble() - 1;
        this.count = this.count == n ? n2 : n;
        PlayerHelper12.matchesObject(this, this.count, Math.max(1500L, (long)this.delayMsSetting.computeDouble() + 250L));
    }

    @Override
    public void onDisable() {
        PlayerHelper12.handleObject(this);
        PlayerHelper12.matchesObject(null, (int)this.slot1Setting.computeDouble() - 1, 250L);
        this.flag = false;
    }
}
