package se.kth.ict.id2203.ports.sp2p;

import se.sics.kompics.PortType;

public class StubbornPointToPointLink extends PortType{
    {
        indication(Sp2pDeliver.class);
        request(Sp2pSend.class);
    }
}
