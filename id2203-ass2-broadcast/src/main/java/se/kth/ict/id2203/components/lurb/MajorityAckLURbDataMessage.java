package se.kth.ict.id2203.components.lurb;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.lurb.MajorityAckLURbDeliver;
import se.sics.kompics.address.Address;

public class MajorityAckLURbDataMessage extends BebDeliver{

    private static final long serialVersionUID = -8033448011408046121L;

    private final MajorityAckLURbDeliver deliverEvent;
    private int seqnum;

    public MajorityAckLURbDataMessage(Address source, MajorityAckLURbDeliver deliverEvent, int seqnum) {
        super(source);
        this.deliverEvent = deliverEvent;
        this.seqnum = seqnum;
    }

    public MajorityAckLURbDeliver getDeliverEvent() {
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
        if((obj != null) && (obj instanceof MajorityAckLURbDataMessage))
        {
            MajorityAckLURbDataMessage p = (MajorityAckLURbDataMessage)obj;
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
