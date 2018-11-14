package se.kth.ict.id2203.ports.le;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class Leader extends Event {

	private final Address source;

	public Leader(Address source) {
		this.source = source;
	}

	public final Address getSource() {
		return source;
	}
}
