package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.brutality.core.itemstats.GetOrCreateHandler;
import org.brutality.core.itemstats.SerializeHandler;

public final class ItemStack {
    public static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.###");
    public int stackSize;
    public int animationsToGo;
    private Item item;
    private NBTTagCompound stackTagCompound;
    private int itemDamage;
    private EntityItemFrame itemFrame;
    private Block canDestroyCacheBlock = null;
    private boolean canDestroyCacheResult = false;
    private Block canPlaceOnCacheBlock = null;
    private boolean canPlaceOnCacheResult = false;

    public ItemStack(Block blockIn) {
        this(blockIn, 1);
    }

    public ItemStack(Block blockIn, int amount) {
        this(blockIn, amount, 0);
    }

    public ItemStack(Block blockIn, int amount, int meta) {
        this(Item.getItemFromBlock(blockIn), amount, meta);
    }

    public ItemStack(Item itemIn) {
        this(itemIn, 1);
    }

    public ItemStack(Item itemIn, int amount) {
        this(itemIn, amount, 0);
    }

    public ItemStack(Item itemIn, int amount, int meta) {
        this.item = itemIn;
        this.stackSize = amount;
        this.itemDamage = meta;
        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }
    }

    public static ItemStack loadItemStackFromNBT(NBTTagCompound nbt) {
        ItemStack stack = new ItemStack();
        stack.readFromNBT(nbt);
        return stack.getItem() != null ? stack : null;
    }

    private ItemStack() {
    }

    public ItemStack splitStack(int amount) {
        ItemStack stack = new ItemStack(this.item, amount, this.itemDamage);
        if (this.stackTagCompound != null) {
            stack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }

        this.stackSize -= amount;
        return stack;
    }

    public Item getItem() {
        return this.item;
    }

    public boolean onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        boolean flag = this.getItem().onItemUse(this, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
        if (flag) {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }

        return flag;
    }

    public float getStrVsBlock(Block blockIn) {
        return this.getItem().getStrVsBlock(this, blockIn);
    }

    public ItemStack useItemRightClick(World worldIn, EntityPlayer playerIn) {
        return this.getItem().onItemRightClick(this, worldIn, playerIn);
    }

    public ItemStack onItemUseFinish(World worldIn, EntityPlayer playerIn) {
        return this.getItem().onItemUseFinish(this, worldIn, playerIn);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        ResourceLocation nameForObject = Item.itemRegistry.getNameForObject(this.item);
        nbt.setString("id", nameForObject == null ? "minecraft:air" : nameForObject.toString());
        nbt.setByte("Count", (byte)this.stackSize);
        nbt.setShort("Damage", (short)this.itemDamage);
        if (this.stackTagCompound != null) {
            nbt.setTag("tag", this.stackTagCompound);
        }

        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("id", 8)) {
            this.item = Item.getByNameOrId(nbt.getString("id"));
        } else {
            this.item = Item.getItemById(nbt.getShort("id"));
        }

        this.stackSize = nbt.getByte("Count");
        this.itemDamage = nbt.getShort("Damage");
        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }

        if (nbt.hasKey("tag", 10)) {
            this.stackTagCompound = nbt.getCompoundTag("tag");
            if (this.item != null) {
                this.item.updateItemStackNBT(this.stackTagCompound);
            }
        }
    }

    public int getMaxStackSize() {
        return this.getItem().getItemStackLimit();
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
    }

    public boolean isItemStackDamageable() {
        return this.item == null ? false : (this.item.getMaxDamage() <= 0 ? false : !this.hasTagCompound() || !this.getTagCompound().getBoolean("Unbreakable"));
    }

    public boolean getHasSubtypes() {
        return this.item.getHasSubtypes();
    }

    public boolean isItemDamaged() {
        return this.isItemStackDamageable() && this.itemDamage > 0;
    }

    public int getItemDamage() {
        return this.itemDamage;
    }

    public int getMetadata() {
        return this.itemDamage;
    }

    public void setItemDamage(int meta) {
        this.itemDamage = meta;
        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }
    }

    public int getMaxDamage() {
        return this.item.getMaxDamage();
    }

    public boolean attemptDamageItem(int amount, Random rand) {
        if (!this.isItemStackDamageable()) {
            return false;
        }
        if (amount > 0) {
            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, this);
            int n = 0;

            for (int i = 0; enchantmentLevel > 0 && i < amount; i++) {
                if (EnchantmentDurability.negateDamage(this, enchantmentLevel, rand)) {
                    n++;
                }
            }

            amount -= n;
            if (amount <= 0) {
                return false;
            }
        }

        this.itemDamage += amount;
        return this.itemDamage > this.getMaxDamage();
    }

    public void damageItem(int amount, EntityLivingBase entityIn) {
        if ((!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).capabilities.isCreativeMode)
            && this.isItemStackDamageable()
            && this.attemptDamageItem(amount, entityIn.getRNG())) {
            entityIn.renderBrokenItemStack(this);
            this.stackSize--;
            if (entityIn instanceof EntityPlayer player) {
                player.triggerAchievement(StatList.objectBreakStats[Item.getIdFromItem(this.item)]);
                if (this.stackSize == 0 && this.getItem() instanceof ItemBow) {
                    player.destroyCurrentEquippedItem();
                }
            }

            if (this.stackSize < 0) {
                this.stackSize = 0;
            }

            this.itemDamage = 0;
        }
    }

    public void hitEntity(EntityLivingBase entityIn, EntityPlayer playerIn) {
        boolean flag = this.item.hitEntity(this, entityIn, playerIn);
        if (flag) {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }
    }

    public void onBlockDestroyed(World worldIn, Block blockIn, BlockPos pos, EntityPlayer playerIn) {
        boolean flag = this.item.onBlockDestroyed(this, worldIn, blockIn, pos, playerIn);
        if (flag) {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }
    }

    public boolean canHarvestBlock(Block blockIn) {
        return this.item.canHarvestBlock(blockIn);
    }

    public boolean interactWithEntity(EntityPlayer playerIn, EntityLivingBase entityIn) {
        return this.item.itemInteractionForEntity(this, playerIn, entityIn);
    }

    public ItemStack copy() {
        ItemStack stack = new ItemStack(this.item, this.stackSize, this.itemDamage);
        if (this.stackTagCompound != null) {
            stack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }

        return stack;
    }

    public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
        return stackA == null && stackB == null
            ? true
            : (
                stackA == null || stackB == null
                    ? false
                    : (
                        stackA.stackTagCompound == null && stackB.stackTagCompound != null
                            ? false
                            : stackA.stackTagCompound == null || stackA.stackTagCompound.equals(stackB.stackTagCompound)
                    )
            );
    }

    public static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB) {
        return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemStackEqual(stackB) : false);
    }

    private boolean isItemStackEqual(ItemStack other) {
        return this.stackSize != other.stackSize
            ? false
            : (
                this.item != other.item
                    ? false
                    : (
                        this.itemDamage != other.itemDamage
                            ? false
                            : (
                                this.stackTagCompound == null && other.stackTagCompound != null
                                    ? false
                                    : this.stackTagCompound == null || this.stackTagCompound.equals(other.stackTagCompound)
                            )
                    )
            );
    }

    public static boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
        return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemEqual(stackB) : false);
    }

    public boolean isItemEqual(ItemStack other) {
        return other != null && this.item == other.item && this.itemDamage == other.itemDamage;
    }

    public String getUnlocalizedName() {
        return this.item.getUnlocalizedName(this);
    }

    public static ItemStack copyItemStack(ItemStack stack) {
        return stack == null ? null : stack.copy();
    }

    @Override
    public String toString() {
        return this.stackSize + "x" + this.item.getUnlocalizedName() + "@" + this.itemDamage;
    }

    public void updateAnimation(World worldIn, Entity entityIn, int inventorySlot, boolean isCurrentItem) {
        if (this.animationsToGo > 0) {
            this.animationsToGo--;
        }

        this.item.onUpdate(this, worldIn, entityIn, inventorySlot, isCurrentItem);
    }

    public void onCrafting(World worldIn, EntityPlayer playerIn, int amount) {
        playerIn.addStat(StatList.objectCraftStats[Item.getIdFromItem(this.item)], amount);
        this.item.onCreated(this, worldIn, playerIn);
    }

    public boolean getIsItemStackEqual(ItemStack p_179549_1_) {
        return this.isItemStackEqual(p_179549_1_);
    }

    public int getMaxItemUseDuration() {
        return this.getItem().getMaxItemUseDuration(this);
    }

    public EnumAction getItemUseAction() {
        return this.getItem().getItemUseAction(this);
    }

    public void onPlayerStoppedUsing(World worldIn, EntityPlayer playerIn, int timeLeft) {
        this.getItem().onPlayerStoppedUsing(this, worldIn, playerIn, timeLeft);
    }

    public boolean hasTagCompound() {
        return this.stackTagCompound != null;
    }

    public NBTTagCompound getTagCompound() {
        return this.stackTagCompound;
    }

    public NBTTagCompound getSubCompound(String key, boolean create) {
        if (this.stackTagCompound != null && this.stackTagCompound.hasKey(key, 10)) {
            return this.stackTagCompound.getCompoundTag(key);
        }
        if (create) {
            NBTTagCompound nbt = new NBTTagCompound();
            this.setTagInfo(key, nbt);
            return nbt;
        }
        return null;
    }

    public NBTTagList getEnchantmentTagList() {
        return this.stackTagCompound == null ? null : this.stackTagCompound.getTagList("ench", 10);
    }

    public void setTagCompound(NBTTagCompound nbt) {
        this.stackTagCompound = nbt;
    }

    public String getDisplayName() {
        String itemStackDisplayName = this.getItem().getItemStackDisplayName(this);
        if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10)) {
            NBTTagCompound compoundTag = this.stackTagCompound.getCompoundTag("display");
            if (compoundTag.hasKey("Name", 8)) {
                itemStackDisplayName = compoundTag.getString("Name");
            }
        }

        return itemStackDisplayName;
    }

    public ItemStack setStackDisplayName(String displayName) {
        if (this.stackTagCompound == null) {
            this.stackTagCompound = new NBTTagCompound();
        }

        if (!this.stackTagCompound.hasKey("display", 10)) {
            this.stackTagCompound.setTag("display", new NBTTagCompound());
        }

        this.stackTagCompound.getCompoundTag("display").setString("Name", displayName);
        return this;
    }

    public void clearCustomName() {
        if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10)) {
            NBTTagCompound compoundTag = this.stackTagCompound.getCompoundTag("display");
            compoundTag.removeTag("Name");
            if (compoundTag.hasNoTags()) {
                this.stackTagCompound.removeTag("display");
                if (this.stackTagCompound.hasNoTags()) {
                    this.setTagCompound((NBTTagCompound)null);
                }
            }
        }
    }

    public boolean hasDisplayName() {
        return this.stackTagCompound == null
            ? false
            : (!this.stackTagCompound.hasKey("display", 10) ? false : this.stackTagCompound.getCompoundTag("display").hasKey("Name", 8));
    }

    public List<String> getTooltip(EntityPlayer playerIn, boolean advanced) {
        List list = Lists.newArrayList();
        var displayName = this.getDisplayName();
        if (this.hasDisplayName()) {
            displayName = EnumChatFormatting.ITALIC + displayName;
        }

        String str = displayName + EnumChatFormatting.RESET;
        if (advanced) {
            String str2 = "";
            if (str.length() > 0) {
                str = str + " (";
                str2 = ")";
            }

            int idFromItem = Item.getIdFromItem(this.item);
            if (this.getHasSubtypes()) {
                str = str + String.format("#%04d/%d%s", idFromItem, this.itemDamage, str2);
            } else {
                str = str + String.format("#%04d%s", idFromItem, str2);
            }
        } else if (!this.hasDisplayName() && this.item == Items.filled_map) {
            str = str + " #" + this.itemDamage;
        }

        list.add(str);
        int n = 0;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("HideFlags", 99)) {
            n = this.stackTagCompound.getInteger("HideFlags");
        }

        if ((n & 32) == 0) {
            this.item.addInformation(this, playerIn, list, advanced);
        }

        if (this.hasTagCompound()) {
            if ((n & 1) == 0) {
                NBTTagList enchantmentTagList = this.getEnchantmentTagList();
                if (enchantmentTagList != null) {
                    for (int i = 0; i < enchantmentTagList.tagCount(); i++) {
                        short s = enchantmentTagList.getCompoundTagAt(i).getShort("id");
                        short s2 = enchantmentTagList.getCompoundTagAt(i).getShort("lvl");
                        if (Enchantment.getEnchantmentById(s) != null) {
                            list.add(Enchantment.getEnchantmentById(s).getTranslatedName(s2));
                        }
                    }
                }
            }

            if (this.stackTagCompound.hasKey("display", 10)) {
                NBTTagCompound compoundTag = this.stackTagCompound.getCompoundTag("display");
                if (compoundTag.hasKey("color", 3)) {
                    if (advanced) {
                        list.add("Color: #" + Integer.toHexString(compoundTag.getInteger("color")).toUpperCase());
                    } else {
                        list.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("item.dyed"));
                    }
                }

                if (compoundTag.getTagId("Lore") == 9) {
                    NBTTagList tagList = compoundTag.getTagList("Lore", 8);
                    if (tagList.tagCount() > 0) {
                        for (int j = 0; j < tagList.tagCount(); j++) {
                            list.add("" + EnumChatFormatting.DARK_PURPLE + EnumChatFormatting.ITALIC + tagList.getStringTagAt(j));
                        }
                    }
                }
            }
        }

        String displayName2 = this.getDisplayName();
        if (displayName2 != null && (displayName2.startsWith("Mystic Sword") || displayName2.startsWith("Mystic Bow") || displayName2.startsWith("Fresh"))) {
            int itemDamage = this.getItemDamage();
            String colorCodeForId = this.getColorCodeForId(itemDamage);
            String str3 = colorCodeForId + "Requires " + colorCodeForId + this.getColorName(itemDamage) + "§r " + EnumChatFormatting.YELLOW + "To §r§e§lT3";
            list.add("");
            list.add(str3);
        }

        var attributeModifiers = this.getAttributeModifiers();
        if (!attributeModifiers.isEmpty() && (n & 2) == 0) {
            list.add("");

            for (Entry entry : attributeModifiers.entries()) {
                AttributeModifier val = (AttributeModifier)entry.getValue();
                double amount = val.getAmount();
                if (val.getID() == Item.itemModifierUUID) {
                    amount += EnchantmentHelper.func_152377_a(this, EnumCreatureAttribute.UNDEFINED);
                }

                double d;
                if (val.getOperation() != 1 && val.getOperation() != 2) {
                    d = amount;
                } else {
                    d = amount * 100.0;
                }

                if (amount > 0.0) {
                    list.add(
                        EnumChatFormatting.BLUE
                            + StatCollector.translateToLocalFormatted(
                                "attribute.modifier.plus." + val.getOperation(),
                                DECIMALFORMAT.format(d),
                                StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())
                            )
                    );
                } else if (amount < 0.0) {
                    d *= -1.0;
                    list.add(
                        EnumChatFormatting.RED
                            + StatCollector.translateToLocalFormatted(
                                "attribute.modifier.take." + val.getOperation(),
                                DECIMALFORMAT.format(d),
                                StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())
                            )
                    );
                }
            }
        }

        if (this.hasTagCompound() && this.getTagCompound().getBoolean("Unbreakable") && (n & 4) == 0) {
            list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("item.unbreakable"));
        }

        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9) && (n & 8) == 0) {
            NBTTagList tagList2 = this.stackTagCompound.getTagList("CanDestroy", 8);
            if (tagList2.tagCount() > 0) {
                list.add("");
                list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.canBreak"));

                for (int k = 0; k < tagList2.tagCount(); k++) {
                    Block blockFromName = Block.getBlockFromName(tagList2.getStringTagAt(k));
                    if (blockFromName != null) {
                        list.add(EnumChatFormatting.DARK_GRAY + blockFromName.getLocalizedName());
                    } else {
                        list.add(EnumChatFormatting.DARK_GRAY + "missingno");
                    }
                }
            }
        }

        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9) && (n & 16) == 0) {
            NBTTagList tagList3 = this.stackTagCompound.getTagList("CanPlaceOn", 8);
            if (tagList3.tagCount() > 0) {
                list.add("");
                list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.canPlace"));

                for (int idx = 0; idx < tagList3.tagCount(); idx++) {
                    Block blockFromName2 = Block.getBlockFromName(tagList3.getStringTagAt(idx));
                    if (blockFromName2 != null) {
                        list.add(EnumChatFormatting.DARK_GRAY + blockFromName2.getLocalizedName());
                    } else {
                        list.add(EnumChatFormatting.DARK_GRAY + "missingno");
                    }
                }
            }
        }

        if (advanced) {
            if (this.isItemDamaged()) {
                list.add("Durability: " + (this.getMaxDamage() - this.getItemDamage()) + " / " + this.getMaxDamage());
            }

            list.add(EnumChatFormatting.DARK_GRAY + Item.itemRegistry.getNameForObject(this.item).toString());
            if (this.hasTagCompound()) {
                list.add(EnumChatFormatting.DARK_GRAY + "NBT: " + this.getTagCompound().getKeySet().size() + " tag(s)");
            }
        }

        SerializeHandler serializeHandler = GetOrCreateHandler.computeSerializeHandler(this);
        if (serializeHandler != null && !serializeHandler.isEmpty()) {
            list.add("");
            list.add(EnumChatFormatting.YELLOW + "[StatTrack]");
            Collections.addAll(list, serializeHandler.computeString().split("\\n"));
        }

        return list;
    }

    private String getColorCodeForId(int colorId) {
        switch (colorId) {
            case 0:
                return "§c";
            case 1:
                return "§e";
            case 2:
                return "§9";
            case 3:
                return "§6";
            case 4:
                return "§a";
            default:
                return "§7";
        }
    }

    private String getColorName(int colorId) {
        switch (colorId) {
            case 0:
                return "Red";
            case 1:
                return "Yellow";
            case 2:
                return "Blue";
            case 3:
                return "Orange";
            case 4:
                return "Green";
            default:
                return "Unknown";
        }
    }

    public boolean hasEffect() {
        return this.getItem().hasEffect(this);
    }

    public EnumRarity getRarity() {
        return this.getItem().getRarity(this);
    }

    public boolean isItemEnchantable() {
        return !this.getItem().isItemTool(this) ? false : !this.isItemEnchanted();
    }

    public void addEnchantment(Enchantment ench, int level) {
        if (this.stackTagCompound == null) {
            this.setTagCompound(new NBTTagCompound());
        }

        if (!this.stackTagCompound.hasKey("ench", 9)) {
            this.stackTagCompound.setTag("ench", new NBTTagList());
        }

        NBTTagList tagList = this.stackTagCompound.getTagList("ench", 10);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setShort("id", (short)ench.effectId);
        nbt.setShort("lvl", (byte)level);
        tagList.appendTag(nbt);
    }

    public boolean isItemEnchanted() {
        return this.stackTagCompound != null && this.stackTagCompound.hasKey("ench", 9);
    }

    public void setTagInfo(String key, NBTBase value) {
        if (this.stackTagCompound == null) {
            this.setTagCompound(new NBTTagCompound());
        }

        this.stackTagCompound.setTag(key, value);
    }

    public boolean canEditBlocks() {
        return this.getItem().canItemEditBlocks();
    }

    public boolean isOnItemFrame() {
        return this.itemFrame != null;
    }

    public void setItemFrame(EntityItemFrame frame) {
        this.itemFrame = frame;
    }

    public EntityItemFrame getItemFrame() {
        return this.itemFrame;
    }

    public int getRepairCost() {
        return this.hasTagCompound() && this.stackTagCompound.hasKey("RepairCost", 3) ? this.stackTagCompound.getInteger("RepairCost") : 0;
    }

    public void setRepairCost(int cost) {
        if (!this.hasTagCompound()) {
            this.stackTagCompound = new NBTTagCompound();
        }

        this.stackTagCompound.setInteger("RepairCost", cost);
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers() {
        Multimap<String, AttributeModifier> multimap;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("AttributeModifiers", 9)) {
            multimap = HashMultimap.create();
            NBTTagList tagList = this.stackTagCompound.getTagList("AttributeModifiers", 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                AttributeModifier attributeModifier = SharedMonsterAttributes.readAttributeModifierFromNBT(compoundTagAt);
                if (attributeModifier != null && attributeModifier.getID().getLeastSignificantBits() != 0L && attributeModifier.getID().getMostSignificantBits() != 0L) {
                    multimap.put(compoundTagAt.getString("AttributeName"), attributeModifier);
                }
            }
        } else {
            multimap = this.getItem().getItemAttributeModifiers();
        }

        return (Multimap<String, AttributeModifier>)multimap;
    }

    public void setItem(Item newItem) {
        this.item = newItem;
    }

    public IChatComponent getChatComponent() {
        ChatComponentText chatComponentText = new ChatComponentText(this.getDisplayName());
        if (this.hasDisplayName()) {
            chatComponentText.getChatStyle().setItalic(true);
        }

        IChatComponent component = new ChatComponentText("[").appendSibling(chatComponentText).appendText("]");
        if (this.item != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            this.writeToNBT(nbt);
            component.getChatStyle().setChatHoverEvent(new HoverEvent(Action.SHOW_ITEM, new ChatComponentText(nbt.toString())));
            component.getChatStyle().setColor(this.getRarity().rarityColor);
        }

        return component;
    }

    public boolean canDestroy(Block blockIn) {
        if (blockIn == this.canDestroyCacheBlock) {
            return this.canDestroyCacheResult;
        }
        this.canDestroyCacheBlock = blockIn;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9)) {
            NBTTagList tagList = this.stackTagCompound.getTagList("CanDestroy", 8);

            for (int i = 0; i < tagList.tagCount(); i++) {
                Block blockFromName = Block.getBlockFromName(tagList.getStringTagAt(i));
                if (blockFromName == blockIn) {
                    this.canDestroyCacheResult = true;
                    return true;
                }
            }
        }

        this.canDestroyCacheResult = false;
        return false;
    }

    public boolean canPlaceOn(Block blockIn) {
        if (blockIn == this.canPlaceOnCacheBlock) {
            return this.canPlaceOnCacheResult;
        }
        this.canPlaceOnCacheBlock = blockIn;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9)) {
            NBTTagList tagList = this.stackTagCompound.getTagList("CanPlaceOn", 8);

            for (int i = 0; i < tagList.tagCount(); i++) {
                Block blockFromName = Block.getBlockFromName(tagList.getStringTagAt(i));
                if (blockFromName == blockIn) {
                    this.canPlaceOnCacheResult = true;
                    return true;
                }
            }
        }

        this.canPlaceOnCacheResult = false;
        return false;
    }
}
