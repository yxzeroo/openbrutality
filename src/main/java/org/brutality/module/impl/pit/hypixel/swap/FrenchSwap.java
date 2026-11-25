package org.brutality.module.impl.pit.hypixel.swap;

import java.util.Arrays;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.combat.CombatHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class FrenchSwap extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private final ModeSetting silentSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Silent"));
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 100.0, 1.0, 500.0, 50.0);
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 8.0, 2.0, 30.0, 1.0);
    private final BooleanSetting swapWhenNearSetting = new BooleanSetting("Swap When Near", this, true);
    private final BooleanSetting swapWhenAttackedSetting = new BooleanSetting("Swap When Attacked", this, true);
    private final BooleanSetting safeModeSetting = new BooleanSetting("Safe Mode", this, true);
    private final BooleanSetting swapBackSetting = new BooleanSetting("Swap Back", this, true);
    private EntityPlayer entityPlayer;
    private long timestamp;
    private ItemStack itemStack;
    private boolean flag;

    private boolean matchesItemStack3(ItemStack stack) {
        return stack.getDisplayName() != null && stack.getDisplayName().toLowerCase().contains("french")
            ? true
            : stack.hasTagCompound() && this.matchesNBTTagCompound(stack.getTagCompound());
    }

    private boolean matchesItemStack2(ItemStack stack) {
        return stack != null
            && stack.getItem() instanceof ItemArmor
            && ((ItemArmor)stack.getItem()).armorType == 0
            && ((ItemArmor)stack.getItem()).getArmorMaterial() == ArmorMaterial.DIAMOND;
    }

    private int computeInt() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && this.matchesItemStack2(stack)) {
                return i < 9 ? 36 + i : i;
            }
        }

        return -1;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            StartHotbarMovesHelper.onEvent3(event);
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                if (mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null && !StartHotbarMovesHelper.flag) {
                    Entity entity2 = CombatHelper.getEntity2();
                    if (this.swapWhenAttackedSetting.isEnabled()
                        && entity2 instanceof EntityPlayer
                        && CombatHelper.matchesLong2(2000L)
                        && this.matchesEntityPlayer((EntityPlayer)entity2)) {
                        this.entityPlayer = (EntityPlayer)entity2;
                        this.timestamp = System.currentTimeMillis();
                    }

                    if (this.swapWhenAttackedSetting.isEnabled() && mc.thePlayer.hurtTime > 0) {
                        EntityPlayer player = this.computeEntityPlayer();
                        if (player != null) {
                            this.entityPlayer = player;
                            this.timestamp = System.currentTimeMillis();
                        }
                    }

                    if (this.flag) {
                        if (this.swapBackSetting.isEnabled() && !this.checkState()) {
                            this.process();
                        }
                    } else if (this.matchesItemStack(mc.thePlayer.getCurrentArmor(3)) && this.checkState()) {
                        int n = this.computeInt();
                        if (n != -1) {
                            this.itemStack = mc.thePlayer.getCurrentArmor(3);
                            this.flag = true;
                            this.handleEntry3(new StartHotbarMovesHelper.Entry3(0, n), null);
                        }
                    }
                }
            }
        }
    }

    private boolean checkState() {
        if (this.swapWhenAttackedSetting.isEnabled()
            && this.entityPlayer != null
            && !this.entityPlayer.isDead
            && System.currentTimeMillis() - this.timestamp <= 2000L) {
            return true;
        }
        if (this.swapWhenNearSetting.isEnabled() && mc.theWorld != null) {
            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity != mc.thePlayer
                    && !playerEntity.isDead
                    && this.matchesEntityPlayer(playerEntity)
                    && mc.thePlayer.getDistanceToEntity(playerEntity) <= this.rangeSetting.computeDouble()) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    public FrenchSwap() {
        super("FrenchSwap", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.silentSetting,
                this.delaySetting,
                this.rangeSetting,
                this.swapWhenNearSetting,
                this.swapWhenAttackedSetting,
                this.safeModeSetting,
                this.swapBackSetting
            }
        );
    }

    private void handleEntry3(StartHotbarMovesHelper.Entry3 entry3, Runnable task) {
        if (this.silentSetting.matchesString2("Silent")) {
            StartHotbarMovesHelper.handleInt2(
                entry3.count, entry3.count2, this.safeModeSetting.isEnabled(), (long)this.delaySetting.computeDouble(), true, true, task
            );
        } else {
            StartHotbarMovesHelper.handleInt(
                entry3.count, entry3.count2, this.safeModeSetting.isEnabled(), (long)this.delaySetting.computeDouble(), true, true, task
            );
        }
    }

    private void process() {
        if (this.itemStack == null) {
            this.flag = false;
        } else {
            int n = StartHotbarMovesHelper.computeInt3(this.itemStack, 0);
            if (n == -1) {
                this.flag = false;
                this.itemStack = null;
            } else {
                this.handleEntry3(new StartHotbarMovesHelper.Entry3(0, n), () -> {
                    this.flag = false;
                    this.itemStack = null;
                });
            }
        }
    }

    private EntityPlayer computeEntityPlayer() {
        EntityPlayer player = null;
        double d = this.rangeSetting.computeDouble();

        for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
            if (playerEntity != mc.thePlayer && !playerEntity.isDead && this.matchesEntityPlayer(playerEntity)) {
                double dist = mc.thePlayer.getDistanceToEntity(playerEntity);
                if (dist <= d) {
                    d = dist;
                    player = playerEntity;
                }
            }
        }

        return player;
    }

    private boolean matchesNBTTagCompound(NBTTagCompound nbt) {
        if (nbt.toString().toLowerCase().contains("french")) {
            return true;
        }
        for (String str : nbt.getKeySet()) {
            NBTBase tag = nbt.getTag(str);
            if (tag instanceof NBTTagCompound && this.matchesNBTTagCompound((NBTTagCompound)tag)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        ItemStack stack = player.inventory.armorInventory[1];
        return stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == 2 && this.matchesItemStack3(stack);
    }

    @Override
    public void onEnable() {
        this.entityPlayer = null;
        this.timestamp = 0L;
        this.itemStack = null;
        this.flag = false;
    }

    private boolean matchesItemStack(ItemStack stack) {
        return stack != null
            && stack.getItem() instanceof ItemArmor
            && ((ItemArmor)stack.getItem()).armorType == 0
            && (((ItemArmor)stack.getItem()).getArmorMaterial() == ArmorMaterial.GOLD || stack.getDisplayName().toLowerCase().contains("golden helmet"));
    }

    @Override
    public void onDisable() {
        this.entityPlayer = null;
        this.itemStack = null;
        this.flag = false;
    }
}
