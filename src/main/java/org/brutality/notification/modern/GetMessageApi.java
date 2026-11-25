package org.brutality.notification.modern;

public interface GetMessageApi {
    ModernHandler.PlayerJoinMode computePlayerJoinMode();

    String getMessage();

    long getTimestamp();

    long getTimestamp2();

    boolean matchesLong(long currentTime);

    ModernHandler.LowMode computeLowMode();
}
