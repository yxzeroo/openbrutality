package org.brutality.mellow.hud;

import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;

public class GetEnabledHandler {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting enabled = new BooleanSetting("Diamond Counter", null, false);
    private final NumberSetting x = new NumberSetting("X", null, 100.0, 0.0, 1000.0, 1.0);
    private final NumberSetting y = new NumberSetting("Y", null, 150.0, 0.0, 1000.0, 1.0);
    private final BooleanSetting showInventory = new BooleanSetting("Show Inventory", null, true);
    private final BooleanSetting showEnderChest = new BooleanSetting("Show Ender Chest", null, true);
    private final BooleanSetting showTotal = new BooleanSetting("Show Total", null, true);
    private int count = 0;
    private int count2 = 0;
    private static final long timestamp = -300715519680380929L;

    @Generated
    public BooleanSetting getEnabled() {
        return this.enabled;
    }

    @Generated
    public NumberSetting getX() {
        return this.x;
    }

    @Generated
    public BooleanSetting getShowEnderChest() {
        return this.showEnderChest;
    }

    @Generated
    public BooleanSetting getShowInventory() {
        return this.showInventory;
    }

    @Generated
    public NumberSetting getY() {
        return this.y;
    }

    private void process() {
        this.count = 0;
        this.count2 = 0;
        if (this.mc.thePlayer != null && this.mc.thePlayer.openContainer != null) {
            Container openContainer = this.mc.thePlayer.openContainer;

            for (int i = 0; i < openContainer.getInventory().size(); i++) {
                ItemStack stack = openContainer.getSlot(i).getStack();
                if (stack != null && stack.getItem() == Items.diamond) {
                    this.count = this.count + stack.stackSize;
                }
            }

            if (openContainer instanceof ContainerChest containerChest) {
                IInventory lowerChestInventory = containerChest.getLowerChestInventory();

                for (int j = 0; j < lowerChestInventory.getSizeInventory(); j++) {
                    ItemStack stack2 = lowerChestInventory.getStackInSlot(j);
                    if (stack2 != null && stack2.getItem() == Items.diamond) {
                        this.count2 = this.count2 + stack2.stackSize;
                    }
                }
            }
        }
    }

    @Generated
    public BooleanSetting getShowTotal() {
        return this.showTotal;
    }

    public void render() {
        if (this.enabled.isEnabled()) {
            if (this.mc.thePlayer != null) {
                this.process();
                int n = (int)this.x.computeDouble();
                int n2 = (int)this.y.computeDouble();
                int n3 = this.showInventory.isEnabled() ? this.count : 0;
                int n4 = this.showEnderChest.isEnabled() ? this.count2 : 0;
                int n5 = this.showTotal.isEnabled() ? n3 + n4 : 0;
                int n6 = (int)timestamp;
                if (this.showInventory.isEnabled()) {
                    this.mc.fontRendererObj.drawStringWithShadow("Inv: " + n3, n, n2, n6);
                    n2 += 12;
                }

                if (this.showEnderChest.isEnabled()) {
                    this.mc.fontRendererObj.drawStringWithShadow("EC: " + n4, n, n2, n6);
                    n2 += 12;
                }

                if (this.showTotal.isEnabled()) {
                    this.mc.fontRendererObj.drawStringWithShadow("Total: " + n5, n, n2, n6);
                }
            }
        }
    }
}
