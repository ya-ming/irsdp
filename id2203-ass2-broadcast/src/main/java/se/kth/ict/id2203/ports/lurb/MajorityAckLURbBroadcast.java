package se.kth.ict.id2203.ports.lurb;

import se.sics.kompics.Event;

public class MajorityAckLURbBroadcast extends Event {

	private final MajorityAckLURbDeliver deliverEvent;

	public MajorityAckLURbBroadcast(MajorityAckLURbDeliver deliverEvent) {
		this.deliverEvent = deliverEvent;
	}
	
	public final MajorityAckLURbDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
