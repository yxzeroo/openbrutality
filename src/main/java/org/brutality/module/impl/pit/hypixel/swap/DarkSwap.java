package org.brutality.module.impl.pit.hypixel.swap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.player.ActiveHelper;
import org.brutality.utils.player.FindIdentityHelper;
import org.brutality.utils.player.PlayerHelper12;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class DarkSwap extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final ModeSetting silentSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Silent"));
    private final ModeSetting manualSetting = new ModeSetting("Trigger", this, "Automatic", Arrays.asList("Automatic", "Manual"));
    private final NumberSetting healthThresholdSetting = new NumberSetting("Health Threshold", this, 3.5, 1.0, 20.0, 0.5);
    private final NumberSetting swapBackHealthSetting = new NumberSetting("Swap Back Health", this, 5.0, 1.0, 20.0, 0.5);
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 75.0, 1.0, 500.0, 25.0);
    private final BooleanSetting safeModeSetting = new BooleanSetting("Safe Mode", this, true);
    private final BooleanSetting swapBackSetting = new BooleanSetting("Swap Back", this, true);
    private final BooleanSetting distanceCheckSetting = new BooleanSetting("Distance Check", this, true);
    private final NumberSetting distanceSetting = new NumberSetting("Distance", this, 11.0, 3.0, 20.0, 0.5);
    private final BooleanSetting useInventorySetting = new BooleanSetting("Use Inventory", this, true);
    private final BooleanSetting swapSpadeSetting = new BooleanSetting("Swap Spade", this, true);
    private final NumberSetting spadeSlotSetting = new NumberSetting("Spade Slot", this, 1.0, 1.0, 9.0, 1.0);
    private final ModeSetting equipAndRestoreSetting = new ModeSetting("Diamond Boots", this, "Equip", Arrays.asList("Off", "Equip", "Equip And Restore"));
    private final BooleanSetting protectOwnPodAndPhoenixSetting = new BooleanSetting("Protect Own Pod And Phoenix", this, true);
    private final NumberSetting swapCooldownMsSetting = new NumberSetting("Swap Cooldown (ms)", this, 1000.0, 750.0, 6000.0, 250.0);
    private final BooleanSetting manualOverrideSetting = new BooleanSetting("Manual Override", this, true);
    private final NumberSetting overrideDurationSSetting = new NumberSetting("Override Duration (s)", this, 10.0, 5.0, 30.0, 1.0);
    private final ActiveHelper activeHelper = new ActiveHelper();
    private EntityPlayer entityPlayer;
    private UUID uuid;
    private long timestamp;
    private long timestamp2;
    private static final long timestamp3 = 1500L;

    @Override
    public void onDisable() {
        if (this.manualSetting.matchesString2("Automatic")) {
            this.process3();
        }
    }

    private void process3() {
        if (this.equipAndRestoreSetting.matchesString2("Equip")) {
            this.activeHelper.handleInt(3);
        }

        this.activeHelper.process4();
    }

    public void process() {
        if (FindIdentityHelper.checkState() && !this.activeHelper.checkState()) {
            EntityPlayer player = this.computeEntityPlayer();
            this.uuid = player == null ? null : player.getUniqueID();
            this.timestamp2 = System.currentTimeMillis() + (long)(this.overrideDurationSSetting.computeDouble() * 1000.0);
            if (this.activeHelper.active()) {
                this.process3();
            } else {
                this.equip(player);
            }

            this.timestamp = System.currentTimeMillis();
        }
    }

    public DarkSwap() {
        super("DarkSwap", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.silentSetting,
                this.manualSetting,
                this.delaySetting,
                this.safeModeSetting,
                this.useInventorySetting,
                this.swapSpadeSetting,
                this.spadeSlotSetting,
                this.equipAndRestoreSetting,
                this.healthThresholdSetting,
                this.swapBackHealthSetting,
                this.swapBackSetting,
                this.distanceCheckSetting,
                this.distanceSetting,
                this.protectOwnPodAndPhoenixSetting,
                this.swapCooldownMsSetting,
                this.manualOverrideSetting,
                this.overrideDurationSSetting
            }
        );
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof RenderEvent4) {
            this.activeHelper.clear();
            this.entityPlayer = null;
            this.uuid = null;
        } else {
            StartHotbarMovesHelper.onEvent3(event);
            if (event instanceof EventUpdate
                && ((EventUpdate)event).getState() == EventState.PRE
                && this.manualSetting.matchesString2("Automatic")
                && FindIdentityHelper.checkState()
                && !this.activeHelper.checkState()) {
                if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                    this.process3();
                } else {
                    long now = System.currentTimeMillis();
                    EntityPlayer player = this.computeEntityPlayer();
                    if (!this.manualOverrideSetting.isEnabled()
                        || now >= this.timestamp2
                        || !Objects.equals(this.uuid, player == null ? null : player.getUniqueID())) {
                        if (!(now - this.timestamp < this.swapCooldownMsSetting.computeDouble())) {
                            if (this.activeHelper.active()) {
                                if (this.swapBackSetting.isEnabled()
                                    && (
                                        this.entityPlayer == null
                                            || this.entityPlayer.isDead
                                            || this.entityPlayer.worldObj != mc.theWorld
                                            || this.entityPlayer.getHealth() <= 0.0F
                                            || this.entityPlayer.getHealth() + this.entityPlayer.getAbsorptionAmount()
                                                >= this.swapBackHealthSetting.computeDouble() * 2.0
                                            || this.distanceCheckSetting.isEnabled()
                                                && mc.thePlayer.getDistanceToEntity(this.entityPlayer) > this.distanceSetting.computeDouble() + 1.0
                                    )) {
                                    this.process3();
                                    this.timestamp = now;
                                }
                            } else if (player != null
                                && FindIdentityHelper.matchesItemStack2(player.getCurrentArmor(1))
                                && !(player.getHealth() + player.getAbsorptionAmount() > this.healthThresholdSetting.computeDouble() * 2.0)
                                && (
                                    !this.distanceCheckSetting.isEnabled()
                                        || !(mc.thePlayer.getDistanceToEntity(player) > this.distanceSetting.computeDouble() - 2.0)
                                )
                                && (!this.protectOwnPodAndPhoenixSetting.isEnabled() || !FindIdentityHelper.matchesItemStack2(mc.thePlayer.getCurrentArmor(1)))
                                )
                             {
                                this.equip(player);
                            }
                        }
                    }
                }
            }
        }
    }

    private EntityPlayer computeEntityPlayer() {
        return FindIdentityHelper.computeEntityPlayer();
    }

    private void equip(EntityPlayer entityPlayer) {
        if (!this.matchesItemStack(mc.thePlayer.getCurrentArmor(1))) {
            int n = FindIdentityHelper.computeInt(this::matchesItemStack, this.useInventorySetting.isEnabled());
            if (n >= 0) {
                ArrayList list = new ArrayList();
                list.add(new StartHotbarMovesHelper.Entry3(2, n));
                int n2 = FindIdentityHelper.computeInt(stack -> stack.getItem() == Items.diamond_boots, this.useInventorySetting.isEnabled());
                if (!this.equipAndRestoreSetting.matchesString2("Off") && n2 >= 0) {
                    list.add(new StartHotbarMovesHelper.Entry3(3, n2));
                }

                ArrayList list2 = new ArrayList();
                if (this.swapSpadeSetting.isEnabled()) {
                    int n3 = FindIdentityHelper.computeInt(stack2 -> stack2.getItem() == Items.diamond_shovel, this.useInventorySetting.isEnabled());
                    if (n3 < 0) {
                        n3 = FindIdentityHelper.computeInt(stack3 -> stack3.getItem() == Items.diamond_sword, this.useInventorySetting.isEnabled());
                    }

                    if (n3 >= 0) {
                        list2.add(new StartHotbarMovesHelper.Entry(n3, (int)this.spadeSlotSetting.computeDouble() - 1));
                    }
                }

                this.activeHelper
                    .handleList(
                        list,
                        list2,
                        this.silentSetting.matchesString2("Silent"),
                        this.safeModeSetting.isEnabled(),
                        (long)this.delaySetting.computeDouble(),
                        () -> {
                            int n4 = (int)this.spadeSlotSetting.computeDouble() - 1;
                            ItemStack stack4 = mc.thePlayer.inventory.mainInventory[n4];
                            if (this.swapSpadeSetting.isEnabled()
                                && stack4 != null
                                && (stack4.getItem() == Items.diamond_shovel || stack4.getItem() == Items.diamond_sword)) {
                                PlayerHelper12.matchesObject(this.activeHelper, n4, timestamp3);
                            }
                        }
                    );
                this.entityPlayer = entityPlayer;
                this.timestamp = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.manualSetting.matchesString2("Manual")) {
            this.process();
            this.handleFlag3(false);
        }
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (!FindIdentityHelper.matchesItemStack(stack)) {
            return false;
        }
        if (!FindIdentityHelper.matchesItemStack(stack, "somber") && !GetMysticEnchantLoreHelper.matchesItemStack3(stack)) {
            for (String str : new String[]{"venom", "misery", "spite", "mind-assault", "solitude", "nostalgia", "grim-reaper", "sanguisuge"}) {
                if (FindIdentityHelper.matchesItemStack(stack, str)) {
                    return true;
                }
            }

            return false;
        }
        return true;
    }
}
