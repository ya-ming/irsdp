package se.kth.ict.id2203.components.riwcmnnar;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class ArDataMessage extends Pp2pDeliver{
    private static final long serialVersionUID = -1116661489560207948L;

    private final Integer rid, ts, wr;
    private final Object val;
    public ArDataMessage(Address source, Integer rid, Integer ts, Integer wr, Object val) {
        super(source);
        this.rid = rid;
        this.ts = ts;
        this.wr = wr;
        this.val = val;
    }

    public Integer getRid() {
        return rid;
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
