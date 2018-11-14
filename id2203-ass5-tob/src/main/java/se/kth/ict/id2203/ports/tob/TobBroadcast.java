package se.kth.ict.id2203.ports.tob;

import se.sics.kompics.Event;

public class TobBroadcast extends Event {

	private final TobDeliver deliverEvent;

	public TobBroadcast(TobDeliver deliverEvent) {
		this.deliverEvent = deliverEvent;
	}

	public final TobDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
