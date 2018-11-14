package se.kth.ict.id2203.ports.nwcrb;

import se.sics.kompics.Event;

public class NWCrbBroadcast extends Event {

	private final NWCrbDeliver deliverEvent;

	public NWCrbBroadcast(NWCrbDeliver deliverEvent) {
		this.deliverEvent = deliverEvent;
	}

	public NWCrbDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
