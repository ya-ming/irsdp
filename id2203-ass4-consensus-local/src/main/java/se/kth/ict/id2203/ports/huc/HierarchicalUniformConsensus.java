package se.kth.ict.id2203.ports.huc;

import se.kth.ict.id2203.ports.fc.Decide;
import se.kth.ict.id2203.ports.fc.Propose;
import se.sics.kompics.PortType;

public class HierarchicalUniformConsensus extends PortType {
	{
		indication(Decide.class);
		request(Propose.class);
	}
}
