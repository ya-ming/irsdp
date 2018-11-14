package se.kth.ict.id2203.components.reconfigurable.rble;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatResponseBleMessage extends Pp2pDeliver {

    private static final long serialVersionUID = -8033448011408046392L;
    private Integer cfg;
    private long round;
    private ReBallot reBallot;

    public HeartbeatResponseBleMessage(Address source, Integer cfg, long round, ReBallot reBallot) {
        super(source);
        this.cfg = cfg;
        this.round = round;
        this.reBallot = reBallot;
    }

    public long getRound() {
        return round;
    }

    public Integer getCfg() {
        return cfg;
    }

    public ReBallot getReBallot() {
        return reBallot;
    }

    @Override
    public String toString() {
        return "HeartbeatResponseBleMessage{" +
                "cfg=" + cfg +
                ", round=" + round +
                ", reBallot=" + reBallot +
                '}';
    }
}

