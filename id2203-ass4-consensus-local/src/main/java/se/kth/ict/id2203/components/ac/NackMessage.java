package se.kth.ict.id2203.components.ac;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class NackMessage extends Pp2pDeliver {
    private static final long serialVersionUID = 3456512345728476025L;

    private final Integer ts, t;

    public NackMessage(Address source, Integer ts, Integer t) {
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
        return "NackMessage{" +
                "ts=" + ts +
                ", t=" + t +
                '}';
    }
}
