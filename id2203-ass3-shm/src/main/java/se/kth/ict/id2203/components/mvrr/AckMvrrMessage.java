package se.kth.ict.id2203.components.mvrr;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class AckMvrrMessage extends Pp2pDeliver {
    private static final long serialVersionUID = 7158512345728476025L;

    private final Integer ts;

    public AckMvrrMessage(Address source, Integer ts) {
        super(source);

        this.ts = ts;
    }

    public Integer getTs() {
        return ts;
    }
}
