package org.brutality.module.impl.visual;

import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.performance.InvalidateHelper;

public class DisplaceHUD extends Module {
    private static final long timestamp = 8439882513597333503L;

    public DisplaceHUD() {
        super("DisplaceHUD", 0, Category.VISUAL);
        this.process18();
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        Module module = Brutality.moduleManager.computeModule("Displace");
        Module module2 = Brutality.moduleManager.computeModule("KeepSprintHUD");
        if (module != null) {
            String str = "§fDisplace: " + (module.checkState44() ? "§aEnabled" : "§cDisabled");
            float f = InvalidateHelper.computeScaledResolution(mc).getScaledWidth() / 2.0F;
            float f2 = module2 != null && module2.checkState44() ? 14 + module2.computeFontRenderer2().FONT_HEIGHT + 2 : 14.0F;
            computeFontRenderer(DisplaceHUD.class)
                .drawStringWithShadow(str, f - computeFontRenderer(DisplaceHUD.class).getStringWidth(str) / 2.0F, f2, (int)timestamp);
        }
    }

    @Override
    public void onEvent(Event event) {
    }
}
