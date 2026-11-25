package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.rotation.OnJumpHelper;
import org.brutality.utils.timer.TimerUtil;

public class AutoVolley extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final TimerUtil timerUtil = new TimerUtil();
    private final TimerUtil timerUtil2 = new TimerUtil();
    private boolean flag = false;
    private Entity entity = null;
    private final NumberSetting healthThresholdSetting = new NumberSetting("Health Threshold", this, 10.0, 0.0, 20.0, 1.0);
    private final NumberSetting entityRangeSetting = new NumberSetting("Entity Range", this, 16.0, 4.0, 32.0, 1.0);
    private final NumberSetting shootCooldownMsSetting = new NumberSetting("Shoot Cooldown (ms)", this, 300.0, 100.0, 1000.0, 1.0);
    private final BooleanSetting auraCheckSetting = new BooleanSetting("Aura Check", this, false);
    private final BooleanSetting rotateToTargetSetting = new BooleanSetting("Rotate To Target", this, true);

    private int computeInt() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (this.matchesItemStack2(stack)) {
                return i;
            }
        }

        return -1;
    }

    private EntityPlayer computeEntityPlayer() {
        EntityPlayer player = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer && entity != mc.thePlayer && !entity.isDead) {
                EntityPlayer player2 = (EntityPlayer)entity;
                if (!player2.capabilities.isCreativeMode && !player2.isSpectator()) {
                    double dist = mc.thePlayer.getDistanceToEntity(player2);
                    if (dist < MAX_VALUE) {
                        MAX_VALUE = dist;
                        player = player2;
                    }
                }
            }
        }

        return player;
    }

    private long computeLong(double d) {
        if (d >= 10.0) {
            return 1000L;
        }
        if (d >= 7.0) {
            return 750L;
        }
        return d >= 4.0 ? 500L : 250L;
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBow) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("pit-mystic-enchants", 10)) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("pit-mystic-enchants");
                if (compoundTag.hasKey("instant_shot")) {
                    return true;
                }
            }

            String displayName = stack.getDisplayName();
            return displayName != null && (displayName.toLowerCase().contains("mega longbow") || displayName.toLowerCase().contains("instant shot"));
        }
        return false;
    }

    private void process3() {
        if (this.flag) {
            this.flag = false;
            ItemStack heldItem = mc.thePlayer.getHeldItem();
            if (heldItem != null && heldItem.getItem() instanceof ItemBow) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                heldItem.getItem().onPlayerStoppedUsing(heldItem, mc.theWorld, mc.thePlayer, 0);
            }

            mc.thePlayer.swingItem();
        }
    }

    private void handleDouble(double d) {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        long l = this.matchesItemStack(heldItem) ? 100L : this.computeLong(d);
        if (!this.flag && this.timerUtil2.matchesLong4((long)this.shootCooldownMsSetting.computeDouble())) {
            this.process4();
        }

        if (this.flag && this.timerUtil.matchesLong4(l)) {
            this.process3();
            this.timerUtil2.reset();
        }
    }

    @Override
    public void onEnable() {
        this.timerUtil.reset();
        this.timerUtil2.reset();
        this.flag = false;
        this.entity = null;
    }

    private boolean matchesItemStack2(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBow) {
            return stack.getDisplayName() != null && stack.getDisplayName().toLowerCase().contains("volley")
                ? true
                : stack.hasTagCompound() && stack.getTagCompound().toString().toLowerCase().contains("volley");
        }
        return false;
    }

    private void process() {
        if (this.flag) {
            this.flag = false;
            if (mc.thePlayer != null) {
                mc.thePlayer.clearItemInUse();
            }

            OnJumpHelper.disable();
        }
    }

    @Override
    public void onDisable() {
        if (this.flag) {
            this.process();
        }

        this.entity = null;
        OnJumpHelper.disable();
    }

    private void onEventPreMotion(EventPreMotion event) {
        if (this.rotateToTargetSetting.isEnabled() && this.flag && OnJumpHelper.checkState() && OnJumpHelper.floatValueArray2 != null) {
            event.setYaw(OnJumpHelper.floatValueArray2[0]);
            event.setPitch(OnJumpHelper.floatValueArray2[1]);
        }
    }

    @Override
    public void onUpdate() {
        if (!this.auraCheckSetting.isEnabled() || KillAura.getInstance() == null || !KillAura.getInstance().checkState44()) {
            if (mc.thePlayer.getHealth() > this.healthThresholdSetting.computeDouble()) {
                if (this.flag) {
                    this.process();
                }
            } else {
                int n = this.computeInt();
                if (n == -1) {
                    if (this.flag) {
                        this.process();
                    }
                } else {
                    EntityPlayer player = this.computeEntityPlayer();
                    if (player == null) {
                        if (this.flag) {
                            this.process();
                        }
                    } else {
                        double dist = mc.thePlayer.getDistanceToEntity(player);
                        if (dist > this.entityRangeSetting.computeDouble()) {
                            if (this.flag) {
                                this.process();
                            }
                        } else {
                            mc.thePlayer.inventory.currentItem = n;
                            this.entity = player;
                            if (this.rotateToTargetSetting.isEnabled()) {
                                this.handleEntityPlayer(player);
                            }

                            this.handleDouble(dist);
                        }
                    }
                }
            }
        }
    }

    public String computeString() {
        if (mc.thePlayer == null) {
            return "No Player";
        }
        if (mc.thePlayer.getHealth() > this.healthThresholdSetting.computeDouble()) {
            return "Health OK";
        }
        if (this.computeInt() == -1) {
            return "No Volley Bow";
        }
        EntityPlayer player = this.computeEntityPlayer();
        if (player == null) {
            return "No Target";
        }
        double dist = mc.thePlayer.getDistanceToEntity(player);
        if (dist > this.entityRangeSetting.computeDouble()) {
            return "Target Too Far";
        }
        if (this.flag) {
            return String.format("Charging (%.1fb)", dist);
        }
        if (!this.timerUtil2.matchesLong4((long)this.shootCooldownMsSetting.computeDouble())) {
            long l = (long)this.shootCooldownMsSetting.computeDouble() - this.timerUtil2.computeLong5();
            return String.format("Cooldown (%.1fs)", l / 1000.0);
        }
        return "Ready";
    }

    public AutoVolley() {
        super("Auto Volley", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{this.healthThresholdSetting, this.entityRangeSetting, this.shootCooldownMsSetting, this.auraCheckSetting, this.rotateToTargetSetting}
        );
    }

    public boolean dx() {
        return this.flag;
    }

    private void process4() {
        this.flag = true;
        this.timerUtil.reset();
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem != null && heldItem.getItem() instanceof ItemBow) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                if (((EventUpdate)event).getState() != EventState.PRE) {
                    return;
                }

                if (mc.thePlayer == null || mc.theWorld == null) {
                    return;
                }

                this.onUpdate();
            } else if (event instanceof EventPreMotion && ((EventPreMotion)event).getState() == EventState.PRE) {
                this.onEventPreMotion((EventPreMotion)event);
            }
        }
    }

    public Entity getEntity() {
        return this.entity;
    }

    private void handleEntityPlayer(EntityPlayer player) {
        double d = player.posX - mc.thePlayer.posX;
        double d2 = player.posZ - mc.thePlayer.posZ;
        double d3 = player.posY + player.getEyeHeight() - 0.4 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dist = Math.sqrt(d * d + d2 * d2);
        float f = (float)(Math.atan2(d2, d) * (180.0 / Math.PI)) - 90.0F;
        float f2 = dist > 0.01 ? (float)(-Math.toDegrees(Math.atan2(d3, dist))) : mc.thePlayer.rotationPitch;
        OnJumpHelper.handleFloat(f, f2, 2);
        OnJumpHelper.floatValueArray2 = new float[]{f, Math.max(-90.0F, Math.min(90.0F, f2))};
    }
}
