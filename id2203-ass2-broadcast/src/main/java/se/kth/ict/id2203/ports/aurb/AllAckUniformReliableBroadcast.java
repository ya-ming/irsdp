package se.kth.ict.id2203.ports.aurb;

import se.sics.kompics.PortType;

public class AllAckUniformReliableBroadcast extends PortType {
    {
        indication(AllAckURbDeliver.class);
        request(AllAckURbBroadcast.class);
    }
}
