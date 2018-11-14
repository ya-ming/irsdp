package se.kth.ict.id2203.ports.sc;

import se.sics.kompics.PortType;

public class SequenceConsensus extends PortType {
	{
		indication(ScDecide.class);
		request(ScPropose.class);
	}
}
