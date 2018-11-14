package se.kth.ict.id2203.ports.reconfigurable.rble;

import se.sics.kompics.PortType;

public class ReconfigurableBallotLeaderElection extends PortType {
	{
		indication(ReconfigurableBallotLeader.class);
	}
}
