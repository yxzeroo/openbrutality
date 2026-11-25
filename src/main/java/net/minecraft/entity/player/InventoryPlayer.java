package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;
import org.brutality.event.impl.player.PlayerEvent4;

public class InventoryPlayer implements IInventory {
    public ItemStack[] mainInventory = new ItemStack[36];
    public ItemStack[] armorInventory = new ItemStack[4];
    public int currentItem;
    public EntityPlayer player;
    private ItemStack itemStack;
    public boolean inventoryChanged;

    public InventoryPlayer(EntityPlayer playerIn) {
        this.player = playerIn;
    }

    public ItemStack getCurrentItem() {
        return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
    }

    public static int getHotbarSize() {
        return 9;
    }

    private int getInventorySlotContainItem(Item itemIn) {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == itemIn) {
                return i;
            }
        }

        return -1;
    }

    private int getInventorySlotContainItemAndDamage(Item itemIn, int p_146024_2_) {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == itemIn && this.mainInventory[i].getMetadata() == p_146024_2_) {
                return i;
            }
        }

        return -1;
    }

    private int storeItemStack(ItemStack itemStackIn) {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null
                && this.mainInventory[i].getItem() == itemStackIn.getItem()
                && this.mainInventory[i].isStackable()
                && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize()
                && this.mainInventory[i].stackSize < this.getInventoryStackLimit()
                && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getMetadata() == itemStackIn.getMetadata())
                && ItemStack.areItemStackTagsEqual(this.mainInventory[i], itemStackIn)) {
                return i;
            }
        }

        return -1;
    }

    public int getFirstEmptyStack() {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] == null) {
                return i;
            }
        }

        return -1;
    }

    public void setCurrentItem(Item itemIn, int p_146030_2_, boolean p_146030_3_, boolean p_146030_4_) {
        ItemStack currentItem = this.getCurrentItem();
        int n = p_146030_3_ ? this.getInventorySlotContainItemAndDamage(itemIn, p_146030_2_) : this.getInventorySlotContainItem(itemIn);
        if (n >= 0 && n < 9) {
            this.currentItem = n;
        } else if (p_146030_4_ && itemIn != null) {
            int firstEmptyStack = this.getFirstEmptyStack();
            if (firstEmptyStack >= 0 && firstEmptyStack < 9) {
                this.currentItem = firstEmptyStack;
            }

            if (currentItem == null
                || !currentItem.isItemEnchantable()
                || this.getInventorySlotContainItemAndDamage(currentItem.getItem(), currentItem.getItemDamage()) != this.currentItem) {
                int inventorySlotContainItemAndDamage = this.getInventorySlotContainItemAndDamage(itemIn, p_146030_2_);
                int n2;
                if (inventorySlotContainItemAndDamage >= 0) {
                    n2 = this.mainInventory[inventorySlotContainItemAndDamage].stackSize;
                    this.mainInventory[inventorySlotContainItemAndDamage] = this.mainInventory[this.currentItem];
                } else {
                    n2 = 1;
                }

                this.mainInventory[this.currentItem] = new ItemStack(itemIn, n2, p_146030_2_);
            }
        }
    }

    public void changeCurrentItem(int direction) {
        PlayerEvent4 playerEvent4 = new PlayerEvent4(-1, direction);
        playerEvent4.process();
        if (!playerEvent4.cancel2()) {
            if (direction > 0) {
                direction = 1;
            }

            if (direction < 0) {
                direction = -1;
            }

            this.currentItem -= direction;

            while (this.currentItem < 0) {
                this.currentItem += 9;
            }

            while (this.currentItem >= 9) {
                this.currentItem -= 9;
            }
        }
    }

    public int clearMatchingItems(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT) {
        int n = 0;

        for (int i = 0; i < this.mainInventory.length; i++) {
            ItemStack stack = this.mainInventory[i];
            if (stack != null
                && (itemIn == null || stack.getItem() == itemIn)
                && (metadataIn <= -1 || stack.getMetadata() == metadataIn)
                && (itemNBT == null || NBTUtil.func_181123_a(itemNBT, stack.getTagCompound(), true))) {
                int n2 = removeCount <= 0 ? stack.stackSize : Math.min(removeCount - n, stack.stackSize);
                n += n2;
                if (removeCount != 0) {
                    this.mainInventory[i].stackSize -= n2;
                    if (this.mainInventory[i].stackSize == 0) {
                        this.mainInventory[i] = null;
                    }

                    if (removeCount > 0 && n >= removeCount) {
                        return n;
                    }
                }
            }
        }

        for (int j = 0; j < this.armorInventory.length; j++) {
            ItemStack stack2 = this.armorInventory[j];
            if (stack2 != null
                && (itemIn == null || stack2.getItem() == itemIn)
                && (metadataIn <= -1 || stack2.getMetadata() == metadataIn)
                && (itemNBT == null || NBTUtil.func_181123_a(itemNBT, stack2.getTagCompound(), false))) {
                int n3 = removeCount <= 0 ? stack2.stackSize : Math.min(removeCount - n, stack2.stackSize);
                n += n3;
                if (removeCount != 0) {
                    this.armorInventory[j].stackSize -= n3;
                    if (this.armorInventory[j].stackSize == 0) {
                        this.armorInventory[j] = null;
                    }

                    if (removeCount > 0 && n >= removeCount) {
                        return n;
                    }
                }
            }
        }

        if (this.itemStack != null) {
            if (itemIn != null && this.itemStack.getItem() != itemIn) {
                return n;
            }

            if (metadataIn > -1 && this.itemStack.getMetadata() != metadataIn) {
                return n;
            }

            if (itemNBT != null && !NBTUtil.func_181123_a(itemNBT, this.itemStack.getTagCompound(), false)) {
                return n;
            }

            int n4 = removeCount <= 0 ? this.itemStack.stackSize : Math.min(removeCount - n, this.itemStack.stackSize);
            n += n4;
            if (removeCount != 0) {
                this.itemStack.stackSize -= n4;
                if (this.itemStack.stackSize == 0) {
                    this.itemStack = null;
                }

                if (removeCount > 0 && n >= removeCount) {
                    return n;
                }
            }
        }

        return n;
    }

    private int storePartialItemStack(ItemStack itemStackIn) {
        Item item = itemStackIn.getItem();
        int stackSize = itemStackIn.stackSize;
        int n = this.storeItemStack(itemStackIn);
        if (n < 0) {
            n = this.getFirstEmptyStack();
        }

        if (n < 0) {
            return stackSize;
        }
        if (this.mainInventory[n] == null) {
            this.mainInventory[n] = new ItemStack(item, 0, itemStackIn.getMetadata());
            if (itemStackIn.hasTagCompound()) {
                this.mainInventory[n].setTagCompound((NBTTagCompound)itemStackIn.getTagCompound().copy());
            }
        }

        int n2 = stackSize;
        if (stackSize > this.mainInventory[n].getMaxStackSize() - this.mainInventory[n].stackSize) {
            n2 = this.mainInventory[n].getMaxStackSize() - this.mainInventory[n].stackSize;
        }

        if (n2 > this.getInventoryStackLimit() - this.mainInventory[n].stackSize) {
            n2 = this.getInventoryStackLimit() - this.mainInventory[n].stackSize;
        }

        if (n2 == 0) {
            return stackSize;
        }
        stackSize -= n2;
        this.mainInventory[n].stackSize += n2;
        this.mainInventory[n].animationsToGo = 5;
        return stackSize;
    }

    public void decrementAnimations() {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null) {
                this.mainInventory[i].updateAnimation(this.player.worldObj, this.player, i, this.currentItem == i);
            }
        }
    }

    public boolean consumeInventoryItem(Item itemIn) {
        int inventorySlotContainItem = this.getInventorySlotContainItem(itemIn);
        if (inventorySlotContainItem < 0) {
            return false;
        }
        if (--this.mainInventory[inventorySlotContainItem].stackSize <= 0) {
            this.mainInventory[inventorySlotContainItem] = null;
        }

        return true;
    }

    public boolean hasItem(Item itemIn) {
        int inventorySlotContainItem = this.getInventorySlotContainItem(itemIn);
        return inventorySlotContainItem >= 0;
    }

    public boolean addItemStackToInventory(final ItemStack itemStackIn) {
        if (itemStackIn != null && itemStackIn.stackSize != 0 && itemStackIn.getItem() != null) {
            try {
                if (itemStackIn.isItemDamaged()) {
                    int firstEmptyStack = this.getFirstEmptyStack();
                    if (firstEmptyStack >= 0) {
                        this.mainInventory[firstEmptyStack] = ItemStack.copyItemStack(itemStackIn);
                        this.mainInventory[firstEmptyStack].animationsToGo = 5;
                        itemStackIn.stackSize = 0;
                        return true;
                    }
                    if (this.player.capabilities.isCreativeMode) {
                        itemStackIn.stackSize = 0;
                        return true;
                    }
                    return false;
                }
                int n;
                do {
                    n = itemStackIn.stackSize;
                    itemStackIn.stackSize = this.storePartialItemStack(itemStackIn);
                } while (itemStackIn.stackSize > 0 && itemStackIn.stackSize < n);

                if (itemStackIn.stackSize == n && this.player.capabilities.isCreativeMode) {
                    itemStackIn.stackSize = 0;
                    return true;
                }
                return itemStackIn.stackSize < n;
            } catch (Throwable e) {
                CrashReport crashReport = CrashReport.makeCrashReport(e, "Adding item to inventory");
                CrashReportCategory crashReportCategory = crashReport.makeCategory("Item being added");
                crashReportCategory.addCrashSection("Item ID", Item.getIdFromItem(itemStackIn.getItem()));
                crashReportCategory.addCrashSection("Item data", itemStackIn.getMetadata());
                crashReportCategory.addCrashSectionCallable("Item name", new Callable<String>() {
                    public String call() throws Exception {
                        return itemStackIn.getDisplayName();
                    }
                });
                throw new ReportedException(crashReport);
            }
        } else {
            return false;
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack[] mainInventory = this.mainInventory;
        if (index >= this.mainInventory.length) {
            mainInventory = this.armorInventory;
            index -= this.mainInventory.length;
        }

        if (mainInventory[index] != null) {
            if (mainInventory[index].stackSize <= count) {
                ItemStack stack = mainInventory[index];
                mainInventory[index] = null;
                return stack;
            }
            ItemStack stack2 = mainInventory[index].splitStack(count);
            if (mainInventory[index].stackSize == 0) {
                mainInventory[index] = null;
            }

            return stack2;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        ItemStack[] mainInventory = this.mainInventory;
        if (index >= this.mainInventory.length) {
            mainInventory = this.armorInventory;
            index -= this.mainInventory.length;
        }

        if (mainInventory[index] != null) {
            ItemStack stack = mainInventory[index];
            mainInventory[index] = null;
            return stack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack[] mainInventory = this.mainInventory;
        if (index >= mainInventory.length) {
            index -= mainInventory.length;
            mainInventory = this.armorInventory;
        }

        mainInventory[index] = stack;
    }

    public float getStrVsBlock(Block blockIn) {
        float f = 1.0F;
        if (this.mainInventory[this.currentItem] != null) {
            f *= this.mainInventory[this.currentItem].getStrVsBlock(blockIn);
        }

        return f;
    }

    public NBTTagList writeToNBT(NBTTagList p_70442_1_) {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setByte("Slot", (byte)i);
                this.mainInventory[i].writeToNBT(nbt);
                p_70442_1_.appendTag(nbt);
            }
        }

        for (int j = 0; j < this.armorInventory.length; j++) {
            if (this.armorInventory[j] != null) {
                NBTTagCompound nbt2 = new NBTTagCompound();
                nbt2.setByte("Slot", (byte)(j + 100));
                this.armorInventory[j].writeToNBT(nbt2);
                p_70442_1_.appendTag(nbt2);
            }
        }

        return p_70442_1_;
    }

    public void readFromNBT(NBTTagList p_70443_1_) {
        this.mainInventory = new ItemStack[36];
        this.armorInventory = new ItemStack[4];

        for (int i = 0; i < p_70443_1_.tagCount(); i++) {
            NBTTagCompound compoundTagAt = p_70443_1_.getCompoundTagAt(i);
            int n = compoundTagAt.getByte("Slot") & 255;
            ItemStack stack = ItemStack.loadItemStackFromNBT(compoundTagAt);
            if (stack != null) {
                if (n >= 0 && n < this.mainInventory.length) {
                    this.mainInventory[n] = stack;
                }

                if (n >= 100 && n < this.armorInventory.length + 100) {
                    this.armorInventory[n - 100] = stack;
                }
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return this.mainInventory.length + 4;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        ItemStack[] mainInventory = this.mainInventory;
        if (index >= mainInventory.length) {
            index -= mainInventory.length;
            mainInventory = this.armorInventory;
        }

        return mainInventory[index];
    }

    @Override
    public String getCommandSenderName() {
        return "container.inventory";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        return (IChatComponent)(this.hasCustomName()
            ? new ChatComponentText(this.getCommandSenderName())
            : new ChatComponentTranslation(this.getCommandSenderName()));
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean canHeldItemHarvest(Block blockIn) {
        if (blockIn.getMaterial().isToolNotRequired()) {
            return true;
        }
        ItemStack stack = this.getStackInSlot(this.currentItem);
        return stack != null ? stack.canHarvestBlock(blockIn) : false;
    }

    public ItemStack armorItemInSlot(int p_70440_1_) {
        return this.armorInventory[p_70440_1_];
    }

    public int getTotalArmorValue() {
        int n = 0;

        for (int i = 0; i < this.armorInventory.length; i++) {
            if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor) {
                int damageReduceAmount = ((ItemArmor)this.armorInventory[i].getItem()).damageReduceAmount;
                n += damageReduceAmount;
            }
        }

        return n;
    }

    public void damageArmor(float damage) {
        float f = damage / 4.0F;
        if (f < 1.0F) {
            f = 1.0F;
        }

        for (int i = 0; i < this.armorInventory.length; i++) {
            if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor) {
                this.armorInventory[i].damageItem((int)f, this.player);
                if (this.armorInventory[i].stackSize == 0) {
                    this.armorInventory[i] = null;
                }
            }
        }
    }

    public void dropAllItems() {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null) {
                this.player.dropItem(this.mainInventory[i], true, false);
                this.mainInventory[i] = null;
            }
        }

        for (int j = 0; j < this.armorInventory.length; j++) {
            if (this.armorInventory[j] != null) {
                this.player.dropItem(this.armorInventory[j], true, false);
                this.armorInventory[j] = null;
            }
        }
    }

    @Override
    public void markDirty() {
        this.inventoryChanged = true;
    }

    public void setItemStack(ItemStack itemStackIn) {
        this.itemStack = itemStackIn;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.player.isDead ? false : player.getDistanceSqToEntity(this.player) <= 64.0;
    }

    public boolean hasItemStack(ItemStack itemStackIn) {
        for (int i = 0; i < this.armorInventory.length; i++) {
            if (this.armorInventory[i] != null && this.armorInventory[i].isItemEqual(itemStackIn)) {
                return true;
            }
        }

        for (int j = 0; j < this.mainInventory.length; j++) {
            if (this.mainInventory[j] != null && this.mainInventory[j].isItemEqual(itemStackIn)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    public void copyInventory(InventoryPlayer playerInventory) {
        for (int i = 0; i < this.mainInventory.length; i++) {
            this.mainInventory[i] = ItemStack.copyItemStack(playerInventory.mainInventory[i]);
        }

        for (int j = 0; j < this.armorInventory.length; j++) {
            this.armorInventory[j] = ItemStack.copyItemStack(playerInventory.armorInventory[j]);
        }

        this.currentItem = playerInventory.currentItem;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.mainInventory.length; i++) {
            this.mainInventory[i] = null;
        }

        for (int j = 0; j < this.armorInventory.length; j++) {
            this.armorInventory[j] = null;
        }
    }
}
