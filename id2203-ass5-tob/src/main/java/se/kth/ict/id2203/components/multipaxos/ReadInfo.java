package se.kth.ict.id2203.components.multipaxos;

import java.util.ArrayList;

public class ReadInfo {
    private final Integer ts/*, nodeId*/;
    private final ArrayList<Object> vsuf;


    public ReadInfo(Integer ts, ArrayList<Object> vsuf/*, Integer nodeId*/) {
        this.ts = ts;
        this.vsuf = vsuf;
//        this.nodeId = nodeId;
    }

    public Integer getTs() {
        return ts;
    }


    public ArrayList<Object> getVsuf() {
        return vsuf;
    }

//    public Integer getNodeId() {
//        return nodeId;
//    }
}

