package se.kth.ict.id2203.ports.sp2p;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public final class Sp2pSend extends Event{
    private final Sp2pDeliver deliverEvent;
    private final Address destination;

    public Sp2pSend(Address destination, Sp2pDeliver sp2pDeliver) {
        this.destination = destination;
        this.deliverEvent = sp2pDeliver;
    }

    public final Sp2pDeliver getDeliverEvent() {
        return deliverEvent;
    }

    public final Address getDestination() {
        return destination;
    }
}
