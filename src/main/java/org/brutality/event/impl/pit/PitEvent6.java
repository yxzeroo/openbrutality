package org.brutality.event.impl.pit;

import org.brutality.event.Event;
import org.brutality.util.contract.Contract;

public class PitEvent6 extends Event {
    public final Contract contract;

    public PitEvent6(Contract contract) {
        this.contract = contract;
    }
}
