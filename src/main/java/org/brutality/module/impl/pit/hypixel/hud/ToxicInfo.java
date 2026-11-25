package org.brutality.module.impl.pit.hypixel.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ToxicInfo extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static ToxicInfo instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1200.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 100.0, 0.0, 1200.0, 1.0);
    public final BooleanSetting showForOthersSetting = new BooleanSetting("Show For Others", this, true);
    private final Map<String, List<Long>> lookupMap = new HashMap<>();
    private EntityLivingBase entityLivingBase;
    private int count;
    private final Map<String, Long> lookupMap2 = new HashMap<>();
    private static final Pattern pattern = Pattern.compile("Toxicity:\\s*(-?\\d+)%");

    public ToxicInfo() {
        super("Toxic Info", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting, this.showForOthersSetting});
        instance = this;
    }

    private int calculateToxicity(String str, long l) {
        var list = this.lookupMap.get(str);
        if (list != null && !list.isEmpty()) {
            list.removeIf(val -> l > val);
            if (list.isEmpty()) {
                this.lookupMap.remove(str);
                return 0;
            }
            return list.size() * 2;
        }
        return 0;
    }

    public EntityLivingBase computeEntityLivingBase() {
        if (this.mc.objectMouseOver != null
            && this.mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY
            && this.mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            this.entityLivingBase = (EntityLivingBase)this.mc.objectMouseOver.entityHit;
            this.count = 0;
            return (EntityLivingBase)this.mc.objectMouseOver.entityHit;
        }
        if (this.entityLivingBase != null) {
            if (++this.count <= 10) {
                return this.entityLivingBase;
            }

            this.entityLivingBase = null;
        }

        return null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap.clear();
        this.lookupMap2.clear();
        HudDragHelper.handleObject(this);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick) {
                EntityLivingBase living = this.computeEntityLivingBase();
                if (living != null && living instanceof EntityPlayer player2) {
                    int n = this.computeInt(this.mc.thePlayer);
                    if (n > 0 && player2.hurtTime >= 9) {
                        this.handleString(this.mc.thePlayer.getName(), player2.getName(), n);
                    }
                }

                if (this.showForOthersSetting.isEnabled()) {
                    for (EntityPlayer player : EntityCache.getEntries()) {
                        if (player != this.mc.thePlayer) {
                            int n2 = this.computeInt(player);
                            if (n2 != 0) {
                                EntityLivingBase living2 = this.computeEntityLivingBase(player);
                                if (living2 != null && living2 instanceof EntityPlayer player3 && player3.hurtTime >= 9) {
                                    this.handleString(player.getName(), player3.getName(), n2);
                                }
                            }
                        }
                    }
                }
            } else if (event instanceof GetEntityEvent getEntityEvent) {
                if (getEntityEvent.getEntity() instanceof EntityPlayer) {
                    EntityPlayer entity = (EntityPlayer)getEntityEvent.getEntity();
                    String name = entity.getName();
                    this.lookupMap.remove(name);
                    this.lookupMap2.entrySet().removeIf(entry -> entry.getKey().startsWith(name + "-") || entry.getKey().endsWith("-" + name));
                }
            } else if (event instanceof EventRender3D && this.showForOthersSetting.isEnabled()) {
                this.process();
            }
        }
    }

    private void applyToxicStack(String str, String str2, int n) {
        if (n != 0) {
            long now = System.currentTimeMillis();
            long l = now + 30000L;
            var list = this.lookupMap.computeIfAbsent(str2, str3 -> new ArrayList<>());
            list.removeIf(val -> now > val);
            int n2 = this.computeInt(n);
            if (list.size() < n2) {
                list.add(l);
            }
        }
    }

    private void process() {
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            long now = System.currentTimeMillis();

            for (EntityPlayer player : EntityCache.getEntries()) {
                if (player != this.mc.thePlayer) {
                    String name = player.getName();
                    int n = this.calculateToxicity(name, now);
                    if (n != 0) {
                        double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * this.mc.timer.renderPartialTicks;
                        double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * this.mc.timer.renderPartialTicks + player.height * 0.5;
                        double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * this.mc.timer.renderPartialTicks;
                        this.handleDouble(d, d2, d3, "-" + n + "%");
                    }
                }
            }
        }
    }

    private void handleDouble(double d, double d2, double d3, String str) {
        double d4 = d - this.mc.getRenderManager().viewerPosX;
        double d5 = d2 - this.mc.getRenderManager().viewerPosY;
        double d6 = d3 - this.mc.getRenderManager().viewerPosZ;
        double dist = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
        float f = Math.max(0.1F, (float)(dist * 0.015F));
        GL11.glPushMatrix();
        GL11.glTranslated(d4, d5, d6);
        GL11.glRotatef(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(this.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-f, -f, f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        int textWidth = computeFontRenderer(ToxicInfo.class).getStringWidth(str);
        computeFontRenderer(ToxicInfo.class).drawStringWithShadow(str, (~textWidth + 1) / 2, 0.0F, 16733525);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void handleString(String str, String str2, int n) {
        long now = System.currentTimeMillis();
        String str3 = str + "-" + str2;
        Long val = this.lookupMap2.get(str3);
        if (val == null || now - val >= 100L) {
            this.lookupMap2.put(str3, now);
            this.applyToxicStack(str, str2, n);
        }
    }

    private int computeInt(int n) {
        switch (n) {
            case 1:
                return 10;
            case 2:
                return 15;
            case 3:
                return 20;
            default:
                return 0;
        }
    }

    public int computeInt(String str) {
        return this.calculateToxicity(str, System.currentTimeMillis());
    }

    @Generated
    public static ToxicInfo getInstance() {
        return instance;
    }

    private int computeInt(EntityPlayer player) {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                if (tagCompound != null && tagCompound.hasKey("ExtraAttributes", 10)) {
                    NBTTagCompound compoundTag = tagCompound.getCompoundTag("ExtraAttributes");
                    if (compoundTag.hasKey("enchantments", 10)) {
                        NBTTagCompound compoundTag2 = compoundTag.getCompoundTag("enchantments");
                        if (compoundTag2.hasKey("really_toxic")) {
                            return compoundTag2.getInteger("really_toxic");
                        }
                    }
                }
            }
        }

        return 0;
    }

    private int computeInt() {
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
            if (scoreboard == null) {
                return 0;
            }
            ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
            if (objectiveInDisplaySlot == null) {
                return 0;
            }
            for (Score score : scoreboard.getSortedScores(objectiveInDisplaySlot)) {
                String playerName = score.getPlayerName();
                ScorePlayerTeam playersTeam = scoreboard.getPlayersTeam(playerName);
                String str = ScorePlayerTeam.formatPlayerName(playersTeam, playerName);
                String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(str);
                Matcher matcher = pattern.matcher(textWithoutFormattingCodes);
                if (matcher.find()) {
                    try {
                        return Integer.parseInt(matcher.group(1));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
            }

            return 0;
        }
        return 0;
    }

    @Override
    public void onRender() {
        if (this.checkState44()) {
            EntityPlayerSP player = this.mc.thePlayer;
            if (player != null) {
                float f = (float)this.xPosSetting.computeDouble();
                float f2 = (float)this.yPosSetting.computeDouble();
                int n = this.computeInt();
                boolean flag = this.mc.currentScreen instanceof GuiChat;
                if (n == 0 && !flag) {
                    HudDragHelper.handleObject(this);
                } else {
                    String str;
                    if (n != 0) {
                        EnumChatFormatting DARK_GREEN = EnumChatFormatting.DARK_GREEN;
                        str = EnumChatFormatting.WHITE + "Toxic: " + DARK_GREEN + n + "%";
                    } else {
                        str = EnumChatFormatting.WHITE + "Toxic: " + EnumChatFormatting.DARK_GREEN + "0%";
                    }

                    if (flag) {
                        this.handleFloat(f, f2, str);
                        f = (float)this.xPosSetting.computeDouble();
                        f2 = (float)this.yPosSetting.computeDouble();
                    }

                    computeFontRenderer(ToxicInfo.class).drawStringWithShadow(str, f, f2, 16777215);
                }
            }
        }
    }

    private void handleFloat(float f, float f2, String str) {
        int[] ints = HudDragHelper.computeIntArray();
        int n = ints[0];
        int n2 = ints[1];
        if (Mouse.isButtonDown(0)) {
            if (!HudDragHelper.matchesObject(this)) {
                int textWidth = computeFontRenderer(ToxicInfo.class).getStringWidth(str);
                int FONT_HEIGHT = computeFontRenderer(ToxicInfo.class).FONT_HEIGHT;
                if (HudDragHelper.matchesInt(n, n2, f, f2, textWidth, FONT_HEIGHT)) {
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
            }
        }
    }

    private EntityLivingBase computeEntityLivingBase(EntityPlayer player) {
        if (this.mc.theWorld == null) {
            return null;
        }
        double d = 4.0;

        for (EntityPlayer player2 : EntityCache.getEntries()) {
            if (player2 != player && player2 != this.mc.thePlayer) {
                double dist = player.getDistanceToEntity(player2);
                if (dist <= d) {
                    return player2;
                }
            }
        }

        return null;
    }
}
