package se.kth.ict.id2203.ports.crb;

import se.sics.kompics.Event;

public class CrbBroadcast extends Event {

	private final CrbDeliver deliverEvent;

	public CrbBroadcast(CrbDeliver deliverEvent) {
		this.deliverEvent = deliverEvent;
	}

	public CrbDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
