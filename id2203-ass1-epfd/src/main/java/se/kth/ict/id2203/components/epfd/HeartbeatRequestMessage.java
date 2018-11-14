package se.kth.ict.id2203.components.epfd;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatRequestMessage extends Pp2pDeliver{

    private static final long serialVersionUID = -8011228011408046391L;
    private long sn;

    public HeartbeatRequestMessage(Address source, long sn) {
        super(source);
        this.sn = sn;
    }

    public long getSn() {
        return sn;
    }
}
