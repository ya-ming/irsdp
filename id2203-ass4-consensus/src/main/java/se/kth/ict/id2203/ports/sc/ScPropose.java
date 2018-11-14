package se.kth.ict.id2203.ports.sc;

import se.sics.kompics.Event;

public class ScPropose extends Event {
	private final Object value;

	public ScPropose(Object value) {
		this.value = value;
	}

	public final Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ScPropose{" +
				"value=" + value +
				'}';
	}
}
