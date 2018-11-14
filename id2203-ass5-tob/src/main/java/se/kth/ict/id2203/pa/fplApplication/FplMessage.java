package se.kth.ict.id2203.pa.fplApplication;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class FplMessage extends FplDeliver {

    private static final long serialVersionUID = -1077397194946311231L;

    private final String message;

    public FplMessage(Address source, String message) {
        super(source);
        this.message = message;
    }

    public final String getMessage() {
        return message;
    }
}
