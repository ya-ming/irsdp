package se.kth.ict.id2203.components.erb;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.erb.RbDeliver;
import se.sics.kompics.address.Address;

public class RbDataMessage extends BebDeliver{

    private static final long serialVersionUID = -8033448011408046391L;

    private final RbDeliver deliverEvent;
    private int seqnum;

    public RbDataMessage(Address source, RbDeliver deliverEvent, int seqnum) {
        super(source);
        this.deliverEvent = deliverEvent;
        this.seqnum = seqnum;
    }

    public RbDeliver getDeliverEvent() {
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
        if((obj != null) && (obj instanceof RbDataMessage))
        {
            RbDataMessage p = (RbDataMessage)obj;
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
