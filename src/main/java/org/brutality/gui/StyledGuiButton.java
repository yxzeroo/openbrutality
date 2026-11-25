package org.brutality.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.brutality.gui.chat.ChatComponent;

public final class StyledGuiButton extends GuiButton {
    private float factor;

    public StyledGuiButton(int n, int n2, int n3, int n4, int n5, String str) {
        super(n, n2, n3, n4, n5, str);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.factor = this.factor + ((this.hovered && this.enabled ? 1.0F : 0.0F) - this.factor) * 0.25F;
            MenuButtonRenderer.handleInt(
                this.xPosition,
                this.yPosition,
                this.width,
                this.height,
                this.displayString,
                ChatComponent.computeDrawStringWithShadowComponent(),
                OceanComponent.computeColor().getRGB(),
                this.factor,
                this.enabled
            );
        }
    }

    public StyledGuiButton(int n, int n2, int n3, String str) {
        super(n, n2, n3, str);
    }
}
