package se.kth.ict.id2203.components.frb;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.erb.RbDeliver;
import se.kth.ict.id2203.ports.frb.FRbDeliver;
import se.sics.kompics.address.Address;

public class FRbDataMessage extends RbDeliver{

    private static final long serialVersionUID = -8033448011408046391L;

    private final FRbDeliver deliverEvent;
    private int seqnum;

    public FRbDataMessage(Address source, FRbDeliver deliverEvent, int seqnum) {
        super(source);
        this.deliverEvent = deliverEvent;
        this.seqnum = seqnum;
    }

    public FRbDeliver getDeliverEvent() {
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
        if((obj != null) && (obj instanceof FRbDataMessage))
        {
            FRbDataMessage p = (FRbDataMessage)obj;
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
