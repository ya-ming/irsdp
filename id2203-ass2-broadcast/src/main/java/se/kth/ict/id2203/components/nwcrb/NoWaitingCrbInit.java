package se.kth.ict.id2203.components.nwcrb;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.Set;

public class NoWaitingCrbInit extends Init<NoWaitingCrb> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;

	public NoWaitingCrbInit(Address selfAddress, Set<Address> allAddresses) {
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
