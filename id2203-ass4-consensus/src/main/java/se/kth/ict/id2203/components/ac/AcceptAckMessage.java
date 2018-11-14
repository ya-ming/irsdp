package se.kth.ict.id2203.components.ac;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class AcceptAckMessage extends Pp2pDeliver {
    private static final long serialVersionUID = 5566512345728476025L;

    private final Integer ts, t;

    public AcceptAckMessage(Address source, Integer ts, Integer t) {
        super(source);
        this.ts = ts;
        this.t = t;

    }

    public Integer getTs() {
        return ts;
    }

    public Integer getT() {
        return t;
    }

    @Override
    public String toString() {
        return "AcceptAckMessage{" +
                "ts=" + ts +
                ", t=" + t +
                '}';
    }
}

