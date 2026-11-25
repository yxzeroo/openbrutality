package org.brutality.module.impl.movement;

import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.GetCurrentScreenHelper;
import org.lwjgl.input.Keyboard;

public class Longjump extends Module {
    private int count;
    private int lastSlot = -1;
    private int count2;
    private boolean flag;
    private boolean flag2;
    private boolean flag3;
    private final NumberSetting speedSetting = new NumberSetting("Speed", this, 1.7, 0.0, 4.0, 0.1);
    private final NumberSetting motionSetting = new NumberSetting("Motion", this, 0.0, 0.0, 1.0, 0.01);
    private final NumberSetting motionTicksSetting = new NumberSetting("Motion ticks", this, 1.0, 1.0, 50.0, 1.0);

    public Longjump() {
        super("Longjump", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.speedSetting, this.motionSetting, this.motionTicksSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion event2 && event2.getState() == EventState.PRE) {
                    if (this.computeInt() != -1 && this.count < 2) {
                        mc.thePlayer.inventory.currentItem = this.computeInt();
                    }

                    if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemFireball) {
                        switch (this.count) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                event2.setPitch(90.0F);
                                mc.thePlayer.rotationPitchHead = 90.0F;
                                break;
                            case 4:
                                event2.setPitch(90.0F);
                                mc.thePlayer.rotationPitchHead = 90.0F;
                                if (!this.flag) {
                                    mc.thePlayer.swingItem();
                                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                    this.flag = true;
                                    if (mc.thePlayer.onGround) {
                                        mc.gameSettings.keyBindJump.pressed = true;
                                        mc.thePlayer.motionY = 0.421F;
                                    }
                                }
                                break;
                            case 5:
                                mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
                                if (this.lastSlot != -1) {
                                    mc.thePlayer.inventory.currentItem = this.lastSlot;
                                    this.lastSlot = -1;
                                }

                                event2.setPitch(90.0F);
                                mc.thePlayer.rotationPitchHead = 90.0F;
                        }

                        this.count++;
                    }

                    if (this.flag2) {
                        this.count2++;
                        if (this.count2 == 1) {
                            GetCurrentScreenHelper.handleString3("Applied");
                            mc.thePlayer.motionY += 2.0E-4;
                        }
                    }

                    if (this.count2 == this.motionTicksSetting.computeDouble() && this.motionSetting.computeDouble() > 0.0) {
                        this.process28();
                    } else if (this.motionSetting.computeDouble() == 0.0 && this.flag2) {
                        this.process28();
                    }
                }

                if (event instanceof GetPacketEvent && ((GetPacketEvent)event).getPacket() instanceof S12PacketEntityVelocity) {
                    if (((GetPacketEvent)event).<S12PacketEntityVelocity>getPacket().getEntityID() != mc.thePlayer.getEntityId()) {
                        return;
                    }

                    this.flag3 = true;
                    this.count2 = 0;
                    this.flag2 = true;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            this.lastSlot = mc.thePlayer.inventory.currentItem;
            this.count = 0;
            this.count2 = 0;
            this.flag2 = false;
            this.flag = false;
            this.flag3 = false;
        }
    }

    public int computeInt() {
        int n = -1;
        int n2 = 0;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemFireball) {
                int stackSize = stack.stackSize;
                if (stack.stackSize > n2) {
                    n2 = stackSize;
                    n = i;
                }
            }
        }

        return n != -1 ? n : -1;
    }
}
