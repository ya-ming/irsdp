package se.kth.ict.id2203.ports.tob;

import java.io.Serializable;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class TobDeliver extends Event implements Serializable {

	private static final long serialVersionUID = -5149201493656182119L;

	private Address source;

	public TobDeliver(Address source) {
		this.source = source;
	}
	
	public final Address getSource() {
		return source;
	}
}
