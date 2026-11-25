package org.brutality.module.impl.pit.hypixel.automation;

import lombok.Generated;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
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

public class AutoFeast extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean flag = false;
    private int count = -1;
    private int count2;
    private final NumberSetting healthSetting;
    private final NumberSetting delaySetting;
    private final BooleanSetting disableWhileBowSetting;
    private final NumberSetting eatCooldownSetting;
    private final BooleanSetting waitForResistanceSetting;
    private long timestamp;
    public static AutoFeast instance;
    private static final long timestamp2 = -7409294042774635495L;
    private static final long timestamp3 = 50L;

    @Generated
    public static AutoFeast getInstance() {
        return instance;
    }

    public int getCount2() {
        return this.count2;
    }

    public boolean isCount() {
        return this.count != -1;
    }

    public long computeLong() {
        if (this.waitForResistanceSetting.isEnabled()) {
            Potion resistance = Potion.resistance;
            return mc.thePlayer.isPotionActive(resistance) ? mc.thePlayer.getActivePotionEffect(resistance).getDuration() * timestamp3 : 0L;
        }
        long l = (long)(this.eatCooldownSetting.computeDouble() * 1000.0);
        long l2 = l - (System.currentTimeMillis() - this.timestamp);
        return Math.max(0L, l2);
    }

    public AutoFeast() {
        super("Auto Feast", 0, Category.PIT);
        this.count2 = (int)timestamp2;
        this.healthSetting = new NumberSetting("Health", this, 7.0, 0.0, 20.0, 1.0);
        this.delaySetting = new NumberSetting("Delay", this, 500.0, 0.0, 5000.0, 50.0);
        this.disableWhileBowSetting = new BooleanSetting("Disable While Bow", this, true);
        this.eatCooldownSetting = new NumberSetting("Eat Cooldown", this, 11.0, 0.0, 60.0, 1.0);
        this.waitForResistanceSetting = new BooleanSetting("Wait For Resistance", this, true);
        this.timestamp = 0L;
        this.eatCooldownSetting.computeNumberSetting(() -> !this.waitForResistanceSetting.isEnabled());
        this.handleSettingArray(
            new Setting[]{this.healthSetting, this.delaySetting, this.disableWhileBowSetting, this.eatCooldownSetting, this.waitForResistanceSetting}
        );
        instance = this;
    }

    private void onPlayerEvent4(PlayerEvent4 playerEvent4) {
        if (this.checkState44() && this.isCount()) {
            playerEvent4.cancel();
        }
    }

    private boolean checkState() {
        if (this.waitForResistanceSetting.isEnabled()) {
            return mc.thePlayer.isPotionActive(Potion.resistance);
        }
        long l = (long)(this.eatCooldownSetting.computeDouble() * 1000.0);
        return System.currentTimeMillis() - this.timestamp < l;
    }

    private void onBasicEvent2(BasicEvent2 basicEvent2) {
        if (this.checkState44() && this.isCount()) {
            basicEvent2.cancel();
        }
    }

    private boolean checkState2() {
        if (mc.thePlayer == null) {
            return false;
        }
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        return heldItem != null && heldItem.getItem() instanceof ItemBow;
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

    private void onEventTick(EventTick event) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (!this.checkState44()) {
                this.count = -1;
            } else if (!this.disableWhileBowSetting.isEnabled() || !this.checkState2()) {
                if (EventState.onEvent(event)) {
                    if (this.checkState()) {
                        this.flag = mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount() <= this.healthSetting.computeDouble();
                        return;
                    }

                    boolean flag = mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount() <= this.healthSetting.computeDouble();
                    if (this.flag && flag && this.timerUtil.matchesLong10((long)this.delaySetting.computeDouble())) {
                        int n = this.computeInt();
                        if (n != -1) {
                            this.count = mc.thePlayer.inventory.currentItem;
                            mc.thePlayer.inventory.currentItem = n;
                            mc.playerController.syncCurrentPlayItem();
                            NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            this.timestamp = System.currentTimeMillis();
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

    private boolean matchesItemStack(ItemStack stack) {
        return stack == null ? false : Item.getIdFromItem(stack.getItem()) == 423;
    }

    private void onBasicEvent(BasicEvent basicEvent) {
        if (this.checkState44() && this.isCount()) {
            basicEvent.cancel();
        }
    }

    private void onBasicEvent4(BasicEvent4 basicEvent4) {
        if (this.checkState44() && this.isCount()) {
            basicEvent4.cancel();
        }
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
}
