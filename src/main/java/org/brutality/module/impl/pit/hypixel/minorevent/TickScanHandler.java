package org.brutality.module.impl.pit.hypixel.minorevent;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public final class TickScanHandler {
    private final Module module;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting outlineSetting;
    private final BooleanSetting fillSetting;
    private final BooleanSetting tracersSetting;
    private final BooleanSetting showDistanceSetting;
    private final BooleanSetting moveNotificationSetting;
    private final BooleanSetting moveTracerSetting;
    private final NumberSetting notificationDurationSSetting;
    private final NumberSetting fillOpacitySetting;
    private final ModeSetting chromaSetting;
    private final ColorSetting eggColorSetting;
    private final ColorSetting tracerColorSetting;
    private final ColorSetting moveTracerColorSetting;
    private static final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);
    private static final IntBuffer intBuffer = BufferUtils.createIntBuffer(16);
    private static final FloatBuffer floatBuffer3 = BufferUtils.createFloatBuffer(3);
    private final ArrayDeque<long[]> arrayDeque = new ArrayDeque<>();
    private final Map<BlockPos, Long> lookupMap = new LinkedHashMap<>();
    private final Map<BlockPos, BlockPos> lookupMap2 = new LinkedHashMap<>();
    private final List<TickScanHandler.Entry> entries = new ArrayList<>();
    private static final int count = 20;

    private BlockPos computeBlockPos(BlockPos pos) {
        for (BlockPos pos2 : this.lookupMap.keySet()) {
            if (mc.theWorld.getBlockState(pos2).getBlock() != Blocks.dragon_egg && pos2.distanceSq(pos) <= 2500.0) {
                return pos2;
            }
        }

        return null;
    }

    public void onEnable() {
        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.entries.clear();
        this.arrayDeque.clear();
    }

    private Color computeColor() {
        if (this.chromaSetting.matchesString2("Chroma")) {
            return this.computeColor(0L);
        }
        Color color = this.eggColorSetting.computeColor();
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)this.fillOpacitySetting.computeDouble());
    }

    private void process() {
        if (mc.thePlayer != null && !this.lookupMap.isEmpty()) {
            Color color = this.computeColor();
            Color color2 = this.computeColor3();
            Color color3 = this.computeColor2();

            for (BlockPos pos : this.lookupMap.keySet()) {
                if (this.outlineSetting.isEnabled() || this.fillSetting.isEnabled()) {
                    ResetColorHelper.handleBlockPos2(pos, color, this.outlineSetting.isEnabled(), this.fillSetting.isEnabled(), 2.0F);
                }

                if (this.tracersSetting.isEnabled()) {
                    ResetColorHelper.handleBlockPos(pos, color2.getRGB());
                }
            }

            if (this.moveTracerSetting.isEnabled()) {
                for (BlockPos pos2 : this.lookupMap2.keySet()) {
                    ResetColorHelper.handleBlockPos(pos2, color3.getRGB());
                }
            }
        }
    }

    public TickScanHandler(Module module) {
        this.module = module;
        this.outlineSetting = new BooleanSetting("Outline", module, true);
        this.fillSetting = new BooleanSetting("Fill", module, true);
        this.tracersSetting = new BooleanSetting("Tracers", module, true);
        this.showDistanceSetting = new BooleanSetting("Show Distance", module, true);
        this.moveNotificationSetting = new BooleanSetting("Move Notification", module, true);
        this.moveTracerSetting = new BooleanSetting("Move Tracer", module, true);
        this.notificationDurationSSetting = new NumberSetting("Notification Duration (s)", module, 3.0, 1.0, 10.0, 0.5);
        this.fillOpacitySetting = new NumberSetting("Fill Opacity", module, 80.0, 0.0, 255.0, 5.0);
        this.chromaSetting = new ModeSetting("Color Mode", module, "Custom", Arrays.asList("Custom", "Chroma"));
        this.eggColorSetting = new ColorSetting("Egg Color", module, 0.75F).computeColorSetting(() -> this.chromaSetting.matchesString2("Custom"));
        this.tracerColorSetting = new ColorSetting("Tracer Color", module, 0.83F)
            .computeColorSetting(() -> this.chromaSetting.matchesString2("Custom") && this.tracersSetting.isEnabled());
        this.moveTracerColorSetting = new ColorSetting("Move Tracer Color", module, 0.0F)
            .computeColorSetting(() -> this.chromaSetting.matchesString2("Custom") && this.moveTracerSetting.isEnabled());
        module.handleSettingArray(
            this.outlineSetting,
            this.fillSetting,
            this.tracersSetting,
            this.showDistanceSetting,
            this.moveNotificationSetting,
            this.moveTracerSetting,
            this.notificationDurationSSetting,
            this.fillOpacitySetting,
            this.chromaSetting,
            this.eggColorSetting,
            this.tracerColorSetting,
            this.moveTracerColorSetting
        );
    }

    public void onEvent(Event event) {
        if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
            this.tickScan();
        } else if (event instanceof EventRender3D) {
            this.process();
        } else if (event instanceof EventRender2D) {
            this.render2D();
        }
    }

    private Color computeColor2() {
        return this.chromaSetting.matchesString2("Chroma") ? this.computeColor(400L) : this.moveTracerColorSetting.computeColor();
    }

    private void tickScan() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (this.arrayDeque.isEmpty()) {
                this.process2();
            }

            for (int i = 0; !this.arrayDeque.isEmpty() && i < 20; i++) {
                long[] ls = this.arrayDeque.poll();
                int n = (int)ls[0];
                int n2 = (int)ls[1];
                if (mc.theWorld.getChunkProvider().chunkExists(n, n2)) {
                    this.handleChunk(mc.theWorld.getChunkFromChunkCoords(n, n2), n, n2);
                }
            }

            this.lookupMap.keySet().removeIf(pos -> mc.theWorld.getBlockState(pos).getBlock() != Blocks.dragon_egg);
            this.lookupMap2.keySet().removeIf(pos2 -> !this.lookupMap.containsKey(pos2));
        }
    }

    private void handleChunk(Chunk chunk, int n, int n2) {
        ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
        long now = System.currentTimeMillis();

        for (int i = 0; i < blockStorageArray.length; i++) {
            ExtendedBlockStorage extendedBlockStorage = blockStorageArray[i];
            if (extendedBlockStorage != null && !extendedBlockStorage.isEmpty()) {
                int n3 = i << 4;

                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 16; k++) {
                        for (int idx = 0; idx < 16; idx++) {
                            int n4 = (n << 4) + j;
                            int n5 = n3 + idx;
                            int n6 = (n2 << 4) + k;
                            BlockPos pos = new BlockPos(n4, n5, n6);
                            if (chunk.getBlockState(pos).getBlock() == Blocks.dragon_egg && !this.lookupMap.containsKey(pos)) {
                                BlockPos pos2 = this.computeBlockPos(pos);
                                if (pos2 != null) {
                                    this.lookupMap2.put(pos, pos2);
                                    this.lookupMap.remove(pos2);
                                    if (this.moveNotificationSetting.isEnabled()) {
                                        double dist = mc.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                                        this.entries.add(new TickScanHandler.Entry(pos, dist));
                                    }
                                }

                                this.lookupMap.put(pos, now);
                            }
                        }
                    }
                }
            }
        }
    }

    private void render2D() {
        FontRenderer fontRenderer = this.module.computeFontRenderer2();
        ScaledResolution sr = new ScaledResolution(mc);
        long now = System.currentTimeMillis();
        if (this.showDistanceSetting.isEnabled() && !this.lookupMap.isEmpty()) {
            for (BlockPos pos : this.lookupMap.keySet()) {
                float f = (float)(pos.getX() + 0.5 - mc.getRenderManager().viewerPosX);
                float f2 = (float)(pos.getY() + 1.3 - mc.getRenderManager().viewerPosY);
                float f3 = (float)(pos.getZ() + 0.5 - mc.getRenderManager().viewerPosZ);
                float[] floats = computeFloatArray(f, f2, f3);
                if (floats != null && !(floats[2] < 0.0F) && !(floats[2] >= 1.0F)) {
                    double dist = mc.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    String str = String.format("§5Egg §7%.0fm", dist);
                    int textWidth = fontRenderer.getStringWidth(str);
                    int n = (int)floats[0] - textWidth / 2;
                    int n2 = (int)floats[1];
                    GL11.glPushMatrix();
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
                    GL11.glBegin(GL11.GL_QUADS);
                    GL11.glVertex2f(n - 2, n2 - 1);
                    GL11.glVertex2f(n - 2, n2 + fontRenderer.FONT_HEIGHT + 1);
                    GL11.glVertex2f(n + textWidth + 2, n2 + fontRenderer.FONT_HEIGHT + 1);
                    GL11.glVertex2f(n + textWidth + 2, n2 - 1);
                    GL11.glEnd();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glPopMatrix();
                    fontRenderer.drawStringWithShadow(str, n, n2, -1);
                }
            }
        }

        if (this.moveNotificationSetting.isEnabled() && !this.entries.isEmpty()) {
            long l = (long)(this.notificationDurationSSetting.computeDouble() * 1000.0);
            this.entries.removeIf(entry -> now - entry.timestamp > l);
            int n3 = sr.getScaledHeight() / 2 - 50;

            for (TickScanHandler.Entry entry2 : this.entries) {
                float f4 = 1.0F - (float)(now - entry2.timestamp) / (float)l;
                String str2 = String.format("§6§lEGG MOVED! §r§7(%.0fm)", entry2.amount);
                int textWidth2 = fontRenderer.getStringWidth(str2);
                int n4 = sr.getScaledWidth() / 2 - textWidth2 / 2;
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F * f4);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2f(n4 - 5, n3 - 2);
                GL11.glVertex2f(n4 - 5, n3 + fontRenderer.FONT_HEIGHT + 2);
                GL11.glVertex2f(n4 + textWidth2 + 5, n3 + fontRenderer.FONT_HEIGHT + 2);
                GL11.glVertex2f(n4 + textWidth2 + 5, n3 - 2);
                GL11.glEnd();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
                fontRenderer.drawStringWithShadow(str2, n4, n3, new Color(1.0F, 1.0F, 1.0F, f4).getRGB());
                n3 += fontRenderer.FONT_HEIGHT + 6;
            }
        }
    }

    private Color computeColor(long l) {
        float f = (float)((System.currentTimeMillis() + l) % 3000L) / 3000.0F;
        return Color.getHSBColor(f, 1.0F, 1.0F);
    }

    public void onDisable() {
        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.entries.clear();
        this.arrayDeque.clear();
    }

    private Color computeColor3() {
        return this.chromaSetting.matchesString2("Chroma") ? this.computeColor(200L) : this.tracerColorSetting.computeColor();
    }

    private void process2() {
        int renderDistanceChunks = mc.gameSettings.renderDistanceChunks;
        int n = (int)mc.thePlayer.posX >> 4;
        int n2 = (int)mc.thePlayer.posZ >> 4;

        for (int i = n - renderDistanceChunks; i <= n + renderDistanceChunks; i++) {
            for (int j = n2 - renderDistanceChunks; j <= n2 + renderDistanceChunks; j++) {
                if (mc.theWorld.getChunkProvider().chunkExists(i, j)) {
                    this.arrayDeque.add(new long[]{i, j});
                }
            }
        }
    }

    private static float[] computeFloatArray(float f, float f2, float f3) {
        try {
            floatBuffer.clear();
            floatBuffer2.clear();
            intBuffer.clear();
            floatBuffer3.clear();
            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, floatBuffer);
            GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, floatBuffer2);
            GL11.glGetInteger(GL11.GL_VIEWPORT, intBuffer);
            boolean flag = GLU.gluProject(f, f2, f3, floatBuffer, floatBuffer2, intBuffer, floatBuffer3);
            if (!flag) {
                return null;
            }
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            int scaleFactor = sr.getScaleFactor();
            return new float[]{floatBuffer3.get(0) / scaleFactor, (intBuffer.get(3) - floatBuffer3.get(1)) / scaleFactor, floatBuffer3.get(2)};
        } catch (Exception e) {
            return null;
        }
    }

    public static class Entry {
        public final BlockPos blockPos;
        public final double amount;
        public final long timestamp = System.currentTimeMillis();

        public Entry(BlockPos blockPos, double amount) {
            this.blockPos = blockPos;
            this.amount = amount;
        }
    }
}
