package se.kth.ict.id2203.ports.reconfigurable.rsc;

import se.sics.kompics.PortType;

public class SequenceConsensus extends PortType {
	{
		indication(ScDecide.class);
		indication(ScAbort.class);
		request(ScPropose.class);
		request(ScDebug.class);
	}
}
