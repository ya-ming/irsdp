package se.kth.ict.id2203.ports.fc;

import se.sics.kompics.PortType;

public class FloodingConsensus extends PortType {
	{
		indication(Decide.class);
		request(Propose.class);
	}
}
