package se.kth.ict.id2203.ports.frb;

import se.sics.kompics.PortType;

public class FIFOReliableBroadcast extends PortType {
	{
		indication(FRbDeliver.class);
		request(FRbBroadcast.class);
	}
}
