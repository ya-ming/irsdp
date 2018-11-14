package se.kth.ict.id2203.components.huc;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.erb.RbDeliver;
import se.sics.kompics.address.Address;

public class HucDecided extends RbDeliver {
    private static final long serialVersionUID = -1456661489560207948L;

    private final Object proposal;

    public HucDecided(Address source, Object proposal) {
        super(source);
        this.proposal = proposal;
    }

    public Object getProposal() {
        return proposal;
    }
}

