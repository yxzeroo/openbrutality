package org.brutality.module.impl.misc;

import lombok.Generated;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class ChatBypass extends Module {
    private static ChatBypass instance;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                ;
            }
        }
    }

    @Generated
    public static ChatBypass getInstance() {
        return instance;
    }

    public ChatBypass() {
        super("Chat Bypass", 0, Category.MISCELLANEOUS);
        instance = this;
    }
}
