package org.brutality.module.impl.visual.notifications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import org.brutality.Brutality;
import org.brutality.module.impl.visual.Notifications;
import org.brutality.module.impl.visual.VisualHandler;
import org.brutality.module.impl.visual.notifications.NotificationsHandler.EeEntry;
import org.brutality.module.impl.visual.notifications.NotificationsHandler.L1l1Entry;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.hypixel.pit.KingsHelper;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.render.RenderHelper9;
import org.lwjgl.opengl.GL11;

public class NotificationFeedHandler {
    private static final boolean flag = true;
    private static final List<NotificationFeedHandler.Entry2> entries = new CopyOnWriteArrayList<>();
    private static final Map<String, String> lookupMap = new HashMap<>();
    private static final NotificationsHandler notificationsHandler = new NotificationsHandler();
    private static Object object;
    private static final Set<String> itemSet = new HashSet<>();
    private static final Set<String> itemSet2 = new HashSet<>();
    private static final NotificationsHandler2 notificationsHandler2 = new NotificationsHandler2();
    private static final Set<String> itemSet3 = new HashSet<>();
    private static final Set<String> itemSet4 = new HashSet<>();
    private static final Set<String> itemSet5 = new HashSet<>();
    private static final Map<String, ItemStack[]> lookupMap2 = new HashMap<>();
    private static final Map<String, String> lookupMap3 = new HashMap<>();
    private static final Map<String, Boolean> lookupMap4 = new HashMap<>();
    private static final Map<String, Integer> lookupMap5 = new HashMap<>();
    private static final Map<String, Long> lookupMap6 = new HashMap<>();
    private static final long timestamp = 1000L;
    private static final String text = "§4";
    private static final long timestamp2 = 9000L;
    private static final List<String> entries2 = Arrays.asList(
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
    private static final String text2 = "§5";
    private static final String text3 = "§e";
    private static final String text4 = "§c";
    private static final String text5 = "§f";
    private static final long timestamp3 = -5792310650487177217L;

    private static void handleEntityPlayer2(EntityPlayer player, String str) {
        Notifications instance = Notifications.getInstance();
        if (instance == null || instance.checkState11()) {
            boolean flag = WHelper.matchesString(str);
            String str2 = flag ? "§c" + str : str;
            String str3 = computeString2(player);
            String str4 = String.format("§8[§e!§8] %s §fJumped down! §7(%s§7)", str2, str3);
            handleString(str4);
            process();
        }
    }

    private static String computeString2(EntityPlayer player) {
        String str = KingsHelper.computeString(FreshHelper.computeString(player.getEntityWorld()), player.posX, player.posY, player.posZ);
        if (str != null && !str.isEmpty()) {
            String str2 = str.replace("Mid", "PIT");
            StringBuilder sb = new StringBuilder(str2.length());

            for (int i = 0; i < str2.length(); i++) {
                char ch = str2.charAt(i);
                sb.append(Character.toUpperCase(ch));
                if (ch == 167 && i + 1 < str2.length()) {
                    sb.append(str2.charAt(++i));
                }
            }

            return sb.toString();
        }
        return "§fUNKNOWN";
    }

    private static boolean matchesEntityPlayer(EntityPlayer player) {
        String text = player.getDisplayName().getFormattedText();
        String name = player.getName();
        if (!text.equals(name) && !text.equals("§f" + name) && !text.replaceAll("§[0-9a-fk-or]", "").trim().equals(name)) {
            return true;
        }
        String str = text.replaceAll("§f", "").replaceAll("§r", "");
        return str.matches(".*§[0-9a-ek-o].*");
    }

    private static boolean checkState() {
        Notifications instance = Notifications.getInstance();
        return instance != null && instance.spawnArmorCheckSetting.isEnabled();
    }

    private static void handleString(String str, boolean flag, boolean flag2) {
        Notifications instance = Notifications.getInstance();
        if (instance == null || instance.checkState()) {
            String str2 = flag ? "§a" + str : (flag2 ? "§c" + str : str);
            String str3 = VisualHandler.computeString(str);
            String str4 = String.format("§8[§a+§8] %s%s §fJoined.", str2, str3);
            handleString(str4);
        }
    }

    private static void handleSet(Set<String> set) {
        HashSet<String> set2 = new HashSet<>(itemSet);
        set2.removeAll(set);

        for (String str : set2) {
            itemSet2.remove(str);
            lookupMap.remove(str);
            notificationsHandler2.handleString(str);
            itemSet4.remove(str);
            itemSet5.remove(str);
            lookupMap2.remove(str);
            lookupMap3.remove(str);
            lookupMap6.remove(str);
            lookupMap4.remove(str);
            lookupMap5.remove(str);
        }
    }

    private static List<EeEntry> computeList() {
        Minecraft mc = Minecraft.getMinecraft();
        List list = new ArrayList();
        if (mc.getNetHandler() != null && mc.thePlayer != null) {
            for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                String name = playerInfo.getGameProfile().getName();
                if (name != null && !name.equalsIgnoreCase(mc.thePlayer.getName())) {
                    list.add(new EeEntry(name, DefaultHelper.matchesString(name), WHelper.matchesString(name)));
                }
            }

            return list;
        }
        return list;
    }

