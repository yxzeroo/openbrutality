package org.brutality.module.impl.combat;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent2;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public class BackTrack extends Module {
    public static BackTrack instance;
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 150.0, 50.0, 400.0, 10.0);
    private final ModeSetting latencySetting = new ModeSetting("Mode", this, "Fixed", Arrays.asList("Fixed", "Latency"));
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 3.0, 2.0, 4.5, 0.1);
    private final BooleanSetting ignoreTeammatesSetting = new BooleanSetting("Ignore teammates", this, true);
    private final BooleanSetting ignoreFriendsSetting = new BooleanSetting("Ignore friends", this, true);
    private final BooleanSetting killAuraOnlySetting = new BooleanSetting("KillAura only", this, false);
    private final BooleanSetting espSetting = new BooleanSetting("ESP", this, true);
    private final BooleanSetting showTrailSetting = new BooleanSetting("Show trail", this, true);
    private final ColorSetting espColorSetting = new ColorSetting("ESP Color", this, 0.0F, 1.0F, 1.0F, 0.6F);
    private final Map<Integer, Deque<BackTrack.Entry>> lookupMap = new HashMap<>();

    public BackTrack() {
        super("BackTrack", 0, Category.COMBAT);
        this.handleSettingArray(
            new Setting[]{
                this.latencySetting,
                this.delaySetting,
                this.rangeSetting,
                this.ignoreTeammatesSetting,
                this.ignoreFriendsSetting,
                this.killAuraOnlySetting,
                this.espSetting,
                this.showTrailSetting,
                this.espColorSetting
            }
        );
        instance = this;
    }

    private void process() {
        if (GetCurrentScreenHelper.checkState2()) {
            if (KillAura.instance == null || !KillAura.instance.checkState44()) {
                if (!this.killAuraOnlySetting.isEnabled()) {
                    MovingObjectPosition objectMouseOver = mc.objectMouseOver;
                    if (objectMouseOver == null || objectMouseOver.typeOfHit != MovingObjectType.ENTITY || !(objectMouseOver.entityHit instanceof EntityPlayer)) {
                        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
                        Vec3 look = mc.thePlayer.getLook(1.0F);
                        double d = this.rangeSetting.computeDouble();
                        Vec3 vec = positionEyes.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
                        long now = System.currentTimeMillis();
                        long l = this.computeLong();
                        EntityPlayer player = null;
                        Vec3 vec2 = null;
                        long MAX_VALUE = Long.MAX_VALUE;

                        for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                            if (playerEntity != mc.thePlayer
                                && !playerEntity.isDead
                                && playerEntity.isEntityAlive()
                                && (!this.ignoreTeammatesSetting.isEnabled() || !GetCurrentScreenHelper.matchesEntity2(playerEntity))
                                && (!this.ignoreFriendsSetting.isEnabled() || !DefaultHelper.matchesString(playerEntity.getName()))) {
                                Deque deque = this.lookupMap.get(playerEntity.getEntityId());
                                if (deque != null && !deque.isEmpty()) {
                                    var it = deque.descendingIterator();

                                    while (it.hasNext()) {
                                        BackTrack.Entry entry = (BackTrack.Entry)it.next();
                                        long l2 = now - entry.timestamp;
                                        if (l2 > l) {
                                            break;
                                        }

                                        MovingObjectPosition hit = entry.axisAlignedBB.calculateIntercept(positionEyes, vec);
                                        if (hit != null && hit.hitVec != null) {
                                            if (l2 < MAX_VALUE) {
                                                MAX_VALUE = l2;
                                                player = playerEntity;
                                                vec2 = hit.hitVec;
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if (player != null && vec2 != null) {
                            mc.objectMouseOver = new MovingObjectPosition(player, vec2);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap.clear();
    }

    public long computeLong() {
        return (long)this.delaySetting.computeDouble();
    }

    public Deque<BackTrack.Entry> computeDeque(int n) {
        return this.lookupMap.get(n);
    }

    public static double computeDouble(EntityLivingBase living) {
        if (instance != null && instance.checkState44() && living != null) {
            Deque deque = instance.lookupMap.get(living.getEntityId());
            if (deque != null && !deque.isEmpty()) {
                long now = System.currentTimeMillis();
                long l = instance.computeLong(living);
                float collisionBorderSize = living.getCollisionBorderSize();
                double MAX_VALUE = Double.MAX_VALUE;

                for (BackTrack.Entry entry : ((Iterable<BackTrack.Entry>)(Object)(deque))) {
                    if (now - entry.timestamp <= l) {
                        double d = computeDouble(entry.axisAlignedBB, collisionBorderSize);
                        if (d < MAX_VALUE) {
                            MAX_VALUE = d;
                        }
                    }
                }

                return MAX_VALUE == Double.MAX_VALUE ? -1.0 : MAX_VALUE;
            }
            return -1.0;
        }
        return -1.0;
    }

    private EntityLivingBase getAttackTarget() {
        if (KillAura.instance != null && KillAura.instance.checkState44() && KillAura.entityLivingBase2 != null) {
            return KillAura.entityLivingBase2;
        }
        if (this.killAuraOnlySetting.isEnabled()) {
            return null;
        }
        return mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityLivingBase
            ? (EntityLivingBase)mc.objectMouseOver.entityHit
            : null;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                this.recordPositions();
            } else if (event instanceof BasicEvent2) {
                this.process();
            } else if (event instanceof EventRender3D) {
                this.process3();
            }
        }
    }

    private long computeLong(EntityLivingBase living) {
        long l = this.computeLong();
        if (this.latencySetting.matchesString2("Latency") && living instanceof EntityPlayer && mc.getNetHandler() != null) {
            NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(((EntityPlayer)living).getUniqueID());
            return playerInfo == null ? l : Math.max(l, Math.min(400L, Math.max(0L, playerInfo.getResponseTime() / 2L)));
        }
        return l;
    }

    private void process3() {
        if (this.espSetting.isEnabled() && GetCurrentScreenHelper.checkState2()) {
            if (this.getAttackTarget() instanceof EntityPlayer player) {
                Deque deque = this.lookupMap.get(player.getEntityId());
                if (deque != null && !deque.isEmpty()) {
                    long now = System.currentTimeMillis();
                    long l = this.computeLong();
                    if (l > 0L) {
                        Color color = this.espColorSetting.computeColor();
                        double renderPosX = mc.getRenderManager().renderPosX;
                        double renderPosY = mc.getRenderManager().renderPosY;
                        double renderPosZ = mc.getRenderManager().renderPosZ;
                        GlStateManager.pushMatrix();
                        GlStateManager.enableBlend();
                        GlStateManager.disableTexture2D();
                        GlStateManager.disableDepth();
                        GlStateManager.depthMask(false);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        if (this.showTrailSetting.isEnabled()) {
                            for (BackTrack.Entry entry : ((Iterable<BackTrack.Entry>)(Object)(deque))) {
                                long l2 = now - entry.timestamp;
                                if (l2 <= l) {
                                    float f = 1.0F - (float)l2 / (float)l;
                                    float f2 = color.getAlpha() / 255.0F * 0.45F * f;
                                    GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, f2);
                                    ResetColorHelper.handleAxisAlignedBB2(entry.axisAlignedBB.offset(-renderPosX, -renderPosY, -renderPosZ));
                                }
                            }
                        }

                        BackTrack.Entry entry2 = (BackTrack.Entry)deque.peekLast();
                        if (entry2 != null && now - entry2.timestamp <= l) {
                            AxisAlignedBB box = entry2.axisAlignedBB.offset(-renderPosX, -renderPosY, -renderPosZ);
                            float f3 = color.getRed() / 255.0F;
                            float f4 = color.getGreen() / 255.0F;
                            float f5 = color.getBlue() / 255.0F;
                            float f6 = color.getAlpha() / 255.0F;
                            GL11.glColor4f(f3, f4, f5, f6 * 0.25F);
                            ResetColorHelper.handleAxisAlignedBB(box);
                            GL11.glLineWidth(2.0F);
                            GL11.glColor4f(f3, f4, f5, f6);
                            ResetColorHelper.handleAxisAlignedBB2(box);
                            GlStateManager.enableTexture2D();
                            GlStateManager.enableDepth();
                            GlStateManager.depthMask(true);
                            GlStateManager.disableBlend();
                            GlStateManager.popMatrix();
                        } else {
                            GlStateManager.enableTexture2D();
                            GlStateManager.enableDepth();
                            GlStateManager.depthMask(true);
                            GlStateManager.disableBlend();
                            GlStateManager.popMatrix();
                        }
                    }
                }
            }
        }
    }

    private void recordPositions() {
        if (!GetCurrentScreenHelper.checkState2()) {
            this.lookupMap.clear();
        } else {
            long now = System.currentTimeMillis();
            long l = this.computeLong(null);
            double d = (this.rangeSetting.computeDouble() + 1.5) * (this.rangeSetting.computeDouble() + 1.5);
            HashSet set = new HashSet();

            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity != mc.thePlayer
                    && !playerEntity.isDead
                    && playerEntity.isEntityAlive()
                    && (!this.ignoreTeammatesSetting.isEnabled() || !GetCurrentScreenHelper.matchesEntity2(playerEntity))
                    && (!this.ignoreFriendsSetting.isEnabled() || !DefaultHelper.matchesString(playerEntity.getName()))
                    && !(mc.thePlayer.getDistanceSqToEntity(playerEntity) > d)) {
                    int entityId = playerEntity.getEntityId();
                    set.add(entityId);
                    Deque deque = this.lookupMap.computeIfAbsent(entityId, integer -> new ArrayDeque<>());
                    deque.addLast(new BackTrack.Entry(now, playerEntity.getEntityBoundingBox()));

                    while (!deque.isEmpty() && now - ((BackTrack.Entry)deque.peekFirst()).timestamp > l) {
                        deque.pollFirst();
                    }
                }
            }

            this.lookupMap.keySet().removeIf(integer2 -> !set.contains(integer2));
        }
    }

    private static double computeDouble(AxisAlignedBB box, float f) {
        AxisAlignedBB box2 = box.expand(f, f, f);
        Vec3 positionEyes = GetCurrentScreenHelper.mc.thePlayer.getPositionEyes(1.0F);
        double d = Math.max(box2.minX, Math.min(positionEyes.xCoord, box2.maxX));
        double d2 = Math.max(box2.minY, Math.min(positionEyes.yCoord, box2.maxY));
        double d3 = Math.max(box2.minZ, Math.min(positionEyes.zCoord, box2.maxZ));
        double d4 = positionEyes.xCoord - d;
        double d5 = positionEyes.yCoord - d2;
        double d6 = positionEyes.zCoord - d3;
        return Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
    }

    public static final class Entry {
        public final long timestamp;
        public final AxisAlignedBB axisAlignedBB;

        public Entry(long timestamp, AxisAlignedBB axisAlignedBB) {
            this.timestamp = timestamp;
            this.axisAlignedBB = axisAlignedBB;
        }
    }
}
