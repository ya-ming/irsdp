package se.kth.ict.id2203.components.lurb;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.Set;

public class MajorityAckLURbInit extends Init<MajorityAckLURb> {

    private final Address selfAddress;
    private final Set<Address> allAddresses;

    public MajorityAckLURbInit(Address selfAddress, Set<Address> allAddresses) {
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
