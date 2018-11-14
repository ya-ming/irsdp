package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class AcceptAckMessage extends FplDeliver {
    private static final long serialVersionUID = 5566512345728476025L;

    private final Integer ptsPrime, l, tPrime;

    public AcceptAckMessage(Address source, Integer ptsPrime, Integer l, Integer tPrime) {
        super(source);
        this.ptsPrime = ptsPrime;
        this.l = l;
        this.tPrime = tPrime;

    }


    public Integer getPtsPrime() {
        return ptsPrime;
    }

    public Integer getL() {
        return l;
    }

    public Integer getTPrime() {
        return tPrime;
    }
}

