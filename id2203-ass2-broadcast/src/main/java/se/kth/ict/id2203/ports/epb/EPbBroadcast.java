package se.kth.ict.id2203.ports.epb;

import se.sics.kompics.Event;

public class EPbBroadcast extends Event {

	private final EPbDeliver deliverEvent;

	public EPbBroadcast(EPbDeliver deliverEvent) {
		this.deliverEvent = deliverEvent;
	}
	
	public final EPbDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
