package se.kth.ict.id2203.components.ac;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;


public class PrepareAckMessage extends Pp2pDeliver {
    private static final long serialVersionUID = 7158512345728476025L;

    private final Integer ats;
    private Object av;
    private final Integer ts, t;

    public PrepareAckMessage(Address source, Integer ats, Object av, Integer ts, Integer t) {
        super(source);
        this.ats = ats;
        this.av = av;
        this.ts = ts;
        this.t = t;

    }

    public Integer getAts() {
        return ats;
    }

    public Object getAv() {
        return av;
    }

    public Integer getTs() {
        return ts;
    }

    public Integer getT() {
        return t;
    }

    @Override
    public String toString() {
        return "PrepareAckMessage{" +
                "ats=" + ats +
                ", av=" + av +
                ", ts=" + ts +
                ", t=" + t +
                '}';
    }
}
