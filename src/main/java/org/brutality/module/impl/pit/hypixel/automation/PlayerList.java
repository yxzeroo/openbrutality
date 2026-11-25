package org.brutality.module.impl.pit.hypixel.automation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.gui.font.Dialog;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.hud.TrucedHandler;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.module.impl.visual.VisualHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
import org.brutality.util.BEASTHelper;
import org.brutality.util.inventory.MysticItemInfo;
import org.brutality.util.inventory.VenomType;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.TruceManager;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.render.ArrangeHelper;
import org.brutality.utils.render.RenderHelper2;
import org.lwjgl.input.Mouse;

public class PlayerList extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ModeSetting newValueSetting = new ModeSetting("Mode", this, "Old", Arrays.asList("Old", "New"));
    private final NumberSetting tableScaleSetting = new NumberSetting("Table Scale", this, 0.65, 0.4, 1.5, 0.05)
        .computeNumberSetting(() -> this.newValueSetting.matchesString2("New"));
    private final ModeSetting bountySetting = new ModeSetting(
            "Sorting", this, "Distance", Arrays.asList("A-Z", "Distance", "Health", "Absorption", "Relationship", "Bounty")
        )
        .computeModeSetting(() -> this.newValueSetting.matchesString2("New"));
    private final BooleanSetting includeKOSSetting = new BooleanSetting("Include KOS", this, true)
        .computeBooleanSetting(() -> this.newValueSetting.matchesString2("New"));
    private final BooleanSetting includeFriendsSetting = new BooleanSetting("Include Friends", this, true)
        .computeBooleanSetting(() -> this.newValueSetting.matchesString2("New"));
    private final BooleanSetting includeTrucesSetting = new BooleanSetting("Include Truces", this, true)
        .computeBooleanSetting(() -> this.newValueSetting.matchesString2("New"));
    private final BooleanSetting includeNickedSetting = new BooleanSetting("Include Nicked", this, false)
        .computeBooleanSetting(() -> this.newValueSetting.matchesString2("New"));
    private final NumberSetting nickedXSetting = new NumberSetting("Nicked X", this, 400.0, 0.0, 1920.0, 1.0).computeNumberSetting(this::z);
    private final NumberSetting nickedYSetting = new NumberSetting("Nicked Y", this, 400.0, 0.0, 1080.0, 1.0)
        .computeNumberSetting(() -> this.newValueSetting.matchesString2("New") && this.separateListsSetting.isEnabled());
    private final NumberSetting xSetting = new NumberSetting("X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 10.0, 0.0, 1080.0, 1.0);
    private final NumberSetting friendsXSetting = new NumberSetting("Friends X", this, 200.0, 0.0, 1920.0, 1.0);
    private final NumberSetting friendsYSetting = new NumberSetting("Friends Y", this, 10.0, 0.0, 1080.0, 1.0);
    private final NumberSetting enemiesXSetting = new NumberSetting("Enemies X", this, 200.0, 0.0, 1920.0, 1.0);
    private final NumberSetting enemiesYSetting = new NumberSetting("Enemies Y", this, 100.0, 0.0, 1080.0, 1.0);
    private final NumberSetting darksXSetting = new NumberSetting("Darks X", this, 200.0, 0.0, 1920.0, 1.0);
    private final NumberSetting darksYSetting = new NumberSetting("Darks Y", this, 190.0, 0.0, 1080.0, 1.0);
    private final NumberSetting regularityXSetting = new NumberSetting("Regularity X", this, 200.0, 0.0, 1920.0, 1.0);
    private final NumberSetting regularityYSetting = new NumberSetting("Regularity Y", this, 280.0, 0.0, 1080.0, 1.0);
    private final NumberSetting tntxSetting = new NumberSetting("TNT X", this, 200.0, 0.0, 1920.0, 1.0);
    private final NumberSetting tntySetting = new NumberSetting("TNT Y", this, 370.0, 0.0, 1080.0, 1.0);
    private final NumberSetting truceXSetting = new NumberSetting("Truce X", this, 400.0, 0.0, 1920.0, 1.0);
    private final NumberSetting bountiesXSetting = new NumberSetting("Bounties X", this, 400.0, 0.0, 1920.0, 1.0);
    private final NumberSetting bountiesYSetting = new NumberSetting("Bounties Y", this, 100.0, 0.0, 1080.0, 1.0);
    private final NumberSetting truceYSetting = new NumberSetting("Truce Y", this, 10.0, 0.0, 1080.0, 1.0);
    private final NumberSetting playerSpacingSetting = new NumberSetting("Player Spacing", this, 12.0, 8.0, 24.0, 1.0);
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 2.0, 0.05);
    private final BooleanSetting showVenomedSetting = new BooleanSetting("Show Venomed", this, true);
    private final BooleanSetting showPinnedSetting = new BooleanSetting("Show Pinned", this, true);
    private final BooleanSetting showLobbyInfoSetting = new BooleanSetting("Show Lobby Info", this, true);
    private final StringSetting lobbyInfoSetting = new StringSetting("Custom Lobby Text", this, "Lobby Info");
    private final ModeSetting afterSetting = new ModeSetting("KOS Mark Position", this, "After", Arrays.asList("Before", "After"));
    private final BooleanSetting separateListsSetting = new BooleanSetting("Separate Lists", this, false);
    private final BooleanSetting autoStackListsSetting = new BooleanSetting("Auto Stack Lists", this, true)
        .computeBooleanSetting(() -> this.separateListsSetting.isEnabled());
    private final BooleanSetting showDarksListSetting = new BooleanSetting("Show Darks List", this, true);
    private final BooleanSetting showRegularityListSetting = new BooleanSetting("Show Regularity List", this, true);
    private final BooleanSetting showEnemiesListSetting = new BooleanSetting("Show Enemies List", this, true);
    private final BooleanSetting showFriendsListSetting = new BooleanSetting("Show Friends List", this, true);
    private final BooleanSetting showNickedListSetting = new BooleanSetting("Show Nicked List", this, true);
    private final BooleanSetting showTNTListSetting = new BooleanSetting("Show TNT List", this, true);
    private final BooleanSetting showBountiesListSetting = new BooleanSetting("Show Bounties List", this, true);
    private final BooleanSetting showTruceListSetting = new BooleanSetting("Show Truce List", this, true);
    private final BooleanSetting useBracketsSetting = new BooleanSetting("Use Brackets", this, true);
    private final ModeSetting curlySetting = new ModeSetting("Bracket Type", this, "Round", Arrays.asList("Round", "Square", "Angular", "Curly"));
    private final ColorSetting bracketColorSetting = new ColorSetting("Bracket Color", this, 0.58F);
    private final BooleanSetting boldBracketsSetting = new BooleanSetting("Bold Brackets", this, false);
    private final BooleanSetting showLocationSetting = new BooleanSetting("Show Location", this, true);
    private final BooleanSetting showDistanceSetting = new BooleanSetting("Show Distance", this, true);
    private final BooleanSetting combineLocationDistanceSetting = new BooleanSetting("Combine Location/Distance", this, true);
    private final BooleanSetting boldLocationSetting = new BooleanSetting("Bold Location", this, false);
    private final ColorSetting locationColorSetting = new ColorSetting("Location Color", this, 0.0F);
    private final BooleanSetting showHealthSetting = new BooleanSetting("Show Health", this, true);
    private final BooleanSetting showKOSSetting = new BooleanSetting("Show KOS", this, true);
    private final BooleanSetting showFriendsSetting = new BooleanSetting("Show Friends", this, true);
    private final BooleanSetting showArmorSetting = new BooleanSetting("Show Armor", this, true);
    private final BooleanSetting showEnchantsSetting = new BooleanSetting("Show Enchants", this, true);
    private final BooleanSetting showAFKSetting = new BooleanSetting("Show AFK", this, true);
    private final BooleanSetting showBedrockSetting = new BooleanSetting("Show Bedrock", this, true);
    private final BooleanSetting boldEnchantsSetting = new BooleanSetting("Bold Enchants", this, false);
    private final BooleanSetting detectStreakingSetSetting = new BooleanSetting("Detect Streaking Set", this, true);
    private final BooleanSetting detectSweatySetSetting = new BooleanSetting("Detect Sweaty Set", this, true);
    private final BooleanSetting detectGoldSetSetting = new BooleanSetting("Detect Gold Set", this, true);
    private final Map<VenomType, BooleanSetting> lookupMap = new LinkedHashMap<>();
    private final KeybindListSetting displaySettingsSetting = new KeybindListSetting(
        "Display Settings",
        this,
        this.showLobbyInfoSetting,
        this.lobbyInfoSetting,
        this.afterSetting,
        this.playerSpacingSetting,
        this.scaleSetting,
        this.showVenomedSetting,
        this.showPinnedSetting
    );
    private final KeybindListSetting listOrganizationSetting = new KeybindListSetting(
        "List Organization",
        this,
        this.separateListsSetting,
        this.autoStackListsSetting,
        this.showDarksListSetting,
        this.showRegularityListSetting,
        this.showEnemiesListSetting,
        this.showFriendsListSetting,
        this.showNickedListSetting,
        this.showTNTListSetting,
        this.showTruceListSetting,
        this.showBountiesListSetting
    );
    private final KeybindListSetting separateListPositionsSetting = new KeybindListSetting(
        "Separate List Positions",
        this,
        this.friendsXSetting,
        this.friendsYSetting,
        this.enemiesXSetting,
        this.enemiesYSetting,
        this.darksXSetting,
        this.darksYSetting,
        this.regularityXSetting,
        this.regularityYSetting,
        this.tntxSetting,
        this.tntySetting,
        this.truceXSetting,
        this.truceYSetting,
        this.bountiesXSetting,
        this.bountiesYSetting
    );
    private final KeybindListSetting styleSettingsSetting = new KeybindListSetting(
        "Style Settings",
        this,
        this.useBracketsSetting,
        this.curlySetting,
        this.bracketColorSetting,
        this.boldBracketsSetting,
        this.boldLocationSetting,
        this.locationColorSetting,
        this.boldEnchantsSetting
    );
    private final KeybindListSetting showSettingsSetting = new KeybindListSetting(
        "Show Settings",
        this,
        this.showLocationSetting,
        this.showDistanceSetting,
        this.combineLocationDistanceSetting,
        this.showHealthSetting,
        this.showKOSSetting,
        this.showFriendsSetting,
        this.showArmorSetting,
        this.showEnchantsSetting,
        this.showAFKSetting,
        this.showBedrockSetting
    );
    private final KeybindListSetting pitEnchantsSetting;
    private final Map<String, PlayerList.Entry3> lookupMap2 = new HashMap<>();
    private final Map<EntityPlayer, Map<VenomType, Integer>> lookupMap3 = new IdentityHashMap<>();
    private final Map<ItemStack, String> lookupMap4 = new IdentityHashMap<>();
    private final Map<EntityPlayer, String> lookupMap5 = new IdentityHashMap<>();
    private final Map<EntityPlayer, Integer> lookupMap6 = new IdentityHashMap<>();
    private final Set<String> itemSet = new HashSet<>();
    private static final long timestamp = 300000000L;
    private List<EntityPlayer> entries = Collections.emptyList();
    private List<EntityPlayer> entries2 = Collections.emptyList();
    private Map<String, List<EntityPlayer>> lookupMap7 = Collections.emptyMap();
    private final List<BooleanSetting> entries3 = new ArrayList<>();
    private boolean[] flagArray;
    private Object object;
    private long timestamp2;
    private boolean flag;
    private int count = Integer.MIN_VALUE;
    private Object object2;
    private int count2 = Integer.MIN_VALUE;
    private static final Set<String> itemSet2 = new HashSet<>(
        Arrays.asList(
            "venom",
            "misery",
            "spite",
            "needless_suffering",
            "grim_reaper",
            "nostalgia",
            "hedge_fund",
            "heartripper",
            "sanguisuge",
            "lycanthropy",
            "mind_assault",
            "golden_handcuffs"
        )
    );
    private static final Pattern pattern = Pattern.compile("§[0-9a-fk-or]");
    private static final Pattern pattern2 = Pattern.compile(".*§[0-9a-ek-o].*");

    private void handleEntityPlayer(EntityPlayer player) {
        PlayerList.Entry3 entry3 = this.lookupMap2.get(player.getName());
        if (entry3 == null) {
            this.lookupMap2.put(player.getName(), new PlayerList.Entry3(player.posX, player.posY, player.posZ));
        } else {
            entry3.matchesDouble(player.posX, player.posY, player.posZ);
        }
    }

    private Map<String, List<EntityPlayer>> computeMap2(List<EntityPlayer> players) {
        if (this.checkState()) {
            return this.computeMap(players);
        }
        HashMap map = new HashMap();
        map.put("friends", new ArrayList());
        map.put("enemies", new ArrayList());
        map.put("darks", new ArrayList());
        map.put("regularity", new ArrayList());
        map.put("tnt", new ArrayList());
        map.put("other", new ArrayList());
        map.put("truces", new ArrayList());

        for (EntityPlayer player : players) {
            if (TruceManager.matchesString(player.getName())) {
                ((List)map.get("truces")).add(player);
            } else {
                Map map2 = this.computeMap(player);
                if (DefaultHelper.matchesString(player.getName())) {
                    ((List)map.get("friends")).add(player);
                } else if (WHelper.matchesString(player.getName())) {
                    ((List)map.get("enemies")).add(player);
                } else if (this.matchesEntityPlayer7(player)) {
                    ((List)map.get("tnt")).add(player);
                } else if (this.matchesMap(map2)) {
                    ((List)map.get("darks")).add(player);
                } else if (map2.containsKey(VenomType.REGULARITY)) {
                    ((List)map.get("regularity")).add(player);
                } else {
                    ((List)map.get("other")).add(player);
                }
            }
        }

        return map;
    }

    private String computeString(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("display", 10)) {
            NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
            boolean flag = false;
            boolean flag2 = false;
            if (compoundTag.hasKey("Name", 8)) {
                String str = compoundTag.getString("Name").toLowerCase();
                if (str.contains("sweaty")) {
                    return "sweaty";
                }

                if (str.contains("moctezuma")) {
                    return "moctezuma";
                }

                if (str.contains("critfunky")) {
                    flag = true;
                }

                if (str.contains("peroxide")) {
                    flag2 = true;
                }
            }

            if (compoundTag.hasKey("Lore", 9)) {
                NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                for (int i = 0; i < tagList.tagCount(); i++) {
                    String str2 = tagList.getStringTagAt(i).toLowerCase();
                    if (str2.contains("sweaty") || str2.contains("gain xp")) {
                        return "sweaty";
                    }

                    if (str2.contains("moctezuma") || str2.contains("gold find")) {
                        return "moctezuma";
                    }

                    if (str2.contains("critfunky")) {
                        flag = true;
                    }

                    if (str2.contains("peroxide")) {
                        flag2 = true;
                    }
                }
            }

            if (flag && flag2) {
                return "streaking";
            }
        }

        return null;
    }

    private boolean matchesMap(Map<VenomType, Integer> map) {
        for (VenomType venomType : map.keySet()) {
            if (itemSet2.contains(venomType.getInternalId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.lookupMap2.clear();
                this.process();
                this.process5();
            }
        }
    }

    private void handleFontRenderer(FontRenderer fontRenderer, int n) {
        ArrayList<PlayerList.Entry> list = new ArrayList<>();
        this.handleList(
            list,
            fontRenderer,
            n,
            "friends",
            this.showFriendsListSetting,
            this.friendsXSetting,
            this.friendsYSetting,
            "Friends",
            5635925,
            this.lookupMap7.get("friends"),
            false
        );
        this.handleList(
            list,
            fontRenderer,
            n,
            "enemies",
            this.showEnemiesListSetting,
            this.enemiesXSetting,
            this.enemiesYSetting,
            "Enemies",
            16733525,
            this.lookupMap7.get("enemies"),
            false
        );
        this.handleList(
            list,
            fontRenderer,
            n,
            "darks",
            this.showDarksListSetting,
            this.darksXSetting,
            this.darksYSetting,
            "Darks",
            11141290,
            this.lookupMap7.get("darks"),
            false
        );
        this.handleList(
            list,
            fontRenderer,
            n,
            "regularity",
            this.showRegularityListSetting,
            this.regularityXSetting,
            this.regularityYSetting,
            "Regularity",
            11141120,
            this.lookupMap7.get("regularity"),
            false
        );
        this.handleList(
            list, fontRenderer, n, "tnt", this.showTNTListSetting, this.tntxSetting, this.tntySetting, "TNT", 16724787, this.lookupMap7.get("tnt"), false
        );
        this.handleList(
            list,
            fontRenderer,
            n,
            "truces",
            this.showTruceListSetting,
            this.truceXSetting,
            this.truceYSetting,
            "Truces",
            5592575,
            this.lookupMap7.get("truces"),
            false
        );
        this.handleList(
            list, fontRenderer, n, "bounties", this.showBountiesListSetting, this.bountiesXSetting, this.bountiesYSetting, "Bounties", 16755200, this.entries2, true
        );
        List list2 = new ArrayList();
        String str = null;

        for (PlayerList.Entry entry : list) {
            if (HudDragHelper.matchesObject(entry.text)) {
                str = entry.text;
            }

            list2.add(
                new ArrangeHelper.Entry(
                    entry.text, (float)entry.numberSetting.computeDouble(), (float)entry.numberSetting2.computeDouble(), entry.count, entry.count2
                )
            );
        }

        Map<Object, Float> map = (Map<Object, Float>)(Map<?, ?>)(this.autoStackListsSetting.isEnabled() ? ArrangeHelper.arrange(list2, str) : Collections.emptyMap());

        for (PlayerList.Entry entry2 : ((Iterable<PlayerList.Entry>)(Object)(list))) {
            float f = (float)entry2.numberSetting.computeDouble();
            float orDefault = map.getOrDefault(entry2.text, (float)entry2.numberSetting2.computeDouble());
            this.handleFloat(f, orDefault, entry2.count, entry2.count2, entry2.text, entry2.numberSetting, entry2.numberSetting2);
            if (HudDragHelper.matchesObject(entry2.text)) {
                f = (float)entry2.numberSetting.computeDouble();
                orDefault = (float)entry2.numberSetting2.computeDouble();
            }

            for (PlayerList.Entry2 entry22 : entry2.entries) {
                this.handleFontRenderer(fontRenderer, entry22.text, f, orDefault, entry22.count);
                orDefault += n;
            }
        }
    }

    private boolean matchesEntityPlayer7(EntityPlayer player) {
        ItemStack stack = player.inventory.armorInventory[1];
        if (stack == null) {
            return false;
        }
        if (stack.getDisplayName() != null && stack.getDisplayName().toLowerCase().contains("tnt")) {
            return true;
        }
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("display", 10)) {
            NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
            if (compoundTag.hasKey("Lore", 9)) {
                NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                for (int i = 0; i < tagList.tagCount(); i++) {
                    if (tagList.getStringTagAt(i).toLowerCase().contains("tnt")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        String text = player.getDisplayName().getFormattedText();
        String name = player.getName();
        if (!text.equals(name) && !text.equals("§f" + name) && !pattern.matcher(text).replaceAll("").trim().equals(name)) {
            return true;
        }
        String str = text.replace("§f", "").replace("§r", "");
        return pattern2.matcher(str).matches();
    }

    private void handleFontRenderer(FontRenderer fontRenderer, String str, float f, float f2, int n) {
        float f3 = this.computeFloat(fontRenderer);
        int scaleFactor = InvalidateHelper.computeScaledResolution(this.mc).getScaleFactor();
        float f4 = ArrangeHelper.computeFloat(f, scaleFactor);
        float f5 = ArrangeHelper.computeFloat(f2, scaleFactor);
        GlStateManager.pushMatrix();

        try {
            GlStateManager.translate(f4, f5, 0.0F);
            GlStateManager.scale(f3, f3, 1.0F);
            if (fontRenderer instanceof CustomFontRenderer && !(fontRenderer instanceof Dialog)) {
                ((CustomFontRenderer)fontRenderer).computeInt6(str, 0.0F, 0.0F, n);
            } else {
                fontRenderer.drawStringWithShadow(str, 0.0F, 0.0F, n);
            }
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private void process4() {
        RenderHelper2 renderHelper2 = new RenderHelper2(this, 18.0F, (float)this.tableScaleSetting.computeDouble());
        ArrayList<TrucedHandler.Entry2> entry2s = new ArrayList<>();
        if (!this.separateListsSetting.isEnabled()) {
            this.handleList(entry2s, renderHelper2, "main", null, this.xSetting, this.ySetting, "Player List", -1, this.entries);
        } else {
            this.handleList(
                entry2s,
                renderHelper2,
                "friends",
                this.showFriendsListSetting,
                this.friendsXSetting,
                this.friendsYSetting,
                "Friends",
                -16733696,
                this.lookupMap7.get("friends")
            );
            this.handleList(
                entry2s,
                renderHelper2,
                "enemies",
                this.showEnemiesListSetting,
                this.enemiesXSetting,
                this.enemiesYSetting,
                "Enemies",
                -43691,
                this.lookupMap7.get("enemies")
            );
            this.handleList(
                entry2s, renderHelper2, "truces", this.showTruceListSetting, this.truceXSetting, this.truceYSetting, "Truces", -11184641, this.lookupMap7.get("truces")
            );
            this.handleList(
                entry2s, renderHelper2, "darks", this.showDarksListSetting, this.darksXSetting, this.darksYSetting, "Darks", -5635926, this.lookupMap7.get("darks")
            );
            this.handleList(
                entry2s,
                renderHelper2,
                "regularity",
                this.showRegularityListSetting,
                this.regularityXSetting,
                this.regularityYSetting,
                "Regularity",
                -5636096,
                this.lookupMap7.get("regularity")
            );
            this.handleList(entry2s, renderHelper2, "tnt", this.showTNTListSetting, this.tntxSetting, this.tntySetting, "TNT", -43691, this.lookupMap7.get("tnt"));
            this.handleList(
                entry2s, renderHelper2, "nicked", this.showNickedListSetting, this.nickedXSetting, this.nickedYSetting, "Nicked", -22016, this.lookupMap7.get("nicked")
            );
            this.handleList(
                entry2s, renderHelper2, "bounties", this.showBountiesListSetting, this.bountiesXSetting, this.bountiesYSetting, "Bounties", -22016, this.entries2
            );
        }

        List list = new ArrayList();
        String str = null;

        for (TrucedHandler.Entry2 entry2 : entry2s) {
            if (HudDragHelper.matchesObject(entry2.text)) {
                str = entry2.text;
            }

            list.add(
                new ArrangeHelper.Entry(
                    entry2.text, (float)entry2.numberSetting.computeDouble(), (float)entry2.numberSetting2.computeDouble(), entry2.entry.count, entry2.entry.count2
                )
            );
        }

        Map<Object, Float> map = (Map<Object, Float>)(Map<?, ?>)(this.separateListsSetting.isEnabled() && this.autoStackListsSetting.isEnabled()
            ? ArrangeHelper.arrange(list, str)
            : Collections.emptyMap());

        for (TrucedHandler.Entry2 entry22 : ((Iterable<TrucedHandler.Entry2>)(Object)(entry2s))) {
            float f = (float)entry22.numberSetting.computeDouble();
            float orDefault = map.getOrDefault(entry22.text, (float)entry22.numberSetting2.computeDouble());
            this.handleFloat(
                f, orDefault, entry22.entry.count, entry22.entry.count2, entry22.text.equals("main") ? this : entry22.text, entry22.numberSetting, entry22.numberSetting2
            );
            if (HudDragHelper.matchesObject(entry22.text.equals("main") ? this : entry22.text)) {
                f = (float)entry22.numberSetting.computeDouble();
                orDefault = (float)entry22.numberSetting2.computeDouble();
            }

            TrucedHandler.handleRenderHelper2(renderHelper2, entry22, f, orDefault);
        }
    }

    private TrucedHandler.Entry3 computeEntry3(EntityPlayer player) {
        float dist = this.mc.thePlayer.getDistanceToEntity(player);
        String str = TrucedHandler.computeString(this.computeString2(player));
        String str2 = TrucedHandler.computeString(
            TruceManager.matchesString(player.getName()), WHelper.matchesString(player.getName()), DefaultHelper.matchesString(player.getName())
        );
        List list = new ArrayList();

        for (Map.Entry entry : this.computeMap(player).entrySet()) {
            list.add(((VenomType)entry.getKey()).getColorCode() + ((VenomType)entry.getKey()).getDisplayName().toLowerCase(Locale.ROOT));
        }

        if (this.matchesEntityPlayer7(player)) {
            list.add("§cTNT");
        }

        String str3 = String.join("§7/", list);
        if (str3.isEmpty()) {
            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack != null) {
                    String str4 = this.computeString2(stack);
                    if ("sweaty".equals(str4)) {
                        str3 = "§bXP Set";
                        break;
                    }

                    if ("moctezuma".equals(str4)) {
                        str3 = "§6Gold Set";
                        break;
                    }

                    if ("streaking".equals(str4)) {
                        str3 = "§cStreaking Set";
                        break;
                    }
                }
            }
        }

        if (str3.isEmpty()) {
            str3 = this.computeString3(player);
        }

        if (str3.isEmpty()) {
            str3 = "§7None";
        }

        int n = TrucedHandler.computeInt(
            TruceManager.matchesString(player.getName()), WHelper.matchesString(player.getName()), DefaultHelper.matchesString(player.getName())
        );
        ResourceLocation location = this.mc.getNetHandler() != null && player instanceof AbstractClientPlayer
            ? ((AbstractClientPlayer)player).getLocationSkin()
            : DefaultPlayerSkin.getDefaultSkin(player.getUniqueID());
        return new TrucedHandler.Entry3(
            new String[]{
                player.getName(),
                TrucedHandler.computeString(player.getHealth(), player.getAbsorptionAmount(), player.getMaxHealth()),
                str,
                str2,
                str3,
                String.format(Locale.ROOT, "%.1fm", dist)
            },
            new int[]{n, -1, -1, -1, -1, TrucedHandler.computeInt(dist)},
            location
        );
    }

    private static void handleList(List<EntityPlayer> players, EntityPlayer player, Map<EntityPlayer, Integer> map) {
        int n;
        for (n = 0; n < players.size(); n++) {
            EntityPlayer player2 = (EntityPlayer)players.get(n);
            int n2 = Integer.compare((Integer)map.get(player2), (Integer)map.get(player));
            if (n2 < 0 || n2 == 0 && player.getName().compareToIgnoreCase(player2.getName()) < 0) {
                break;
            }
        }

        if (n < 10) {
            players.add(n, player);
            if (players.size() > 10) {
                players.remove(10);
            }
        }
    }

    private float computeFloat(FontRenderer fontRenderer) {
        return fontRenderer instanceof CustomFontRenderer && !(fontRenderer instanceof Dialog) ? 1.0F : (float)this.scaleSetting.computeDouble();
    }

    private String buildEnchantInfo(EntityPlayer player) {
        List<String> list = new ArrayList<>();
        boolean flag = false;
        String str = null;

        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor)stack.getItem();
                if (item.getArmorMaterial() == ArmorMaterial.LEATHER && item.armorType == 2) {
                    flag = true;
                    str = this.computeString2(stack);
                }
            }
        }

        if (flag && str != null) {
            if (str.equals("sweaty") && this.detectSweatySetSetting.isEnabled()) {
                return EnumChatFormatting.AQUA + "XP Set";
            }

            if (str.equals("moctezuma") && this.detectGoldSetSetting.isEnabled()) {
                return EnumChatFormatting.GOLD + "Gold Set";
            }

            if (str.equals("streaking") && this.detectStreakingSetSetting.isEnabled()) {
                return EnumChatFormatting.RED + "Streaking Set";
            }
        }

        for (Map.Entry entry : this.computeMap(player).entrySet()) {
            BooleanSetting boolSetting = this.lookupMap.get(entry.getKey());
            if (boolSetting != null && boolSetting.isEnabled()) {
                String str2 = ((VenomType)entry.getKey()).getColorCode() + ((VenomType)entry.getKey()).getDisplayName();
                if (!list.contains(str2)) {
                    list.add(str2);
                }
            }
        }

        if (!list.isEmpty()) {
            String str3 = this.boldEnchantsSetting.isEnabled() ? EnumChatFormatting.BOLD.toString() : "";
            return list.stream()
                .map(str4 -> str3 + str4 + EnumChatFormatting.RESET)
                .reduce((str5, str6) -> str5 + EnumChatFormatting.GRAY + "/" + str6)
                .orElse("");
        }
        return "";
    }

    private boolean matchesEntityPlayer4(EntityPlayer player) {
        return player.getUniqueID() != null && player.getUniqueID().version() == 1;
    }

    private boolean matchesLong(long l) {
        if (!this.matchesLong(l, this.mc.theWorld, this.checkState2())) {
            return false;
        }
        this.lookupMap6.clear();
        this.entries = this.computeList();
        this.entries2 = this.showBountiesListSetting.isEnabled() ? this.computeList2() : Collections.emptyList();
        if (this.checkState() && !this.entries2.isEmpty()) {
            this.entries2 = new ArrayList<>(this.entries2);
            this.sortNewPlayers(this.entries2);
        }

        this.lookupMap7 = this.separateListsSetting.isEnabled() ? this.computeMap2(this.entries) : Collections.emptyMap();
        this.timestamp2 = l;
        this.object = this.mc.theWorld;
        this.flag = true;
        return true;
    }

    private boolean checkState() {
        return this.newValueSetting != null && this.newValueSetting.matchesString2("New");
    }

    private void process() {
        this.lookupMap3.clear();
        this.lookupMap4.clear();
        this.lookupMap5.clear();
        this.lookupMap6.clear();
        this.object2 = null;
        this.count2 = Integer.MIN_VALUE;
    }

    private boolean matchesEntityPlayer8(EntityPlayer player) {
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor)stack.getItem();
                if (item.getArmorMaterial() == ArmorMaterial.LEATHER && item.armorType == 2) {
                    String str = this.computeString2(stack);
                    if (str != null) {
                        switch (str) {
                            case "sweaty":
                                return this.detectSweatySetSetting.isEnabled();
                            case "moctezuma":
                                return this.detectGoldSetSetting.isEnabled();
                            case "streaking":
                                return this.detectStreakingSetSetting.isEnabled();
                        }
                    }
                }
            }
        }

        return false;
    }

    private void handleList(
        List<PlayerList.Entry> list,
        FontRenderer fontRenderer,
        int n,
        String str,
        BooleanSetting boolSetting,
        NumberSetting numberSetting,
        NumberSetting numberSetting2,
        String str2,
        int n2,
        List<EntityPlayer> players,
        boolean flag
    ) {
        if (boolSetting.isEnabled() && (!players.isEmpty() || EditableModulesHelper.isActive())) {
            ArrayList<PlayerList.Entry2> entry2s = new ArrayList<>();
            entry2s.add(new PlayerList.Entry2(str2 + ": (" + players.size() + ")", n2));

            for (EntityPlayer player : players) {
                entry2s.add(this.computeEntry2(player, flag));
            }

            int n3 = 1;

            for (PlayerList.Entry2 entry2 : entry2s) {
                n3 = Math.max(n3, (int)Math.ceil(fontRenderer.getStringWidth(entry2.text) * this.computeFloat(fontRenderer)) + 4);
            }

            int n4 = (entry2s.size() - 1) * n + (int)Math.ceil(fontRenderer.FONT_HEIGHT * this.computeFloat(fontRenderer)) + 2;
            list.add(new PlayerList.Entry(str, numberSetting, numberSetting2, entry2s, n3, n4));
        }
    }

    private boolean matchesEntityPlayer2(EntityPlayer player) {
        if ((!this.showFriendsSetting.isEnabled() || !DefaultHelper.matchesString(player.getName()))
            && (!this.showKOSSetting.isEnabled() || !WHelper.matchesString(player.getName()))) {
            return this.showBedrockSetting.isEnabled() && this.matchesEntityPlayer6(player)
                ? true
                : this.showEnchantsSetting.isEnabled() && this.matchesEntityPlayer5(player);
        }
        return true;
    }

    private void handleList(
        List<TrucedHandler.Entry2> entry2s,
        RenderHelper2 renderHelper2,
        String str,
        BooleanSetting boolSetting,
        NumberSetting numberSetting,
        NumberSetting numberSetting2,
        String str2,
        int n,
        List<EntityPlayer> players
    ) {
        if (boolSetting == null || boolSetting.isEnabled()) {
            if (players == null) {
                players = Collections.emptyList();
            }

            if (!players.isEmpty() || str.equals("main") || EditableModulesHelper.isActive()) {
                List list = new ArrayList();

                for (EntityPlayer player : players) {
                    list.add(this.computeEntry3(player));
                }

                int n2 = InvalidateHelper.computeScaledResolution(this.mc).getScaledWidth() - 8;
                entry2s.add(
                    new TrucedHandler.Entry2(
                        str,
                        str2,
                        n,
                        numberSetting,
                        numberSetting2,
                        list,
                        TrucedHandler.computeEntry(renderHelper2, list, Math.max(200, n2), (float)this.tableScaleSetting.computeDouble())
                    )
                );
            }
        }
    }

    private float computeFloat(FontRenderer fontRenderer, float f, float f2, int n, EntityPlayer player, boolean flag) {
        PlayerList.Entry2 entry2 = this.computeEntry2(player, flag);
        this.handleFontRenderer(fontRenderer, entry2.text, f, f2, entry2.count);
        return f2 + n;
    }

    private String computeString(String str, String str2) {
        if (!this.useBracketsSetting.isEnabled()) {
            return str;
        }
        String str3 = str2.isEmpty() ? this.computeString(this.bracketColorSetting.computeInt()) : str2;
        String str4 = this.boldBracketsSetting.isEnabled() ? EnumChatFormatting.BOLD.toString() : "";
        return str3 + str4 + this.computeString2() + str + str3 + str4 + this.computeString() + EnumChatFormatting.RESET;
    }

    private int computeInt(float f) {
        if (f <= 20.0F) {
            return 16733525;
        }
        return f <= 40.0F ? 16777045 : 5635925;
    }

    private boolean matchesEntityPlayer6(EntityPlayer player) {
        ItemStack heldItem = player.getHeldItem();
        return heldItem != null && heldItem.getItem() == Item.getItemFromBlock(Blocks.bedrock);
    }

    private void process3() {
        if (this.object2 != this.mc.theWorld || this.count2 != this.mc.thePlayer.ticksExisted) {
            this.lookupMap3.clear();
            this.lookupMap4.clear();
            this.object2 = this.mc.theWorld;
            this.count2 = this.mc.thePlayer.ticksExisted;
        }

        this.lookupMap5.clear();
    }

    private float computeFloat(FontRenderer fontRenderer, float f, float f2, int n, EntityPlayer player) {
        return this.computeFloat(fontRenderer, f, f2, n, player, false);
    }

    private boolean z() {
        return this.newValueSetting.matchesString2("New") && this.separateListsSetting.isEnabled();
    }

    private PlayerList.Entry2 computeEntry2(EntityPlayer player, boolean flag) {
        int n = this.showDistanceSetting.isEnabled() ? this.computeInt(this.mc.thePlayer.getDistanceToEntity(player)) : 16777215;
        String text = player.getDisplayName().getFormattedText();
        StringBuilder sb = new StringBuilder();
        if (flag) {
            sb.append(EnumChatFormatting.GOLD).append(this.lookupMap6.get(player)).append("g ").append(EnumChatFormatting.RESET);
        }

        if (this.afterSetting.getText().equals("Before")
            && (
                this.showFriendsSetting.isEnabled() && DefaultHelper.matchesString(player.getName())
                    || this.showKOSSetting.isEnabled() && WHelper.matchesString(player.getName())
            )) {
            String str = DefaultHelper.matchesString(player.getName()) ? EnumChatFormatting.GREEN + "F" : EnumChatFormatting.DARK_RED + "K";
            sb.append(this.computeString(str, "")).append(" ");
        }

        sb.append(text);
        if (TruceManager.matchesString(player.getName())) {
            sb.append(" ").append(EnumChatFormatting.BLUE).append("[T]").append(EnumChatFormatting.RESET);
        }

        if (this.showBedrockSetting.isEnabled()) {
            ItemStack heldItem = player.getHeldItem();
            if (heldItem != null && heldItem.getItem() == Item.getItemFromBlock(Blocks.bedrock)) {
                sb.append(" ").append(this.computeString(EnumChatFormatting.AQUA + "Bedrock x" + heldItem.stackSize, ""));
            }
        }

        String str2 = VisualHandler.computeString2(player.getName());
        if (!str2.isEmpty()) {
            sb.append(str2);
        }

        if (this.afterSetting.getText().equals("After")
            && (
                this.showFriendsSetting.isEnabled() && DefaultHelper.matchesString(player.getName())
                    || this.showKOSSetting.isEnabled() && WHelper.matchesString(player.getName())
            )) {
            String str3 = DefaultHelper.matchesString(player.getName()) ? EnumChatFormatting.GREEN + "F" : EnumChatFormatting.DARK_RED + "K";
            sb.append(" ").append(this.computeString(str3, ""));
        }

        if (this.showVenomedSetting.isEnabled()) {
            int n2 = VenomedTimer.getInstance() != null ? VenomedTimer.getInstance().computeInt3(player.getName()) : 0;
            if (n2 > 0) {
                String str4 = VenomedTimer.getInstance().computeString(player.getName());
                sb.append(" ").append(EnumChatFormatting.DARK_PURPLE + "[Venomed " + str4 + "s]");
            }
        }

        if (this.showPinnedSetting.isEnabled()) {
            PinTimer instance = PinTimer.getInstance();
            if (instance != null && instance.matchesString(player.getName())) {
                String str5 = instance.matchesString2(player.getName()) ? EnumChatFormatting.GREEN + "[Pinned]" : EnumChatFormatting.RED + "[Pinned]";
                sb.append(" ").append(str5);
            }
        }

        if (this.showHealthSetting.isEnabled()) {
            sb.append(" ").append(this.computeString4(player));
        }

        if (this.showArmorSetting.isEnabled()) {
            String str6 = this.computeString(player);
            if (str6.isEmpty()) {
                String str7 = this.computeString3(player);
                if (!str7.isEmpty()) {
                    sb.append(" ").append(this.computeString(str7, ""));
                }
            }
        }

        if (this.showEnchantsSetting.isEnabled()) {
            String str8 = this.computeString(player);
            if (!str8.isEmpty()) {
                sb.append(" ").append(this.computeString(str8, ""));
            }
        }

        if (this.combineLocationDistanceSetting.isEnabled() && (this.showLocationSetting.isEnabled() || this.showDistanceSetting.isEnabled())) {
            StringBuilder sb2 = new StringBuilder();
            if (this.showLocationSetting.isEnabled()) {
                String str9 = this.computeString2(player);
                String str10 = this.computeString(this.locationColorSetting.computeInt());
                if (this.boldLocationSetting.isEnabled()) {
                    sb2.append(EnumChatFormatting.BOLD).append(str10).append(str9).append(EnumChatFormatting.RESET);
                } else {
                    sb2.append(str10).append(str9).append(EnumChatFormatting.RESET);
                }
            }

            if (this.showLocationSetting.isEnabled() && this.showDistanceSetting.isEnabled()) {
                sb2.append(EnumChatFormatting.GRAY + " - ");
            }

            if (this.showDistanceSetting.isEnabled()) {
                float dist = this.mc.thePlayer.getDistanceToEntity(player);
                EnumChatFormatting enumChatFormatting = this.computeEnumChatFormatting(dist);
                sb2.append(String.format("%s%.1f", enumChatFormatting, dist));
            }

            if (sb2.length() > 0) {
                sb.append(" ").append(this.computeString(sb2.toString(), ""));
            }
        } else {
            if (this.showLocationSetting.isEnabled()) {
                String str11 = this.computeString2(player);
                var str12 = this.computeString(this.locationColorSetting.computeInt());
                String str13 = this.boldLocationSetting.isEnabled()
                    ? EnumChatFormatting.BOLD + str12 + str11 + EnumChatFormatting.RESET
                    : str12 + str11 + EnumChatFormatting.RESET;
                sb.append(" ").append(this.computeString(str13, ""));
            }

            if (this.showDistanceSetting.isEnabled()) {
                float dist2 = this.mc.thePlayer.getDistanceToEntity(player);
                EnumChatFormatting enumChatFormatting2 = this.computeEnumChatFormatting(dist2);
                String str14 = String.format("%s%.1f", enumChatFormatting2, dist2);
                sb.append(" ").append(this.computeString(str14, ""));
            }
        }

        if (this.showAFKSetting.isEnabled()) {
            String str15 = this.computeString(player.getName());
            if (!str15.isEmpty()) {
                sb.append(" ").append(str15);
            }
        }

        return new PlayerList.Entry2(sb.toString(), n);
    }

    private void handleFontRenderer(FontRenderer fontRenderer, float f, float f2, int n, List<EntityPlayer> players, boolean flag) {
        if (!players.isEmpty()) {
            if (flag) {
                this.handleFloat(
                    f,
                    f2,
                    Math.max(1, Math.round(300.0F * (float)this.scaleSetting.computeDouble())),
                    (players.size() + 1) * n + 4,
                    "bounties",
                    this.bountiesXSetting,
                    this.bountiesYSetting
                );
            }

            this.handleFontRenderer(fontRenderer, EnumChatFormatting.GOLD + "Bounties: (" + players.size() + ")", f, f2, 16755200);
            float f3 = f2 + n;

            for (EntityPlayer player : players) {
                f3 = this.computeFloat(fontRenderer, f, f3, n, player, true);
            }
        }
    }

    private String computeString3(EntityPlayer player) {
        int n = 0;
        int n2 = 0;
        boolean flag = false;

        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor)stack.getItem();
                switch (item.getArmorMaterial()) {
                    case DIAMOND:
                        n++;
                        break;
                    case IRON:
                        n2++;
                        break;
                    case CHAIN:
                        flag = true;
                }
            }
        }

        if (n >= 1) {
            return EnumChatFormatting.AQUA + "Shop";
        }
        if (n2 >= 3) {
            return EnumChatFormatting.WHITE + "Iron";
        }
        return flag ? EnumChatFormatting.GRAY + "Chain" : "";
    }

    public PlayerList() {
        super("Player List", 0, Category.PIT);
        this.process18();

        for (VenomType venomType : VenomType.values()) {
            this.lookupMap.put(venomType, new BooleanSetting(venomType.getDisplayName(), this, venomType.isFeatured()));
        }

        Collections.addAll(
            this.entries3,
            this.separateListsSetting,
            this.showBountiesListSetting,
            this.showFriendsSetting,
            this.showKOSSetting,
            this.showEnchantsSetting,
            this.showBedrockSetting,
            this.detectStreakingSetSetting,
            this.detectSweatySetSetting,
            this.detectGoldSetSetting
        );
        Collections.addAll(this.entries3, this.includeKOSSetting, this.includeFriendsSetting, this.includeTrucesSetting, this.includeNickedSetting);
        this.entries3.addAll(this.lookupMap.values());
        List<Setting> settings = new ArrayList<>();
        settings.add(this.detectStreakingSetSetting);
        settings.add(this.detectSweatySetSetting);
        settings.add(this.detectGoldSetSetting);
        settings.addAll(this.lookupMap.values());
        this.pitEnchantsSetting = new KeybindListSetting("Pit Enchants", this, settings.toArray(new Setting[0]));
        this.handleSettingArray(
            new Setting[]{
                this.newValueSetting,
                this.xSetting,
                this.ySetting,
                this.tableScaleSetting,
                this.bountySetting,
                this.includeKOSSetting,
                this.includeFriendsSetting,
                this.includeTrucesSetting,
                this.includeNickedSetting,
                this.displaySettingsSetting,
                this.listOrganizationSetting,
                this.separateListPositionsSetting,
                this.styleSettingsSetting,
                this.showSettingsSetting,
                this.pitEnchantsSetting,
                this.nickedXSetting,
                this.nickedYSetting
            }
        );

        for (Setting setting : new Setting[]{this.displaySettingsSetting, this.styleSettingsSetting, this.showSettingsSetting, this.pitEnchantsSetting}) {
            setting.setConditionalVisibility(() -> this.newValueSetting.matchesString2("Old"));
        }

        this.separateListPositionsSetting.setConditionalVisibility(() -> this.separateListsSetting.isEnabled());
        this.newValueSetting.setCallback(() -> {
            this.process5();
            this.process();
        });
        this.bountySetting.setCallback(this::process5);
    }

    private void handleFloat(float f, float f2, int n, int n2, Object obj, NumberSetting numberSetting, NumberSetting numberSetting2) {
        if (EditableModulesHelper.isActive()) {
            int[] ints = HudDragHelper.computeIntArray();
            int n3 = ints[0];
            int n4 = ints[1];
            if (Mouse.isButtonDown(0)) {
                if (!HudDragHelper.matchesObject(obj) && HudDragHelper.matchesInt(n3, n4, f, f2, n, n2)) {
                    HudDragHelper.matchesObject(obj, n3, n4, (int)f, (int)f2);
                }
            } else {
                HudDragHelper.handleObject(obj);
            }

            if (HudDragHelper.matchesObject(obj)) {
                int[] ints2 = HudDragHelper.computeIntArray(obj);
                if (ints2 != null) {
                    numberSetting.handleDouble2(ints2[0]);
                    numberSetting2.handleDouble2(ints2[1]);
                }
            }
        } else {
            HudDragHelper.handleObject(obj);
        }
    }

    @Override
    public void onRender() {
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            this.process3();
            if (this.checkState()) {
                this.matchesLong(System.nanoTime());
                this.process4();
            } else if (computeFontRenderer(PlayerList.class) != null) {
                FontRenderer fontRenderer = this.computeFontRenderer();
                float f = (float)this.scaleSetting.computeDouble();
                boolean flag = this.matchesLong(System.nanoTime());
                List entries = this.entries;
                List list = this.entries2;
                int n = Math.max(
                    (int)Math.ceil(fontRenderer.FONT_HEIGHT * this.computeFloat(fontRenderer)) + 2, (int)Math.ceil(this.playerSpacingSetting.computeDouble() * f)
                );
                if (flag || this.count != this.mc.thePlayer.ticksExisted) {
                    this.handleList(entries, list);
                    this.count = this.mc.thePlayer.ticksExisted;
                }

                if (this.separateListsSetting.isEnabled()) {
                    this.handleFontRenderer(fontRenderer, n);
                } else {
                    float f2 = (float)this.xSetting.computeDouble();
                    float f3 = (float)this.ySetting.computeDouble();
                    short s = 300;
                    int n2 = entries.size() + (list.isEmpty() ? 0 : list.size() + 1);
                    int n3 = (n2 + (this.showLobbyInfoSetting.isEnabled() ? 2 : 1)) * n + 4;
                    this.handleFloat(f2, f3, Math.max(1, Math.round(s * f)), n3, this, this.xSetting, this.ySetting);
                    if (this.showLobbyInfoSetting.isEnabled()) {
                        String val = this.lobbyInfoSetting.getValue();
                        this.handleFontRenderer(fontRenderer, val, f2, f3, 16777215);
                        f3 += n;
                    }

                    f3 = this.computeFloat(fontRenderer, f2, f3, n, entries);
                    this.handleFontRenderer(fontRenderer, f2, f3, n, list, false);
                }
            }
        }
    }

    private float computeFloat(FontRenderer fontRenderer, float f, float f2, int n, List<EntityPlayer> players) {
        for (EntityPlayer player : players) {
            f2 = this.computeFloat(fontRenderer, f, f2, n, player);
        }

        return f2;
    }

    private boolean matchesEntityPlayer5(EntityPlayer player) {
        if (this.matchesEntityPlayer8(player)) {
            return true;
        }
        for (Map.Entry entry : this.computeMap(player).entrySet()) {
            BooleanSetting boolSetting = this.lookupMap.get(entry.getKey());
            if (boolSetting != null && boolSetting.isEnabled()) {
                return true;
            }
        }

        return false;
    }

    private String computeString2(ItemStack stack) {
        if (this.lookupMap4.containsKey(stack)) {
            return this.lookupMap4.get(stack);
        }
        String str = this.computeString(stack);
        this.lookupMap4.put(stack, str);
        return str;
    }

    private FontRenderer computeFontRenderer() {
        return this.computeFontRenderer(18.0F * (float)this.scaleSetting.computeDouble());
    }

    private void handleList(List<EntityPlayer> players, List<EntityPlayer> players2) {
        this.itemSet.clear();

        for (EntityPlayer player : players) {
            this.itemSet.add(player.getName());
            this.handleEntityPlayer(player);
        }

        for (EntityPlayer player2 : players2) {
            if (this.itemSet.add(player2.getName())) {
                this.handleEntityPlayer(player2);
            }
        }

        this.lookupMap2.keySet().retainAll(this.itemSet);
    }

    private void process5() {
        this.entries = Collections.emptyList();
        this.entries2 = Collections.emptyList();
        this.lookupMap7 = Collections.emptyMap();
        this.lookupMap6.clear();
        this.object = null;
        this.flag = false;
        this.flagArray = null;
        this.count = Integer.MIN_VALUE;
    }

    private boolean matchesLong(long l, Object obj, boolean flag) {
        return !this.flag || this.object != obj || flag || l - this.timestamp2 >= 300000000L;
    }

    private String computeString(String str) {
        PlayerList.Entry3 entry3 = this.lookupMap2.get(str);
        if (entry3 != null && !entry3.flag) {
            long l = entry3.computeLong() / 1000L;
            if (l <= 0L) {
                return "";
            }
            EnumChatFormatting enumChatFormatting;
            if (l < 10L) {
                enumChatFormatting = EnumChatFormatting.DARK_GREEN;
            } else if (l < 20L) {
                enumChatFormatting = EnumChatFormatting.GOLD;
            } else {
                enumChatFormatting = EnumChatFormatting.DARK_RED;
            }

            EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
            EnumChatFormatting GRAY2 = EnumChatFormatting.GRAY;
            return this.computeString(EnumChatFormatting.GREEN + "AFK " + GRAY2 + "- " + enumChatFormatting + l + GRAY + "s", "");
        }
        return "";
    }

    private EnumChatFormatting computeEnumChatFormatting(float f) {
        if (f <= 20.0F) {
            return EnumChatFormatting.RED;
        }
        return f <= 40.0F ? EnumChatFormatting.YELLOW : EnumChatFormatting.GREEN;
    }

    private void sortNewPlayers(List<EntityPlayer> players) {
        String text = this.bountySetting.getText();

        players.sort(
            ((Comparator<EntityPlayer>)(switch (text) {
                    case "A-Z" -> Comparator.comparing(EntityPlayer::getName, String.CASE_INSENSITIVE_ORDER);
                    case "Health" -> Comparator.comparingDouble(EntityLivingBase::getHealth);
                    case "Absorption" -> Comparator.comparingDouble(EntityPlayer::getAbsorptionAmount).reversed();
                    case "Relationship" -> Comparator.<EntityPlayer>comparingInt(
                        player -> WHelper.matchesString(player.getName())
                            ? 0
                            : (TruceManager.matchesString(player.getName()) ? 1 : (DefaultHelper.matchesString(player.getName()) ? 2 : 3))
                    );
                    case "Bounty" -> Comparator.<EntityPlayer>comparingInt(player2 -> this.lookupMap6.computeIfAbsent(player2, BEASTHelper::computeInt2))
                        .reversed();
                    default -> Comparator.<EntityPlayer>comparingDouble(player3 -> this.mc.thePlayer.getDistanceSqToEntity(player3));
                }))
                .thenComparing(EntityPlayer::getName, String.CASE_INSENSITIVE_ORDER)
        );
    }

    private String computeString4(EntityPlayer player) {
        float health = player.getHealth();
        float f = health / 2.0F;
        EnumChatFormatting enumChatFormatting;
        if (f < 3.0F) {
            enumChatFormatting = EnumChatFormatting.RED;
        } else if (f < 5.0F) {
            enumChatFormatting = EnumChatFormatting.GOLD;
        } else {
            enumChatFormatting = EnumChatFormatting.GREEN;
        }

        return String.format("%s%.2f%sâ¤", enumChatFormatting, f, EnumChatFormatting.RED);
    }

    private Map<VenomType, Integer> computeMap(EntityPlayer player) {
        var map = this.lookupMap3.get(player);
        if (map != null) {
            return map;
        }
        LinkedHashMap map2 = new LinkedHashMap();

        for (ItemStack stack : player.inventory.armorInventory) {
            this.handleItemStack(stack, map2);
        }

        this.handleItemStack(player.getHeldItem(), map2);
        this.lookupMap3.put(player, map2);
        return map2;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.lookupMap2.clear();
        this.process();
        this.process5();
    }

    private Map<String, List<EntityPlayer>> computeMap(List<EntityPlayer> players) {
        LinkedHashMap map = new LinkedHashMap();

        for (String str : new String[]{"friends", "enemies", "truces", "darks", "regularity", "tnt", "nicked", "other"}) {
            map.put(str, new ArrayList());
        }

        for (EntityPlayer player : players) {
            var map2 = this.computeMap(player);
            String str2;
            if (this.showTNTListSetting.isEnabled() && this.matchesEntityPlayer7(player)) {
                str2 = "tnt";
            } else if (this.showDarksListSetting.isEnabled() && this.matchesMap(map2)) {
                str2 = "darks";
            } else if (this.showRegularityListSetting.isEnabled() && map2.containsKey(VenomType.REGULARITY)) {
                str2 = "regularity";
            } else if (TruceManager.matchesString(player.getName())) {
                str2 = "truces";
            } else if (DefaultHelper.matchesString(player.getName())) {
                str2 = "friends";
            } else if (WHelper.matchesString(player.getName())) {
                str2 = "enemies";
            } else {
                str2 = "nicked";
            }

            ((List)map.get(str2)).add(player);
        }

        return map;
    }

    private void handleItemStack(ItemStack stack, Map<VenomType, Integer> map) {
        if (stack != null
            && stack.getTagCompound() != null
            && !stack.getTagCompound().getCompoundTag("ExtraAttributes").getTagList("CustomEnchants", 10).hasNoTags()) {
            MysticItemInfo mysticItemInfo = new MysticItemInfo(stack);

            for (Map.Entry entry : mysticItemInfo.lookupMap2.entrySet()) {
                Integer integer = (Integer)map.get(entry.getKey());
                if (integer == null || (Integer)entry.getValue() > integer) {
                    map.put((VenomType)entry.getKey(), (Integer)entry.getValue());
                }
            }
        }
    }

    private List<EntityPlayer> computeList2() {
        List list = new ArrayList(10);
        HashSet set = new HashSet();

        for (EntityPlayer player : EntityCache.getEntries()) {
            if ((!this.checkState() || this.matchesEntityPlayer3(player))
                && player != this.mc.thePlayer
                && !player.isDead
                && player.isEntityAlive()
                && set.add(player.getName())
                && this.matchesEntityPlayer(player)) {
                int n = BEASTHelper.computeInt2(player);
                if (n > 0) {
                    this.lookupMap6.put(player, n);
                    handleList(list, player, this.lookupMap6);
                }
            }
        }

        return list;
    }

    private String computeString(EntityPlayer player) {
        String str = this.lookupMap5.get(player);
        if (str != null) {
            return str;
        }
        String str2 = this.buildEnchantInfo(player);
        this.lookupMap5.put(player, str2);
        return str2;
    }

    private String computeString() {
        String text = this.curlySetting.getText();
        switch (text) {
            case "Round":
                return ")";
            case "Square":
                return "]";
            case "Angular":
                return ">";
            case "Curly":
                return "}";
            default:
                return ")";
        }
    }

    private boolean checkState2() {
        boolean flag = this.flagArray == null || this.flagArray.length != this.entries3.size();
        if (flag) {
            this.flagArray = new boolean[this.entries3.size()];
        }

        for (int i = 0; i < this.entries3.size(); i++) {
            boolean enabled = this.entries3.get(i).isEnabled();
            if (this.flagArray[i] != enabled) {
                flag = true;
            }

            this.flagArray[i] = enabled;
        }

        return flag;
    }

    private String computeString2() {
        String text = this.curlySetting.getText();
        switch (text) {
            case "Round":
                return "(";
            case "Square":
                return "[";
            case "Angular":
                return "<";
            case "Curly":
                return "{";
            default:
                return "(";
        }
    }

    private List<EntityPlayer> computeList() {
        List list = new ArrayList();
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            HashSet set = new HashSet();

            for (EntityPlayer player : EntityCache.getEntries()) {
                if (player != this.mc.thePlayer && !player.isDead && player.isEntityAlive() && set.add(player.getName())) {
                    if (this.checkState()) {
                        if (this.matchesEntityPlayer3(player)) {
                            list.add(player);
                        }
                    } else if (this.matchesEntityPlayer(player)) {
                        if (!TruceManager.matchesString(player.getName())
                            && (!this.showFriendsSetting.isEnabled() || !DefaultHelper.matchesString(player.getName()))
                            && (!this.showKOSSetting.isEnabled() || !WHelper.matchesString(player.getName()))) {
                            if (this.matchesEntityPlayer2(player)) {
                                list.add(player);
                            }
                        } else {
                            list.add(player);
                        }
                    }
                }
            }

            if (this.checkState()) {
                this.sortNewPlayers(list);
            }

            return list;
        }
        return list;
    }

    private String computeString(int n) {
        int n2 = (0xFF | ~(n >> 16)) - ~(n >> 16);
        int n3 = (0xFF | ~(n >> 8)) - ~(n >> 8);
        int n4 = n & 0xFF;
        if (n2 > 200 && n3 < 100 && n4 < 100) {
            return EnumChatFormatting.RED.toString();
        }
        if (n2 < 100 && n3 > 200 && n4 < 100) {
            return EnumChatFormatting.GREEN.toString();
        }
        if (n2 < 100 && n3 < 100 && n4 > 200) {
            return EnumChatFormatting.BLUE.toString();
        }
        if (n2 > 200 && n3 > 200 && n4 < 100) {
            return EnumChatFormatting.YELLOW.toString();
        }
        if (n2 > 150 && n3 < 150 && n4 > 150) {
            return EnumChatFormatting.LIGHT_PURPLE.toString();
        }
        if (n2 < 150 && n3 > 150 && n4 > 150) {
            return EnumChatFormatting.AQUA.toString();
        }
        return n2 > 150 && n3 > 150 && n4 > 150 ? EnumChatFormatting.WHITE.toString() : EnumChatFormatting.GRAY.toString();
    }

    private boolean matchesEntityPlayer3(EntityPlayer player) {
        String name = player.getName();
        return this.includeKOSSetting.isEnabled() && WHelper.matchesString(name)
            || this.includeFriendsSetting.isEnabled() && DefaultHelper.matchesString(name)
            || this.includeTrucesSetting.isEnabled() && TruceManager.matchesString(name)
            || this.includeNickedSetting.isEnabled() && this.matchesEntityPlayer4(player);
    }

    private String computeString2(EntityPlayer player) {
        return FreshHelper.computeString(player, this.mc.theWorld);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap2.clear();
        this.process();
        this.process5();
        HudDragHelper.handleObject(this);
        HudDragHelper.handleObject("friends");
        HudDragHelper.handleObject("enemies");
        HudDragHelper.handleObject("darks");
        HudDragHelper.handleObject("regularity");
        HudDragHelper.handleObject("tnt");
        HudDragHelper.handleObject("truces");
        HudDragHelper.handleObject("bounties");
        HudDragHelper.handleObject("nicked");
    }

    public static final class Entry {
        public final String text;
        public final NumberSetting numberSetting;
        public final NumberSetting numberSetting2;
        public final List<PlayerList.Entry2> entries;
        public final int count;
        public final int count2;

        public Entry(String text, NumberSetting numberSetting, NumberSetting numberSetting2, List<PlayerList.Entry2> entries, int count, int n) {
            this.text = text;
            this.numberSetting = numberSetting;
            this.numberSetting2 = numberSetting2;
            this.entries = entries;
            this.count = count;
            this.count2 = n;
        }
    }

    public static final class Entry2 {
        public final String text;
        public final int count;

        public Entry2(String text, int count) {
            this.text = text;
            this.count = count;
        }
    }

    public static class Entry3 {
        private double amount;
        private double amount2;
        private double amount3;
        private long timestamp;
        public boolean flag;
        private static final long timestamp2 = 1000L;

        public Entry3(double amount, double amount2, double amount3) {
            this.amount = amount;
            this.amount2 = amount2;
            this.amount3 = amount3;
            this.timestamp = System.currentTimeMillis();
            this.flag = true;
        }

        public long computeLong() {
            return System.currentTimeMillis() - this.timestamp;
        }

        public boolean matchesDouble(double amount, double amount2, double amount3) {
            boolean flag = amount != this.amount || amount2 != this.amount2 || amount3 != this.amount3;
            if (flag) {
                this.amount = amount;
                this.amount2 = amount2;
                this.amount3 = amount3;
                this.timestamp = System.currentTimeMillis();
                this.flag = true;
            } else if (this.flag && System.currentTimeMillis() - this.timestamp > timestamp2) {
                this.flag = false;
            }

            return this.flag;
        }
    }
}