    public static void process6() {
        Minecraft mc = Minecraft.getMinecraft();
        notificationsHandler.handleCollection(computeList());
        object = mc.theWorld;
        if (mc.theWorld != null && mc.thePlayer != null) {
            for (EntityPlayer player : EntityCache.getEntries()) {
                String name = player.getName();
                if (matchesEntityPlayer(player)) {
                    boolean flag = DefaultHelper.matchesString(name);
                    boolean flag2 = WHelper.matchesString(name);
                    itemSet.add(name);
                    lookupMap6.put(name, System.currentTimeMillis());
                    String str = computeString(player, name);
                    if (str != null) {
                        itemSet2.add(name);
                        lookupMap.put(name, str);
                    }

                    if (flag2) {
                        notificationsHandler2.handleString(name);
                        boolean flag3 = FreshHelper.matchesWorld3(player.getEntityWorld(), player);
                        notificationsHandler2.matchesString(
                            name, player, flag3, flag3 && FreshHelper.matchesWorld(player.getEntityWorld(), player), !player.isDead && player.getHealth() > 0.0F, player.posY
                        );
                    }
                }
            }
        }
    }

    private static NotificationFeedHandler.Entry computeEntry(long l, float f) {
        NotificationFeedHandler.Entry entry = new NotificationFeedHandler.Entry();
        entry.posX = f + RenderHelper9.computeFloat2(l);
        float f2 = Math.max(0.0F, Math.min(1.0F, (float)(l - 8500L) / 500.0F));
        entry.offsetY = 6.0F * f2 * f2;
        entry.factor = Math.min(RenderHelper9.computeFloat(l), RenderHelper9.computeFloat3(9000L - l));
        return entry;
    }

    private static void handleString2(String str) {
    }

    public static void process5() {
        entries.clear();
        notificationsHandler.clear();
        object = null;
        process3();
    }

    private static String computeString(String str) {
        return str.replaceAll("§[0-9a-fk-or]", "").trim();
    }

    private static void handleEntityPlayer(EntityPlayer player, String str) {
        World entityWorld = player.getEntityWorld();
        if (FreshHelper.computeString(entityWorld) != null) {
            boolean flag = FreshHelper.matchesWorld3(entityWorld, player);
            if (notificationsHandler2.matchesString(
                    str, player, flag, flag && FreshHelper.matchesWorld(entityWorld, player), !player.isDead && player.getHealth() > 0.0F, player.posY
                )
                && (!checkState() || !FreshHelper.matchesEntityPlayer2(player))) {
                handleEntityPlayer2(player, str);
            }
        }
    }

    private static void handleString2(String str, String str2) {
        Notifications instance = Notifications.getInstance();
        if (instance == null || instance.checkState5()) {
            str = str.replaceAll("(?i)(?:\\s|§[0-9a-fk-or])+$", "").trim();
            String str3 = String.format("%s%s equipped %sDark Pants %s(§9%s§f)", str, "§e", "§5", "§f", str2);
            handleString(str3);
        }
    }

    private static void handleString2(String str, boolean flag, boolean flag2) {
        Notifications instance = Notifications.getInstance();
        if (instance == null || instance.checkState6()) {
            String str2 = flag ? "§a" + str : (flag2 ? "§c" + str : str);
            String str3 = VisualHandler.computeString(str);
            String str4 = String.format("§8[§c-§8] %s%s §fLeft.", str2, str3);
            handleString(str4);
        }
    }

