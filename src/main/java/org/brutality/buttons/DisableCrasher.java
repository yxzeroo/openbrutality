package org.brutality.buttons;

import net.minecraft.client.gui.GuiButton;
import org.brutality.module.impl.exploit.Crasher;

public class DisableCrasher extends GuiButton {
    private final Crasher crasher;

    public void r() {
        this.crasher.process28();
        this.displayString = this.crasher.checkState44() ? "Disable Crasher" : "Enable Crasher";
    }

    public DisableCrasher(int n, int n2, int n3, int n4, int n5, Crasher crasher) {
        super(n, n2, n3, n4, n5, crasher.checkState44() ? "Disable Crasher" : "Enable Crasher");
        this.crasher = crasher;
    }
}
