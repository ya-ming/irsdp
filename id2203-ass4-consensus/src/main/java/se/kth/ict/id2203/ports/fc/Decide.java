package se.kth.ict.id2203.ports.fc;

import se.sics.kompics.Event;

public class Decide extends Event {

	private final Object value;

	public Decide(Object value) {
		this.value = value;
	}
	
	public final Object getValue() {
		return value;
	}
}
