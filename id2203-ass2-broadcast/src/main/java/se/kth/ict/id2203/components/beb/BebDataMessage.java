package se.kth.ict.id2203.components.beb;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class BebDataMessage extends Pp2pDeliver{

    private static final long serialVersionUID = -8011228011408046391L;

    private final BebDeliver deliverEvent;

    public BebDataMessage(Address source, BebDeliver deliverEvent) {
        super(source);
        this.deliverEvent = deliverEvent;
    }

    public BebDeliver getDeliverEvent() {
        return deliverEvent;
    }
}
