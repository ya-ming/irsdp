package se.kth.ict.id2203.components.riwmonar;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class WriteRiwmonarMessage extends BebDeliver{
    private static final long serialVersionUID = 7158574361234476025L;

    @Override
    public String toString() {
        return "WriteRiwmonarMessage{" +
                "rid=" + rid +
                ", wts=" + wts +
                ", val=" + val +
                '}';
    }

    private final Integer rid, wts;
    private final Object val;


    public WriteRiwmonarMessage(Address source, Integer rid, Integer wts, Object val) {
        super(source);

        this.rid = rid;
        this.val = val;
        this.wts = wts;
    }

    public Object getVal() {
        return val;
    }

    public Integer getWts() {
        return wts;
    }

    public Integer getRid() {
        return rid;
    }
}
