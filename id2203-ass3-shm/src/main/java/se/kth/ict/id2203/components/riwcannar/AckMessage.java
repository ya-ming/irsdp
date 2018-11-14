package se.kth.ict.id2203.components.riwcannar;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class AckMessage extends Pp2pDeliver {
    private static final long serialVersionUID = 7158512345728476025L;

    public AckMessage(Address source) {
        super(source);
    }
}
