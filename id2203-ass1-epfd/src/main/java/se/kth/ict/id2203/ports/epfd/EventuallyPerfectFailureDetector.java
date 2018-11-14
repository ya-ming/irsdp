package se.kth.ict.id2203.ports.epfd;

import se.sics.kompics.PortType;

public class EventuallyPerfectFailureDetector extends PortType {
	{
		indication(Suspect.class);
		indication(Restore.class);
	}
}
