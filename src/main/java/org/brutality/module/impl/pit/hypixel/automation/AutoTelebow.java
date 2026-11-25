package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
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
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.rotation.OnJumpHelper;
import org.brutality.utils.timer.TimerUtil;

public class AutoTelebow extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean flag = false;
    private final NumberSetting healthThresholdSetting = new NumberSetting("Health Threshold", this, 10.0, 0.0, 20.0, 1.0);
    private final NumberSetting maxAngleSetting = new NumberSetting("Max Angle", this, 90.0, 30.0, 180.0, 1.0);

    private boolean matchesItemStack2(ItemStack stack) {
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

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSneak.pressed = false;
        this.flag = false;
        OnJumpHelper.disable();
    }

    private int computeInt() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (this.matchesItemStack(stack)) {
                return i;
            }
        }

        return -1;
    }

    private float[] computeFloatArray() {
        Entity entity = this.computeEntity();
        float yaw = mc.thePlayer.rotationYaw;
        float f = (float)(-this.maxAngleSetting.computeDouble());
        if (entity != null) {
            double d = entity.posX - mc.thePlayer.posX;
            double d2 = entity.posZ - mc.thePlayer.posZ;
            yaw = (float)(Math.atan2(d2, d) * (180.0 / Math.PI) - 90.0);
        }

        return !this.matchesFloat(yaw, f) ? null : new float[]{yaw, f};
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBow) {
            return stack.getDisplayName() != null && stack.getDisplayName().toLowerCase().contains("telebow")
                ? true
                : stack.hasTagCompound() && stack.getTagCompound().toString().toLowerCase().contains("telebow");
        }
        return false;
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer.getHealth() > this.healthThresholdSetting.computeDouble()) {
            if (this.flag) {
                this.process3();
            }
        } else {
            int n = this.computeInt();
            if (n == -1) {
                if (this.flag) {
                    this.process3();
                }
            } else {
                mc.thePlayer.inventory.currentItem = n;
                mc.gameSettings.keyBindSneak.pressed = true;
                float[] floats = this.computeFloatArray();
                if (floats != null) {
                    OnJumpHelper.handleFloat(floats[0], floats[1], 2);
                    OnJumpHelper.floatValueArray2 = new float[]{floats[0], Math.max(-90.0F, Math.min(90.0F, floats[1]))};
                }

                ItemStack heldItem = mc.thePlayer.getHeldItem();
                long l = this.matchesItemStack2(heldItem) ? 100L : 1000L;
                if (!this.flag) {
                    if (heldItem != null && heldItem.getItem() instanceof ItemBow) {
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
                        this.flag = true;
                        this.timerUtil.reset();
                    }
                } else if (this.timerUtil.matchesLong4(l)) {
                    this.process();
                }
            }
        }
    }

    public AutoTelebow() {
        super("Auto Telebow", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.healthThresholdSetting, this.maxAngleSetting});
    }

    @Override
    public void onEnable() {
        this.timerUtil.reset();
        this.flag = false;
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

    private void onEventPreMotion(EventPreMotion event) {
        if (this.flag && OnJumpHelper.checkState() && OnJumpHelper.floatValueArray2 != null) {
            event.setYaw(OnJumpHelper.floatValueArray2[0]);
            event.setPitch(OnJumpHelper.floatValueArray2[1]);
        }
    }

    private void process3() {
        if (this.flag) {
            mc.thePlayer.clearItemInUse();
        }

        this.flag = false;
        mc.gameSettings.keyBindSneak.pressed = false;
        OnJumpHelper.disable();
    }

    private void process() {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem != null && heldItem.getItem() instanceof ItemBow) {
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            heldItem.getItem().onPlayerStoppedUsing(heldItem, mc.theWorld, mc.thePlayer, 0);
        }

        this.flag = false;
        mc.gameSettings.keyBindSneak.pressed = false;
    }

    private Entity computeEntity() {
        Entity entity = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (Entity entity2 : mc.theWorld.loadedEntityList) {
            if (entity2 != mc.thePlayer && !entity2.isDead) {
                double dist = mc.thePlayer.getDistanceToEntity(entity2);
                if (dist < MAX_VALUE) {
                    MAX_VALUE = dist;
                    entity = entity2;
                }
            }
        }

        return entity;
    }

    private boolean matchesFloat(float f, float f2) {
        for (int i = 1; i <= 10; i++) {
            double d = mc.thePlayer.posX + i * Math.cos(Math.toRadians(f));
            double d2 = mc.thePlayer.posY + i * Math.tan(Math.toRadians(f2));
            double d3 = mc.thePlayer.posZ + i * Math.sin(Math.toRadians(f));
            BlockPos pos = new BlockPos(d, d2, d3);
            if (mc.theWorld.getBlockState(pos).getBlock().getMaterial() != Material.air) {
                return false;
            }
        }

        return true;
    }
}
