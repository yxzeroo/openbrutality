package org.brutality.module.impl.pit.weasel.utility;

import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.timer.TimerUtil;

public class XCarry extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean flag = false;
    private int count = 0;
    private int count2 = 0;
    private int count3 = -1;
    private int count4 = -1;
    private final TimerUtil timerUtil = new TimerUtil();
    private final BooleanSetting autoTransferSetting = new BooleanSetting("Auto Transfer", this, false);
    private final BooleanSetting transferLeggingsSetting = new BooleanSetting("Transfer Leggings", this, true);
    private final BooleanSetting transferHotbarSetting = new BooleanSetting("Transfer Hotbar", this, true);
    public static XCarry instance;

    private void process3() {
        EntityPlayerSP player = this.mc.thePlayer;
        switch (this.count2) {
            case 1:
                if (this.transferLeggingsSetting.isEnabled()) {
                    ItemStack stack = player.inventoryContainer.getSlot(7).getStack();
                    if (stack != null) {
                        this.count3 = 7;
                        this.count4 = 1;
                        this.count2 = 2;
                        break;
                    }
                }

                this.count2 = 3;
                break;
            case 2:
                this.count++;
                if (this.count == 2) {
                    this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count3, 0, 0, player);
                } else if (this.count == 4) {
                    this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count4, 0, 0, player);
                } else if (this.count == 6) {
                    if (player.inventory.getItemStack() != null) {
                        this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count3, 0, 0, player);
                    }

                    ModuleManager.handleString(MessageUtils.computeString("Transferred leggings to crafting slot", MessageUtils.text8));
                    this.count2 = 3;
                    this.count = 0;
                }
                break;
            case 3:
                if (this.transferHotbarSetting.isEnabled()) {
                    byte b = 36;
                    this.count3 = b;
                    this.count4 = 2;
                    this.count2 = 4;
                    this.count = 0;
                } else {
                    this.count2 = 7;
                }
                break;
            case 4:
                this.count++;
                if (this.count == 2) {
                    this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count3, 0, 0, player);
                } else if (this.count == 4) {
                    this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count4, 0, 0, player);
                } else if (this.count == 6) {
                    if (player.inventory.getItemStack() != null) {
                        this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count3, 0, 0, player);
                    }

                    ModuleManager.handleString(MessageUtils.computeString("Transferred hotbar slot 1 to crafting slot 2", MessageUtils.text8));
                    this.count2 = 5;
                    this.count = 0;
                }
                break;
            case 5:
                this.count3 = 37;
                this.count4 = 3;
                this.count2 = 6;
                this.count = 0;
                break;
            case 6:
                this.count++;
                if (this.count == 2) {
                    this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count3, 0, 0, player);
                } else if (this.count == 4) {
                    this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count4, 0, 0, player);
                } else if (this.count == 6) {
                    if (player.inventory.getItemStack() != null) {
                        this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count3, 0, 0, player);
                    }

                    ModuleManager.handleString(MessageUtils.computeString("Transferred hotbar slot 2 to crafting slot 3", MessageUtils.text8));
                    this.count2 = 7;
                    this.count = 0;
                }
                break;
            case 7:
                this.count3 = 38;
                this.count4 = 4;
                this.count2 = 8;
                this.count = 0;
                break;
            case 8:
                this.count++;
                if (this.count == 2) {
                    this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count3, 0, 0, player);
                } else if (this.count == 4) {
                    this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count4, 0, 0, player);
                } else if (this.count == 6) {
                    if (player.inventory.getItemStack() != null) {
                        this.mc.playerController.windowClick(player.inventoryContainer.windowId, this.count3, 0, 0, player);
                    }

                    ModuleManager.handleString(MessageUtils.computeString("Transferred hotbar slot 3 to crafting slot 4", MessageUtils.text8));
                    this.count2 = 9;
                    this.count = 0;
                }
                break;
            case 9:
                this.flag = true;
                this.count2 = 0;
                ModuleManager.handleString(MessageUtils.computeString("Items transferred to XCarry slots complete", MessageUtils.text11));
        }
    }

    public void process4() {
        if (this.autoTransferSetting.isEnabled()) {
            this.reset();
            this.process();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
    }

    public XCarry() {
        super("XCarry", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.autoTransferSetting, this.transferLeggingsSetting, this.transferHotbarSetting});
        instance = this;
    }

    @Generated
    public static XCarry getInstance() {
        return instance;
    }

    private boolean checkState() {
        for (int i = 1; i <= 4; i++) {
            if (this.mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventPacketSend event2) {
                if (!GetCurrentScreenHelper.checkState2()) {
                    this.process28();
                    return;
                }

                if (event2.getPacket() instanceof C0DPacketCloseWindow && this.checkState()) {
                    ModuleManager.handleString(MessageUtils.computeString("Blocked inventory close packet", MessageUtils.text8));
                    event2.setCancelled(true);
                }
            }

            if (event instanceof EventUpdate && this.autoTransferSetting.isEnabled() && this.count2 > 0) {
                this.process3();
            }
        }
    }

    private void reset() {
        this.flag = false;
        this.count = 0;
        this.count2 = 0;
        this.count3 = -1;
        this.count4 = -1;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.autoTransferSetting.isEnabled()) {
            this.reset();
            this.process();
        }
    }

    private void process() {
        if (GetCurrentScreenHelper.checkState2() && this.autoTransferSetting.isEnabled()) {
            this.flag = false;
            this.count2 = 1;
            this.count = 0;
            ModuleManager.handleString(MessageUtils.computeString("Starting item transfer to XCarry slots", MessageUtils.text11));
        }
    }
}
