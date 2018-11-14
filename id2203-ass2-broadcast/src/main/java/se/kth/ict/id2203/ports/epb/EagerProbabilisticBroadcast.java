package se.kth.ict.id2203.ports.epb;

import se.sics.kompics.PortType;

public class EagerProbabilisticBroadcast extends PortType {
    {
        indication(EPbDeliver.class);
        request(EPbBroadcast.class);
    }
}
