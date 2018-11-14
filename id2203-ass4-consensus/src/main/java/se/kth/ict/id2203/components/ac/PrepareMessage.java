package se.kth.ict.id2203.components.ac;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class PrepareMessage extends BebDeliver {
    private static final long serialVersionUID = -1236661489560207948L;

    private final Integer pts;
    private final Integer t;

    public PrepareMessage(Address source, Integer pts, Integer t) {
        super(source);
        this.pts = pts;
        this.t = t;
    }

    public Integer getPts() {
        return pts;
    }

    public Integer getT() {
        return t;
    }

    @Override
    public String toString() {
        return "PrepareMessage{" +
                "pts=" + pts +
                ", t=" + t +
                '}';
    }
}
