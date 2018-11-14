package se.kth.ict.id2203.components.fuc;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.Set;

public class FucInit extends Init<Fuc> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;

	public FucInit(Address selfAddress, Set<Address> allAddresses) {
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
