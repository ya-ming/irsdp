package se.kth.ict.id2203.components.ac;

public class ReadInfo {
    private final Integer ts;
    private final Object v;
    private final Integer nodeId;


    public ReadInfo(Integer ts, Object v, Integer nodeId) {
        this.ts = ts;
        this.v = v;
        this.nodeId = nodeId;
    }

    public Integer getTs() {
        return ts;
    }


    public Object getV() {
        return v;
    }

    public Integer getNodeId() {
        return nodeId;
    }
}

