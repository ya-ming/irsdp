package se.kth.ict.id2203.components.reconfigurable.rsc;

import se.kth.ict.id2203.ports.fpl.FPp2pDeliver;
import se.sics.kompics.address.Address;

public class AcceptMessage extends FPp2pDeliver {
    private static final long serialVersionUID = 3456512345728476025L;

    private final Integer nL;
    private Object C;

    public AcceptMessage(Address source, Integer nL, Object C) {
        super(source);
        this.nL = nL;
        this.C = C;
    }

    public Integer getnL() {
        return nL;
    }

    public Object getC() {
        return C;
    }

    @Override
    public String toString() {
        return "AcceptMessage{" +
                "nL=" + nL +
                ", C=" + C +
                '}';
    }
}
