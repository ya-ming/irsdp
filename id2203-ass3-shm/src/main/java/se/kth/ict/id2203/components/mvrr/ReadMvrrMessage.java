package se.kth.ict.id2203.components.mvrr;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class ReadMvrrMessage extends BebDeliver{
    private static final long serialVersionUID = 7158574361234476026L;

    private final Integer rid;

    public ReadMvrrMessage(Address source, Integer rid) {
        super(source);

        this.rid = rid;
    }

    public Integer getRid() {
        return rid;
    }
}
