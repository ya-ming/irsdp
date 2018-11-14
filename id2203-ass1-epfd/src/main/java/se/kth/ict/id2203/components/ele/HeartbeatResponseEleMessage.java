package se.kth.ict.id2203.components.ele;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatResponseEleMessage extends Pp2pDeliver {

    private static final long serialVersionUID = -8033448011408046391L;
    private long epoch;

    public HeartbeatResponseEleMessage(Address source, long epoch) {
        super(source);
        this.epoch = epoch;
    }

    public long getEpoch() {
        return epoch;
    }
}

