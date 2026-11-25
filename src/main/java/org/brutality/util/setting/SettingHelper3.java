package org.brutality.util.setting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.brutality.core.GetDisplayNameHandler;

public class SettingHelper3 {
    private final List<GetDisplayNameHandler<?>> y = new ArrayList<>();
    private final String name;

    public String getName() {
        return this.name;
    }

    public List<GetDisplayNameHandler<?>> getY() {
        return this.y;
    }

    public void handleGetDisplayNameHandler(GetDisplayNameHandler<?> getDisplayNameHandler) {
        this.y.add(getDisplayNameHandler);
    }

    public void handleComparator(Comparator<GetDisplayNameHandler<?>> comparator) {
        this.y.sort(comparator);
    }

    public SettingHelper3(String name) {
        this.name = name;
    }
}
