package se.kth.ict.id2203.components.nwcrb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.erb.RbDeliver;
import se.kth.ict.id2203.ports.nwcrb.NWCrbDeliver;
import se.sics.kompics.address.Address;

import java.util.Vector;

public class NWCrbDataMessage extends RbDeliver{

    private static final Logger logger = LoggerFactory.getLogger(NWCrbDataMessage.class);

    private static final long serialVersionUID = -8055668011408046392L;

    private final NWCrbDeliver deliverEvent;

    private final Vector<NWCrbDeliver> mpast;

    public NWCrbDataMessage(Address source, NWCrbDeliver deliverEvent, Vector<NWCrbDeliver> mpast) {
        super(source);
        this.deliverEvent = deliverEvent;
        this.mpast = mpast;
    }

    public NWCrbDeliver getDeliverEvent() {
        return deliverEvent;
    }

    public Vector<NWCrbDeliver> getMpast() {
        return mpast;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)
        {
            return true;
        }
        if((obj != null) && (obj instanceof NWCrbDataMessage))
        {
            NWCrbDataMessage p = (NWCrbDataMessage)obj;
            if(getSource().equals(p.getSource()) && mpast.equals(p.getMpast()))
            {
                return true;
            }
        }
        return false;
    }

//    @Override
//    public int hashCode() {
//        return this.getSource().hashCode() * 37 + this.getMpast().hashCode();
//    }

//    public boolean lessOrEqualto(Vector<Integer> mpast) {
//        logger.debug("lessOrEqualTo" + " W' " + this.mpast + " V " + mpast);
//        if (this.getMpast().size() != mpast.size()) {
//            logger.debug("lessOrEqualTo, size not equal");
//            return false;
//        }
//
//        for (int i = 0; i < this.getMpast().size(); i++) {
////            logger.debug("lessOrEqualTo content " + mpast.get(i) + " : " + this.getMpast().get(i));
//            if (mpast.get(i) < this.getMpast().get(i)) {
//                return false;
//            }
//        }
//
//        return true;
//    }
}
