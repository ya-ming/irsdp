package se.kth.ict.id2203.ports.lrb;

import se.sics.kompics.Event;

public class LRbBroadcast extends Event {

	private final LRbDeliver deliverEvent;

	public LRbBroadcast(LRbDeliver deliverEvent) {
		this.deliverEvent = deliverEvent;
	}
	
	public final LRbDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
