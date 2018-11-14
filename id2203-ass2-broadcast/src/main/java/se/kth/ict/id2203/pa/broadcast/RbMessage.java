package se.kth.ict.id2203.pa.broadcast;

import se.kth.ict.id2203.ports.erb.RbDeliver;
import se.sics.kompics.address.Address;

public class RbMessage extends RbDeliver {

	private static final long serialVersionUID = -1855724247802103843L;

	private final String message;

	public RbMessage(Address source, String message) {
		super(source);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
