package se.kth.ict.id2203.components.eld;

import java.util.Set;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

public class MonarchicalEldInit extends Init<MonarchicalEld> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;
	private final int initialTimeoutPeriod;
	private final int timeoutPeriodDelta;

	public MonarchicalEldInit(Address selfAddress, Set<Address> allAddresses, int initialTimeoutPeriod, int timeoutPeriodDelta) {
		this.selfAddress = selfAddress;
		this.allAddresses = allAddresses;
		this.initialTimeoutPeriod = initialTimeoutPeriod;
		this.timeoutPeriodDelta = timeoutPeriodDelta;
	}
	
	public Address getSelfAddress() {
		return selfAddress;
	}
	
	public Set<Address> getAllAddresses() {
		return allAddresses;
	}
	
	public int getInitialTimeoutPeriod() {
		return initialTimeoutPeriod;
	}

	public int getTimeoutPeriodDelta() {
		return timeoutPeriodDelta;
	}
}
