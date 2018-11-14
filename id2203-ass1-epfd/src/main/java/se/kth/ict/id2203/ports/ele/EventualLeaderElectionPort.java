package se.kth.ict.id2203.ports.ele;

import se.sics.kompics.PortType;

public class EventualLeaderElectionPort extends PortType {
    {
        indication(Trust.class);
    }
}
