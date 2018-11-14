package se.kth.ict.id2203.components.riwmonar;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class ValueRiwmonarMessage extends Pp2pDeliver {
    private static final long serialVersionUID = 7158512345728476025L;

    private final Object val;
    private final Integer r, ts;

    @Override
    public String toString() {
        return "ValueRiwmonarMessage{" +
                "val=" + val +
                ", r=" + r +
                ", ts=" + ts +
                '}';
    }

    public ValueRiwmonarMessage(Address source, Integer r, Integer ts, Object val) {
        super(source);

        this.r = r;
        this.ts = ts;
        this.val = val;
    }

    public Integer getTs() {
        return ts;
    }

    public Object getVal() {
        return val;
    }

    public Integer getR() {
        return r;
    }
}
