package se.kth.ict.id2203.ports.crb;

import java.io.Serializable;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class CrbDeliver extends Event implements Serializable {

	private static final long serialVersionUID = 4088333329204792579L;

	private Address source;

	public CrbDeliver(Address source) {
		this.source = source;
	}
	
	public Address getSource() {
		return source;
	}
}
