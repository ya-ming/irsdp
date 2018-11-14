package se.kth.ict.id2203.components.reconfigurable.rsc;

import se.kth.ict.id2203.ports.reconfigurable.cfg.Configuration;
import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.Set;

public class MultiPaxosInit extends Init<MultiPaxos> {

	private final Address selfAddress;
	private final Set<Address> allAddresses;
	private final Configuration configuration;

	public MultiPaxosInit(Address selfAddress, Configuration configuration, Set<Address> allAddresses) {
		this.selfAddress = selfAddress;
		this.configuration = configuration;
		this.allAddresses = allAddresses;
	}
	
	public Address getSelfAddress() {
		return selfAddress;
	}

	public Set<Address> getAllAddresses() {
		return allAddresses;
	}

	public Configuration getConfiguration() {
		return configuration;
	}
}
