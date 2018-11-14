package se.kth.ict.id2203.components.huc;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.Set;

public class HucInit extends Init<Huc> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;

	public HucInit(Address selfAddress, Set<Address> allAddresses) {
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
