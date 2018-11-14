package se.kth.ict.id2203.components.riwcmnnar;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class ReadArMessage extends BebDeliver {
    private final Integer rid;

    public ReadArMessage(Address source, Integer rid) {
        super(source);
        this.rid = rid;
    }

    public Integer getRid() {
        return rid;
    }
}
