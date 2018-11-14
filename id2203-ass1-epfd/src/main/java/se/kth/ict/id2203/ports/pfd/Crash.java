package se.kth.ict.id2203.ports.pfd;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class Crash extends Event {

	private final Address source;

	public Crash(Address source) {
		this.source = source;
	}

	public final Address getSource() {
		return source;
	}
}
