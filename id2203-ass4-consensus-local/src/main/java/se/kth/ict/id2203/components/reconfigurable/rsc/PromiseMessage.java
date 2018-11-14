package se.kth.ict.id2203.components.reconfigurable.rsc;

import se.kth.ict.id2203.ports.fpl.FPp2pDeliver;
import se.sics.kompics.address.Address;

import java.util.List;


public class PromiseMessage extends FPp2pDeliver {
    private static final long serialVersionUID = 7158512345728476025L;

    private final Integer np, na, ld;
    private final List<Object> sfx;


    public PromiseMessage(Address source, Integer np, Integer na, List<Object> sfx, Integer ld) {
        super(source);
        this.np = np;
        this.na = na;
        this.sfx = sfx;
        this.ld = ld;
    }

    public Integer getNp() {
        return np;
    }

    public Integer getNa() {
        return na;
    }

    public Integer getLd() {
        return ld;
    }

    public List<Object> getSfx() {
        return sfx;
    }

    @Override
    public String toString() {
        return "PromiseMessage{" +
                "np=" + np +
                ", na=" + na +
                ", ld=" + ld +
                ", sfx=" + sfx +
                '}';
    }
}
