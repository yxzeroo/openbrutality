package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.Chunk;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.opengl.GL11;

public class XRay extends Module {
    private static final int count = 16384;
    private final BooleanSetting diamondOreSetting = new BooleanSetting("Diamond ore", this, true);
    private final BooleanSetting goldOreSetting = new BooleanSetting("Gold ore", this, true);
    private final BooleanSetting ironOreSetting = new BooleanSetting("Iron ore", this, true);
    private final BooleanSetting coalOreSetting = new BooleanSetting("Coal ore", this, false);
    private final BooleanSetting redstoneOreSetting = new BooleanSetting("Redstone ore", this, false);
    private final BooleanSetting emeraldOreSetting = new BooleanSetting("Emerald ore", this, true);
    private final BooleanSetting lapisOreSetting = new BooleanSetting("Lapis ore", this, true);
    private final BooleanSetting sugarCaneSetting = new BooleanSetting("Sugar cane", this, false);
    private final NumberSetting scanIntervalSetting = new NumberSetting("Scan interval", this, 3000.0, 250.0, 15000.0, 250.0);
    private final NumberSetting scanRangeSetting = new NumberSetting("Scan range", this, 24.0, 4.0, 512.0, 1.0);
    private final BooleanSetting boxESPSetting = new BooleanSetting("Box ESP", this, true);
    private final BooleanSetting tracersSetting = new BooleanSetting("Tracers", this, false);
    private final NumberSetting alphaSetting = new NumberSetting("Alpha", this, 45.0, 0.0, 255.0, 1.0);
    private final ModeSetting caveSetting = new ModeSetting("Exposure mode", this, "All", Arrays.asList("All", "Exposed", "Cave"));
    private final NumberSetting markerCapSetting = new NumberSetting("Marker cap", this, 256.0, 64.0, 4096.0, 64.0);
    private final List<BlockPos> entries = new java.util.ArrayList<>();
    private final List<BlockPos> entries2 = new java.util.ArrayList<>();
    private BlockPos blockPos;
    private int count2;
    private long timestamp;
    private boolean flag;

    private void handleBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
        this.entries2.clear();
        this.count2 = 0;
        this.flag = true;
        int n = (int)this.scanRangeSetting.computeDouble();

