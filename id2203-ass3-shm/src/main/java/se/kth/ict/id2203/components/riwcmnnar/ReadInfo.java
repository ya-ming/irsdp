package se.kth.ict.id2203.components.riwcmnnar;

public class ReadInfo {
    private final Integer ts, wr, nodeId;
    Object v;


    public ReadInfo(Integer ts, Integer wr, Object v, Integer nodeId) {
        this.ts = ts;
        this.wr = wr;   // identity of the process that writes a value
        this.v = v;
        this.nodeId = nodeId;
    }

    public Integer getTs() {
        return ts;
    }

    public Integer getWr() {
        return wr;
    }

    public Object getV() {
        return v;
    }

    public Integer getNodeId() {
        return nodeId;
    }
}
