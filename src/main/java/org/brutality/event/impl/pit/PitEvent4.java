package org.brutality.event.impl.pit;

import org.brutality.event.Event;
import org.brutality.util.contract.Contract;

public class PitEvent4 extends Event {
    private final Contract contract;

    public PitEvent4(Contract contract) {
        this.contract = contract;
    }
}
