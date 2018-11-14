package se.kth.ict.id2203.ports.sp2p;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

import java.io.Serializable;

public abstract class Sp2pDeliver extends Event implements Serializable {
    private static final long serialVersionUID = -1565687654301069512L;
    private final Address source;

    public Sp2pDeliver(Address source) {
        this.source = source;
    }

    public final Address getDestination() {
        return source;
    }
}
