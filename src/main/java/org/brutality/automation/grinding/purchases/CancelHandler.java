package org.brutality.automation.grinding.purchases;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.automation.mining.MiningHandler2;
import org.brutality.core.DisplayNameHandler2;
import org.brutality.core.ExtraAttributesHandler;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.util.GetNonBotPlayersHelper;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.inventory.GetHoveredSlotHelper;
import org.brutality.util.inventory.GoldenHeadsType;
import org.brutality.util.inventory.HelmetHelper;
import org.brutality.util.inventory.PerkSlot1Type;
import org.brutality.util.inventory.SecondGappleType;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.utils.MessageUtils;

public class CancelHandler {
    private static final double amount = 4.0;
    private static final double amount2 = 3.5;
    private static final long timestamp = 10000L;
    private static final long timestamp2 = 20000L;
    private static final long timestamp3 = 120000L;
    private final AutoGrinder autoGrinder;
    private static final Map<String, String> lookupMap = DisplayNameHandler2.lookupMap6;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final org.brutality.automation.navigation.ClearPathHandler clearPathHandler = new org.brutality.automation.navigation.ClearPathHandler();
    private final List<Integer> entries = new CancelHandler.Entry();
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private final StopwatchTimer utilHelper82 = new StopwatchTimer();
    private final StopwatchTimer utilHelper83 = new StopwatchTimer();
    private boolean flag = false;
    private boolean flag2 = false;
    private int count = 0;
    private int count2 = 0;
    private boolean flag3 = false;
    private int count3 = 0;
    private boolean flag4 = false;
    private GoldenHeadsType goldenHeadsType = null;
    private boolean flag5 = false;
    private boolean flag6 = false;
    private int count4 = 0;
    private boolean flag7 = false;
    private boolean flag8 = false;
    private final List<Integer> entries2 = new ArrayList<>();
    private boolean flag9 = false;
    private SecondGappleType secondGappleType = null;
    private boolean flag10 = false;
    private int count5 = -1;
    private long timestamp4;
    private static final long timestamp5 = -3308044542216765438L;

    private void process() {
        this.flag = false;
        this.utilHelper83.clear();
        this.clearPathHandler.clearPath();
        KeybindStateUtil.process();
        this.autoGrinder.process31();
    }

