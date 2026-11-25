package org.brutality.module.impl.visual;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.brutality.event.Event;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.MultiBoolSetting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.TruceManager;
import org.brutality.utils.WHelper;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.render.BindTextureHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ESP extends Module {
    private static ESP instance;
    private final BooleanSetting kosOnlySetting = new BooleanSetting("KOS Only", this, false);
    private final BooleanSetting friendsOnlySetting = new BooleanSetting("Friends Only", this, false);
    private final BooleanSetting trucesOnlySetting = new BooleanSetting("Truces Only", this, false);
    private final ModeSetting arrayListSetting = new ModeSetting("Color Mode", this, "Theme", Arrays.asList("Theme", "ArrayList"));
    private final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);
    private final IntBuffer intBuffer = BufferUtils.createIntBuffer(16);
    private final FloatBuffer floatBuffer3 = BufferUtils.createFloatBuffer(4);
    private final MultiBoolSetting healthSetting = new MultiBoolSetting("Elements", this, Arrays.asList("Box", "Health"), Arrays.asList("Box", "Health"));

    private int computeInt(String str, int n) {
        boolean flag = WHelper.matchesString(str);
        boolean flag2 = DefaultHelper.matchesString(str);
        boolean flag3 = TruceManager.matchesString(str);
        if (!this.kosOnlySetting.isEnabled() && !this.friendsOnlySetting.isEnabled() && !this.trucesOnlySetting.isEnabled()
            || this.kosOnlySetting.isEnabled() && flag
            || this.friendsOnlySetting.isEnabled() && flag2
            || this.trucesOnlySetting.isEnabled() && flag3) {
            if (flag) {
                return -173408256;
            }
            if (flag2) {
                return -184505856;
            }
            return flag3 ? -184549206 : n;
        }
        return 0;
    }

    private void handleFloat(float f, float f2, float f3, float f4, float f5, int n) {
        BindTextureHelper.handleFloat2(f, f2, f3, f2 + f5, n);
        BindTextureHelper.handleFloat2(f, f4 - f5, f3, f4, n);
        BindTextureHelper.handleFloat2(f, f2 + f5, f + f5, f4 - f5, n);
        BindTextureHelper.handleFloat2(f3 - f5, f2 + f5, f3, f4 - f5, n);
    }

    private ESP.Entry computeEntry(EntityPlayer player, float f, int n, int n2) {
        double d = computeDouble(player.lastTickPosX, player.posX, f);
        double d2 = computeDouble(player.lastTickPosY, player.posY, f);
        double d3 = computeDouble(player.lastTickPosZ, player.posZ, f);
        AxisAlignedBB box = player.getEntityBoundingBox().offset(d - player.posX, d2 - player.posY, d3 - player.posZ).expand(0.05, 0.08, 0.05);
        if (!ResetColorHelper.matchesAxisAlignedBB(box)) {
            return null;
        }
        double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
        double POSITIVE_INFINITY2 = Double.POSITIVE_INFINITY;
        double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
        double NEGATIVE_INFINITY2 = Double.NEGATIVE_INFINITY;
        double viewerPosX = mc.getRenderManager().viewerPosX;
        double viewerPosY = mc.getRenderManager().viewerPosY;
        double viewerPosZ = mc.getRenderManager().viewerPosZ;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    double[] doubles = this.computeDoubleArray(
                        (i == 0 ? box.minX : box.maxX) - viewerPosX,
                        (j == 0 ? box.minY : box.maxY) - viewerPosY,
                        (k == 0 ? box.minZ : box.maxZ) - viewerPosZ,
                        n,
                        n2
                    );
                    if (doubles == null) {
                        return null;
                    }

                    POSITIVE_INFINITY = Math.min(POSITIVE_INFINITY, doubles[0]);
                    POSITIVE_INFINITY2 = Math.min(POSITIVE_INFINITY2, doubles[1]);
                    NEGATIVE_INFINITY = Math.max(NEGATIVE_INFINITY, doubles[0]);
                    NEGATIVE_INFINITY2 = Math.max(NEGATIVE_INFINITY2, doubles[1]);
                }
            }
        }

        if (!(NEGATIVE_INFINITY < 0.0) && !(POSITIVE_INFINITY > n) && !(NEGATIVE_INFINITY2 < 0.0) && !(POSITIVE_INFINITY2 > n2) && !(NEGATIVE_INFINITY - POSITIVE_INFINITY < 1.0) && !(NEGATIVE_INFINITY2 - POSITIVE_INFINITY2 < 2.0)) {
            double d4 = (NEGATIVE_INFINITY - POSITIVE_INFINITY) * 0.1;
            POSITIVE_INFINITY += d4;
            NEGATIVE_INFINITY -= d4;
            return new ESP.Entry((float)POSITIVE_INFINITY, (float)POSITIVE_INFINITY2, (float)NEGATIVE_INFINITY, (float)NEGATIVE_INFINITY2, player.getHealth(), player.getMaxHealth());
        }
        return null;
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    public static void handleEntityPlayer(EntityPlayer player, float f, int n, String str) {
        ESP esp = instance;
        if (esp != null && mc.thePlayer != null && mc.theWorld != null && player != null && player != mc.thePlayer && player.isEntityAlive()) {
            ScaledResolution sr = new ScaledResolution(mc);
            esp.process();
            ESP.Entry entry = esp.computeEntry(player, f, sr.getScaledWidth(), sr.getScaledHeight());
            if (entry != null) {
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glPushMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glPushMatrix();
                GL11.glPushAttrib(24836);

                try {
                    mc.entityRenderer.setupOverlayRendering();
                    GlStateManager.disableDepth();
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    esp.handleEntry(entry, n);
                    if (str != null && !str.isEmpty()) {
                        GlStateManager.enableTexture2D();
                        computeFontRenderer(ESP.class)
                            .drawStringWithShadow(
                                str,
                                (entry.factor + entry.factor3 - computeFontRenderer(ESP.class).getStringWidth(str)) / 2.0F,
                                entry.factor2 - computeFontRenderer(ESP.class).FONT_HEIGHT - 2.0F,
                                n
                            );
                    }
                } finally {
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GL11.glPopAttrib();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_PROJECTION);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GlStateManager.resetColor();
                }
            }
        }
    }

    private List<ESP.Entry> computeList(float f) {
        var players = EntityCache.getEntries();
        if (players.isEmpty() && mc.theWorld != null) {
            players = mc.theWorld.playerEntities;
        }

        if (players != null && !players.isEmpty()) {
            java.util.ArrayList list = new java.util.ArrayList(players.size());
            ScaledResolution sr = new ScaledResolution(mc);
            int scaledWidth = sr.getScaledWidth();
            int scaledHeight = sr.getScaledHeight();
            this.process();
            double viewerPosX = mc.getRenderManager().viewerPosX;
            double viewerPosY = mc.getRenderManager().viewerPosY;
            double viewerPosZ = mc.getRenderManager().viewerPosZ;
            Color color = this.computeColor();
            int rgb = new Color(color.getRed(), color.getGreen(), color.getBlue(), 245).getRGB();

            for (EntityPlayer player : players) {
                if (player != null && player != mc.thePlayer && player.isEntityAlive()) {
                    double d = computeDouble(player.lastTickPosX, player.posX, f);
                    double d2 = computeDouble(player.lastTickPosY, player.posY, f);
                    double d3 = computeDouble(player.lastTickPosZ, player.posZ, f);
                    AxisAlignedBB box = player.getEntityBoundingBox()
                        .offset(d - player.posX, d2 - player.posY, d3 - player.posZ)
                        .expand(0.05, 0.08, 0.05);
                    if (ResetColorHelper.matchesAxisAlignedBB(box)) {
                        int n = this.computeInt(player.getName(), rgb);
                        if (n != 0) {
                            double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
                            double POSITIVE_INFINITY2 = Double.POSITIVE_INFINITY;
                            double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
                            double NEGATIVE_INFINITY2 = Double.NEGATIVE_INFINITY;
                            boolean flag = true;

                            for (int i = 0; i < 2 && flag; i++) {
                                for (int j = 0; j < 2 && flag; j++) {
                                    for (int k = 0; k < 2; k++) {
                                        double[] doubles = this.computeDoubleArray(
                                            (i == 0 ? box.minX : box.maxX) - viewerPosX,
                                            (j == 0 ? box.minY : box.maxY) - viewerPosY,
                                            (k == 0 ? box.minZ : box.maxZ) - viewerPosZ,
                                            scaledWidth,
                                            scaledHeight
                                        );
                                        if (doubles == null) {
                                            flag = false;
                                            break;
                                        }

                                        POSITIVE_INFINITY = Math.min(POSITIVE_INFINITY, doubles[0]);
                                        POSITIVE_INFINITY2 = Math.min(POSITIVE_INFINITY2, doubles[1]);
                                        NEGATIVE_INFINITY = Math.max(NEGATIVE_INFINITY, doubles[0]);
                                        NEGATIVE_INFINITY2 = Math.max(NEGATIVE_INFINITY2, doubles[1]);
                                    }
                                }
                            }

                            if (flag
                                && !(NEGATIVE_INFINITY < 0.0)
                                && !(POSITIVE_INFINITY > scaledWidth)
                                && !(NEGATIVE_INFINITY2 < 0.0)
                                && !(POSITIVE_INFINITY2 > scaledHeight)
                                && !(NEGATIVE_INFINITY - POSITIVE_INFINITY < 1.0)
                                && !(NEGATIVE_INFINITY2 - POSITIVE_INFINITY2 < 2.0)) {
                                double d4 = (NEGATIVE_INFINITY - POSITIVE_INFINITY) * 0.1;
                                POSITIVE_INFINITY += d4;
                                NEGATIVE_INFINITY -= d4;
                                list.add(new ESP.Entry((float)POSITIVE_INFINITY, (float)POSITIVE_INFINITY2, (float)NEGATIVE_INFINITY, (float)NEGATIVE_INFINITY2, player.getHealth(), player.getMaxHealth(), n));
                            }
                        }
                    }
                }
            }

            return list;
        }
        return Collections.emptyList();
    }

    private void handleEntry(ESP.Entry entry) {
        float f = entry.factor6 <= 0.0F ? 0.0F : clamp(entry.factor5 / entry.factor6, 0.0F, 1.0F);
        float f2 = entry.factor - 4.25F;
        float f3 = entry.factor2;
        float f4 = entry.factor4;
        BindTextureHelper.handleFloat2(f2, f3, f2 + 2.5F, f4, -654311424);
        float f5 = f4 - 1.0F - (f4 - f3 - 2.0F) * f;
        int n = Math.round(255.0F * (1.0F - f));
        int n2 = Math.round(220.0F * f);
        int rgb = new Color(n, n2, 55, 255).getRGB();
        BindTextureHelper.handleFloat2(f2 + 0.75F, f5, f2 + 1.75F, f4 - 1.0F, rgb);
    }

    private Color computeColor() {
        if (this.arrayListSetting.matchesString2("ArrayList")) {
            return ArrayList.computeColor(this);
        }
        BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
        return brutalityHelper != null && brutalityHelper.getColor() != null ? brutalityHelper.getColor() : Color.WHITE;
    }

    private double[] computeDoubleArray(double d, double d2, double d3, int n, int n2) {
        this.floatBuffer3.clear();
        if (!GLU.gluProject((float)d, (float)d2, (float)d3, this.floatBuffer, this.floatBuffer2, this.intBuffer, this.floatBuffer3)) {
            return null;
        }
        float f = this.floatBuffer3.get(2);
        if (!(f < 0.0F) && !(f >= 1.0F)) {
            double d4 = (double)this.intBuffer.get(2) / n;
            double d5 = (double)this.intBuffer.get(3) / n2;
            double d6 = (this.floatBuffer3.get(0) - this.intBuffer.get(0)) / d4;
            double d7 = (this.intBuffer.get(3) - (this.floatBuffer3.get(1) - this.intBuffer.get(1))) / d5;
            return new double[]{d6, d7, f};
        }
        return null;
    }

    private void process() {
        this.floatBuffer.clear();
        this.floatBuffer2.clear();
        this.intBuffer.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, this.floatBuffer);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, this.floatBuffer2);
        GL11.glGetInteger(GL11.GL_VIEWPORT, this.intBuffer);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && event instanceof GetPartialTicksEvent && mc.thePlayer != null && mc.theWorld != null) {
            GetPartialTicksEvent getPartialTicksEvent = (GetPartialTicksEvent)event;
            var list = this.computeList(getPartialTicksEvent.getPartialTicks());
            if (!list.isEmpty()) {
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glPushMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glPushMatrix();
                GL11.glPushAttrib(24836);

                try {
                    mc.entityRenderer.setupOverlayRendering();
                    GlStateManager.disableDepth();
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

                    for (ESP.Entry entry : list) {
                        if (this.healthSetting.matchesString("Box")) {
                            this.handleEntry(entry, entry.count);
                        }

                        if (this.healthSetting.matchesString("Health")) {
                            this.handleEntry(entry);
                        }
                    }
                } finally {
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.popAttrib();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_PROJECTION);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GlStateManager.resetColor();
                }
            }
        }
    }

    private void handleEntry(ESP.Entry entry, int n) {
        this.handleFloat(entry.factor, entry.factor2, entry.factor3, entry.factor4, 1.8F, -654311424);
        this.handleFloat(entry.factor + 0.75F, entry.factor2 + 0.75F, entry.factor3 - 0.75F, entry.factor4 - 0.75F, 0.85F, n);
    }

    public ESP() {
        super("ESP", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(new Setting[]{this.healthSetting, this.kosOnlySetting, this.friendsOnlySetting, this.trucesOnlySetting, this.arrayListSetting});
        instance = this;
    }

    private static double computeDouble(double d, double d2, float f) {
        return d + (d2 - d) * f;
    }

    public static final class Entry {
        public final float factor;
        public final float factor2;
        public final float factor3;
        public final float factor4;
        public final float factor5;
        public final float factor6;
        public final int count;

        public Entry(float factor, float f, float f2, float f3, float f4, float f5, int count) {
            this.count = count;
            this.factor = factor;
            this.factor2 = f;
            this.factor3 = f2;
            this.factor4 = f3;
            this.factor5 = f4;
            this.factor6 = f5;
        }

        public Entry(float f, float f2, float f3, float f4, float f5, float f6) {
            this(f, f2, f3, f4, f5, f6, 0);
        }
    }
}
