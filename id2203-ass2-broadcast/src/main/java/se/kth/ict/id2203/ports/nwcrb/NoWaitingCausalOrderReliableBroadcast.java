package se.kth.ict.id2203.ports.nwcrb;

import se.sics.kompics.PortType;

public class NoWaitingCausalOrderReliableBroadcast extends PortType {
	{
		indication(NWCrbDeliver.class);
		request(NWCrbBroadcast.class);
	}
}
