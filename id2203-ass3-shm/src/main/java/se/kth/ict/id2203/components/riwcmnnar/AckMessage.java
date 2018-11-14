package se.kth.ict.id2203.components.riwcmnnar;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class AckMessage extends Pp2pDeliver {
    private static final long serialVersionUID = 7158512345728476025L;

    private final Integer rid;

    public AckMessage(Address source, Integer rid) {
        super(source);
        this.rid = rid;
    }

    public Integer getRid() {
        return rid;
    }
}
