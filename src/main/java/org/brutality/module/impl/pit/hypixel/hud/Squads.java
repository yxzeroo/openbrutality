package org.brutality.module.impl.pit.hypixel.hud;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StringUtils;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.opengl.GL11;

public class Squads extends Module {
    private final PitCategory pitCategory = PitCategory.MAJOR_EVENT;
    private final BooleanSetting showInfoSetting = new BooleanSetting("Show Info", this, true);
    private final BooleanSetting bannerESPSetting = new BooleanSetting("Banner ESP", this, true);
    private final BooleanSetting debugScoreboardDetectionSetting = new BooleanSetting("Debug Scoreboard Detection", this, false);
    private final NumberSetting hudxSetting = new NumberSetting("HUD X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting hudySetting = new NumberSetting("HUD Y", this, 10.0, 0.0, 1080.0, 1.0);
    private static final Pattern pattern = Pattern.compile("remaining\\s*:\\s*(\\d{1,2}):(\\d{2})", 2);
    private static final Pattern pattern2 = Pattern.compile("(\\d[\\d,]*)");
    private static final Pattern pattern3 = Pattern.compile("(\\d[\\d,]*)\\s*pts", 2);
    private static final Pattern pattern4 = Pattern.compile("(\\d[\\d,]*)\\s*banners", 2);
    private static final Pattern pattern5 = Pattern.compile("\\[([A-Za-z])]");
    private static final Pattern pattern6 = Pattern.compile("[^\\x20-\\x7E]");
    private static final Pattern pattern7 = Pattern.compile("(?:\\bIn\\s*)?\\[([A-Za-z])]\\s*#\\s*(\\d+)", 2);
    private static final Pattern pattern8 = Pattern.compile("\\+(\\d[\\d,]*)\\s*pts\\b", 2);
    private static Object object;
    private static int count = Integer.MIN_VALUE;
    private static boolean flag;
    private static String text;
    private final List<Squads.Entry> entries = new ArrayList<>();
    private boolean active;
    private String text2 = "--:--";
    private int count2;
    private int count3 = 8;
    private int count4;
    private int count5;
    private int count6 = -1;
    private int count7;
    private boolean flag2;
    private Object object2;
    private String text3;
    private long timestamp;
    private long timestamp2;
    private int count8;
    private String text4;
    private boolean flag3;
    private boolean flag4;
    private long timestamp3;
    private final List<String> entries2 = new ArrayList<>();

    private static int computeInt(String str) {
        try {
            return Integer.parseInt(str.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        if (player != null && mc.thePlayer != null && mc.theWorld != null) {
            process3();
            return flag && matchesString(text, player.getDisplayName() == null ? null : player.getDisplayName().getFormattedText());
        }
        return false;
    }

    private boolean checkState() {
        this.active = false;
        this.text2 = "--:--";
        this.count3 = 8;
        this.count5 = 0;
        this.count6 = -1;
        this.count4 = 0;
        this.count8 = 0;
        this.text4 = null;
        this.entries2.clear();
        if (mc.theWorld == null) {
            this.process6();
            this.flag2 = false;
            this.entries.clear();
            return false;
        }
        if (this.object2 != mc.theWorld) {
            this.object2 = mc.theWorld;
            this.flag2 = false;
            this.process6();
        }

        try {
            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            ScoreObjective objective = computeScoreObjective(scoreboard);
            if (objective == null) {
                return false;
            }

            if (this.debugScoreboardDetectionSetting.isEnabled()) {
                this.entries2.add("Objective " + objective.getName() + ": " + objective.getDisplayName());
            }

            this.handleString(objective.getDisplayName());

            for (Score score : scoreboard.getSortedScores(objective)) {
                String playerName = score.getPlayerName();
                if (playerName != null && !playerName.startsWith("#")) {
                    String str = ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(playerName), playerName);
                    if (this.debugScoreboardDetectionSetting.isEnabled()) {
                        this.entries2.add(str);
                    }

                    this.handleString(str);
                }
            }

            for (String str2 : GetCurrentScreenHelper.computeList2()) {
                this.handleString(str2);
            }

            if (mc.thePlayer != null && mc.thePlayer.getDisplayName() != null) {
                String str3 = computeString2(mc.thePlayer.getDisplayName().getFormattedText());
                if (this.text4 == null && str3 != null) {
                    this.text4 = str3;
                }
            }
        } catch (Exception e) {
            this.active = false;
        }

        if (this.active) {
            if (!this.flag2) {
                this.process6();
                this.flag2 = true;
            }

            if (this.count6 >= 0) {
                this.count2 = this.count6;
            }

            if (this.bannerESPSetting.isEnabled() && System.currentTimeMillis() - this.timestamp2 >= 250L) {
                this.timestamp2 = System.currentTimeMillis();
                this.process();
            }
        } else {
            this.flag2 = false;
            this.process6();
            this.entries.clear();
        }

        return this.active;
    }

    private BlockPos computeBlockPos(EntityArmorStand entity) {
        BlockPos pos = entity.getPosition();
        BlockPos pos2 = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = -5; k <= 1; k++) {
                    BlockPos pos3 = pos.add(i, k, j);
                    Block block = mc.theWorld.getBlockState(pos3).getBlock();
                    if (block == Blocks.standing_banner || block == Blocks.wall_banner) {
                        double distanceSq = entity.getDistanceSq(pos3.getX() + 0.5, pos3.getY() + 0.5, pos3.getZ() + 0.5);
                        if (distanceSq < MAX_VALUE) {
                            pos2 = pos3;
                            MAX_VALUE = distanceSq;
                        }
                    }
                }
            }
        }

