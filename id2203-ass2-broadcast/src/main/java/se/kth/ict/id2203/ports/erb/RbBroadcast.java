package se.kth.ict.id2203.ports.erb;

import se.sics.kompics.Event;

public class RbBroadcast extends Event {

	private final RbDeliver deliverEvent;

	public RbBroadcast(RbDeliver deliverEvent) {
		this.deliverEvent = deliverEvent;
	}
	
	public final RbDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
