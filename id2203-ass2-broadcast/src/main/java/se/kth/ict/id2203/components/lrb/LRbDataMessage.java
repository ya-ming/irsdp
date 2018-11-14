package se.kth.ict.id2203.components.lrb;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.lrb.LRbDeliver;
import se.sics.kompics.address.Address;

public class LRbDataMessage extends BebDeliver{

    private static final long serialVersionUID = -8033448011408046123L;

    private final LRbDeliver deliverEvent;
    private int seqnum;

    public LRbDataMessage(Address source, LRbDeliver deliverEvent, int seqnum) {
        super(source);
        this.deliverEvent = deliverEvent;
        this.seqnum = seqnum;
    }

    public LRbDeliver getDeliverEvent() {
        return deliverEvent;
    }

    public int getSeqnum() {
        return seqnum;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)
        {
            return true;
        }
        if((obj != null) && (obj instanceof LRbDataMessage))
        {
            LRbDataMessage p = (LRbDataMessage)obj;
            if(getSource().equals(p.getSource()) && seqnum == p.getSeqnum())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getSource().hashCode() * 37 + seqnum;
    }
}
