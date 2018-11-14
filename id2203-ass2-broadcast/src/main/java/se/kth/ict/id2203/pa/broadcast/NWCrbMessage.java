package se.kth.ict.id2203.pa.broadcast;

import se.kth.ict.id2203.ports.nwcrb.NWCrbDeliver;
import se.sics.kompics.address.Address;

public class NWCrbMessage extends NWCrbDeliver {

    private static final long serialVersionUID = 448909643383096233L;

    private final String message;

    public NWCrbMessage(Address source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
//        System.out.println("XXX equals");
        if(this==obj)
        {
//            System.out.println("XXX equals this==obj");
            return true;
        }
//        System.out.println("XXX equals before if");
        if((obj != null) && (obj instanceof NWCrbMessage))
        {
            NWCrbMessage p = (NWCrbMessage)obj;
//            System.out.println("XXX equals compare source and message " + getSource() + " " + p.getSource());
//            System.out.println("XXX equals compare source and message " + message + " " + p.getMessage());
            if(getSource().equals(p.getSource()) && message.equals(p.getMessage()))
            {
//                System.out.println("XXX equals return true");
                return true;
            }
        }
//        System.out.println("XXX equals return false");
        return false;
    }

    @Override
    public int hashCode() {
//        System.out.println("XXX hashCode " + this.getSource() + " " + this.getSource().hashCode() * 37 + this.getMessage().hashCode());
        return this.getSource().hashCode() * 37 + this.getMessage().hashCode();
    }
}

