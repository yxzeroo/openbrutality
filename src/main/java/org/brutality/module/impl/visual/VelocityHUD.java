package org.brutality.module.impl.visual;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.performance.InvalidateHelper;

public class VelocityHUD extends Module {
    @Override
    public void onRender() {
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(Minecraft.getMinecraft());
        Module module = Brutality.moduleManager.computeModule("Velocity");
        if (module != null) {
            String str = "§fVelocity: " + (module.checkState44() ? "§aEnabled" : "§cDisabled");
            int n = sr.getScaledWidth() / 2;
            byte b = 0;
            computeFontRenderer(VelocityHUD.class)
                .drawStringWithShadow(
                    str,
                    n - computeFontRenderer(VelocityHUD.class).getStringWidth(str) / 2.0F,
                    b + 2,
                    module.checkState44() ? Color.GREEN.getRGB() : Color.RED.getRGB()
                );
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    public VelocityHUD() {
        super("VelocityHUD", 0, Category.VISUAL);
        this.process18();
    }
}
