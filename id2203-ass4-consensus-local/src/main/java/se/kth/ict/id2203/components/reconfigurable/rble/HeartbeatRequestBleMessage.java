package se.kth.ict.id2203.components.reconfigurable.rble;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatRequestBleMessage extends Pp2pDeliver{

    private static final long serialVersionUID = -8011228011408046392L;
    private Integer cfg;
    private long round;
    private ReBallot reBallotMax;

    public HeartbeatRequestBleMessage(Address source, Integer cfg, long round, ReBallot reBallotMax) {
        super(source);
        this.cfg = cfg;
        this.round = round;
        this.reBallotMax = reBallotMax;
    }

    public long getRound() {
        return round;
    }

    public Integer getCfg() {
        return cfg;
    }

    public ReBallot getReBallotMax() {
        return reBallotMax;
    }

    @Override
    public String toString() {
        return "HeartbeatRequestBleMessage{" +
                "cfg=" + cfg +
                ", round=" + round +
                ", reBallotMax=" + reBallotMax +
                '}';
    }
}
