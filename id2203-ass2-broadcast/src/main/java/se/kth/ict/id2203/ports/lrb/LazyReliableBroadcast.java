package se.kth.ict.id2203.ports.lrb;

import se.sics.kompics.PortType;

public class LazyReliableBroadcast extends PortType {
    {
        indication(LRbDeliver.class);
        request(LRbBroadcast.class);
    }
}
