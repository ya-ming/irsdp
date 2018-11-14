package se.kth.ict.id2203.components.reconfigurable.rble;

import se.kth.ict.id2203.ports.reconfigurable.cfg.Configuration;
import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.Set;

public class ReconfigurableBleInit extends Init<ReconfigurableBle> {
    private final Address selfAddress;
    private final Configuration configuration;
    private final Set<Address> allAddresses;
    private final long initialDelay;
    private final long deltaDelay;

    public ReconfigurableBleInit(Address selfAddress, Configuration configuration, Set<Address> allAddresses,
                                 long initialDelay, long deltaDelay) {
        this.selfAddress = selfAddress;
        this.configuration = configuration;
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

    public Configuration getConfiguration() {
        return configuration;
    }
}
