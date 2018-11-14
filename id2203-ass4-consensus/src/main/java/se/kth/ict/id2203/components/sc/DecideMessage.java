package se.kth.ict.id2203.components.sc;

import se.kth.ict.id2203.ports.fpl.FPp2pDeliver;
import se.sics.kompics.address.Address;

public class DecideMessage extends FPp2pDeliver {
    private static final long serialVersionUID = 1266512345728476025L;

    private final Integer ld, nL;

    public DecideMessage(Address source, Integer ld, Integer nL) {
        super(source);
        this.ld = ld;
        this.nL = nL;

    }

    public Integer getNL() {
        return nL;
    }

    public Integer getLd() {
        return ld;
    }

    @Override
    public String toString() {
        return "DecideMessage{" +
                "ld=" + ld +
                ", nL=" + nL +
                '}';
    }
}

