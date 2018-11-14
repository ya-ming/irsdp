package se.kth.ict.id2203.appStubborn;

import se.kth.ict.id2203.ports.sp2p.Sp2pDeliver;
import se.sics.kompics.address.Address;

public class Sp2pMessage extends Sp2pDeliver {

    private static final long serialVersionUID = -1012347194946341231L;

    private final String message;

    public Sp2pMessage(Address source, String message) {
        super(source);
        this.message = message;
    }

    public final String getMessage() {
        return message;
    }
}
