package se.kth.ict.id2203.components.mvrr;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class WriteMvrrMessage extends BebDeliver{
    private static final long serialVersionUID = 7158574361234476025L;

    private final Object val;
    private final Integer wts;

    public WriteMvrrMessage(Address source, Integer wts, Object val) {
        super(source);

        this.val = val;
        this.wts = wts;
    }

    public Object getVal() {
        return val;
    }

    public Integer getWts() {
        return wts;
    }
}
