package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class NackMessage extends FplDeliver {
    private static final long serialVersionUID = 3456512345728476025L;

    private final Integer ptsPrime, tPrime;

    public NackMessage(Address source, Integer ptsPrime, Integer tPrime) {
        super(source);
        this.ptsPrime = ptsPrime;
        this.tPrime = tPrime;

    }

    public Integer getPtsPrime() {
        return ptsPrime;
    }

    public Integer getTPrime() {
        return tPrime;
    }
}
