package org.brutality.clickgui.discord;

public enum UpType {
    UP,
    DOWN;

    private static UpType[] computeUpTypeArray() {
        return new UpType[]{UP, DOWN};
    }
}
