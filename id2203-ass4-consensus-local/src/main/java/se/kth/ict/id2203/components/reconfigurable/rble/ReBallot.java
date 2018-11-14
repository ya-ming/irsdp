package se.kth.ict.id2203.components.reconfigurable.rble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class ReBallot implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ReBallot.class);

    private static final long serialVersionUID = -1565611742901119333L;

    private Integer cfg;
    private Integer n;
    private Integer pid;

    public ReBallot(Integer cfg, Integer n, Integer pid) {
        this.cfg = cfg;
        this.n = n;
        this.pid = pid;
    }

    public void setCfg(Integer cfg) {
        this.cfg = cfg;
    }

    public Integer getCfg() {
        return cfg;
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

    public boolean isLessThan(ReBallot reBallot) {
        boolean ret = false;

        // cfg has higher priority than ballot number
        if (this.cfg.equals(reBallot.cfg)) {
            if (this.n < reBallot.n) {
                logger.debug("ReBallot isLessThan " +
                        "(" + this.cfg + ", " + this.n + ")" + " <= " +
                        "(" + reBallot.cfg + ", " + reBallot.n + ")");
                ret = true;
            }
        }
        return ret;
    }

    public boolean isLessThanOrEqualTo(ReBallot reBallot) {
        boolean ret = false;

        // cfg has higher priority than ballot number
        if (this.cfg.equals(reBallot.cfg)) {
            if (this.n <= reBallot.n) {
                logger.debug("ReBallot isLessThanOrEqualTo " +
                        "(" + this.cfg + ", " + this.n + ")" + " <= " +
                        "(" + reBallot.cfg + ", " + reBallot.n + ")");
                ret = true;
            }
        }

        return ret;
    }

//    public void increment() {
//        n++;
//    }

    @Override
    public String toString() {
        return "ReBallot{" +
                "cfg=" + cfg +
                ", n=" + n +
                ", pid=" + pid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReBallot that = (ReBallot) o;

        if (cfg != null ? !cfg.equals(that.cfg) : that.cfg != null) return false;
        if (n != null ? !n.equals(that.n) : that.n != null) return false;
        return pid != null ? pid.equals(that.pid) : that.pid == null;
    }

    @Override
    public int hashCode() {
        int result = cfg != null ? cfg.hashCode() : 0;
        result = 31 * result + (n != null ? n.hashCode() : 0);
        result = 31 * result + (pid != null ? pid.hashCode() : 0);
        return result;
    }
}
