package se.kth.ict.id2203.components.epfd;

import java.util.Set;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

public class EpfdInit extends Init<Epfd> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;
	private final long initialDelay;
	private final long deltaDelay;

	public EpfdInit(Address selfAddress, Set<Address> allAddresses, long initialDelay, long deltaDelay) {
		this.selfAddress = selfAddress;
		this.allAddresses = allAddresses;
		this.initialDelay = initialDelay;
		this.deltaDelay = deltaDelay;
	}

	public Address getSelfAddress() {
		return selfAddress;
	}

	public Set<Address> getAllAddresses() {
		return allAddresses;
	}

	public final long getInitialDelay() {
		return initialDelay;
	}
	
	public final long getDeltaDelay() {
		return deltaDelay;
	}
}
