package se.kth.ict.id2203.pa.rowaonrr;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.Set;

public final class ApplicationInit extends Init<Application> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;
	private final String commandScript;

	public ApplicationInit(Address selfAddress, Set<Address> allAddresses, String commandScript) {
		this.selfAddress = selfAddress;
		this.allAddresses = allAddresses;
		this.commandScript = commandScript;
	}

	public final Address getSelfAddress() {
		return selfAddress;
	}
	
	public final Set<Address> getAllAddresses() {
		return allAddresses;
	}

	public final String getCommandScript() {
		return commandScript;
	}
}
