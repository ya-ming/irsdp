package se.kth.ict.id2203.ports.aurb;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

import java.io.Serializable;

public class AllAckURbDeliver extends Event implements Serializable {

	private static final long serialVersionUID = -8286469609012345670L;

	private final Address source;

	public AllAckURbDeliver(Address source) {
		this.source = source;
	}
	
	public final Address getSource() {
		return source;
	}
}
