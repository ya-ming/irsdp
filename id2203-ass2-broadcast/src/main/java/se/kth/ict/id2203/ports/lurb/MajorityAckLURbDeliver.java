package se.kth.ict.id2203.ports.lurb;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

import java.io.Serializable;

public class MajorityAckLURbDeliver extends Event implements Serializable {

	private static final long serialVersionUID = -8286469609012345670L;

	private final Address source;

	public MajorityAckLURbDeliver(Address source) {
		this.source = source;
	}
	
	public final Address getSource() {
		return source;
	}
}