    public static void handleMinecraft(Minecraft mc, FontRenderer fontRenderer, float f, float f2) {
        try {
            Brutality.renderNotifications();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        long now = System.currentTimeMillis();
        int n = fontRenderer.FONT_HEIGHT + 2;
        int n2 = (int)f2;
        ArrayList<NotificationFeedHandler.Entry2> entry2s = new ArrayList<>();
        var entry2s2 = new ArrayList<>(entries);
        if (entry2s2.isEmpty() && EditableModulesHelper.isActive()) {
            entry2s2.add(new NotificationFeedHandler.Entry2("§8[§e!§8] §cExamplePlayer §fJumped down! §7(§c§lPIT§7)", now - 900L));
            entry2s2.add(new NotificationFeedHandler.Entry2("§cExamplePlayer§e equipped §5Dark Pants §f(§9Venom§f)", now - 1500L));
        }

        for (NotificationFeedHandler.Entry2 entry2 : entry2s2) {
            long l = now - entry2.getTimestamp();
            if (l >= 9000L) {
                entry2s.add(entry2);
            } else {
                NotificationFeedHandler.Entry entry = computeEntry(l, f);
                int n3 = Math.max(0, Math.min(255, (int)(entry.factor * 255.0F)));
                int n4 = n3 << 24 | (int)timestamp3;
                if (n3 >= 4) {
                    fontRenderer.drawStringWithShadow(entry2.getMessage(), entry.posX, n2 + entry.offsetY, n4);
                }

                n2 += n;
            }
        }

        for (NotificationFeedHandler.Entry2 entry22 : entry2s) {
            entries.remove(entry22);
        }

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private static void handleEntityPlayer(EntityPlayer player, String str, String str2) {
        if (!itemSet4.contains(str)) {
            String str3 = computeString(player, str);
            boolean flag = str3 != null;
            boolean flag2 = itemSet2.contains(str);
            if (flag == flag2) {
                lookupMap4.remove(str);
                lookupMap5.remove(str);
            } else {
                Boolean val = lookupMap4.get(str);
                int n = val != null && val == flag ? lookupMap5.getOrDefault(str, 0) + 1 : 1;
                lookupMap4.put(str, flag);
                lookupMap5.put(str, n);
                if (n < 2) {
                    return;
                }
            }

            Long val2 = lookupMap6.get(str);
            if (val2 != null && System.currentTimeMillis() - val2 < 1000L) {
                if (flag) {
                    itemSet2.add(str);
                    lookupMap.put(str, str3);
                }
            } else {
                if (flag != flag2) {
                    Minecraft mc = Minecraft.getMinecraft();
                    if (mc.theWorld == null) {
                        return;
                    }

                    boolean flag3 = false;

                    for (EntityPlayer player2 : EntityCache.getEntries()) {
                        if (player2.getName().equals(str) && matchesEntityPlayer(player2)) {
                            flag3 = true;
                            break;
                        }
                    }

                    if (!flag3) {
                        return;
                    }
                }

                if (flag && !flag2) {
                    handleString2("EQUIP player=" + str + " enchant=" + str3);
                    handleString2(str2, str3);
                    itemSet2.add(str);
                    lookupMap.put(str, str3);
                } else if (!flag && flag2) {
                    String orDefault = lookupMap.getOrDefault(str, "Unknown");
                    handleString2("UNEQUIP player=" + str + " enchant=" + orDefault);
                    handleString(str2, orDefault);
                    itemSet2.remove(str);
                    lookupMap.remove(str);
                }

                lookupMap4.remove(str);
                lookupMap5.remove(str);
            }
        }
    }

    private static String computeString(EntityPlayer player, String str) {
        ItemStack[] stacks = lookupMap2.get(str);
        ItemStack[] stacks2 = new ItemStack[4];
        System.arraycopy(player.inventory.armorInventory, 0, stacks2, 0, 4);
        if (stacks != null && matchesItemStackArray(stacks, stacks2)) {
            return lookupMap3.get(str);
        }
        String str2 = computeString(player);
        ItemStack[] stacks3 = new ItemStack[4];
        System.arraycopy(stacks2, 0, stacks3, 0, 4);
        lookupMap2.put(str, stacks3);
        lookupMap3.put(str, str2);
        return str2;
    }

    private static void handleString(String str, String str2) {
        Notifications instance = Notifications.getInstance();
        if (instance == null || instance.checkState8()) {
            str = str.replaceAll("(?i)(?:\\s|§[0-9a-fk-or])+$", "").trim();
            String str3 = String.format("%s%s unequipped %sDark Pants %s(§9%s§f)", str, "§c", "§5", "§f", str2);
            handleString(str3);
        }
    }

    private static void process3() {
        lookupMap.clear();
        itemSet.clear();
        itemSet2.clear();
        notificationsHandler2.clear();
        itemSet3.clear();
        itemSet4.clear();
        itemSet5.clear();
        lookupMap2.clear();
        lookupMap3.clear();
        lookupMap6.clear();
        lookupMap4.clear();
        lookupMap5.clear();
    }

    public static void handleString(String str) {
        entries.add(new NotificationFeedHandler.Entry2(str, System.currentTimeMillis()));
    }

    public static void process4() {
        notificationsHandler.handleLong(System.currentTimeMillis());
        object = null;
        process3();
    }

    public static void process2() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                process7();
                HashSet set = new HashSet();
                HashSet set2 = new HashSet();

                for (EntityPlayer player : EntityCache.getEntries()) {
                    String name = player.getName();
                    if (set2.add(name)) {
                        if (!matchesEntityPlayer(player)) {
                            itemSet4.add(name);
                        } else {
                            if (itemSet4.contains(name)) {
                                itemSet4.remove(name);
                            }

                            boolean flag = DefaultHelper.matchesString(name);
                            boolean flag2 = WHelper.matchesString(name);
                            String str = computeString3(player);
                            set.add(name);
                            if (!itemSet.contains(name)) {
                                lookupMap6.put(name, System.currentTimeMillis());
                            }

                            if (flag2) {
                                handleEntityPlayer(player, name);
                            }

                            handleEntityPlayer(player, name, str);
                        }
                    }
                }

                handleSet(set);
                itemSet.clear();
                itemSet.addAll(set);
            }
        }
    }

    private static String computeString(EntityPlayer player) {
        ItemStack stack = player.inventory.armorInventory[1];
        if (stack != null && stack.getItem() instanceof ItemArmor) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                if (compoundTag.hasKey("Lore", 9)) {
                    NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                    for (int i = 0; i < tagList.tagCount(); i++) {
                        String stringTagAt = tagList.getStringTagAt(i);

                        for (String str : entries2) {
                            if (stringTagAt.contains(str)) {
                                return str;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private static void process() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.playSound("random.anvil_land", 1.0F, 1.0F);
        }
    }

    private static void process7() {
        Minecraft mc = Minecraft.getMinecraft();
        if (object != mc.theWorld) {
            notificationsHandler.handleLong(System.currentTimeMillis());
            object = mc.theWorld;
        }

        if (mc.getNetHandler() != null && !mc.getNetHandler().getPlayerInfoMap().isEmpty()) {
            for (L1l1Entry l1l1Entry : notificationsHandler.computeList(computeList(), System.currentTimeMillis())) {
                EeEntry eeEntry = l1l1Entry.l1l1();
                if (l1l1Entry.hi()) {
                    handleString(eeEntry.ee(), eeEntry.hj(), eeEntry.hk());
                } else {
                    handleString2(eeEntry.ee(), eeEntry.hj(), eeEntry.hk());
                }
            }
        }
    }

    private static boolean matchesItemStackArray(ItemStack[] stacks, ItemStack[] stacks2) {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = stacks[i];
            ItemStack stack2 = stacks2[i];
            if (stack != null || stack2 != null) {
                if (stack == null || stack2 == null) {
                    return false;
                }

                if (stack.getItem() != stack2.getItem() || stack.stackSize != stack2.stackSize) {
                    return false;
                }

                NBTTagCompound tagCompound = stack.getTagCompound();
                NBTTagCompound tagCompound2 = stack2.getTagCompound();
                if (tagCompound != null || tagCompound2 != null) {
                    if (tagCompound == null || tagCompound2 == null) {
                        return false;
                    }

                    if (!tagCompound.equals(tagCompound2)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static String computeString3(EntityPlayer player) {
        return player.getDisplayName().getFormattedText();
    }

    public static class Entry {
        public float posX = 2.0F;
        public float offsetY = 0.0F;
        public float factor = 1.0F;
    }

    public static class Entry2 {
        private final String message;
        private final long timestamp;

        public long getTimestamp() {
            return this.timestamp;
        }

        public Entry2(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return this.message;
        }
    }
}
