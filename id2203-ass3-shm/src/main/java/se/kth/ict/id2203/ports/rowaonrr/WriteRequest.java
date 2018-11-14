package se.kth.ict.id2203.ports.rowaonrr;

import se.sics.kompics.Event;

public class WriteRequest extends Event {

	private final Object value;

	public WriteRequest(Object value) {
		super();
		this.value = value;
	}

	public Object getValue() {
		return value;
	}
}
