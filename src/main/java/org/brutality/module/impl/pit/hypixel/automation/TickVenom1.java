package org.brutality.module.impl.pit.hypixel.automation;

import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.EntityCache;

public class TickVenom1 extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private static TickVenom1 instance;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting kosOnlySetting = new BooleanSetting("KOS Only", this, false);
    private final BooleanSetting friendsIncludedSetting = new BooleanSetting("Friends Included", this, false);
    private final NumberSetting friendsKOSDistanceSetting = new NumberSetting("Friends KOS Distance", this, 5.0, 1.0, 10.0, 1.0);
    private boolean flag = false;
    private EntityPlayer entityPlayer;
    private boolean flag2 = false;
    private int count = -1;

    private int computeInt() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null) {
                return i;
            }

            if (!(stack.getItem() instanceof ItemSword) && stack.getItem() != Items.diamond_shovel) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick) {
                if (((EventTick)event).getState() == EventState.PRE) {
                    if (mc.theWorld != null && mc.thePlayer != null) {
                        if (!this.dy()) {
                            MessageUtils.x("§c1 Tick Venom disabled — you are not wearing venom pants.");
                            this.process28();
                        } else {
                            VenomedTimer instance = VenomedTimer.getInstance();
                            if (instance != null) {
                                int n = instance.computeInt();
                                EntityPlayer player;
                                if (n >= 2) {
                                    if (this.entityPlayer == null || !this.matchesEntityPlayer(this.entityPlayer)) {
                                        this.entityPlayer = this.computeEntityPlayer();
                                    }

                                    player = this.entityPlayer;
                                } else {
                                    this.entityPlayer = null;
                                    player = this.computeEntityPlayer2();
                                }

                                KillAura.entityLivingBase3 = player;
                                this.handleEntityPlayer(player);
                                if (this.entityPlayer != null && instance.computeInt3(this.entityPlayer.getName()) > 0) {
                                    this.process28();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        KillAura.entityLivingBase3 = null;
        KillAura.flag50 = false;
        this.entityPlayer = null;
        if (this.flag2 && this.count != -1) {
            mc.thePlayer.inventory.currentItem = this.count;
        }

        this.flag2 = false;
        this.count = -1;
        if (!this.flag && KillAura.instance != null && KillAura.instance.checkState44()) {
            KillAura.instance.handleFlag3(false);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.entityPlayer = null;
        this.flag = KillAura.instance != null && KillAura.instance.checkState44();
        KillAura.flag50 = true;
        if (KillAura.instance != null && !KillAura.instance.checkState44()) {
            KillAura.instance.handleFlag3(true);
        }
    }

    private boolean matchesItemStack(ItemStack stack, Item item) {
        if (stack != null && stack.getItem() == item) {
            String displayName = stack.getDisplayName();
            if (displayName != null && displayName.contains("Fresh")) {
                return false;
            }
            var mysticEnchantLore = GetMysticEnchantLoreHelper.getMysticEnchantLore(stack);
            if (mysticEnchantLore.isEmpty()) {
                return false;
            }
            for (String str : mysticEnchantLore) {
                String str2 = str.replaceAll("§[0-9a-fk-or]", "").toLowerCase();
                if (str2.contains("venom")) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    private EntityPlayer computeEntityPlayer() {
        EntityPlayer player = null;

        for (EntityPlayer player2 : EntityCache.getEntries()) {
            if (player2 != mc.thePlayer
                && !player2.isDead
                && player2.isEntityAlive()
                && !FreshHelper.matchesWorld3(mc.theWorld, player2)
                && this.matchesEntityPlayer(player2)
                && (!this.kosOnlySetting.isEnabled() || WHelper.matchesString(player2.getName()))
                && (player == null || player2.getDistanceToEntity(mc.thePlayer) < player.getDistanceToEntity(mc.thePlayer))) {
                player = player2;
            }
        }

        return player;
    }

    private boolean matchesEntityPlayer2(EntityPlayer player) {
        return this.matchesItemStack(player.inventory.armorInventory[1], Items.leather_leggings)
            ? true
            : this.matchesItemStack(player.inventory.armorInventory[0], Items.leather_boots);
    }

    private int computeInt2() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == Items.diamond_shovel && stack.getDisplayName().contains("Combat Spade")) {
                return i;
            }
        }

        int n = -1;
        float f = -1.0F;

        for (int j = 0; j < 9; j++) {
            ItemStack stack2 = mc.thePlayer.inventory.getStackInSlot(j);
            if (stack2 != null && stack2.getItem() instanceof ItemSword) {
                float damageVsEntity = ((ItemSword)stack2.getItem()).getDamageVsEntity();
                if (damageVsEntity > f) {
                    f = damageVsEntity;
                    n = j;
                }
            }
        }

        return n;
    }

    private boolean matchesDouble(double d) {
        for (EntityPlayer player : EntityCache.getEntries()) {
            if (player != mc.thePlayer
                && !player.isDead
                && player.isEntityAlive()
                && WHelper.matchesString(player.getName())
                && player.getDistanceToEntity(mc.thePlayer) <= d) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        if (player == null || player.isDead || !player.isEntityAlive()) {
            return false;
        }
        return GetMysticEnchantLoreHelper.matchesEntityPlayer3(player) ? false : this.matchesEntityPlayer2(player);
    }

    private EntityPlayer computeEntityPlayer2() {
        EntityPlayer player = null;

        for (EntityPlayer player2 : EntityCache.getEntries()) {
            if (player2 != mc.thePlayer
                && !player2.isDead
                && player2.isEntityAlive()
                && !FreshHelper.matchesWorld3(mc.theWorld, player2)
                && !GetMysticEnchantLoreHelper.matchesEntityPlayer3(player2)
                && !this.matchesEntityPlayer2(player2)
                && (player == null || player2.getDistanceToEntity(mc.thePlayer) < player.getDistanceToEntity(mc.thePlayer))) {
                player = player2;
            }
        }

        if (player != null) {
            return player;
        }
        if (!this.friendsIncludedSetting.isEnabled()) {
            return null;
        }
        if (this.matchesDouble(this.friendsKOSDistanceSetting.computeDouble())) {
            return null;
        }
        EntityPlayer player3 = null;

        for (EntityPlayer player4 : EntityCache.getEntries()) {
            if (player4 != mc.thePlayer
                && !player4.isDead
                && player4.isEntityAlive()
                && !FreshHelper.matchesWorld3(mc.theWorld, player4)
                && DefaultHelper.matchesString(player4.getName())
                && !this.matchesEntityPlayer2(player4)
                && (player3 == null || player4.getDistanceToEntity(mc.thePlayer) < player3.getDistanceToEntity(mc.thePlayer))) {
                player3 = player4;
            }
        }

        return player3;
    }

    public TickVenom1() {
        super("1 Tick Venom", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.kosOnlySetting, this.friendsIncludedSetting, this.friendsKOSDistanceSetting});
        instance = this;
    }

    @Generated
    public static TickVenom1 getInstance() {
        return instance;
    }

    private boolean dy() {
        ItemStack stack = mc.thePlayer.inventory.armorInventory[1];
        if (stack == null) {
            return false;
        }
        for (String str : GetMysticEnchantLoreHelper.getMysticEnchantLore(stack)) {
            String str2 = str.replaceAll("§[0-9a-fk-or]", "").toLowerCase();
            if (str2.contains("venom")) {
                return true;
            }
        }

        return false;
    }

    private void handleEntityPlayer(EntityPlayer player) {
        boolean flag = player != null && DefaultHelper.matchesString(player.getName());
        if (flag) {
            if (!this.flag2) {
                int n = this.computeInt();
                if (n != -1) {
                    this.count = mc.thePlayer.inventory.currentItem;
                    mc.thePlayer.inventory.currentItem = n;
                    this.flag2 = true;
                }
            }
        } else if (this.flag2) {
            int n2 = this.computeInt2();
            if (n2 != -1) {
                mc.thePlayer.inventory.currentItem = n2;
            } else if (this.count != -1) {
                mc.thePlayer.inventory.currentItem = this.count;
            }

            this.count = -1;
            this.flag2 = false;
        } else if (player == this.entityPlayer) {
            int n3 = this.computeInt2();
            if (n3 != -1 && n3 != mc.thePlayer.inventory.currentItem) {
                mc.thePlayer.inventory.currentItem = n3;
            }
        }
    }
}
