package se.kth.ict.id2203.components.ac;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class AcceptMessage extends BebDeliver {
    private static final long serialVersionUID = -1456661489560207948L;

    private final Integer pts;
    private final Object pv;
    private final Integer t;

    public AcceptMessage(Address source, Integer pts, Object pv, Integer t) {
        super(source);
        this.pts = pts;
        this.pv = pv;
        this.t = t;
    }

    public Integer getPts() {
        return pts;
    }

    public Object getPv() {
        return pv;
    }

    public Integer getT() {
        return t;
    }

    @Override
    public String toString() {
        return "AcceptMessage{" +
                "pts=" + pts +
                ", pv=" + pv +
                ", t=" + t +
                '}';
    }
}

