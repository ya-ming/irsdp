package se.kth.ict.id2203.components.sc;

import se.kth.ict.id2203.ports.fpl.FPp2pDeliver;
import se.sics.kompics.address.Address;

import java.util.List;

public class AcceptSyncMessage extends FPp2pDeliver {
    private static final long serialVersionUID = -1456661489560207948L;

    private final Integer nL, ldsp;
    private final List<Object> sfxp;

    public AcceptSyncMessage(Address source, Integer nL, List<Object> sfxp, Integer ldsp) {
        super(source);
        this.nL = nL;
        this.sfxp = sfxp;
        this.ldsp = ldsp;
    }

    public Integer getnL() {
        return nL;
    }

    public Integer getLdsp() {
        return ldsp;
    }

    public List<Object> getSfxp() {
        return sfxp;
    }

    @Override
    public String toString() {
        return "AcceptSyncMessage{" +
                "nL=" + nL +
                ", ldsp=" + ldsp +
                ", sfxp=" + sfxp +
                '}';
    }
}

