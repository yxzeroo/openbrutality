package org.brutality.gui.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import org.brutality.alt.altmanager.gui.GuiAccountSelector;
import org.brutality.alt.altmanager.gui.GuiAltManager;
import org.brutality.alt.altmanager.gui.GuiMicrosoftAuth;
import org.brutality.alt.altmanager.gui.GuiNfasAltManager;
import org.brutality.gui.ChangeSkinScreen;
import org.brutality.gui.NameChangerScreen;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.proxy.gui.GuiAddProxy;
import org.brutality.proxy.gui.GuiDeleteProxyConfirm;
import org.brutality.proxy.gui.GuiProxyManager;
import org.brutality.proxy.gui.GuiProxyWarning;

public final class ChatComponent {
    public static FontRenderer computeFontRenderer(Minecraft mc) {
        if (mc != null && matchesGuiScreen(mc.currentScreen)) {
            return computeDrawStringWithShadowComponent();
        }
        return (FontRenderer)(mc != null ? mc.fontRendererObj : computeDrawStringWithShadowComponent());
    }

    private static boolean matchesClassType(Class<?> clazz) {
        return clazz == GuiMultiplayer.class
            || clazz == GuiSelectWorld.class
            || clazz == GuiDisconnected.class
            || clazz == GuiDownloadTerrain.class
            || clazz == GuiProxyManager.class
            || clazz == GuiProxyWarning.class
            || clazz == GuiDeleteProxyConfirm.class
            || clazz == GuiAddProxy.class
            || clazz == GuiAltManager.class
            || clazz == GuiNfasAltManager.class
            || clazz == GuiMicrosoftAuth.class
            || clazz == GuiAccountSelector.class
            || clazz == NameChangerScreen.class
            || clazz == ChangeSkinScreen.class;
    }

    public static void handleGuiScreen(GuiScreen screen) {
        if (screen != null) {
            FontComponent.handleInt(screen.width, screen.height);
        }
    }

    public static boolean matchesGuiScreen(GuiScreen screen) {
        return screen != null && matchesClassType(screen.getClass());
    }

    public static FontRenderer computeFontRenderer(FontRenderer fontRenderer) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && matchesGuiScreen(mc.currentScreen)) {
            return computeDrawStringWithShadowComponent();
        }
        return (FontRenderer)(fontRenderer != null ? fontRenderer : computeDrawStringWithShadowComponent());
    }

    private ChatComponent() {
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent() {
        return FontComponent.computeDrawStringWithShadowComponent();
    }
}
