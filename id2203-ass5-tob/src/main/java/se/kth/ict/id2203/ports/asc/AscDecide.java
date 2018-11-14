package se.kth.ict.id2203.ports.asc;

import se.sics.kompics.Event;

public class AscDecide extends Event {

	private final Object value;
	
	public AscDecide(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
}