        return pos2;
    }

    @Override
    public void onDisable() {
        HudDragHelper.handleObject(this);
        super.onDisable();
    }

    private static String computeString(String str) {
        return str == null ? "" : pattern6.matcher(StringUtils.stripControlCodes(str)).replaceAll("").trim();
    }

    private void handleDouble(double d, double d2, double d3, int n) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glLineWidth(2.0F);
            GlStateManager.color((n >> 16 & 0xFF) / 255.0F, (n >> 8 & 0xFF) / 255.0F, (n & 0xFF) / 255.0F, 0.95F);
            RenderGlobal.func_181561_a(new AxisAlignedBB(d - 0.4, d2, d3 - 0.4, d + 0.4, d2 + 1.8, d3 + 0.4));
        }
    }

    private static String computeString2(String str) {
        if (str == null) {
            return null;
        }
        Matcher matcher = pattern5.matcher(StringUtils.stripControlCodes(str));
        return matcher.find() ? matcher.group(1) : null;
    }

    private void handleString(String str) {
        if (str != null) {
            String str2 = computeString(str);
            String str3 = str2.toLowerCase(Locale.ROOT);
            if (str3.contains("squads") && !str3.contains("next")) {
                this.active = true;
            }

            Matcher matcher = pattern.matcher(str2);
            if (matcher.find()) {
                this.text2 = matcher.group(1) + ":" + matcher.group(2);
            }

            if (str3.contains("kills")) {
                int[] ints = computeIntArray(str2);
                if (ints != null) {
                    this.count6 = ints[0];
                    this.count2 = ints[0];
                    this.count3 = ints[1];
                }
            }

            Matcher matcher2 = pattern3.matcher(str2);
            if (!str3.contains("pts/s") && matcher2.find()) {
                this.count4 = computeInt(matcher2.group(1));
                String str4 = computeString2(str2);
                if (str4 != null) {
                    this.text4 = str4;
                }
            }

            Matcher matcher3 = pattern7.matcher(str2);
            if (matcher3.find()) {
                this.text4 = matcher3.group(1);
                this.count5 = computeInt(matcher3.group(2));
            }

            Matcher matcher4 = pattern4.matcher(str2);
            if (matcher4.find()) {
                this.count8 = computeInt(matcher4.group(1));
            }
        }
    }

    public static boolean isActive() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            process3();
            return flag;
        }
        return false;
    }

    private static ScoreObjective computeScoreObjective(Scoreboard scoreboard) {
        if (scoreboard == null) {
            return null;
        }
        ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
        if (mc.thePlayer != null && mc.thePlayer.getTeam() instanceof ScorePlayerTeam) {
            int colorIndex = ((ScorePlayerTeam)mc.thePlayer.getTeam()).getChatFormat().getColorIndex();
            ScoreObjective objective = colorIndex < 0 ? null : scoreboard.getObjectiveInDisplaySlot(3 + colorIndex);
            if (objective != null) {
                objectiveInDisplaySlot = objective;
            }
        }

        return objectiveInDisplaySlot;
    }

    private void process6() {
        this.count2 = 0;
        this.count7 = 0;
        this.text3 = null;
        this.timestamp = 0L;
    }

    private void handleString2(String str) {
        if (this.active && str != null) {
            long now = System.currentTimeMillis();
            if (!str.equals(this.text3) || now - this.timestamp >= 100L) {
                this.text3 = str;
                this.timestamp = now;
                if (ExecuteCommandHelper.matchesString(str)) {
                    String str2 = StringUtils.stripControlCodes(str).trim();
                    this.count2++;
                    Matcher matcher = pattern8.matcher(str2);
                    if (matcher.find()) {
                        this.count7 = this.count7 + computeInt(matcher.group(1));
                    }
                }
            }
        }
    }

    private static Squads.Entry2 computeEntry2(String str) {
        if (str != null && computeString2(str) != null) {
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            boolean flag = false;
            char ch = '7';

            for (int i = 0; i < str.length(); i++) {
                char ch2 = str.charAt(i);
                if (ch2 == 167 && i + 1 < str.length()) {
                    char str2 = Character.toLowerCase(str.charAt(++i));
                    if ("0123456789abcdef".indexOf(str2) >= 0) {
                        ch = str2;
                        flag = true;
                    } else if (str2 == 'r') {
                        ch = '7';
                    }
                } else if ("●○•◉⬤◯".indexOf(ch2) >= 0) {
                    n++;
                    boolean flag2 = ch2 != 9675 && ch2 != 9711;
                    if (flag2) {
                        n3++;
                    }

                    if (flag2 && ch != '7' && ch != '8') {
                        n2++;
                    }
                }
            }

            return n != 5 ? null : new Squads.Entry2(computeString2(str).toUpperCase(Locale.ROOT), (flag ? n2 : n3) * 20);
        }
        return null;
    }

    private static String computeString(int n, int n2, int n3) {
        String str = n == 0 ? "§7" : (n >= n2 ? "§a" : (n >= 4 ? "§6" : "§c"));
        return "§fKills: " + str + n + "§f/" + n2 + " §7(§b" + n3 + "pts§7)";
    }

    private void process5() {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            for (Squads.Entry entry : this.entries) {
                double d = entry.amount - mc.getRenderManager().viewerPosX;
                double d2 = entry.amount2 - mc.getRenderManager().viewerPosY;
                double d3 = entry.amount3 - mc.getRenderManager().viewerPosZ;
                this.handleDouble(d, d2, d3, entry.count);
                GlStateManager.pushMatrix();

                try {
                    GlStateManager.translate(d, d2 + 2.15, d3);
                    GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
                    double dist = mc.thePlayer.getDistance(entry.amount, entry.amount2, entry.amount3);
                    double d4 = computeDouble(dist);
                    GlStateManager.scale(-d4, -d4, d4);
                    GlStateManager.enableTexture2D();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    computeFontRenderer(Squads.class)
                        .drawStringWithShadow(
                            entry.text, ~(computeFontRenderer(Squads.class).getStringWidth(entry.text) - 1) / 2, entry.entry2 == null ? 0.0F : -12.0F, entry.count
                        );
                    if (entry.entry2 != null) {
                        int n = 1 + (int)(System.currentTimeMillis() / 400L % 3L);
                        String str = ".".repeat(n);
                        String text = entry.entry2.text;
                        int count = entry.entry2.count;
                        String str2 = str;
                        String str3 = "§eCapturing" + str2 + " §b" + count + "% §7[" + text + "]";
                        computeFontRenderer(Squads.class)
                            .drawStringWithShadow(str3, -computeFontRenderer(Squads.class).getStringWidth(str3) / 2, 0.0F, 16777215);
                    }
                } finally {
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    public boolean isActive2() {
        return this.active;
    }

    public Squads() {
        super("Squads", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{this.showInfoSetting, this.bannerESPSetting, this.debugScoreboardDetectionSetting, this.hudxSetting, this.hudySetting}
        );
    }

    private void process4() {
        FontRenderer fontRenderer = this.computeFontRenderer2();
        int n = Math.max(12, fontRenderer.FONT_HEIGHT + 2);
        String[] parts = new String[]{
            "§f§lSquads Info:",
            "§fRemaining: §b" + this.text2,
            computeString(this.count2, this.count3, this.count7),
            "§fPoints: §b" + this.count4,
            "§fBanners: §e" + this.count8,
            "§fPlace: §b" + (this.count5 > 0 ? "#" + this.count5 : "--")
        };
        int n2 = 0;

        for (String str : parts) {
            n2 = Math.max(n2, fontRenderer.getStringWidth(str));
        }

        HudDragHelper.handleObject(this, this.hudxSetting, this.hudySetting, n2, parts.length * n);
        int n3 = (int)this.hudxSetting.computeDouble();
        int n4 = (int)this.hudySetting.computeDouble();

        for (String str2 : parts) {
            fontRenderer.drawStringWithShadow(str2, n3, n4, -1);
            n4 += n;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.process6();
                this.flag2 = false;
                this.object2 = null;
                this.entries.clear();
                this.active = false;
                object = null;
                count = Integer.MIN_VALUE;
            } else if (event instanceof ChatEvent chatEvent) {
                if (chatEvent.byteVal != 2 && chatEvent.message != null) {
                    this.handleString2(chatEvent.message.getUnformattedText());
                }
            } else if (event instanceof EventUpdate) {
                if (((EventUpdate)event).getState() == EventState.POST) {
                    return;
                }

                boolean flag = this.checkState();
                if (this.debugScoreboardDetectionSetting.isEnabled()
                    && mc.thePlayer != null
                    && (!this.flag3 || flag != this.flag4 || System.currentTimeMillis() - this.timestamp3 > 15000L)) {
                    this.timestamp3 = System.currentTimeMillis();
                    mc.thePlayer
                        .addChatMessage(new ChatComponentText("§8[§5Squads Debug§8] Scoreboard detection: " + (flag ? "§aDETECTED" : "§cNOT DETECTED")));

                    for (String str : this.entries2) {
                        MessageUtils.x("Squads Debug sidebar: " + str);
                    }
                }

                this.flag4 = flag;
                this.flag3 = true;
            } else if (event instanceof EventRender2D) {
                if ((this.active || EditableModulesHelper.isActive()) && this.showInfoSetting.isEnabled()) {
                    this.process4();
                }
            } else if (event instanceof EventRender3D && this.active && this.bannerESPSetting.isEnabled()) {
                this.process5();
            }
        }
    }

    private static int[] computeIntArray(String str) {
        Matcher matcher = Pattern.compile("(\\d+)\\s*/\\s*(\\d+)").matcher(str);
        return matcher.find() ? new int[]{computeInt(matcher.group(1)), computeInt(matcher.group(2))} : null;
    }

    private static void process3() {
        int ticksExisted = mc.thePlayer.ticksExisted;
        if (object != mc.theWorld || count != ticksExisted) {
            object = mc.theWorld;
            count = ticksExisted;
            flag = false;
            text = null;

            try {
                Scoreboard scoreboard = mc.theWorld.getScoreboard();
                ScoreObjective objective = computeScoreObjective(scoreboard);
                if (objective == null) {
                    return;
                }

                for (Score score : scoreboard.getSortedScores(objective)) {
                    String playerName = score.getPlayerName();
                    if (playerName != null && !playerName.startsWith("#")) {
                        String str = computeString(ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(playerName), playerName));
                        String str2 = str.toLowerCase(Locale.ROOT);
                        if (str2.contains("squads") && !str2.contains("next")) {
                            flag = true;
                        }

                        Matcher matcher = pattern7.matcher(str);
                        if (matcher.find()) {
                            text = matcher.group(1);
                        }
                    }
                }

                String str3 = computeString(objective.getDisplayName()).toLowerCase(Locale.ROOT);
                if (str3.contains("squads") && !str3.contains("next")) {
                    flag = true;
                }

                if (text == null && mc.thePlayer.getDisplayName() != null) {
                    text = computeString2(mc.thePlayer.getDisplayName().getFormattedText());
                }
            } catch (Exception e) {
                flag = false;
                text = null;
            }
        }
    }

    private static boolean matchesString(String str, String str2) {
        return str != null && str.equalsIgnoreCase(computeString2(str2));
    }

    private void process() {
        this.entries.clear();
        ArrayList<EntityArmorStand> entities = new ArrayList<>();

        for (Object obj : mc.theWorld.loadedEntityList) {
            if (obj instanceof EntityArmorStand && ((EntityArmorStand)obj).hasCustomName()) {
                entities.add((EntityArmorStand)obj);
            }
        }

        HashSet set = new HashSet();

        for (EntityArmorStand entity : entities) {
            String str = StringUtils.stripControlCodes(entity.getCustomNameTag()).trim();
            String str2 = str;
            String str3 = computeString(str, this.text4);
            if (str3 != null || str.toLowerCase(Locale.ROOT).contains("banner")) {
                if (str3 == null) {
                    for (EntityArmorStand entity2 : ((Iterable<EntityArmorStand>)(Object)(entities))) {
                        if (Math.abs(entity2.posX - entity.posX) <= 0.5 && Math.abs(entity2.posZ - entity.posZ) <= 0.5 && Math.abs(entity2.posY - entity.posY) <= 2.5) {
                            str3 = computeString(entity2.getCustomNameTag(), this.text4);
                            if (str3 != null) {
                                str2 = entity2.getCustomNameTag();
                                break;
                            }
                        }
                    }
                }

                if (str3 != null) {
                    Squads.Entry2 entry2 = null;

                    for (EntityArmorStand entity3 : ((Iterable<EntityArmorStand>)(Object)(entities))) {
                        if (Math.abs(entity3.posX - entity.posX) <= 0.6 && Math.abs(entity3.posZ - entity.posZ) <= 0.6 && Math.abs(entity3.posY - entity.posY) <= 2.5) {
                            Squads.Entry2 entry22 = computeEntry2(entity3.getCustomNameTag());
                            if (entry22 != null) {
                                entry2 = entry22;
                                break;
                            }
                        }
                    }

                    BlockPos pos = this.computeBlockPos(entity);
                    if (pos == null || set.add(pos)) {
                        double d = pos == null ? entity.posX : pos.getX() + 0.5;
                        double d2 = pos == null ? entity.posY - 1.5 : pos.getY();
                        double d3 = pos == null ? entity.posZ : pos.getZ() + 0.5;
                        int n = str3.equals("Claimed") ? -11141291 : (str3.equals("Unclaimed Banner") ? -171 : -43691);
                        String str4 = computeString2(str2);
                        if (entry2 != null && str3.equals("Claimed") && str4 != null) {
                            str3 = str3 + " [" + str4.toUpperCase(Locale.ROOT) + "]";
                        }

                        this.entries.add(new Squads.Entry(d, d2, d3, n, str3, entry2));
                    }
                }
            }
        }
    }

    public static boolean matchesEntityPlayer2(EntityPlayer player) {
        if (player != null && mc.thePlayer != null && mc.theWorld != null) {
            process3();
            return flag && text != null && player.getDisplayName() != null && computeString2(player.getDisplayName().getFormattedText()) != null;
        }
        return false;
    }

    private static double computeDouble(double d) {
        return 0.025 * (1.0 + Math.max(0.0, d) * 0.05);
    }

    private static String computeString(String str, String str2) {
        String str3 = StringUtils.stripControlCodes(str).trim();
        String str4 = str3.toLowerCase(Locale.ROOT);
        if (str4.contains("uncontrolled") || str4.contains("unclaimed") || str4.contains("not claimed")) {
            return "Unclaimed Banner";
        }
        if (!str4.contains("controlled by") && !str4.contains("claimed by") && !str4.contains("claimed")) {
            return null;
        }
        String str5 = computeString2(str3);
        return matchesString(str2, str3) ? "Claimed" : "Enemy Banner" + (str5 == null ? "" : " [" + str5.toUpperCase(Locale.ROOT) + "]");
    }

    public static final class Entry {
        public final double amount;
        public final double amount2;
        public final double amount3;
        public final int count;
        public final String text;
        public final Squads.Entry2 entry2;

        public Entry(double amount, double amount2, double amount3, int count, String text, Squads.Entry2 entry2) {
            this.amount = amount;
            this.amount2 = amount2;
            this.amount3 = amount3;
            this.count = count;
            this.text = text;
            this.entry2 = entry2;
        }
    }

    public static final class Entry2 {
        public final String text;
        public final int count;

        public Entry2(String text, int count) {
            this.text = text;
            this.count = count;
        }
    }
}
