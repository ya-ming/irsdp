package se.kth.ict.id2203.components.fc;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class FcDecided extends BebDeliver {
    private static final long serialVersionUID = -1456661489560207948L;

    private final Object decision;

    public FcDecided(Address source, Object decision) {
        super(source);
        this.decision = decision;
    }

    public Object getDecision() {
        return decision;
    }
}

