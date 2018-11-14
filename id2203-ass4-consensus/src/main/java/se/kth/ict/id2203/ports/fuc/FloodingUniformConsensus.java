package se.kth.ict.id2203.ports.fuc;

import se.kth.ict.id2203.ports.fc.Decide;
import se.kth.ict.id2203.ports.fc.Propose;
import se.sics.kompics.PortType;

public class FloodingUniformConsensus extends PortType {
	{
		indication(Decide.class);
		request(Propose.class);
	}
}
