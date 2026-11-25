package org.brutality.module.impl.player;

import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSoup;
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
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.network.NetworkHelper;
import org.brutality.utils.timer.TimerUtil;

public class AutoHeal extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static AutoHeal instance;
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean flag = false;
    private int count = -1;
    private int count2 = 0;
    private float factor;
    private boolean flag2;
    private final NumberSetting healthSetting = new NumberSetting("Health", this, 10.0, 0.0, 20.0, 1.0);
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 4000.0, 0.0, 5000.0, 50.0);
    private final BooleanSetting regenCheckSetting = new BooleanSetting("Regen Check", this, false);
    private final BooleanSetting hurtCheckSetting = new BooleanSetting("Hurt Check", this, false);
    private final NumberSetting hurtTimeSetting = new NumberSetting("Hurt Time", this, 20.0, 1.0, 100.0, 1.0);

    @Generated
    public static AutoHeal getInstance() {
        return instance;
    }

    private void onEventTick(EventTick event) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (!this.checkState44()) {
                this.count = -1;
            } else {
                if (this.hurtCheckSetting.isEnabled()) {
                    if (this.count2 > 0) {
                        this.count2--;
                    }

                    if (mc.thePlayer.hurtTime > 0) {
                        this.count2 = (int)this.hurtTimeSetting.computeDouble();
                    }
                } else {
                    this.count2 = 1;
                }

                if (EventState.onEvent(event)) {
                    boolean flag = mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount() <= (float)this.healthSetting.computeDouble();
                    boolean flag2 = this.factor > 0.0F && mc.thePlayer.getAbsorptionAmount() <= 0.0F;
                    if (flag2 && this.flag2 && mc.thePlayer.getHealth() <= this.healthSetting.computeDouble()) {
                        this.flag = true;
                    }

                    if (this.flag && flag && !this.checkState() && this.timerUtil.matchesLong10((long)this.delaySetting.computeDouble()) && this.count2 > 0) {
                        int n = this.computeInt();
                        if (n != -1) {
                            this.count = mc.thePlayer.inventory.currentItem;
                            mc.thePlayer.inventory.currentItem = n;
                            mc.playerController.syncCurrentPlayItem();
                            NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            this.flag2 = this.matchesItemStack(mc.thePlayer.getHeldItem());
                            this.timerUtil.reset();
                        }
                    }

                    this.flag = flag;
                    this.factor = mc.thePlayer.getAbsorptionAmount();
                } else if (EventState.onEvent2(event) && this.count != -1 && !mc.thePlayer.isUsingItem()) {
                    mc.thePlayer.inventory.currentItem = this.count;
                    mc.playerController.syncCurrentPlayItem();
                    this.count = -1;
                }
            }
        }
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

    private void onBasicEvent(BasicEvent basicEvent) {
        if (this.checkState44() && this.isCount()) {
            basicEvent.cancel();
        }
    }

    public boolean isCount() {
        return this.count != -1;
    }

    private void onPlayerEvent4(PlayerEvent4 playerEvent4) {
        if (this.checkState44() && this.isCount()) {
            playerEvent4.cancel();
        }
    }

    private boolean matchesItemStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemSkull && stack.hasDisplayName() && stack.getDisplayName().contains("Golden Head");
    }

    private int computeInt() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.hasDisplayName()) {
                String displayName = stack.getDisplayName();
                if (stack.getItem() instanceof ItemSkull && displayName.contains("§6") && displayName.contains("Golden Head")) {
                    return i;
                }
            }
        }

        for (int j = 0; j < 9; j++) {
            ItemStack stack2 = mc.thePlayer.inventory.getStackInSlot(j);
            if (stack2 != null && stack2.hasDisplayName()) {
                String displayName2 = stack2.getDisplayName();
                if (stack2.getItem() instanceof ItemSkull && displayName2.matches("\\S+§c's Head")) {
                    return j;
                }
            }
        }

        for (int k = 0; k < 9; k++) {
            ItemStack stack3 = mc.thePlayer.inventory.getStackInSlot(k);
            if (stack3 != null && stack3.hasDisplayName()) {
                String displayName3 = stack3.getDisplayName();
                if (stack3.getItem() instanceof ItemFood && displayName3.contains("§6Cornucopia")) {
                    return k;
                }

                if (stack3.getItem() instanceof ItemSoup
                    && (displayName3.contains("§a") && displayName3.contains("Tasty Soup") || displayName3.contains("§a") && displayName3.contains("Assist Soup"))) {
                    return k;
                }
            }
        }

        return -1;
    }

    public AutoHeal() {
        super("AutoHeal", 0, Category.PLAYER);
        this.handleSettingArray(new Setting[]{this.healthSetting, this.delaySetting, this.regenCheckSetting, this.hurtCheckSetting, this.hurtTimeSetting});
        instance = this;
    }

    private void onBasicEvent2(BasicEvent2 basicEvent2) {
        if (this.checkState44() && this.isCount()) {
            basicEvent2.cancel();
        }
    }

    private boolean checkState() {
        return this.regenCheckSetting.isEnabled() && mc.thePlayer.isPotionActive(Potion.regeneration);
    }

    private void onBasicEvent4(BasicEvent4 basicEvent4) {
        if (this.checkState44() && this.isCount()) {
            basicEvent4.cancel();
        }
    }
}
