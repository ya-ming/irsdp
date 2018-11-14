package se.kth.ict.id2203.ports.frb;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

import java.io.Serializable;

public class FRbDeliver extends Event implements Serializable {

	private static final long serialVersionUID = -8286469609005119810L;

	private final Address source;

	public FRbDeliver(Address source) {
		this.source = source;
	}
	
	public final Address getSource() {
		return source;
	}
}
