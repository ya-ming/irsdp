package se.kth.ict.id2203.pa.broadcast;

import se.kth.ict.id2203.ports.frb.FRbDeliver;
import se.sics.kompics.address.Address;

public class FRbMessage extends FRbDeliver {

    private static final long serialVersionUID = -1855724247802103844L;

    private final String message;

    public FRbMessage(Address source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
