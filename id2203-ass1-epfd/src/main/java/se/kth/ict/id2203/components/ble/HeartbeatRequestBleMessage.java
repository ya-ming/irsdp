package se.kth.ict.id2203.components.ble;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatRequestBleMessage extends Pp2pDeliver{

    private static final long serialVersionUID = -8011228011408046392L;
    private long round;
    private Ballot ballotMax;

    public HeartbeatRequestBleMessage(Address source, long round, Ballot ballotMax) {
        super(source);
        this.round = round;
        this.ballotMax = ballotMax;
    }

    public long getRound() {
        return round;
    }

    public Ballot getBallotMax() {
        return ballotMax;
    }

    @Override
    public String toString() {
        return "HeartbeatRequestBleMessage{" +
                "round=" + round +
                ", ballotMax=" + ballotMax +
                '}';
    }
}
