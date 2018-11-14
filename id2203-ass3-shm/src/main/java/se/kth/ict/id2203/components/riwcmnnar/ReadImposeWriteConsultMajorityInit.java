package se.kth.ict.id2203.components.riwcmnnar;

import java.util.Set;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

public class ReadImposeWriteConsultMajorityInit extends Init<ReadImposeWriteConsultMajority> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;

	public ReadImposeWriteConsultMajorityInit(Address selfAddress, Set<Address> allAddresses) {
		this.selfAddress = selfAddress;
		this.allAddresses = allAddresses;
	}
	
	public final Address getSelfAddress() {
		return selfAddress;
	}

	public final Set<Address> getAllAddresses() {
		return allAddresses;
	}
}
