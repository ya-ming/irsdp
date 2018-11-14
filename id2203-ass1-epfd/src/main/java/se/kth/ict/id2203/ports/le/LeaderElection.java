package se.kth.ict.id2203.ports.le;

import se.sics.kompics.PortType;

public class LeaderElection extends PortType {
	{
		indication(Leader.class);
	}
}
