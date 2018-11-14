package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

import java.util.ArrayList;


public class PrepareAckMessage extends FplDeliver {
    private static final long serialVersionUID = 7158512345728476025L;

    private final Integer ptsPrime, ts, l, tPrime;
    private final ArrayList<Object> vsuf;


    public PrepareAckMessage(Address source, Integer ptsPrime, Integer ts, ArrayList<Object> vsuf, Integer l, Integer tPrime) {
        super(source);
        this.ptsPrime = ptsPrime;
        this.ts = ts;
        this.vsuf = vsuf;
        this.l = l;
        this.tPrime = tPrime;
    }

    public Integer getPtsPrime() {
        return ptsPrime;
    }

    public Integer getTs() {
        return ts;
    }

    public Integer getL() {
        return l;
    }

    public Integer getTPrime() {
        return tPrime;
    }

    public ArrayList<Object> getVsuf() {
        return vsuf;
    }
}
