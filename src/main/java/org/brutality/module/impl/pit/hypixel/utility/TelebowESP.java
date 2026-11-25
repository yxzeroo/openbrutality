package org.brutality.module.impl.pit.hypixel.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.hud.TelebowTimer;
import org.brutality.notification.old.ModuleEnabledHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.player.ObserveAttemptHelper;
import org.brutality.utils.player.SomberHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.opengl.GL11;

public class TelebowESP extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final BooleanSetting autoScaleLocationSetting = new BooleanSetting("Auto Scale Location", this, true);
    private final BooleanSetting detectOthersSetting = new BooleanSetting("Detect Others", this, true);
    private final BooleanSetting liveAimPreviewSetting = new BooleanSetting("Live Aim Preview", this, true)
        .computeBooleanSetting(() -> this.detectOthersSetting.isEnabled());
    private final ModeSetting bothSetting = new ModeSetting("KOS Alert", this, "Chat", Arrays.asList("Off", "Chat", "Notification", "Both"));
    private final BooleanSetting lowHealthGuildNotifySetting = new BooleanSetting("Low Health Guild Notify", this, false);
    private final NumberSetting lowHealthThresholdSetting = new NumberSetting("Low Health Threshold", this, 10.0, 1.0, 20.0, 0.5);
    private final BooleanSetting includeLocationSetting = new BooleanSetting("Include Location", this, false);
    private final BooleanSetting warnLowHealthFriendsSetting = new BooleanSetting("Warn Low Health Friends", this, true);
    private final NumberSetting friendHealthThresholdSetting = new NumberSetting("Friend Health Threshold", this, 5.0, 1.0, 5.0, 0.5);
    private final ModeSetting kitsSetting = new ModeSetting("Friend Warning Format", this, "Count", Arrays.asList("Count", "Names", "Kits"));
    private final NumberSetting warningWithinSetting = new NumberSetting("Warning Within", this, 10.0, 1.0, 10.0, 1.0);
    private long timestamp;
    private long timestamp2;
    private final Map<UUID, TelebowESP.Entry2> lookupMap = new HashMap<>();
    private final Map<UUID, TelebowESP.Entry4> lookupMap2 = new HashMap<>();
    private final Map<UUID, Long> lookupMap3 = new HashMap<>();
    private final Map<UUID, TelebowESP.Entry5> lookupMap4 = new HashMap<>();
    private final Map<String, TelebowESP.Entry3> lookupMap5 = new HashMap<>();
    private final Map<String, Long> lookupMap6 = new HashMap<>();
    private final Set<Integer> itemSet = new HashSet<>();
    private final List<TelebowESP.Entry4> entries = new ArrayList<>();
    private World world;
    private static final long timestamp3 = 6969552592124772351L;

    private String computeString(UUID uuid, long l) {
        TelebowESP.Entry5 entry5 = this.lookupMap4.get(uuid);
        return entry5 != null && l - entry5.timestamp <= 15000L ? entry5.text : "SHOP";
    }

    private void flushFriendThreats(long l) {
        Iterator it = this.lookupMap5.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            TelebowESP.Entry3 val = (TelebowESP.Entry3)entry.getValue();
            if (l >= val.timestamp) {
                it.remove();
                this.lookupMap6.put((String)entry.getKey(), l);
                String str = FormatHelper.format(this.kitsSetting.getText(), val.lookupMap);
                String str2 = val.text2;
                int count = val.count;
                String text = val.text;
                String text2 = val.text;
                mc.thePlayer.sendChatMessage("/msg " + text2 + " " + str + " telebowing on " + text + " (" + count + "hp) in " + str2);
            }
        }

        this.lookupMap6.entrySet().removeIf(entry2 -> l - entry2.getValue() > 5000L);
    }

    private BlockPos computeBlockPos(EntityArrow entity, List<Vec3> vecs) {
        return computeBlockPos(new Vec3(entity.posX, entity.posY, entity.posZ), entity.motionX, entity.motionY, entity.motionZ, vecs);
    }

    private void update() {
        if (!SomberHelper.checkState()) {
            ObserveAttemptHelper.instance.handleObject2(mc.theWorld);
            this.clear();
        } else {
            if (this.world != mc.theWorld) {
                this.clear();
                this.world = mc.theWorld;
            }

            long now = System.currentTimeMillis();
            if (now < this.timestamp && now >= this.timestamp2) {
                mc.thePlayer.playSound("note.pling", 1.0F, 1.5F);
                this.timestamp2 = now + 250L;
            }

            long totalWorldTime = mc.theWorld.getTotalWorldTime();
            this.entries.removeIf(entry4 -> now - entry4.timestamp > 4000L);
            this.lookupMap3.entrySet().removeIf(entry -> now - entry.getValue() > 1000L);
            HashSet set = new HashSet();
            if (!this.detectOthersSetting.isEnabled()) {
                this.clear();
            } else {
                for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                    if (playerEntity != mc.thePlayer) {
                        UUID uniqueID = playerEntity.getUniqueID();
                        this.handleEntityPlayer(playerEntity, uniqueID, now);
                        set.add(uniqueID);
                        if (!SomberHelper.matchesEntityPlayer2(playerEntity)) {
                            this.lookupMap.remove(uniqueID);
                            this.lookupMap2.remove(uniqueID);
                            this.lookupMap3.remove(uniqueID);
                            this.entries.removeIf(entry42 -> uniqueID.equals(entry42.uuid));
                            if (!playerEntity.isEntityAlive()) {
                                SomberHelper.handleEntityPlayer(playerEntity);
                            }
                        } else {
                            ItemStack heldItem = playerEntity.getHeldItem();
                            boolean flag = SomberHelper.matchesEntityPlayer(playerEntity) && playerEntity.isEating() && matchesItemStack(heldItem);
                            TelebowESP.Entry2 entry2 = this.lookupMap.get(uniqueID);
                            if (flag) {
                                if (entry2 == null) {
                                    entry2 = new TelebowESP.Entry2(totalWorldTime, matchesItemStack2(heldItem), TelebowTimer.computeInt2(heldItem), this.computeString(uniqueID, now));
                                    this.lookupMap.put(uniqueID, entry2);
                                }

                                entry2.handleEntry(new TelebowESP.Entry(playerEntity));
                                this.lookupMap2.remove(uniqueID);
                                if (this.liveAimPreviewSetting.isEnabled() && playerEntity.isSneaking() && !FreshHelper.matchesWorld3(mc.theWorld, playerEntity)) {
                                    ArrayList list = new ArrayList();
                                    BlockPos pos = computeBlockPos(
                                        new TelebowESP.Entry(playerEntity), entry2.flag ? 3.0F : computeFloat((int)Math.max(1L, totalWorldTime - entry2.timestamp)), list
                                    );
                                    if (pos != null) {
                                        this.lookupMap2
                                            .put(
                                                uniqueID,
                                                new TelebowESP.Entry4(
                                                    pos,
                                                    now,
                                                    list,
                                                    playerEntity.getName() + " aiming",
                                                    StringUtils.stripControlCodes(
                                                        FreshHelper.computeString(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, mc.theWorld)
                                                    )
                                                )
                                            );
                                    }
                                }
                            } else if (entry2 != null) {
                                this.lookupMap2.remove(uniqueID);
                                if (matchesItemStack(heldItem)
                                    && !this.lookupMap3.containsKey(uniqueID)
                                    && entry2.checkState()
                                    && computeFloat((int)(totalWorldTime - entry2.timestamp)) > 0.0F) {
                                    TelebowESP.Entry entry3 = entry2.entries.isEmpty() ? null : entry2.entries.get(entry2.entries.size() - 1);
                                    if (entry3 != null) {
                                        int n = (int)Math.max(1L, totalWorldTime - entry2.timestamp);
                                        ArrayList list2 = new ArrayList();
                                        BlockPos pos2 = computeBlockPos(entry3, entry2.flag ? 3.0F : computeFloat(n), list2);
                                        this.handleEntityPlayer(playerEntity, pos2, now, list2, entry2.count, entry2.text);
                                    }
                                }

                                this.lookupMap.remove(uniqueID);
                            }
                        }
                    }
                }

                this.lookupMap.keySet().retainAll(set);
                this.lookupMap2.keySet().retainAll(set);
                this.lookupMap4.keySet().retainAll(set);
                HashSet set2 = new HashSet();

                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityArrow entity2) {
                        set2.add(entity2.getEntityId());
                        if (this.itemSet.add(entity2.getEntityId())
                            && entity2.ticksExisted <= 3
                            && entity2.shootingEntity instanceof EntityPlayer
                            && entity2.shootingEntity != mc.thePlayer) {
                            EntityPlayer shootingEntity = (EntityPlayer)entity2.shootingEntity;
                            UUID uniqueID2 = shootingEntity.getUniqueID();
                            TelebowESP.Entry2 entry22 = this.lookupMap.get(uniqueID2);
                            if (!this.lookupMap3.containsKey(uniqueID2) && entry22 != null && entry22.checkState()) {
                                ArrayList list3 = new ArrayList();
                                this.handleEntityPlayer(shootingEntity, this.computeBlockPos(entity2, list3), now, list3, entry22.count, entry22.text);
                                this.lookupMap.remove(uniqueID2);
                                this.lookupMap2.remove(uniqueID2);
                            }
                        }
                    }
                }

                this.itemSet.retainAll(set2);
                this.flushFriendThreats(now);
            }
        }
    }

    private static boolean matchesItemStack3(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return false;
        }
        NBTTagCompound tagCompound = stack.getTagCompound();
        NBTTagCompound nbt = tagCompound.hasKey("ExtraAttributes", 10) ? tagCompound.getCompoundTag("ExtraAttributes") : tagCompound;
        return nbt.hasKey("CustomEnchants", 9) ? nbt.getTagList("CustomEnchants", 10).tagCount() > 0 : false;
    }

    private void handleBlockPos(BlockPos pos, float f, boolean flag, List<Vec3> vecs, String str, String str2) {
        double d = pos.getX() - mc.getRenderManager().viewerPosX;
        IBlockState blockState = mc.theWorld.getBlockState(pos.down());
        AxisAlignedBB collisionBoundingBox = blockState.getBlock().getCollisionBoundingBox(mc.theWorld, pos.down(), blockState);
        double d2 = (collisionBoundingBox == null ? pos.getY() : collisionBoundingBox.maxY) - mc.getRenderManager().viewerPosY;
        double d3 = pos.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB box = new AxisAlignedBB(d + 0.2, d2, d3 + 0.2, d + 0.8, d2 + 1.8, d3 + 0.8);
        this.handleList(vecs, f, flag);
        handleAxisAlignedBB(box, f, flag);

        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(d + 0.5, d2 + 1.35, d3 + 0.5);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
            float dist = (float)mc.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            float f2 = this.autoScaleLocationSetting.isEnabled() ? 1.0F + dist * 0.05F : 1.0F;
            GlStateManager.scale(-0.025F * f2, -0.025F * f2, 0.025F * f2);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.disableCull();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            int n = (int)timestamp3 + ((int)(f * 255.0F) << 24 & ~((int)timestamp3));
            computeFontRenderer(TelebowESP.class)
                .drawStringWithShadow(
                    str, -computeFontRenderer(TelebowESP.class).getStringWidth(str) / 2.0F, -computeFontRenderer(TelebowESP.class).FONT_HEIGHT, n
                );
            computeFontRenderer(TelebowESP.class)
                .drawStringWithShadow(str2, ~(computeFontRenderer(TelebowESP.class).getStringWidth(str2) - 1) / 2.0F, 0.0F, n);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.clear();
    }

    private static String computeString(String str) {
        String[] parts = new String[]{
            "VENOM",
            "MISERY",
            "SPITE",
            "NEEDLESS SUFFERING",
            "GRIM REAPER",
            "NOSTALGIA",
            "HEDGE FUND",
            "HEARTRIPPER",
            "SANGUISUGE",
            "LYCANTHROPY",
            "MIND ASSAULT",
            "GOLDEN HANDCUFFS"
        };
        String str2 = str.toUpperCase(Locale.ROOT);

        for (String str3 : parts) {
            if (str2.contains(str3)) {
                return str3;
            }
        }

        return null;
    }

    private static BlockPos computeBlockPos(TelebowESP.Entry entry, float f, List<Vec3> vecs) {
        if (f <= 0.0F) {
            return null;
        }
        float rad = (float)Math.toRadians(entry.factor);
        float rad2 = (float)Math.toRadians(entry.factor2);
        Vec3 vec = new Vec3(entry.amount - MathHelper.sin(rad) * 0.16, entry.amount2 + entry.factor3 - 0.1, entry.amount3 + MathHelper.cos(rad) * 0.16);
        double d = -MathHelper.sin(rad) * MathHelper.cos(rad2) * f;
        double d2 = -MathHelper.sin(rad2) * f;
        double d3 = MathHelper.cos(rad) * MathHelper.cos(rad2) * f;
        return computeBlockPos(vec, d, d2, d3, vecs);
    }

    private void clear() {
        this.timestamp = this.timestamp2 = 0L;
        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.entries.clear();
        this.lookupMap3.clear();
        this.itemSet.clear();
        this.lookupMap4.clear();
        this.lookupMap5.clear();
        this.lookupMap6.clear();
        this.world = null;
    }

    public static BlockPos computeBlockPos(EntityArrow entity) {
        return mc.theWorld == null
            ? null
            : computeBlockPos(new Vec3(entity.posX, entity.posY, entity.posZ), entity.motionX, entity.motionY, entity.motionZ, new ArrayList<>());
    }

    private void handleEntityPlayer(EntityPlayer player, UUID uuid, long l) {
        ItemStack stack = player.inventory == null ? null : player.inventory.armorInventory[1];
        if (stack == null) {
            this.lookupMap4.put(uuid, new TelebowESP.Entry5("SHOP", l));
        } else if (stack.getItem() != Items.diamond_leggings) {
            this.lookupMap4.put(uuid, new TelebowESP.Entry5(computeString(stack), l));
        }
    }

    private static void handleBlockPos(BlockPos pos, String str, boolean flag, boolean flag2) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.depthMask(false);

            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    BlockPos pos2 = pos.add(i, 0, j);
                    double d = computeDouble(pos2, str);
                    boolean finite = Double.isFinite(d);
                    boolean flag3 = flag && finite && (i != 0 || j != 0 || !flag2);
                    double d2 = pos2.getX() - mc.getRenderManager().viewerPosX;
                    double d3 = (finite ? d : pos2.getY()) + 0.025 - mc.getRenderManager().viewerPosY;
                    double d4 = pos2.getZ() - mc.getRenderManager().viewerPosZ;
                    GlStateManager.color(flag3 ? 0.15F : 1.0F, flag3 ? 1.0F : 0.15F, 0.2F, 0.38F);
                    GL11.glBegin(GL11.GL_QUADS);
                    GL11.glVertex3d(d2 + 0.04, d3, d4 + 0.04);
                    GL11.glVertex3d(d2 + 0.96, d3, d4 + 0.04);
                    GL11.glVertex3d(d2 + 0.96, d3, d4 + 0.96);
                    GL11.glVertex3d(d2 + 0.04, d3, d4 + 0.96);
                    GL11.glEnd();
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.clear();
            } else {
                if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    this.update();
                }

                if (event instanceof EventRender3D) {
                    this.render();
                }
            }
        }
    }

    private void queueFriendThreats(BlockPos pos, EntityPlayer player, String str, long l) {
        if (this.warnLowHealthFriendsSetting.isEnabled() && mc.theWorld != null) {
            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity != mc.thePlayer
                    && playerEntity.isEntityAlive()
                    && DefaultHelper.matchesString(playerEntity.getName())
                    && !(playerEntity.getHealth() <= 0.0F)
                    && !(playerEntity.getHealth() > this.friendHealthThresholdSetting.computeDouble())) {
                    double d = pos.getX() + 0.5 - playerEntity.posX;
                    double d2 = pos.getY() + 1.0 - playerEntity.posY;
                    double d3 = pos.getZ() + 0.5 - playerEntity.posZ;
                    double dist = Math.sqrt(d * d + d2 * d2 + d3 * d3);
                    if (!(dist > this.warningWithinSetting.computeDouble())) {
                        String str2 = playerEntity.getName().toLowerCase(Locale.ROOT);
                        long orDefault = this.lookupMap6.getOrDefault(str2, 0L);
                        if (l - orDefault >= 1500L) {
                            TelebowESP.Entry3 entry3 = this.lookupMap5
                                .computeIfAbsent(
                                    str2,
                                    str3 -> new TelebowESP.Entry3(
                                        playerEntity.getName(),
                                        Math.round(playerEntity.getHealth()),
                                        StringUtils.stripControlCodes(FreshHelper.computeString(playerEntity, mc.theWorld)),
                                        l + 200L
                                    )
                                );
                            entry3.lookupMap.putIfAbsent(player.getName(), str);
                        }
                    }
                }
            }
        }
    }

    public static BlockPos computeBlockPos(EntityPlayer player, int n) {
        return mc.theWorld != null && player != null
            ? computeBlockPos(new TelebowESP.Entry(player), matchesItemStack2(player.getHeldItem()) ? 3.0F : computeFloat(n), new ArrayList<>())
            : null;
    }

    private static boolean matchesItemStack2(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (!tagCompound.hasKey("display", 10)) {
                return false;
            }
            NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
            if (!compoundTag.hasKey("Lore", 9)) {
                return false;
            }
            NBTTagList tagList = compoundTag.getTagList("Lore", 8);

            for (int i = 0; i < tagList.tagCount(); i++) {
                String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(tagList.getStringTagAt(i));
                if (textWithoutFormattingCodes != null && textWithoutFormattingCodes.contains("Mega Longbow")) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    private void handleEntityPlayer(EntityPlayer player, BlockPos pos, long l, List<Vec3> vecs, int n, String str) {
        if (SomberHelper.matchesEntityPlayer(player)) {
            SomberHelper.handleEntityPlayer(player, n, pos, l);
            if (ObserveAttemptHelper.instance.matchesObject(mc.theWorld, player.getUniqueID(), l)) {
                if (this.lowHealthGuildNotifySetting.isEnabled()
                    && WHelper.matchesString(player.getName())
                    && !DefaultHelper.matchesString(player.getName())
                    && player.getHealth() <= this.lowHealthThresholdSetting.computeDouble()) {
                    String str2;
                    if (this.includeLocationSetting.isEnabled()) {
                        str2 = StringUtils.stripControlCodes(FreshHelper.computeString(player, mc.theWorld));
                        int x = player.getPosition().getX();
                        int z = player.getPosition().getZ();
                        int y = player.getPosition().getY();
                        int n2 = x;
                        String str3 = str2;
                        str2 = " at " + str3 + " (X: " + n2 + " Y: " + y + " Z: " + z + ")";
                    } else {
                        str2 = "";
                    }

                    String str4 = str2;
                    EntityPlayerSP player2 = mc.thePlayer;
                    int n3 = Math.round(player.getHealth());
                    String name = player.getName();
                    player2.sendChatMessage("/gc " + name + " is telebowing! (" + n3 + "hp)" + str4);
                }

                if (pos != null) {
                    UUID uniqueID = player.getUniqueID();
                    this.lookupMap3.put(uniqueID, l);
                    String str5 = StringUtils.stripControlCodes(FreshHelper.computeString(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, mc.theWorld));
                    String str6 = StringUtils.stripControlCodes(player.getDisplayName().getFormattedText()) + " telebowing";
                    TelebowESP.Entry4 entry4 = new TelebowESP.Entry4(pos, l, vecs, str6, str5);
                    entry4.uuid = uniqueID;
                    this.entries.add(entry4);
                    if (WHelper.matchesString(player.getName()) && !DefaultHelper.matchesString(player.getName())) {
                        double d = pos.getX() + 0.5 - mc.thePlayer.posX;
                        double d2 = pos.getY() + 1.0 - mc.thePlayer.posY;
                        double d3 = pos.getZ() + 0.5 - mc.thePlayer.posZ;
                        double dist = Math.sqrt(d * d + d2 * d2 + d3 * d3);
                        this.queueFriendThreats(pos, player, str, l);
                        if (dist <= this.warningWithinSetting.computeDouble()) {
                            ModuleEnabledHandler.handleString5("Telebow nearby", player.getName() + " telebowing within " + Math.round(dist) + " blocks!", 3000L);
                            this.timestamp = l + 3000L;
                            this.timestamp2 = l;
                        }

                        if (!this.bothSetting.matchesString2("Off")) {
                            String name2 = player.getName();
                            long l2 = Math.round(dist);
                            String str7 = name2;
                            String str8 = "KOS " + str7 + " telebowing " + l2 + " blocks away!";
                            if (this.bothSetting.matchesString2("Chat") || this.bothSetting.matchesString2("Both")) {
                                MessageUtils.x(MessageUtils.text14 + str8);
                            }

                            if (this.bothSetting.matchesString2("Notification") || this.bothSetting.matchesString2("Both")) {
                                String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(
                                    FreshHelper.computeString(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, mc.theWorld)
                                );
                                String name3 = player.getName();
                                long l3 = Math.round(dist);
                                ModuleEnabledHandler.handleString5("Telebow", name3 + " telebowing to " + textWithoutFormattingCodes + " (" + l3 + " blocks away)!", 3500L);
                            }
                        }
                    }
                }
            }
        }
    }

    private static double computeDouble(BlockPos pos, String str) {
        if (mc.theWorld.isBlockLoaded(pos.down()) && mc.theWorld.isBlockLoaded(pos.up()) && mc.theWorld.getWorldBorder().contains(pos)) {
            IBlockState blockState = mc.theWorld.getBlockState(pos.down());
            if (blockState.getBlock() == Blocks.barrier) {
                return Double.NaN;
            }
            AxisAlignedBB collisionBoundingBox = blockState.getBlock().getCollisionBoundingBox(mc.theWorld, pos.down(), blockState);
            if (collisionBoundingBox == null) {
                return Double.NaN;
            }
            double d = pos.getX() + 0.5;
            double maxY = collisionBoundingBox.maxY;
            double d2 = pos.getZ() + 0.5;
            if (FreshHelper.matchesString(str, d, maxY, d2)) {
                return Double.NaN;
            }
            AxisAlignedBB box = new AxisAlignedBB(d - 0.3, maxY, d2 - 0.3, d + 0.3, maxY + 1.8, d2 + 0.3);
            ArrayList list = new ArrayList();

            for (int i = MathHelper.floor_double(maxY) - 1; i <= MathHelper.floor_double(box.maxY); i++) {
                BlockPos pos2 = new BlockPos(pos.getX(), i, pos.getZ());
                IBlockState blockState2 = mc.theWorld.getBlockState(pos2);
                blockState2.getBlock().addCollisionBoxesToList(mc.theWorld, pos2, blockState2, box, list, mc.thePlayer);
                if (!list.isEmpty()) {
                    return Double.NaN;
                }
            }

            return maxY;
        }
        return Double.NaN;
    }

    public TelebowESP() {
        super("TelebowESP", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.bothSetting,
                this.lowHealthGuildNotifySetting,
                this.lowHealthThresholdSetting,
                this.includeLocationSetting,
                this.warnLowHealthFriendsSetting,
                this.friendHealthThresholdSetting,
                this.kitsSetting,
                this.autoScaleLocationSetting,
                this.warningWithinSetting,
                this.detectOthersSetting,
                this.liveAimPreviewSetting
            }
        );
    }

    private void handleList(List<Vec3> vecs, float f, boolean flag) {
        if (vecs.size() >= 2) {
            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.disableAlpha();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GL11.glLineWidth(2.5F);
                GL11.glBegin(GL11.GL_LINE_STRIP);

                for (int i = 0; i < vecs.size(); i++) {
                    float f2 = (float)i / (vecs.size() - 1);
                    GL11.glColor4f(flag ? 1.0F : 0.2F, flag ? 0.15F : 1.0F, 0.2F, f * (0.85F - f2 * 0.2F));
                    Vec3 vec = (Vec3)vecs.get(i);
                    GL11.glVertex3d(
                        vec.xCoord - mc.getRenderManager().viewerPosX,
                        vec.yCoord - mc.getRenderManager().viewerPosY,
                        vec.zCoord - mc.getRenderManager().viewerPosZ
                    );
                }

                GL11.glEnd();
            }
        }
    }

    private static float computeFloat(int n) {
        float f = Math.min(1.0F, n / 20.0F);
        f = (f * f + 2.0F * f) / 3.0F;
        return f < 0.1F ? 0.0F : f * 3.0F;
    }

    private void render() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            ItemStack heldItem = mc.thePlayer.getHeldItem();
            if (mc.thePlayer.isSneaking() && mc.thePlayer.isUsingItem() && matchesItemStack(heldItem)) {
                int itemInUseDuration = mc.thePlayer.getItemInUseDuration();
                ArrayList list = new ArrayList();
                BlockPos pos = computeBlockPos(new TelebowESP.Entry(mc.thePlayer), matchesItemStack2(heldItem) ? 3.0F : computeFloat(itemInUseDuration), list);
                if (pos != null) {
                    String str = FreshHelper.computeString(mc.theWorld);
                    boolean flag = SomberHelper.matchesEntityPlayer(mc.thePlayer);
                    boolean flag2 = matchesList(list);
                    boolean flag3 = flag && !flag2 && Double.isFinite(computeDouble(pos, str));
                    handleBlockPos(pos, str, flag, flag2);
                    this.handleBlockPos(
                        pos,
                        1.0F,
                        !flag3,
                        list,
                        flag3 ? "Your Telebow" : "Telebow blocked",
                        StringUtils.stripControlCodes(FreshHelper.computeString(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, mc.theWorld))
                    );
                }
            }

            if (SomberHelper.checkState()) {
                long now = System.currentTimeMillis();
                if (this.detectOthersSetting.isEnabled() && this.liveAimPreviewSetting.isEnabled()) {
                    for (TelebowESP.Entry4 entry4 : this.lookupMap2.values()) {
                        this.handleBlockPos(entry4.blockPos, 1.0F, true, entry4.entries, entry4.text, entry4.text2);
                    }
                }

                for (TelebowESP.Entry4 entry42 : this.entries) {
                    float f = 1.0F - Math.min(1.0F, (float)(now - entry42.timestamp) / 4000.0F);
                    if (f > 0.0F) {
                        this.handleBlockPos(entry42.blockPos, f, false, entry42.entries, entry42.text, entry42.text2);
                    }
                }
            }
        }
    }

    private static boolean matchesList(List<Vec3> vecs) {
        if (vecs.size() < 2) {
            return false;
        }
        Vec3 vec = (Vec3)vecs.get(vecs.size() - 1);
        Vec3 vec2 = vec.subtract((Vec3)vecs.get(vecs.size() - 2)).normalize();

        for (int i = -1; i <= 7; i++) {
            BlockPos pos = new BlockPos(vec.addVector(vec2.xCoord * i * 0.25, vec2.yCoord * i * 0.25, vec2.zCoord * i * 0.25));
            if (mc.theWorld.getBlockState(pos).getBlock() == Blocks.barrier) {
                return true;
            }
        }

        return false;
    }

    private static String computeString(ItemStack stack) {
        if (stack == null) {
            return "SHOP";
        }
        if (stack.getItem() == Items.diamond_leggings) {
            return "SHOP";
        }
        String str = "";
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("display", 10)) {
            NBTTagCompound compoundTag = stack.getTagCompound().getCompoundTag("display");
            if (compoundTag.hasKey("Lore", 9)) {
                NBTTagList tagList = compoundTag.getTagList("Lore", 8);
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < tagList.tagCount(); i++) {
                    sb.append(' ').append(EnumChatFormatting.getTextWithoutFormattingCodes(tagList.getStringTagAt(i)));
                }

                str = sb.toString();
            }
        }

        String str2 = computeString(str);
        if (str2 != null) {
            return str2;
        }
        return !stack.isItemEnchanted() && !matchesItemStack3(stack) && !Pattern.compile("(?i)\\b(?:I{1,3}|IV|V|VI|1|2|3|4|5|6)\\b").matcher(str).find()
            ? "SHOP"
            : "SET";
    }

    private static void handleAxisAlignedBB(AxisAlignedBB box, float f, boolean flag) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GL11.glLineWidth(2.0F);
            GlStateManager.color(flag ? 1.0F : 0.2F, flag ? 0.15F : 1.0F, 0.2F, f);
            RenderGlobal.func_181561_a(box);
            GlStateManager.disableCull();
            GlStateManager.disableAlpha();
            double d = (box.minX + box.maxX) / 2.0;
            double d2 = (box.minZ + box.maxZ) / 2.0;
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(flag ? 1.0F : 0.2F, flag ? 0.15F : 1.0F, 0.2F, 0.18F * f);
            GL11.glVertex3d(d - 0.3, box.minY, d2);
            GL11.glVertex3d(d + 0.3, box.minY, d2);
            GL11.glVertex3d(d + 0.3, box.minY + 60.0, d2);
            GL11.glVertex3d(d - 0.3, box.minY + 60.0, d2);
            GL11.glVertex3d(d, box.minY, d2 - 0.3);
            GL11.glVertex3d(d, box.minY, d2 + 0.3);
            GL11.glVertex3d(d, box.minY + 60.0, d2 + 0.3);
            GL11.glVertex3d(d, box.minY + 60.0, d2 - 0.3);
            GL11.glEnd();
        }
    }

    private static boolean matchesItemStack(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBow) {
            if (stack.hasTagCompound()) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                NBTTagCompound nbt = tagCompound.hasKey("ExtraAttributes", 10) ? tagCompound.getCompoundTag("ExtraAttributes") : tagCompound;
                if (nbt.hasKey("CustomEnchants", 9)) {
                    NBTTagList tagList = nbt.getTagList("CustomEnchants", 10);

                    for (int i = 0; i < tagList.tagCount(); i++) {
                        if ("telebow".equalsIgnoreCase(tagList.getCompoundTagAt(i).getString("Key"))) {
                            return true;
                        }
                    }
                }

                if (tagCompound.hasKey("display", 10)) {
                    NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                    if (compoundTag.hasKey("Lore", 9)) {
                        NBTTagList tagList2 = compoundTag.getTagList("Lore", 8);

                        for (int j = 0; j < tagList2.tagCount(); j++) {
                            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(tagList2.getStringTagAt(j));
                            if (textWithoutFormattingCodes != null && textWithoutFormattingCodes.toLowerCase(Locale.ROOT).contains("telebow")) {
                                return true;
                            }
                        }
                    }
                }
            }

            return stack.hasDisplayName()
                && EnumChatFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()).toLowerCase(Locale.ROOT).contains("telebow");
        }
        return false;
    }

    private static BlockPos computeBlockPos(Vec3 vec, double d, double d2, double d3, List<Vec3> vecs) {
        Vec3 vec2 = vec;
        vecs.add(vec);

        for (int i = 0; i < 300; i++) {
            Vec3 vec3 = vec2.addVector(d, d2, d3);
            MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(vec2, vec3, false, true, false);
            if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK) {
                vecs.add(hit.hitVec);
                BlockPos blockPos = hit.getBlockPos();
                BlockPos pos = blockPos;

                for (int j = 0; j < 8; j++) {
                    if (!mc.theWorld.getBlockState(pos.up()).getBlock().getMaterial().blocksMovement()) {
                        return pos.up();
                    }

                    pos = pos.up();
                }

                return blockPos.up();
            }

            vecs.add(vec3);
            vec2 = vec3;
            d *= 0.99;
            d2 = d2 * 0.99 - 0.05;
            d3 *= 0.99;
            if (vec3.yCoord < 0.0) {
                break;
            }
        }

        return null;
    }

    public static final class Entry {
        public final double amount;
        public final double amount2;
        public final double amount3;
        public final float factor;
        public final float factor2;
        public final float factor3;
        public final boolean flag;

        public Entry(double amount, double amount2, double amount3, float factor, float f, float f2, boolean flag) {
            this.amount = amount;
            this.amount2 = amount2;
            this.amount3 = amount3;
            this.factor = factor;
            this.factor2 = f;
            this.factor3 = f2;
            this.flag = flag;
        }

        public Entry(EntityPlayer player) {
            this(player.posX, player.posY, player.posZ, player.rotationYawHead, player.rotationPitch, player.getEyeHeight(), player.isSneaking());
        }
    }

    public static final class Entry2 {
        public final long timestamp;
        public final boolean flag;
        public final int count;
        public final String text;
        public final List<TelebowESP.Entry> entries = new ArrayList<>();

        public TelebowESP.Entry computeEntry() {
            if (this.entries.isEmpty()) {
                return null;
            }
            int n = Math.max(0, this.entries.size() - 5);
            int n2 = this.entries.size() > 1 ? this.entries.size() - 1 : this.entries.size();
            double d = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            double d4 = 0.0;
            double d5 = 0.0;
            double d6 = 0.0;
            float f = this.entries.get(n2 - 1).factor3;

            for (int i = n; i < n2; i++) {
                TelebowESP.Entry entry = this.entries.get(i);
                d += Math.sin(Math.toRadians(entry.factor));
                d2 += Math.cos(Math.toRadians(entry.factor));
                d3 += entry.factor2;
                d4 += entry.amount;
                d5 += entry.amount2;
                d6 += entry.amount3;
            }

            int n3 = n2 - n;
            return new TelebowESP.Entry(
                d4 / n3, d5 / n3, d6 / n3, (float)Math.toDegrees(Math.atan2(d, d2)), (float)(d3 / n3), f, false
            );
        }

        public Entry2(long timestamp, boolean flag, int count, String text) {
            this.timestamp = timestamp;
            this.flag = flag;
            this.count = count;
            this.text = text;
        }

        public boolean checkState() {
            for (int i = this.entries.size() - 1; i >= Math.max(0, this.entries.size() - 5); i--) {
                if (this.entries.get(i).flag) {
                    return true;
                }
            }

            return false;
        }

        public void handleEntry(TelebowESP.Entry entry) {
            this.entries.add(entry);
            if (this.entries.size() > 10) {
                this.entries.remove(0);
            }
        }
    }

    public static final class Entry3 {
        public final String text;
        public final String text2;
        public final int count;
        public final long timestamp;
        public final Map<String, String> lookupMap = new LinkedHashMap<>();

        public Entry3(String text, int count, String str, long timestamp) {
            this.text = text;
            this.count = count;
            this.text2 = str;
            this.timestamp = timestamp;
        }
    }

    public static final class Entry4 {
        public UUID uuid;
        public final BlockPos blockPos;
        public final long timestamp;
        public final List<Vec3> entries;
        public final String text;
        public final String text2;

        public Entry4(BlockPos blockPos, long timestamp, List<Vec3> entries, String text, String str) {
            this.blockPos = blockPos;
            this.timestamp = timestamp;
            this.entries = entries;
            this.text = text;
            this.text2 = str;
        }
    }

    public static final class Entry5 {
        public final String text;
        public final long timestamp;

        public Entry5(String text, long timestamp) {
            this.text = text;
            this.timestamp = timestamp;
        }
    }
}
