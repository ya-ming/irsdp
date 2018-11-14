package se.kth.ict.id2203.components.riwcannar;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class WriteArMessage extends BebDeliver{
    private static final long serialVersionUID = 7158574361234476025L;

    private final Integer ts, wr;
    private final Object val;
    public WriteArMessage(Address source,  Integer ts, Integer wr, Object val) {
        super(source);

        this.ts = ts;
        this.wr = wr;
        this.val = val;
    }

    public Integer getTs() {
        return ts;
    }

    public Integer getWr() {
        return wr;
    }

    public Object getVal() {
        return val;
    }
}