    private static boolean matchesItemStack(ItemStack stack, String str) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound compoundTag = stack.getTagCompound().getCompoundTag("display");
            if (compoundTag != null && compoundTag.hasKey("Lore", 9)) {
                NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                for (int i = 0; i < tagList.tagCount(); i++) {
                    if (tagList.getStringTagAt(i).toLowerCase().contains(str.toLowerCase())) {
                        return true;
                    }
                }

                return false;
            }
            return false;
        }
        return false;
    }

    public static boolean matchesInt(int n, int n2) {
        return n == 1 && n2 >= 0;
    }

    private int computeInt(String str, int n) {
        try {
            int n2 = Integer.parseInt(lookupMap.getOrDefault(str, String.valueOf(n)));
            if (n2 >= 1 && n2 <= 100) {
                return n2;
            }
        } catch (NumberFormatException ignored) {
        }

        return Math.max(1, Math.min(100, n));
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void tick() {
        if (mc.thePlayer != null) {
            if (mc.theWorld == null) {
                this.cancel();
                return;
            }

            if (!this.flag) {
                this.process2();
            }

            if (this.utilHelper83.matchesLong(120000L)) {
                this.handleString("Perk selection timed out; continuing AutoGrinder.");
                return;
            }

            BlockPos pos = GetNonBotPlayersHelper.computeBlockPos4();
            if (mc.currentScreen == null) {
                if (pos == null) {
                    this.clearPathHandler.clearPath();
                    KeybindStateUtil.process();
                    if (this.utilHelper83.matchesLong(10000L)) {
                        this.handleString("Could not find the perk villager; skipping this perk check.");
                    }

                    return;
                }

                if (this.utilHelper83.matchesLong(20000L)) {
                    this.handleString("Could not open the perk menu; skipping this perk check.");
                    return;
                }

                double distanceSq = mc.thePlayer.getDistanceSq(pos);
                boolean flag = distanceSq < 4.0;
                double d = this.computeDouble();
                int n = this.computeInt("a", (int)this.autoGrinder.yawSpeedSetting.computeDouble());
                int n2 = this.computeInt("b", (int)this.autoGrinder.pitchSpeedSetting.computeDouble());
                ExtraAttributesHandler.process2();
                if (distanceSq >= 4.0) {
                    this.clearPathHandler.handleBlockPos(pos, true, n, n2, 0.3F, 0.0);
                    boolean flag2 = false;
                    if (!this.clearPathHandler.isEntry2()
                        && !this.clearPathHandler.checkState2()
                        && MiningHandler2.matchesBlockPos(pos, true)
                        && BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, pos)) {
                        flag2 = true;
                        BuildTargetComparatorHelper.handleBlockPos(pos, n, n2);
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), distanceSq > 12.25);
                    }

                    if (!flag2 && this.clearPathHandler.checkState()) {
                        this.handleString("Could not path to the perk villager; skipping this perk check.");
                        return;
                    }

                    if (flag) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                    }
                } else {
                    KeybindStateUtil.process();
                    BuildTargetComparatorHelper.handleBlockPos(pos, n, n2);
                }

                if (flag && this.utilHelper8.matchesLong(200L)) {
                    this.utilHelper8.reset();
                    KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                }

                this.utilHelper82.reset();
            }

            if (mc.currentScreen == null) {
                return;
            }

            if (!this.utilHelper82.matchesLong(this.timestamp4 > 0L ? this.timestamp4 : (int)this.autoGrinder.windowClickDelaySetting.computeDouble())) {
                return;
            }

            this.utilHelper82.reset();
            if (!GetHoveredSlotHelper.matchesGuiScreen8(mc.currentScreen)
                && !GetHoveredSlotHelper.matchesGuiScreen6(mc.currentScreen)
                && !GetHoveredSlotHelper.matchesGuiScreen10(mc.currentScreen)
                && !GetHoveredSlotHelper.matchesGuiScreen(mc.currentScreen)
                && !GetHoveredSlotHelper.matchesGuiScreen4(mc.currentScreen)) {
                mc.thePlayer.closeScreen();
                return;
            }

            KeybindStateUtil.process();
            if (!this.flag3) {
                boolean flag3 = GetHoveredSlotHelper.matchesGuiScreen8(mc.currentScreen);
                boolean flag4 = GetHoveredSlotHelper.matchesGuiScreen10(mc.currentScreen);
                boolean flag5 = GetHoveredSlotHelper.matchesGuiScreen6(mc.currentScreen);
                if (flag3) {
                    this.flag5 = GetHoveredSlotHelper.computeInt(lookupMap.get("f")) != -1;
                }

                ArrayList list = new ArrayList();
                list.add(GoldenHeadsType.valueOf(this.autoGrinder.goldenheadsSetting.getText()));
                list.add(GoldenHeadsType.valueOf(this.autoGrinder.strengthchainingSetting2.getText()));
                list.add(GoldenHeadsType.valueOf(this.autoGrinder.safetyfirstSetting.getText()));
                list.add(GoldenHeadsType.valueOf(this.autoGrinder.gladiatorSetting.getText()));
                if (this.count3 >= (this.flag5 ? 4 : 3)) {
                    this.flag3 = true;
                    return;
                }

                if (flag3 && this.count5 >= 0 && this.count3 != this.count5) {
                    this.count3++;
                    return;
                }

                if (flag3) {
                    if (this.flag4) {
                        this.count3++;
                        this.flag4 = false;
                        MessageUtils.x(
                            String.valueOf(new StringBuilder("Selected perk: ").append(EnumChatFormatting.YELLOW).append(this.goldenHeadsType.getDisplayName()))
                        );
                        this.goldenHeadsType = null;
                        return;
                    }

                    int n3 = this.entries.get(this.count3);
                    Slot slot = GetHoveredSlotHelper.computeSlot(n3);
                    if (slot != null && slot.getStack() != null && slot.getStack().getItem() == Item.getItemFromBlock(Blocks.bedrock)) {
                        MessageUtils.x(String.valueOf(new StringBuilder("Skipping locked perk slot: ").append(this.count3 + 1)));
                        this.count3++;
                        return;
                    }

                    GoldenHeadsType goldenHeadsType = (GoldenHeadsType)list.get(this.count3);
                    if (slot != null && slot.getStack() != null && slot.getStack().getItem() == goldenHeadsType.getItem()) {
                        MessageUtils.x(
                            String.valueOf(new StringBuilder("Already have perk selected: ").append(EnumChatFormatting.YELLOW).append(goldenHeadsType.getDisplayName()))
                        );
                        this.count3++;
                        return;
                    }

                    HelmetHelper.handleInt2(n3);
                    return;
                }

                if (!flag4) {
                    if (flag5) {
                        this.flag4 = false;
                        this.goldenHeadsType = null;
                        HelmetHelper.handleInt2(GetHoveredSlotHelper.computeInt());
                        MessageUtils.x(
                            String.valueOf(
                                new StringBuilder("Purchased perk: ")
                                    .append(EnumChatFormatting.YELLOW)
                                    .append(((GoldenHeadsType)list.get(this.count3)).getDisplayName())
                            )
                        );
                        this.autoGrinder
                            .handleString(
                                String.valueOf(new StringBuilder("Purchased perk: ").append(((GoldenHeadsType)list.get(this.count3)).getDisplayName())),
                                Color.cyan
                            );
                        this.count3++;
                        return;
                    }

                    return;
                }

                GoldenHeadsType goldenHeadsType2 = (GoldenHeadsType)list.get(this.count3);
                Slot slot2 = GetHoveredSlotHelper.computeSlot(goldenHeadsType2.getItem());
                if (this.flag4) {
                    this.count3++;
                    this.flag4 = false;
                    this.goldenHeadsType = null;
                    Slot slot3 = GetHoveredSlotHelper.computeSlot(GoldenHeadsType.GO_BACK_SLOT.getItem());
                    if (slot3 != null) {
                        HelmetHelper.handleInt2(slot3.slotNumber);
                        return;
                    }

                    return;
                }

                if (slot2 != null) {
                    HelmetHelper.handleInt2(slot2.slotNumber);
                    this.flag4 = true;
                    this.goldenHeadsType = goldenHeadsType2;
                    return;
                }

                this.count3++;
                MessageUtils.x(
                    String.valueOf(new StringBuilder("Perk not unlocked, skipping: ").append(EnumChatFormatting.YELLOW).append(goldenHeadsType2.getDisplayName()))
                );
                Slot slot4 = GetHoveredSlotHelper.computeSlot(GoldenHeadsType.GO_BACK_SLOT.getItem());
                if (slot4 != null) {
                    HelmetHelper.handleInt2(slot4.slotNumber);
                    return;
                }

                return;
            }

            if (!this.flag2) {
                boolean flag6 = GetHoveredSlotHelper.matchesGuiScreen8(mc.currentScreen);
                boolean flag7 = GetHoveredSlotHelper.matchesGuiScreen6(mc.currentScreen);
                ArrayList list2 = new ArrayList();
                if (this.autoGrinder.buyElGatoSetting.isEnabled()) {
                    list2.add(PerkSlot1Type.EL_GATO);
                }

                if (this.autoGrinder.buyXPBoostSetting.isEnabled()) {
                    list2.add(PerkSlot1Type.XP_BOOST);
                }

                if (this.autoGrinder.buyGoldBoostSetting.isEnabled()) {
                    list2.add(PerkSlot1Type.GOLD_BOOST);
                }

                if (this.autoGrinder.buyDamageBoostSetting.isEnabled()) {
                    list2.add(PerkSlot1Type.MELEE_DAMAGE);
                }

                if (this.autoGrinder.buyDamageReductionSetting.isEnabled()) {
                    list2.add(PerkSlot1Type.DAMAGE_REDUCTION);
                }

                if (this.count >= list2.size()) {
                    this.flag2 = true;
                    return;
                }

                if (flag6) {
                    if (this.count2 >= 1) {
                        this.count++;
                        this.count2 = 0;
                        return;
                    }

                    HelmetHelper.handleInt2(((PerkSlot1Type)list2.get(this.count)).getMenuSlot());
                    this.count2++;
                    return;
                }

                if (flag7) {
                    int n4 = GetHoveredSlotHelper.computeInt();
                    if (!matchesInt(this.count2, n4)) {
                        return;
                    }

                    HelmetHelper.handleInt2(n4);
                    this.count2 = (int)timestamp5;
                    return;
                }

                return;
            }

            if (!this.flag6 && this.autoGrinder.buyKillstreaksSetting.isEnabled()) {
                boolean flag8 = GetHoveredSlotHelper.matchesGuiScreen8(mc.currentScreen);
                boolean flag9 = GetHoveredSlotHelper.matchesGuiScreen(mc.currentScreen);
                boolean flag10 = GetHoveredSlotHelper.matchesGuiScreen4(mc.currentScreen);
                boolean flag11 = GetHoveredSlotHelper.matchesGuiScreen6(mc.currentScreen);
                if (flag9 && !this.flag8) {
                    this.flag8 = true;
                    this.entries2.clear();
                    int n5 = 0;
                    ArrayList<Slot> slots = new ArrayList<>();

                    for (Object inventorySlot : ((GuiContainer)mc.currentScreen).inventorySlots.inventorySlots) {
                        Slot slot5 = (Slot)inventorySlot;
                        if (matchesSlot(slot5)) {
                            slots.add(slot5);
                            if (slot5.getStack().getItem() != Item.getItemFromBlock(Blocks.bedrock)) {
                                n5++;
                            }
                        }
                    }

                    Collections.sort(slots, new Comparator<Slot>() {
                        public int compare(Slot slot6, Slot slot7) {
                            return Integer.compare(slot6.slotNumber, slot7.slotNumber);
                        }
                    });

                    for (Slot slot8 : slots) {
                        this.entries2.add(slot8.slotNumber);
                    }

                    this.flag7 = n5 >= 3;
                }

                ArrayList list3 = new ArrayList();
                list3.add(SecondGappleType.valueOf(this.autoGrinder.feastSetting.getText()));
                list3.add(SecondGappleType.valueOf(this.autoGrinder.fightorflightSetting.getText()));
                list3.add(SecondGappleType.valueOf(this.autoGrinder.expliciousSetting.getText()));
                if (this.count4 >= (this.flag7 ? 3 : 2)) {
                    this.flag6 = true;
                    if (flag9) {
                        int n6 = GetHoveredSlotHelper.computeInt(SecondGappleType.GO_BACK_BUTTON.getDisplayName());
                        if (n6 != -1) {
                            HelmetHelper.handleInt2(n6);
                            return;
                        }
                    }

                    if (flag10) {
                        int n7 = GetHoveredSlotHelper.computeInt(SecondGappleType.GO_BACK_BUTTON.getDisplayName());
                        if (n7 != -1) {
                            HelmetHelper.handleInt2(n7);
                        }
                    }

                    mc.thePlayer.closeScreen();
                    return;
                }

                if (flag8) {
                    Slot slot9 = GetHoveredSlotHelper.computeSlot(Items.blaze_powder);
                    if (slot9 != null && slot9.getStack() != null) {
                        if (matchesItemStack(slot9.getStack(), "Can't access while on streak")) {
                            this.flag6 = true;
                            return;
                        }

                        HelmetHelper.handleInt2(slot9.slotNumber);
                        return;
                    }

                    this.flag6 = true;
                    return;
                }

                if (!flag9) {
                    if (!flag10) {
                        if (flag11) {
                            if (!this.flag9) {
                                return;
                            }

                            this.flag9 = false;
                            this.secondGappleType = null;
                            HelmetHelper.handleInt2(GetHoveredSlotHelper.computeInt());
                            MessageUtils.x(
                                String.valueOf(
                                    new StringBuilder("Purchased killstreak: ")
                                        .append(EnumChatFormatting.YELLOW)
                                        .append(((SecondGappleType)list3.get(this.count4)).name())
                                )
                            );
                            this.autoGrinder
                                .handleString(
                                    String.valueOf(new StringBuilder("Purchased killstreak: ").append(((SecondGappleType)list3.get(this.count4)).name())),
                                    Color.cyan
                                );
                            this.count4++;
                            this.flag10 = true;
                            return;
                        }

                        return;
                    }

                    SecondGappleType secondGappleType = (SecondGappleType)list3.get(this.count4);
                    Slot slot10 = GetHoveredSlotHelper.computeSlot(secondGappleType.getDisplayName());
                    if (this.flag10) {
                        this.flag10 = false;
                        int n8 = GetHoveredSlotHelper.computeInt(SecondGappleType.GO_BACK_BUTTON.getDisplayName());
                        if (n8 != -1) {
                            HelmetHelper.handleInt2(n8);
                            return;
                        }
                    }

                    if (this.flag9) {
                        this.count4++;
                        this.flag9 = false;
                        this.secondGappleType = null;
                        Slot slot11 = GetHoveredSlotHelper.computeSlot(SecondGappleType.GO_BACK_BUTTON.getDisplayName());
                        if (slot11 != null) {
                            HelmetHelper.handleInt2(slot11.slotNumber);
                            return;
                        }

                        return;
                    }

                    if (slot10 != null) {
                        HelmetHelper.handleInt2(slot10.slotNumber);
                        this.flag9 = true;
                        this.secondGappleType = secondGappleType;
                        return;
                    }

                    this.count4++;
                    MessageUtils.x(String.valueOf(new StringBuilder("Killstreak not unlocked, skipping: ").append(secondGappleType.name())));
                    int n9 = GetHoveredSlotHelper.computeInt(SecondGappleType.GO_BACK_BUTTON.getDisplayName());
                    if (n9 != -1) {
                        HelmetHelper.handleInt2(n9);
                        return;
                    }

                    return;
                }

                Slot slot12 = null;
                if (this.count4 < this.entries2.size()) {
                    slot12 = GetHoveredSlotHelper.computeSlot(this.entries2.get(this.count4));
                }

                if (this.flag9) {
                    this.count4++;
                    this.flag9 = false;
                    MessageUtils.x(
                        String.valueOf(
                            new StringBuilder("Selected killstreak: ").append(EnumChatFormatting.YELLOW).append(this.secondGappleType.getDisplayName())
                        )
                    );
                    this.secondGappleType = null;
                    return;
                }

                if (slot12 != null && (slot12.getStack() == null || slot12.getStack().getItem() != Item.getItemFromBlock(Blocks.bedrock))) {
                    SecondGappleType secondGappleType2 = (SecondGappleType)list3.get(this.count4);
                    boolean flag12 = slot12.getStack().getDisplayName().toLowerCase().contains("perk slot");
                    boolean flag13 = slot12.getStack().getItem() == secondGappleType2.getItem()
                        && slot12.getStack().getDisplayName().toLowerCase().contains(secondGappleType2.getDisplayName().toLowerCase());
                    if (!flag12 && slot12.getStack() != null && flag13) {
                        MessageUtils.x(
                            String.valueOf(
                                new StringBuilder("Already have killstreak selected: ").append(EnumChatFormatting.YELLOW).append(secondGappleType2.getDisplayName())
                            )
                        );
                        this.count4++;
                        this.flag9 = false;
                        this.secondGappleType = null;
                        return;
                    }

                    HelmetHelper.handleInt2(slot12.slotNumber);
                    return;
                }

                MessageUtils.x(String.valueOf(new StringBuilder("Skipping locked killstreak slot: ").append(this.count4 + 1)));
                this.count4++;
                return;
            }

            this.process();
        }
    }

    public void handleInt(int n, long l) {
        this.process2();
        this.count5 = n;
        this.timestamp4 = Math.max(100L, l);
        if (n >= 0) {
            this.flag2 = true;
            this.flag6 = true;
        }
    }

    private double computeDouble() {
        try {
            double d = Double.parseDouble(lookupMap.getOrDefault("h", String.valueOf(3.5)));
            if (!Double.isNaN(d) && !Double.isInfinite(d) && d >= 2.0 && d <= 3.75) {
                return d;
            }
        } catch (NumberFormatException ignored) {
        }

        return 3.5;
    }

    public void reset() {
        this.count5 = -1;
        this.timestamp4 = 0L;
        this.flag2 = false;
        this.count = 0;
        this.count2 = 0;
        this.flag3 = false;
        this.count3 = 0;
        this.flag4 = false;
        this.goldenHeadsType = null;
        this.flag5 = false;
        this.flag6 = false;
        this.count4 = 0;
        this.flag7 = false;
        this.flag8 = false;
        this.entries2.clear();
        this.flag9 = false;
        this.secondGappleType = null;
        this.flag10 = false;
        this.flag = false;
        this.utilHelper8.reset();
        this.utilHelper82.reset();
        this.utilHelper83.clear();
        this.clearPathHandler.clearPath();
        KeybindStateUtil.process();
    }

    public CancelHandler(AutoGrinder autoGrinder) {
        this.autoGrinder = autoGrinder;
    }

    private static boolean matchesSlot(Slot slot) {
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            if (stack.getItem() == Item.getItemFromBlock(Blocks.bedrock)) {
                return true;
            }
            String str = stack.getDisplayName().toLowerCase();
            if (str.contains("perk slot")) {
                return true;
            }
            for (SecondGappleType secondGappleType : SecondGappleType.values()) {
                if (secondGappleType != SecondGappleType.GO_BACK_BUTTON
                    && secondGappleType != SecondGappleType.NO_KILLSTREAK_BUTTON
                    && str.contains(secondGappleType.getDisplayName().toLowerCase())) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    public void process2() {
        this.reset();
        this.flag = true;
        this.utilHelper83.reset();
    }

    private static boolean matchesInt(int n) {
        Slot slot = GetHoveredSlotHelper.computeSlot(n);
        return slot != null && slot.getHasStack() ? slot.getStack().getItem() == Item.getItemFromBlock(Blocks.bedrock) : false;
    }

    private void handleString(String str) {
        this.autoGrinder.handleString5(str);
        this.process();
    }

    public void cancel() {
        this.flag = false;
        this.utilHelper83.clear();
        this.clearPathHandler.clearPath();
        KeybindStateUtil.process();
    }

    public class Entry extends ArrayList<Integer> {
        public Entry() {
            this.add(PerkSlot1Type.PERK_SLOT_1.getMenuSlot());
            this.add(PerkSlot1Type.PERK_SLOT_2.getMenuSlot());
            this.add(PerkSlot1Type.PERK_SLOT_3.getMenuSlot());
            this.add(PerkSlot1Type.PERK_SLOT_4.getMenuSlot());
        }
    }
}
