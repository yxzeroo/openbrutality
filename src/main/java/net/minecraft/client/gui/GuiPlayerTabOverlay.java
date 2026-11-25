package net.minecraft.client.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.IScoreObjectiveCriteria.EnumRenderType;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings.GameType;
import org.brutality.module.impl.pit.hypixel.utility.PitDebugger;
import org.brutality.utils.BrutalityUserHelper;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.TruceManager;
import org.brutality.utils.WHelper;
import org.lwjgl.opengl.GL11;

public class GuiPlayerTabOverlay extends Gui {
    private static final Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new GuiPlayerTabOverlay.PlayerComparator());
    private final Minecraft mc;
    private final GuiIngame guiIngame;
    private IChatComponent footer;
    private IChatComponent header;
    private long lastTimeOpened;
    private boolean isBeingRendered;

    public GuiPlayerTabOverlay(Minecraft mcIn, GuiIngame guiIngameIn) {
        this.mc = mcIn;
        this.guiIngame = guiIngameIn;
    }

    public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        return BrutalityUserHelper.computeString2(networkPlayerInfoIn);
    }

    public void updatePlayerList(boolean willBeRendered) {
        if (willBeRendered && !this.isBeingRendered) {
            this.lastTimeOpened = Minecraft.getSystemTime();
        }

        this.isBeingRendered = willBeRendered;
    }

    public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
        NetHandlerPlayClient sendQueue = this.mc.thePlayer.sendQueue;
        var playerInfos = field_175252_a.sortedCopy(sendQueue.getPlayerInfoMap());
        int size = playerInfos.size();
        int n = 0;
        int n2 = 0;

        for (NetworkPlayerInfo playerInfo : playerInfos) {
            int textWidth = this.mc.fontRendererObj.getStringWidth(this.getPlayerName(playerInfo));
            n = Math.max(n, textWidth);
            if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != EnumRenderType.HEARTS) {
                textWidth = this.mc.fontRendererObj.getStringWidth(" " + scoreboardIn.getValueFromObjective(playerInfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
                n2 = Math.max(n2, textWidth);
            }
        }

        var playerInfos2 = playerInfos.subList(0, Math.min(playerInfos.size(), 80));
        PitDebugger.handleList(playerInfos2, size);
        int size2 = playerInfos2.size();
        int n3 = size2;

        int n4;
        for (n4 = 1; n3 > 20; n3 = (size2 + n4 - 1) / n4) {
            n4++;
        }

        boolean flag = this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted();
        int n5;
        if (scoreObjectiveIn != null) {
            if (scoreObjectiveIn.getRenderType() == EnumRenderType.HEARTS) {
                n5 = 90;
            } else {
                n5 = n2;
            }
        } else {
            n5 = 0;
        }

        int n6 = Math.min(n4 * ((flag ? 9 : 0) + n + n5 + 13), width - 50) / n4;
        int n7 = width / 2 - (n6 * n4 + (n4 - 1) * 5) / 2;
        int n8 = 10;
        int n9 = n6 * n4 + (n4 - 1) * 5;
        List<String> list = null;
        List<String> list2 = null;
        if (this.header != null) {
            list = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);

            for (String str : list) {
                n9 = Math.max(n9, this.mc.fontRendererObj.getStringWidth(str));
            }
        }

        if (this.footer != null) {
            list2 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);

            for (String str2 : list2) {
                n9 = Math.max(n9, this.mc.fontRendererObj.getStringWidth(str2));
            }
        }

        String str3 = "" + EnumChatFormatting.GREEN + size + EnumChatFormatting.WHITE + " Online";
        int textWidth2 = this.mc.fontRendererObj.getStringWidth(str3);
        drawRect(width / 2 - n9 / 2 - 1, n8 - 11, width / 2 + n9 / 2 + 1, n8 - 1, Integer.MIN_VALUE);
        this.mc.fontRendererObj.drawStringWithShadow(str3, width / 2 - textWidth2 / 2, n8 - 9, -1);
        if (list != null) {
            drawRect(
                width / 2 - n9 / 2 - 1, n8 - 1, width / 2 + n9 / 2 + 1, n8 + list.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE
            );

            for (String str4 : ((Iterable<String>)(Object)(list))) {
                int textWidth3 = this.mc.fontRendererObj.getStringWidth(str4);
                this.mc.fontRendererObj.drawStringWithShadow(str4, width / 2 - textWidth3 / 2, n8, -1);
                n8 += this.mc.fontRendererObj.FONT_HEIGHT;
            }

            n8++;
        }

        drawRect(width / 2 - n9 / 2 - 1, n8 - 1, width / 2 + n9 / 2 + 1, n8 + n3 * 9, Integer.MIN_VALUE);

        for (int i = 0; i < size2; i++) {
            int n10 = i / n3;
            int n11 = i % n3;
            int n12 = n7 + n10 * n6 + n10 * 5;
            int n13 = n8 + n11 * 9;
            int n14 = 553648127;
            if (i < playerInfos2.size()) {
                NetworkPlayerInfo playerInfo2 = (NetworkPlayerInfo)playerInfos2.get(i);
                String name = playerInfo2.getGameProfile().getName();
                if (name.equals(this.mc.thePlayer.getGameProfile().getName())) {
                    Color YELLOW = Color.YELLOW;
                    n14 = -2147483648 | YELLOW.getRed() << 16 | YELLOW.getGreen() << 8 | YELLOW.getBlue();
                } else if (DefaultHelper.matchesString(name)) {
                    Color color = DefaultHelper.computeColor();
                    n14 = color.getAlpha() << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
                } else if (WHelper.matchesString(name)) {
                    Color color2 = WHelper.computeColor();
                    n14 = color2.getAlpha() << 24 | color2.getRed() << 16 | color2.getGreen() << 8 | color2.getBlue();
                } else if (TruceManager.matchesString(name)) {
                    n14 = 1852746480;
                }
            }

            drawRect(n12, n13, n12 + n6, n13 + 8, n14);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            if (i < playerInfos2.size()) {
                NetworkPlayerInfo playerInfo3 = (NetworkPlayerInfo)playerInfos2.get(i);
                var playerName = this.getPlayerName(playerInfo3);
                GameProfile gameProfile = playerInfo3.getGameProfile();
                if (flag) {
                    EntityPlayer playerEntityByUUID = this.mc.theWorld.getPlayerEntityByUUID(gameProfile.getId());
                    boolean flag2 = playerEntityByUUID != null
                        && playerEntityByUUID.isWearing(EnumPlayerModelParts.CAPE)
                        && (gameProfile.getName().equals("Dinnerbone") || gameProfile.getName().equals("Grumm"));
                    this.mc.getTextureManager().bindTexture(playerInfo3.getLocationSkin());
                    int n15 = 8 + (flag2 ? 8 : 0);
                    int n16 = 8 * (flag2 ? -1 : 1);
                    Gui.drawScaledCustomSizeModalRect(n12, n13, 8.0F, n15, 8, n16, 8, 8, 64.0F, 64.0F);
                    if (playerEntityByUUID != null && playerEntityByUUID.isWearing(EnumPlayerModelParts.HAT)) {
                        int n17 = 8 + (flag2 ? 8 : 0);
                        int n18 = 8 * (flag2 ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(n12, n13, 40.0F, n17, 8, n18, 8, 8, 64.0F, 64.0F);
                    }

                    n12 += 9;
                }

                if (playerInfo3.getGameType() == GameType.SPECTATOR) {
                    playerName = EnumChatFormatting.ITALIC + playerName;
                    this.mc.fontRendererObj.drawStringWithShadow((String)playerName, n12, n13, -1862270977);
                } else {
                    this.mc.fontRendererObj.drawStringWithShadow((String)playerName, n12, n13, -1);
                }

                if (scoreObjectiveIn != null && playerInfo3.getGameType() != GameType.SPECTATOR) {
                    int n19 = n12 + n + 1;
                    int n20 = n19 + n5;
                    if (n20 - n19 > 5) {
                        this.drawScoreboardValues(scoreObjectiveIn, n13, gameProfile.getName(), n19, n20, playerInfo3);
                    }
                }

                this.drawPing(n6, n12 - (flag ? 9 : 0), n13, playerInfo3);
            }
        }

        if (list2 != null) {
            int n21 = n8 + n3 * 9 + 1;
            drawRect(
                width / 2 - n9 / 2 - 1, n21 - 1, width / 2 + n9 / 2 + 1, n21 + list2.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE
            );

            for (String str5 : ((Iterable<String>)(Object)(list2))) {
                int textWidth4 = this.mc.fontRendererObj.getStringWidth(str5);
                this.mc.fontRendererObj.drawStringWithShadow(str5, width / 2 - textWidth4 / 2, n21, -1);
                n21 += this.mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    protected void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
        byte b = 0;
        byte b2 = 0;
        if (networkPlayerInfoIn.getResponseTime() < 0) {
            b2 = 5;
        } else if (networkPlayerInfoIn.getResponseTime() < 150) {
            b2 = 0;
        } else if (networkPlayerInfoIn.getResponseTime() < 300) {
            b2 = 1;
        } else if (networkPlayerInfoIn.getResponseTime() < 600) {
            b2 = 2;
        } else if (networkPlayerInfoIn.getResponseTime() < 1000) {
            b2 = 3;
        } else {
            b2 = 4;
        }

        this.zLevel += 100.0F;
        this.drawTexturedModalRect(p_175245_2_ + p_175245_1_ - 11, p_175245_3_, 0 + b * 10, 176 + b2 * 8, 10, 8);
        this.zLevel -= 100.0F;
    }

    private void drawScoreboardValues(ScoreObjective p_175247_1_, int p_175247_2_, String p_175247_3_, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo p_175247_6_) {
        int scorePoints = p_175247_1_.getScoreboard().getValueFromObjective(p_175247_3_, p_175247_1_).getScorePoints();
        if (p_175247_1_.getRenderType() == EnumRenderType.HEARTS) {
            this.mc.getTextureManager().bindTexture(icons);
            if (this.lastTimeOpened == p_175247_6_.func_178855_p()) {
                if (scorePoints < p_175247_6_.func_178835_l()) {
                    p_175247_6_.func_178846_a(Minecraft.getSystemTime());
                    p_175247_6_.func_178844_b(this.guiIngame.getUpdateCounter() + 20);
                } else if (scorePoints > p_175247_6_.func_178835_l()) {
                    p_175247_6_.func_178846_a(Minecraft.getSystemTime());
                    p_175247_6_.func_178844_b(this.guiIngame.getUpdateCounter() + 10);
                }
            }

            if (Minecraft.getSystemTime() - p_175247_6_.func_178847_n() > 1000L || this.lastTimeOpened != p_175247_6_.func_178855_p()) {
                p_175247_6_.func_178836_b(scorePoints);
                p_175247_6_.func_178857_c(scorePoints);
                p_175247_6_.func_178846_a(Minecraft.getSystemTime());
            }

            p_175247_6_.func_178843_c(this.lastTimeOpened);
            p_175247_6_.func_178836_b(scorePoints);
            int n = MathHelper.ceiling_float_int(Math.max(scorePoints, p_175247_6_.func_178860_m()) / 2.0F);
            int n2 = Math.max(MathHelper.ceiling_float_int(scorePoints / 2), Math.max(MathHelper.ceiling_float_int(p_175247_6_.func_178860_m() / 2), 10));
            boolean flag = p_175247_6_.func_178858_o() > this.guiIngame.getUpdateCounter()
                && (p_175247_6_.func_178858_o() - this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L;
            if (n > 0) {
                float f = Math.min((float)(p_175247_5_ - p_175247_4_ - 4) / n2, 9.0F);
                if (f > 3.0F) {
                    for (int i = n; i < n2; i++) {
                        this.drawTexturedModalRect(p_175247_4_ + i * f, p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                    }

                    for (int j = 0; j < n; j++) {
                        this.drawTexturedModalRect(p_175247_4_ + j * f, p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                        if (flag) {
                            if (j * 2 + 1 < p_175247_6_.func_178860_m()) {
                                this.drawTexturedModalRect(p_175247_4_ + j * f, p_175247_2_, 70, 0, 9, 9);
                            }

                            if (j * 2 + 1 == p_175247_6_.func_178860_m()) {
                                this.drawTexturedModalRect(p_175247_4_ + j * f, p_175247_2_, 79, 0, 9, 9);
                            }
                        }

                        if (j * 2 + 1 < scorePoints) {
                            this.drawTexturedModalRect(p_175247_4_ + j * f, p_175247_2_, j >= 10 ? 160 : 52, 0, 9, 9);
                        }

                        if (j * 2 + 1 == scorePoints) {
                            this.drawTexturedModalRect(p_175247_4_ + j * f, p_175247_2_, j >= 10 ? 169 : 61, 0, 9, 9);
                        }
                    }
                } else {
                    float f2 = MathHelper.clamp_float(scorePoints / 20.0F, 0.0F, 1.0F);
                    int n3 = (int)((1.0F - f2) * 255.0F) << 16 | (int)(f2 * 255.0F) << 8;
                    String str = String.valueOf(scorePoints / 2.0F);
                    if (p_175247_5_ - this.mc.fontRendererObj.getStringWidth(str + "hp") >= p_175247_4_) {
                        str = str + "hp";
                    }

                    this.mc.fontRendererObj.drawStringWithShadow(str, (p_175247_5_ + p_175247_4_) / 2 - this.mc.fontRendererObj.getStringWidth(str) / 2, p_175247_2_, n3);
                }
            }
        } else {
            String str2 = "" + EnumChatFormatting.YELLOW + scorePoints;
            this.mc.fontRendererObj.drawStringWithShadow(str2, p_175247_5_ - this.mc.fontRendererObj.getStringWidth(str2), p_175247_2_, 16777215);
        }
    }

    public void setFooter(IChatComponent footerIn) {
        this.footer = footerIn;
    }

    public void setHeader(IChatComponent headerIn) {
        this.header = headerIn;
    }

    public void func_181030_a() {
        this.header = null;
        this.footer = null;
    }

    static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
            ScorePlayerTeam playerTeam = p_compare_1_.getPlayerTeam();
            ScorePlayerTeam playerTeam2 = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start()
                .compareTrueFirst(p_compare_1_.getGameType() != GameType.SPECTATOR, p_compare_2_.getGameType() != GameType.SPECTATOR)
                .compare(playerTeam != null ? playerTeam.getRegisteredName() : "", playerTeam2 != null ? playerTeam2.getRegisteredName() : "")
                .compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName())
                .result();
        }
    }
}
