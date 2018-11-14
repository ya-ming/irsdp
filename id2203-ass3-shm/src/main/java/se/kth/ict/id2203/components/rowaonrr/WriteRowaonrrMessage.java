package se.kth.ict.id2203.components.rowaonrr;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class WriteRowaonrrMessage extends BebDeliver{
    private static final long serialVersionUID = 7158574361234476025L;

    private final Object val;
    public WriteRowaonrrMessage(Address source, Object val) {
        super(source);

        this.val = val;
    }

    public Object getVal() {
        return val;
    }
}
