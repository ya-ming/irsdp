package se.kth.ict.id2203.components.huc;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HucAck extends Pp2pDeliver {
    private static final long serialVersionUID = -1456661489560207947L;

    public HucAck(Address source) {
        super(source);
    }
}

