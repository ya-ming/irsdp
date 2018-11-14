package se.kth.ict.id2203.components.le;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.Set;

public class LeInit extends Init<Le> {

    private final Address selfAddress;
    private final Set<Address> allAddresses;

    public LeInit(Address selfAddress, Set<Address> allAddresses) {
        this.selfAddress = selfAddress;
        this.allAddresses = allAddresses;
//        System.out.println("###################" + allAddresses.toString());
    }

    public Address getSelfAddress() {
        return selfAddress;
    }

    public Set<Address> getAllAddresses() {
        return allAddresses;
    }
}
