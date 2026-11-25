package org.brutality.module.impl.pit.hypixel.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class Robbery extends Module {
    private final PitCategory pitCategory = PitCategory.MAJOR_EVENT;
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 100.0, 0.0, 1080.0, 1.0);
    private final NumberSetting topPlayersSetting = new NumberSetting("Top Players", this, 5.0, 1.0, 20.0, 1.0);
    private final NumberSetting highlightGoldThresholdSetting = new NumberSetting("Highlight Gold Threshold", this, 10000.0, 1000.0, 50000.0, 1000.0);
    private final BooleanSetting espBoxesSetting = new BooleanSetting("ESP Boxes", this, true);
    private final BooleanSetting tracersSetting = new BooleanSetting("Tracers", this, true);
    private final BooleanSetting showGoldListSetting = new BooleanSetting("Show Gold List", this, true);
    private final BooleanSetting robberyEventOnlySetting = new BooleanSetting("Robbery Event Only", this, false);
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean flag = false;
    private final Map<String, Integer> lookupMap = new LinkedHashMap<>();
    private List<Entry<String, Integer>> entries = new ArrayList<>();
    private long timestamp = 0L;
    private static final long timestamp2 = 1000L;
    private static final Pattern pattern = Pattern.compile("(\\d[\\d,]*)g");

    private void process() {
        for (EntityPlayer player : EntityCache.getEntries()) {
            if (player != this.mc.thePlayer) {
                String text = player.getDisplayName().getFormattedText();
                String str = text.replaceAll("§[0-9a-fk-or]", "");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    String str2 = matcher.group(1).replace(",", "");

                    try {
                        int n = Integer.parseInt(str2);
                        this.lookupMap.put(player.getName(), n);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
    }

    private void onEventRender3D(EventRender3D event) {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            if (this.espBoxesSetting.isEnabled() || this.tracersSetting.isEnabled()) {
                if (!this.entries.isEmpty()) {
                    float factor = event.getFactor();
                    int n = (int)this.highlightGoldThresholdSetting.computeDouble();
                    int n2 = 0;

                    for (Entry entry : this.entries) {
                        if (n2 >= 3 || (Integer)entry.getValue() < n) {
                            break;
                        }

                        EntityPlayer playerEntityByName = this.mc.theWorld.getPlayerEntityByName((String)entry.getKey());
                        if (playerEntityByName != null && playerEntityByName != this.mc.thePlayer && !FreshHelper.matchesWorld3(this.mc.theWorld, playerEntityByName)) {
                            double d = playerEntityByName.lastTickPosX + (playerEntityByName.posX - playerEntityByName.lastTickPosX) * factor - this.mc.getRenderManager().viewerPosX;
                            double d2 = playerEntityByName.lastTickPosY + (playerEntityByName.posY - playerEntityByName.lastTickPosY) * factor - this.mc.getRenderManager().viewerPosY;
                            double d3 = playerEntityByName.lastTickPosZ + (playerEntityByName.posZ - playerEntityByName.lastTickPosZ) * factor - this.mc.getRenderManager().viewerPosZ;

                            Color color = switch (n2) {
                                case 0 -> new Color(255, 215, 0, 150);
                                case 1 -> new Color(192, 192, 192, 150);
                                case 2 -> new Color(205, 127, 50, 150);
                                default -> new Color(255, 215, 0, 100);
                            };
                            if (this.espBoxesSetting.isEnabled()) {
                                GlStateManager.pushMatrix();
                                GlStateManager.disableTexture2D();
                                GlStateManager.enableBlend();
                                GlStateManager.disableDepth();
                                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                                GL11.glLineWidth(2.0F);
                                GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
                                double d4 = playerEntityByName.width / 2.0;
                                double d5 = playerEntityByName.height + 0.1;
                                GL11.glBegin(GL11.GL_LINE_LOOP);
                                GL11.glVertex3d(d - d4, d2, d3 - d4);
                                GL11.glVertex3d(d - d4, d2, d3 + d4);
                                GL11.glVertex3d(d + d4, d2, d3 + d4);
                                GL11.glVertex3d(d + d4, d2, d3 - d4);
                                GL11.glEnd();
                                GL11.glBegin(GL11.GL_LINE_LOOP);
                                GL11.glVertex3d(d - d4, d2 + d5, d3 - d4);
                                GL11.glVertex3d(d - d4, d2 + d5, d3 + d4);
                                GL11.glVertex3d(d + d4, d2 + d5, d3 + d4);
                                GL11.glVertex3d(d + d4, d2 + d5, d3 - d4);
                                GL11.glEnd();
                                GL11.glBegin(GL11.GL_LINES);
                                GL11.glVertex3d(d - d4, d2, d3 - d4);
                                GL11.glVertex3d(d - d4, d2 + d5, d3 - d4);
                                GL11.glVertex3d(d + d4, d2, d3 - d4);
                                GL11.glVertex3d(d + d4, d2 + d5, d3 - d4);
                                GL11.glVertex3d(d + d4, d2, d3 + d4);
                                GL11.glVertex3d(d + d4, d2 + d5, d3 + d4);
                                GL11.glVertex3d(d - d4, d2, d3 + d4);
                                GL11.glVertex3d(d - d4, d2 + d5, d3 + d4);
                                GL11.glEnd();
                                GlStateManager.enableDepth();
                                GlStateManager.enableTexture2D();
                                GlStateManager.disableBlend();
                                GlStateManager.popMatrix();
                            }

                            if (this.tracersSetting.isEnabled()) {
                                GlStateManager.pushMatrix();
                                GlStateManager.disableTexture2D();
                                GlStateManager.enableBlend();
                                GlStateManager.disableDepth();
                                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                                GL11.glLineWidth(1.5F);
                                GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.8F);
                                GL11.glBegin(GL11.GL_LINES);
                                GL11.glVertex3d(0.0, this.mc.thePlayer.getEyeHeight(), 0.0);
                                GL11.glVertex3d(d, d2 + playerEntityByName.height / 2.0F, d3);
                                GL11.glEnd();
                                GlStateManager.enableDepth();
                                GlStateManager.enableTexture2D();
                                GlStateManager.disableBlend();
                                GlStateManager.popMatrix();
                            }

                            n2++;
                        }
                    }
                }
            }
        }
    }

    private String computeString(int n) {
        if (n >= 1000000) {
            return String.format("%.1fM", n / 1000000.0);
        }
        return n >= 1000 ? String.format("%.1fk", n / 1000.0) : String.valueOf(n);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap.clear();
        this.entries.clear();
        this.flag = false;
        HudDragHelper.handleObject(this);
    }

    private boolean checkState() {
        try {
            Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
            if (scoreboard == null) {
                return false;
            }

            ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
            if (objectiveInDisplaySlot == null) {
                return false;
            }

            for (Score score : scoreboard.getSortedScores(objectiveInDisplaySlot)) {
                String playerName = score.getPlayerName();
                String str = playerName.replaceAll("§[0-9a-fk-or]", "").toLowerCase();
                if (str.contains("robbery") || str.contains("event: robbery")) {
                    return true;
                }
            }

            String displayName = objectiveInDisplaySlot.getDisplayName();
            if (displayName != null) {
                String str2 = displayName.replaceAll("§[0-9a-fk-or]", "").toLowerCase();
                if (str2.contains("robbery")) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    @Override
    public void onUpdate() {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            long now = System.currentTimeMillis();
            if (now - this.timestamp >= 1000L) {
                this.timestamp = now;
                this.flag = this.checkState();
                if (this.robberyEventOnlySetting.isEnabled() && !this.flag) {
                    this.lookupMap.clear();
                    this.entries.clear();
                } else {
                    this.process4();
                    this.entries = this.lookupMap.entrySet().stream().sorted(Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toList());
                }
            }
        }
    }

    private void process3() {
        if (this.showGoldListSetting.isEnabled()) {
            if (!this.entries.isEmpty()) {
                FontRenderer fontRenderer = computeFontRenderer(Robbery.class);
                float f = (float)this.xPosSetting.computeDouble();
                float f2 = (float)this.yPosSetting.computeDouble();
                boolean flag = this.mc.currentScreen instanceof GuiChat;
                if (flag) {
                    int[] ints = HudDragHelper.computeIntArray();
                    int n = ints[0];
                    int n2 = ints[1];
                    short s = 150;
                    int n3 = Math.min(this.entries.size(), (int)this.topPlayersSetting.computeDouble()) * (fontRenderer.FONT_HEIGHT + 2) + fontRenderer.FONT_HEIGHT + 6;
                    if (Mouse.isButtonDown(0)) {
                        if (!HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(n, n2, f, f2, s, n3)) {
                            HudDragHelper.matchesObject(this, n, n2, (int)f, (int)f2);
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

                String str = this.flag ? "§6§lRobbery §7- Gold Holders" : "§e§lGold Holders";
                fontRenderer.drawStringWithShadow(str, f, f2, 16777215);
                float f3 = f2 + fontRenderer.FONT_HEIGHT + 4.0F;
                int n4 = 0;
                int n5 = (int)this.highlightGoldThresholdSetting.computeDouble();

                for (Entry entry : this.entries) {
                    if (n4 >= (int)this.topPlayersSetting.computeDouble()) {
                        break;
                    }

                    String key = (String)entry.getKey();
                    int val = (Integer)entry.getValue();
                    boolean flag2 = val >= n5;
                    String str2 = this.computeString(val);
                    int n6 = flag2 ? new Color(255, 215, 0).getRGB() : new Color(200, 200, 200).getRGB();
                    int n7 = flag2 ? new Color(255, 85, 85).getRGB() : new Color(170, 170, 170).getRGB();
                    String str3 = "§7" + (n4 + 1) + ". ";
                    fontRenderer.drawStringWithShadow(str3 + "§r" + key, f, f3, n6);
                    fontRenderer.drawStringWithShadow(" §6" + str2 + "g", f + fontRenderer.getStringWidth(str3 + key) + 2.0F, f3, n7);
                    f3 += fontRenderer.FONT_HEIGHT + 2;
                    n4++;
                }
            }
        }
    }

    public Robbery() {
        super("Robbery", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.xPosSetting,
                this.yPosSetting,
                this.topPlayersSetting,
                this.highlightGoldThresholdSetting,
                this.espBoxesSetting,
                this.tracersSetting,
                this.showGoldListSetting,
                this.robberyEventOnlySetting
            }
        );
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                this.onUpdate();
            } else if (event instanceof EventRender2D) {
                this.process3();
            } else if (event instanceof EventRender3D) {
                this.onEventRender3D((EventRender3D)event);
            }
        }
    }

    private String computeString(String str) {
        String str2 = str.replaceAll("\\d+\\.\\s*", "").trim();
        String[] parts = str2.split("[:\\s]+");
        if (parts.length > 0) {
            String str3 = parts[0].trim();
            if (str3.length() >= 2 && str3.length() <= 16 && str3.matches("[a-zA-Z0-9_]+")) {
                return str3;
            }
        }

        return null;
    }

    private void process4() {
        this.lookupMap.clear();

        try {
            Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
            if (scoreboard == null) {
                return;
            }

            ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
            if (objectiveInDisplaySlot == null) {
                return;
            }

            for (Score score : scoreboard.getSortedScores(objectiveInDisplaySlot)) {
                String playerName = score.getPlayerName();
                String str = playerName.replaceAll("§[0-9a-fk-or]", "");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    String str2 = matcher.group(1).replace(",", "");

                    try {
                        int n = Integer.parseInt(str2);
                        String str3 = this.computeString(str);
                        if (str3 != null && !str3.isEmpty()) {
                            this.lookupMap.put(str3, n);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            if (this.lookupMap.isEmpty()) {
                this.process();
            }
        } catch (Exception ignored2) {
        }
    }
}
