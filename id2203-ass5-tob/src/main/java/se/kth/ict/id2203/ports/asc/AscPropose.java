package se.kth.ict.id2203.ports.asc;

import se.sics.kompics.Event;

public class AscPropose extends Event {
	
	private final Object value;
	
	public AscPropose(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
}
