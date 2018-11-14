package se.kth.ict.id2203.ports.reconfigurable.rsc;

import se.sics.kompics.Event;

public class ScAbort extends Event {
	private final Object value;

	public ScAbort(Object value) {
		this.value = value;
	}

	public final Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ScDebug{" +
				"value=" + value +
				'}';
	}
}
