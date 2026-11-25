package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Pattern;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.border.WorldBorder;
import net.optifine.CustomColors;
import org.brutality.Brutality;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.pit.hypixel.automation.FakeContractHUD;
import org.brutality.module.impl.pit.hypixel.hud.StreakInfo;
import org.brutality.module.impl.pit.hypixel.utility.PitDebugger;
import org.brutality.module.impl.visual.Hotbar;
import org.brutality.module.impl.visual.NickHider;
import org.brutality.module.impl.visual.Notifications;
import org.brutality.module.impl.visual.PlayerHUD;
import org.brutality.module.impl.visual.PostProcessing;
import org.brutality.notification.modern.ModernHandler2;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.chat.MessageCopiedHelper;
import org.brutality.utils.hypixel.LoadAvatarHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.performance.RecordMetricHelper;
import org.brutality.utils.render.LowLifeWarningHelper;
import org.brutality.utils.render.RenderHelper;
import org.lwjgl.opengl.GL11;

public class GuiIngame extends Gui {
    private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    private final Random rand = new Random();
    private final Minecraft mc;
    private final RenderItem itemRenderer;
    private StreakInfo streakInfo;
    private final GuiNewChat persistantChatGUI;
    private final GuiStreamIndicator streamIndicator;
    private int updateCounter;
    private String recordPlaying = "";
    private int recordPlayingUpFor;
    private boolean recordIsPlaying;
    public float prevVignetteBrightness = 1.0F;
    private int remainingHighlightTicks;
    private ItemStack highlightingItemStack;
    private final GuiOverlayDebug overlayDebug;
    private final GuiSpectator spectatorGui;
    private final GuiPlayerTabOverlay overlayPlayerList;
    private int field_175195_w;
    private String field_175201_x = "";
    private String field_175200_y = "";
    private int field_175199_z;
    private int field_175192_A;
    private int field_175193_B;
    private int playerHealth = 0;
    private int lastPlayerHealth = 0;
    private long lastSystemTime = 0L;
    private long healthUpdateCounter = 0L;
    private static final Pattern SCORE_FORMATTING = Pattern.compile("§.");
    private static final Pattern SCORE_NON_TEXT = Pattern.compile("[^\\p{L}\\p{N}\\p{P}\\p{Z}]");
    private static final Pattern SCORE_DATE = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{2,4}");
    private static final Pattern SCORE_LOBBY = Pattern.compile("\\b[A-Z]+\\d+[A-Z]*\\b");
    private static final Pattern SCORE_NUMBER = Pattern.compile("^\\d+$");

    public GuiIngame(Minecraft mcIn) {
        this.mc = mcIn;
        this.itemRenderer = mcIn.getRenderItem();
        this.overlayDebug = new GuiOverlayDebug(mcIn);
        this.spectatorGui = new GuiSpectator(mcIn);
        this.persistantChatGUI = new GuiNewChat(mcIn);
        this.streamIndicator = new GuiStreamIndicator(mcIn);
        this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
        this.func_175177_a();
    }

    public void func_175177_a() {
        this.field_175199_z = 10;
        this.field_175192_A = 70;
        this.field_175193_B = 20;
    }

