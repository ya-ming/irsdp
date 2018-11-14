package se.kth.ict.id2203.ports.lurb;

import se.sics.kompics.PortType;

public class MajorityAckLoggedUniformReliableBroadcast extends PortType {
    {
        indication(MajorityAckLURbDeliver.class);
        request(MajorityAckLURbBroadcast.class);
    }
}
