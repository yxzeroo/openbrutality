package org.brutality.module.impl.pit.hypixel.automation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.IScoreObjectiveCriteria.EnumRenderType;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.WHelper;
import org.lwjgl.opengl.GL11;

public class CombatDetection extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting kosOnlySetting = new BooleanSetting("KOS Only", this, false);
    private final BooleanSetting gambleBombersOnlySetting = new BooleanSetting("Gamble Bombers Only", this, false);
    private final BooleanSetting announceChatSetting = new BooleanSetting("Announce Chat", this, false);
    private static final long timestamp = 15000L;
    private final Map<String, Long> lookupMap = new HashMap<>();
    private final Map<String, CombatDetection.VEntry> lookupMap2 = new HashMap<>();
    private Object object;
    private static final long timestamp2 = -8994096065435183787L;

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.object = null;
    }

    private void process() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            float renderPartialTicks = mc.timer.renderPartialTicks;
            long now = System.currentTimeMillis();

            for (Entry entry : this.lookupMap.entrySet()) {
                long l = (Long)entry.getValue() - now;
                if (l > 0L) {
                    EntityPlayer playerEntityByName = mc.theWorld.getPlayerEntityByName((String)entry.getKey());
                    if (playerEntityByName != null && !playerEntityByName.isDead) {
                        String str = String.format(Locale.ROOT, "%.1fs", l / 1000.0);
                        double d = playerEntityByName.lastTickPosX + (playerEntityByName.posX - playerEntityByName.lastTickPosX) * renderPartialTicks - mc.getRenderManager().viewerPosX;
                        double d2 = playerEntityByName.lastTickPosY + (playerEntityByName.posY - playerEntityByName.lastTickPosY) * renderPartialTicks - mc.getRenderManager().viewerPosY + playerEntityByName.height + 0.5;
                        double d3 = playerEntityByName.lastTickPosZ + (playerEntityByName.posZ - playerEntityByName.lastTickPosZ) * renderPartialTicks - mc.getRenderManager().viewerPosZ;
                        GL11.glPushMatrix();
                        GL11.glTranslated(d, d2, d3);
                        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
                        float f = 0.03F;
                        GL11.glScalef(-f, -f, f);
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        int textWidth = computeFontRenderer(CombatDetection.class).getStringWidth(str);
                        computeFontRenderer(CombatDetection.class).drawStringWithShadow(str, ~(textWidth - 1) / 2, 0.0F, (int)timestamp2);
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GL11.glPopMatrix();
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick) {
                if (((EventTick)event).getState() != EventState.PRE) {
                    return;
                }

                this.tick();
            } else if (event instanceof EventRender3D) {
                this.process();
            }
        }
    }

    private void tick() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            long now = System.currentTimeMillis();
            if (this.object != mc.theWorld) {
                this.lookupMap.clear();
                this.lookupMap2.clear();
                this.object = mc.theWorld;
            }

            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(2);
            if (objectiveInDisplaySlot != null && !matchesScoreObjective(objectiveInDisplaySlot)) {
                objectiveInDisplaySlot = null;
            }

            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity != mc.thePlayer && !playerEntity.isDead && playerEntity.isEntityAlive() && this.matchesEntityPlayer(playerEntity)) {
                    String name = playerEntity.getName();
                    Integer integer = objectiveInDisplaySlot != null && scoreboard.entityHasObjective(name, objectiveInDisplaySlot) ? scoreboard.getValueFromObjective(name, objectiveInDisplaySlot).getScorePoints() : null;
                    CombatDetection.VEntry vEntry = new CombatDetection.VEntry(playerEntity.getHealth(), integer);
                    CombatDetection.VEntry vEntry2 = this.lookupMap2.put(name, vEntry);
                    if (vEntry2 != null && (vEntry.v() < vEntry2.v() || integer != null && vEntry2.O0O0() != null && integer < vEntry2.O0O0())) {
                        boolean flag = this.lookupMap.containsKey(name) && this.lookupMap.get(name) > now;
                        this.lookupMap.put(name, now + 15000L);
                        if (!flag && this.announceChatSetting.isEnabled()) {
                            mc.thePlayer.sendChatMessage("/gc " + name + " tagged - 15s to spawn");
                        }
                    }
                }
            }

            Iterator it = this.lookupMap.entrySet().iterator();

            while (it.hasNext()) {
                Entry entry = (Entry)it.next();
                if ((Long)entry.getValue() < now) {
                    it.remove();
                    this.lookupMap2.remove(entry.getKey());
                }
            }
        }
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        return this.kosOnlySetting.isEnabled() && !WHelper.matchesString(player.getName())
            ? false
            : !this.gambleBombersOnlySetting.isEnabled() || !(player.getAbsorptionAmount() <= 0.0F);
    }

    private static boolean matchesScoreObjective(ScoreObjective objective) {
        if (objective.getRenderType() == EnumRenderType.HEARTS) {
            return true;
        }
        String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(objective.getDisplayName());
        if (textWithoutFormattingCodes == null) {
            return false;
        }
        textWithoutFormattingCodes = textWithoutFormattingCodes.trim();
        return textWithoutFormattingCodes.contains("♥") || textWithoutFormattingCodes.equalsIgnoreCase("health") || textWithoutFormattingCodes.equalsIgnoreCase("hp");
    }

    public CombatDetection() {
        super("Combat Detection", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.kosOnlySetting, this.gambleBombersOnlySetting, this.announceChatSetting});
    }

    public record VEntry(float v, Integer O0O0) {
    }
}
