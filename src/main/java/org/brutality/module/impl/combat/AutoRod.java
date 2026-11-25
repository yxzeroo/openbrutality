package org.brutality.module.impl.combat;

import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.rotation.GetVectorForRotationHelper;
import org.brutality.utils.rotation.OnJumpHelper;

public class AutoRod extends Module {
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 6.0, 2.0, 12.0, 0.5);
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 350.0, 100.0, 1500.0, 50.0);
    private final ModeSetting cameraSetting = new ModeSetting("Rotation", this, "Silent", Arrays.asList("Silent", "Camera"));
    private long timestamp;

    @Override
    public void onDisable() {
        super.onDisable();
        OnJumpHelper.disable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.timestamp = 0L;
    }

    private int computeInt() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == Items.fishing_rod) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
            if (mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null) {
                if (KillAura.instance == null || !KillAura.instance.checkState44() || KillAura.entityLivingBase2 == null) {
                    if (System.currentTimeMillis() >= this.timestamp) {
                        EntityPlayer player = this.computeEntityPlayer();
                        int n = this.computeInt();
                        if (player != null && n != -1) {
                            float[] floats = GetVectorForRotationHelper.computeFloatArray(player);
                            if (floats != null) {
                                if (this.cameraSetting.matchesString2("Silent")) {
                                    OnJumpHelper.handleFloat(floats[0], floats[1], 1);
                                } else {
                                    mc.thePlayer.rotationYaw = floats[0];
                                    mc.thePlayer.rotationPitch = floats[1];
                                }

                                int currentItem = mc.thePlayer.inventory.currentItem;
                                mc.thePlayer.inventory.currentItem = n;
                                mc.playerController.updateController();
                                ItemStack heldItem = mc.thePlayer.getHeldItem();
                                if (heldItem != null && heldItem.getItem() == Items.fishing_rod) {
                                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
                                }

                                mc.thePlayer.inventory.currentItem = currentItem;
                                mc.playerController.updateController();
                                this.timestamp = System.currentTimeMillis() + (long)this.delaySetting.computeDouble();
                            }
                        }
                    }
                }
            }
        }
    }

    public AutoRod() {
        super("AutoRod", 0, Category.COMBAT);
        this.handleSettingArray(new Setting[]{this.rangeSetting, this.delaySetting, this.cameraSetting});
    }

    private EntityPlayer computeEntityPlayer() {
        EntityPlayer player = null;
        double d = this.rangeSetting.computeDouble();

        for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
            if (playerEntity != mc.thePlayer
                && !playerEntity.isDead
                && playerEntity.isEntityAlive()
                && !playerEntity.getDisplayName().getUnformattedText().contains("[NPC]")
                && !DefaultHelper.matchesString(playerEntity.getName())) {
                double dist = mc.thePlayer.getDistanceToEntity(playerEntity);
                if (dist <= d) {
                    d = dist;
                    player = playerEntity;
                }
            }
        }

        return player;
    }
}
