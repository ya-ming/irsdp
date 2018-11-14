package se.kth.ict.id2203.pa.tob;

import se.kth.ict.id2203.ports.tob.TobDeliver;
import se.sics.kompics.address.Address;

public class TobMessage extends TobDeliver {

	private static final long serialVersionUID = -1395141136695708761L;

	private final String message;

	public TobMessage(Address source, String message) {
		super(source);
		this.message = message;
	}

	public final String getMessage() {
		return message;
	}
}
