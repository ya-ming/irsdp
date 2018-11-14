package se.kth.ict.id2203.components.sp2p;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class StubbornLinkInit extends Init<StubbornLink> {

	private final Topology topology;
	private final long sigma;

	public StubbornLinkInit(Topology topology) {
		this(topology, 0);
	}
	
	public StubbornLinkInit(Topology topology, long sigma) {
		this.topology = topology;
		this.sigma = sigma;
	}
	
	public final Topology getTopology() {
		return topology;
	}

	public final long getSigma() {
		return sigma;
	}
}
