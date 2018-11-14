package se.kth.ict.id2203.components.ble;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatResponseBleMessage extends Pp2pDeliver {

    private static final long serialVersionUID = -8033448011408046392L;
    private long round;
    private Ballot ballot;

    public HeartbeatResponseBleMessage(Address source, long round, Ballot ballot) {
        super(source);
        this.round = round;
        this.ballot = ballot;
    }

    public long getRound() {
        return round;
    }

    public Ballot getBallot() {
        return ballot;
    }

    @Override
    public String toString() {
        return "HeartbeatResponseBleMessage{" +
                "round=" + round +
                ", ballot=" + ballot +
                '}';
    }
}

