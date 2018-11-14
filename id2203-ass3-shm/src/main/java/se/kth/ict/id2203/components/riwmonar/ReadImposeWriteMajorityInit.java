package se.kth.ict.id2203.components.riwmonar;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.Set;

public class ReadImposeWriteMajorityInit extends Init<ReadImposeWriteMajority> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;

	public ReadImposeWriteMajorityInit(Address selfAddress, Set<Address> allAddresses) {
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
