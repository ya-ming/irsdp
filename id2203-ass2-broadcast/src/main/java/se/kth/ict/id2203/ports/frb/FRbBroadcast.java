package se.kth.ict.id2203.ports.frb;

import se.sics.kompics.Event;

public class FRbBroadcast extends Event {

	private final FRbDeliver deliverEvent;

	public FRbBroadcast(FRbDeliver deliverEvent) {
		this.deliverEvent = deliverEvent;
	}
	
	public final FRbDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
