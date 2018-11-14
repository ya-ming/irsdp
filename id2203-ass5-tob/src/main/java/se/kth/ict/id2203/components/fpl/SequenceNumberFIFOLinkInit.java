package se.kth.ict.id2203.components.fpl;

import java.util.Set;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

public class SequenceNumberFIFOLinkInit extends Init<SequenceNumberFIFOLink> {

	private final Address self;
	private final Set<Address> allAddresses;
	
	public SequenceNumberFIFOLinkInit(Address self, Set<Address> allAddresses) {
		this.self = self;
		this.allAddresses = allAddresses;
	}

	public Address getSelf() {
		return self;
	}

	public Set<Address> getAllAddresses() {
		return allAddresses;
	}
}
