package org.brutality.module.impl.pit.hypit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S02PacketChat;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.timer.TimerUtil;

public class AutoEnchant extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ModeSetting darkSetting = new ModeSetting("Item Type", this, "Sword", Arrays.asList("Sword", "Green", "Yellow", "Red", "Blue", "Dark"));
    private final NumberSetting freshDelaySetting = new NumberSetting("Fresh Delay", this, 500.0, 500.0, 5000.0, 100.0);
    private final NumberSetting enchantDelaySetting = new NumberSetting("Enchant Delay", this, 500.0, 500.0, 5000.0, 100.0);
    private final NumberSetting scanDelaySetting = new NumberSetting("Scan Delay", this, 1000.0, 500.0, 5000.0, 100.0);
    private final NumberSetting trashGUIWaitSetting = new NumberSetting("Trash GUI Wait", this, 500.0, 500.0, 5000.0, 100.0);
    private final NumberSetting trashCloseDelaySetting = new NumberSetting("Trash Close Delay", this, 300.0, 500.0, 5000.0, 100.0);
    private AutoEnchant.IdleMode idleMode = AutoEnchant.IdleMode.IDLE;
    private Set<String> itemSet = new HashSet<>();
    private TimerUtil timerUtil = new TimerUtil();
    private TimerUtil timerUtil2 = new TimerUtil();
    private int count = 0;
    private boolean flag = false;
    private static List<String> entries = new ArrayList<>();
    private boolean flag2 = false;
    private boolean flag3 = false;

    private void reset() {
        this.idleMode = AutoEnchant.IdleMode.IDLE;
        this.itemSet.clear();
        this.itemSet.addAll(entries);
        this.count = 0;
        this.flag = false;
        this.flag2 = false;
        this.flag3 = false;
        this.timerUtil.reset();
        this.timerUtil2.reset();
    }

    private void sendMessage(String str) {
        try {
            ModuleManager.handleString(MessageUtils.computeString(str, MessageUtils.text8));
        } catch (Exception ignored) {
        }
    }

    private void process6() {
        if (!this.flag) {
            String str = this.computeString();
            this.mc.thePlayer.sendChatMessage(str);
            this.sendMessage("§a[AutoEnchant] §fExecuted: " + str);
            this.flag = true;
            this.timerUtil.reset();
            this.sendMessage("§7[AutoEnchant Debug] §fTimer reset, waiting 2s for item...");
        } else {
            if (this.timerUtil.matchesLong4((long)this.freshDelaySetting.computeDouble())) {
                this.idleMode = AutoEnchant.IdleMode.ENCHANTING;
                this.count = 0;
                this.timerUtil2.reset();
                this.sendMessage("§a[AutoEnchant] §fStarting enchanting phase...");
            }
        }
    }

    public AutoEnchant() {
        super("AutoEnchant", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.darkSetting,
                this.freshDelaySetting,
                this.enchantDelaySetting,
                this.scanDelaySetting,
                this.trashGUIWaitSetting,
                this.trashCloseDelaySetting
            }
        );
    }

    private int computeInt(String str) {
        if (str.endsWith(" X")) {
            return 10;
        }
        if (str.endsWith(" IX")) {
            return 9;
        }
        if (str.endsWith(" VIII")) {
            return 8;
        }
        if (str.endsWith(" VII")) {
            return 7;
        }
        if (str.endsWith(" VI")) {
            return 6;
        }
        if (str.endsWith(" V")) {
            return 5;
        }
        if (str.endsWith(" IV")) {
            return 4;
        }
        if (str.endsWith(" III")) {
            return 3;
        }
        if (str.endsWith(" II")) {
            return 2;
        }
        return str.endsWith(" I") ? 1 : 0;
    }

    private String computeString() {
        String str = this.darkSetting.getText().toLowerCase();
        return "/fresh " + str + " 100";
    }

    private void process3() {
        this.process4();
    }

    public static List<String> computeList() {
        return new ArrayList<>(entries);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.mc.thePlayer != null && this.mc.theWorld != null) {
                if (event instanceof GetPacketEvent) {
                    this.onGetPacketEvent((GetPacketEvent)event);
                } else if (event instanceof EventUpdate) {
                    this.process();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
        this.sendMessage("§c[AutoEnchant] §fDisabled");
    }

    private void process4() {
        if (!this.flag2) {
            this.mc.thePlayer.sendChatMessage("/trash");
            this.flag2 = true;
            this.flag3 = false;
            this.timerUtil.reset();
            this.sendMessage("§e[AutoEnchant] §fOpened trash GUI, scanning for chest...");
        } else if (this.timerUtil.matchesLong4((long)this.trashGUIWaitSetting.computeDouble())) {
            if (this.flag3) {
                if (this.flag3 && this.timerUtil.matchesLong4((long)this.trashCloseDelaySetting.computeDouble())) {
                    this.mc.thePlayer.closeScreen();
                    this.flag2 = false;
                    this.flag3 = false;
                    this.sendMessage("§a[AutoEnchant] §fInventory trashed! Restarting cycle...");
                    this.idleMode = AutoEnchant.IdleMode.FRESH;
                    this.flag = false;
                    this.count = 0;
                    this.timerUtil.reset();
                }
            } else {
                try {
                    if (this.mc.thePlayer.openContainer != null) {
                        boolean flag = false;
                        int n = -1;

                        for (int i = 0; i < this.mc.thePlayer.openContainer.inventorySlots.size(); i++) {
                            ItemStack stack = this.mc.thePlayer.openContainer.getSlot(i).getStack();
                            if (stack != null && stack.getItem() != null) {
                                int idFromItem = Item.getIdFromItem(stack.getItem());
                                if (idFromItem == 54) {
                                    n = i;
                                    flag = true;
                                    break;
                                }
                            }
                        }

                        if (flag && n != -1) {
                            this.mc.playerController.windowClick(this.mc.thePlayer.openContainer.windowId, n, 0, 0, this.mc.thePlayer);
                            this.sendMessage("§a[AutoEnchant] §fClicked chest at slot " + n + " - inventory cleared!");
                            this.flag3 = true;
                        } else {
                            this.sendMessage("§c[AutoEnchant] §fNo chest found in GUI, trying slot 13...");
                            this.mc.playerController.windowClick(this.mc.thePlayer.openContainer.windowId, 13, 0, 0, this.mc.thePlayer);
                            this.flag3 = true;
                        }

                        this.timerUtil.reset();
                    }
                } catch (Exception e) {
                    this.sendMessage("§c[AutoEnchant] §fError clicking trash: " + e.getMessage());
                    this.flag3 = true;
                }
            }
        }
    }

    private ItemStack getStackInSlot(int n) {
        try {
            if (this.mc.thePlayer.inventoryContainer != null && n >= 0 && n < this.mc.thePlayer.inventoryContainer.inventorySlots.size()) {
                return this.mc.thePlayer.inventoryContainer.getSlot(n).getStack();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private void handleInt(int n) {
        try {
            this.mc.playerController.windowClick(0, n, 1, 4, this.mc.thePlayer);
        } catch (Exception e) {
            this.sendMessage("§c[AutoEnchant] §fError dropping item: " + e.getMessage());
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (entries.isEmpty()) {
            this.sendMessage("§c[AutoEnchant] §fNo enchants configured! Use §e.autoenchant <enchant1,enchant2,...>");
            this.process28();
        } else {
            this.reset();
            this.sendMessage("§a[AutoEnchant] §fEnabled with enchants: §e" + String.join(", ", entries));
            this.sendMessage("§a[AutoEnchant] §fItem type: §e" + this.darkSetting.getText());
            this.idleMode = AutoEnchant.IdleMode.FRESH;
            this.timerUtil.reset();
            this.sendMessage("§e[AutoEnchant] §fPhase set to FRESH, waiting for first update...");
        }
    }

    private void onGetPacketEvent(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof S02PacketChat packet) {
            String text = packet.getChatComponent().getFormattedText();
            String str = text.replaceAll("§[0-9a-fk-or]", "");
        }
    }

    private void process() {
        if (this.idleMode != AutoEnchant.IdleMode.IDLE) {
            if (this.mc.thePlayer.ticksExisted % 100 == 0) {
                boolean flag = this.flag2;
                int count = this.count;
                boolean flag2 = this.flag;
                AutoEnchant.IdleMode idleMode = this.idleMode;
                this.sendMessage("§7[AutoEnchant Debug] §fPhase: " + idleMode + ", HasFresh: " + flag2 + ", EnchantCount: " + count + ", TrashGUI: " + flag);
            }

            switch (this.idleMode) {
                case FRESH:
                    this.process6();
                    break;
                case ENCHANTING:
                    this.mc();
                    break;
                case CHECKING:
                    this.process5();
                    break;
                case TRASHING:
                    this.process4();
                    break;
                case DROPPING:
                    this.process3();
            }
        }
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            String str = this.darkSetting.getText().toLowerCase();
            if (str.equals("sword")) {
                return stack.getItem() == Items.golden_sword;
            }
            if (stack.getItem() == Items.leather_leggings) {
                int n = tagCompound.hasKey("pit-mystic-tier") ? tagCompound.getInteger("pit-mystic-tier") : 0;
                int n2 = str.equals("dark") ? 2 : 3;
                if (n != n2) {
                    return false;
                }
                String str2 = tagCompound.hasKey("pit-pants-color") ? tagCompound.getString("pit-pants-color") : "";
                return str2.equalsIgnoreCase(str);
            }
            return false;
        }
        return false;
    }

    public static void handleString(String str) {
        entries.clear();
        String[] parts = str.split(",");

        for (String str2 : parts) {
            entries.add(str2.trim().toLowerCase());
        }
    }

    private void mc() {
        if (this.count >= 3) {
            this.idleMode = AutoEnchant.IdleMode.CHECKING;
            this.timerUtil.reset();
            this.sendMessage("§a[AutoEnchant] §fFinished enchanting, checking inventory...");
        } else if (this.timerUtil2.matchesLong4((long)this.enchantDelaySetting.computeDouble())) {
            this.mc.thePlayer.sendChatMessage("/enchant all");
            this.count++;
            this.timerUtil2.reset();
            this.sendMessage("§a[AutoEnchant] §fEnchant §e" + this.count + "/3");
        }
    }

    private void process5() {
        if (this.timerUtil.matchesLong4((long)this.scanDelaySetting.computeDouble())) {
            var map = this.computeMap();
            if (!map.isEmpty()) {
                this.sendMessage("§a[AutoEnchant] §fFound §e" + map.size() + " §fitems with target enchants!");

                for (Entry entry : map.entrySet()) {
                    var list = this.computeList((ItemStack)entry.getValue());
                    var key = entry.getKey();
                    String displayName = ((ItemStack)entry.getValue()).getDisplayName();
                    String str = String.join(", ", list);
                    String str2 = displayName;
                    var obj = key;
                    this.sendMessage("§7  Slot " + obj + ": §b" + str2 + " §7with §e" + str);
                }

                this.sendMessage("§a§l[AutoEnchant] §a§lSUCCESS! §fFound item with target enchants!");
                this.sendMessage("§a[AutoEnchant] §fToggling off module to keep the item...");
                this.process28();
            } else {
                this.sendMessage("§e[AutoEnchant] §fNo items with target enchants found, trashing all...");
                this.idleMode = AutoEnchant.IdleMode.TRASHING;
                this.timerUtil.reset();
            }
        }
    }

    private List<String> computeList(ItemStack stack) {
        List list = new ArrayList();
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound.hasKey("pit-mystic-enchants")) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("pit-mystic-enchants");

                for (Object obj : compoundTag.getKeySet()) {
                    String str = obj.toString();
                    int integer = compoundTag.getInteger(str);
                    list.add(str);
                    list.add(str + integer);
                }
            }

            if (tagCompound.hasKey("display")) {
                NBTTagCompound compoundTag2 = tagCompound.getCompoundTag("display");
                if (compoundTag2.hasKey("Lore")) {
                    NBTTagList tagList = compoundTag2.getTagList("Lore", 8);

                    for (int i = 0; i < tagList.tagCount(); i++) {
                        String stringTagAt = tagList.getStringTagAt(i);
                        String str2 = stringTagAt.replaceAll("§[0-9a-fk-or]", "").trim();
                        if (!str2.isEmpty()) {
                            String str3 = str2;
                            int n = this.computeInt(str2);
                            if (n > 0) {
                                str3 = str2.replaceAll("\\s+(I|II|III|IV|V|VI|VII|VIII|IX|X)$", "").trim();
                                list.add(str3 + n);
                            }

                            list.add(str3);
                        }
                    }
                }
            }

            return list;
        }
        return list;
    }

    private Map<Integer, ItemStack> computeMap() {
        HashMap map = new HashMap();
        if (this.mc.thePlayer != null && this.mc.thePlayer.inventoryContainer != null) {
            for (int i = 9; i < 45; i++) {
                ItemStack stack = this.getStackInSlot(i);
                if (stack != null && stack.getItem() != null && this.matchesItemStack(stack)) {
                    var list = this.computeList(stack);
                    boolean flag = true;

                    for (String str : this.itemSet) {
                        boolean flag2 = false;

                        for (String str2 : list) {
                            if (str2.toLowerCase().contains(str.toLowerCase())) {
                                flag2 = true;
                                break;
                            }
                        }

                        if (!flag2) {
                            flag = false;
                            break;
                        }
                    }

                    if (flag) {
                        map.put(i, stack);
                    }
                }
            }

            return map;
        }
        return map;
    }

    public static enum IdleMode {
        IDLE,
        FRESH,
        ENCHANTING,
        CHECKING,
        TRASHING,
        DROPPING;

        private static AutoEnchant.IdleMode[] computeIdleModeArray() {
            return new AutoEnchant.IdleMode[]{IDLE, FRESH, ENCHANTING, CHECKING, TRASHING, DROPPING};
        }
    }
}
