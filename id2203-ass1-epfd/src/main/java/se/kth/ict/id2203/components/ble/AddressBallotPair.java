package se.kth.ict.id2203.components.ble;

import se.sics.kompics.address.Address;

public class AddressBallotPair {
    private Address address;
    private Ballot ballot;

    public AddressBallotPair(Address address, Ballot ballot) {
        this.address = address;
        this.ballot = ballot;
    }

    public Address getAddress() {
        return address;
    }

    public Ballot getBallot() {
        return ballot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressBallotPair that = (AddressBallotPair) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        return ballot != null ? ballot.equals(that.ballot) : that.ballot == null;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (ballot != null ? ballot.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AddressBallotPair{" +
                "address=" + address +
                ", ballot=" + ballot +
                '}';
    }
}
