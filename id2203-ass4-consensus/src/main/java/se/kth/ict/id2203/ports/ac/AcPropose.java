package se.kth.ict.id2203.ports.ac;

import se.sics.kompics.Event;

public class AcPropose extends Event {
	private final Object value;

	public AcPropose(Object value) {
		this.value = value;
	}

	public final Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "AcPropose{" +
				"value=" + value +
				'}';
	}
}
