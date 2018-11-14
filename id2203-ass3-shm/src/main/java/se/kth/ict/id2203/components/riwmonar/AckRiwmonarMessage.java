package se.kth.ict.id2203.components.riwmonar;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class AckRiwmonarMessage extends Pp2pDeliver {
    private static final long serialVersionUID = 7158512345728476025L;

    private final Integer rid;

    public AckRiwmonarMessage(Address source, Integer rid) {
        super(source);

        this.rid = rid;
    }

    @Override
    public String toString() {
        return "AckRiwmonarMessage{" +
                "rid=" + rid +
                '}';
    }

    public Integer getRid() {
        return rid;
    }
}
