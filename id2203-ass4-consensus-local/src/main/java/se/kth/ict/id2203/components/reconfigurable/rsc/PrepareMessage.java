package se.kth.ict.id2203.components.reconfigurable.rsc;

import se.kth.ict.id2203.ports.fpl.FPp2pDeliver;
import se.sics.kompics.address.Address;

public class PrepareMessage extends FPp2pDeliver {
    private static final long serialVersionUID = -1236661489560207948L;


    private final Integer np, ld;
    private final Integer na;
    private final Integer cfg;

    public Integer getCfg() {
        return cfg;
    }

    public PrepareMessage(Address source, Integer np, Integer ld, Integer na, Integer cfg) {
        super(source);
        this.np = np;
        this.ld = ld;
        this.na = na;
        this.cfg = cfg;

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

    @Override
    public String toString() {
        return "PrepareMessage{" +
                "np=" + np +
                ", ld=" + ld +
                ", na=" + na +
                ", cfg=" + cfg +
                '}';
    }
}
