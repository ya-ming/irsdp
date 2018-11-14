package se.kth.ict.id2203.components.hc;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class HcDecided extends BebDeliver {
    private static final long serialVersionUID = -1456661489560207948L;

    private final Object proposal;

    public HcDecided(Address source, Object proposal) {
        super(source);
        this.proposal = proposal;
    }

    public Object getProposal() {
        return proposal;
    }
}

