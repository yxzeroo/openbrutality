package org.brutality.module.impl.pit.hypixel.utility;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.opengl.GL11;

public class PitAlerts extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private static final List<String> entries = Arrays.asList(
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
    private static final List<String> entries2 = Arrays.asList(
        "Ace of Spades",
        "Brakes!",
        "Combo: Breaching Charge",
        "Do it like the French",
        "Heigh-Ho",
        "New Deal",
        "Really Toxic",
        "Sybil",
        "Regularity",
        "Think of the People"
    );
    private final BooleanSetting darkEnchantDetectionSetting = new BooleanSetting("Dark Enchant Detection", this, true);
    private final BooleanSetting rageEnchantDetectionSetting = new BooleanSetting("Rage Enchant Detection", this, true);
    private final BooleanSetting gambleDetectionSetting = new BooleanSetting("Gamble Detection", this, true);
    private final BooleanSetting kosDetectionSetting = new BooleanSetting("KOS Detection", this, true);
    private final BooleanSetting kosOnlySetting = new BooleanSetting("KOS Only", this, false);
    private final BooleanSetting donTAlertIfLookingSetting = new BooleanSetting("Don't Alert If Looking", this, true);
    private final BooleanSetting ignoreFriendsSetting = new BooleanSetting("Ignore Friends", this, true);
    private final BooleanSetting detectLobbySetting = new BooleanSetting("Detect Lobby", this, false);
    private final NumberSetting distanceBlocksSetting = new NumberSetting("Distance (Blocks)", this, 10.0, 1.0, 20.0, 1.0);
    private final NumberSetting cooldownSecsSetting = new NumberSetting("Cooldown (Secs)", this, 3.0, 0.5, 10.0, 1.0);
    private final NumberSetting textScaleSetting = new NumberSetting("Text Scale", this, 3.0, 1.0, 6.0, 1.0);
    private final BooleanSetting soundEnabledSetting = new BooleanSetting("Sound Enabled", this, true);
    private final BooleanSetting venomSetting = new BooleanSetting("Venom", this, true);
    private final BooleanSetting miserySetting = new BooleanSetting("Misery", this, true);
    private final BooleanSetting spiteSetting = new BooleanSetting("Spite", this, true);
    private final BooleanSetting needlessSufferingSetting = new BooleanSetting("Needless Suffering", this, true);
    private final BooleanSetting grimReaperSetting = new BooleanSetting("Grim Reaper", this, true);
    private final BooleanSetting nostalgiaSetting = new BooleanSetting("Nostalgia", this, true);
    private final BooleanSetting hedgeFundSetting = new BooleanSetting("Hedge Fund", this, true);
    private final BooleanSetting heartripperSetting = new BooleanSetting("Heartripper", this, true);
    private final BooleanSetting sanguisugeSetting = new BooleanSetting("Sanguisuge", this, true);
    private final BooleanSetting lycanthropySetting = new BooleanSetting("Lycanthropy", this, true);
    private final BooleanSetting mindAssaultSetting = new BooleanSetting("Mind Assault", this, true);
    private final BooleanSetting goldenHandcuffsSetting = new BooleanSetting("Golden Handcuffs", this, true);
    private final BooleanSetting aceOfSpadesSetting = new BooleanSetting("Ace of Spades", this, true);
    private final BooleanSetting brakesSetting = new BooleanSetting("Brakes!", this, true);
    private final BooleanSetting comboBreachingChargeSetting = new BooleanSetting("Combo: Breaching Charge", this, true);
    private final BooleanSetting doItLikeTheFrenchSetting = new BooleanSetting("Do it like the French", this, true);
    private final BooleanSetting heighHoSetting = new BooleanSetting("Heigh-Ho", this, true);
    private final BooleanSetting newDealSetting = new BooleanSetting("New Deal", this, true);
    private final BooleanSetting reallyToxicSetting = new BooleanSetting("Really Toxic", this, true);
    private final BooleanSetting sybilSetting = new BooleanSetting("Sybil", this, true);
    private final BooleanSetting regularitySetting = new BooleanSetting("Regularity", this, true);
    private final BooleanSetting thinkOfThePeopleSetting = new BooleanSetting("Think of the People", this, true);
    private final BooleanSetting autoSwapOnDarksSetting = new BooleanSetting("Auto-Swap on Darks", this, false);
    private final BooleanSetting autoSwapOnGambleSetting = new BooleanSetting("Auto-Swap on Gamble", this, false);
    private final ModeSetting armageddonSetting = new ModeSetting("Swap To", this, "Venom", Arrays.asList("Venom", "Mirror", "Armageddon"));
    private final NumberSetting swapDelayMsSetting = new NumberSetting("Swap Delay (ms)", this, 100.0, 0.0, 500.0, 50.0);
    private final BooleanSetting autoSwapBackSetting = new BooleanSetting("Auto Swap-Back", this, true);
    private final NumberSetting swapBackDelaySSetting = new NumberSetting("Swap-Back Delay (s)", this, 5.0, 2.0, 15.0, 1.0);
    private final ColorSetting darkEnchantColorSetting = new ColorSetting("Dark Enchant Color", this, 0.0F);
    private final ColorSetting rageEnchantColorSetting = new ColorSetting("Rage Enchant Color", this, 30.0F);
    private final ColorSetting gambleColorSetting = new ColorSetting("Gamble Color", this, 270.0F);
    private final ColorSetting kosColorSetting = new ColorSetting("KOS Color", this, 120.0F);
    private final KeybindListSetting detectionModesSetting = new KeybindListSetting(
        "Detection Modes",
        this,
        this.darkEnchantDetectionSetting,
        this.rageEnchantDetectionSetting,
        this.gambleDetectionSetting,
        this.kosDetectionSetting,
        this.kosOnlySetting,
        this.donTAlertIfLookingSetting,
        this.ignoreFriendsSetting,
        this.detectLobbySetting
    );
    private final KeybindListSetting darkEnchantsSetting = new KeybindListSetting(
        "Dark Enchants",
        this,
        this.venomSetting,
        this.miserySetting,
        this.spiteSetting,
        this.needlessSufferingSetting,
        this.grimReaperSetting,
        this.nostalgiaSetting,
        this.hedgeFundSetting,
        this.heartripperSetting,
        this.sanguisugeSetting,
        this.lycanthropySetting,
        this.mindAssaultSetting,
        this.goldenHandcuffsSetting
    );
    private final KeybindListSetting rageEnchantsSetting = new KeybindListSetting(
        "Rage Enchants",
        this,
        this.aceOfSpadesSetting,
        this.brakesSetting,
        this.comboBreachingChargeSetting,
        this.doItLikeTheFrenchSetting,
        this.heighHoSetting,
        this.newDealSetting,
        this.reallyToxicSetting,
        this.sybilSetting,
        this.regularitySetting,
        this.thinkOfThePeopleSetting
    );
    private final KeybindListSetting autoSwapSetting = new KeybindListSetting(
        "Auto-Swap",
        this,
        this.autoSwapOnDarksSetting,
        this.autoSwapOnGambleSetting,
        this.armageddonSetting,
        this.swapDelayMsSetting,
        this.autoSwapBackSetting,
        this.swapBackDelaySSetting
    );
    private final KeybindListSetting colorsSetting = new KeybindListSetting(
        "Colors", this, this.darkEnchantColorSetting, this.rageEnchantColorSetting, this.gambleColorSetting, this.kosColorSetting
    );
    private final Set<String> itemSet = new HashSet<>();
    private final Map<String, TimerUtil> lookupMap = new HashMap<>();
    private final Map<String, Double> lookupMap2 = new HashMap<>();
    private final TimerUtil timerUtil = new TimerUtil();
    private final TimerUtil timerUtil2 = new TimerUtil();
    private final Map<String, Boolean> lookupMap3 = new HashMap<>();
    private final Set<String> itemSet2 = new HashSet<>();
    private final Map<String, Long> lookupMap4 = new HashMap<>();
    private static final double amount = 95.0;
    private static final long timestamp = 1000L;
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private volatile boolean flag = false;
    private volatile boolean flag2 = false;
    private long timestamp2 = 0L;
    private String text = null;
    private static final int count = 3553;
    private static final int count2 = 3042;
    private static final int count3 = 770;
    private static final int count4 = 771;
    private static final int count5 = 7;

    private static void handleFloat(float f, float f2, float f3) {
        GL11.glScalef(f, f2, f3);
    }

    private static void handleFloat(float f, float f2, float f3, float f4) {
        GL11.glColor4f(f, f2, f3, f4);
    }

    private boolean matchesString(String str) {
        switch (str) {
            case "Ace of Spades":
                return this.aceOfSpadesSetting.isEnabled();
            case "Brakes!":
                return this.brakesSetting.isEnabled();
            case "Combo: Breaching Charge":
                return this.comboBreachingChargeSetting.isEnabled();
            case "Do it like the French":
                return this.doItLikeTheFrenchSetting.isEnabled();
            case "Heigh-Ho":
                return this.heighHoSetting.isEnabled();
            case "New Deal":
                return this.newDealSetting.isEnabled();
            case "Really Toxic":
                return this.reallyToxicSetting.isEnabled();
            case "Sybil":
                return this.sybilSetting.isEnabled();
            case "Regularity":
                return this.regularitySetting.isEnabled();
            case "Think of the People":
                return this.thinkOfThePeopleSetting.isEnabled();
            default:
                return true;
        }
    }

    private String computeString(EntityPlayer player) {
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

                            for (String str : entries2) {
                                if (stringTagAt.contains(str) && this.matchesString(str)) {
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

    private static void glEnd() {
        GL11.glEnd();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.itemSet.clear();
        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.lookupMap3.clear();
        this.itemSet2.clear();
        this.lookupMap4.clear();
        this.flag = false;
        this.flag2 = false;
        this.text = null;

        try {
            scheduledExecutorService.shutdownNow();
        } catch (Exception ignored) {
        }
    }

    private void handleString(String str) {
        if (!this.flag && mc.thePlayer != null) {
            String str2 = this.armageddonSetting.getText().toLowerCase();
            int n = this.computeInt2(str2);
            if (n != -1) {
                ItemStack stack = mc.thePlayer.inventory.armorInventory[1];
                if (stack == null || !this.matchesItemStack(stack, str2)) {
                    if (stack != null && stack.hasDisplayName()) {
                        this.text = stack.getDisplayName();
                    }

                    this.flag = true;
                    this.flag2 = true;
                    this.timestamp2 = System.currentTimeMillis();
                    String str3 = MessageUtils.text9;
                    String str4 = MessageUtils.text13;
                    String str5 = MessageUtils.text9;
                    String str6 = MessageUtils.text15;
                    String text = this.armageddonSetting.getText();
                    String str7 = str6;
                    String str8 = str5;
                    String str9 = str4;
                    MessageUtils.x(str3 + "[" + str9 + "PitAlerts" + str8 + "] " + str7 + "Auto-swapping to " + text + " pants (" + str + " detected)");
                    long l = (long)this.swapDelayMsSetting.computeDouble();
                    if (l > 0L) {
                        scheduledExecutorService.schedule(() -> this.handleInt(n), l, TimeUnit.MILLISECONDS);
                    } else {
                        this.handleInt(n);
                    }
                }
            }
        }
    }

    private int computeInt2(String str) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor)stack.getItem();
                if (item.armorType == 2 && this.matchesItemStack(stack, str)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        return mc.thePlayer == null || mc.objectMouseOver == null ? false : mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit == player;
    }

    private void onEventRender2D(EventRender2D event) {
        if (!this.itemSet.isEmpty()) {
            ScaledResolution scaledResolution = event.getScaledResolution();
            int n = scaledResolution.getScaledWidth() / 2;
            int n2 = (int)(scaledResolution.getScaledHeight() * 0.25);
            float f = (float)this.textScaleSetting.computeDouble();
            process3();
            handleFloat(f, f, 1.0F);
            int n3 = (int)(n / f);
            int n4 = (int)(n2 / f);
            int n5 = n4;

            for (String str : this.itemSet) {
                String[] parts = str.split("\\|");
                String str2 = parts[0];
                int n6 = Integer.parseInt(parts[1]);
                String str3 = parts[2];
                int textWidth = computeFontRenderer(PitAlerts.class).getStringWidth(str2);
                computeFontRenderer(PitAlerts.class).drawStringWithShadow(str2, n3 - textWidth / 2, n5, n6);
                int n7 = n5 + 15;
                if (this.lookupMap2.containsKey(str3)) {
                    double d = this.lookupMap2.get(str3);
                    String str4 = String.format("%.1f", d);
                    int n8;
                    if (d <= this.distanceBlocksSetting.computeDouble()) {
                        n8 = new Color(255, 85, 85).getRGB();
                    } else {
                        n8 = new Color(85, 255, 85).getRGB();
                    }

                    int textWidth2 = computeFontRenderer(PitAlerts.class).getStringWidth(str4);
                    computeFontRenderer(PitAlerts.class).drawStringWithShadow(str4, n3 - textWidth2 / 2, n7, n8);
                    n7 += 15;
                }

                if (parts.length > 3) {
                    String str5 = parts[3];
                    int textWidth3 = computeFontRenderer(PitAlerts.class).getStringWidth(str5);
                    computeFontRenderer(PitAlerts.class).drawStringWithShadow(str5, n3 - textWidth3 / 2, n7, new Color(200, 200, 200).getRGB());
                    n7 += 15;
                }

                n5 = n7 + 5;
            }

            process();
        }
    }

    private void to() {
        if (this.soundEnabledSetting.isEnabled()) {
            mc.thePlayer.playSound("random.orb", 0.5F, 2.0F);
            this.timerUtil2.reset();
        }
    }

    private static void handleInt(int n, int n2) {
        GL11.glBlendFunc(n, n2);
    }

    private static void glBegin(int n) {
        GL11.glBegin(n);
    }

    private boolean matchesString2(String str) {
        switch (str) {
            case "Venom":
                return this.venomSetting.isEnabled();
            case "Misery":
                return this.miserySetting.isEnabled();
            case "Spite":
                return this.spiteSetting.isEnabled();
            case "Needless Suffering":
                return this.needlessSufferingSetting.isEnabled();
            case "Grim Reaper":
                return this.grimReaperSetting.isEnabled();
            case "Nostalgia":
                return this.nostalgiaSetting.isEnabled();
            case "Hedge Fund":
                return this.hedgeFundSetting.isEnabled();
            case "Heartripper":
                return this.heartripperSetting.isEnabled();
            case "Sanguisuge":
                return this.sanguisugeSetting.isEnabled();
            case "Lycanthropy":
                return this.lycanthropySetting.isEnabled();
            case "Mind Assault":
                return this.mindAssaultSetting.isEnabled();
            case "Golden Handcuffs":
                return this.goldenHandcuffsSetting.isEnabled();
            default:
                return true;
        }
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

                            for (String str : entries) {
                                if (stringTagAt.contains(str) && this.matchesString2(str)) {
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

    private void handleInt(int n) {
        mc.addScheduledTask(() -> {
            if (mc.thePlayer == null) {
                this.flag = false;
            } else {
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 7, n, 2, mc.thePlayer);
                mc.thePlayer.playSound("random.click", 1.0F, 1.2F);
                this.flag = false;
                if (this.autoSwapBackSetting.isEnabled()) {
                    long l = (long)(this.swapBackDelaySSetting.computeDouble() * 1000.0);
                    scheduledExecutorService.schedule(() -> mc.addScheduledTask(() -> {
                        if (mc.thePlayer == null) {
                            this.flag2 = false;
                        } else {
                            int n2 = -1;
                            if (this.text != null) {
                                n2 = this.computeInt(this.text);
                            }

                            if (n2 == -1) {
                                String str = this.armageddonSetting.getText().toLowerCase();

                                for (int i = 0; i < 9; i++) {
                                    ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
                                    if (stack != null && stack.getItem() instanceof ItemArmor) {
                                        ItemArmor item = (ItemArmor)stack.getItem();
                                        if (item.armorType == 2 && !this.matchesItemStack(stack, str)) {
                                            n2 = i;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (n2 != -1) {
                                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 7, n2, 2, mc.thePlayer);
                                mc.thePlayer.playSound("random.click", 1.0F, 1.0F);
                                String str2 = MessageUtils.text11;
                                String str3 = MessageUtils.text9;
                                String str4 = MessageUtils.text13;
                                MessageUtils.x(MessageUtils.text9 + "[" + str4 + "PitAlerts" + str3 + "] " + str2 + "Swapped back to original pants.");
                            }

                            this.flag2 = false;
                            this.text = null;
                        }
                    }), l, TimeUnit.MILLISECONDS);
                }
            }
        });
    }

    private static void handleInt2(int n) {
        GL11.glEnable(n);
    }

    private static void handleFloat(float f, float f2) {
        GL11.glVertex2f(f, f2);
    }

    private static void handleInt3(int n) {
        GL11.glDisable(n);
    }

    private static void process3() {
        GL11.glPushMatrix();
    }

    public PitAlerts() {
        super("Pit Alerts", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.distanceBlocksSetting,
                this.cooldownSecsSetting,
                this.textScaleSetting,
                this.soundEnabledSetting,
                this.detectionModesSetting,
                this.darkEnchantsSetting,
                this.rageEnchantsSetting,
                this.autoSwapSetting,
                this.colorsSetting
            }
        );
    }

    private static void process() {
        GL11.glPopMatrix();
    }

    private boolean matchesItemStack(ItemStack stack, String str) {
        if (stack != null && stack.hasTagCompound()) {
            String str2 = stack.getTagCompound().toString().toLowerCase();
            return str2.contains(str);
        }
        return false;
    }

    private int computeInt(String str) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor)stack.getItem();
                if (item.armorType == 2 && stack.hasDisplayName() && stack.getDisplayName().equals(str)) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                this.onEventUpdate((EventUpdate)event);
            } else if (event instanceof EventRender2D) {
                this.onEventRender2D((EventRender2D)event);
            }
        }
    }

    private String computeString(String str) {
        switch (str) {
            case "Venom":
                return "VENOMS";
            case "Misery":
                return "MISERY";
            case "Spite":
                return "SPITE";
            case "Needless Suffering":
                return "NEEDLESS";
            case "Grim Reaper":
                return "REAPER";
            case "Nostalgia":
                return "NOSTALGIA";
            case "Hedge Fund":
                return "HEDGE";
            case "Heartripper":
                return "HEARTS";
            case "Sanguisuge":
                return "SANG";
            case "Lycanthropy":
                return "LYCAN";
            case "Mind Assault":
                return "MINDS";
            case "Golden Handcuffs":
                return "HANDCUFFS";
            case "Ace of Spades":
                return "ACE";
            case "Brakes!":
                return "BRAKES";
            case "Combo: Breaching Charge":
                return "BREACH";
            case "Do it like the French":
                return "FRENCH";
            case "Heigh-Ho":
                return "HEIGH-HO";
            case "New Deal":
                return "NEW DEAL";
            case "Really Toxic":
                return "TOXIC";
            case "Sybil":
                return "SYBIL";
            case "Regularity":
                return "REGS";
            case "Think of the People":
                return "THINK NEARBY";
            default:
                return str.toUpperCase();
        }
    }

    private void onEventUpdate(EventUpdate event) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (this.detectLobbySetting.isEnabled() || !FreshHelper.matchesWorld2(mc.theWorld, mc.thePlayer)) {
                HashSet set = new HashSet();
                this.lookupMap2.clear();

                for (EntityPlayer player : EntityCache.getEntries()) {
                    if (player != mc.thePlayer) {
                        String name = player.getName();
                        if (!this.ignoreFriendsSetting.isEnabled() || !DefaultHelper.matchesString(name)) {
                            boolean flag = this.computeString2(player) != null;
                            boolean flag2 = this.computeString(player) != null;
                            boolean flag3 = this.matchesEntityPlayer2(player);
                            boolean flag4 = flag || flag2 || flag3;
                            boolean flag5 = FreshHelper.matchesWorld2(mc.theWorld, player);
                            if (this.detectLobbySetting.isEnabled()) {
                                if (flag5 && !flag4) {
                                    double dist = mc.thePlayer.getDistanceToEntity(player);
                                    if (!(dist > this.distanceBlocksSetting.computeDouble())) {
                                        if (!this.lookupMap.containsKey(name)) {
                                            this.lookupMap.put(name, new TimerUtil());
                                        }

                                        TimerUtil timerUtil = this.lookupMap.get(name);
                                        if (timerUtil.matchesLong((long)(this.cooldownSecsSetting.computeDouble() * 1000.0), false)) {
                                            this.lookupMap2.put(name, dist);
                                            String str = "IN LOBBY! " + name;
                                            set.add(str + "|" + this.kosColorSetting.computeColor().getRGB() + "|" + name);
                                            timerUtil.reset();
                                            this.to();
                                        }
                                    }
                                }
                            } else {
                                if (!this.lookupMap4.containsKey(name)) {
                                    this.lookupMap4.put(name, System.currentTimeMillis());
                                }

                                Long val = this.lookupMap4.get(name);
                                if (val != null && System.currentTimeMillis() - val < 1000L) {
                                    this.lookupMap3.put(name, flag5);
                                    this.itemSet2.remove(name);
                                } else if (flag5) {
                                    this.itemSet2.remove(name);
                                    this.lookupMap3.put(name, true);
                                } else {
                                    Boolean val2 = this.lookupMap3.get(name);
                                    if (val2 == null) {
                                        this.lookupMap3.put(name, false);
                                    } else {
                                        if (val2 && player.getHealth() > 0.0F) {
                                            this.itemSet2.add(name);
                                        }

                                        this.lookupMap3.put(name, false);
                                        if (this.itemSet2.contains(name)) {
                                            if (player.posY >= 95.0) {
                                                continue;
                                            }

                                            this.itemSet2.remove(name);
                                        }

                                        double dist2 = mc.thePlayer.getDistanceToEntity(player);
                                        if (!(dist2 > this.distanceBlocksSetting.computeDouble())) {
                                            boolean flag6 = WHelper.matchesString(name);
                                            if ((!this.kosOnlySetting.isEnabled() || flag6)
                                                && (!this.donTAlertIfLookingSetting.isEnabled() || !this.matchesEntityPlayer(player))) {
                                                if (!this.lookupMap.containsKey(name)) {
                                                    this.lookupMap.put(name, new TimerUtil());
                                                }

                                                TimerUtil timerUtil2 = this.lookupMap.get(name);
                                                if (timerUtil2.matchesLong((long)(this.cooldownSecsSetting.computeDouble() * 1000.0), false)) {
                                                    this.lookupMap2.put(name, dist2);
                                                    if (this.kosDetectionSetting.isEnabled() && flag6) {
                                                        String str2 = "KOS NEAR! " + name;
                                                        set.add(str2 + "|" + this.kosColorSetting.computeColor().getRGB() + "|" + name);
                                                        timerUtil2.reset();
                                                        this.to();
                                                    }

                                                    if (this.darkEnchantDetectionSetting.isEnabled()) {
                                                        String str3 = this.computeString2(player);
                                                        if (str3 != null) {
                                                            String str4 = this.computeString(str3) + "! " + name;
                                                            set.add(str4 + "|" + this.darkEnchantColorSetting.computeColor().getRGB() + "|" + name);
                                                            timerUtil2.reset();
                                                            this.to();
                                                            if (this.autoSwapOnDarksSetting.isEnabled() && !this.flag && !this.flag2) {
                                                                this.handleString("darks");
                                                            }
                                                        }
                                                    }

                                                    if (this.rageEnchantDetectionSetting.isEnabled()) {
                                                        String str5 = this.computeString(player);
                                                        if (str5 != null) {
                                                            String str6 = this.computeString(str5);
                                                            if (str5.equals("Think of the People")) {
                                                                String str7 = "THINK NEARBY! " + name;
                                                                set.add(
                                                                    str7
                                                                        + "|"
                                                                        + this.rageEnchantColorSetting.computeColor().getRGB()
                                                                        + "|"
                                                                        + name
                                                                        + "|Executioner and Perun Don't work!"
                                                                );
                                                            } else {
                                                                String str8 = str6 + "! " + name;
                                                                set.add(str8 + "|" + this.rageEnchantColorSetting.computeColor().getRGB() + "|" + name);
                                                            }

                                                            timerUtil2.reset();
                                                            this.to();
                                                        }
                                                    }

                                                    if (this.gambleDetectionSetting.isEnabled() && this.matchesEntityPlayer2(player)) {
                                                        String str9 = "BOMBER! " + name;
                                                        set.add(str9 + "|" + this.gambleColorSetting.computeColor().getRGB() + "|" + name);
                                                        timerUtil2.reset();
                                                        this.to();
                                                        if (this.autoSwapOnGambleSetting.isEnabled() && !this.flag && !this.flag2) {
                                                            this.handleString("gamble");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (!set.isEmpty()) {
                    this.itemSet.clear();
                    this.itemSet.addAll(set);
                    this.timerUtil.reset();
                }

                if (this.timerUtil.matchesLong(4000L, false)) {
                    this.itemSet.clear();
                }

                if (this.soundEnabledSetting.isEnabled() && !this.itemSet.isEmpty() && !this.timerUtil2.matchesLong(1000L, false)) {
                    mc.thePlayer.playSound("random.orb", 0.5F, 2.0F);
                    this.timerUtil2.reset();
                }
            }
        }
    }

    private boolean matchesEntityPlayer2(EntityPlayer player) {
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null) {
            NBTTagCompound tagCompound = heldItem.getTagCompound();
            if (tagCompound != null) {
                return tagCompound.toString().toLowerCase().contains("gamble");
            }
        }

        return false;
    }
}
