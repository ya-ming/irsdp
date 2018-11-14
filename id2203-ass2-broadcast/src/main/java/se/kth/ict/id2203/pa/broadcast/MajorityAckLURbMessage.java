package se.kth.ict.id2203.pa.broadcast;

import se.kth.ict.id2203.ports.lurb.MajorityAckLURbDeliver;
import se.sics.kompics.address.Address;

public class MajorityAckLURbMessage extends MajorityAckLURbDeliver {

    private static final long serialVersionUID = -1855724247802103123L;

    private final String message;

    public MajorityAckLURbMessage(Address source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj != null) && (obj instanceof MajorityAckLURbMessage)) {
            MajorityAckLURbMessage p = (MajorityAckLURbMessage) obj;
            if (getSource().equals(p.getSource()) && message.equals(p.getMessage())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getSource().hashCode() * 37 + this.getMessage().hashCode();
    }
}


