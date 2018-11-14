package se.kth.ict.id2203.components.aurb;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.aurb.AllAckURbDeliver;
import se.sics.kompics.address.Address;

public class AllAckURbDataMessage extends BebDeliver{

    private static final long serialVersionUID = -8033448011408046121L;

    private final AllAckURbDeliver deliverEvent;
    private int seqnum;

    public AllAckURbDataMessage(Address source, AllAckURbDeliver deliverEvent, int seqnum) {
        super(source);
        this.deliverEvent = deliverEvent;
        this.seqnum = seqnum;
    }

    public AllAckURbDeliver getDeliverEvent() {
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
        if((obj != null) && (obj instanceof AllAckURbDataMessage))
        {
            AllAckURbDataMessage p = (AllAckURbDataMessage)obj;
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
