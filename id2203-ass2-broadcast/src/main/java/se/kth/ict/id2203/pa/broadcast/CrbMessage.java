package se.kth.ict.id2203.pa.broadcast;

import se.kth.ict.id2203.ports.crb.CrbDeliver;
import se.sics.kompics.address.Address;

public class CrbMessage extends CrbDeliver {

	private static final long serialVersionUID = 448909643383096233L;

	private final String message;

	public CrbMessage(Address source, String message) {
		super(source);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