        for (int i = blockPos.getX() - n >> 4; i <= blockPos.getX() + n >> 4; i++) {
            for (int j = blockPos.getZ() - n >> 4; j <= blockPos.getZ() + n >> 4; j++) {
                Chunk chunkFromChunkCoords = mc.theWorld.getChunkFromChunkCoords(i, j);
                if (chunkFromChunkCoords != null && !chunkFromChunkCoords.isEmpty()) {
                    int n2 = i << 4;
                    int n3 = j << 4;

                    for (int k = 0; k < 16; k++) {
                        for (int idx = 0; idx < 16; idx++) {
                            if (this.matchesInt(n2 + k, n3 + idx, n)) {
                                for (int idx2 = 0; idx2 < 256; idx2++) {
                                    this.entries2.add(new BlockPos(n2 + k, idx2, n3 + idx));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.process3();
    }

    private Color computeColor(Color color, Color color2, float f) {
        return new Color(
            (int)(color.getRed() + (color2.getRed() - color.getRed()) * f),
            (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f),
            (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f)
        );
    }

    private void handleDouble(double d, double d2, double d3, double d4, double d5, double d6) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(d, d2, d3);
        GL11.glVertex3d(d4, d2, d3);
        GL11.glVertex3d(d4, d2, d6);
        GL11.glVertex3d(d, d2, d6);
        GL11.glVertex3d(d, d5, d3);
        GL11.glVertex3d(d, d5, d6);
        GL11.glVertex3d(d4, d5, d6);
        GL11.glVertex3d(d4, d5, d3);
        GL11.glVertex3d(d, d2, d3);
        GL11.glVertex3d(d, d5, d3);
        GL11.glVertex3d(d4, d5, d3);
        GL11.glVertex3d(d4, d2, d3);
        GL11.glVertex3d(d, d2, d6);
        GL11.glVertex3d(d4, d2, d6);
        GL11.glVertex3d(d4, d5, d6);
        GL11.glVertex3d(d, d5, d6);
        GL11.glVertex3d(d, d2, d3);
        GL11.glVertex3d(d, d2, d6);
        GL11.glVertex3d(d, d5, d6);
        GL11.glVertex3d(d, d5, d3);
        GL11.glVertex3d(d4, d2, d3);
        GL11.glVertex3d(d4, d5, d3);
        GL11.glVertex3d(d4, d5, d6);
        GL11.glVertex3d(d4, d2, d6);
        GL11.glEnd();
    }

    public XRay() {
        super("XRay", 0, Category.VISUAL);
        this.process18();
    }

    @Override
    public String getSuffix() {
        return String.valueOf(this.entries.size());
    }

    private void process3() {
        this.blockPos = null;
        this.count2 = 0;
        this.flag = false;
        this.entries.clear();
        this.entries2.clear();
    }

    private void onEventRender2D(EventRender2D event) {
        if (mc.theWorld != null) {
            ScaledResolution scaledResolution = event.getScaledResolution();
            float f = this.computeFloat();
            String str = !this.flag && this.blockPos != null
                ? String.format(
                    Locale.ROOT,
                    "Refresh in %.1fs",
                    Math.max(0.0, this.scanIntervalSetting.computeDouble() - (System.currentTimeMillis() - this.timestamp)) / 1000.0
                )
                : "Scanning " + Math.round(f * 100.0F) + "%";
            String str2 = str + " | Found " + this.entries.size();
            int n = scaledResolution.getScaledWidth() / 2;
            int n2 = n - 60;
            int n3 = scaledResolution.getScaledHeight() - 70;
            int n4 = Math.max(0, Math.min(120, Math.round(f * 120.0F)));
            Gui.drawRect(n2, n3, n2 + 121, n3 + 3, -805306368);
            if (n4 > 0) {
                Gui.drawRect(n2 + 1, n3 + 1, n2 + 1 + n4, n3 + 3, this.computeColor(f).getRGB());
            }

            computeFontRenderer(XRay.class).drawStringWithShadow(str2, n - computeFontRenderer(XRay.class).getStringWidth(str2) / 2.0F, n3 - 12, -1);
        }
    }

    private void process() {
        if (this.flag && !this.entries2.isEmpty()) {
            int n = (int)this.scanRangeSetting.computeDouble();

            for (int i = 0; this.count2 < this.entries2.size() && i < 16384; i++) {
                BlockPos pos = this.entries2.get(this.count2);
                if (this.matchesInt(pos.getX(), pos.getZ(), n)) {
                    Color color = this.computeColor(mc.theWorld.getBlockState(pos).getBlock());
                    if (color != null && this.matchesBlockPos(pos) && !this.entries.contains(pos)) {
                        this.entries.add(pos);
                    }
                }

                this.count2++;
            }

            if (this.count2 >= this.entries2.size()) {
                this.flag = false;
                this.timestamp = System.currentTimeMillis();
            }
        } else {
            if (this.flag) {
                this.flag = false;
                this.timestamp = System.currentTimeMillis();
            }
        }
    }

    private boolean matchesBlockPos(BlockPos pos) {
        String text = this.caveSetting.getText();
        if (text.equals("All")) {
            return true;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            Block block = mc.theWorld.getBlockState(pos.offset(facing)).getBlock();
            if (text.equals("Cave")) {
                if (block.getMaterial() == Material.air) {
                    return true;
                }
            } else if (!block.isOpaqueCube()) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesInt(int n, int n2, int n3) {
        return (long)(n - this.blockPos.getX()) * (n - this.blockPos.getX()) + (long)(n2 - this.blockPos.getZ()) * (n2 - this.blockPos.getZ())
            <= (long)n3 * n3;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.process3();
            } else if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof EventTick) {
                    BlockPos pos = new BlockPos(mc.thePlayer);
                    if (this.blockPos == null || this.blockPos.distanceSq(pos) > 4096.0) {
                        this.handleBlockPos(pos);
                    } else if (!this.flag && System.currentTimeMillis() - this.timestamp >= (long)this.scanIntervalSetting.computeDouble()) {
                        this.handleBlockPos(pos);
                    }

                    this.process();
                    this.handleBlockPos(pos, 6);
                }

                if (event instanceof EventRender2D) {
                    this.onEventRender2D((EventRender2D)event);
                }

                if (event instanceof EventRender3D) {
                    this.onEventRender3D((EventRender3D)event);
                }
            }
        }
    }

    private void handleBlockPos(BlockPos pos, int n) {
        int n2 = (int)this.scanRangeSetting.computeDouble();

        for (int i = -n; i <= n; i++) {
            for (int j = ~(n - 1); j <= n; j++) {
                for (int k = -n; k <= n; k++) {
                    BlockPos pos2 = pos.add(i, j, k);
                    if (this.matchesInt(pos2.getX(), pos2.getZ(), n2) && !this.entries.contains(pos2)) {
                        Color color = this.computeColor(mc.theWorld.getBlockState(pos2).getBlock());
                        if (color != null && this.matchesBlockPos(pos2)) {
                            this.entries.add(pos2);
                        }
                    }
                }
            }
        }
    }

    private Color computeColor(Block block) {
        if (block == Blocks.diamond_ore && this.diamondOreSetting.isEnabled()) {
            return new Color(0, 255, 255);
        }
        if (block == Blocks.gold_ore && this.goldOreSetting.isEnabled()) {
            return new Color(255, 190, 0);
        }
        if (block == Blocks.iron_ore && this.ironOreSetting.isEnabled()) {
            return new Color(215, 175, 145);
        }
        if (block == Blocks.coal_ore && this.coalOreSetting.isEnabled()) {
            return new Color(65, 65, 65);
        }
        if ((block == Blocks.redstone_ore || block == Blocks.lit_redstone_ore) && this.redstoneOreSetting.isEnabled()) {
            return new Color(255, 30, 30);
        }
        if (block == Blocks.emerald_ore && this.emeraldOreSetting.isEnabled()) {
            return new Color(0, 230, 80);
        }
        if (block == Blocks.lapis_ore && this.lapisOreSetting.isEnabled()) {
            return new Color(45, 80, 255);
        }
        return block == Blocks.reeds && this.sugarCaneSetting.isEnabled() ? new Color(80, 220, 70) : null;
    }

    @Override
    public void onEnable() {
        this.process3();
    }

    private Color computeColor(float f) {
        return f < 0.5F
            ? this.computeColor(new Color(255, 55, 55), new Color(255, 225, 45), f * 2.0F)
            : this.computeColor(new Color(255, 225, 45), new Color(55, 135, 255), (f - 0.5F) * 2.0F);
    }

    private void onEventRender3D(EventRender3D event) {
        int n = (int)this.alphaSetting.computeDouble();
        if (mc.thePlayer != null && n > 0 && !this.entries.isEmpty() && (this.boxESPSetting.isEnabled() || this.tracersSetting.isEnabled())) {
            int n2 = 0;
            int n3 = (int)this.markerCapSetting.computeDouble();
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            for (BlockPos pos : this.entries) {
                if (n2++ >= n3) {
                    break;
                }

                Block block = mc.theWorld.getBlockState(pos).getBlock();
                Color color = this.computeColor(block);
                if (color != null) {
                    double d = pos.getX() - mc.getRenderManager().renderPosX;
                    double d2 = pos.getY() - mc.getRenderManager().renderPosY;
                    double d3 = pos.getZ() - mc.getRenderManager().renderPosZ;
                    float f = n / 255.0F;
                    if (this.boxESPSetting.isEnabled()) {
                        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, f);
                        this.handleDouble(d, d2, d3, d + 1.0, d2 + 1.0, d3 + 1.0);
                    }

                    if (this.tracersSetting.isEnabled()) {
                        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, f * 0.8F);
                        GL11.glBegin(GL11.GL_LINES);
                        GL11.glVertex3d(d + 0.5, d2 + 0.5, d3 + 0.5);
                        GL11.glVertex3d(0.0, 0.0, 0.0);
                        GL11.glEnd();
                    }
                }
            }

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }
    }

    private float computeFloat() {
        if (this.blockPos != null && !this.entries2.isEmpty()) {
            return Math.min(1.0F, (float)this.count2 / this.entries2.size());
        }
        return this.flag ? 0.0F : 1.0F;
    }
}
