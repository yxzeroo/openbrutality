package org.brutality.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.brutality.Brutality;
import org.brutality.gui.chat.ChatComponent;
import org.brutality.module.Module;

public final class GuiComponent2 extends GuiButton {
    private final String z;
    private float factor;

    public void process() {
        Module module = this.computeModule();
        if (module != null) {
            module.process28();
        }
    }

    public boolean matchesInt(int n, int n2) {
        return this.visible && n >= this.xPosition && n < this.xPosition + this.width && n2 >= this.yPosition && n2 < this.yPosition + this.height;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            Module module = this.computeModule();
            this.enabled = module != null;
            this.hovered = this.matchesInt(mouseX, mouseY);
            this.factor = this.factor + ((this.hovered && this.enabled ? 1.0F : 0.0F) - this.factor) * 0.25F;
            boolean flag = module != null && module.checkState44();
            MenuButtonRenderer.handleInt(
                this.xPosition,
                this.yPosition,
                this.width,
                this.height,
                this.displayString,
                ChatComponent.computeDrawStringWithShadowComponent(),
                this.factor,
                this.enabled,
                flag
            );
        }
    }

    public GuiComponent2(int n, String str, String str2) {
        super(n, 0, 0, 0, 16, str2);
        this.z = str;
    }

    private Module computeModule() {
        return Brutality.moduleManager == null ? null : Brutality.moduleManager.computeModule(this.z);
    }
}
