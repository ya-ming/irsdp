package se.kth.ict.id2203.components.sc;

import se.sics.kompics.address.Address;

public class NlLeaderPair {
    public Integer nL = 0;
    public Address leader = null;

    @Override
    public String toString() {
        return "NlLeaderPair{" +
                "nL=" + nL +
                ", leader=" + leader +
                '}';
    }
}
