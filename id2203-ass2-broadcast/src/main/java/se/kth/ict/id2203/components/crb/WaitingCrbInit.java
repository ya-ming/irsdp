package se.kth.ict.id2203.components.crb;

import java.util.Set;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

public class WaitingCrbInit extends Init<WaitingCrb> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;

	public WaitingCrbInit(Address selfAddress, Set<Address> allAddresses) {
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
