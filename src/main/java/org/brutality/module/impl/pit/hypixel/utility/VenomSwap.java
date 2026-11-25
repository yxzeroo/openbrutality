package org.brutality.module.impl.pit.hypixel.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.ActiveHelper;
import org.brutality.utils.player.FindIdentityHelper;
import org.brutality.utils.player.PlayerHelper12;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class VenomSwap extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int count = 9;
    private static final int count2 = 8;
    private static final int count3 = 7;
    private static final int count4 = 6;
    private static final int count5 = 5;
    private static final int count6 = 0;
    private final ModeSetting silentSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Silent"));
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 100.0, 1.0, 500.0, 50.0);
    private final BooleanSetting safeModeSetting = new BooleanSetting("Safe Mode", this, true);
    private final BooleanSetting useInventorySetting = new BooleanSetting("Use Inventory", this, true);
    private final BooleanSetting swapWhenVenomedVictimSetting = new BooleanSetting("Swap When Venomed (Victim)", this, true);
    private final BooleanSetting swapBackVictimSetting = new BooleanSetting("Swap Back (Victim)", this, true);
    private final BooleanSetting swapSpadeVictimSetting = new BooleanSetting("Swap Spade (Victim)", this, false);
    private final NumberSetting spadeSlotSetting = new NumberSetting("Spade Slot", this, 1.0, 1.0, 9.0, 1.0);
    private final BooleanSetting swapEggVictimSetting = new BooleanSetting("Swap Egg (Victim)", this, false);
    private final NumberSetting eggSlotSetting = new NumberSetting("Egg Slot", this, 8.0, 1.0, 9.0, 1.0);
    private final BooleanSetting swapBedrockVictimSetting = new BooleanSetting("Swap Bedrock (Victim)", this, false);
    private final NumberSetting bedrockSlotSetting = new NumberSetting("Bedrock Slot", this, 9.0, 1.0, 9.0, 1.0);
    private final BooleanSetting swapToVenomHunterSetting = new BooleanSetting("Swap To Venom (Hunter)", this, false);
    private final BooleanSetting swapBackVenomHunterSetting = new BooleanSetting("Swap Back Venom (Hunter)", this, true);
    private final BooleanSetting swapOnVenomProcHunterSetting = new BooleanSetting("Swap On Venom Proc (Hunter)", this, false);
    private final KeybindListSetting chainmailLeggingsSetting = new KeybindListSetting(
        "Alternative Pants",
        this,
        new BooleanSetting("Diamond Leggings", this, true),
        new BooleanSetting("Iron Leggings", this, true),
        new BooleanSetting("Fresh Pants", this, false),
        new BooleanSetting("Chainmail Leggings", this, false)
    );
    private final BooleanSetting swapWhenSprintJumpingSetting = new BooleanSetting("Swap When Sprint Jumping", this, false);
    private final ActiveHelper activeHelper = new ActiveHelper();
    private final ActiveHelper activeHelper2 = new ActiveHelper();
    private boolean flag;
    private boolean flag2;
    private EntityPlayer entityPlayer;
    private Object object;
    private Object object2;
    private volatile boolean flag3 = false;
    private boolean flag4 = false;
    private static VenomSwap instance;
    private static final long timestamp = 1500L;

    private void handleList(List<StartHotbarMovesHelper.Entry3> entry3s, Runnable task) {
        if (entry3s != null && !entry3s.isEmpty()) {
            if (this.silentSetting.matchesString2("Silent")) {
                StartHotbarMovesHelper.startSwap(entry3s, this.safeModeSetting.isEnabled(), (long)this.delaySetting.computeDouble(), true, true, task);
            } else {
                StartHotbarMovesHelper.handleList(entry3s, this.safeModeSetting.isEnabled(), (long)this.delaySetting.computeDouble(), true, true, task);
            }
        }
    }

    private void process5() {
        if (!this.activeHelper.active() && !this.activeHelper.checkState()) {
            this.flag3 = true;
            ArrayList list = new ArrayList();
            int n = this.computeInt5();
            int n2 = this.computeInt();
            if (n >= 0) {
                list.add(new StartHotbarMovesHelper.Entry3(2, n));
            }

            if (n2 >= 0) {
                list.add(new StartHotbarMovesHelper.Entry3(3, n2));
            }

            this.activeHelper
                .handleList(
                    list,
                    this.computeList(),
                    this.silentSetting.matchesString2("Silent"),
                    this.safeModeSetting.isEnabled(),
                    (long)this.delaySetting.computeDouble(),
                    () -> {
                        int n3 = (int)this.spadeSlotSetting.computeDouble() - 1;
                        ItemStack stack = mc.thePlayer.inventory.mainInventory[n3];
                        if (this.swapSpadeVictimSetting.isEnabled()
                            && stack != null
                            && (stack.getItem() == Items.diamond_shovel || stack.getItem() == Items.diamond_sword)) {
                            PlayerHelper12.matchesObject(this.activeHelper, n3, timestamp);
                        }
                    }
                );
        }
    }

    private int findItemContainerSlot(Item item) {
        return FindIdentityHelper.computeInt(stack -> stack.getItem() == item, this.useInventorySetting.isEnabled());
    }

    private void process4() {
        this.activeHelper.process4();
    }

    private void process() {
        this.flag3 = false;
        this.flag = this.flag2 = false;
        this.entityPlayer = null;
        this.flag4 = false;
    }

    private int computeInt(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemArmor) {
            ArmorMaterial armorMaterial = ((ItemArmor)stack.getItem()).getArmorMaterial();
            if (armorMaterial == ArmorMaterial.DIAMOND) {
                return 9;
            }
            if (armorMaterial == ArmorMaterial.IRON) {
                return 8;
            }
            if (armorMaterial == ArmorMaterial.CHAIN) {
                return 6;
            }
            return armorMaterial == ArmorMaterial.LEATHER ? 5 : 0;
        }
        return 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.activeHelper.process4();
        this.activeHelper2.process4();
        this.process();
    }

    private void process3() {
        int n = this.computeInt4();
        if (n >= 0 && !this.activeHelper2.active()) {
            this.activeHelper2
                .handleList(
                    Collections.singletonList(new StartHotbarMovesHelper.Entry3(2, n)),
                    Collections.emptyList(),
                    this.silentSetting.matchesString2("Silent"),
                    this.safeModeSetting.isEnabled(),
                    (long)this.delaySetting.computeDouble()
                );
            this.flag4 = this.activeHelper2.active();
        }
    }

    private int computeInt4() {
        if (mc.thePlayer == null) {
            return -1;
        }
        int n = this.useInventorySetting.isEnabled() ? 36 : 9;

        for (int i = 0; i < n; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == 2 && this.matchesItemStack(stack)) {
                return i < 9 ? 36 + i : i;
            }
        }

        return -1;
    }

    private void process8() {
        if (this.activeHelper2.active()) {
            this.process6();
        } else {
            int n = this.computeInt5();
            if (n != -1) {
                ArrayList list = new ArrayList();
                list.add(new StartHotbarMovesHelper.Entry3(2, n));
                this.handleList(list, null);
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.process();
    }

    private int computeInt5() {
        return this.computeInt(2, mc.thePlayer.inventory.armorInventory[1], false);
    }

    private boolean checkState2() {
        for (PotionEffect activePotionEffect : mc.thePlayer.getActivePotionEffects()) {
            if (activePotionEffect.getPotionID() == Potion.moveSpeed.getId() && activePotionEffect.getDuration() > 0) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        VenomedTimer instance = VenomedTimer.getInstance();
        if (instance != null && instance.computeInt3(player.getName()) > 0 && !instance.matchesString(player.getName())) {
            return true;
        }
        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            if (activePotionEffect.getPotionID() == Potion.poison.getId()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            StartHotbarMovesHelper.onEvent3(event);
            if (event instanceof RenderEvent4 || this.object != mc.thePlayer || this.object2 != mc.theWorld) {
                this.activeHelper.clear();
                this.activeHelper2.clear();
                this.process();
                this.object = mc.thePlayer;
                this.object2 = mc.theWorld;
                if (event instanceof RenderEvent4) {
                    return;
                }
            }

            if (event instanceof GetEntityEvent getEntityEvent) {
                if (getEntityEvent.getEntity() == mc.thePlayer) {
                    this.process7();
                }
            } else if (!StartHotbarMovesHelper.flag) {
                if (event instanceof EventUpdate event2) {
                    if (event2.getState() != EventState.PRE) {
                        return;
                    }

                    if (mc.thePlayer == null || mc.theWorld == null) {
                        return;
                    }

                    if (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0.0F) {
                        this.process7();
                        return;
                    }

                    if (!FindIdentityHelper.checkState()) {
                        return;
                    }

                    this.onTick();
                }
            }
        }
    }

    private int computeInt(int n, ItemStack stack, boolean flag) {
        int n2 = -1;
        int n3 = flag ? this.computeInt(stack) : this.computeInt2(stack);
        int n4 = this.useInventorySetting.isEnabled() ? 36 : 9;

        for (int i = 0; i < n4; i++) {
            ItemStack stack2 = mc.thePlayer.inventory.mainInventory[i];
            if (stack2 != null && stack2.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor)stack2.getItem();
                if (item.armorType == n && !this.matchesItemStack(stack2)) {
                    int n5 = flag ? this.computeInt(stack2) : this.computeInt2(stack2);
                    if (n5 > n3) {
                        n3 = n5;
                        n2 = i < 9 ? 36 + i : i;
                    }
                }
            }
        }

        return n2;
    }

    private int computeInt2(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemArmor)) {
            return 0;
        }
        if (this.matchesItemStack(stack)) {
            return 0;
        }
        ItemArmor item = (ItemArmor)stack.getItem();
        ArmorMaterial armorMaterial = item.getArmorMaterial();
        BooleanSetting boolSetting = (BooleanSetting)this.chainmailLeggingsSetting.computeSetting("Fresh Pants");
        if (boolSetting != null && boolSetting.isEnabled() && stack.hasDisplayName() && stack.getDisplayName().toLowerCase().contains("fresh")) {
            return 7;
        }
        if (armorMaterial == ArmorMaterial.DIAMOND) {
            return ((BooleanSetting)this.chainmailLeggingsSetting.computeSetting("Diamond Leggings")).isEnabled() ? 9 : 0;
        }
        if (armorMaterial == ArmorMaterial.IRON) {
            BooleanSetting boolSetting2 = (BooleanSetting)this.chainmailLeggingsSetting.computeSetting("Iron Leggings");
            if (boolSetting2 != null && boolSetting2.isEnabled()) {
                return 8;
            }
        }

        if (armorMaterial == ArmorMaterial.CHAIN) {
            BooleanSetting boolSetting3 = (BooleanSetting)this.chainmailLeggingsSetting.computeSetting("Chainmail Leggings");
            if (boolSetting3 != null && boolSetting3.isEnabled()) {
                return 6;
            }
        }

        return armorMaterial == ArmorMaterial.LEATHER ? 5 : 0;
    }

    private List<StartHotbarMovesHelper.Entry> computeList() {
        ArrayList list = new ArrayList();
        if (this.swapSpadeVictimSetting.isEnabled()) {
            int n = this.computeInt2();
            int n2 = (int)this.spadeSlotSetting.computeDouble() - 1;
            if (n != -1) {
                list.add(new StartHotbarMovesHelper.Entry(n, n2));
            }
        }

        if (this.swapEggVictimSetting.isEnabled()) {
            int n3 = this.findItemContainerSlot(Items.spawn_egg);
            int n4 = (int)this.eggSlotSetting.computeDouble() - 1;
            if (n3 != -1) {
                list.add(new StartHotbarMovesHelper.Entry(n3, n4));
            }
        }

        if (this.swapBedrockVictimSetting.isEnabled()) {
            int n5 = this.findItemContainerSlot(Item.getItemFromBlock(Blocks.bedrock));
            int n6 = (int)this.bedrockSlotSetting.computeDouble() - 1;
            if (n5 != -1) {
                list.add(new StartHotbarMovesHelper.Entry(n5, n6));
            }
        }

        return list;
    }

    private int computeInt() {
        return this.computeInt(3, mc.thePlayer.inventory.armorInventory[0], true);
    }

    private void process7() {
        this.activeHelper.clear();
        this.activeHelper2.clear();
        this.process();
    }

    private void process6() {
        this.activeHelper2.process4();
        this.flag4 = false;
    }

    private boolean checkState() {
        return !this.swapWhenSprintJumpingSetting.isEnabled() || mc.thePlayer.isSprinting() && !mc.thePlayer.onGround && mc.thePlayer.motionY > 0.0;
    }

    @Generated
    public static VenomSwap getInstance() {
        return instance;
    }

    private boolean matchesItemStack(ItemStack stack) {
        return FindIdentityHelper.matchesItemStack(stack, "venom");
    }

    private int computeInt2() {
        int n = this.findItemContainerSlot(Items.diamond_shovel);
        return n != -1 ? n : this.findItemContainerSlot(Items.diamond_sword);
    }

    private PotionEffect computePotionEffect() {
        for (PotionEffect activePotionEffect : mc.thePlayer.getActivePotionEffects()) {
            if (activePotionEffect.getPotionID() == Potion.poison.getId()) {
                return activePotionEffect;
            }
        }

        return null;
    }

    private int computeInt3() {
        PotionEffect effect = this.computePotionEffect();
        return effect == null ? 0 : effect.getDuration();
    }

    private void onTick() {
        EntityPlayer player = FindIdentityHelper.computeEntityPlayer();
        int n = this.computeInt3();
        if (n <= 0) {
            this.flag = false;
            if (this.flag3 && this.swapBackVictimSetting.isEnabled()) {
                this.flag2 = true;
            } else if (this.flag3) {
                this.flag3 = false;
                this.activeHelper.clear();
            }
        } else if (!this.flag3 && this.swapWhenVenomedVictimSetting.isEnabled() && n >= 200) {
            this.flag = true;
        }

        if (this.flag2) {
            if (this.checkState()) {
                this.flag2 = false;
                this.flag3 = false;
                this.process4();
            }
        } else if (this.flag) {
            if (this.checkState()) {
                this.flag = false;
                this.process5();
            }
        } else if (!this.flag3 && !this.activeHelper.active() && !this.activeHelper.checkState()) {
            if (this.swapOnVenomProcHunterSetting.isEnabled()
                && this.checkState2()
                && player != null
                && player != this.entityPlayer
                && this.matchesEntityPlayer(player)) {
                this.process8();
                this.entityPlayer = player;
            } else {
                if (player == null || !this.matchesEntityPlayer(player)) {
                    this.entityPlayer = null;
                }

                if (this.swapToVenomHunterSetting.isEnabled()) {
                    if (!this.flag4 && player != null && player != this.entityPlayer && !this.matchesEntityPlayer(player) && this.checkState()) {
                        this.process3();
                    } else if (this.flag4 && this.swapBackVenomHunterSetting.isEnabled() && (player == null || player == this.entityPlayer) && this.checkState()) {
                        this.process6();
                    }
                }
            }
        }
    }

    public VenomSwap() {
        super("VenomSwap", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.silentSetting,
                this.delaySetting,
                this.safeModeSetting,
                this.useInventorySetting,
                this.swapWhenVenomedVictimSetting,
                this.swapBackVictimSetting,
                this.swapSpadeVictimSetting,
                this.spadeSlotSetting,
                this.swapEggVictimSetting,
                this.eggSlotSetting,
                this.swapBedrockVictimSetting,
                this.bedrockSlotSetting,
                this.swapToVenomHunterSetting,
                this.swapBackVenomHunterSetting,
                this.swapOnVenomProcHunterSetting,
                this.swapWhenSprintJumpingSetting,
                this.chainmailLeggingsSetting
            }
        );
        instance = this;
    }
}
