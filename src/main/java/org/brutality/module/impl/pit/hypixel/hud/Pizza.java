package org.brutality.module.impl.pit.hypixel.hud;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.hypixel.pit.RichestHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.opengl.GL11;

public class Pizza extends Module {
    private static final int count = -10164333;
    private static final int count2 = -13456;
    private static final int count3 = 5;
    private static final double amount = 16384.0;
    private static final RichestHelper.ImEntry imEntry = new RichestHelper.ImEntry("03:37", 25, "45/500");
    private final BooleanSetting espSetting = new BooleanSetting("ESP", this, true);
    private final BooleanSetting espLabelsSetting = new BooleanSetting("ESP Labels", this, true).computeBooleanSetting(this.espSetting::isEnabled);
    private final BooleanSetting topPlayerESPSetting = new BooleanSetting("Top Player ESP", this, false);
    private final BooleanSetting topPlayerTracersSetting = new BooleanSetting("Top Player Tracers", this, false);
    private final NumberSetting topPlayersSetting = new NumberSetting("Top Players", this, 5.0, 1.0, 10.0, 1.0)
        .computeNumberSetting(() -> this.topPlayerESPSetting.isEnabled() || this.topPlayerTracersSetting.isEnabled());
    private final BooleanSetting pizzaHUDSetting = new BooleanSetting("Pizza HUD", this, true);
    private final NumberSetting hudxSetting = new NumberSetting("HUD X", this, 10.0, 0.0, 1920.0, 1.0).computeNumberSetting(this.pizzaHUDSetting::isEnabled);
    private final NumberSetting hudySetting = new NumberSetting("HUD Y", this, 85.0, 0.0, 1080.0, 1.0).computeNumberSetting(this.pizzaHUDSetting::isEnabled);
    private World world;
    private EntityPlayer entityPlayer;
    private int count4;
    private RichestHelper.ImEntry imEntry2;
    private int count5;
    private List<Pizza.Il1IEntry> entries = List.of();
    private List<Pizza.I1IlEntry> entries2 = List.of();

