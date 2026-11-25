package org.brutality.module.impl.player;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;

public class AutoPolicies extends Module {
    private boolean flag = false;

    private void process() {
        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.openContainer.windowId));
    }

    @Override
    public void process28() {
    }

    private boolean checkState() {
        ItemStack currentEquippedItem = mc.thePlayer.getCurrentEquippedItem();
        if (currentEquippedItem != null && currentEquippedItem.getItem() == Items.written_book) {
            if (!currentEquippedItem.hasTagCompound()) {
                return false;
            }

            NBTTagCompound tagCompound = currentEquippedItem.getTagCompound();
            NBTTagList tagList = tagCompound.getTagList("pages", 8);
            if (tagList != null) {
                String stringTagAt = tagList.getStringTagAt(0);
                if (stringTagAt.contains("/policies accept")) {
                    MessageUtils.x(MessageUtils.text13 + "[AutoPolicies] " + MessageUtils.text8 + "Policies book detected, accepting...");
                    mc.thePlayer.sendChatMessage("/policies accept");
                    MessageUtils.x(MessageUtils.text13 + "[AutoPolicies] " + MessageUtils.text11 + "Policies accepted!");
                    this.process();
                    return true;
                }
            }
        }

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == Items.written_book && stack.hasTagCompound()) {
                NBTTagCompound tagCompound2 = stack.getTagCompound();
                NBTTagList tagList2 = tagCompound2.getTagList("pages", 8);
                if (tagList2 != null && tagList2.tagCount() > 0) {
                    String stringTagAt2 = tagList2.getStringTagAt(0);
                    if (stringTagAt2.contains("/policies accept")) {
                        MessageUtils.x(MessageUtils.text13 + "[AutoPolicies] " + MessageUtils.text8 + "Policies book detected in inventory, accepting...");
                        mc.thePlayer.sendChatMessage("/policies accept");
                        MessageUtils.x(MessageUtils.text13 + "[AutoPolicies] " + MessageUtils.text11 + "Policies accepted!");
                        this.process();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public AutoPolicies() {
        super("AutoPolicies", 0, Category.PLAYER);
        this.handleFlag3(true);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof GetPacketEvent getPacketEvent && getPacketEvent.getPacket() instanceof S3FPacketCustomPayload) {
                    S3FPacketCustomPayload packet = getPacketEvent.getPacket();
                    String channelName = packet.getChannelName();
                    if (channelName.equals("MC|BOpen")) {
                        this.flag = true;
                    }
                }

                if (event instanceof EventUpdate && this.flag && this.checkState()) {
                    this.flag = false;
                }
            }
        }
    }
}
