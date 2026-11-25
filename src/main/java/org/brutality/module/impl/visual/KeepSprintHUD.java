package org.brutality.module.impl.visual;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.performance.InvalidateHelper;

public class KeepSprintHUD extends Module {
    public KeepSprintHUD() {
        super("KeepSprintHUD", 0, Category.VISUAL);
        this.process18();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
        Module module = Brutality.moduleManager.computeModule("KeepSprint");
        if (module != null) {
            String str = "§fKeepSprint: " + (module.checkState44() ? "§aEnabled" : "§cDisabled");
            int n = sr.getScaledWidth() / 2;
            computeFontRenderer(KeepSprintHUD.class)
                .drawStringWithShadow(
                    str,
                    n - computeFontRenderer(KeepSprintHUD.class).getStringWidth(str) / 2.0F,
                    14.0F,
                    module.checkState44() ? Color.GREEN.getRGB() : Color.RED.getRGB()
                );
        }
    }
}
