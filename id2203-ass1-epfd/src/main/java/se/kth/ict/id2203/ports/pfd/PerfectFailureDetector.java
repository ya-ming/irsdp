package se.kth.ict.id2203.ports.pfd;

import se.sics.kompics.PortType;

public class PerfectFailureDetector extends PortType {
	{
		indication(Crash.class);
	}
}
