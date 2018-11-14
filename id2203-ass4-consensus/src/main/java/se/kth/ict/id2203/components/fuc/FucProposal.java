package se.kth.ict.id2203.components.fuc;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

import java.util.HashSet;

public class FucProposal extends BebDeliver {
    private static final long serialVersionUID = -1236661489560207948L;

    private final Integer round;
    private final HashSet<Object> ps;

    public FucProposal(Address source, Integer round, HashSet<Object> ps) {
        super(source);
        this.round = round;
        this.ps = ps;
    }

    public Integer getRound() {
        return round;
    }

    public HashSet<Object> getPs() {
        return ps;
    }
}
