package se.kth.ict.id2203.components.frb;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.Set;

public class FIFORbInit extends Init<FIFORb> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;

	public FIFORbInit(Address selfAddress, Set<Address> allAddresses) {
		this.selfAddress = selfAddress;
		this.allAddresses = allAddresses;
	}
	
	public Address getSelfAddress() {
		return selfAddress;
	}
	
	public Set<Address> getAllAddresses() {
		return allAddresses;
	}
}
