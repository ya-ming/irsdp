package se.kth.ict.id2203.ports.lrb;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

import java.io.Serializable;

public class LRbDeliver extends Event implements Serializable {

	private static final long serialVersionUID = -8286469609012345678L;

	private final Address source;

	public LRbDeliver(Address source) {
		this.source = source;
	}
	
	public final Address getSource() {
		return source;
	}
}
