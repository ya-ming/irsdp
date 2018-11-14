package se.kth.ict.id2203.ports.asc;

import se.sics.kompics.PortType;

public class AbortableSequenceConsensus extends PortType {
	{
		indication(AscDecide.class);
		indication(AscAbort.class);
		request(AscPropose.class);
	}
}
