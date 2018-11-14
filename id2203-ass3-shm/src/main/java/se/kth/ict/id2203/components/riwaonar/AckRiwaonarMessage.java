package se.kth.ict.id2203.components.riwaonar;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class AckRiwaonarMessage extends Pp2pDeliver {
    private static final long serialVersionUID = 7158512345728476025L;

    public AckRiwaonarMessage(Address source) {
        super(source);
    }
}
