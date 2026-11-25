package org.brutality.automation.grinding.purchases;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.automation.mining.FenceHandler;
import org.brutality.core.ExtraAttributesHandler;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.GetNonBotPlayersHelper;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.inventory.HelmetHelper;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;

public final class HasItemHandler {
    private static final long timestamp = 60000L;
    private static final long timestamp2 = 10000L;
    private static final long timestamp3 = 20000L;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting booleanSetting;
    private final ModeSetting modeSetting;
    private final NumberSetting numberSetting;
    private final KeybindListSetting keybindListSetting;
    private final NumberSetting numberSetting2;
    private final NumberSetting numberSetting3;
    private final NumberSetting numberSetting4;
    private final org.brutality.automation.navigation.ClearPathHandler clearPathHandler = new org.brutality.automation.navigation.ClearPathHandler();
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private final StopwatchTimer utilHelper82 = new StopwatchTimer(0L);
    private final StopwatchTimer utilHelper83 = new StopwatchTimer();
    private final List<HasItemHandler.DiamondSwordMode> entries = new ArrayList<>();
    private boolean active2;
    private boolean flag = true;
    private int count;
    private boolean flag2;
    private String status = "Idle";

    private Slot computeSlot(HasItemHandler.DiamondSwordMode diamondSwordMode) {
        GuiContainer screen = (GuiContainer)this.mc.currentScreen;

        for (Object inventorySlot : screen.inventorySlots.inventorySlots) {
            Slot slot = (Slot)inventorySlot;
            if (slot != null && slot.getHasStack() && slot.inventory != this.mc.thePlayer.inventory) {
                ItemStack stack = slot.getStack();
                String str = EnumChatFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()).toLowerCase(Locale.ROOT);
                if (str.contains(diamondSwordMode.shopName.toLowerCase(Locale.ROOT)) && (diamondSwordMode.stackSize == 0 || stack.stackSize == diamondSwordMode.stackSize)) {
                    return slot;
                }
            }
        }

