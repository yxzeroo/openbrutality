package org.brutality.util.inventory;

import java.util.function.Function;

public interface IsCompleteApi {
    boolean checkState2();

    boolean isComplete();

    void handleLong(long timestamp);

    void tick();

    boolean checkState3();

    void handleFunction(Function<Void, Boolean> completionAction);

    void start();

    boolean checkState();
}
