package se.kth.ict.id2203.app.epfd;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class Pp2pMessage extends Pp2pDeliver {

	private static final long serialVersionUID = 2193713942080123560L;
	
	private final String message;

	public Pp2pMessage(Address source, String message) {
		super(source);
		this.message = message;
	}

	public final String getMessage() {
		return message;
	}
}