        return null;
    }

    private boolean hasItem(Item item) {
        for (ItemStack stack : this.mc.thePlayer.inventory.armorInventory) {
            if (stack != null && stack.getItem() == item) {
                return true;
            }
        }

        for (int i = 0; i < 36; i++) {
            ItemStack stack2 = this.mc.thePlayer.inventory.getStackInSlot(i);
            if (stack2 != null && stack2.getItem() == item) {
                return true;
            }
        }

        return false;
    }

    public boolean isActive() {
        return this.active2;
    }

    public void cancel() {
        this.active2 = false;
        this.count = 0;
        this.entries.clear();
        this.clearPathHandler.process2();
        this.status = "Idle";
    }

    public String getStatus() {
        return this.status;
    }

    public HasItemHandler(
        BooleanSetting booleanSetting, ModeSetting modeSetting, NumberSetting numberSetting, KeybindListSetting keybindListSetting, NumberSetting numberSetting2, NumberSetting numberSetting3, NumberSetting numberSetting4
    ) {
        this.booleanSetting = booleanSetting;
        this.modeSetting = modeSetting;
        this.numberSetting = numberSetting;
        this.keybindListSetting = keybindListSetting;
        this.numberSetting2 = numberSetting2;
        this.numberSetting3 = numberSetting3;
        this.numberSetting4 = numberSetting4;
    }

    private void process() {
        KeybindStateUtil.process();
        if (this.utilHelper8.matchesLong(Math.max(500L, (long)this.numberSetting2.computeDouble()))) {
            while (this.count < this.entries.size() && this.matchesDiamondSwordMode(this.entries.get(this.count))) {
                this.count++;
            }

            if (this.count >= this.entries.size()) {
                this.finish();
            } else {
                Slot slot = this.computeSlot(this.entries.get(this.count));
                if (slot == null) {
                    this.count++;
                } else {
                    HelmetHelper.handleInt2(slot.slotNumber);
                    this.count++;
                }

                this.utilHelper8.reset();
            }
        }
    }

    private void finish() {
        if (this.mc.thePlayer != null && this.mc.currentScreen != null) {
            this.mc.thePlayer.closeScreen();
        }

        this.cancel();
        this.flag = true;
    }

    private BlockPos computeBlockPos(BlockPos pos) {
        BlockPos pos2 = pos;
        double MAX_VALUE = Double.MAX_VALUE;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockPos pos3 = pos.add(i, 0, j);
                if (FenceHandler.matchesBlockPos7(pos3)) {
                    double distanceSqToCenter = this.mc.thePlayer.getDistanceSqToCenter(pos3);
                    if (distanceSqToCenter < MAX_VALUE) {
                        pos2 = pos3;
                        MAX_VALUE = distanceSqToCenter;
                    }
                }
            }
        }

        return pos2;
    }

    public void onEventRender3D(EventRender3D event) {
        if (this.active2) {
            this.clearPathHandler.handleFlag2(true);
            this.clearPathHandler.onEventRender3D(event);
        }
    }

    public void process2() {
        this.cancel();
        this.flag = false;
    }

    public void tick() {
        if (this.active2 && this.mc.thePlayer != null && this.mc.theWorld != null) {
            if (!this.booleanSetting.isEnabled()) {
                this.cancel();
            } else if (!FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer)) {
                this.finish();
            } else if (this.utilHelper83.matchesLong(60000L)) {
                this.handleString("Shop purchases timed out; continuing.");
            } else if (this.mc.currentScreen instanceof GuiContainer && this.checkState()) {
                this.flag2 = true;
                this.status = "Buying items";
                this.process();
            } else {
                if (this.mc.currentScreen != null) {
                    this.mc.thePlayer.closeScreen();
                }

                BlockPos pos = GetNonBotPlayersHelper.computeBlockPos();
                EntityVillager entity = this.computeEntityVillager(pos);
                if (entity == null) {
                    this.status = "Finding shop";
                    KeybindStateUtil.process();
                    if (this.utilHelper83.matchesLong(10000L)) {
                        this.handleString("Could not find the items shop; skipping purchases.");
                    }
                } else if (!this.flag2 && this.utilHelper83.matchesLong(20000L)) {
                    this.handleString("Could not open the items shop; skipping purchases.");
                } else {
                    ExtraAttributesHandler.process2();
                    if (!(this.mc.thePlayer.getDistanceSqToEntity(entity) > 9.0) && this.mc.thePlayer.canEntityBeSeen(entity)) {
                        this.status = "Opening shop";
                        this.clearPathHandler.process2();
                        KeybindStateUtil.process();
                        BuildTargetComparatorHelper.handleDouble(
                            entity.posX,
                            entity.posY + entity.getEyeHeight(),
                            entity.posZ,
                            (int)this.numberSetting3.computeDouble(),
                            (int)this.numberSetting4.computeDouble()
                        );
                        if (this.utilHelper8.matchesLong(Math.max(500L, (long)this.numberSetting2.computeDouble()))) {
                            this.mc.playerController.interactWithEntitySendPacket(this.mc.thePlayer, entity);
                            this.utilHelper8.reset();
                        }
                    } else {
                        this.status = "Walking to shop";
                        BlockPos pos2 = this.computeBlockPos(entity.getPosition());
                        this.clearPathHandler.handleBlockPos(pos2, (int)this.numberSetting3.computeDouble(), (int)this.numberSetting4.computeDouble(), 0.3);
                    }
                }
            }
        }
    }

    private boolean matchesDiamondSwordMode(HasItemHandler.DiamondSwordMode diamondSwordMode) {
        if (diamondSwordMode.consumable) {
            return false;
        }
        return diamondSwordMode == HasItemHandler.DiamondSwordMode.IRON_PACK
            ? this.hasItem(Items.iron_chestplate) && this.hasItem(Items.iron_leggings) && this.hasItem(Items.iron_boots)
            : diamondSwordMode.item != null && this.hasItem(diamondSwordMode.item);
    }

    public boolean checkState2() {
        if (!this.booleanSetting.isEnabled()) {
            if (this.active2) {
                this.cancel();
            }

            return false;
        }
        if (this.active2) {
            return true;
        }
        if (!this.flag && this.mc.thePlayer != null && this.mc.theWorld != null && FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer)) {
            if (this.modeSetting.matchesString2("Every Mins") && !this.utilHelper82.matchesLong((long)this.numberSetting.computeDouble() * 60000L)) {
                return false;
            }
            this.entries.clear();

            for (Setting setting : this.keybindListSetting.getEntries()) {
                if (setting instanceof BooleanSetting && ((BooleanSetting)setting).isEnabled()) {
                    HasItemHandler.DiamondSwordMode diamondSwordMode = HasItemHandler.DiamondSwordMode.computeDiamondSwordMode(setting.getName());
                    if (diamondSwordMode != null && !this.matchesDiamondSwordMode(diamondSwordMode)) {
                        this.entries.add(diamondSwordMode);
                    }
                }
            }

            if (this.entries.isEmpty()) {
                this.flag = true;
                return false;
            }
            this.active2 = true;
            this.count = 0;
            this.utilHelper8.reset();
            this.utilHelper82.reset();
            this.utilHelper83.reset();
            this.flag2 = false;
            this.status = "Finding shop";
            return true;
        }
        return false;
    }

    private void handleString(String str) {
        MessageUtils.x("Auto Buy: " + str);
        this.finish();
    }

    private EntityVillager computeEntityVillager(BlockPos pos) {
        if (pos == null) {
            return null;
        }
        EntityVillager entity = null;
        double d = 9.0;

        for (Entity entity2 : this.mc.theWorld.loadedEntityList) {
            if (entity2 instanceof EntityVillager && !entity2.isDead) {
                double distanceSqToCenter = entity2.getDistanceSqToCenter(pos);
                if (distanceSqToCenter < d) {
                    entity = (EntityVillager)entity2;
                    d = distanceSqToCenter;
                }
            }
        }

        return entity;
    }

    private boolean checkState() {
        if (!(this.mc.thePlayer.openContainer instanceof ContainerChest containerChest)) {
            return false;
        }
        String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(containerChest.getLowerChestInventory().getDisplayName().getUnformattedText());
        if (textWithoutFormattingCodes == null) {
            return false;
        }
        textWithoutFormattingCodes = textWithoutFormattingCodes.toLowerCase(Locale.ROOT);
        return textWithoutFormattingCodes.contains("items") || textWithoutFormattingCodes.contains("shop");
    }

    public static enum DiamondSwordMode {
        DIAMOND_SWORD("Diamond Sword", "Diamond Sword", Items.diamond_sword, false, 0),
        DIAMOND_CHESTPLATE("Diamond Chestplate", "Diamond Chestplate", Items.diamond_chestplate, false, 0),
        DIAMOND_LEGGINGS("Diamond Leggings", "Diamond Leggings", Items.diamond_leggings, false, 0),
        DIAMOND_BOOTS("Diamond Boots", "Diamond Boots", Items.diamond_boots, false, 0),
        FIRST_AID_EGG("First-Aid Egg", "First-Aid Egg", Items.spawn_egg, false, 0),
        COMBAT_SPADE("Combat Spade", "Combat Spade", Items.diamond_shovel, false, 0),
        IRON_PACK("Iron Pack", "Iron Pack", null, false, 0),
        OBSIDIAN_8("Obsidian (8)", "Obsidian", null, true, 8),
        OBSIDIAN_64("Obsidian (64)", "Obsidian", null, true, 64);

        private final String settingName;
        public final String shopName;
        public final Item item;
        public final boolean consumable;
        public final int stackSize;

        public static HasItemHandler.DiamondSwordMode computeDiamondSwordMode(String str) {
            for (HasItemHandler.DiamondSwordMode diamondSwordMode : values()) {
                if (diamondSwordMode.settingName.equals(str)) {
                    return diamondSwordMode;
                }
            }

            return null;
        }

        private static HasItemHandler.DiamondSwordMode[] computeDiamondSwordModeArray() {
            return new HasItemHandler.DiamondSwordMode[]{
                DIAMOND_SWORD, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS, FIRST_AID_EGG, COMBAT_SPADE, IRON_PACK, OBSIDIAN_8, OBSIDIAN_64
            };
        }

        private DiamondSwordMode(String settingName, String shopName, Item item, boolean consumable, int stackSize) {
            this.settingName = settingName;
            this.shopName = shopName;
            this.item = item;
            this.consumable = consumable;
            this.stackSize = stackSize;
        }
    }
}
