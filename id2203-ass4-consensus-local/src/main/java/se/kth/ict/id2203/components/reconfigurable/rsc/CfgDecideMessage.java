package se.kth.ict.id2203.components.reconfigurable.rsc;

import se.kth.ict.id2203.components.reconfigurable.rsc.reconfiguration.StopSign;
import se.kth.ict.id2203.ports.fpl.FPp2pDeliver;
import se.sics.kompics.Stop;
import se.sics.kompics.address.Address;

import java.util.List;

public class CfgDecideMessage extends FPp2pDeliver {
    private static final long serialVersionUID = 1266512345898476025L;

    private final Integer currentCfg;
    private final Integer ld, nL;
    private final List<Object> vd;

    public CfgDecideMessage(Address source, Integer currentCfg, Integer ld, Integer nL, List vd) {
        super(source);
        this.currentCfg = currentCfg;
        this.ld = ld;
        this.nL = nL;
        this.vd = vd;

    }

    public Integer getCurrentCfg() {
        return currentCfg;
    }

    public Integer getNL() {
        return nL;
    }

    public Integer getLd() {
        return ld;
    }

    public List<Object> getVd() {
        return vd;
    }

    @Override
    public String toString() {
        return "CfgDecideMessage{" +
                "currentCfg=" + currentCfg +
                ", ld=" + ld +
                ", nL=" + nL +
                ", vd=" + vd +
                '}';
    }
}

