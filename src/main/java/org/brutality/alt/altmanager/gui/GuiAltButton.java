package org.brutality.alt.altmanager.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.brutality.gui.MenuButtonRenderer;
import org.brutality.gui.font.FontComponent;

public class GuiAltButton extends GuiButton {
    private final int count;
    private final int count2;
    private boolean flag = false;
    private float factor;
    private long timestamp = System.nanoTime();

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            long now = System.nanoTime();
            float f = Math.min(0.05F, (float)(now - this.timestamp) / 1.0E9F);
            this.timestamp = now;
            this.factor = this.factor + ((this.hovered && this.enabled ? 1.0F : 0.0F) - this.factor) * (1.0F - (float)Math.exp(-14.0F * f));
            MenuButtonRenderer.handleInt(
                this.xPosition,
                this.yPosition,
                this.width,
                this.height,
                this.displayString,
                FontComponent.computeDrawStringWithShadowComponent(),
                this.count,
                this.factor,
                this.enabled
            );
        }
    }

    public GuiAltButton(int n, int n2, int n3, int n4, int n5, String str, int count, int n6) {
        super(n, n2, n3, n4, n5, str);
        this.count = count;
        this.count2 = n6;
    }
}
