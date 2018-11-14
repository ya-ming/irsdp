package se.kth.ict.id2203.ports.ble;

import se.sics.kompics.PortType;

public class BallotLeaderElection extends PortType {
	{
		indication(BallotLeader.class);
	}
}
