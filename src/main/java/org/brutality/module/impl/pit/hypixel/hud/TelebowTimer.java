package org.brutality.module.impl.pit.hypixel.hud;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.GetPlayerEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.utility.TelebowESP;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.player.ObserveAttemptHelper;
import org.brutality.utils.player.SomberHelper;
import org.brutality.utils.player.TickHelper;
import org.lwjgl.input.Mouse;

public class TelebowTimer extends Module {
    private static TelebowTimer instance;
    private final PitCategory pitCategory = PitCategory.HUD;
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 50.0, 0.0, 1080.0, 1.0);
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 5.0, 0.0, 1920.0, 1.0);
    private final BooleanSetting showInChatSetting = new BooleanSetting("Show In Chat", this, false);
    private final BooleanSetting showLabelSetting = new BooleanSetting("Show Label", this, true);
    private final BooleanSetting showNotificationSetting = new BooleanSetting("Show Notification", this, false);
    private final BooleanSetting warnOnDarkPantsSetting = new BooleanSetting("Warn on Dark Pants", this, true);
    private final ModeSetting modeSetting = new ModeSetting("Suffix Mode", this, "#", Arrays.asList("#", "#.#"));
    private final BooleanSetting detectOthersSetting = new BooleanSetting("Detect Others", this, true);
    private final BooleanSetting othersOnHUDSetting = new BooleanSetting("Others On HUD", this, true)
        .computeBooleanSetting(() -> this.detectOthersSetting.isEnabled());
    private final BooleanSetting estimateOtherHitsSetting = new BooleanSetting("Estimate Other Hits", this, true)
        .computeBooleanSetting(() -> this.detectOthersSetting.isEnabled());
    private final BooleanSetting othersInChatSetting = new BooleanSetting("Others In Chat", this, false)
        .computeBooleanSetting(() -> this.detectOthersSetting.isEnabled());
    private final BooleanSetting showOnNametagsSetting = new BooleanSetting("Show On Nametags", this, false)
        .computeBooleanSetting(() -> this.detectOthersSetting.isEnabled());
    private final BooleanSetting showTelebowTierSetting = new BooleanSetting("Show Telebow Tier", this, true);
    private final BooleanSetting kosOnlySetting = new BooleanSetting("KOS Only", this, false);
    private long timestamp = 0L;
    private final Map<String, TelebowTimer.Entry3> lookupMap = new ConcurrentHashMap<>();
    private final Map<String, TelebowTimer.Entry> lookupMap2 = new ConcurrentHashMap<>();
    private final Map<String, TelebowTimer.Entry2> lookupMap3 = new ConcurrentHashMap<>();
    private World world;
    private static final List<String> entries = Arrays.asList(
        "somber",
        "venom",
        "misery",
        "spite",
        "needless suffering",
        "grim reaper",
        "nostalgia",
        "hedge fund",
        "heartripper",
        "sanguisuge",
        "lycanthropy",
        "mind assault",
        "golden handcuffs",
        "solitude"
    );

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private static int computeInt(String str) {
        String str2 = str.toUpperCase();
        if (str2.startsWith("III")) {
            return 3;
        }
        if (str2.startsWith("II")) {
            return 2;
        }
        if (str2.startsWith("IV")) {
            return 4;
        }
        if (str2.startsWith("V")) {
            return 5;
        }
        return str2.startsWith("I") ? 1 : 0;
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        ItemStack stack = player.inventory.armorInventory[1];
        if (stack != null && stack.hasTagCompound()) {
            String str = stack.getTagCompound().toString().toLowerCase(Locale.ROOT);

            for (String str2 : entries) {
                if (str.contains(str2)) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    private void handleEntityPlayer(EntityPlayer player, int n, long l, BlockPos pos) {
        if (SomberHelper.matchesEntityPlayer(player)) {
            SomberHelper.handleEntityPlayer(player, n, pos, l);
        }
    }

    public boolean checkState() {
        return this.checkState44()
            && this.detectOthersSetting != null
            && this.detectOthersSetting.isEnabled()
            && this.estimateOtherHitsSetting != null
            && this.estimateOtherHitsSetting.isEnabled();
    }

    private FontRenderer computeFontRenderer(Minecraft mc) {
        return computeFontRenderer(TelebowTimer.class);
    }

    public static long computeLong(int n) {
        switch (n) {
            case 2:
                return 45000L;
            case 3:
                return 20000L;
            default:
                return 90000L;
        }
    }

    private EntityPlayer computeEntityPlayer(String str) {
        if (mc.theWorld != null) {
            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity.getName().equalsIgnoreCase(str)) {
                    return playerEntity;
                }
            }
        }

        return null;
    }

    private void handleString(String str, int n, long l, long l2) {
        if (l > l2 && this.detectOthersSetting.isEnabled()) {
            TelebowTimer.Entry3 entry3 = this.lookupMap.get(str);
            EntityPlayer player = this.computeEntityPlayer(str);
            UUID uuid = player != null ? player.getUniqueID() : (entry3 == null ? null : entry3.uuid);
            this.lookupMap.put(str, new TelebowTimer.Entry3(str, n, l, l2, uuid));
            if (entry3 == null || entry3.timestamp != l) {
                this.handleString(str + "'s", l - l2);
                if (this.othersInChatSetting.isEnabled()) {
                    long l3 = (l - l2 + 999L) / 1000L;
                    MessageUtils.x("§f" + str + "'s Telebow cooldown: §d" + l3 + "s");
                }
            }
        }
    }

    private void handleString(String str, long l) {
        if (this.showNotificationSetting.isEnabled()) {
            long l2 = (l + 999L) / 1000L;
            NotificationFeedHandler.handleString("§f" + str + " Telebow cooldown: §d" + l2 + "s");
        }
    }

    public static int computeInt2(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            NBTTagCompound nbt = tagCompound.hasKey("ExtraAttributes") ? tagCompound.getCompoundTag("ExtraAttributes") : tagCompound;
            if (nbt.hasKey("CustomEnchants")) {
                NBTTagList tagList = nbt.getTagList("CustomEnchants", 10);

                for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                    if ("telebow".equals(compoundTagAt.getString("Key"))) {
                        int integer = compoundTagAt.getInteger("Level");
                        if (integer > 0) {
                            return integer;
                        }
                    }
                }
            }

            return computeInt(stack);
        }
        return 0;
    }

    public TelebowTimer() {
        super("TelebowTimer", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.yPosSetting,
                this.xPosSetting,
                this.showInChatSetting,
                this.showLabelSetting,
                this.showNotificationSetting,
                this.modeSetting,
                this.warnOnDarkPantsSetting,
                this.detectOthersSetting,
                this.othersOnHUDSetting,
                this.estimateOtherHitsSetting,
                this.othersInChatSetting,
                this.showOnNametagsSetting,
                this.showTelebowTierSetting,
                this.kosOnlySetting
            }
        );
        instance = this;
    }

    public String computeString(String str, long l) {
        if (this.checkState() && mc.theWorld != null) {
            TelebowTimer.Entry3 entry3 = this.lookupMap.get(str);
            EntityPlayer player = this.computeEntityPlayer(str);
            UUID uuid = entry3 != null && entry3.uuid != null ? entry3.uuid : (player == null ? null : player.getUniqueID());
            if (uuid == null) {
                return "";
            }
            long l2 = ObserveAttemptHelper.instance.computeLong3(mc.theWorld, uuid, l);
            return l2 <= 0L ? "" : " (Est: ~" + this.computeString(l2 - l) + ")";
        }
        return "";
    }

    private String computeString(long l) {
        double d = Math.max(0.0, l / 1000.0);
        return this.modeSetting.matchesString2("#") ? String.format(Locale.ROOT, "%.0f", d) : String.format(Locale.ROOT, "%.1f", d);
    }

    private void onGetPlayerEvent(GetPlayerEvent getPlayerEvent) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player != null && getPlayerEvent.getPlayer() == player && SomberHelper.matchesEntityPlayer(player) && getPlayerEvent.getCount() >= 3) {
            ItemStack itemStack = getPlayerEvent.getItemStack();
            int n = computeInt2(itemStack);
            if (n > 0) {
                if (this.matchesEntityPlayer(player)) {
                    if (this.warnOnDarkPantsSetting.isEnabled() && player.ticksExisted % 20 == 0) {
                        MessageUtils.x(MessageUtils.text13 + "Telebow timer not started while wearing dark pants.");
                    }
                } else if (!player.isPotionActive(Potion.poison)) {
                    if (!FreshHelper.matchesWorld3(player.worldObj, player)) {
                        if (itemStack != null && player.isSneaking()) {
                            SomberHelper.handleEntityPlayer(player, n, TelebowESP.computeBlockPos(player, getPlayerEvent.getCount()), System.currentTimeMillis());
                        }
                    }
                }
            }
        }
    }

    private static int computeInt(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("display", 10)) {
            NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
            if (!compoundTag.hasKey("Lore", 9)) {
                return 0;
            }
            NBTTagList tagList = compoundTag.getTagList("Lore", 8);

            for (int i = 0; i < tagList.tagCount(); i++) {
                String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(tagList.getStringTagAt(i));
                int n = textWithoutFormattingCodes.toLowerCase().indexOf("telebow");
                if (n != -1) {
                    String str = textWithoutFormattingCodes.substring(n + "telebow".length()).trim();
                    return computeInt(str);
                }
            }

            return 0;
        }
        return 0;
    }

    public String computeString(String str) {
        if (!this.checkState44() || !SomberHelper.checkState() || !this.detectOthersSetting.isEnabled() || !this.showOnNametagsSetting.isEnabled()) {
            return "";
        }
        if (this.kosOnlySetting.isEnabled() && !WHelper.matchesString(str)) {
            return "";
        }
        TelebowTimer.Entry3 entry3 = this.lookupMap.get(str);
        long l = entry3 == null ? 0L : entry3.timestamp - System.currentTimeMillis();
        if (l <= 0L) {
            return "";
        }
        EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
        EnumChatFormatting LIGHT_PURPLE = EnumChatFormatting.LIGHT_PURPLE;
        String str2 = this.computeString(l);
        EnumChatFormatting GOLD = EnumChatFormatting.GOLD;
        String str3 = this.computeString(str, System.currentTimeMillis());
        EnumChatFormatting WHITE2 = EnumChatFormatting.WHITE;
        String str4 = str3;
        EnumChatFormatting enumChatFormatting = GOLD;
        String str5 = str2;
        EnumChatFormatting enumChatFormatting2 = LIGHT_PURPLE;
        return WHITE + "[Telebow: " + enumChatFormatting2 + str5 + enumChatFormatting + str4 + WHITE2 + "]";
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.thePlayer != null && (!(mc.currentScreen instanceof GuiChat) || this.showInChatSetting.isEnabled())) {
            boolean flag = this.timestamp > System.currentTimeMillis();
            boolean flag2 = SomberHelper.checkState() && this.detectOthersSetting.isEnabled() && this.othersOnHUDSetting.isEnabled();
            boolean flag3 = flag2 && !this.lookupMap.isEmpty();
            boolean flag4 = mc.currentScreen instanceof GuiChat;
            boolean flag5 = flag || flag3 || flag4 && this.showInChatSetting.isEnabled();
            if (!flag5) {
                HudDragHelper.handleObject(this);
            } else {
                float f = (float)this.xPosSetting.computeDouble();
                float f2 = (float)this.yPosSetting.computeDouble();
                if (flag4) {
                    int[] ints = HudDragHelper.computeIntArray();
                    int n = ints[0];
                    int n2 = ints[1];
                    if (Mouse.isButtonDown(0)) {
                        if (!HudDragHelper.matchesObject(this)) {
                            FontRenderer fontRenderer = this.computeFontRenderer(mc);
                            int n3 = fontRenderer.FONT_HEIGHT * (1 + (flag2 ? this.lookupMap.size() : 0));
                            String str = flag ? "Telebow: " + (this.timestamp - System.currentTimeMillis()) / 1000L : "Telebow: 0";
                            int n4 = fontRenderer.getStringWidth(str) + 30;
                            if (flag2) {
                                for (TelebowTimer.Entry3 entry3 : this.lookupMap.values()) {
                                    String str2 = this.showTelebowTierSetting.isEnabled() ? " [" + entry3.count + "]" : "";
                                    String text = entry3.text;
                                    String str3 = this.computeString(entry3.timestamp - System.currentTimeMillis());
                                    String str4 = this.computeString(entry3.text, System.currentTimeMillis());
                                    String str5 = str3;
                                    n4 = Math.max(n4, fontRenderer.getStringWidth(text + str2 + ": " + str5 + str4));
                                }
                            }

                            if (HudDragHelper.matchesInt(n, n2, f, f2, n4, n3)) {
                                HudDragHelper.matchesObject(this, n, n2, (int)f, (int)f2);
                            }
                        }
                    } else {
                        HudDragHelper.handleObject(this);
                    }

                    if (HudDragHelper.matchesObject(this)) {
                        int[] ints2 = HudDragHelper.computeIntArray(this);
                        if (ints2 != null) {
                            this.xPosSetting.handleDouble2(ints2[0]);
                            this.yPosSetting.handleDouble2(ints2[1]);
                            f = ints2[0];
                            f2 = ints2[1];
                        }
                    }
                }

                FontRenderer fontRenderer2 = this.computeFontRenderer(mc);
                float f3 = f2;
                if (flag) {
                    String str6 = this.showLabelSetting.isEnabled() ? "Telebow: " : "";
                    fontRenderer2.drawStringWithShadow(str6, f, f2, 16777215);
                    fontRenderer2.drawStringWithShadow(
                        this.computeString(this.timestamp - System.currentTimeMillis()), f + fontRenderer2.getStringWidth(str6), f2, 16733695
                    );
                    f3 = f2 + (fontRenderer2.FONT_HEIGHT + 2);
                } else if (flag4 && this.showInChatSetting.isEnabled()) {
                    String str7 = this.showLabelSetting.isEnabled() ? "Telebow: " : "";
                    fontRenderer2.drawStringWithShadow(str7, f, f2, 16777215);
                    fontRenderer2.drawStringWithShadow(this.computeString(0L), f + fontRenderer2.getStringWidth(str7), f2, 16733695);
                    f3 = f2 + (fontRenderer2.FONT_HEIGHT + 2);
                }

                if (flag2) {
                    for (Map.Entry entry : this.lookupMap.entrySet()) {
                        TelebowTimer.Entry3 val = (TelebowTimer.Entry3)entry.getValue();
                        long l = val.timestamp - System.currentTimeMillis();
                        if (l > 0L) {
                            String str8 = this.showTelebowTierSetting.isEnabled() ? " [" + val.count + "]" : "";
                            String str9 = val.text + str8 + ": ";
                            fontRenderer2.drawStringWithShadow(str9, f, f3, 11184810);
                            fontRenderer2.drawStringWithShadow(this.computeString(l), f + fontRenderer2.getStringWidth(str9), f3, 16733695);
                            String str10 = this.computeString(val.text, System.currentTimeMillis());
                            if (!str10.isEmpty()) {
                                fontRenderer2.drawStringWithShadow(str10, f + fontRenderer2.getStringWidth(str9 + this.computeString(l)), f3, 16755200);
                            }

                            f3 += fontRenderer2.FONT_HEIGHT + 1;
                        }
                    }
                }
            }
        } else {
            HudDragHelper.handleObject(this);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        TickHelper.clear();
        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.lookupMap3.clear();
        this.world = null;
        this.timestamp = 0L;
        HudDragHelper.handleObject(this);
    }

    public void handleEntityPlayer(EntityPlayer player, long timestamp, long l) {
        if (this.checkState44()) {
            if (player == mc.thePlayer) {
                this.timestamp = timestamp;
                this.handleString("Your", timestamp - l);
            } else if (this.detectOthersSetting.isEnabled() && (!this.kosOnlySetting.isEnabled() || WHelper.matchesString(player.getName()))) {
                long l2 = timestamp - l;
                int n = l2 == computeLong(3) ? 3 : (l2 == computeLong(2) ? 2 : 1);
                this.handleString(player.getName(), n, timestamp, l);
            }
        }
    }

    private void handleUpdate() {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.world != mc.theWorld) {
            ObserveAttemptHelper.instance.handleObject2(mc.theWorld);
            this.lookupMap2.clear();
            this.lookupMap3.clear();
            this.world = mc.theWorld;
        }

        if (mc.theWorld != null && mc.thePlayer != null) {
            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (!playerEntity.isEntityAlive()) {
                    SomberHelper.handleEntityPlayer(playerEntity);
                }
            }

            if (!mc.thePlayer.isEntityAlive()) {
                SomberHelper.handleEntityPlayer(mc.thePlayer);
            }

            TickHelper.tick();
            if (!SomberHelper.checkState()) {
                this.lookupMap2.clear();
                this.lookupMap3.clear();
            } else if (!this.detectOthersSetting.isEnabled()) {
                this.lookupMap.clear();
                this.lookupMap2.clear();
                this.lookupMap3.clear();
            } else {
                long now = System.currentTimeMillis();
                this.lookupMap
                    .entrySet()
                    .removeIf(entry -> now > entry.getValue().timestamp || this.kosOnlySetting.isEnabled() && !WHelper.matchesString(entry.getKey()));
                this.lookupMap2.entrySet().removeIf(entry2 -> now - entry2.getValue().timestamp > 5000L);
                HashSet set = new HashSet();

                for (EntityPlayer playerEntity2 : mc.theWorld.playerEntities) {
                    if (playerEntity2 != mc.thePlayer) {
                        String name = playerEntity2.getName();
                        set.add(name);
                        if (this.kosOnlySetting.isEnabled() && !WHelper.matchesString(name)) {
                            this.lookupMap3.remove(name);
                            this.lookupMap.remove(name);
                            this.lookupMap2.remove(name);
                        } else if (!SomberHelper.matchesEntityPlayer2(playerEntity2)) {
                            this.lookupMap3.remove(name);
                            this.lookupMap2.remove(name);
                            if (!playerEntity2.isEntityAlive()) {
                                this.handleEntityPlayer(playerEntity2);
                            }
                        } else {
                            ItemStack heldItem = playerEntity2.getHeldItem();
                            int n = heldItem != null && heldItem.getItem() instanceof ItemBow ? computeInt2(heldItem) : 0;
                            if (n > 0) {
                                TelebowTimer.Entry entry3 = this.lookupMap2.get(name);
                                long l = playerEntity2.isSneaking() ? now : (entry3 == null ? -1L : entry3.timestamp2);
                                this.lookupMap2.put(name, new TelebowTimer.Entry(n, now, l));
                            }

                            boolean flag = n > 0 && (playerEntity2.isEating() || playerEntity2.isUsingItem());
                            TelebowTimer.Entry2 entry22 = this.lookupMap3.get(name);
                            if (flag) {
                                if (entry22 == null) {
                                    entry22 = new TelebowTimer.Entry2(n, mc.theWorld.getTotalWorldTime());
                                    this.lookupMap3.put(name, entry22);
                                }

                                if (playerEntity2.isSneaking()) {
                                    entry22.timestamp2 = now;
                                }
                            } else if (entry22 != null) {
                                this.lookupMap3.remove(name);
                                if (entry22.timestamp2 >= 0L
                                    && now - entry22.timestamp2 <= 200L
                                    && !this.matchesEntityPlayer(playerEntity2)
                                    && !playerEntity2.isPotionActive(Potion.poison)
                                    && n > 0) {
                                    this.handleEntityPlayer(
                                        playerEntity2,
                                        entry22.count,
                                        now,
                                        TelebowESP.computeBlockPos(playerEntity2, (int)Math.max(1L, mc.theWorld.getTotalWorldTime() - entry22.timestamp))
                                    );
                                }
                            }

                            TelebowTimer.Entry entry4 = this.lookupMap2.get(name);
                            long l2 = ObserveAttemptHelper.instance.computeLong2(mc.theWorld, playerEntity2.getUniqueID(), now);
                            if (l2 > now && (entry4 != null || this.lookupMap.containsKey(name))) {
                                this.handleString(name, entry4 != null ? entry4.count : this.lookupMap.get(name).count, l2, now);
                            }
                        }
                    }
                }

                this.lookupMap3.keySet().retainAll(set);
                this.lookupMap2.keySet().retainAll(set);

                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityArrow entity2
                        && entity2.shootingEntity != null
                        && entity2.shootingEntity instanceof EntityPlayer
                        && entity2.shootingEntity != mc.thePlayer
                        && entity2.ticksExisted <= 3) {
                        EntityPlayer shootingEntity = (EntityPlayer)entity2.shootingEntity;
                        String name2 = shootingEntity.getName();
                        if (!this.kosOnlySetting.isEnabled() || WHelper.matchesString(name2)) {
                            TelebowTimer.Entry entry5 = this.lookupMap2.get(name2);
                            if (entry5 != null
                                && entry5.timestamp2 >= 0L
                                && now - entry5.timestamp2 <= 250L
                                && SomberHelper.matchesEntityPlayer(shootingEntity)
                                && !this.matchesEntityPlayer(shootingEntity)) {
                                this.handleEntityPlayer(shootingEntity, entry5.count, now, TelebowESP.computeBlockPos(entity2));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                TickHelper.clear();
                ObserveAttemptHelper.instance.handleObject2(mc.theWorld);
                this.lookupMap2.clear();
                this.lookupMap3.clear();
                this.world = null;
            } else {
                if (event instanceof GetPlayerEvent && !event.cancel2()) {
                    GetPlayerEvent getPlayerEvent = (GetPlayerEvent)event;
                    TickHelper.handleEntityPlayer(getPlayerEvent.getPlayer(), getPlayerEvent.getItemStack(), getPlayerEvent.getCount());
                    this.onGetPlayerEvent(getPlayerEvent);
                }

                if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    this.handleUpdate();
                }

                if (event instanceof ChatEvent chatEvent
                    && chatEvent.message != null
                    && mc.thePlayer != null
                    && SomberHelper.matchesString(chatEvent.message.getUnformattedText())) {
                    ObserveAttemptHelper.instance.handleUUID2(mc.thePlayer.getUniqueID());
                }
            }
        }
    }

    public void handleEntityPlayer(EntityPlayer player) {
        if (player == mc.thePlayer) {
            TickHelper.clear();
        }

        this.lookupMap.remove(player.getName());
        this.lookupMap2.remove(player.getName());
        this.lookupMap3.remove(player.getName());
    }

    public static TelebowTimer getInstance() {
        return instance;
    }

    public static class Entry {
        public final int count;
        public final long timestamp;
        public final long timestamp2;

        public Entry(int count, long timestamp, long l) {
            this.count = count;
            this.timestamp = timestamp;
            this.timestamp2 = l;
        }
    }

    public static final class Entry2 {
        public final int count;
        public final long timestamp;
        public long timestamp2;
        private static final long timestamp3 = -1L;

        public Entry2(int count, long timestamp) {
            this.timestamp2 = timestamp3;
            this.count = count;
            this.timestamp = timestamp;
        }
    }

    public static class Entry3 {
        public final String text;
        public final int count;
        public final long timestamp;
        public final long timestamp2;
        public final UUID uuid;

        public Entry3(String text, int count, long timestamp, long l, UUID uuid) {
            this.text = text;
            this.count = count;
            this.timestamp = timestamp;
            this.timestamp2 = l;
            this.uuid = uuid;
        }
    }
}
