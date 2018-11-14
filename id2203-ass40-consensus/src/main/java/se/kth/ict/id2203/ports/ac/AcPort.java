package se.kth.ict.id2203.ports.ac;

import se.sics.kompics.PortType;

public class AcPort extends PortType {
	{
		request(AcPropose.class);
		indication(AcDecide.class);
	}
}
