package org.brutality.util.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.clickgui.classic.ConfirmScreen;
import org.brutality.clickgui.classic.DrawSettingLabelScreen;
import org.brutality.clickgui.newclick.NewClickGUI;
import org.brutality.core.DisplayNameHandler;
import org.brutality.util.ActivateSwapInputGuardHelper;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.util.LockForMillisHelper;

public class GetMysticEnchantLoreHelper {
    private static Minecraft minecraft = Minecraft.getMinecraft();
    private static int count = 8;
    private static final List<String> r = Arrays.asList("sweaty");
    public static int count2;
    private static final List<GetSlotIndexHelper> entries = Arrays.asList(
        new GetSlotIndexHelper("Gamble", "gamble", "Gam", EnumChatFormatting.DARK_PURPLE),
        new GetSlotIndexHelper("Billionaire", "billionaire", "Bil", EnumChatFormatting.GOLD),
        new GetSlotIndexHelper("Perun", "perun's", "Perun", EnumChatFormatting.AQUA),
        new GetSlotIndexHelper("Executioner", "executioner", "Exe", EnumChatFormatting.RED),
        new GetSlotIndexHelper("Lifesteal", "lifesteal", "LS", EnumChatFormatting.RED),
        new GetSlotIndexHelper("Stomp", "diamond stomp", "Stomp", EnumChatFormatting.AQUA),
        new GetSlotIndexHelper("C-Heal", "combo: heal", "CHeal", EnumChatFormatting.YELLOW),
        new GetSlotIndexHelper("Duelist", "duelist", "Duel", EnumChatFormatting.DARK_RED),
        new GetSlotIndexHelper("Pain Focus", "pain focus", "PF", EnumChatFormatting.DARK_RED),
        new GetSlotIndexHelper("Berserker", "berserker", "Bers", EnumChatFormatting.GREEN),
        new GetSlotIndexHelper("Bounty Reaper", "bounty reaper", "BReaper", EnumChatFormatting.RED)
    );
    public static List<GetSlotIndexHelper> entries2;
    private static HashMap<String, String> lookupMap;
    public static final List<Item> entries3 = new GetMysticEnchantLoreHelper.Entry();
    public static List<GetSlotIndexHelper> entries4;
    private static final List<String> w = Arrays.asList("moctezuma", "gold boost", "gold bump", "pit mba");
    private static final List<GetSlotIndexHelper> x = Arrays.asList(
        new GetSlotIndexHelper("MLB", "mega longbow", "MLB", EnumChatFormatting.GREEN),
        new GetSlotIndexHelper("True Shot", "true shot", "True", EnumChatFormatting.RED),
        new GetSlotIndexHelper("Volley", "volley", "Vol", EnumChatFormatting.LIGHT_PURPLE),
        new GetSlotIndexHelper("Pullbow", "pullbow", "Pull", EnumChatFormatting.LIGHT_PURPLE),
        new GetSlotIndexHelper("Telebow", "telebow", "Tele", EnumChatFormatting.LIGHT_PURPLE),
        new GetSlotIndexHelper("Pin Down", "pin down", "Pin", EnumChatFormatting.GRAY),
        new GetSlotIndexHelper("Wasp", "wasp", "Wasp", EnumChatFormatting.RED),
        new GetSlotIndexHelper("Sprint Drain", "sprint drain", "Drain", EnumChatFormatting.YELLOW)
    );
    public static int count3;

    public static boolean matchesItemStack6(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        long l = computeLong(stack);
        return l == -1L ? false : (l > count2 ? 1 : (l == count2 ? 0 : -1)) == 0;
    }

    public static int computeInt2(String str) {
        ItemStack[] mainInventory = Minecraft.getMinecraft().thePlayer.inventory.mainInventory;
        String str2 = str.toLowerCase();

        for (int i = 0; i < Integer.parseInt(lookupMap.getOrDefault("tke12vvv", "3")); i++) {
            ItemStack stack = mainInventory[i];
            if (stack != null && stack.getUnlocalizedName().toLowerCase().contains(str2)) {
                return i;
            }
        }

        return -1;
    }

