package se.kth.ict.id2203.components.riwmonar;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class ReadRiwmonarMessage extends BebDeliver{
    private static final long serialVersionUID = 7158574361234476026L;

    private final Integer rid;

    public ReadRiwmonarMessage(Address source, Integer rid) {
        super(source);

        this.rid = rid;
    }

    public Integer getRid() {
        return rid;
    }

    @Override
    public String toString() {
        return "ReadRiwmonarMessage{" +
                "rid=" + rid +
                '}';
    }
}
