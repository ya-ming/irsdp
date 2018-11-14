package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class DecideMessage extends FplDeliver {
    private static final long serialVersionUID = 1266512345728476025L;

    private final Integer ts, l, tPrime;

    public DecideMessage(Address source, Integer ts, Integer l, Integer tPrime) {
        super(source);
        this.ts = ts;
        this.l = l;
        this.tPrime = tPrime;

    }

    public Integer getTs() {
        return ts;
    }

    public Integer getTPrime() {
        return tPrime;
    }

    public Integer getL() {
        return l;
    }
}

