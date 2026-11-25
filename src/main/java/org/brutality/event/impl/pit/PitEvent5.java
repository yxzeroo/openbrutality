package org.brutality.event.impl.pit;

import org.brutality.event.Event;
import org.brutality.util.contract.Contract;

public class PitEvent5 extends Event {
    private final Contract contract;

    public PitEvent5(Contract contract) {
        this.contract = contract;
    }
}
