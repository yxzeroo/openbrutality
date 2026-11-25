package org.brutality.notification.modern;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class GetCurrentPlayerCountHandler {
    private static GetCurrentPlayerCountHandler instance;
    private final CleanupOldCooldownsHandler cleanupOldCooldownsHandler;
    private final Set<String> itemSet;
    private final Set<String> itemSet2;
    private final Set<String> itemSet3;
    private final Map<String, String> lookupMap;
    private final Set<String> itemSet4;
    private final Map<String, Boolean> lookupMap2;
    private final Set<String> itemSet5;
    private final List<String> entries = Arrays.asList(
        "Venom",
        "Misery",
        "Spite",
        "Needless Suffering",
        "Grim Reaper",
        "Nostalgia",
        "Hedge Fund",
        "Heartripper",
        "Sanguisuge",
        "Lycanthropy",
        "Mind Assault",
        "Golden Handcuffs"
    );
    private long lastUpdateTime = 0L;
    private final long timestamp = 100L;

    private String computeString(String str) {
        return str.replaceAll("§[0-9a-fk-or]", "").trim();
    }

    private void handleEntityPlayer(EntityPlayer player, String str, String str2) {
        String str3 = this.computeString2(player);
        if (str3 != null) {
            if (!this.itemSet4.contains(str)) {
                this.handleString2(str2, str, str3);
                this.itemSet4.add(str);
                this.lookupMap.put(str, str3);
            }
        } else if (this.itemSet4.contains(str)) {
            String orDefault = this.lookupMap.getOrDefault(str, "Unknown");
            this.handleString(str2, str, orDefault);
            this.itemSet4.remove(str);
            this.lookupMap.remove(str);
        }
    }

    public void update() {
        long now = System.currentTimeMillis();
        if (now - this.lastUpdateTime >= 100L) {
            this.lastUpdateTime = now;
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null && mc.thePlayer != null) {
                this.handleMinecraft(mc);
            }
        }
    }

    private void handleMinecraft(Minecraft mc, EntityPlayer player, String str) {
        boolean flag = FreshHelper.matchesWorld3(mc.theWorld, player);
        Boolean val = this.lookupMap2.get(str);
        if (val != null && val && !flag) {
            this.handleMinecraft(mc, str);
        }

        this.lookupMap2.put(str, flag);
    }

    public int computeInt3() {
        return (int)this.itemSet.stream().filter(WHelper::matchesString).count();
    }

    public static GetCurrentPlayerCountHandler getInstance() {
        if (instance == null) {
            instance = new GetCurrentPlayerCountHandler();
        }

        return instance;
    }

    private String computeString2(EntityPlayer player) {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                    NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                    if (compoundTag.hasKey("Lore", 9)) {
                        NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                        for (int j = 0; j < tagList.tagCount(); j++) {
                            String stringTagAt = tagList.getStringTagAt(j);

                            for (String str : this.entries) {
                                if (stringTagAt.contains(str)) {
                                    return str;
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private void handleString2(String str) {
        this.itemSet4.remove(str);
        this.lookupMap.remove(str);
        this.lookupMap2.remove(str);
        this.itemSet3.remove(str);
        this.itemSet5.remove(str);
    }

    public int computeInt() {
        return this.itemSet4.size();
    }

    private void handleString2(String str, String str2, String str3) {
        String str4 = String.format("%s §eequipped §5Dark Pants §f(§9%s§f)", str, str3);
        this.cleanupOldCooldownsHandler.handleString(str4, ModernHandler.PlayerJoinMode.DARK_EQUIPMENT, ModernHandler.LowMode.NORMAL, str2, false);
    }

    private void handleMinecraft(Minecraft mc) {
        HashSet set = new HashSet();

        for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
            String name = playerEntity.getName();
            String str = this.computeString(playerEntity);
            set.add(name);
            boolean flag = DefaultHelper.matchesString(name);
            boolean flag2 = WHelper.matchesString(name);
            if (!this.matchesEntityPlayer(playerEntity)) {
                this.itemSet3.add(name);
            } else {
                if (this.itemSet3.contains(name)) {
                    this.itemSet3.remove(name);
                }

                if ((flag || flag2) && !this.itemSet.contains(name)) {
                    this.handleString(name, str, flag, flag2);
                    this.itemSet2.remove(name);
                }

                if (flag2) {
                    this.handleMinecraft(mc, playerEntity, name);
                }

                this.handleEntityPlayer(playerEntity, name, str);
            }
        }

        this.handleSet(set);
        this.itemSet.clear();
        this.itemSet.addAll(set);
    }

    private void handleString(String str, boolean flag, boolean flag2) {
        String str2;
        ModernHandler.PlayerJoinMode playerJoinMode;
        ModernHandler.LowMode lowMode;
        if (flag) {
            str2 = "§a" + str;
            playerJoinMode = ModernHandler.PlayerJoinMode.FRIEND_ACTIVITY;
            lowMode = ModernHandler.LowMode.LOW;
        } else {
            if (!flag2) {
                return;
            }

            str2 = "§c" + str;
            playerJoinMode = ModernHandler.PlayerJoinMode.KOS_ACTIVITY;
            lowMode = ModernHandler.LowMode.NORMAL;
        }

        String str3 = str2 + " §fLeft.";
        this.cleanupOldCooldownsHandler.handleString(str3, playerJoinMode, lowMode, str, false);
    }

    public void handleString(String str) {
        this.itemSet.remove(str);
        this.itemSet2.remove(str);
        this.handleString2(str);
    }

    private String computeString(EntityPlayer player) {
        return player.getDisplayName().getFormattedText();
    }

    public int computeInt2() {
        return (int)this.itemSet.stream().filter(DefaultHelper::matchesString).count();
    }

    private void handleSet(Set<String> set) {
        for (String str : this.itemSet) {
            if (!set.contains(str) && !this.itemSet2.contains(str)) {
                boolean flag = DefaultHelper.matchesString(str);
                boolean flag2 = WHelper.matchesString(str);
                if (flag || flag2) {
                    this.handleString(str, flag, flag2);
                    this.itemSet2.add(str);
                    this.itemSet5.remove(str);
                }
            }
        }

        this.itemSet2.removeIf(str2 -> !this.itemSet.contains(str2) && !set.contains(str2));
        HashSet<String> set2 = new HashSet<>(this.itemSet);
        set2.removeAll(set);

        for (String str3 : set2) {
            this.handleString2(str3);
        }
    }

    private void handleString(String str, String str2, boolean flag, boolean flag2) {
        String str3;
        ModernHandler.PlayerJoinMode playerJoinMode;
        ModernHandler.LowMode lowMode;
        if (flag) {
            str3 = "§a" + str;
            playerJoinMode = ModernHandler.PlayerJoinMode.FRIEND_ACTIVITY;
            lowMode = ModernHandler.LowMode.NORMAL;
        } else {
            if (!flag2) {
                return;
            }

            str3 = "§c" + str;
            playerJoinMode = ModernHandler.PlayerJoinMode.KOS_ACTIVITY;
            lowMode = ModernHandler.LowMode.HIGH;
        }

        String str4 = str3 + " §fJoined.";
        this.cleanupOldCooldownsHandler.handleString(str4, playerJoinMode, lowMode, str, true);
    }

    public int getCurrentPlayerCount() {
        return this.itemSet.size();
    }

    private void handleString(String str, String str2, String str3) {
        String str4 = String.format("%s §cunequipped §5Dark Pants §f(§9%s§f)", str, str3);
        this.cleanupOldCooldownsHandler.handleString(str4, ModernHandler.PlayerJoinMode.DARK_EQUIPMENT, ModernHandler.LowMode.LOW, str2, false);
    }

    private void handleMinecraft(Minecraft mc, String str) {
        EntityPlayerSP player = mc.thePlayer;
        WorldClient world = mc.theWorld;
        String str2 = FreshHelper.computeString(player, world);
        if (str2.contains("Spawn")) {
            str2 = "" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "PIT";
        }

        String str3 = "§c" + str;
        String str4 = str3 + " §fJumped down! (" + str2 + "§f)";
        this.cleanupOldCooldownsHandler.handleString(str4, ModernHandler.PlayerJoinMode.PLAYER_JUMP, ModernHandler.LowMode.HIGH, str, true);
    }

    private GetCurrentPlayerCountHandler() {
        this.cleanupOldCooldownsHandler = CleanupOldCooldownsHandler.getInstance();
        this.itemSet = new HashSet<>();
        this.itemSet2 = new HashSet<>();
        this.itemSet3 = new HashSet<>();
        this.lookupMap = new HashMap<>();
        this.itemSet4 = new HashSet<>();
        this.lookupMap2 = new HashMap<>();
        this.itemSet5 = new HashSet<>();
    }

    public void clearAll() {
        this.itemSet.clear();
        this.itemSet2.clear();
        this.itemSet3.clear();
        this.lookupMap.clear();
        this.itemSet4.clear();
        this.lookupMap2.clear();
        this.itemSet5.clear();
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        String text = player.getDisplayName().getFormattedText();
        String name = player.getName();
        if (!text.equals(name) && !text.equals("§f" + name) && !text.replaceAll("§[0-9a-fk-or]", "").trim().equals(name)) {
            return true;
        }
        String str = text.replaceAll("§f", "").replaceAll("§r", "");
        return str.matches(".*§[0-9a-ek-o].*");
    }
}
