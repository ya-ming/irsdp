package se.kth.ict.id2203.components.epb;

import se.kth.ict.id2203.ports.epb.EPbDeliver;
import se.kth.ict.id2203.ports.flp2p.Flp2pDeliver;
import se.sics.kompics.address.Address;

public class EPbDataMessage extends Flp2pDeliver {

    private static final long serialVersionUID = -8033448011408046121L;

    private final EPbDeliver deliverEvent;
    private int seqnum;
    private int round;


    public EPbDataMessage(Address source, EPbDeliver deliverEvent, int round, int seqnum) {
        super(source);
        this.deliverEvent = deliverEvent;
        this.seqnum = seqnum;
        this.round = round;
    }

    public EPbDeliver getDeliverEvent() {
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
        if((obj != null) && (obj instanceof EPbDataMessage))
        {
            EPbDataMessage p = (EPbDataMessage)obj;
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

    public int getRound() {
        return round;
    }
}
