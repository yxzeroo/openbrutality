package org.brutality.module.impl.pit.hypixel.swap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
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
import org.brutality.utils.player.PlayerHelper11;
import org.lwjgl.input.Mouse;

public class PitSwap extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private final NumberSetting delaySetting;
    private final BooleanSetting holdLeftClickSetting;
    private final BooleanSetting useDiamondAxeSetting;
    private final BooleanSetting onlyOnHitSetting;
    private long timestamp;
    private long timestamp2;
    private PlayerHelper11.Entry entry = null;
    private PlayerHelper11.Entry entry2 = null;
    private boolean flag = false;
    private static final long timestamp3 = 1000L;

    @Override
    public void onEnable() {
        this.timestamp = 0L;
        this.timestamp2 = 0L;
        this.entry = null;
        this.entry2 = null;
        this.flag = false;
        this.process3();
    }

    private void process3() {
        if (this.useDiamondAxeSetting.isEnabled()) {
            this.entry = PlayerHelper11.computeEntry2("Diamond Axe");
        } else {
            this.entry = PlayerHelper11.computeEntry2("Diamond Sword");
        }

        this.entry2 = PlayerHelper11.computeEntry2("Combat Spade");
    }

    public PitSwap() {
        super("PitSwap", 0, Category.PIT);
        this.delaySetting = new NumberSetting("Delay", this, 50.0, 1.0, 100.0, 10.0);
        this.holdLeftClickSetting = new BooleanSetting("Hold Left Click", this, true);
        this.useDiamondAxeSetting = new BooleanSetting("Use Diamond Axe", this, false);
        this.onlyOnHitSetting = new BooleanSetting("Only On Hit", this, false);
        this.handleSettingArray(new Setting[]{this.delaySetting, this.holdLeftClickSetting, this.useDiamondAxeSetting, this.onlyOnHitSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null) {
                    return;
                }

                if (this.onlyOnHitSetting.isEnabled() && !this.flag) {
                    return;
                }

                if (System.currentTimeMillis() - this.timestamp >= timestamp3) {
                    this.process3();
                    this.timestamp = System.currentTimeMillis();
                }

                if (System.currentTimeMillis() - this.timestamp2 >= this.delaySetting.computeDouble()) {
                    if (this.holdLeftClickSetting.isEnabled()
                        && (
                            (!Mouse.isCreated() || !Mouse.isButtonDown(0)) && !AutoGrinder.checkState15()
                                || Minecraft.getMinecraft().currentScreen instanceof GuiContainer
                        )) {
                        return;
                    }

                    if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null) {
                        String displayName = Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName();
                        if (this.useDiamondAxeSetting.isEnabled() && (displayName.contains("Diamond Axe") || displayName.contains("Combat Spade"))
                            || !this.useDiamondAxeSetting.isEnabled() && (displayName.contains("Diamond Sword") || displayName.contains("Combat Spade"))) {
                            this.process();
                        }
                    }

                    this.timestamp2 = System.currentTimeMillis();
                    this.flag = false;
                }
            }

            if (event instanceof AttackEvent attackEvent && attackEvent.getEntityPlayer() == Minecraft.getMinecraft().thePlayer) {
                this.flag = true;
            }
        }
    }

    private void process() {
        if (this.entry != null && this.entry2 != null) {
            if (this.entry.isValid() && this.entry2.isValid()) {
                String displayName = Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName();
                if (this.useDiamondAxeSetting.isEnabled()) {
                    if (displayName.contains("Diamond Axe")) {
                        PlayerHelper11.matchesEntry(this.entry2, this);
                    } else if (displayName.contains("Combat Spade")) {
                        PlayerHelper11.matchesEntry(this.entry, this);
                    }
                } else if (displayName.contains("Diamond Sword")) {
                    PlayerHelper11.matchesEntry(this.entry2, this);
                } else if (displayName.contains("Combat Spade")) {
                    PlayerHelper11.matchesEntry(this.entry, this);
                }

                this.timestamp2 = System.currentTimeMillis();
            }
        }
    }
}
