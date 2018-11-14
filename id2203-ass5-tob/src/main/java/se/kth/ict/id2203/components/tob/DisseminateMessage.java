package se.kth.ict.id2203.components.tob;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class DisseminateMessage extends BebDeliver {

	private static final long serialVersionUID = 4150619576496319910L;

	private final Message message;

	public DisseminateMessage(Address source, Message message) {
		super(source);
		this.message = message;
	}
	
	public Message getMessage() {
		return message;
	}
}
