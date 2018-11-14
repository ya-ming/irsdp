package se.kth.ict.id2203.components.reconfigurable.rsc;

import se.kth.ict.id2203.ports.fpl.FPp2pDeliver;
import se.sics.kompics.address.Address;

public class NackCfgMessage extends FPp2pDeliver {
    private static final long serialVersionUID = -1236632389560207948L;

    private final Integer newCfg, currentCfg, ld;

    public NackCfgMessage(Address source, Integer newCfg, Integer currentCfg, Integer ld) {
        super(source);
        this.newCfg = newCfg;
        this.currentCfg = currentCfg;
        this.ld = ld;
    }

    public Integer getNewCfg() {
        return newCfg;
    }

    public Integer getLd() {
        return ld;
    }

    public Integer getCurrentCfg() {
        return currentCfg;
    }

    @Override
    public String toString() {
        return "NackCfgMessage{" +
                "newCfg=" + newCfg +
                ", currentCfg=" + currentCfg +
                ", ld=" + ld +
                '}';
    }
}
