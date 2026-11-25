package org.brutality.module.impl.visual;

import java.util.Arrays;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.ArrayListHelper;

public final class ClientInfo extends Module {
    private final ModeSetting arrayListSetting = new ModeSetting("Color", this, "Theme", Arrays.asList("Theme", "ArrayList"));
    private final BooleanSetting showUserSetting = new BooleanSetting("Show User", this, true);
    private final NumberSetting marginSetting = new NumberSetting("Margin", this, 6.0, 2.0, 30.0, 1.0);

    @Override
    public void onRender() {
        if (this.checkState44() && mc.thePlayer != null) {
            FontRenderer fontRenderer = this.computeFontRenderer2();
            if (fontRenderer != null) {
                ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
                int n = (int)this.marginSetting.computeDouble();
                String str = ArrayListHelper.computeString(fontRenderer, this.computeString(), sr.getScaledWidth() - n * 2);
                fontRenderer.drawStringWithShadow(
                    str,
                    sr.getScaledWidth() - n - fontRenderer.getStringWidth(str),
                    sr.getScaledHeight() - n - fontRenderer.FONT_HEIGHT,
                    ArrayListHelper.computeColor(this, this.arrayListSetting.getText()).getRGB()
                );
            }
        }
    }

    @Override
    public void onEvent(Event event) {
    }

    public ClientInfo() {
        super("ClientInfo", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(new Setting[]{this.arrayListSetting, this.showUserSetting, this.marginSetting});
    }

    public String computeString() {
        String str = System.getProperty("brutality.license.username", "").trim();
        // VENDOR-DISABLED: Brutality.authHandler.getBackendAuthService().getUsername()

        String version = Brutality.version;
        String str2 = this.showUserSetting.isEnabled() ? "  |  User: " + (str != null && !str.isEmpty() ? str : "Local") : "";
        String str3 = version;
        return "Version: " + str3 + str2;
    }
}
