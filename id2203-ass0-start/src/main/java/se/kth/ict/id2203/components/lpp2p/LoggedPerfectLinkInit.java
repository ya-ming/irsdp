package se.kth.ict.id2203.components.lpp2p;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class LoggedPerfectLinkInit extends Init<LoggedPerfectLink> {

	private final Topology topology;
	private final long sigma;

	public LoggedPerfectLinkInit(Topology topology) {
		this(topology, 0);
	}
	
	public LoggedPerfectLinkInit(Topology topology, long sigma) {
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
