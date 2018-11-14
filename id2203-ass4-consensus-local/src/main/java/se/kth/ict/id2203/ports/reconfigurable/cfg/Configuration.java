package se.kth.ict.id2203.ports.reconfigurable.cfg;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

import java.util.Set;

public class Configuration extends Event {
    private Integer cfg;
    private Set<Address> addresses;

    public void setCfg(Integer cfg) {
        this.cfg = cfg;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public Configuration(Integer cfg, Set<Address> addresses) {
        this.cfg = cfg;
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "cfg=" + cfg +
                ", addresses=" + addresses +
                '}';
    }

    public Integer getCfg() {
        return cfg;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }
}
