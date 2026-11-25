package org.brutality.notification.old;

public enum InfoType {
    INFO,
    WARNING,
    ERROR,
    SUCCESS,
    FAILURE;

    private static InfoType[] computeInfoTypeArray() {
        return new InfoType[]{INFO, WARNING, ERROR, SUCCESS, FAILURE};
    }
}
