package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

import java.util.ArrayList;

public class AcceptMessage extends FplDeliver {
    private static final long serialVersionUID = -1456661489560207948L;

    private final Integer ts, offs, tPrime;
    private final ArrayList<Object> vsuf;

    public AcceptMessage(Address source, Integer ts, ArrayList<Object> vsuf, Integer offs, Integer tPrime) {
        super(source);
        this.ts = ts;
        this.vsuf = vsuf;
        this.offs = offs;
        this.tPrime = tPrime;
    }

    public Integer getTPrime() {
        return tPrime;
    }

    public Integer getTs() {
        return ts;
    }

    public Integer getOffs() {
        return offs;
    }

    public ArrayList<Object> getVsuf() {
        return vsuf;
    }
}

