package org.brutality.gui;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.brutality.gui.mainmenu.effects.EffectsComponent;
import org.brutality.gui.mainmenu.effects.NebulaComponent;
import org.brutality.utils.render.GlStateGuard;

public final class OceanComponent {
    public static boolean matchesInt(int n, int n2) {
        try {
            boolean flag;
            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                Minecraft mc = Minecraft.getMinecraft();
                EffectsComponent.handleInt(mc.displayWidth, mc.displayHeight);
                Gui.drawRect(0, 0, n, n2, -15655379);
                NebulaComponent.init();
                NebulaComponent.computeMainMenuScene2().handleInt(n, n2, 0.0F, 0, 0);
                Gui.drawRect(0, 0, n, n2, 1207959552);
                flag = true;
            }

            return flag;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private OceanComponent() {
    }

    public static void handleGuiScreen(GuiScreen screen, int n, int n2, float f) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            Minecraft mc = Minecraft.getMinecraft();
            EffectsComponent.handleInt(mc.displayWidth, mc.displayHeight);
            Gui.drawRect(0, 0, screen.width, screen.height, -15655379);
            NebulaComponent.init();
            NebulaComponent.computeMainMenuScene2().handleInt(screen.width, screen.height, f, n, n2);
            Gui.drawRect(0, 0, screen.width, screen.height, 1207959552);
        } catch (RuntimeException e) {
            Gui.drawRect(0, 0, screen.width, screen.height, -15655379);
        }
    }

    public static Color computeColor() {
        String str = NebulaComponent.computeMainMenuScene2() == null ? "Ocean" : NebulaComponent.computeMainMenuScene2().getName();
        if (str.equals("Halloween")) {
            return new Color(245, 165, 85);
        }
        if (str.equals("Fire") || str.equals("London") || str.equals("Sunset")) {
            return new Color(245, 155, 95);
        }
        if (str.equals("Sakura")) {
            return new Color(235, 145, 190);
        }
        if (str.equals("Alpine") || str.equals("Earth")) {
            return new Color(120, 205, 180);
        }
        return !str.equals("Nebula") && !str.equals("Black Hole") && !str.equals("Tokyo") && !str.equals("Cyberpunk")
            ? new Color(110, 190, 235)
            : new Color(190, 145, 235);
    }
}
