package org.brutality.module.impl.misc;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.opengl.GL11;

public class BlinkDetection extends Module {
    private final Map<String, BlinkDetection.Entry> lookupMap = new HashMap<>();
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 3.0, 0.1);
    private final ModeSetting rainbowSetting = new ModeSetting(
        "Color", this, "Red", Arrays.asList("Red", "Yellow", "Green", "Cyan", "Purple", "White", "Rainbow")
    );
    private final NumberSetting distanceThresholdSetting = new NumberSetting("Distance Threshold", this, 5.0, 3.0, 10.0, 0.5);
    private final NumberSetting displayDurationSetting = new NumberSetting("Display Duration", this, 3.0, 1.0, 10.0, 0.5);
    private static final long timestamp = 500L;
    private static final long timestamp2 = 100L;

    private int computeInt2() {
        long now = System.currentTimeMillis();
        float f = (float)(now % 3000L) / 3000.0F;
        int n = Color.HSBtoRGB(f, 0.8F, 1.0F);
        return 0xFF000000 | n;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap.clear();
    }

    private void drawRect(int n, int n2, int n3, int n4, int n5) {
        float f = (n5 >> 24 & 0xFF) / 255.0F;
        float f2 = (n5 >> 16 & 0xFF) / 255.0F;
        float f3 = (n5 >> 8 & 0xFF) / 255.0F;
        float f4 = ((0xFF | ~n5) - ~n5) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n4);
        GL11.glVertex2f(n3, n4);
        GL11.glVertex2f(n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void process() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            long now = System.currentTimeMillis();

            for (EntityPlayer player : EntityCache.getEntries()) {
                if (player != mc.thePlayer) {
                    String name = player.getName();
                    Vec3 vec = new Vec3(player.posX, player.posY, player.posZ);
                    BlinkDetection.Entry entry = this.lookupMap.get(name);
                    if (entry == null) {
                        this.lookupMap.put(name, new BlinkDetection.Entry(vec));
                    } else {
                        double d = vec.distanceTo(entry.vec3);
                        long l = now - entry.timestamp;
                        if (d > this.distanceThresholdSetting.computeDouble() && l < 50L && now - entry.timestamp2 > 500L) {
                            entry.flag = true;
                            entry.timestamp2 = now;
                            entry.timestamp3 = now;
                        }

                        long l2 = (long)(this.displayDurationSetting.computeDouble() * 1000.0);
                        if (entry.flag && now - entry.timestamp3 > l2) {
                            entry.flag = false;
                        }

                        if (now - entry.timestamp4 > 300L) {
                            entry.count = (entry.count + 1) % 4;
                            entry.timestamp4 = now;
                        }

                        if (d > 0.1) {
                            entry.vec3 = vec;
                            entry.timestamp = now;
                        }
                    }
                }
            }

            Iterator it = this.lookupMap.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry entry2 = (Map.Entry)it.next();
                boolean flag = false;

                for (EntityPlayer player2 : EntityCache.getEntries()) {
                    if (player2.getName().equals(entry2.getKey())) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    it.remove();
                }
            }
        } else {
            this.lookupMap.clear();
        }
    }

    public BlinkDetection() {
        super("Blink Detection", 0, Category.MISCELLANEOUS);
        this.process18();
        this.handleSettingArray(new Setting[]{this.scaleSetting, this.rainbowSetting, this.distanceThresholdSetting, this.displayDurationSetting});
    }

    private String computeString(int n) {
        switch (n) {
            case 0:
                return "Blinking";
            case 1:
                return "Blinking.";
            case 2:
                return "Blinking..";
            case 3:
                return "Blinking...";
            default:
                return "Blinking";
        }
    }

    private void process3() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            RenderManager renderManager = mc.getRenderManager();
            FontRenderer fontRenderer = computeFontRenderer(BlinkDetection.class);

            for (EntityPlayer player : EntityCache.getEntries()) {
                if (player != mc.thePlayer) {
                    BlinkDetection.Entry entry = this.lookupMap.get(player.getName());
                    if (entry != null && entry.flag) {
                        String str = this.computeString(entry.count);
                        double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks - renderManager.viewerPosX;
                        double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks - renderManager.viewerPosY + player.height + 0.5;
                        double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks - renderManager.viewerPosZ;
                        int n = this.computeInt();
                        this.handleString(str, d, d2, d3, fontRenderer, n);
                    }
                }
            }
        }
    }

    private void handleString(String str, double d, double d2, double d3, FontRenderer fontRenderer, int n) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(d, d2, d3);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        float f = (float)(0.016666668F * this.scaleSetting.computeDouble());
        GlStateManager.scale(-f, -f, f);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        int textWidth = fontRenderer.getStringWidth(str);
        int n2 = 536870912;
        this.drawRect(-textWidth / 2 - 1, -1, textWidth / 2 + 1, fontRenderer.FONT_HEIGHT, n2);
        fontRenderer.drawString(str, (~textWidth + 1) / 2, 0, n);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick) {
                this.process();
            } else if (event instanceof EventRender3D) {
                this.process3();
            }
        }
    }

    private int computeInt() {
        String text = this.rainbowSetting.getText();
        switch (text) {
            case "Red":
                return -43691;
            case "Yellow":
                return -171;
            case "Green":
                return -11141291;
            case "Cyan":
                return -11141121;
            case "Purple":
                return -43521;
            case "White":
                return -1;
            case "Rainbow":
                return this.computeInt2();
            default:
                return -43691;
        }
    }

    public static class Entry {
        public Vec3 vec3;
        public long timestamp;
        public long timestamp2;
        public long timestamp3;
        public boolean flag;
        public int count;
        public long timestamp4;

        public Entry(Vec3 vec3) {
            this.vec3 = vec3;
            this.timestamp = System.currentTimeMillis();
            this.timestamp2 = 0L;
            this.timestamp3 = 0L;
            this.flag = false;
            this.count = 0;
            this.timestamp4 = System.currentTimeMillis();
        }
    }
}
