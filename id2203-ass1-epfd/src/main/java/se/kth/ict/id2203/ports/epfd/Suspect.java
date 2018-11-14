package se.kth.ict.id2203.ports.epfd;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class Suspect extends Event {

	private final Address source;

	public Suspect(Address source) {
		this.source = source;
	}

	public final Address getSource() {
		return source;
	}
}
