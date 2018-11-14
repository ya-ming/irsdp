package se.kth.ict.id2203.components.huc;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class HucProposal extends BebDeliver {
    private static final long serialVersionUID = -1456661489560207949L;

    private final Object proposal;

    public HucProposal(Address source, Object proposal) {
        super(source);
        this.proposal = proposal;
    }

    public Object getProposal() {
        return proposal;
    }
}

