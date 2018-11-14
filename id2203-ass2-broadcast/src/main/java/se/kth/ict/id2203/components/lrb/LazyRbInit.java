package se.kth.ict.id2203.components.lrb;

import java.util.Set;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

public class LazyRbInit extends Init<LazyRb> {

    private final Address selfAddress;
    private final Set<Address> allAddresses;

    public LazyRbInit(Address selfAddress, Set<Address> allAddresses) {
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
