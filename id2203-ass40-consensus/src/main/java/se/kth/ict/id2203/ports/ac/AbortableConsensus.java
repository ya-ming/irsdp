package se.kth.ict.id2203.ports.ac;

import se.sics.kompics.PortType;

public class AbortableConsensus extends PortType {
	{
		indication(AcDecide.class);
		indication(AcAbort.class);
		request(AcPropose.class);
	}
}