    public static boolean matchesEntityPlayer6(EntityPlayer player) {
        for (int i = 0; i < 3; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null) {
                if (i == 1) {
                    boolean flag = stack != null && stack.getItem() == Items.leather_leggings ? stack.getDisplayName().contains("Fresh") : false;
                    if (!flag) {
                        ItemArmor item = (ItemArmor)stack.getItem();
                        if (item.getArmorMaterial() != ArmorMaterial.IRON) {
                        }
                    }
                } else {
                    ItemArmor item2 = (ItemArmor)stack.getItem();
                    if (item2.getArmorMaterial() != ArmorMaterial.IRON && item2.getArmorMaterial() != ArmorMaterial.CHAIN) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static int computeInt() {
        return computeInt3(9);
    }

    public static boolean checkState10() {
        GuiScreen screen = minecraft.currentScreen;
        if (screen instanceof GuiIngameMenu) {
            return true;
        }
        if (screen instanceof GuiOptions) {
            return true;
        }
        if (screen instanceof GuiAchievements) {
            return true;
        }
        if (screen instanceof GuiStats) {
            return true;
        }
        if (screen instanceof GuiShareToLan) {
            return true;
        }
        if (screen instanceof GuiVideoSettings) {
            return true;
        }
        return !(screen instanceof GuiControls) ? screen instanceof GuiScreenOptionsSounds : true;
    }

    public static boolean checkState8() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return false;
        }
        if (mc.thePlayer.getHeldItem() == null) {
            return false;
        }
        return mc.thePlayer.getHeldItem().getItem().getUnlocalizedName().equals(lookupMap.get("wfefwbefhebshfsbehfbsehfb"))
            ? true
            : mc.thePlayer.getHeldItem().getItem().getUnlocalizedName().equals(lookupMap.get("wfefwbefhebshfsbehfbsehfb12312"));
    }

    public static boolean matchesEntityPlayer10(EntityPlayer player) {
        ItemStack[] armorInventory = player.inventory.armorInventory;

        for (ItemStack stack : armorInventory) {
            if (stack != null
                && (
                    stack.getItem() == Items.diamond_helmet
                        || stack.getItem() == Items.diamond_chestplate
                        || stack.getItem() == Items.diamond_leggings
                        || stack.getItem() == Items.diamond_boots
                )) {
                return true;
            }
        }

        return false;
    }

    public static boolean matchesItem(Item item) {
        return item instanceof ItemSword ? true : item instanceof ItemAxe || item == Items.diamond_shovel;
    }

    static {
        var lookupMap9 = DisplayNameHandler.lookupMap9;
        lookupMap = lookupMap9;
        count2 = Integer.parseInt(lookupMap9.getOrDefault("rp1n", "5")) + 8;
        count3 = Integer.parseInt(lookupMap.getOrDefault("dp2n", "11")) - 49;
        entries2 = Arrays.asList(
            new GetSlotIndexHelper(lookupMap.getOrDefault("reg2dn", "Solitude"), lookupMap.getOrDefault("reg2n", "solitude"), "Misery", EnumChatFormatting.RED),
            new GetSlotIndexHelper(lookupMap.getOrDefault("reg3dn", "Crit funky"), lookupMap.getOrDefault("reg3n", "crit"), "Spite", EnumChatFormatting.RED),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("reg4dn", "Mcswimmer"), lookupMap.getOrDefault("reg4n", "mcswimmer"), "Needless", EnumChatFormatting.RED
            ),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("reg5dn", "pero"), lookupMap.getOrDefault("reg5n", "peroxide"), "Grim", EnumChatFormatting.DARK_PURPLE
            ),
            new GetSlotIndexHelper(lookupMap.getOrDefault("reg6dn", "Booboo"), lookupMap.getOrDefault("reg6n", "booboo"), "Nostalgia", EnumChatFormatting.AQUA),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("reg1dn", "Martyrdom"), lookupMap.getOrDefault("reg1n", "martyrdom"), "Venom", EnumChatFormatting.GREEN
            ),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("reg7dn", "rgm"), lookupMap.getOrDefault("reg7n", "retro gravity"), "Hedge", EnumChatFormatting.DARK_PURPLE
            ),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("reg8dn", "Prot"), lookupMap.getOrDefault("reg8n", "protection"), "Heartripper", EnumChatFormatting.DARK_PURPLE
            ),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("reg9dn", "Pebble"), lookupMap.getOrDefault("reg9n", "Pebble"), "Sang", EnumChatFormatting.DARK_PURPLE
            ),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("reg10dn", "Speed"), lookupMap.getOrDefault("reg10n", "gotta go fast"), "Lycan", EnumChatFormatting.DARK_PURPLE
            ),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("reg11dn", "ep"), lookupMap.getOrDefault("reg11n", "escape pod"), "Mind", EnumChatFormatting.DARK_PURPLE
            ),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("reg12dn", "Pond"), lookupMap.getOrDefault("reg12n", "portable pond"), "GHandcuffs", EnumChatFormatting.DARK_PURPLE
            )
        );
        entries4 = Arrays.asList(
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("darke1dn", "Regularity"), lookupMap.getOrDefault("darke1n", "Reuglarity"), "Reg", EnumChatFormatting.DARK_RED
            ),
            new GetSlotIndexHelper(lookupMap.getOrDefault("darke2dn", "Ace"), lookupMap.getOrDefault("darke2n", "Ace Of Spades"), "Fr", EnumChatFormatting.GOLD),
            new GetSlotIndexHelper(lookupMap.getOrDefault("darke3dn", "Mirror"), lookupMap.getOrDefault("darke3n", "mirror"), "ND", EnumChatFormatting.DARK_RED),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("darke44dn", "Mirror"), lookupMap.getOrDefault("darke44n", "mirror"), "Ho", EnumChatFormatting.DARK_RED
            ),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("darke4dn", "Really toxic"), lookupMap.getOrDefault("darke4n", "toxic"), "Sol", EnumChatFormatting.BLUE
            ),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("darke5dn", "devil"), lookupMap.getOrDefault("darke5n", "deal with the devil"), "CF", EnumChatFormatting.DARK_AQUA
            ),
            new GetSlotIndexHelper(lookupMap.getOrDefault("darke6dn", "French"), lookupMap.getOrDefault("darke6n", "french"), "RGM", EnumChatFormatting.YELLOW),
            new GetSlotIndexHelper(lookupMap.getOrDefault("darke7dn", "Reg"), lookupMap.getOrDefault("darke7n", "Reg"), "Prick", EnumChatFormatting.DARK_GREEN),
            new GetSlotIndexHelper(lookupMap.getOrDefault("darke8dn", "Brakes"), lookupMap.getOrDefault("darke8n", "brakes"), "Mir", EnumChatFormatting.WHITE),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("darke9dn", "peruns wrath"), lookupMap.getOrDefault("darke9n", "perun"), "Per", EnumChatFormatting.RED
            ),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("darke10dn", "radar"), lookupMap.getOrDefault("darke10n", "pants radar"), "Fast", EnumChatFormatting.YELLOW
            ),
            new GetSlotIndexHelper(
                lookupMap.getOrDefault("darke11dn", "Not glad"), lookupMap.getOrDefault("darke11n", "not glads"), "Pod", EnumChatFormatting.GREEN
            ),
            new GetSlotIndexHelper(lookupMap.getOrDefault("darke12dn", "Test"), lookupMap.getOrDefault("darke12n", "test"), "Abs", EnumChatFormatting.GOLD),
            new GetSlotIndexHelper("Sing", "singularity", "Sing", EnumChatFormatting.LIGHT_PURPLE),
            new GetSlotIndexHelper("Assassin", "assassin", "Ass", EnumChatFormatting.DARK_GRAY),
            new GetSlotIndexHelper("Phoenix", "phoenix", "Pho", EnumChatFormatting.RED)
        );
    }

    public static boolean checkState9() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return false;
        }
        return mc.thePlayer.inventory.getCurrentItem() == null
            ? false
            : mc.thePlayer.inventory.getCurrentItem().getItem().getUnlocalizedName().equals(lookupMap.get("tbeepoqwdlqwef"));
    }

    public static boolean matchesEntityPlayer3(EntityPlayer player) {
        if (player.inventory.armorInventory == null) {
            return false;
        }
        if (player.inventory.armorInventory[Integer.parseInt(lookupMap.get("itn142nn"))] == null) {
            return false;
        }
        ItemStack stack = player.inventory.armorInventory[Integer.parseInt(lookupMap.get("itn1244nn523535"))];
        long l = computeLong(stack);
        if (stack.getDisplayName().contains("Fresh")) {
            return false;
        }
        return l == -1L ? false : (l > count3 ? 1 : (l == count3 ? 0 : -1)) == 0;
    }

    public static boolean checkState3() {
        ItemStack[] mainInventory = minecraft.thePlayer.inventory.mainInventory;

        for (ItemStack stack : mainInventory) {
            if (stack == null) {
                return false;
            }
        }

        return true;
    }

    public static boolean matchesItemStack4(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return !minecraft.thePlayer.inventory.getCurrentItem().getItem().getUnlocalizedName().equals(lookupMap.get("tbeepoqwdlqwef"))
            ? false
            : computeList(minecraft.thePlayer.inventory.getCurrentItem())
                .toString()
                .toLowerCase()
                .contains(lookupMap.getOrDefault("tbeepoqwdlq123123wef", "t5b4124eepoqwdlqweftbeepo6qwdlqweftbeepoqwdlqweftbeepoqwdlqweftbeepoqwdlqwef"));
    }

    public static boolean matchesEntityPlayer5(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        if (player.inventory.armorInventory == null) {
            return false;
        }
        ItemStack stack = player.inventory.armorInventory[1];
        return stack == null ? false : (new MysticItemInfo(stack)).lookupMap2.containsKey(VenomType.ESCAPE_POD);
    }

    public static int computeInt(ItemStack stack) {
        return stack == null ? -1 : stack.writeToNBT(new NBTTagCompound()).getCompoundTag("tag").getCompoundTag("ExtraAttributes").getInteger("UpgradeTier");
    }

    public static boolean matchesLong2(long l) {
        return (l > count ? 1 : (l == count ? 0 : -1)) == 0;
    }

    public static void process() {
        minecraft.addScheduledTask(GetMysticEnchantLoreHelper::process2);
    }

    public static int computeInt(EntityPlayer player, String str) {
        int n = 0;
        ItemStack stack = player.inventory.armorInventory[Integer.parseInt(lookupMap.getOrDefault("wefh8uwehf8wehfwehfywe", "20"))];
        if (stack == null) {
            return -1;
        }
        var list = computeList(stack);
        if (list == null) {
            return -1;
        }
        String[] parts = list.toString().split(lookupMap.get("cecbebcebcbeycbeycbyecbyecbyecbyecbye"));

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].contains(str)) {
                String[] parts2 = parts[i + 1].split(lookupMap.get("cecbebcebcbeycbeycbyecbyecbyecbyecbye12"));
                if (!parts2[0].equals(lookupMap.get("defcecbebcebcbeycbeycbyecbyecbyecbyecbye12123"))) {
                    return Integer.parseInt(lookupMap.getOrDefault("defcecbebcebcbeycbeycbyecbyecbyecbyecbye12", "-1"));
                }

                for (String str2 : parts2) {
                    if (str2.equals(lookupMap.get("defcecbebcebcbeycbeycbyecbyecbyecbyecbye12123"))) {
                        n++;
                    }
                }

                return n;
            }
        }

        return -1;
    }

    public static int computeInt(int n) {
        switch (n) {
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            default:
                return -99;
        }
    }

    public static boolean matchesItemStack11(ItemStack stack) {
        return stack.getItem() == Items.feather;
    }

    public static int computeInt(EntityPlayer player) {
        if (player == null) {
            return 0;
        }
        int n = 0;
        ItemStack currentArmor = player.getCurrentArmor(1);
        if (currentArmor != null && currentArmor.getItem() == Items.leather_leggings && (new MysticItemInfo(currentArmor)).count3 == 3) {
            n = 2;
        }

        ItemStack currentArmor2 = player.getCurrentArmor(3);
        if (currentArmor2 != null) {
            n = currentArmor2.getItem() != Items.golden_helmet && currentArmor2.getItem() != Items.diamond_helmet ? n + 1 : n + 2;
        }

        ItemStack currentArmor3 = player.getCurrentArmor(2);
        ItemStack currentArmor4 = player.getCurrentArmor(0);
        if (currentArmor3 != null && currentArmor3.getItem() == Items.diamond_chestplate) {
            n++;
        }

        if (currentArmor4 != null && currentArmor4.getItem() == Items.diamond_boots) {
            n++;
        }

        return n;
    }

    public static boolean matchesItemStack9(ItemStack stack) {
        return stack != null && stack.getItem() == Items.leather_leggings ? stack.getDisplayName().contains("Fresh") : false;
    }

    public static boolean checkState() {
        return matchesEntityPlayer7(minecraft.thePlayer);
    }

    public static synchronized String computeString(EntityPlayer player, boolean flag) {
        if (player.inventory.armorInventory == null) {
            return lookupMap.getOrDefault("rt1", "Diamond");
        }
        if (player.inventory.armorInventory[1] == null) {
            return lookupMap.getOrDefault("rt2", "Iron");
        }
        if (!player.inventory.armorInventory[1].getItem().getUnlocalizedName().equals(lookupMap.getOrDefault("l1nplate", "Chestplate"))) {
            String displayName = player.inventory.armorInventory[Integer.parseInt(lookupMap.getOrDefault("ain34hfv384hg834hg83h483hdafwaefawfqwf", "35"))]
                .getDisplayName();
            if (displayName.toLowerCase().contains(lookupMap.get("dl123"))) {
                return EnumChatFormatting.AQUA + (flag ? "Dia" : "Diamond");
            }
            return displayName.toLowerCase().contains(lookupMap.get("ddl123"))
                ? String.valueOf(new StringBuilder().append(EnumChatFormatting.GOLD).append(lookupMap.get("ddl123n")))
                : "";
        }
        var mysticEnchantLore = getMysticEnchantLore(player.inventory.armorInventory[1]);
        if (mysticEnchantLore.isEmpty()) {
            return lookupMap.getOrDefault("IllIIIILLILIIllIIILL", "Fresh Rages");
        }
        for (GetSlotIndexHelper getSlotIndexHelper : entries2) {
            for (String str : mysticEnchantLore) {
                if (str.toLowerCase().contains(getSlotIndexHelper.text2)) {
                    return getSlotIndexHelper.enumChatFormatting + (flag ? getSlotIndexHelper.text3 : getSlotIndexHelper.text);
                }
            }
        }

        String orDefault = lookupMap.getOrDefault("ens", String.valueOf(EnumChatFormatting.STRIKETHROUGH));
        StringBuilder sb = new StringBuilder();

        for (GetSlotIndexHelper getSlotIndexHelper2 : entries4) {
            for (String str2 : mysticEnchantLore) {
                if (str2.toLowerCase().contains(getSlotIndexHelper2.text2)) {
                    sb.append(getSlotIndexHelper2.enumChatFormatting).append(flag ? getSlotIndexHelper2.text3 : getSlotIndexHelper2.text).append(EnumChatFormatting.GRAY).append(orDefault);
                }
            }
        }

        String orDefault2 = lookupMap.getOrDefault("ens", String.valueOf(EnumChatFormatting.BOLD));
        if (sb.toString().endsWith(orDefault2) && sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    public static Map<VenomType, Integer> computeMap(ItemStack stack) {
        return (new MysticItemInfo(stack)).lookupMap2;
    }

    public static boolean matchesItemStack(ItemStack stack, VenomType venomType) {
        return (new MysticItemInfo(stack)).lookupMap2.containsKey(venomType);
    }

    public static boolean matchesLong(long l) {
        return l != count2 ? (l > count3 ? 1 : (l == count3 ? 0 : -1)) == 0 : true;
    }

    public static boolean checkState2() {
        return minecraft.currentScreen instanceof NewClickGUI
            || minecraft.currentScreen instanceof DrawSettingLabelScreen
            || minecraft.currentScreen instanceof ConfirmScreen;
    }

    public static int computeInt(String str) {
        ItemStack[] mainInventory = Minecraft.getMinecraft().thePlayer.inventory.mainInventory;
        String str2 = str.toLowerCase();
        int n = Integer.parseInt(lookupMap.getOrDefault("tke12vvv", "3"));

        for (int i = 0; i < n; i++) {
            ItemStack stack = mainInventory[i];
            if (stack != null && stack.getDisplayName().toLowerCase().contains(str2)) {
                return i;
            }
        }

        return -1;
    }

    public static EnumChatFormatting computeEnumChatFormatting(String str) {
        var list = Arrays.asList(entries4, entries2, entries, x);
        String str2 = str.toLowerCase();

        for (List list2 : list) {
            for (GetSlotIndexHelper getSlotIndexHelper : ((Iterable<GetSlotIndexHelper>)(Object)(list2))) {
                if (str2.contains(getSlotIndexHelper.text2)) {
                    return getSlotIndexHelper.enumChatFormatting;
                }
            }
        }

        return EnumChatFormatting.GRAY;
    }

    public static boolean checkState5() {
        return matchesGuiScreen(minecraft.currentScreen);
    }

    public static boolean matchesEntityPlayer4(EntityPlayer player) {
        if (player.inventory.armorInventory == null) {
            return false;
        }
        if (player.inventory.armorInventory[Integer.parseInt(lookupMap.get("itn142nn"))] == null) {
            return false;
        }
        ItemStack stack = player.inventory.armorInventory[Integer.parseInt(lookupMap.get("itn1244nn523535"))];
        long l = computeLong(player.inventory.armorInventory[Integer.parseInt(lookupMap.get("itn1244nn523535"))]);
        if (stack.getDisplayName().contains("Fresh")) {
            return false;
        }
        return l == -1L ? false : (l > count2 ? 1 : (l == count2 ? 0 : -1)) == 0;
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        ItemStack[] armorInventory = player.inventory.armorInventory;

        for (ItemStack stack : armorInventory) {
            if (stack != null
                && (
                    stack.getItem() == Items.leather_helmet
                        || stack.getItem() == Items.leather_chestplate
                        || stack.getItem() == Items.leather_leggings
                        || stack.getItem() == Items.leather_boots
                )) {
                return true;
            }
        }

        return false;
    }

    public static boolean matchesItemStack2(ItemStack stack) {
        return stack == null ? false : stack.writeToNBT(new NBTTagCompound()).getCompoundTag("tag").hasKey("ExtraAttributes");
    }

    public static List<String> getMysticEnchantLore(ItemStack stack) {
        var list = computeList(stack);
        List<String> list2 = new ArrayList<>();
        String[] parts = new String[]{"as strong as", "attack damage", "require heresy", "mystic well", "original:"};

        for (int i = 0; i < list.size(); i++) {
            if (((String)list.get(i)).isEmpty() && i + 1 != list.size()) {
                list2.add((String)list.get(i + 1));
            }
        }

        if (!list2.isEmpty()) {
            list2.removeIf(str -> {
                String str2 = str.toLowerCase();

                for (String str3 : parts) {
                    if (str2.contains(str3)) {
                        return true;
                    }
                }

                return false;
            });
        }

        return list2;
    }

    public static int computeInt2() {
        return computeInt3(36);
    }

    public static ItemStack computeItemStack() {
        return minecraft.thePlayer.inventory.armorInventory[1];
    }

    public static int computeInt(int n, int n2) {
        ItemStack[] mainInventory = Minecraft.getMinecraft().thePlayer.inventory.mainInventory;

        for (int i = 0; i < Integer.parseInt(lookupMap.getOrDefault("tke12vvv", "3")); i++) {
            ItemStack stack = mainInventory[i];
            if (stack != null) {
                int idFromItem = Item.getIdFromItem(stack.getItem());
                int metadata = stack.getMetadata();
                if (idFromItem == n && metadata == n2) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static boolean checkState4() {
        return minecraft.currentScreen instanceof GuiChat;
    }

    public static boolean matchesItemStack5(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        String str = ExecuteCommandHelper.computeString6(stack.getDisplayName());
        if (!entries3.contains(stack.getItem())) {
            return false;
        }
        return !str.startsWith("Fresh") ? str.startsWith("Mystic") : true;
    }

    public static boolean matchesItemStack7(ItemStack stack) {
        if (stack != null && stack.getItem() == Items.potionitem) {
            NBTTagList tagList = stack.writeToNBT(new NBTTagCompound()).getCompoundTag("tag").getTagList("CustomPotionEffects", 10);
            if (tagList == null) {
                return false;
            }
            if (tagList.tagCount() == 0) {
                return false;
            }
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(0);
            return compoundTagAt != null && compoundTagAt.getByte("Id") == Potion.jump.getId();
        }
        return false;
    }

    public static int computeInt2(Item item) {
        int n = 0;
        ItemStack[] mainInventory = minecraft.thePlayer.inventory.mainInventory;

        for (ItemStack stack : mainInventory) {
            if (stack != null && stack.getItem().equals(item)) {
                n += stack.stackSize;
            }
        }

        return n;
    }

    public static List<String> computeList(ItemStack stack) {
        List list = new ArrayList();

        try {
            NBTTagList tagList = stack.writeToNBT(new NBTTagCompound())
                .getCompoundTag(lookupMap.get("mbhhdhdrhdrhdrq"))
                .getCompoundTag(lookupMap.get("hgdhdrhdrhdr"))
                .getTagList(lookupMap.get("mbsegfsegq"), Integer.parseInt(lookupMap.get("mbqdaw")) - 2);

            for (int i = 0; i < tagList.tagCount(); i++) {
                list.add(tagList.getStringTagAt(i));
            }

            return list;
        } catch (Exception e) {
            return list;
        }
    }

    public static boolean matchesEntityPlayer9(EntityPlayer player) {
        return player == null ? false : player.inventory.armorInventory[1] != null && player.inventory.armorInventory[1].getItem() == Items.golden_leggings;
    }

    public static boolean matchesEntityPlayer7(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        ItemStack heldItem = player.getHeldItem();
        return heldItem != null && heldItem.getItem() == Items.potionitem ? player.isUsingItem() : false;
    }

    public static int computeInt2(int n) {
        for (int i = 0; i < n; i++) {
            ItemStack stack = minecraft.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == 3) {
                return i;
            }
        }

        return -1;
    }

    public static long computeLong(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            return -1L;
        }
        NBTTagCompound compoundTag = tagCompound.getCompoundTag("ExtraAttributes");
        return compoundTag == null ? -1L : compoundTag.getLong("Nonce");
    }

    public static ItemStack computeItemStack(Predicate<ItemStack> predicate) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = minecraft.thePlayer.inventory.mainInventory[i];
            if (stack != null && predicate.test(stack)) {
                return stack;
            }
        }

        return null;
    }

    public static boolean checkState6() {
        if (minecraft.thePlayer == null) {
            return false;
        }
        return minecraft.currentScreen == null ? false : minecraft.thePlayer.inventory.getItemStack() != null;
    }

    public static int computeInt3(int n) {
        for (int i = 0; i < n; i++) {
            ItemStack stack = minecraft.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == 2) {
                return i;
            }
        }

        return -1;
    }

    public static int computeInt(Predicate<ItemStack> predicate) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = minecraft.thePlayer.inventory.mainInventory[i];
            if (stack != null && predicate.test(stack)) {
                return i;
            }
        }

        return -1;
    }

    public static boolean isWearingVenomPants(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        if (player.inventory.armorInventory == null) {
            return false;
        }
        ItemStack stack = player.inventory.armorInventory[1];
        if (stack == null) {
            return false;
        }
        return matchesEntityPlayer3(player) ? getMysticEnchantLore(stack).stream().anyMatch(str -> str.contains("Combo: Venom")) : false;
    }

    public static boolean matchesEntityPlayer8(EntityPlayer player) {
        ItemStack stack = player.inventory.armorInventory[Integer.parseInt(lookupMap.get("itn1244nn523535"))];
        if (stack == null) {
            return false;
        }
        if (!(stack.getItem() instanceof ItemArmor)) {
            return false;
        }
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            return false;
        }
        if (!tagCompound.hasKey(lookupMap.get("hgdhdrhdrhdr"), Integer.parseInt(lookupMap.get("cbcbcbecbecbegdcbegbbcgebcgbecg12")))) {
            return false;
        }
        NBTTagCompound compoundTag = tagCompound.getCompoundTag(lookupMap.get("hgdhdrhdrhdr"));
        if (!compoundTag.hasKey(lookupMap.get("mbsegfsegq"), Integer.parseInt(lookupMap.get("cbcbcbecbecbegdcbegbbcgebcgbecg1222")))) {
            return false;
        }
        NBTTagList tagList = compoundTag.getTagList(lookupMap.get("mbsegfsegq"), Integer.parseInt(lookupMap.get("cbcbcbecbecbegdcbegbbcgebcgbecg5555")));

        for (int i = 0; i < tagList.tagCount(); i++) {
            if (tagList.getStringTagAt(i).contains(lookupMap.get("cbcbcbecbecbegdcbegbbcgebcgbecg"))) {
                return true;
            }
        }

        return false;
    }

    public static boolean matchesEntityPlayer11(EntityPlayer player) {
        return computeInt(player, "Sybil") != -1;
    }

    public static boolean matchesItemStack3(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        long l = computeLong(stack);
        return l == -1L ? false : (l > count3 ? 1 : (l == count3 ? 0 : -1)) == 0;
    }

    public static int computeInt3(Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = minecraft.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == item) {
                return i;
            }
        }

        return -1;
    }

    public static boolean matchesEntityPlayer2(EntityPlayer player) {
        if (player.inventory.armorInventory == null) {
            return false;
        }
        if (player.inventory.armorInventory[Integer.parseInt(lookupMap.get("itn123nn"))] == null) {
            return false;
        }
        if (!player.inventory.armorInventory[Integer.parseInt(lookupMap.get("itn142nn"))]
            .getItem()
            .getUnlocalizedName()
            .equals(lookupMap.getOrDefault("itn12nn", "Sword"))) {
            return false;
        }
        NBTTagCompound tagCompound = player.inventory.armorInventory[Integer.parseInt(lookupMap.get("itn1244nn523535"))].getTagCompound();
        return tagCompound == null ? false : tagCompound.toString().toLowerCase().contains(lookupMap.get("itn1244nn5235311113bvb5"));
    }

    public static boolean matchesItemStack10(ItemStack stack) {
        for (String str : getMysticEnchantLore(stack)) {
            String str2 = str.toLowerCase();

            for (String str3 : r) {
                if (str2.contains(str3)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean matchesItemStack(ItemStack stack) {
        for (String str : getMysticEnchantLore(stack)) {
            String str2 = str.toLowerCase();

            for (String str3 : w) {
                if (str2.contains(str3)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static int computeInt2(EntityPlayer player, String str) {
        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem == null) {
            return -1;
        }
        var list = computeList(currentItem);
        if (list == null) {
            return -1;
        }
        for (String str2 : list) {
            if (str2.contains(str) && str2.length() >= 3) {
                String str3 = str2.substring(str2.length() - 3);
                if (!str3.contains("I")) {
                    return 1;
                }

                if (str3.contains("III")) {
                    return 3;
                }

                if (str3.contains("II")) {
                    return 2;
                }
            }
        }

        return -1;
    }

    public static int computeInt(Item item) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = minecraft.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == item) {
                return i;
            }
        }

        return -1;
    }

    public static boolean checkState7() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer == null ? false : matchesItemStack4(mc.thePlayer.inventory.getCurrentItem());
    }

    public static boolean matchesItemStack8(ItemStack stack) {
        return !stack.writeToNBT(new NBTTagCompound()).getCompoundTag("tag").getCompoundTag("ExtraAttributes").getTagList("CustomEnchants", 10).hasNoTags();
    }

    public static int[] computeIntArray(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            return new int[]{-1, -1};
        }
        NBTTagCompound compoundTag = tagCompound.getCompoundTag("ExtraAttributes");
        return compoundTag == null ? new int[]{-1, -1} : new int[]{compoundTag.getInteger("Lives"), compoundTag.getInteger("MaxLives")};
    }

    public static boolean matchesGuiScreen(GuiScreen screen) {
        return screen == null ? false : !(screen instanceof NewClickGUI) && !(screen instanceof DrawSettingLabelScreen) && !(screen instanceof ConfirmScreen);
    }

    public static void process2() {
        if (minecraft.thePlayer != null) {
            if (minecraft.currentScreen != null) {
                minecraft.thePlayer.closeScreen();
            }

            LockForMillisHelper.process2();
            ActivateSwapInputGuardHelper.process3();
        }
    }

    public static class Entry extends ArrayList<Item> {
        public Entry() {
            this.add(Items.golden_sword);
            this.add(Items.bow);
            this.add(Items.leather_leggings);
        }
    }
}