    private static void handleInt(int n) {
        GlStateManager.color(((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F, (n >> 8 & 0xFF) / 255.0F, (n & 0xFF) / 255.0F, 0.9F);
    }

    private void handleImEntry(RichestHelper.ImEntry imEntry, int n) {
        FontRenderer fontRenderer = this.computeFontRenderer2();
        String[] parts = new String[]{
            "§f§lPizza Info:",
            "§fRemaining: §b" + imEntry.im(),
            "§fWallet: §a" + (imEntry.bJn() < 0 ? "—" : "$" + imEntry.bJn()),
            "§fDeliveries: §e" + imEntry.in(),
            "§fPizzas: §6" + n
        };
        int n2 = Math.max(12, fontRenderer.FONT_HEIGHT + 2);
        int n3 = 0;

        for (String str : parts) {
            n3 = Math.max(n3, fontRenderer.getStringWidth(str));
        }

        HudDragHelper.handleObject(this, this.hudxSetting, this.hudySetting, n3, parts.length * n2);
        int n4 = (int)this.hudxSetting.computeDouble();
        int n5 = (int)this.hudySetting.computeDouble();

        for (String str2 : parts) {
            fontRenderer.drawStringWithShadow(str2, n4, n5, -1);
            n5 += n2;
        }
    }

    private List<Pizza.Il1IEntry> findRecipients() {
        ArrayList<EntityVillager> entities = new ArrayList<>();
        ArrayList<Pizza.L1l1Entry> l1l1Entries = new ArrayList<>();
        LinkedHashMap<EntityVillager, Integer> map = new LinkedHashMap<>();

        for (Entity entity : this.world.loadedEntityList) {
            if (!entity.isDead && !(entity.getDistanceSqToEntity(this.entityPlayer) > 16384.0)) {
                if (entity instanceof EntityVillager entity4) {
                    entities.add(entity4);
                    int n = RichestHelper.computeInt3(entity4.getDisplayName().getUnformattedText());
                    if (n > 0) {
                        map.put(entity4, n);
                    }
                } else if (entity instanceof EntityArmorStand entity5 && entity5.hasCustomName()) {
                    int n2 = RichestHelper.computeInt3(entity5.getCustomNameTag());
                    if (n2 > 0) {
                        l1l1Entries.add(new Pizza.L1l1Entry(entity5, n2));
                    }
                }
            }
        }

        LinkedHashMap<EntityVillager, Double> map2 = new LinkedHashMap<>();

        for (Pizza.L1l1Entry l1l1Entry : l1l1Entries) {
            EntityVillager entity2 = null;
            double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;

            for (EntityVillager entity3 : entities) {
                double d = l1l1Entry.l1l1().posX - entity3.posX;
                double d2 = l1l1Entry.l1l1().posZ - entity3.posZ;
                double d3 = l1l1Entry.l1l1().posY - entity3.posY;
                double d4 = d * d + d2 * d2;
                if (!(d4 > 2.25) && !(d3 < -0.25) && !(d3 > 4.0)) {
                    double d5 = d3 - entity3.height;
                    double d6 = d4 + d5 * d5 * 0.15;
                    if (d6 < POSITIVE_INFINITY) {
                        POSITIVE_INFINITY = d6;
                        entity2 = entity3;
                    }
                }
            }

            if (entity2 != null && POSITIVE_INFINITY < map2.getOrDefault(entity2, Double.POSITIVE_INFINITY)) {
                map2.put(entity2, POSITIVE_INFINITY);
                map.put(entity2, l1l1Entry.bBQ());
            }
        }

        return ((java.util.Map<EntityVillager, Integer>)map).entrySet().stream().map(entry -> new Pizza.Il1IEntry((EntityVillager)entry.getKey(), (Integer)entry.getValue())).toList();
    }

    private void handleAxisAlignedBB(AxisAlignedBB box, String str, int n) {
        GlStateManager.pushMatrix();

        try {
            RenderManager renderManager = mc.getRenderManager();
            FontRenderer fontRenderer = this.computeFontRenderer2();
            double d = (box.minX + box.maxX) / 2.0;
            double d2 = box.maxY + 0.28;
            double d3 = (box.minZ + box.maxZ) / 2.0;
            float f = 0.025F * (1.0F + (float)Math.min(64.0, Math.sqrt(d * d + d2 * d2 + d3 * d3)) * 0.025F);
            GlStateManager.translate(d, d2, d3);
            GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f, -f, f);
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            fontRenderer.drawStringWithShadow(str, -fontRenderer.getStringWidth(str) / 2.0F, 0.0F, n);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static void handleAxisAlignedBB(AxisAlignedBB box, int n) {
        GlStateManager.disableTexture2D();
        handleInt(n);
        RenderGlobal.func_181561_a(box);
    }

    private void handleFloat(float f) {
        boolean flag = this.espSetting.isEnabled() && !this.entries.isEmpty();
        boolean flag2 = this.topPlayerESPSetting.isEnabled() && !this.entries2.isEmpty();
        boolean flag3 = this.topPlayerTracersSetting.isEnabled() && !this.entries2.isEmpty();
        if (flag || flag2 || flag3) {
            RenderManager renderManager = mc.getRenderManager();
            Vec3 pos = ActiveRenderInfo.getPosition();

            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.disableLighting();
                GlStateManager.disableFog();
                GlStateManager.disableDepth();
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GL11.glLineWidth(1.5F);
                if (flag) {
                    for (Pizza.Il1IEntry il1IEntry : this.entries) {
                        EntityVillager entity = il1IEntry.Il1I();
                        if (entity.isEntityAlive()) {
                            AxisAlignedBB box = computeAxisAlignedBB(entity, f, renderManager.viewerPosX, renderManager.viewerPosY, renderManager.viewerPosZ);
                            handleAxisAlignedBB(box, -10164333);
                            if (this.espLabelsSetting.isEnabled()) {
                                this.handleAxisAlignedBB(box, "PIZZA! $" + il1IEntry.bBS(), -10164333);
                            }
                        }
                    }
                }

                if (flag2 || flag3) {
                    for (Pizza.I1IlEntry i1IlEntry : this.entries2) {
                        EntityPlayer player = i1IlEntry.I1Il();
                        if (player.isEntityAlive() && !(player.getDistanceSqToEntity(this.entityPlayer) > 16384.0)) {
                            AxisAlignedBB box2 = computeAxisAlignedBB(player, f, renderManager.viewerPosX, renderManager.viewerPosY, renderManager.viewerPosZ);
                            if (flag2) {
                                handleAxisAlignedBB(box2, -13456);
                                this.handleAxisAlignedBB(box2, "#" + i1IlEntry.rank() + " $" + i1IlEntry.bBR(), -13456);
                            }

                            if (flag3) {
                                GlStateManager.disableTexture2D();
                                handleInt(-13456);
                                GL11.glBegin(GL11.GL_LINES);
                                GL11.glVertex3d(pos.xCoord, pos.yCoord, pos.zCoord);
                                GL11.glVertex3d((box2.minX + box2.maxX) / 2.0, (box2.minY + box2.maxY) / 2.0, (box2.minZ + box2.maxZ) / 2.0);
                                GL11.glEnd();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.reset();
        super.onDisable();
    }

    private void reset() {
        this.world = null;
        this.entityPlayer = null;
        this.count4 = -5;
        this.imEntry2 = null;
        this.count5 = 0;
        this.entries = List.of();
        this.entries2 = List.of();
        HudDragHelper.handleObject(this);
    }

    private boolean checkState() {
        return this.imEntry2 != null && this.world == mc.theWorld && this.entityPlayer == mc.thePlayer;
    }

    private static AxisAlignedBB computeAxisAlignedBB(Entity entity, float f, double d, double d2, double d3) {
        return entity.getEntityBoundingBox()
            .expand(0.045, 0.045, 0.045)
            .offset(
                (entity.lastTickPosX - entity.posX) * (1.0F - f) - d,
                (entity.lastTickPosY - entity.posY) * (1.0F - f) - d2,
                (entity.lastTickPosZ - entity.posZ) * (1.0F - f) - d3
            );
    }

    private List<Pizza.I1IlEntry> computeList() {
        if (mc.getNetHandler() == null) {
            return List.of();
        }
        List list = new ArrayList();

        for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
            GameProfile gameProfile = playerInfo.getGameProfile();
            if (gameProfile.getId() != null && gameProfile.getName() != null) {
                int n = playerInfo.getDisplayName() == null ? -1 : RichestHelper.computeInt2(playerInfo.getDisplayName().getUnformattedText());
                if (n < 0) {
                    n = RichestHelper.computeInt2(ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), gameProfile.getName()));
                }

                if (n > 0) {
                    list.add(new RichestHelper.L1I1Entry(gameProfile.getId(), gameProfile.getName(), n));
                }
            }
        }

        List list2 = new ArrayList();
        int n2 = 0;

        for (RichestHelper.L1I1Entry l1I1Entry : ((Iterable<RichestHelper.L1I1Entry>)(Object)(RichestHelper.richest(list, (int)this.topPlayersSetting.computeDouble())))) {
            n2++;
            EntityPlayer playerEntityByUUID = this.world.getPlayerEntityByUUID(l1I1Entry.l1I1());
            if (playerEntityByUUID != null && playerEntityByUUID != this.entityPlayer && playerEntityByUUID.isEntityAlive() && !playerEntityByUUID.isSpectator()) {
                list2.add(new Pizza.I1IlEntry(playerEntityByUUID, n2, l1I1Entry.bJo()));
            }
        }

        return list2;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof RenderEvent4) {
            this.reset();
        } else if (event instanceof EventUpdate event2 && event2.getState() == EventState.PRE) {
            this.process();
        } else if (event instanceof EventRender2D && this.pizzaHUDSetting.isEnabled()) {
            if (this.checkState()) {
                this.handleImEntry(this.imEntry2, this.count5);
            } else if (EditableModulesHelper.isActive()) {
                this.handleImEntry(imEntry, 3);
            }
        } else if (event instanceof EventRender3D event3 && this.checkState()) {
            this.handleFloat(event3.getFactor());
        }
    }

    private void process() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (this.world != mc.theWorld || this.entityPlayer != mc.thePlayer) {
                this.reset();
                this.world = mc.theWorld;
                this.entityPlayer = mc.thePlayer;
            }

            int ticksExisted = this.entityPlayer.ticksExisted;
            if (ticksExisted < this.count4 || ticksExisted - this.count4 >= 5) {
                this.count4 = ticksExisted;
                this.imEntry2 = RichestHelper.computeImEntry(GetCurrentScreenHelper.computeList2());
                if (this.imEntry2 == null) {
                    this.count5 = 0;
                    this.entries = List.of();
                    this.entries2 = List.of();
                } else {
                    if (this.pizzaHUDSetting.isEnabled()) {
                        this.count5 = RichestHelper.computeInt(this.entityPlayer.inventory.getItemStack());

                        for (ItemStack stack : this.entityPlayer.inventory.mainInventory) {
                            this.count5 = this.count5 + RichestHelper.computeInt(stack);
                        }
                    }

                    this.entries = this.espSetting.isEnabled() ? this.findRecipients() : List.of();
                    this.entries2 = !this.topPlayerESPSetting.isEnabled() && !this.topPlayerTracersSetting.isEnabled() ? List.of() : this.computeList();
                }
            }
        } else {
            this.reset();
        }
    }

    public Pizza() {
        super("Pizza", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.espSetting,
                this.espLabelsSetting,
                this.topPlayerESPSetting,
                this.topPlayerTracersSetting,
                this.topPlayersSetting,
                this.pizzaHUDSetting,
                this.hudxSetting,
                this.hudySetting
            }
        );
    }

    public record I1IlEntry(EntityPlayer I1Il, int rank2, int bBR) {
        public int rank() {
            return this.rank2;
        }
    }

    public record Il1IEntry(EntityVillager Il1I, int bBS) {
    }

    public record L1l1Entry(EntityArmorStand l1l1, int bBQ) {
    }
}
