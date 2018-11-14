package se.kth.ict.id2203.app.fpl;

import se.kth.ict.id2203.ports.fpl.FPp2pDeliver;
import se.sics.kompics.address.Address;

public class FPp2pMessage extends FPp2pDeliver {

	private static final long serialVersionUID = 2193713942080123560L;
	
	private final String message;

	public FPp2pMessage(Address source, String message) {
		super(source);
		this.message = message;
	}

	public final String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "FPp2pMessage{" +
				"message='" + message + '\'' +
				'}';
	}
}
