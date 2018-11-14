package se.kth.ict.id2203.components.riwaonar;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class WriteRiwaonarMessage extends BebDeliver{
    private static final long serialVersionUID = 7158574361234476025L;

    private final Integer ts;
    private final Object val;
    public WriteRiwaonarMessage(Address source, Integer ts, Object val) {
        super(source);

        this.ts = ts;
        this.val = val;
    }

    public Object getVal() {
        return val;
    }

    public Integer getTs() {
        return ts;
    }
}
