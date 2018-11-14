package se.kth.ict.id2203.components.reconfigurable.rsc;

import se.kth.ict.id2203.ports.fpl.FPp2pDeliver;
import se.sics.kompics.address.Address;

public class AcceptedMessage extends FPp2pDeliver {
    private static final long serialVersionUID = 5566512345728476025L;

    private final Integer nL, lva;

    public AcceptedMessage(Address source, Integer nL, Integer lva) {
        super(source);
        this.nL = nL;
        this.lva = lva;
    }


    public Integer getnL() {
        return nL;
    }

    public Integer getLva() {
        return lva;
    }

    @Override
    public String toString() {
        return "AcceptedMessage{" +
                "nL=" + nL +
                ", lva=" + lva +
                '}';
    }
}

