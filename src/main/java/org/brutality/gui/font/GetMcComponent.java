package org.brutality.gui.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.font.atlas.ArialComponent;
import org.brutality.utils.mcp.McpApi3;

public class GetMcComponent implements McpApi3 {
    public static int count = 0;
    public static int count2 = 0;
    public static CustomFontRenderer drawStringWithShadowComponent;
    public static CustomFontRenderer drawStringWithShadowComponent2;
    public static CustomFontRenderer drawStringWithShadowComponent3;
    public static CustomFontRenderer drawStringWithShadowComponent4;
    public static CustomFontRenderer drawStringWithShadowComponent5;
    public static CustomFontRenderer drawStringWithShadowComponent6;
    public static CustomFontRenderer drawStringWithShadowComponent7;
    public static CustomFontRenderer drawStringWithShadowComponent8;
    public static CustomFontRenderer drawStringWithShadowComponent9;
    public static CustomFontRenderer drawStringWithShadowComponent10;
    public static CustomFontRenderer drawStringWithShadowComponent11;
    public static CustomFontRenderer drawStringWithShadowComponent12;
    public static CustomFontRenderer drawStringWithShadowComponent13;
    public static CustomFontRenderer drawStringWithShadowComponent14;
    public static CustomFontRenderer drawStringWithShadowComponent15;
    public static CustomFontRenderer drawStringWithShadowComponent16;
    public static CustomFontRenderer drawStringWithShadowComponent17;
    public static CustomFontRenderer drawStringWithShadowComponent18;
    public static CustomFontRenderer drawStringWithShadowComponent19;
    public static CustomFontRenderer drawStringWithShadowComponent20;
    public static CustomFontRenderer drawStringWithShadowComponent21;
    public static CustomFontRenderer drawStringWithShadowComponent22;
    private static org.brutality.gui.font.atlas.AtlasFontRenderer drawStringWithShadowComponent23;
    private final Minecraft mc = Minecraft.getMinecraft();

    public static org.brutality.gui.font.atlas.AtlasFontRenderer computeDrawStringWithShadowComponent() {
        if (drawStringWithShadowComponent23 == null) {
            drawStringWithShadowComponent23 = new org.brutality.gui.font.atlas.AtlasFontRenderer(
                ArialComponent.computeFont("brutality.ttf", 18), true, false
            );
            drawStringWithShadowComponent23.process2();
        }

        return drawStringWithShadowComponent23;
    }

    private static CustomFontRenderer computeDrawStringWithShadowComponent(String str, float f) throws IOException, FontFormatException {
        ResourceLocation location = new ResourceLocation("brutality/font/" + str);
        Font font = Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream()).deriveFont(f);
        return new CustomFontRenderer(font);
    }

    public void update() {
        if (count != this.mc.displayHeight || count2 != this.mc.displayWidth) {
            this.init();
        }
    }

    public void init() {
        try {
            drawStringWithShadowComponent = computeDrawStringWithShadowComponent("arial.ttf", 16.0F);
            drawStringWithShadowComponent2 = computeDrawStringWithShadowComponent("arial.ttf", 18.0F);
            drawStringWithShadowComponent3 = computeDrawStringWithShadowComponent("arial.ttf", 24.0F);
            drawStringWithShadowComponent4 = computeDrawStringWithShadowComponent("arial.ttf", 36.0F);
            drawStringWithShadowComponent5 = computeDrawStringWithShadowComponent("arial.ttf", 48.0F);
            drawStringWithShadowComponent6 = computeDrawStringWithShadowComponent("arial.ttf", 64.0F);
            drawStringWithShadowComponent8 = computeDrawStringWithShadowComponent("consolas.ttf", 15.0F);
            drawStringWithShadowComponent9 = computeDrawStringWithShadowComponent("consolas.ttf", 17.0F);
            drawStringWithShadowComponent10 = computeDrawStringWithShadowComponent("consolas.ttf", 18.0F);
            drawStringWithShadowComponent11 = computeDrawStringWithShadowComponent("consolas.ttf", 21.0F);
            drawStringWithShadowComponent12 = computeDrawStringWithShadowComponent("productsans.ttf", 18.0F);
            drawStringWithShadowComponent13 = computeDrawStringWithShadowComponent("productsans.ttf", 20.0F);
            drawStringWithShadowComponent14 = computeDrawStringWithShadowComponent("productsans.ttf", 26.0F);
            drawStringWithShadowComponent15 = computeDrawStringWithShadowComponent("productsans.ttf", 31.0F);
            drawStringWithShadowComponent16 = computeDrawStringWithShadowComponent("comfortaalight.ttf", 18.0F);
            drawStringWithShadowComponent17 = computeDrawStringWithShadowComponent("comfortaalight.ttf", 24.0F);
            drawStringWithShadowComponent18 = computeDrawStringWithShadowComponent("comfortaalight.ttf", 36.0F);
            drawStringWithShadowComponent19 = computeDrawStringWithShadowComponent("comfortaalight.ttf", 64.0F);
            drawStringWithShadowComponent20 = computeDrawStringWithShadowComponent("skidma.ttf", 18.0F);
            drawStringWithShadowComponent21 = computeDrawStringWithShadowComponent("skidma.ttf", 22.0F);
            drawStringWithShadowComponent22 = computeDrawStringWithShadowComponent("skidma.ttf", 52.0F);
            count = this.mc.displayHeight;
            count2 = this.mc.displayWidth;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Generated
    public Minecraft getMc() {
        return this.mc;
    }
}
