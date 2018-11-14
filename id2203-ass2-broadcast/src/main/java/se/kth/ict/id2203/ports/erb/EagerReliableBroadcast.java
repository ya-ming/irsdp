package se.kth.ict.id2203.ports.erb;

import se.sics.kompics.PortType;

public class EagerReliableBroadcast extends PortType {
	{
		indication(RbDeliver.class);
		request(RbBroadcast.class);
	}
}
