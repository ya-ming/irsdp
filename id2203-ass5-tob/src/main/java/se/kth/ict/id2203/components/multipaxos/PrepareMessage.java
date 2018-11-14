package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class PrepareMessage extends FplDeliver {
    private static final long serialVersionUID = -1236661489560207948L;

    private final Integer ts, l;
    private final Integer tPrime;

    public PrepareMessage(Address source, Integer ts, Integer l, Integer tPrime) {
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
