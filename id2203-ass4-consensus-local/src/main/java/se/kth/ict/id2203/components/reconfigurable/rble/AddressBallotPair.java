package se.kth.ict.id2203.components.reconfigurable.rble;

import se.sics.kompics.address.Address;

public class AddressBallotPair {
    private Address address;
    private ReBallot reBallot;

    public AddressBallotPair(Address address, ReBallot reBallot) {
        this.address = address;
        this.reBallot = reBallot;
    }

    public Address getAddress() {
        return address;
    }

    public ReBallot getReBallot() {
        return reBallot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressBallotPair that = (AddressBallotPair) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        return reBallot != null ? reBallot.equals(that.reBallot) : that.reBallot == null;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (reBallot != null ? reBallot.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AddressBallotPair{" +
                "address=" + address +
                ", reBallot=" + reBallot +
                '}';
    }
}
