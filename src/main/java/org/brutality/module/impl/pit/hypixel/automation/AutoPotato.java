package org.brutality.module.impl.pit.hypixel.automation;

import lombok.Generated;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent;
import org.brutality.event.impl.basic.BasicEvent2;
import org.brutality.event.impl.basic.BasicEvent4;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.PlayerEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.network.NetworkHelper;
import org.brutality.utils.timer.TimerUtil;

public class AutoPotato extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean flag = false;
    private int count = -1;
    private int count2;
    private final NumberSetting healthSetting;
    private final NumberSetting delaySetting;
    private final BooleanSetting disableWhileBowSetting;
    private final NumberSetting eatCooldownSetting;
    private long timestamp;
    public boolean flag2;
    public static AutoPotato instance;
    private static final long timestamp2 = 6082670192268147737L;

    private boolean checkState2() {
        long l = (long)(this.eatCooldownSetting.computeDouble() * 1000.0);
        return System.currentTimeMillis() - this.timestamp < l;
    }

    public boolean isCount() {
        return this.count != -1;
    }

    private void onBasicEvent2(BasicEvent2 basicEvent2) {
        if (this.checkState44() && this.isCount()) {
            basicEvent2.cancel();
        }
    }

    public long computeLong() {
        long l = (long)(this.eatCooldownSetting.computeDouble() * 1000.0);
        long l2 = l - (System.currentTimeMillis() - this.timestamp);
        return Math.max(0L, l2);
    }

    private boolean checkState() {
        if (mc.thePlayer == null) {
            return false;
        }
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        return heldItem != null && heldItem.getItem() instanceof ItemBow;
    }

    private void onEventTick(EventTick event) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (!this.checkState44()) {
                this.count = -1;
            } else if (!this.disableWhileBowSetting.isEnabled() || !this.checkState()) {
                if (EventState.onEvent(event)) {
                    if (this.checkState2()) {
                        this.flag2 = false;
                        this.flag = mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount() <= this.healthSetting.computeDouble();
                        return;
                    }

                    boolean flag = mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount() <= this.healthSetting.computeDouble();
                    if (this.flag && flag && this.count == -1 && this.timerUtil.matchesLong10((long)this.delaySetting.computeDouble())) {
                        int n = this.computeInt();
                        if (n != -1) {
                            this.count = mc.thePlayer.inventory.currentItem;
                            mc.thePlayer.inventory.currentItem = n;
                            mc.playerController.syncCurrentPlayItem();
                            NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            this.timestamp = System.currentTimeMillis();
                            this.flag2 = true;
                            this.count2 = mc.thePlayer.ticksExisted;
                            this.timerUtil.reset();
                        }
                    }

                    this.flag = flag;
                } else if (EventState.onEvent2(event) && this.count != -1 && !mc.thePlayer.isUsingItem()) {
                    mc.thePlayer.inventory.currentItem = this.count;
                    mc.playerController.syncCurrentPlayItem();
                    this.count = -1;
                }
            }
        }
    }

    public int getCount2() {
        return this.count2;
    }

    private void onPlayerEvent4(PlayerEvent4 playerEvent4) {
        if (this.checkState44() && this.isCount()) {
            playerEvent4.cancel();
        }
    }

    private void onBasicEvent(BasicEvent basicEvent) {
        if (this.checkState44() && this.isCount()) {
            basicEvent.cancel();
        }
    }

    private boolean matchesItemStack(ItemStack stack) {
        return stack == null ? false : Item.getIdFromItem(stack.getItem()) == 393;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick) {
                this.onEventTick((EventTick)event);
            } else if (event instanceof BasicEvent2) {
                this.onBasicEvent2((BasicEvent2)event);
            } else if (event instanceof BasicEvent4) {
                this.onBasicEvent4((BasicEvent4)event);
            } else if (event instanceof PlayerEvent4) {
                this.onPlayerEvent4((PlayerEvent4)event);
            } else if (event instanceof BasicEvent) {
                this.onBasicEvent((BasicEvent)event);
            }
        }
    }

    @Generated
    public static AutoPotato getInstance() {
        return instance;
    }

    public AutoPotato() {
        super("Auto Potato", 0, Category.PIT);
        this.count2 = (int)timestamp2;
        this.healthSetting = new NumberSetting("Health", this, 10.0, 0.0, 50.0, 1.0);
        this.delaySetting = new NumberSetting("Delay", this, 500.0, 0.0, 5000.0, 50.0);
        this.disableWhileBowSetting = new BooleanSetting("Disable While Bow", this, false);
        this.eatCooldownSetting = new NumberSetting("Eat Cooldown", this, 15.0, 0.0, 60.0, 1.0);
        this.timestamp = 0L;
        this.flag2 = false;
        this.handleSettingArray(new Setting[]{this.healthSetting, this.delaySetting, this.disableWhileBowSetting, this.eatCooldownSetting});
        instance = this;
    }

    private int computeInt() {
        EntityPlayerSP player = this.getMc().thePlayer;
        if (player == null) {
            return -1;
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && this.matchesItemStack(stack)) {
                return i;
            }
        }

        return -1;
    }

    private void onBasicEvent4(BasicEvent4 basicEvent4) {
        if (this.checkState44() && this.isCount()) {
            basicEvent4.cancel();
        }
    }
}
