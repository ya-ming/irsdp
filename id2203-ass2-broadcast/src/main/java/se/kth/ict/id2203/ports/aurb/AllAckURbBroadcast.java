package se.kth.ict.id2203.ports.aurb;

import se.sics.kompics.Event;

public class AllAckURbBroadcast extends Event {

	private final AllAckURbDeliver deliverEvent;

	public AllAckURbBroadcast(AllAckURbDeliver deliverEvent) {
		this.deliverEvent = deliverEvent;
	}
	
	public final AllAckURbDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
