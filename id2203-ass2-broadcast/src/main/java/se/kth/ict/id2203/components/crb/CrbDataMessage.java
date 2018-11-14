package se.kth.ict.id2203.components.crb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.crb.CrbDeliver;
import se.kth.ict.id2203.ports.erb.RbDeliver;
import se.sics.kompics.address.Address;

import java.util.Vector;

public class CrbDataMessage extends RbDeliver{

    private static final Logger logger = LoggerFactory.getLogger(CrbDataMessage.class);

    private static final long serialVersionUID = -8055668011408046391L;

    private final CrbDeliver deliverEvent;

    private final Vector<Integer> v;

    public CrbDataMessage(Address source, CrbDeliver deliverEvent, Vector<Integer> v) {
        super(source);
        this.deliverEvent = deliverEvent;
        this.v = v;
    }

    public CrbDeliver getDeliverEvent() {
        return deliverEvent;
    }

    public Vector<Integer> getV() {
        return v;
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)
        {
            return true;
        }
        if((obj != null) && (obj instanceof CrbDataMessage))
        {
            CrbDataMessage p = (CrbDataMessage)obj;
            if(getSource().equals(p.getSource()) && v == p.getV())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getSource().hashCode() * 37 + this.getV().hashCode();
    }

    public boolean lessOrEqualto(Vector<Integer> v) {
        logger.debug("lessOrEqualTo" + " W' " + this.v + " V " + v);
        if (this.getV().size() != v.size()) {
            logger.debug("lessOrEqualTo, size not equal");
            return false;
        }

        for (int i = 0; i < this.getV().size(); i++) {
//            logger.debug("lessOrEqualTo content " + v.get(i) + " : " + this.getV().get(i));
            if (v.get(i) < this.getV().get(i)) {
                return false;
            }
        }

        return true;
    }
}
