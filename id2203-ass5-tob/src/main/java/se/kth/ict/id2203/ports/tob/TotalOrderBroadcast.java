package se.kth.ict.id2203.ports.tob;

import se.sics.kompics.PortType;

public class TotalOrderBroadcast extends PortType {
	{
		indication(TobDeliver.class);
		request(TobBroadcast.class);
	}
}
