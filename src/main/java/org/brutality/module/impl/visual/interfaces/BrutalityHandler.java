package org.brutality.module.impl.visual.interfaces;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.Brutality;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.PostProcessing;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.LocationHelper;
import org.lwjgl.opengl.GL11;

public final class BrutalityHandler {
    private static final float factor = 6.0F;
    private static final float factor2 = 3.0F;
    private final Minecraft mc;
    private String text = "Brutality";
    private CustomFontRenderer drawStringWithShadowComponent;

    private float computeFloat(float f, float f2, BrutalityHelper brutalityHelper, long l) {
        Color color = brutalityHelper == null ? new Color(139, 122, 255) : brutalityHelper.getColor();
        Color color2 = brutalityHelper == null ? color.brighter() : brutalityHelper.getColor2();
        float f3 = f;
        long l2 = 3000L;

        for (int i = 0; i < "brutality".length(); i++) {
            String str = String.valueOf("brutality".charAt(i));
            float f4 = 1.0F - (float)(Math.abs(l - i * 300L) % l2) / (float)l2;
            float f5 = (float)(2.0 * Math.abs(f4 - Math.floor(f4 + 0.5F)));
            Color color3 = computeColor(color, color2, f5);
            this.drawStringWithShadowComponent.computeInt3(str, f3, f2, color3.getRGB());
            f3 += this.drawStringWithShadowComponent.getStringWidth(str);
        }

        return f3;
    }

    public BrutalityHandler(Minecraft mc) {
        this.mc = mc;
    }

    private int computeInt() {
        if (this.mc.getNetHandler() != null && this.mc.thePlayer != null) {
            NetworkPlayerInfo playerInfo = this.mc.getNetHandler().getPlayerInfo(this.mc.thePlayer.getUniqueID());
            return playerInfo == null ? 0 : Math.max(0, playerInfo.getResponseTime());
        }
        return 0;
    }

    public void handleFloat(float f, float f2, BrutalityHelper brutalityHelper) {
        BrutalityHandler.Entry entry = this.computeEntry(f, f2);
        boolean flag = this.checkState();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        Color color = flag ? new Color(20, 20, 28, 110) : new Color(18, 18, 22, 200);
        LocationHelper.handleFloat2(entry.factor, entry.factor2, entry.factor3, entry.factor4, entry.factor4 / 2.0F, color);
        float f3 = entry.factor + 6.0F;
        float f4 = entry.factor2 + 3.0F;
        float f5 = this.computeFloat(f3, f4, brutalityHelper, System.currentTimeMillis());
        this.drawStringWithShadowComponent.computeInt3(entry.text, f5, f4, Color.WHITE.getRGB());
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private String computeString() {
        String str = this.mc.isSingleplayer()
            ? "singleplayer"
            : (this.mc.getCurrentServerData() != null ? this.mc.getCurrentServerData().serverIP : "unknown");
        if (str != null && !"unknown".equals(str) && !"singleplayer".equals(str)) {
            String str2 = str.toLowerCase();
            int n = str2.indexOf("liquidproxy.net");
            return n < 0 ? str : "******liquidproxy.net";
        }
        return str;
    }

    private boolean checkState() {
        if (Brutality.moduleManager == null) {
            return false;
        }
        Module module = Brutality.moduleManager.computeModule("PostProcessing");
        return module instanceof PostProcessing && module.checkState44() && ((PostProcessing)module).blurSetting.isEnabled();
    }

    private static Color computeColor(Color color, Color color2, float f) {
        float f2 = Math.max(0.0F, Math.min(1.0F, f));
        return new Color(
            Math.round(color.getRed() + (color2.getRed() - color.getRed()) * f2),
            Math.round(color.getGreen() + (color2.getGreen() - color.getGreen()) * f2),
            Math.round(color.getBlue() + (color2.getBlue() - color.getBlue()) * f2)
        );
    }

    public void setText(String text) {
        this.text = text;
    }

    public void handleFloat(float f, float f2) {
        BrutalityHandler.Entry entry = this.computeEntry(f, f2);
        LocationHelper.handleFloat2(entry.factor, entry.factor2, entry.factor3, entry.factor4, entry.factor4 / 2.0F, Color.WHITE);
    }

    private BrutalityHandler.Entry computeEntry(float f, float f2) {
        this.drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(this.text, 15.0F);
        String str = "§7 | §f";
        int debugFPS = Minecraft.getDebugFPS();
        String str2 = this.computeString();
        String str3 = new SimpleDateFormat("HH:mm").format(new Date());
        int n = this.computeInt();
        String str4 = str3;
        String str5 = str2;
        int n2 = debugFPS;
        String str6 = str + n2 + " fps" + str + str5 + str + str4 + str + n + " ms";
        float f3 = 6.0F + this.drawStringWithShadowComponent.getStringWidth("brutality") + this.drawStringWithShadowComponent.getStringWidth(str6) + 6.0F;
        float f4 = this.drawStringWithShadowComponent.getHeight() + 6.0F;
        return new BrutalityHandler.Entry(f, f2, f3, f4, str6);
    }

    public static final class Entry {
        public final float factor;
        public final float factor2;
        public final float factor3;
        public final float factor4;
        public final String text;

        public Entry(float factor, float f, float f2, float f3, String text) {
            this.factor = factor;
            this.factor2 = f;
            this.factor3 = f2;
            this.factor4 = f3;
            this.text = text;
        }
    }
}
