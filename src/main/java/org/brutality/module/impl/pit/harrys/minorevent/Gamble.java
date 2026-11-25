package org.brutality.module.impl.pit.harrys.minorevent;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Gamble extends Module {
    private final PitCategory pitCategory = PitCategory.MINOR_EVENT;
    private final BooleanSetting boxSetting = new BooleanSetting("Box", this, true);
    private final BooleanSetting outlineSetting = new BooleanSetting("Outline", this, true);
    private final BooleanSetting filledSetting = new BooleanSetting("Filled", this, true);
    private final BooleanSetting nameTagSetting = new BooleanSetting("Name Tag", this, true);
    private final BooleanSetting showDistanceSetting = new BooleanSetting("Show Distance", this, true);
    private final NumberSetting outlineWidthSetting = new NumberSetting("Outline Width", this, 2.0, 1.0, 5.0, 1.0);
    private final ColorSetting boxColorSetting = new ColorSetting("Box Color", this, 60.0F);
    private final ColorSetting outlineColorSetting = new ColorSetting("Outline Color", this, 45.0F);
    private final ColorSetting textColorSetting = new ColorSetting("Text Color", this, 0.0F);
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<Gamble.Entry> entries = new ArrayList<>();
    private final Pattern pattern = Pattern.compile("GIANT TICKET! Spawned at \\(X: ([-]?\\d+) Z: ([-]?\\d+)\\)! Capture it to gain \\+30 Tickets!");
    private final Pattern pattern2 = Pattern.compile("GIANT TICKET! Claimed by ([a-zA-Z0-9_]+), gaining \\+30 Tickets!");

    private void handleAxisAlignedBB(AxisAlignedBB box) {
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(box.minX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
        GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.minZ);
        GL11.glVertex3d(box.minX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.minZ);
        GL11.glVertex3d(box.minX, box.minY, box.minZ);
        GL11.glVertex3d(box.minX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
        GL11.glEnd();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof ChatEvent) {
                this.onChatEvent((ChatEvent)event);
            } else if (event instanceof EventUpdate) {
                this.onEventUpdate((EventUpdate)event);
            } else if (event instanceof EventRender3D) {
                this.onEventRender3D((EventRender3D)event);
            } else if (event instanceof EventRender2D) {
                this.onEventRender2D((EventRender2D)event);
            }
        }
    }

    @Override
    public void onDisable() {
        this.entries.clear();
    }

    private void onEventUpdate(EventUpdate event) {
    }

    private BlockPos computeBlockPos(int n, int n2) {
        if (this.mc.theWorld == null) {
            return null;
        }
        for (int i = 50; i <= 80; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = -2; k <= 2; k++) {
                    BlockPos pos = new BlockPos(n + j, i, n2 + k);
                    if (this.mc.theWorld.getBlockState(pos).getBlock() == Blocks.stained_hardened_clay) {
                        int metaFromState = this.mc.theWorld.getBlockState(pos).getBlock().getMetaFromState(this.mc.theWorld.getBlockState(pos));
                        if (metaFromState == 4) {
                            return pos;
                        }
                    }
                }
            }
        }

        return new BlockPos(n, 64, n2);
    }

    public Gamble() {
        super("Gamble", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.boxSetting,
                this.outlineSetting,
                this.filledSetting,
                this.nameTagSetting,
                this.showDistanceSetting,
                this.outlineWidthSetting,
                this.boxColorSetting,
                this.outlineColorSetting,
                this.textColorSetting
            }
        );
    }

    private void onChatEvent(ChatEvent chatEvent) {
        if (chatEvent.message != null) {
            String text = chatEvent.message.getUnformattedText();
            Matcher matcher = this.pattern.matcher(text);
            if (matcher.find()) {
                try {
                    int n = Integer.parseInt(matcher.group(1));
                    int n2 = Integer.parseInt(matcher.group(2));
                    BlockPos pos = this.computeBlockPos(n, n2);
                    if (pos != null) {
                        Gamble.Entry entry = new Gamble.Entry(pos, text.trim());
                        this.entries.add(entry);
                    }
                } catch (NumberFormatException ignored) {
                }
            } else {
                Matcher matcher2 = this.pattern2.matcher(text);
                if (matcher2.find()) {
                    this.entries.clear();
                }
            }
        }
    }

    private void handleEntry(Gamble.Entry entry, float f) {
        BlockPos blockPos = entry.blockPos;
        double d = blockPos.getX() + 0.5 - this.mc.getRenderManager().viewerPosX;
        double d2 = blockPos.getY() + 2.0 - this.mc.getRenderManager().viewerPosY;
        double d3 = blockPos.getZ() + 0.5 - this.mc.getRenderManager().viewerPosZ;
        float[] floats = this.computeFloatArray((float)d, (float)d2, (float)d3);
        if (floats != null && !(floats[2] < 0.0F) && !(floats[2] >= 1.0F)) {
            int n = (int)floats[0];
            int n2 = (int)floats[1];
            FontRenderer fontRenderer = computeFontRenderer(Gamble.class);
            Color color = this.textColorSetting.computeColor();
            int rgb = color.getRGB();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)n, (float)n2, 0.0F);
            GlStateManager.scale(1.0F, 1.0F, 1.0F);
            String str = "&6&lGIANT TICKET!";
            int textWidth = fontRenderer.getStringWidth(str);
            fontRenderer.drawStringWithShadow(str, -textWidth / 2, -20.0F, rgb);
            if (this.showDistanceSetting.isEnabled()) {
                double dist = this.mc.thePlayer.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
                String str2 = String.format("&7[%.1fm]", dist);
                int textWidth2 = fontRenderer.getStringWidth(str2);
                fontRenderer.drawStringWithShadow(str2, -textWidth2 / 2, -10.0F, rgb);
            }

            GlStateManager.popMatrix();
        }
    }

    private void onEventRender3D(EventRender3D event) {
        if (!this.entries.isEmpty()) {
            float factor = event.getFactor();

            for (Gamble.Entry entry : this.entries) {
                this.handleEntry2(entry, factor);
            }
        }
    }

    private float[] computeFloatArray(float f, float f2, float f3) {
        try {
            FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
            FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);
            IntBuffer intBuffer = BufferUtils.createIntBuffer(16);
            FloatBuffer floatBuffer3 = BufferUtils.createFloatBuffer(3);
            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, floatBuffer);
            GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, floatBuffer2);
            GL11.glGetInteger(GL11.GL_VIEWPORT, intBuffer);
            boolean flag = GLU.gluProject(f, f2, f3, floatBuffer, floatBuffer2, intBuffer, floatBuffer3);
            if (flag) {
                ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
                return new float[]{floatBuffer3.get(0) / sr.getScaleFactor(), (intBuffer.get(3) - floatBuffer3.get(1)) / sr.getScaleFactor(), floatBuffer3.get(2)};
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private void onEventRender2D(EventRender2D event) {
        if (!this.entries.isEmpty() && this.nameTagSetting.isEnabled()) {
            float partialTicks = event.getPartialTicks();

            for (Gamble.Entry entry : this.entries) {
                this.handleEntry(entry, partialTicks);
            }
        }
    }

    private void handleEntry2(Gamble.Entry entry, float f) {
        BlockPos blockPos = entry.blockPos;
        double d = blockPos.getX() - this.mc.getRenderManager().viewerPosX;
        double d2 = blockPos.getY() - this.mc.getRenderManager().viewerPosY;
        double d3 = blockPos.getZ() - this.mc.getRenderManager().viewerPosZ;
        AxisAlignedBB box = new AxisAlignedBB(d - 0.5, d2 - 1.0, d3 - 0.5, d + 1.5, d2 + 3.0, d3 + 1.5);
        if (ResetColorHelper.matchesAxisAlignedBB(box)) {
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            Color color = this.boxColorSetting.computeColor();
            Color color2 = this.outlineColorSetting.computeColor();
            if (this.filledSetting.isEnabled() && this.boxSetting.isEnabled()) {
                GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
                ResetColorHelper.handleAxisAlignedBB3(box);
            }

            if (this.outlineSetting.isEnabled()) {
                GL11.glLineWidth((float)this.outlineWidthSetting.computeDouble());
                GL11.glColor4f(color2.getRed() / 255.0F, color2.getGreen() / 255.0F, color2.getBlue() / 255.0F, color2.getAlpha() / 255.0F);
                this.handleAxisAlignedBB(box);
            }

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
        }
    }

    public static class Entry {
        public final BlockPos blockPos;
        public final String text;

        public Entry(BlockPos blockPos, String text) {
            this.blockPos = blockPos;
            this.text = text;
        }
    }
}
