package se.kth.ict.id2203.ports.erb;

import java.io.Serializable;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class RbDeliver extends Event implements Serializable {

	private static final long serialVersionUID = -8286469609005119810L;

	private final Address source;

	public RbDeliver(Address source) {
		this.source = source;
	}
	
	public final Address getSource() {
		return source;
	}
}
