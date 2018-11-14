package se.kth.ict.id2203.ports.fc;

import se.sics.kompics.Event;

public class Propose extends Event {

	private final Object value;

	public Propose(Object value) {
		this.value = value;
	}

	public final Object getValue() {
		return value;
	}
}
