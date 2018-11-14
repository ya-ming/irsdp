package se.kth.ict.id2203.pa.broadcast;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class BebMessage extends BebDeliver {

	private final String message;

	private static final long serialVersionUID = 5491596109178800519L;

	public BebMessage(Address source, String message) {
		super(source);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
