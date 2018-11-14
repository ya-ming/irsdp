package se.kth.ict.id2203.components.hc;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.Set;

public class HcInit extends Init<Hc> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;

	public HcInit(Address selfAddress, Set<Address> allAddresses) {
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
