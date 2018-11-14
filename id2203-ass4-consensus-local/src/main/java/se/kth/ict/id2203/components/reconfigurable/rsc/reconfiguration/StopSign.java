package se.kth.ict.id2203.components.reconfigurable.rsc.reconfiguration;

import se.sics.kompics.address.Address;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class StopSign implements Serializable {
    private static final long serialVersionUID = 5566512345112476025L;


    private Integer version;
    private Integer rid;
    private Set<Address> pi;

    public StopSign(Integer version, Set<Address> pi, Integer rid) {
        this.version = version;
        this.rid = rid;
        this.pi = new HashSet<Address>(pi);
    }

    public Integer getVersion() {
        return version;
    }

    public Integer getRid() {
        return rid;
    }

    public Set<Address> getPi() {
        return pi;
    }

    @Override
    public String toString() {
        return "StopSign{" +
                "version=" + version +
                ", rid=" + rid +
                ", pi=" + pi +
                '}';
    }
}
