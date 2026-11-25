package org.brutality.module.impl.pit.harrys.minorevent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.lwjgl.opengl.GL11;

public class MeteoriteTracker extends Module {
    private final PitCategory pitCategory = PitCategory.MINOR_EVENT;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern pattern = Pattern.compile("A meteorite has landed \\(X: (-?\\d+) Z: (-?\\d+)\\)");
    private double amount = Double.NaN;
    private double amount2 = Double.NaN;
    private boolean flag = false;

    private void process() {
        ScaledResolution sr = new ScaledResolution(mc);
        int n = sr.getScaledWidth() / 2;
        int n2 = sr.getScaledHeight() / 2;
        double d = this.amount - mc.thePlayer.posX;
        double d2 = this.amount2 - mc.thePlayer.posZ;
        double dist = Math.sqrt(d * d + d2 * d2);
        float f = (float)Math.toDegrees(Math.atan2(d2, d)) - 90.0F;
        float f2 = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        float f3 = MathHelper.wrapAngleTo180_float(f - f2);
        float rad = (float)Math.toRadians(f3);
        byte b = 55;
        int n3 = n + (int)(-Math.sin(rad) * b);
        int n4 = n2 - (int)(Math.cos(rad) * b);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glLineWidth(3.0F);
        float f4 = 0.6F + 0.4F * (float)Math.sin(System.currentTimeMillis() / 300.0);
        float f5 = 0.2F + 0.8F * f4;
        float f6 = 0.6F + 0.4F * f4;
        float f7 = 0.1F;
        float f8 = 8.0F;
        float f9 = n3 + (float)Math.cos(rad - 2.5F) * f8;
        float f10 = n4 - (float)Math.sin(rad - 2.5F) * f8;
        float f11 = n3 + (float)Math.cos(rad + 2.5F) * f8;
        float f12 = n4 - (float)Math.sin(rad + 2.5F) * f8;
        GL11.glColor4f(f5, f6, f7, 0.4F);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(n3, n4);
        GL11.glVertex2f(f9, f10);
        GL11.glVertex2f(f11, f12);
        GL11.glEnd();
        GL11.glColor4f(f5, f6, f7, 1.0F);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(n3 + (float)Math.cos(rad + 3.14) * 2.0F, n4 - (float)Math.sin(rad + 3.14) * 2.0F);
        GL11.glVertex2f(f9 + (float)Math.cos(rad + 3.14) * 2.0F, f10 - (float)Math.sin(rad + 3.14) * 2.0F);
        GL11.glVertex2f(f11 + (float)Math.cos(rad + 3.14) * 2.0F, f12 - (float)Math.sin(rad + 3.14) * 2.0F);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        computeFontRenderer(MeteoriteTracker.class)
            .drawStringWithShadow(
                (int)dist + "m", n3 - computeFontRenderer(MeteoriteTracker.class).getStringWidth((int)dist + "m") / 2.0F, n4 - 22, 16777215
            );
        String str = EnumChatFormatting.GOLD + "Meteorite";
        computeFontRenderer(MeteoriteTracker.class)
            .drawStringWithShadow(str, n3 - computeFontRenderer(MeteoriteTracker.class).getStringWidth(str) / 2.0F, n4 - 10, 16777215);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public MeteoriteTracker() {
        super("MeteoriteTracker", 0, Category.PIT);
        this.process18();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof ChatEvent) {
                    String text = ((ChatEvent)event).message.getUnformattedText();
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        this.amount = Integer.parseInt(matcher.group(1)) + 0.5;
                        this.amount2 = Integer.parseInt(matcher.group(2)) + 0.5;
                        this.flag = true;
                    }
                }

                if (event instanceof EventRender2D && this.flag) {
                    this.process();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.flag = false;
        this.amount = Double.NaN;
        this.amount2 = Double.NaN;
        super.onDisable();
    }
}
