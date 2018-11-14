package se.kth.ict.id2203.ports.ele;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class Trust extends Event {

    private final Address source;

    public Trust(Address source) {
        this.source = source;
    }

    public final Address getSource() {
        return source;
    }
}

