package org.brutality.gui.font.atlas;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public final class ArialComponent {
    public static int computeInt(Font font, String str) {
        if (font == null) {
            return 0;
        }
        BufferedImage image = new BufferedImage(1, 1, 2);
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(font);
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int n = fontMetrics.stringWidth(str);
        g2d.dispose();
        return n;
    }

    private ArialComponent() {
    }

    public static Font computeFont(String str, int n) {
        try {
            ResourceLocation location = new ResourceLocation("brutality/font/" + str);
            return Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream()).deriveFont(0, n);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return new Font("Arial", 0, n);
        }
    }
}
