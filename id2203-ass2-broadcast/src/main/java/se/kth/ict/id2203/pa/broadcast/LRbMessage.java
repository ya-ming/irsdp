package se.kth.ict.id2203.pa.broadcast;

import se.kth.ict.id2203.ports.lrb.LRbDeliver;
import se.sics.kompics.address.Address;

public class LRbMessage extends LRbDeliver {

    private static final long serialVersionUID = -1855724247802103123L;

    private final String message;

    public LRbMessage(Address source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
