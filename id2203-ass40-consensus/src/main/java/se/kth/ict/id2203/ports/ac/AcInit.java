package se.kth.ict.id2203.ports.ac;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

public class AcInit extends Init<AcComponent> {
	private final int numberOfNodes;
	private final int nodeId;
	private final Address self;

	public AcInit(int numberOfNodes, int nodeId, Address self) {
		super();
		this.numberOfNodes = numberOfNodes;
		this.nodeId = nodeId;
		this.self = self;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public int getNodeId() {
		return nodeId;
	}

	public Address getSelf() {
		return self;
	}

}