    public void renderGameOverlay(float partialTicks) {
        RecordMetricHelper.process4();
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
        int scaledWidth = sr.getScaledWidth();
        int scaledHeight = sr.getScaledHeight();
        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();
        if (Config.isVignetteEnabled()) {
            this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), sr);
        } else {
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        }

        ItemStack stack = this.mc.thePlayer.inventory.armorItemInSlot(3);
        if (this.mc.gameSettings.thirdPersonView == 0 && stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            this.renderPumpkinOverlay(sr);
        }

        if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
            float f = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
            if (f > 0.0F) {
                this.func_180474_b(f, sr);
            }
        }

        if (Brutality.moduleManager.computeModule("PostProcessing") instanceof PostProcessing postProcessing) {
            postProcessing.process3();
        }

        if (this.mc.playerController.isSpectator()) {
            this.spectatorGui.renderTooltip(sr, partialTicks);
        } else {
            this.renderTooltip(sr, partialTicks);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
        GlStateManager.enableBlend();
        if (this.showCrosshair()) {
            GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.enableAlpha();
            this.drawTexturedModalRect(scaledWidth / 2 - 7, scaledHeight / 2 - 7, 0, 0, 16, 16);
        }

        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        this.mc.mcProfiler.startSection("bossHealth");
        this.renderBossHealth();
        this.mc.mcProfiler.endSection();
        if (this.mc.playerController.shouldDrawHUD()) {
            this.renderPlayerStats(sr);
        }

        GlStateManager.disableBlend();
        if (this.mc.thePlayer.getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int sleepTimer = this.mc.thePlayer.getSleepTimer();
            float f2 = sleepTimer / 100.0F;
            if (f2 > 1.0F) {
                f2 = 1.0F - (sleepTimer - 100) / 10.0F;
            }

            int n = (int)(220.0F * f2) << 24 | 1052704;
            drawRect(0, 0, scaledWidth, scaledHeight, n);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            this.mc.mcProfiler.endSection();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int n2 = scaledWidth / 2 - 91;
        if (this.mc.thePlayer.isRidingHorse()) {
            this.renderHorseJumpBar(sr, n2);
        } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
            this.renderExpBar(sr, n2);
        }

        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            this.func_181551_a(sr);
        } else if (this.mc.thePlayer.isSpectator()) {
            this.spectatorGui.func_175263_a(sr);
        }

        if (this.mc.isDemo()) {
            this.renderDemo(sr);
        }

        if (this.mc.gameSettings.showDebugInfo) {
            this.overlayDebug.renderDebugInfo(sr);
        }

        if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");
            float f3 = this.recordPlayingUpFor - partialTicks;
            int n3 = (int)(f3 * 255.0F / 20.0F);
            if (n3 > 255) {
                n3 = 255;
            }

            if (n3 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(scaledWidth / 2), (float)(scaledHeight - 68), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                int n4 = 16777215;
                if (this.recordIsPlaying) {
                    n4 = MathHelper.func_181758_c(f3 / 50.0F, 0.7F, 0.6F) & 16777215;
                }

                this.getFontRenderer()
                    .drawString(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4, n4 + (n3 << 24 & 0xFF000000));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        if (this.field_175195_w > 0) {
            this.mc.mcProfiler.startSection("titleAndSubtitle");
            float f4 = this.field_175195_w - partialTicks;
            int n5 = 255;
            if (this.field_175195_w > this.field_175193_B + this.field_175192_A) {
                float f5 = this.field_175199_z + this.field_175192_A + this.field_175193_B - f4;
                n5 = (int)(f5 * 255.0F / this.field_175199_z);
            }

            if (this.field_175195_w <= this.field_175193_B) {
                n5 = (int)(f4 * 255.0F / this.field_175193_B);
            }

            int n6 = MathHelper.clamp_int(n5, 0, 255);
            if (n6 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(scaledWidth / 2), (float)(scaledHeight / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 4.0F, 4.0F);
                int n7 = n6 << 24 & 0xFF000000;
                this.getFontRenderer()
                    .drawString(this.field_175201_x, -this.getFontRenderer().getStringWidth(this.field_175201_x) / 2, -10.0F, 16777215 | n7, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                this.getFontRenderer()
                    .drawString(this.field_175200_y, -this.getFontRenderer().getStringWidth(this.field_175200_y) / 2, 5.0F, 16777215 | n7, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective objective = null;
        ScorePlayerTeam playersTeam = scoreboard.getPlayersTeam(this.mc.thePlayer.getCommandSenderName());
        if (playersTeam != null) {
            int colorIndex = playersTeam.getChatFormat().getColorIndex();
            if (colorIndex >= 0) {
                objective = scoreboard.getObjectiveInDisplaySlot(3 + colorIndex);
            }
        }

        ScoreObjective objective2 = objective != null ? objective : scoreboard.getObjectiveInDisplaySlot(1);
        if (objective2 != null) {
            this.renderScoreboard(objective2, sr);
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, (float)(scaledHeight - 48), 0.0F);
        this.mc.mcProfiler.startSection("chat");
        this.persistantChatGUI.drawChat(this.updateCounter);
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
        objective2 = scoreboard.getObjectiveInDisplaySlot(0);
        if (!this.mc.gameSettings.keyBindPlayerList.isKeyDown()
            || this.mc.isIntegratedServerRunning() && this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && objective2 == null) {
            this.overlayPlayerList.updatePlayerList(false);
        } else {
            this.overlayPlayerList.updatePlayerList(true);
            this.overlayPlayerList.renderPlayerlist(scaledWidth, scoreboard, objective2);
        }

        LoadAvatarHelper.render();
        MessageCopiedHelper.handleScaledResolution(InvalidateHelper.computeScaledResolution(this.mc));

        try (RenderHelper renderHelper = new RenderHelper()) {
            ScaledResolution sr2 = InvalidateHelper.computeScaledResolution(this.mc);
            EventRender2D event = new EventRender2D(sr2, partialTicks);
            event.process();
            if (!event.cancel2()) {
            }

            ModuleManager.onRender();
            Notifications instance = Notifications.getInstance();
            if (instance != null && instance.checkState44() && !EditableModulesHelper.isActive()) {
                instance.process();
            }

            if (!EditableModulesHelper.isActive()) {
                ModernHandler2.render();
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        RecordMetricHelper.process2();
    }

    protected void renderTooltip(ScaledResolution sr, float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            if (Hotbar.checkState()) {
                return;
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            EntityPlayer renderViewEntity = (EntityPlayer)this.mc.getRenderViewEntity();
            int n = sr.getScaledWidth() / 2;
            float zLevel = this.zLevel;
            this.zLevel = -90.0F;
            this.drawTexturedModalRect(n - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
            this.drawTexturedModalRect(n - 91 - 1 + renderViewEntity.inventory.currentItem * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
            this.zLevel = zLevel;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

            for (int i = 0; i < 9; i++) {
                int n2 = sr.getScaledWidth() / 2 - 90 + i * 20 + 2;
                int n3 = sr.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(i, n2, n3, partialTicks, renderViewEntity);
            }

            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    public void renderHorseJumpBar(ScaledResolution p_175186_1_, int p_175186_2_) {
        this.mc.mcProfiler.startSection("jumpBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        float horseJumpPower = this.mc.thePlayer.getHorseJumpPower();
        int n = 182;
        int n2 = (int)(horseJumpPower * (n + 1));
        int n3 = p_175186_1_.getScaledHeight() - 32 + 3;
        this.drawTexturedModalRect(p_175186_2_, n3, 0, 84, n, 5);
        if (n2 > 0) {
            this.drawTexturedModalRect(p_175186_2_, n3, 0, 89, n2, 5);
        }

        this.mc.mcProfiler.endSection();
    }

    public void renderExpBar(ScaledResolution p_175176_1_, int p_175176_2_) {
        this.mc.mcProfiler.startSection("expBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        int n = this.mc.thePlayer.xpBarCap();
        if (n > 0) {
            int n2 = 182;
            int n3 = (int)(this.mc.thePlayer.experience * (n2 + 1));
            int n4 = p_175176_1_.getScaledHeight() - 32 + 3;
            this.drawTexturedModalRect(p_175176_2_, n4, 0, 64, n2, 5);
            if (n3 > 0) {
                this.drawTexturedModalRect(p_175176_2_, n4, 0, 69, n3, 5);
            }
        }

        this.mc.mcProfiler.endSection();
        if (this.mc.thePlayer.experienceLevel > 0) {
            this.mc.mcProfiler.startSection("expLevel");
            int n5 = 8453920;
            if (Config.isCustomColors()) {
                n5 = CustomColors.getExpBarTextColor(n5);
            }

            String str = String.valueOf(this.mc.thePlayer.experienceLevel);
            int n6 = (p_175176_1_.getScaledWidth() - this.getFontRenderer().getStringWidth(str)) / 2;
            int n7 = p_175176_1_.getScaledHeight() - 31 - 4;
            this.getFontRenderer().drawString(str, n6 + 1, n7, 0);
            this.getFontRenderer().drawString(str, n6 - 1, n7, 0);
            this.getFontRenderer().drawString(str, n6, n7 + 1, 0);
            this.getFontRenderer().drawString(str, n6, n7 - 1, 0);
            this.getFontRenderer().drawString(str, n6, n7, n5);
            this.mc.mcProfiler.endSection();
        }
    }

    public void func_181551_a(ScaledResolution p_181551_1_) {
        this.mc.mcProfiler.startSection("selectedItemName");
        if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
            var displayName = this.highlightingItemStack.getDisplayName();
            if (this.highlightingItemStack.hasDisplayName()) {
                displayName = EnumChatFormatting.ITALIC + displayName;
            }

            int n = (p_181551_1_.getScaledWidth() - this.getFontRenderer().getStringWidth((String)displayName)) / 2;
            int n2 = p_181551_1_.getScaledHeight() - 59;
            if (!this.mc.playerController.shouldDrawHUD()) {
                n2 += 14;
            }

            int n3 = (int)(this.remainingHighlightTicks * 256.0F / 10.0F);
            if (n3 > 255) {
                n3 = 255;
            }

            if (n3 > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                this.getFontRenderer().drawStringWithShadow((String)displayName, n, n2, 16777215 + (n3 << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        this.mc.mcProfiler.endSection();
    }

    public void renderDemo(ScaledResolution p_175185_1_) {
        this.mc.mcProfiler.startSection("demo");
        String str = "";
        if (this.mc.theWorld.getTotalWorldTime() >= 120500L) {
            str = I18n.format("demo.demoExpired");
        } else {
            str = I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime())));
        }

        int textWidth = this.getFontRenderer().getStringWidth(str);
        this.getFontRenderer().drawStringWithShadow(str, p_175185_1_.getScaledWidth() - textWidth - 10, 5.0F, 16777215);
        this.mc.mcProfiler.endSection();
    }

    protected boolean showCrosshair() {
        if (this.mc.gameSettings.showDebugInfo && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo) {
            return false;
        }
        if (this.mc.playerController.isSpectator()) {
            if (this.mc.pointedEntity != null) {
                return true;
            }
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
                if (this.mc.theWorld.getTileEntity(blockPos) instanceof IInventory) {
                    return true;
                }
            }

            return false;
        }
        return true;
    }

    public void renderStreamIndicator(ScaledResolution p_180478_1_) {
        this.streamIndicator.render(p_180478_1_.getScaledWidth() - 10, 10);
    }

    private void renderScoreboard(ScoreObjective scoreObjective, ScaledResolution scaledResolution) {
        if (org.brutality.module.impl.visual.Scoreboard.instance != null && org.brutality.module.impl.visual.Scoreboard.instance.checkState44()) {
            try (RenderHelper renderHelper = new RenderHelper()) {
                this.renderScoreboardInternal(scoreObjective, InvalidateHelper.computeScaledResolution(this.mc));
            }
        } else {
            this.renderScoreboardInternal(scoreObjective, scaledResolution);
        }
    }

    private void renderScoreboardInternal(ScoreObjective scoreObjective, ScaledResolution scaledResolution) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        Scoreboard scoreboard = scoreObjective.getScoreboard();
        var sortedScores = scoreboard.getSortedScores(scoreObjective);
        java.util.List list = Lists.newArrayList(Iterables.filter(sortedScores, new Predicate<Score>() {
            public boolean apply(Score p_apply_1_) {
                return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
        }));
        java.util.List list2;
        if (list.size() > 15) {
            list2 = Lists.newArrayList(Iterables.skip(list, sortedScores.size() - 15));
        } else {
            list2 = list;
        }

        boolean flag = org.brutality.module.impl.visual.Scoreboard.instance != null && org.brutality.module.impl.visual.Scoreboard.instance.checkState44();
        ArrayList<Score> scores = (ArrayList<Score>)(Object)(Lists.newArrayList());

        for (Score score : ((Iterable<Score>)(Object)(list2))) {
            if (score != null && score.getPlayerName() != null) {
                try {
                    ScorePlayerTeam playersTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String str = ScorePlayerTeam.formatPlayerName(playersTeam, score.getPlayerName());
                    boolean flag2 = flag && org.brutality.module.impl.visual.Scoreboard.instance.checkState5();
                    if (!flag2 || !shouldRemoveLine(score.getPlayerName()) && !shouldRemoveLine(str)) {
                        scores.add(score);
                    }
                } catch (Exception e) {
                    scores.add(score);
                }
            }
        }

        PitDebugger.handleScoreObjective(scoreObjective, scores);
        boolean flag3 = flag && org.brutality.module.impl.visual.Scoreboard.instance.checkState();
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        CustomFontRenderer drawStringWithShadowComponent = flag3 ? org.brutality.module.impl.visual.Scoreboard.instance.computeDrawStringWithShadowComponent() : null;
        if (flag3 && drawStringWithShadowComponent == null) {
            flag3 = false;
        }

        int n = flag3 ? org.brutality.module.impl.visual.Scoreboard.computeInt() : fontRenderer.FONT_HEIGHT;
        int n2 = flag3 ? drawStringWithShadowComponent.getStringWidth(scoreObjective.getDisplayName()) : fontRenderer.getStringWidth(scoreObjective.getDisplayName());

        for (Score score2 : scores) {
            ScorePlayerTeam playersTeam2 = scoreboard.getPlayersTeam(score2.getPlayerName());
            String str2 = ScorePlayerTeam.formatPlayerName(playersTeam2, score2.getPlayerName());
            int n3 = flag3 ? drawStringWithShadowComponent.getStringWidth(str2) : fontRenderer.getStringWidth(str2);
            n2 = Math.max(n2, n3);
        }

        String str3 = "";
        boolean flag4 = false;
        if (this.isOnPit()) {
            StreakInfo streakInfo = (StreakInfo)Brutality.moduleManager.computeModule("StreakInfo");
            if (streakInfo != null && streakInfo.checkState44() && streakInfo.text3 != null && !streakInfo.text3.isEmpty() && streakInfo.text4 != null && !streakInfo.text4.isEmpty()) {
                String str4 = streakInfo.text3;
                String str5 = streakInfo.text4;
                str3 = "Req:  "
                    + EnumChatFormatting.GOLD
                    + str4
                    + "g"
                    + EnumChatFormatting.GRAY
                    + " ("
                    + EnumChatFormatting.GREEN
                    + str5
                    + "%"
                    + EnumChatFormatting.GRAY
                    + ")";
                int n4 = flag3 ? drawStringWithShadowComponent.getStringWidth(str3) : fontRenderer.getStringWidth(str3);
                n2 = Math.max(n2, n4);
                flag4 = true;
            }
        }

        java.util.List<String> list3 = new ArrayList<>();
        if (this.isOnPit() && FakeContractHUD.getInstance() != null && FakeContractHUD.getInstance().checkState44()) {
            list3 = FakeContractHUD.getInstance().computeList();

            for (String str6 : list3) {
                int n5 = flag3 ? drawStringWithShadowComponent.getStringWidth(str6) : fontRenderer.getStringWidth(str6);
                n2 = Math.max(n2, n5);
            }
        }

        String str7 = "";
        boolean flag5 = false;
        if (flag && org.brutality.module.impl.visual.Scoreboard.instance.checkState4()) {
            str7 = org.brutality.module.impl.visual.Scoreboard.instance.computeString4();
            int n6 = flag3 ? drawStringWithShadowComponent.getStringWidth(str7) : fontRenderer.getStringWidth(str7);
            n2 = Math.max(n2, n6);
            flag5 = true;
        }

        int n7 = scores.size() + (flag4 ? 1 : 0) + list3.size();
        int n8 = n7 * n;
        int n9 = scaledResolution.getScaledHeight() / 2 + n8 / 3;
        byte b = 3;
        int n10 = scaledResolution.getScaledWidth() - n2 - b;
        boolean flag6 = false;
        if (flag) {
            n9 += (int)org.brutality.module.impl.visual.Scoreboard.instance.getYOffset();
            n9 += (int)org.brutality.module.impl.visual.Scoreboard.instance.getPosY();
            flag6 = org.brutality.module.impl.visual.Scoreboard.instance.computeDouble() != 1.0;
            String str8 = org.brutality.module.impl.visual.Scoreboard.instance.computeString3();

            n10 = switch (str8) {
                case "Left" -> 10;
                case "Center" -> scaledResolution.getScaledWidth() / 2 - n2 / 2;
                default -> scaledResolution.getScaledWidth() - n2 - b;
            } + (int)org.brutality.module.impl.visual.Scoreboard.instance.getPosX();
            if (flag6) {
                GL11.glPushMatrix();
                GL11.glTranslatef(n10, n9, 0.0F);
                GL11.glScalef(
                    (float)org.brutality.module.impl.visual.Scoreboard.instance.computeDouble(),
                    (float)org.brutality.module.impl.visual.Scoreboard.instance.computeDouble(),
                    1.0F
                );
                GL11.glTranslatef(-n10, -n9, 0.0F);
            }
        }

        int n11 = 0;
        boolean flag7 = false;
        boolean flag8 = false;
        int n12 = 80;
        int n13 = 1342177280;
        int n14 = 1610612736;
        int n15 = -1;
        if (flag && org.brutality.module.impl.visual.Scoreboard.instance.checkState2()) {
            n12 = (int)(org.brutality.module.impl.visual.Scoreboard.instance.computeDouble2() / 100.0 * 255.0);
            n13 = n12 << 24;
            n14 = Math.min(255, n12 + 50) << 24;
        }

        if (!list3.isEmpty()) {
            for (String str9 : ((Iterable<String>)(Object)(list3))) {
                n11++;
                int n16 = n9 - n11 * n;
                int n17 = n10 + n2 + 2;
                Gui.drawRect(n10 - 2, n16, n17, n16 + n, n13);
                if (flag3) {
                    drawStringWithShadowComponent.computeInt3(str9, n10, n16, n15);
                } else {
                    fontRenderer.drawString(str9, n10, n16, n15);
                }
            }
        }

        int n18 = 0;

        for (Score score3 : ((Iterable<Score>)(Object)(scores))) {
            n11++;
            n18++;
            ScorePlayerTeam playersTeam3 = scoreboard.getPlayersTeam(score3.getPlayerName());
            String str10 = ScorePlayerTeam.formatPlayerName(playersTeam3, score3.getPlayerName());
            NickHider instance = NickHider.getInstance();
            if (instance != null && instance.checkState44()) {
                String str11 = Minecraft.getMinecraft().thePlayer != null ? Minecraft.getMinecraft().thePlayer.getName() : null;
                if (str11 != null && score3.getPlayerName().equals(str11)) {
                    str10 = instance.computeString2(str10);
                }
            }

            if (n18 == 1 && flag5) {
                str10 = org.brutality.module.impl.visual.Scoreboard.instance.computeString2();
            }

            String str12 = SCORE_NON_TEXT.matcher(SCORE_FORMATTING.matcher(str10).replaceAll("")).replaceAll("").trim();
            if (this.isOnPit() && !flag7 && str12.startsWith("Gold:")) {
                flag7 = true;
                if (flag4 && !flag8) {
                    int n19 = n9 - n11 * n;
                    int n20 = n10 + n2 + 2;
                    Gui.drawRect(n10 - 2, n19, n20, n19 + n, n13);
                    if (flag3) {
                        drawStringWithShadowComponent.computeInt3(str3, n10, n19, n15);
                    } else {
                        fontRenderer.drawString(str3, n10, n19, n15);
                    }

                    n11++;
                    flag8 = true;
                }
            }

            int n21 = n9 - n11 * n;
            int n22 = n10 + n2 + 2;
            Gui.drawRect(n10 - 2, n21, n22, n21 + n, n13);
            if (flag3) {
                drawStringWithShadowComponent.computeInt3(str10, n10, n21, n15);
            } else {
                fontRenderer.drawString(str10, n10, n21, n15);
            }

            if (n11 == n7) {
                String displayName = scoreObjective.getDisplayName();
                Gui.drawRect(n10 - 2, n21 - n - 1, n22, n21 - 1, n14);
                Gui.drawRect(n10 - 2, n21 - 1, n22, n21, n13);
                int n23 = flag3 ? drawStringWithShadowComponent.getStringWidth(displayName) : fontRenderer.getStringWidth(displayName);
                int n24 = n10 + n2 / 2 - n23 / 2;
                int n25 = n21 - n;
                if (flag3) {
                    drawStringWithShadowComponent.computeInt3(displayName, n24, n25, n15);
                } else {
                    fontRenderer.drawString(displayName, n24, n25, n15);
                }
            }
        }

        if (flag && flag6) {
            GL11.glPopMatrix();
        }

        GlStateManager.disableBlend();
    }

    private boolean isOnPit() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) {
            return false;
        }
        String str = FreshHelper.computeString(mc.theWorld);
        return str != null;
    }

    private void renderDefaultScoreboard(ScoreObjective scoreObjective, ScaledResolution scaledResolution) {
        Scoreboard scoreboard = scoreObjective.getScoreboard();
        var sortedScores = scoreboard.getSortedScores(scoreObjective);
        java.util.List list = Lists.newArrayList(Iterables.filter(sortedScores, new Predicate<Score>() {
            public boolean apply(Score p_apply_1_) {
                return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
        }));
        java.util.List list2;
        if (list.size() > 15) {
            list2 = Lists.newArrayList(Iterables.skip(list, sortedScores.size() - 15));
        } else {
            list2 = list;
        }

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        int textWidth = fontRenderer.getStringWidth(scoreObjective.getDisplayName());

        for (Score score : ((Iterable<Score>)(Object)(list2))) {
            ScorePlayerTeam playersTeam = scoreboard.getPlayersTeam(score.getPlayerName());
            String str = ScorePlayerTeam.formatPlayerName(playersTeam, score.getPlayerName());
            textWidth = Math.max(textWidth, fontRenderer.getStringWidth(str));
        }

        int size = list2.size();
        int n = size * fontRenderer.FONT_HEIGHT;
        int n2 = scaledResolution.getScaledHeight() / 2 + n / 3;
        byte b = 3;
        int n3 = scaledResolution.getScaledWidth() - textWidth - b;
        int n4 = 0;

        for (Score score2 : ((Iterable<Score>)(Object)(list2))) {
            n4++;
            ScorePlayerTeam playersTeam2 = scoreboard.getPlayersTeam(score2.getPlayerName());
            String str2 = ScorePlayerTeam.formatPlayerName(playersTeam2, score2.getPlayerName());
            int n5 = n2 - n4 * fontRenderer.FONT_HEIGHT;
            int n6 = scaledResolution.getScaledWidth() - b + 2;
            Gui.drawRect(n3 - 2, n5, n6, n5 + fontRenderer.FONT_HEIGHT, 1342177280);
            fontRenderer.drawString(str2, n3, n5, 553648127);
            if (n4 == size) {
                String displayName = scoreObjective.getDisplayName();
                Gui.drawRect(n3 - 2, n5 - fontRenderer.FONT_HEIGHT - 1, n6, n5 - 1, 1610612736);
                Gui.drawRect(n3 - 2, n5 - 1, n6, n5, 1342177280);
                fontRenderer.drawString(displayName, n3 + textWidth / 2 - fontRenderer.getStringWidth(displayName) / 2, n5 - fontRenderer.FONT_HEIGHT, 553648127);
            }
        }
    }

    private static boolean shouldRemoveLine(String text) {
        if (text != null && !text.trim().isEmpty()) {
            String str = SCORE_FORMATTING.matcher(text).replaceAll("").trim();
            if (SCORE_DATE.matcher(str).find()) {
                return true;
            }
            return SCORE_LOBBY.matcher(str).find() ? true : SCORE_NUMBER.matcher(str).matches();
        }
        return false;
    }

    private void renderPlayerStats(ScaledResolution p_180477_1_) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            EntityPlayer renderViewEntity = (EntityPlayer)this.mc.getRenderViewEntity();
            int n = MathHelper.ceiling_float_int(renderViewEntity.getHealth());
            boolean flag = this.healthUpdateCounter > this.updateCounter && (this.healthUpdateCounter - this.updateCounter) / 3L % 2L == 1L;
            if (n < this.playerHealth && renderViewEntity.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 20;
            } else if (n > this.playerHealth && renderViewEntity.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 10;
            }

            if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
                this.playerHealth = n;
                this.lastPlayerHealth = n;
                this.lastSystemTime = Minecraft.getSystemTime();
            }

            this.playerHealth = n;
            int lastPlayerHealth = this.lastPlayerHealth;
            this.rand.setSeed(this.updateCounter * 312871);
            boolean flag2 = false;
            FoodStats foodStats = renderViewEntity.getFoodStats();
            int foodLevel = foodStats.getFoodLevel();
            int prevFoodLevel = foodStats.getPrevFoodLevel();
            IAttributeInstance entityAttribute = renderViewEntity.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            int n2 = p_180477_1_.getScaledWidth() / 2 - 91;
            int n3 = p_180477_1_.getScaledWidth() / 2 + 91;
            int n4 = p_180477_1_.getScaledHeight() - 39;
            float attributeValue = (float)entityAttribute.getAttributeValue();
            float absorptionAmount = renderViewEntity.getAbsorptionAmount();
            int n5 = MathHelper.ceiling_float_int((attributeValue + absorptionAmount) / 2.0F / 10.0F);
            int n6 = Math.max(10 - (n5 - 2), 3);
            int n7 = n4 - (n5 - 1) * n6 - 10;
            float f = absorptionAmount;
            int totalArmorValue = renderViewEntity.getTotalArmorValue();
            int n8 = -1;
            if (renderViewEntity.isPotionActive(Potion.regeneration)) {
                n8 = this.updateCounter % MathHelper.ceiling_float_int(attributeValue + 5.0F);
            }

            boolean flag3 = true;
            if (PlayerHUD.getInstance() != null && PlayerHUD.getInstance().checkState44() && PlayerHUD.getInstance().removeArmorSetting.isEnabled()) {
                flag3 = false;
            }

            if (!flag3) {
                this.mc.mcProfiler.startSection("health");
            } else {
                this.mc.mcProfiler.startSection("armor");

                for (int i = 0; i < 10; i++) {
                    if (totalArmorValue > 0) {
                        int n9 = n2 + i * 8;
                        if (i * 2 + 1 < totalArmorValue) {
                            this.drawTexturedModalRect(n9, n7, 34, 9, 9, 9);
                        }

                        if (i * 2 + 1 == totalArmorValue) {
                            this.drawTexturedModalRect(n9, n7, 25, 9, 9, 9);
                        }

                        if (i * 2 + 1 > totalArmorValue) {
                            this.drawTexturedModalRect(n9, n7, 16, 9, 9, 9);
                        }
                    }
                }

                this.mc.mcProfiler.endStartSection("health");
            }

            for (int j = MathHelper.ceiling_float_int((attributeValue + absorptionAmount) / 2.0F) - 1; j >= 0; j--) {
                int n10 = 16;
                if (renderViewEntity.isPotionActive(Potion.poison)) {
                    n10 += 36;
                } else if (renderViewEntity.isPotionActive(Potion.wither)) {
                    n10 += 72;
                }

                byte b = 0;
                if (flag) {
                    b = 1;
                }

                int n11 = MathHelper.ceiling_float_int((j + 1) / 10.0F) - 1;
                int n12 = n2 + j % 10 * 8;
                int n13 = n4 - n11 * n6;
                if (n <= 4) {
                    n13 += this.rand.nextInt(2);
                }

                if (j == n8) {
                    n13 -= 2;
                }

                byte b2 = 0;
                if (renderViewEntity.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
                    b2 = 5;
                }

                this.drawTexturedModalRect(n12, n13, 16 + b * 9, 9 * b2, 9, 9);
                if (flag) {
                    if (j * 2 + 1 < lastPlayerHealth) {
                        this.drawTexturedModalRect(n12, n13, n10 + 54, 9 * b2, 9, 9);
                    }

                    if (j * 2 + 1 == lastPlayerHealth) {
                        this.drawTexturedModalRect(n12, n13, n10 + 63, 9 * b2, 9, 9);
                    }
                }

                if (f <= 0.0F) {
                    if (j * 2 + 1 < n) {
                        this.drawTexturedModalRect(n12, n13, n10 + 36, 9 * b2, 9, 9);
                    }

                    if (j * 2 + 1 == n) {
                        this.drawTexturedModalRect(n12, n13, n10 + 45, 9 * b2, 9, 9);
                    }
                } else {
                    if (f == absorptionAmount && absorptionAmount % 2.0F == 1.0F) {
                        this.drawTexturedModalRect(n12, n13, n10 + 153, 9 * b2, 9, 9);
                    } else {
                        this.drawTexturedModalRect(n12, n13, n10 + 144, 9 * b2, 9, 9);
                    }

                    f -= 2.0F;
                }
            }

            Entity ridingEntity = renderViewEntity.ridingEntity;
            boolean flag4 = true;
            if (PlayerHUD.getInstance() != null && PlayerHUD.getInstance().checkState44() && PlayerHUD.getInstance().removeHungerSetting.isEnabled()) {
                flag4 = false;
            }

            if (ridingEntity == null && flag4) {
                this.mc.mcProfiler.endStartSection("food");

                for (int k = 0; k < 10; k++) {
                    int n14 = n4;
                    int n15 = 16;
                    byte b3 = 0;
                    if (renderViewEntity.isPotionActive(Potion.hunger)) {
                        n15 += 36;
                        b3 = 13;
                    }

                    if (renderViewEntity.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (foodLevel * 3 + 1) == 0) {
                        n14 = n4 + (this.rand.nextInt(3) - 1);
                    }

                    if (flag2) {
                        b3 = 1;
                    }

                    int n16 = n3 - k * 8 - 9;
                    this.drawTexturedModalRect(n16, n14, 16 + b3 * 9, 27, 9, 9);
                    if (flag2) {
                        if (k * 2 + 1 < prevFoodLevel) {
                            this.drawTexturedModalRect(n16, n14, n15 + 54, 27, 9, 9);
                        }

                        if (k * 2 + 1 == prevFoodLevel) {
                            this.drawTexturedModalRect(n16, n14, n15 + 63, 27, 9, 9);
                        }
                    }

                    if (k * 2 + 1 < foodLevel) {
                        this.drawTexturedModalRect(n16, n14, n15 + 36, 27, 9, 9);
                    }

                    if (k * 2 + 1 == foodLevel) {
                        this.drawTexturedModalRect(n16, n14, n15 + 45, 27, 9, 9);
                    }
                }
            } else if (ridingEntity instanceof EntityLivingBase) {
                this.mc.mcProfiler.endStartSection("mountHealth");
                EntityLivingBase living = (EntityLivingBase)ridingEntity;
                int n17 = (int)Math.ceil(living.getHealth());
                float maxHealth = living.getMaxHealth();
                int n18 = (int)(maxHealth + 0.5F) / 2;
                if (n18 > 30) {
                    n18 = 30;
                }

                int n19 = n4;

                for (int idx = 0; n18 > 0; idx += 20) {
                    int n20 = Math.min(n18, 10);
                    n18 -= n20;

                    for (int idx2 = 0; idx2 < n20; idx2++) {
                        byte b4 = 52;
                        byte b5 = 0;
                        if (flag2) {
                            b5 = 1;
                        }

                        int n21 = n3 - idx2 * 8 - 9;
                        this.drawTexturedModalRect(n21, n19, b4 + b5 * 9, 9, 9, 9);
                        if (idx2 * 2 + 1 + idx < n17) {
                            this.drawTexturedModalRect(n21, n19, b4 + 36, 9, 9, 9);
                        }

                        if (idx2 * 2 + 1 + idx == n17) {
                            this.drawTexturedModalRect(n21, n19, b4 + 45, 9, 9, 9);
                        }
                    }

                    n19 -= 10;
                }
            }

            this.mc.mcProfiler.endStartSection("air");
            if (renderViewEntity.isInsideOfMaterial(Material.water)) {
                int air = this.mc.thePlayer.getAir();
                int n22 = MathHelper.ceiling_double_int((air - 2) * 10.0 / 300.0);
                int n23 = MathHelper.ceiling_double_int(air * 10.0 / 300.0) - n22;

                for (int idx3 = 0; idx3 < n22 + n23; idx3++) {
                    if (idx3 < n22) {
                        this.drawTexturedModalRect(n3 - idx3 * 8 - 9, n7, 16, 18, 9, 9);
                    } else {
                        this.drawTexturedModalRect(n3 - idx3 * 8 - 9, n7, 25, 18, 9, 9);
                    }
                }
            }

            this.mc.mcProfiler.endSection();
        }
    }

    private void renderBossHealth() {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            BossStatus.statusBarTime--;
            FontRenderer fontRenderer = this.mc.fontRendererObj;
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
            int scaledWidth = sr.getScaledWidth();
            int n = 182;
            int n2 = scaledWidth / 2 - n / 2;
            int n3 = (int)(BossStatus.healthScale * (n + 1));
            byte b = 12;
            this.drawTexturedModalRect(n2, b, 0, 74, n, 5);
            this.drawTexturedModalRect(n2, b, 0, 74, n, 5);
            if (n3 > 0) {
                this.drawTexturedModalRect(n2, b, 0, 79, n3, 5);
            }

            String bossName = BossStatus.bossName;
            this.getFontRenderer().drawStringWithShadow(bossName, scaledWidth / 2 - this.getFontRenderer().getStringWidth(bossName) / 2, b - 10, 16777215);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(icons);
        }
    }

    private void renderPumpkinOverlay(ScaledResolution p_180476_1_) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldRenderer.func_181662_b(0.0, p_180476_1_.getScaledHeight(), -90.0).func_181673_a(0.0, 1.0).func_181675_d();
        worldRenderer.func_181662_b(p_180476_1_.getScaledWidth(), p_180476_1_.getScaledHeight(), -90.0).func_181673_a(1.0, 1.0).func_181675_d();
        worldRenderer.func_181662_b(p_180476_1_.getScaledWidth(), 0.0, -90.0).func_181673_a(1.0, 0.0).func_181675_d();
        worldRenderer.func_181662_b(0.0, 0.0, -90.0).func_181673_a(0.0, 0.0).func_181675_d();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderVignette(float p_180480_1_, ScaledResolution p_180480_2_) {
        if (!Config.isVignetteEnabled()) {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        } else {
            p_180480_1_ = 1.0F - p_180480_1_;
            float f = MathHelper.clamp_float(p_180480_1_, 0.0F, 1.0F);
            WorldBorder worldBorder = this.mc.theWorld.getWorldBorder();
            float closestDistance = (float)worldBorder.getClosestDistance(this.mc.thePlayer);
            double d = Math.min(worldBorder.getResizeSpeed() * worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getTargetSize() - worldBorder.getDiameter()));
            double d2 = Math.max((double)worldBorder.getWarningDistance(), d);
            if (closestDistance < d2) {
                closestDistance = 1.0F - (float)(closestDistance / d2);
            } else {
                closestDistance = 0.0F;
            }

            this.prevVignetteBrightness = (float)(this.prevVignetteBrightness + (f - this.prevVignetteBrightness) * 0.01);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.tryBlendFuncSeparate(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_ONE, GL11.GL_ZERO);
            if (closestDistance > 0.0F) {
                GlStateManager.color(0.0F, closestDistance, closestDistance, 1.0F);
            } else {
                GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
            }

            this.mc.getTextureManager().bindTexture(vignetteTexPath);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            worldRenderer.func_181662_b(0.0, p_180480_2_.getScaledHeight(), -90.0).func_181673_a(0.0, 1.0).func_181675_d();
            worldRenderer.func_181662_b(p_180480_2_.getScaledWidth(), p_180480_2_.getScaledHeight(), -90.0).func_181673_a(1.0, 1.0).func_181675_d();
            worldRenderer.func_181662_b(p_180480_2_.getScaledWidth(), 0.0, -90.0).func_181673_a(1.0, 0.0).func_181675_d();
            worldRenderer.func_181662_b(0.0, 0.0, -90.0).func_181673_a(0.0, 0.0).func_181675_d();
            tessellator.draw();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        }
    }

    private void func_180474_b(float p_180474_1_, ScaledResolution p_180474_2_) {
        if (p_180474_1_ < 1.0F) {
            p_180474_1_ *= p_180474_1_;
            p_180474_1_ *= p_180474_1_;
            p_180474_1_ = p_180474_1_ * 0.8F + 0.2F;
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, p_180474_1_);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        TextureAtlasSprite texture = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
        float minU = texture.getMinU();
        float minV = texture.getMinV();
        float maxU = texture.getMaxU();
        float maxV = texture.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldRenderer.func_181662_b(0.0, p_180474_2_.getScaledHeight(), -90.0).func_181673_a(minU, maxV).func_181675_d();
        worldRenderer.func_181662_b(p_180474_2_.getScaledWidth(), p_180474_2_.getScaledHeight(), -90.0).func_181673_a(maxU, maxV).func_181675_d();
        worldRenderer.func_181662_b(p_180474_2_.getScaledWidth(), 0.0, -90.0).func_181673_a(maxU, minV).func_181675_d();
        worldRenderer.func_181662_b(0.0, 0.0, -90.0).func_181673_a(minU, minV).func_181675_d();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer p_175184_5_) {
        try {
            LowLifeWarningHelper.handleInt(index, xPos, yPos);
        } catch (Exception ignored) {
        }

        ItemStack stack = p_175184_5_.inventory.mainInventory[index];
        if (stack != null) {
            float f = stack.animationsToGo - partialTicks;
            if (f > 0.0F) {
                GlStateManager.pushMatrix();
                float f2 = 1.0F + f / 5.0F;
                GlStateManager.translate((float)(xPos + 8), (float)(yPos + 12), 0.0F);
                GlStateManager.scale(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(xPos + 8)), (float)(-(yPos + 12)), 0.0F);
            }

            this.itemRenderer.renderItemAndEffectIntoGUI(stack, xPos, yPos);
            if (f > 0.0F) {
                GlStateManager.popMatrix();
            }

            this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, stack, xPos, yPos);
        }
    }

    public void updateTick() {
        if (this.recordPlayingUpFor > 0) {
            this.recordPlayingUpFor--;
        }

        if (this.field_175195_w > 0) {
            this.field_175195_w--;
            if (this.field_175195_w <= 0) {
                this.field_175201_x = "";
                this.field_175200_y = "";
            }
        }

        this.updateCounter++;
        this.streamIndicator.func_152439_a();
        if (this.mc.thePlayer != null) {
            ItemStack currentItem = this.mc.thePlayer.inventory.getCurrentItem();
            if (currentItem == null) {
                this.remainingHighlightTicks = 0;
            } else if (this.highlightingItemStack != null
                && currentItem.getItem() == this.highlightingItemStack.getItem()
                && ItemStack.areItemStackTagsEqual(currentItem, this.highlightingItemStack)
                && (currentItem.isItemStackDamageable() || currentItem.getMetadata() == this.highlightingItemStack.getMetadata())) {
                if (this.remainingHighlightTicks > 0) {
                    this.remainingHighlightTicks--;
                }
            } else {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = currentItem;
        }
    }

    public void setRecordPlayingMessage(String p_73833_1_) {
        this.setRecordPlaying(I18n.format("record.nowPlaying", p_73833_1_), true);
    }

    public void setRecordPlaying(String p_110326_1_, boolean p_110326_2_) {
        this.recordPlaying = p_110326_1_;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = p_110326_2_;
    }

    public void displayTitle(String p_175178_1_, String p_175178_2_, int p_175178_3_, int p_175178_4_, int p_175178_5_) {
        if (p_175178_1_ == null && p_175178_2_ == null && p_175178_3_ < 0 && p_175178_4_ < 0 && p_175178_5_ < 0) {
            this.field_175201_x = "";
            this.field_175200_y = "";
            this.field_175195_w = 0;
        } else if (p_175178_1_ != null) {
            this.field_175201_x = p_175178_1_;
            this.field_175195_w = this.field_175199_z + this.field_175192_A + this.field_175193_B;
        } else if (p_175178_2_ != null) {
            this.field_175200_y = p_175178_2_;
        } else {
            if (p_175178_3_ >= 0) {
                this.field_175199_z = p_175178_3_;
            }

            if (p_175178_4_ >= 0) {
                this.field_175192_A = p_175178_4_;
            }

            if (p_175178_5_ >= 0) {
                this.field_175193_B = p_175178_5_;
            }

            if (this.field_175195_w > 0) {
                this.field_175195_w = this.field_175199_z + this.field_175192_A + this.field_175193_B;
            }
        }
    }

    public String getDisplayedTitle() {
        return this.field_175201_x;
    }

    public String getDisplayedSubTitle() {
        return this.field_175200_y;
    }

    public void setRecordPlaying(IChatComponent p_175188_1_, boolean p_175188_2_) {
        this.setRecordPlaying(p_175188_1_.getUnformattedText(), p_175188_2_);
    }

    public GuiNewChat getChatGUI() {
        return this.persistantChatGUI;
    }

    public int getUpdateCounter() {
        return this.updateCounter;
    }

    public FontRenderer getFontRenderer() {
        return this.mc.fontRendererObj;
    }

    public GuiSpectator getSpectatorGui() {
        return this.spectatorGui;
    }

    public GuiPlayerTabOverlay getTabList() {
        return this.overlayPlayerList;
    }

    public void func_181029_i() {
        this.overlayPlayerList.func_181030_a();
    }
}
