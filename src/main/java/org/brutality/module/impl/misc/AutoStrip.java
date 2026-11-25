package org.brutality.module.impl.misc;

import lombok.Generated;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;

public class AutoStrip extends Module {
    private static AutoStrip instance;

    public String computeString(String str) {
        return str == null ? null : str.replace("§k", "").replace("Â§k", "");
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    public boolean matchesString(String str) {
        return str == null ? false : str.contains("§k") || str.contains("Â§k");
    }

    public String computeString2(String str) {
        return str == null ? null : str.replaceAll("§[0-9a-fk-or]", "").replaceAll("Â§[0-9a-fk-or]", "");
    }

    @Generated
    public static AutoStrip getInstance() {
        return instance;
    }

    public AutoStrip() {
        super("Auto Strip", 0, Category.MISCELLANEOUS);
        instance = this;
    }

    public String computeString3(String str) {
        return str == null ? null : str.replace("§k", "").replace("Â§k", "");
    }
}
