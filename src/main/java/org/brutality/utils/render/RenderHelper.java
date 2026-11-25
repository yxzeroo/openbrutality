package org.brutality.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.clickgui.classic.ConfirmScreen;
import org.brutality.clickgui.classic.DrawScriptsScreen;
import org.brutality.clickgui.classic.DrawSettingLabelScreen;
import org.brutality.clickgui.discord.DiscordGUI;
// VENDOR-DISABLED: import org.brutality.clickgui.newclick.GuiCloudBrowser;
// VENDOR-DISABLED: import org.brutality.clickgui.newclick.GuiCloudUpload;
import org.brutality.clickgui.newclick.LatestWipeScreen;
import org.brutality.clickgui.newclick.NewClickGUI;
import org.brutality.clickgui.newclick.HudEditorGui;
import org.brutality.clickgui.newclick.NewclickScreen;
import org.brutality.clickgui.newclick.OpenApiDocsScreen;
import org.brutality.clickgui.newclick.RefreshScreen;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.utils.performance.InvalidateHelper;
import org.lwjgl.opengl.GL11;

public final class RenderHelper implements AutoCloseable {
    private static final ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> threadLocal2 = ThreadLocal.withInitial(() -> 2);
    private final int count;
    private final GlStateGuard renderHelper7;
    private boolean closed;

    public static RenderHelper computeRenderHelper2(GuiScreen screen) {
        return new RenderHelper(true, computeInt(screen));
    }

    private RenderHelper(boolean flag, int n, boolean flag2) {
        int n2 = threadLocal.get();
        this.count = threadLocal2.get();
        this.renderHelper7 = n2 == 0 && flag ? new GlStateGuard() : null;
        if (n2 == 0 || flag2) {
            threadLocal2.set(n);
        }

        threadLocal.set(n2 + 1);
        if (this.renderHelper7 != null) {
            try {
                ScaledResolution sr = InvalidateHelper.computeScaledResolution(Minecraft.getMinecraft());
                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0.0, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.loadIdentity();
                GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            } catch (Error | RuntimeException e) {
                threadLocal.set(n2);
                threadLocal2.set(this.count);
                this.renderHelper7.close();
                throw e;
            }
        }
    }

    public static boolean matchesGuiScreen(GuiScreen screen) {
        return screen instanceof CustomGuiScreen
            ? true
            : screen instanceof DrawSettingLabelScreen
                || screen instanceof DrawScriptsScreen
                || screen instanceof ConfirmScreen
                || screen instanceof DiscordGUI
                || screen instanceof NewClickGUI
                // VENDOR-DISABLED: || screen instanceof GuiCloudBrowser
                || screen instanceof RefreshScreen
                || screen instanceof NewclickScreen
                // VENDOR-DISABLED: || screen instanceof GuiCloudUpload
                || screen instanceof LatestWipeScreen
                || screen instanceof OpenApiDocsScreen
                || screen instanceof HudEditorGui;
    }

    public RenderHelper() {
        this(true);
    }

    public static boolean isActive() {
        return threadLocal.get() > 0;
    }

    public static RenderHelper getInstance() {
        return new RenderHelper(false);
    }

    public static RenderHelper computeRenderHelper(GuiScreen screen) {
        return new RenderHelper(false, computeInt(screen), true);
    }

    public static int computeInt(int n) {
        return threadLocal.get() > 0 ? threadLocal2.get() : n;
    }

    private RenderHelper(boolean flag, int n) {
        this(flag, n, false);
    }

    private RenderHelper(boolean flag) {
        this(flag, 2);
    }

    private static int computeInt(GuiScreen screen) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!matchesGuiScreen(screen)) {
            return mc != null && mc.gameSettings != null ? mc.gameSettings.guiScale : 2;
        }
        return !(screen instanceof CustomGuiScreen) || mc == null || mc.displayWidth >= 1280 && mc.displayHeight >= 720 ? 2 : 1;
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            threadLocal.set(threadLocal.get() - 1);
            threadLocal2.set(this.count);
            if (this.renderHelper7 != null) {
                this.renderHelper7.close();
            }
        }
    }
}
