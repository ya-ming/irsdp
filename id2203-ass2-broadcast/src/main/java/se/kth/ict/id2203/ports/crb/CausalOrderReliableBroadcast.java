package se.kth.ict.id2203.ports.crb;

import se.sics.kompics.PortType;

public class CausalOrderReliableBroadcast extends PortType {
	{
		indication(CrbDeliver.class);
		request(CrbBroadcast.class);
	}
}
