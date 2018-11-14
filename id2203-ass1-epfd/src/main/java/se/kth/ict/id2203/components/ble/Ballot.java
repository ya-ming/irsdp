package se.kth.ict.id2203.components.ble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class Ballot implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Ballot.class);

    private static final long serialVersionUID = -1565611742901119333L;

    private Integer n;
    private Integer pid;

    public Ballot(Integer n, Integer pid) {
        this.n = n;
        this.pid = pid;
    }

    public Integer getN() {
        return n;
    }

    public Integer getPid() {
        return pid;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public boolean isLessThan(Ballot ballot) {
        boolean ret = false;

        if (this.n < ballot.n) {
            ret = true;
        }
//        else if (this.n.equals(ballot.n)) {
//            if (this.pid < ballot.pid) {
//                ret = true;
//            }
//        }

        return ret;
    }

    public boolean isLessThanOrEqualTo(Ballot ballot) {
        boolean ret = false;

        logger.debug("Ballot isLessThanOrEqualTo");
        if (this.n <= ballot.n) {
            logger.debug("Ballot isLessThanOrEqualTo " + this.n + " <= " + ballot.n);
            ret = true;
        }
//        else {
//            if (this.pid <= ballot.pid) {
//                ret = true;
//            }
//        }

        return ret;
    }

    public void increment() {
        n++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ballot ballot = (Ballot) o;

        if (n != null ? !n.equals(ballot.n) : ballot.n != null) return false;
        return pid != null ? pid.equals(ballot.pid) : ballot.pid == null;
    }

    @Override
    public int hashCode() {
        int result = n != null ? n.hashCode() : 0;
        result = 31 * result + (pid != null ? pid.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Ballot{" +
                "n=" + n +
                ", pid=" + pid +
                '}';
    }
}
