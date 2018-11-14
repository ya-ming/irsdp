package se.kth.ict.id2203.ports.nwcrb;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

import java.io.Serializable;

public class NWCrbDeliver extends Event implements Serializable {

	private static final long serialVersionUID = 4088333329204792579L;

	private Address source;

	public NWCrbDeliver(Address source) {
		this.source = source;
	}
	
	public Address getSource() {
		return source;
	}
}
